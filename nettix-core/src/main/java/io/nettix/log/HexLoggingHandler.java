package io.nettix.log;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelHandler.Sharable;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.nettix.channel.handler.MessageEventHandler;
import io.nettix.util.Character;
import io.nettix.util.StringUtil;

/**
 * Hex logger that logs messages only when the log level is set to DEBUG.
 * <p>
 * Typically, this handler is not used directly because {@code IoLoggingHandler}
 * already provides hexadecimal logging.
 * </p>
 *
 * @author sanha
 */
@Sharable
public class HexLoggingHandler extends MessageEventHandler {

    /**
     * Logger instance.
     */
    private final Logger _logger;

    /**
     * Default constructor.
     */
    public HexLoggingHandler() {
        _logger = LoggerFactory.getLogger(HexLoggingHandler.class);
    }

    /**
     * Constructor with a logger name suffix.
     *
     * @param suffix
     *        the suffix to append to the logger name
     */
    public HexLoggingHandler(String suffix) {
        _logger = LoggerFactory.getLogger(HexLoggingHandler.class.getName() + '.' + suffix);
    }

    /**
     * Logs the given message event in hexadecimal format when DEBUG level is enabled.
     *
     * @param e
     *        the message event to log
     */
    private void log(MessageEvent e) {
        Object msg = e.getMessage();

        if (_logger.isDebugEnabled() && (msg instanceof ChannelBuffer)) {
            StringBuilder buf = new StringBuilder();
            Object attach = e.getChannel().getAttachment();

            if (attach != null) {
                buf.append(attach.toString());
                buf.append(Character.SPACE);
            }

            buf.append(e.toString());
            buf.append(StringUtil.toHexDump((ChannelBuffer) msg));

            _logger.debug(buf.toString());
        }
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        log(e);
        ctx.sendUpstream(e);
    }

    @Override
    public void writeRequested(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        log(e);
        ctx.sendDownstream(e);
    }

}