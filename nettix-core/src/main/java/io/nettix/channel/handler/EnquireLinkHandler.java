package io.nettix.channel.handler;

import java.util.concurrent.TimeUnit;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandler.Sharable;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.LifeCycleAwareChannelHandler;
import org.jboss.netty.handler.timeout.ReadTimeoutException;
import org.jboss.netty.util.Timeout;
import org.jboss.netty.util.TimerTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.nettix.channel.EnquireLinkFactory;
import io.nettix.util.Singleton;

/**
 * Channel handler that monitors the connection state by sending periodic
 * heartbeat (enquire link) messages.
 *
 * @param <T> the type of heartbeat message
 *
 * @author sanha
 */
@Sharable
public class EnquireLinkHandler<T>
        extends ConnectStateEventHandler
        implements LifeCycleAwareChannelHandler
{
    /** Logger */
    private static final Logger _logger = LoggerFactory.getLogger(EnquireLinkHandler.class);

    /** Interval to send enquire link messages in seconds */
    private final int _delay;

    /** Name for logging purposes on timeout */
    private final String _name;

    /** Factory to create heartbeat messages */
    private final EnquireLinkFactory<T> _factory;

    /**
     * Constructor.
     *
     * @param name
     *          Name used in logging on timeout
     * @param delay
     *          Interval to send heartbeat messages (in seconds)
     * @param factory
     *          Factory to create heartbeat messages
     */
    public EnquireLinkHandler(String name, int delay,
                              EnquireLinkFactory<T> factory)
    {
        _name = name;
        _delay = delay;
        _factory = factory;
    }

    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e)
            throws Exception
    {
        start(ctx);
        ctx.sendUpstream(e);
    }

    @Override
    public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e)
            throws Exception
    {
        stop(ctx);
        ctx.sendUpstream(e);
    }

    /**
     * Starts sending periodic heartbeat messages to keep the connection alive.
     *
     * @param ctx
     *          The channel handler context
     */
    private void start(final ChannelHandlerContext ctx)
    {
        Timeout task = Singleton.Timer.newTimeout(new TimerTask()
        {
            @Override
            public void run(Timeout timeout) throws Exception
            {
                if (timeout.isCancelled())
                    return;

                ChannelFuture writeFuture = Channels.future(ctx.getChannel());
                final T heartbeat = _factory.createHeartbeat();
                Channels.write(ctx, writeFuture, heartbeat);

                writeFuture.addListener(new ChannelFutureListener()
                {
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception
                    {
                        if (future.isSuccess())
                        {
                            ChannelFuture linkFuture = Channels.future(ctx.getChannel());
                            _factory.putChannelFuture(heartbeat, linkFuture);
                            linkFuture.addListener(new ChannelFutureListener()
                            {
                                @Override
                                public void operationComplete(ChannelFuture future)
                                        throws Exception
                                {
                                    if (!future.isCancelled() && !future.isSuccess())
                                        handleFailure(future);
                                }
                            });
                        }
                        else
                        {
                            handleFailure(future);
                        }
                    }
                });

                Timeout nextTask = Singleton.Timer.newTimeout(this, _delay,
                        TimeUnit.SECONDS);
                ctx.setAttachment(nextTask);
            }
        }, _delay, TimeUnit.SECONDS);

        ctx.setAttachment(task);
    }

    /**
     * Handles failures in sending the heartbeat message.
     *
     * @param future
     *          The channel future representing the result of the write operation
     */
    private void handleFailure(ChannelFuture future)
    {
        Channel ch = future.getChannel();
        Throwable e = future.getCause();

        if (e instanceof ReadTimeoutException)
            _logger.error("{} {} heartbeat timed out. Closing connection.", ch, _name);
        else
            _logger.error("{} {} heartbeat failed - {}. Closing connection.", ch, _name, e);

        ch.close();
    }

    /**
     * Stops sending periodic heartbeat messages.
     *
     * @param ctx
     *          The channel handler context
     */
    private void stop(ChannelHandlerContext ctx)
    {
        Timeout task = (Timeout) ctx.getAttachment();

        if (task != null)
            task.cancel();
    }

    @Override
    public void beforeAdd(ChannelHandlerContext ctx) throws Exception
    {
    }

    @Override
    public void afterAdd(ChannelHandlerContext ctx) throws Exception
    {
        if (ctx.getChannel().isConnected())
            start(ctx);
    }

    @Override
    public void beforeRemove(ChannelHandlerContext ctx) throws Exception
    {
        stop(ctx);
    }

    @Override
    public void afterRemove(ChannelHandlerContext ctx) throws Exception
    {
    }

}