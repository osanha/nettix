package io.nettix.websocket;

import static org.jboss.netty.channel.ChannelFutureListener.CLOSE;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.FORBIDDEN;

import java.net.URI;
import java.net.URISyntaxException;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.handler.codec.http.websocketx.WebSocketFrame;
import org.jboss.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import org.jboss.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;

import io.nettix.http.HttpHeadersExt;
import io.nettix.http.HttpRequest;
import io.nettix.http.HttpResponse;
import io.nettix.http.HttpUtil;

/**
 * WebSocket server channel handler.
 * Handles HTTP handshake requests and WebSocket frames.
 *
 * @author sanha
 */
public class WebSocketServerHandler
        extends AbstractWebSocketHandler
{
    /**
     * Constructor.
     *
     * @param uri
     *          The URI to which the server will bind.
     * @param handlers
     *          Additional handlers to add after the handshake.
     * @throws URISyntaxException
     *           If the given URI string is invalid.
     */
    public WebSocketServerHandler(String uri, ChannelHandler... handlers)
            throws URISyntaxException
    {
        super(uri, handlers);
    }

    /**
     * Constructor.
     *
     * @param uri
     *          The URI to which the server will bind.
     * @param handlers
     *          Additional handlers to add after the handshake.
     */
    public WebSocketServerHandler(URI uri, ChannelHandler... handlers)
    {
        super(uri, handlers);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
            throws Exception
    {
        Object o = e.getMessage();

        if (o instanceof HttpRequest)
            handleHttpRequest(ctx, (HttpRequest) o);
        else
            handleWebSocketFrame(ctx, (WebSocketFrame) o, e);
    }

    /**
     * Handles incoming HTTP requests for WebSocket handshake.
     *
     * @param ctx
     *          The channel handler context.
     * @param req
     *          The incoming HTTP request.
     * @throws Exception
     *           If an error occurs during processing.
     */
    private void handleHttpRequest(final ChannelHandlerContext ctx,
                                   final HttpRequest req) throws Exception
    {
        String path = req.getPath();

        if (!path.equals(uri().getPath()))
        {
            HttpResponse res = new HttpResponse(FORBIDDEN);
            String msg = "Invalid URI - " + path;
            HttpHeadersExt.setContentLength(res, msg.length());
            HttpUtil.writeResponse(ctx, res, msg).addListener(CLOSE);
            return;
        }

        Channel ch = ctx.getChannel();
        WebSocketServerHandshakerFactory f = new WebSocketServerHandshakerFactory(
                uri().toString(),
                null,
                false);
        WebSocketServerHandshaker handshaker = f.newHandshaker(req);

        if (handshaker == null)
            f.sendUnsupportedWebSocketVersionResponse(ch).addListener(CLOSE);
        else
            handshaker.handshake(ch, req).addListener(new ChannelFutureListener()
            {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception
                {
                    if (future.isSuccess())
                        WebSocketServerHandler.this.handshakeCompleted(ctx);
                }
            });
    }

}