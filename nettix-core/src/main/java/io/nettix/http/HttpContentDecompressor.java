package io.nettix.http;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.handler.codec.compression.CompressionException;

/**
 * A channel handler that decompresses compressed HttpMessages.
 *
 * @author sanha
 */
public class HttpContentDecompressor
        extends org.jboss.netty.handler.codec.http.HttpContentDecompressor
{
    /**
     * The encoding to check for compression.
     */
    private final HttpContentEncoding _encoding;

    /**
     * Default constructor.
     */
    public HttpContentDecompressor()
    {
        this(null);
    }

    /**
     * Constructor.
     *
     * @param encoding
     *          The encoding to check for compression.
     */
    public HttpContentDecompressor(HttpContentEncoding encoding)
    {
        _encoding = encoding;
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
            throws Exception
    {
        if ((_encoding != null) && (e.getMessage() instanceof HttpMessage))
        {
            HttpMessage msg = (HttpMessage) e.getMessage();

            if (msg.isChunked() || msg.getContent().readable())
            {
                String encoding = HttpHeadersExt.getContentEncoding(msg);

                if (!_encoding.name().equalsIgnoreCase(encoding))
                    throw new CompressionException("Unexpected Content-Encoding: "
                            + encoding);
            }
        }

        super.messageReceived(ctx, e);
    }

}