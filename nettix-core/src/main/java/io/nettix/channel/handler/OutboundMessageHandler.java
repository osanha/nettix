package io.nettix.channel.handler;

import org.jboss.netty.channel.ChannelDownstreamHandler;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

/**
 * Abstract handler that processes only outbound message events.
 *
 * Handles events that are intended to be sent downstream through the channel.
 * Subclasses should implement the {@link #writeRequested(ChannelHandlerContext, MessageEvent)}
 * method to define how outbound messages are handled.
 *
 * @author sanha
 */
public abstract class OutboundMessageHandler
        implements ChannelDownstreamHandler
{
    @Override
    public void handleDownstream(ChannelHandlerContext ctx, ChannelEvent e)
            throws Exception
    {
        if (e instanceof MessageEvent)
            writeRequested(ctx, (MessageEvent) e);
        else
            ctx.sendDownstream(e);
    }

    /**
     * Handles an outbound message event.
     *
     * @param ctx the channel handler context
     * @param e the outbound message event
     * @throws Exception if an error occurs during processing
     */
    protected abstract void writeRequested(ChannelHandlerContext ctx,
                                           MessageEvent e) throws Exception;
}