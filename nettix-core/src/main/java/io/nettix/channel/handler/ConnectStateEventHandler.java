package io.nettix.channel.handler;

import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelState;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ChannelUpstreamHandler;

/**
 * Abstract handler that only processes channel connection events.
 * Handles connected and disconnected states.
 *
 * @author sanha
 */
public abstract class ConnectStateEventHandler implements ChannelUpstreamHandler {

    @Override
    public void handleUpstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
        if (e instanceof ChannelStateEvent) {
            ChannelStateEvent evt = (ChannelStateEvent) e;

            if (evt.getState() == ChannelState.CONNECTED) {
                if (evt.getValue() != null) {
                    // Channel has been connected
                    channelConnected(ctx, evt);
                } else {
                    // Channel has been disconnected
                    channelDisconnected(ctx, evt);
                }
                return;
            }
        }

        // Pass other events upstream
        ctx.sendUpstream(e);
    }

    /**
     * Handles the channel connected event.
     *
     * @param ctx
     *          the channel handler context
     * @param e
     *          the channel state event
     * @throws Exception
     *           if an error occurs while handling the event
     */
    protected void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        ctx.sendUpstream(e);
    }

    /**
     * Handles the channel disconnected event.
     *
     * @param ctx
     *          the channel handler context
     * @param e
     *          the channel state event
     * @throws Exception
     *           if an error occurs while handling the event
     */
    protected void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        ctx.sendUpstream(e);
    }

}