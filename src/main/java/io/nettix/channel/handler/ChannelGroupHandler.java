package io.nettix.channel.handler;

import org.jboss.netty.channel.ChannelHandler.Sharable;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;

/**
 * A handler that manages a group of channel connections.
 * <p>
 * This handler keeps track of all connected channels in a {@link ChannelGroup}.
 * </p>
 *
 * @author sanha
 */
@Sharable
public class ChannelGroupHandler
        extends ConnectStateEventHandler
{
    /**
     * The channel group managed by this handler.
     */
    private final ChannelGroup _connections;

    /**
     * Default constructor.
     * <p>
     * Initializes an empty channel group.
     * </p>
     */
    public ChannelGroupHandler()
    {
        _connections = new DefaultChannelGroup();
    }

    /**
     * Constructor with a specified name for the channel group.
     *
     * @param name the name of the channel group
     */
    public ChannelGroupHandler(String name)
    {
        _connections = new DefaultChannelGroup(name);
    }

    @Override
    protected void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e)
            throws Exception
    {
        _connections.add(e.getChannel());
        ctx.sendUpstream(e);
    }

    /**
     * Returns the channel group managed by this handler.
     *
     * @return the {@link ChannelGroup} containing all connected channels
     */
    public ChannelGroup connections()
    {
        return _connections;
    }

}