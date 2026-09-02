package io.nettix.http.client;

import java.nio.channels.ClosedChannelException;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.handler.codec.http.HttpChunk;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.timeout.ReadTimeoutException;
import org.jboss.netty.util.Timeout;
import org.jboss.netty.util.TimerTask;

import io.nettix.channel.CallableChannelFuture;
import io.nettix.http.HttpHeadersExt;
import io.nettix.http.HttpResponse;
import io.nettix.http.HttpState;
import io.nettix.util.Singleton;

/**
 * HTTP client channel handler.
 * When the response message does not contain a Content-Length header,
 * decoding is completed only after the channel is closed.
 * Therefore, the {@code channelDisconnected} event may be received
 * even after the {@code messageReceived} event has already been triggered.
 *
 * @author sanha
 */
public class HttpClientHandler extends SimpleChannelHandler
{
    /**
     * Whether to keep the connection alive.
     */
    private final boolean _isKeepAlive;

    /**
     * Connection pool queue.
     */
    private final Queue<Channel> _connPool;

    /**
     * HTTP response handler.
     */
    private HttpResponseHandler _handler;

    /**
     * Response timeout task.
     */
    private volatile Timeout _timeoutTask;

    /**
     * HTTP request to be sent.
     */
    private HttpRequest _request;

    /**
     * Response timeout (in seconds).
     */
    private int _resTimeout;

    /**
     * Final processing result.
     */
    private CallableChannelFuture<HttpResponse> _finalFuture;

    /**
     * Whether to forcefully close the connection.
     */
    private boolean _isConnectionClose;

    /**
     * Current connection state.
     */
    private HttpState _state = HttpState.CONNECTED;

    /**
     * Constructor.
     *
     * @param connPool    the connection pool
     * @param isKeepAlive whether to keep the connection alive
     */
    public HttpClientHandler(Queue<Channel> connPool, boolean isKeepAlive)
    {
        _connPool = connPool;
        _isKeepAlive = isKeepAlive;
    }

    /**
     * Executes an HTTP request/response exchange.
     *
     * @param ch          the channel
     * @param request     the HTTP request to send
     * @param handler     the HTTP response handler
     * @param resTimeout  the response timeout (in seconds)
     * @param finalFuture the final processing result
     */
    void execute(final Channel ch, HttpRequest request,
                 HttpResponseHandler handler, int resTimeout,
                 CallableChannelFuture<HttpResponse> finalFuture)
    {
        if ((_state != HttpState.CONNECTED) && (_state != HttpState.RESPONSED))
            throw new IllegalStateException(_state.name());

        _request = request;
        _handler = handler;
        _resTimeout = resTimeout;
        _finalFuture = finalFuture;

        ch.write(_request).addListener(new ChannelFutureListener()
        {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception
            {
                if (future.isSuccess())
                {
                    // If the full request (including content) has been successfully sent, start waiting for a response.
                    if (!_request.isChunked())
                        beginResponseTimeout(ch);

                    if (_handler != null)
                        _handler.requestComplete(ch);
                }
            }
        });
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
            throws Exception
    {
        Object o = e.getMessage();
        Channel ch = ctx.getChannel();

        if (o instanceof HttpResponse)
        {
            HttpResponse res = (HttpResponse) o;
            _isConnectionClose = HttpHeadersExt.isConnectionClose(res);

            if (!res.isChunked())
                done(ch);

            _finalFuture.setSuccess(res);

            if (_handler != null)
                _handler.responseReceived(ch, res);

        }
        else if (o instanceof HttpChunk)
        {
            HttpChunk chunk = (HttpChunk) o;

            if (chunk.isLast())
                done(ch);

            if (_handler != null)
                _handler.chunkReceived(ch, chunk);
        }

        ctx.sendUpstream(e);
    }

    @Override
    public void closeRequested(ChannelHandlerContext ctx, ChannelStateEvent e)
            throws Exception
    {
        // For chunked requests, the user handler may close the channel before the last chunk is received.
        _state = HttpState.RESPONSED;
        ctx.sendDownstream(e);
    }

    @Override
    public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e)
            throws Exception
    {
        // The connection may be closed by the server.
        if (_timeoutTask != null)
        {
            _timeoutTask.cancel();
            _timeoutTask = null;
        }

        if (_handler != null)
            _handler.disconnected(ctx.getChannel());

        // If the disconnection occurred while the request was being sent,
        // Netty will raise an exception. Use 'later' to fire it after this event completes.
        if (_state == HttpState.REQUESTED)
            Channels.fireExceptionCaughtLater(ctx.getChannel(),
                    new ClosedChannelException());

        ctx.sendUpstream(e);
    }

    /**
     * Finalizes the connection.
     *
     * @param ch the channel
     */
    private void done(Channel ch)
    {
        // When there is no Content-Length header, decoding completes only after the channel is closed.
        _state = HttpState.RESPONSED;

        if (_timeoutTask != null)
        {
            _timeoutTask.cancel();
            _timeoutTask = null;
        }

        if (ch.isConnected())
        {
            if (_isConnectionClose || !_isKeepAlive)
                ch.close();
            else
                _connPool.offer(ch);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
            throws Exception
    {
        Channel ch = e.getChannel();

        // Can occur under various conditions.
        _isConnectionClose = true;
        done(ch);

        if (_handler != null)
            _handler.exceptionCaught(ch, e.getCause());

        _finalFuture.setFailure(e.getCause());

        if (ctx != null)
            ctx.sendUpstream(e);
    }

    @Override
    public void writeRequested(ChannelHandlerContext ctx, MessageEvent e)
            throws Exception
    {
        if (_request.isChunked())
        {
            Object o = e.getMessage();

            if ((o instanceof HttpChunk) && ((HttpChunk) o).isLast())
            {
                e.getFuture().addListener(new ChannelFutureListener()
                {
                    @Override
                    public void operationComplete(ChannelFuture future)
                            throws Exception
                    {
                        if (future.isSuccess())
                            beginResponseTimeout(future.getChannel());
                    }
                });
            }
        }

        ctx.sendDownstream(e);
    }

    /**
     * Starts the response timeout timer.
     *
     * @param ch the channel
     */
    private void beginResponseTimeout(final Channel ch)
    {
        // When using a local channel, the response may already have been received.
        if (_state == HttpState.RESPONSED)
            return;

        _state = HttpState.REQUESTED;
        _timeoutTask = Singleton.Timer.newTimeout(new TimerTask()
        {
            @Override
            public void run(Timeout timeout) throws Exception
            {
                Throwable cause = new ReadTimeoutException("Response timed out");
                // Do not use 'later' here so that timeout is logged and handled immediately.
                Channels.fireExceptionCaught(ch, cause);
            }
        }, _resTimeout, TimeUnit.SECONDS);
    }

}