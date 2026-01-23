package io.nettix.http;

import static org.jboss.netty.channel.Channels.succeededFuture;
import static org.jboss.netty.channel.Channels.write;
import static org.jboss.netty.handler.codec.http.HttpHeaders.is100ContinueExpected;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.handler.codec.frame.TooLongFrameException;
import org.jboss.netty.handler.codec.http.HttpChunk;
import org.jboss.netty.handler.codec.http.HttpChunkAggregator;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.util.CharsetUtil;

/**
 * Handles HTTP content by checking maximum length, aggregating HTTP chunk messages,
 * and processing 'Expect: 100-continue' headers.
 *
 * @author sanha
 */
public class HttpContentHandler
        extends HttpChunkAggregator
{
    /**
     * Maximum allowed content length.
     */
    private final int _maxContentLength;

    /**
     * Whether to aggregate HTTP chunk messages.
     */
    private final boolean _isChunkAggregate;

    /**
     * Total content length received.
     */
    private int _totalContentLength;

    /**
     * Whether a TooLongFrameException has already been thrown.
     */
    private boolean _tooLongFrameFound;

    /**
     * Response message for 'Expect: 100-continue' requests.
     */
    private static final ChannelBuffer CONTINUE = ChannelBuffers.copiedBuffer("HTTP/1.1 100 Continue\r\n\r\n",
            CharsetUtil.US_ASCII);

    /**
     * Constructor.
     *
     * @param maxContentLength
     *          Maximum allowed content length.
     * @param isChunkAggregate
     *          Whether to aggregate chunk messages.
     */
    public HttpContentHandler(int maxContentLength, boolean isChunkAggregate)
    {
        super(maxContentLength);
        _isChunkAggregate = isChunkAggregate;
        _maxContentLength = maxContentLength;
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
            throws Exception
    {
        Object o = e.getMessage();

        if (o instanceof HttpMessage)
        {
            _totalContentLength = 0;
            _tooLongFrameFound = false;
            HttpMessage msg = (HttpMessage) o;

            // Regardless of _isChunkAggregate, handle 'Expect: 100-continue' to receive content.
            if (is100ContinueExpected(msg))
            {
                // Prevent duplicate handling in HttpChunkAggregator and remove the processed 'Expect' header.
                msg.headers().remove(HttpHeaders.Names.EXPECT);
                write(ctx, succeededFuture(ctx.getChannel()), CONTINUE.duplicate());
            }

            if (msg.isChunked())
            {
                if (_isChunkAggregate)
                    super.messageReceived(ctx, e);
                else
                    ctx.sendUpstream(e);
            }
            else
            {
                checkContentLength(ctx, e, msg.getContent().readableBytes());
            }
        }
        else if (o instanceof HttpChunk)
        {
            if (_isChunkAggregate)
                super.messageReceived(ctx, e);
            else
                checkContentLength(ctx, e,
                        ((HttpChunk) o).getContent().readableBytes());
        }
        else
        {
            ctx.sendUpstream(e);
        }
    }

    /**
     * Checks the content length.
     *
     * @param ctx
     *          Channel handler context.
     * @param e
     *          Message event.
     * @param length
     *          Length of the received message.
     * @throws TooLongFrameException
     *           If the total content length exceeds the configured maximum.
     */
    private void checkContentLength(ChannelHandlerContext ctx, MessageEvent e,
                                    int length) throws TooLongFrameException
    {
        // Even if Transfer-Encoding is not chunked, content exceeding the configured length
        // is delivered in chunks by HttpMessageDecoder. If the channel is closed due to
        // a TooLongFrameException, HttpMessageDecoder may continue sending the remaining chunks,
        // which should be ignored.
        if (!_tooLongFrameFound)
        {
            _totalContentLength += length;

            if (_totalContentLength > _maxContentLength)
            {
                _tooLongFrameFound = true;
                throw new TooLongFrameException("HTTP content length exceeded "
                        + _maxContentLength + " bytes.");
            }

            ctx.sendUpstream(e);
        }
    }

}