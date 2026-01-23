package io.nettix.ssl;

import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.handler.ssl.SslHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.nettix.channel.handler.ConnectStateEventHandler;

/**
 * Handles the SSL handshake process.
 *
 * This handler is separated from {@link SslHandler} to allow proper
 * logging of the "connected" event after the handshake is successfully completed.
 *
 * @author sanha
 */
public class SslHandshaker
        extends ConnectStateEventHandler
{
    /**
     * Logger instance.
     */
    private final Logger _logger;

    /**
     * SSL handler instance.
     */
    private final SslHandler _handler;

    /**
     * Indicates whether the SSL handshake has been completed.
     */
    private boolean _isHandshaked = false;

    /**
     * Creates a new instance with the specified SSL handler.
     *
     * @param handler
     *          the SSL handler
     */
    public SslHandshaker(SslHandler handler)
    {
        this(handler, null);
    }

    /**
     * Creates a new instance with the specified SSL handler and optional logger name suffix.
     *
     * @param handler
     *          the SSL handler
     * @param name
     *          an optional suffix for the logger name
     */
    public SslHandshaker(SslHandler handler, String name)
    {
        _handler = handler;

        if (name != null)
            _logger = LoggerFactory.getLogger(SslHandshaker.class.getName() + '.' + name);
        else
            _logger = LoggerFactory.getLogger(SslHandshaker.class);
    }

    @Override
    public void channelConnected(final ChannelHandlerContext ctx,
                                 final ChannelStateEvent e) throws Exception
    {
        _handler.handshake().addListener(new ChannelFutureListener()
        {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception
            {
                if (future.isSuccess())
                {
                    _logger.info("{} SSL session established using cipher suite: {}",
                            e.getChannel().toString(),
                            _handler.getEngine().getSession().getCipherSuite());

                    _isHandshaked = true;
                    ctx.sendUpstream(e);
                }
            }
        });
    }

    @Override
    public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e)
            throws Exception
    {
        if (_isHandshaked)
            ctx.sendUpstream(e);
    }

}