package io.nettix.http.server;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.handler.codec.compression.CompressionException;
import org.jboss.netty.handler.codec.frame.TooLongFrameException;
import org.jboss.netty.handler.codec.http.HttpChunk;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;

import io.nettix.channel.handler.ChannelReadTimeoutHandler;
import io.nettix.http.HttpException;
import io.nettix.http.HttpHeadersExt;
import io.nettix.http.HttpRequest;
import io.nettix.http.HttpResponse;
import io.nettix.http.HttpState;
import io.nettix.http.HttpUtil;

/**
 * Handles incoming and outgoing HTTP messages for the server.
 *
 * @author sanha
 */
public class HttpServerHandler extends SimpleChannelHandler
{
    /**
     * User-defined HTTP request handler.
     */
    private final HttpRequestHandler _handler;

    /**
     * Whether to keep the connection alive.
     */
    private final boolean _isKeepAlive;

    /**
     * Channel handler for detecting HTTP request receive timeout.
     */
    private final ChannelReadTimeoutHandler _timeoutHandler;

    /**
     * Indicates whether the connection should be forcibly closed.
     */
    private boolean _isConnectionClose;

    /**
     * Current HTTP state.
     */
    private HttpState _state;

    /**
     * Constructor.
     *
     * @param handler         HTTP request handler
     * @param timeoutHandler  Timeout handler for incoming requests
     * @param isKeepAlive     Whether to keep the connection alive
     */
    public HttpServerHandler(HttpRequestHandler handler,
                             ChannelReadTimeoutHandler timeoutHandler,
                             boolean isKeepAlive)
    {
        _handler = handler;
        _timeoutHandler = timeoutHandler;
        _isKeepAlive = isKeepAlive;
    }

    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e)
            throws Exception
    {
        _state = HttpState.CONNECTED;

        if (_handler != null)
            _handler.connected(ctx.getChannel());

        ctx.sendUpstream(e);
    }

    @Override
    public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e)
            throws Exception
    {
        if (_handler != null)
            _handler.disconnected(ctx.getChannel());

        ctx.sendUpstream(e);
    }

    /**
     * Handles completion of HTTP request reception.
     *
     * @param ctx the channel handler context
     * @throws Exception if an error occurs while handling the request
     */
    private void requestReceived(ChannelHandlerContext ctx) throws Exception
    {
        _state = HttpState.REQUESTED;
        _timeoutHandler.stopTimeout(ctx.getPipeline());
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
            throws Exception
    {
        Object o = e.getMessage();

        if (o instanceof HttpRequest)
        {
            if ((_state != HttpState.CONNECTED) && (_state != HttpState.RESPONSED))
                throw new IllegalStateException(_state.name());

            HttpRequest req = (HttpRequest) o;
            _isConnectionClose = HttpHeadersExt.isConnectionClose(req);

            if (!req.isChunked())
                requestReceived(ctx);

            if (_handler != null)
                _handler.requestReceived(ctx.getChannel(), e.getRemoteAddress(), req);
        }
        else if (o instanceof HttpChunk)
        {
            HttpChunk chunk = (HttpChunk) o;

            if (chunk.isLast())
                requestReceived(ctx);

            if (_handler != null)
                _handler.chunkReceived(ctx.getChannel(), e.getRemoteAddress(),
                        (HttpChunk) o);
        }

        ctx.sendUpstream(e);
    }

    /**
     * Performs post-processing after the HTTP response is sent.
     *
     * @param ctx        the channel handler context
     * @param e          the message event
     * @param keepAlive  whether to keep the connection alive
     */
    private void responseRequested(final ChannelHandlerContext ctx,
                                   MessageEvent e, final boolean keepAlive)
    {
        _state = HttpState.RESPONSED;
        e.getFuture().addListener(new ChannelFutureListener()
        {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception
            {
                Channel ch = future.getChannel();

                if (keepAlive && future.isSuccess())
                    _timeoutHandler.startTimeout(ctx.getPipeline());
                else if (ch.isConnected())
                    ch.close();
            }
        });
    }

    @Override
    public void writeRequested(final ChannelHandlerContext ctx, MessageEvent e)
            throws Exception
    {
        Object o = e.getMessage();
        boolean isKeepAlive = !_isConnectionClose && _isKeepAlive;

        if (o instanceof HttpResponse)
        {
            HttpResponse res = (HttpResponse) o;

            if (isKeepAlive)
                HttpHeadersExt.removeConnectionClose(res);
            else
                HttpHeadersExt.setConnectionClose(res);

            if (!res.isChunked())
            {
                // Always set the Content-Length, even if it is zero.
                HttpHeadersExt.setContentLength(res,
                        res.getContent().readableBytes());
                responseRequested(ctx, e, isKeepAlive);
            }
        }
        else if (o instanceof HttpChunk)
        {
            if (((HttpChunk) o).isLast())
                responseRequested(ctx, e, isKeepAlive);
        }

        ctx.sendDownstream(e);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
    {
        Channel ch = e.getChannel();

        // When SSL handshake fails, the state is null.
        if ((_state == null) || (_state == HttpState.EXCEPTION_HANDLED)
                || (_state == HttpState.RESPONSED))
        {
            if (ch.isConnected())
                ch.close();

            return;
        }

        _state = HttpState.EXCEPTION_HANDLED;
        HttpResponseStatus status = null;
        Throwable t = e.getCause();

        if (t instanceof HttpException)
        {
            status = ((HttpException) t).status();
        }
        else
        {
            if (_state == HttpState.REQUESTED)
                status = HttpResponseStatus.INTERNAL_SERVER_ERROR;
                // Thrown by HttpChunkAggregator when content exceeds the maximum size.
            else if (t instanceof TooLongFrameException)
                status = HttpResponseStatus.REQUEST_ENTITY_TOO_LARGE;
                // Thrown by HttpContentDecompressor when decompression format is invalid.
            else if (t instanceof CompressionException)
                status = HttpResponseStatus.NOT_ACCEPTABLE;
            else
                status = HttpResponseStatus.BAD_REQUEST;

            _isConnectionClose = true;
        }

        String msg = (t instanceof HttpException) ? t.getMessage() : t.toString();
        HttpUtil.writeResponse(ch, status, msg);
    }
}