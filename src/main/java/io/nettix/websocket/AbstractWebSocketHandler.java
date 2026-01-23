package io.nettix.websocket;

import java.net.URI;
import java.net.URISyntaxException;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import org.jboss.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import org.jboss.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import org.jboss.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import org.jboss.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.jboss.netty.handler.codec.http.websocketx.WebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.nettix.channel.HeartbeatFactory;
import io.nettix.channel.handler.HeartbeatHandler;

/**
 * Abstract WebSocket handler used by both server and client.
 * Handles common WebSocket operations such as handshake and frame processing.
 *
 * @author sanha
 */
public abstract class AbstractWebSocketHandler
        extends SimpleChannelUpstreamHandler
{
    /**
     * Ping frame constant.
     */
    public static final PingWebSocketFrame PING = new PingWebSocketFrame();

    /**
     * Pong frame constant.
     */
    public static final PongWebSocketFrame PONG = new PongWebSocketFrame();

    /**
     * Logger instance.
     */
    private static final Logger _logger = LoggerFactory.getLogger(AbstractWebSocketHandler.class);

    /**
     * Heartbeat handler for connection monitoring.
     */
    private HeartbeatHandler<PingWebSocketFrame> _enquireLinker;

    /**
     * Connection URI.
     */
    private final URI _uri;

    /**
     * Additional handlers to add after handshake.
     */
    private final ChannelHandler[] _handlers;

    /**
     * Constructor.
     *
     * @param uri
     *          Connection URI as string
     * @param handlers
     *          Handlers to add after handshake
     * @throws URISyntaxException
     *           If the URI string is invalid
     */
    public AbstractWebSocketHandler(String uri, ChannelHandler[] handlers)
            throws URISyntaxException
    {
        this(new URI(uri), handlers);
    }

    /**
     * Constructor.
     *
     * @param uri
     *          Connection URI
     * @param handlers
     *          Handlers to add after handshake
     */
    public AbstractWebSocketHandler(URI uri, ChannelHandler[] handlers)
    {
        _uri = uri;
        _handlers = handlers;
    }

    /**
     * Returns the connection URI.
     *
     * @return URI
     */
    public URI uri()
    {
        return _uri;
    }

    /**
     * Sets the heartbeat handler for connection monitoring.
     *
     * @param name
     *          Name used in logging
     * @param delay
     *          Interval between heartbeat messages
     * @param timeout
     *          Response timeout in seconds
     */
    public void setEnquireLink(String name, int delay, int timeout)
    {
        HeartbeatFactory<PingWebSocketFrame> f = new HeartbeatFactory<PingWebSocketFrame>()
        {
            @Override
            public PingWebSocketFrame createHeartbeat()
            {
                return PING;
            }
        };

        _enquireLinker = new HeartbeatHandler<PingWebSocketFrame>(name, delay,
                timeout, f);
    }

    /**
     * Handles post-handshake processing.
     * Adds additional handlers and fires the connected event.
     *
     * @param ctx
     *          Channel handler context
     */
    protected void handshakeCompleted(ChannelHandlerContext ctx)
    {
        Channel ch = ctx.getChannel();
        ChannelPipeline pipeline = ch.getPipeline();

        if (_enquireLinker != null)
            pipeline.addBefore(ctx.getName(), "ENQUIRE_LINKER", _enquireLinker);

        for (ChannelHandler handler : _handlers)
            pipeline.addAfter(ctx.getName(), handler.getClass().getName(), handler);

        Channels.fireChannelConnected(ctx, ch.getRemoteAddress());
    }

    /**
     * Handles incoming WebSocket frames.
     *
     * @param ctx
     *          Channel handler context
     * @param frame
     *          Incoming WebSocket frame
     * @param e
     *          Message event
     * @throws Exception
     */
    protected void handleWebSocketFrame(ChannelHandlerContext ctx,
                                        WebSocketFrame frame, MessageEvent e)
            throws Exception
    {
        Channel ch = ctx.getChannel();
        _logger.debug("{} Received WebSocket frame: {}", ch, frame);

        if ((frame instanceof BinaryWebSocketFrame)
                || (frame instanceof TextWebSocketFrame))
        {
            ctx.sendUpstream(e);
        }
        else if (frame instanceof PingWebSocketFrame)
        {
            Channels.write(ctx, Channels.future(ch), PONG);
        }
        else if (frame instanceof CloseWebSocketFrame)
        {
            ch.write(frame).addListener(ChannelFutureListener.CLOSE);
        }
    }

    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e)
            throws Exception
    {
        // Do not propagate the connected event yet to ensure it occurs after handshake.
    }

}