package io.nettix.channel.handler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandler.Sharable;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.timeout.ReadTimeoutHandler;
import org.jboss.netty.util.Timeout;
import org.jboss.netty.util.TimerTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.nettix.channel.HeartbeatFactory;
import io.nettix.util.Singleton;

/**
 * A channel handler for monitoring and maintaining channel connectivity through heartbeat messages.
 * Sends periodic heartbeat messages and closes the channel if a read timeout occurs.
 *
 * @param <T> the type of heartbeat message
 *
 * @author sanha
 */
@Sharable
public class HeartbeatHandler<T> extends ReadTimeoutHandler {

    /** Logger instance */
    private static final Logger _logger = LoggerFactory.getLogger(HeartbeatHandler.class);

    /**
     * Map to store scheduled timeout tasks for each channel.
     * ChannelHandlerContext cannot store attachments because ReadTimeoutHandler is already attached.
     */
    private static final Map<Channel, Timeout> _taskMap = new ConcurrentHashMap<Channel, Timeout>();

    /** Task interface for recurring heartbeat messages */
    private static interface RecursiveTimerTask extends TimerTask, ChannelFutureListener {
    }

    /** Interval in seconds for sending heartbeat messages */
    private final int _time;

    /** Name used for logging on timeout */
    private final String _name;

    /** Factory to create heartbeat messages */
    private final HeartbeatFactory<T> _factory;

    /**
     * Constructs a HeartbeatHandler.
     *
     * @param name    name used for logging on timeout
     * @param time    interval for sending heartbeat messages in seconds
     * @param timeout read timeout in seconds
     * @param factory factory for creating heartbeat messages
     */
    public HeartbeatHandler(String name, int time, int timeout, HeartbeatFactory<T> factory) {
        super(Singleton.Timer, timeout);
        _name = name;
        _time = time;
        _factory = factory;
    }

    @Override
    protected void readTimedOut(ChannelHandlerContext ctx) throws Exception {
        Channel ch = ctx.getChannel();
        _logger.error("{} {} channel read timed out.", ch, _name);
        ch.close();
    }

    @Override
    public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        start(ctx);
        super.channelOpen(ctx, e);
    }

    @Override
    public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        stop(ctx);
        super.channelClosed(ctx, e);
    }

    @Override
    public void beforeAdd(ChannelHandlerContext ctx) throws Exception {
        if (ctx.getPipeline().isAttached()) {
            start(ctx);
        }
        super.beforeAdd(ctx);
    }

    @Override
    public void beforeRemove(ChannelHandlerContext ctx) throws Exception {
        stop(ctx);
        super.beforeRemove(ctx);
    }

    /**
     * Starts sending heartbeat messages at fixed intervals to maintain the channel connection.
     *
     * @param ctx the channel handler context
     */
    private void start(final ChannelHandlerContext ctx) {
        final Channel ch = ctx.getChannel();

        Timeout task = Singleton.Timer.newTimeout(new RecursiveTimerTask() {
            @Override
            public void run(Timeout timeout) throws Exception {
                if (!timeout.isCancelled() && ch.isConnected()) {
                    ChannelFuture future = Channels.future(ch);
                    Channels.write(ctx, future, _factory.createHeartbeat());
                    future.addListener(this);
                }
            }

            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    Timeout nextTask = Singleton.Timer.newTimeout(this, _time, TimeUnit.SECONDS);
                    _taskMap.put(ctx.getChannel(), nextTask);
                }
            }
        }, _time, TimeUnit.SECONDS);

        _taskMap.put(ctx.getChannel(), task);
    }

    /**
     * Stops sending heartbeat messages for the specified channel.
     *
     * @param ctx the channel handler context
     */
    private void stop(ChannelHandlerContext ctx) {
        Timeout task = _taskMap.get(ctx.getChannel());
        if (task != null) {
            task.cancel();
        }
    }
}