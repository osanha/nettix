package io.nettix.channel.handler;

import static org.jboss.netty.channel.Channels.fireExceptionCaught;

import org.jboss.netty.channel.ChannelHandler.Sharable;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.handler.timeout.ReadTimeoutHandler;
import org.jboss.netty.util.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handler that closes the channel if no packets are received within a specified timeout period.
 *
 * <p>This handler can optionally treat a timeout as an exception or simply close the channel.</p>
 *
 * @author sanha
 */
@Sharable
public class ChannelReadTimeoutHandler
        extends ReadTimeoutHandler
{
    /**
     * Logger instance for logging timeout events
     */
    private static final Logger _logger = LoggerFactory.getLogger(ChannelReadTimeoutHandler.class);

    /**
     * Flag indicating whether a timeout should be handled as an exception
     */
    private final boolean _useException;

    // The default Netty ReadTimeoutException may need to be handled by other handlers.
    // This internal exception class is used to distinguish exceptions thrown by this handler.
    @SuppressWarnings("serial")
    private class TimeoutException
            extends Throwable
    {
    }

    /**
     * Constructor with default exception handling enabled.
     *
     * @param timer
     *          The timer instance
     * @param timeoutSeconds
     *          Timeout duration in seconds
     */
    public ChannelReadTimeoutHandler(Timer timer, int timeoutSeconds)
    {
        this(timer, timeoutSeconds, true);
    }

    /**
     * Constructor.
     *
     * @param timer
     *          The timer instance
     * @param timeoutSeconds
     *          Timeout duration in seconds
     * @param useException
     *          Whether to handle timeout as an exception. If true, a TimeoutException is fired.
     */
    public ChannelReadTimeoutHandler(Timer timer, int timeoutSeconds,
                                     boolean useException)
    {
        super(timer, timeoutSeconds);
        _useException = useException;
    }

    /**
     * Called when a read timeout occurs.
     *
     * <p>If {@code _useException} is true, a {@link TimeoutException} is fired.
     * Otherwise, the channel is closed and a log entry is generated.</p>
     *
     * @param ctx
     *          The channel handler context
     * @throws Exception
     *          If an error occurs while handling the timeout
     */
    @Override
    protected void readTimedOut(ChannelHandlerContext ctx) throws Exception
    {
        if (_useException)
        {
            fireExceptionCaught(ctx.getChannel(), new TimeoutException());
        }
        else
        {
            _logger.info("Channel {} has timed out. Closing the channel.", ctx.getChannel());
            ctx.getChannel().close();
        }
    }

    /**
     * Handles exceptions caught in the channel pipeline.
     *
     * <p>If the exception is a {@link TimeoutException}, logs an error and closes the channel.
     * Otherwise, the exception is propagated upstream.</p>
     *
     * @param ctx
     *          The channel handler context
     * @param e
     *          The exception event
     * @throws Exception
     *          If an error occurs while handling the exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
            throws Exception
    {
        if (e.getCause() instanceof TimeoutException)
        {
            _logger.error("Channel {} timed out. Closing the channel.", ctx.getChannel());
            ctx.getChannel().close();
        }
        else
        {
            ctx.sendUpstream(e);
        }
    }

    /**
     * Restarts the read timeout for the channel.
     *
     * @param cp
     *          The channel pipeline
     * @throws Exception
     *          If an error occurs while starting the timeout
     */
    public void startTimeout(ChannelPipeline cp) throws Exception
    {
        super.beforeAdd(cp.getContext(this));
    }

    /**
     * Stops the read timeout for the channel.
     *
     * @param cp
     *          The channel pipeline
     * @throws Exception
     *          If an error occurs while stopping the timeout
     */
    public void stopTimeout(ChannelPipeline cp) throws Exception
    {
        ChannelHandlerContext ctx = cp.getContext(this);
        super.beforeRemove(ctx);
        ctx.setAttachment(null);
    }

}