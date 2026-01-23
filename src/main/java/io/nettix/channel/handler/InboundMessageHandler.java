package io.nettix.channel.handler;

import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelUpstreamHandler;
import org.jboss.netty.channel.MessageEvent;

/**
 * Abstract handler that only processes inbound message events.
 *
 * @author sanha
 */
public abstract class InboundMessageHandler
        implements ChannelUpstreamHandler
{
    @Override
    public void handleUpstream(ChannelHandlerContext ctx, ChannelEvent e)
            throws Exception
    {
        if (e instanceof MessageEvent) {
            // Handle the received message event
            messageReceived(ctx, (MessageEvent) e);
        } else {
            // Pass other events upstream
            ctx.sendUpstream(e);
        }
    }

    /**
     * Handles the received message event.
     *
     * @param ctx
     *          the channel handler context
     * @param e
     *          the message event
     * @throws Exception
     *           if an error occurs while processing the message
     */
    protected abstract void messageReceived(ChannelHandlerContext ctx,
                                            MessageEvent e) throws Exception;
}