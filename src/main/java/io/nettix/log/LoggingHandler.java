package io.nettix.log;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Set;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelDownstreamHandler;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandler.Sharable;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelState;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ChannelUpstreamHandler;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.nettix.util.StringUtil;

/**
 * A handler that logs I/O and exception events.
 *
 * <p>By default, only key events such as connection establishment, disconnection,
 * and active close are logged at the INFO level. When the logger level is set to DEBUG,
 * all I/O events can be logged if enabled, including message contents in hexadecimal form.</p>
 *
 * @author sanha
 */
@Sharable
public class LoggingHandler
        implements ChannelUpstreamHandler, ChannelDownstreamHandler
{
    /**
     * Logger instance.
     */
    private final Logger _logger;

    /**
     * Set of IP addresses to be excluded from logging.
     */
    private Set<String> _excludes;

    /**
     * Whether to log all I/O events at DEBUG level.
     * <p>By default, this is false to avoid unnecessary overhead under heavy load.</p>
     */
    private boolean _allEventsLogging;

    /**
     * Whether to log objects attached to the channel.
     */
    private boolean _attachmentLogging;

    /**
     * Default constructor.
     */
    public LoggingHandler()
    {
        _logger = LoggerFactory.getLogger(LoggingHandler.class);
    }

    /**
     * Constructs a new handler with a logger name suffix.
     *
     * @param suffix the suffix to append to the logger name
     */
    public LoggingHandler(String suffix)
    {
        _logger = LoggerFactory.getLogger(LoggingHandler.class.getName() + '.' + suffix);
    }

    /**
     * Enables or disables logging of all I/O events at DEBUG level.
     *
     * <p>Note: Enabling this may cause performance degradation under heavy traffic.</p>
     *
     * @param enabled true to log all I/O events
     */
    public void setAllEventsLogging(boolean enabled)
    {
        _allEventsLogging = enabled;
    }

    /**
     * Enables or disables logging of objects attached to the channel.
     *
     * @param enabled true to log the attached object
     */
    public void setAttachmentLogging(boolean enabled)
    {
        _attachmentLogging = enabled;
    }

    /**
     * Sets the set of IP addresses to exclude from logging.
     *
     * @param excludes the set of IP addresses
     */
    public void setLogExcludes(Set<String> excludes)
    {
        _excludes = excludes;
    }

    @Override
    public void handleUpstream(ChannelHandlerContext ctx, ChannelEvent e)
            throws Exception
    {
        log(ctx, e, false);
        ctx.sendUpstream(e);
    }

    @Override
    public void handleDownstream(ChannelHandlerContext ctx, ChannelEvent e)
            throws Exception
    {
        log(ctx, e, true);
        ctx.sendDownstream(e);
    }

    /**
     * Checks whether the remote address of the connected channel
     * is excluded from logging. The result is cached for performance.
     *
     * @param ctx the channel handler context
     * @param addr the remote address
     * @return true if the address is excluded from logging
     */
    private boolean isExcluded(ChannelHandlerContext ctx, SocketAddress addr)
    {
        if (_excludes != null)
        {
            Boolean isExcluded = (Boolean) ctx.getAttachment();

            if (isExcluded != null)
                return isExcluded;

            if ((addr != null) && (addr instanceof InetSocketAddress))
            {
                isExcluded = _excludes.contains(((InetSocketAddress) addr).getAddress().getHostAddress());
                // Cache the result in the channel context for reuse.
                ctx.setAttachment(isExcluded);
                return isExcluded;
            }
        }

        return false;
    }

    /**
     * Creates a log message builder.
     *
     * @param ch the channel
     * @return the log message builder
     */
    private StringBuilder createLogBuilder(Channel ch)
    {
        StringBuilder buf = new StringBuilder();

        if (_attachmentLogging)
        {
            Object attach = ch.getAttachment();

            if (attach != null)
                buf.append(attach.toString()).append(' ');
        }

        return buf;
    }

    /**
     * Performs logging for the given channel event.
     *
     * <p>By default, only channel connect/disconnect and close events are logged at INFO level.
     * When in DEBUG mode, ChannelBuffer message events are also logged in hexadecimal format.
     * If {@code allEventsLogging} is enabled, all I/O events will be logged at DEBUG level.</p>
     *
     * @param ctx the channel handler context
     * @param e the channel event
     * @param isDown true if the event is outgoing
     */
    public void log(ChannelHandlerContext ctx, ChannelEvent e, boolean isDown)
    {
        if (!_logger.isInfoEnabled()
                || isExcluded(ctx, e.getChannel().getRemoteAddress()))
            return;

        String log;
        Channel ch = e.getChannel();

        // Since many events are not logged, create the log builder only when needed.
        if (e instanceof ChannelStateEvent)
        {
            ChannelState state = ((ChannelStateEvent) e).getState();

            if ((state == ChannelState.CONNECTED)
                    || (isDown && (state == ChannelState.OPEN)))
            {
                log = createLogBuilder(ch).append(e.toString()).toString();
                _logger.info(log);
            }
            else if (_allEventsLogging && _logger.isDebugEnabled())
            {
                log = createLogBuilder(ch).append(e.toString()).toString();
                _logger.debug(log);
            }
        }
        else if (e instanceof MessageEvent)
        {
            if (_logger.isDebugEnabled())
            {
                // The IoLogging handler is positioned first, so the message is always a ChannelBuffer.
                ChannelBuffer buf = (ChannelBuffer) ((MessageEvent) e).getMessage();
                String hex = StringUtil.toHexDump(buf);
                log = createLogBuilder(ch).append(e.toString()).append(hex).toString();
                _logger.debug(log);
            }
        }
        else if (e instanceof ExceptionEvent)
        {
            log = createLogBuilder(ch).append(ch.toString()).append(" EXCEPTION").toString();
            _logger.error(log, ((ExceptionEvent) e).getCause());
        }
    }
}