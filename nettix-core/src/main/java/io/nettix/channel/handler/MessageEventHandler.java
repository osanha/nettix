package io.nettix.channel.handler;

import org.jboss.netty.channel.ChannelDownstreamHandler;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelUpstreamHandler;
import org.jboss.netty.channel.MessageEvent;

/**
 * Abstract handler that only processes message events for both upstream and downstream.
 * Handles message reception and message sending separately, leaving other events
 * to pass through the pipeline.
 *
 * @author sanha
 */
public abstract class MessageEventHandler
        implements ChannelUpstreamHandler, ChannelDownstreamHandler
{
    @Override
    public void handleUpstream(ChannelHandlerContext ctx, ChannelEvent e)
            throws Exception
    {
        if (e instanceof MessageEvent)
            messageReceived(ctx, (MessageEvent) e);
        else
            ctx.sendUpstream(e);
    }

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
     * Handles a received message event.
     *
     * @param ctx the channel context
     * @param e the message event
     * @throws Exception if an error occurs while processing the message
     */
    protected abstract void messageReceived(ChannelHandlerContext ctx,
                                            MessageEvent e) throws Exception;

    /**
     * Handles a message event that is requested to be sent downstream.
     *
     * @param ctx the channel context
     * @param e the message event
     * @throws Exception if an error occurs while sending the message
     */
    protected abstract void writeRequested(ChannelHandlerContext ctx,
                                           MessageEvent e) throws Exception;
}