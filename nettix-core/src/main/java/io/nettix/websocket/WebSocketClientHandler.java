package io.nettix.websocket;

import java.net.URI;
import java.net.URISyntaxException;

import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import org.jboss.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import org.jboss.netty.handler.codec.http.websocketx.WebSocketFrame;
import org.jboss.netty.handler.codec.http.websocketx.WebSocketHandshakeException;
import org.jboss.netty.handler.codec.http.websocketx.WebSocketVersion;

/**
 * WebSocket client channel handler.
 * Handles WebSocket handshake and frame processing.
 *
 * @author sanha
 */
public class WebSocketClientHandler
        extends AbstractWebSocketHandler
{
    /**
     * WebSocket handshake handler.
     */
    private WebSocketClientHandshaker _handshaker;

    /**
     * Constructor.
     *
     * @param uri
     *          Connection URI.
     * @param handlers
     *          Additional channel handlers to add after handshake is complete.
     * @throws URISyntaxException
     */
    public WebSocketClientHandler(String uri, ChannelHandler... handlers)
            throws URISyntaxException
    {
        super(uri, handlers);
    }

    /**
     * Constructor.
     *
     * @param uri
     *          Connection URI.
     * @param handlers
     *          Additional channel handlers to add after handshake is complete.
     */
    public WebSocketClientHandler(URI uri, ChannelHandler... handlers)
    {
        super(uri, handlers);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
            throws Exception
    {
        Object o = e.getMessage();

        if (o instanceof HttpResponse)
          {
            _handshaker.finishHandshake(ctx.getChannel(), (HttpResponse) o);
            handshakeCompleted(ctx);
          }
        else
          {
            handleWebSocketFrame(ctx, (WebSocketFrame) o, e);
          }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
            throws Exception
    {
        if (e.getCause() instanceof WebSocketHandshakeException)
            ctx.getChannel().close();
    }

    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e)
            throws Exception
    {
        WebSocketClientHandshakerFactory f = new WebSocketClientHandshakerFactory();
        _handshaker = f.newHandshaker(uri(), WebSocketVersion.V13, null, false,
                null);
        _handshaker.handshake(ctx.getChannel());
    }

}