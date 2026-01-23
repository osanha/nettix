package io.nettix.channel;

import java.util.concurrent.TimeUnit;

import org.jboss.netty.channel.ChannelHandler.Sharable;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.util.Timeout;
import org.jboss.netty.util.TimerTask;

import io.nettix.channel.handler.ConnectStateEventHandler;
import io.nettix.util.Singleton;

/**
 * Manages client channels that maintain a persistent connection.
 * Actively closing the connection stops further reconnection attempts.
 *
 * @author sanha
 */
public class PersistentClientChannelManager
        extends ClientChannelManager
{
    /**
     * Handler that attempts reconnection after a certain period when the connection is lost.
     */
    @Sharable
    private class KeepConnectionHandler
            extends ConnectStateEventHandler
    {
        @Override
        protected void channelDisconnected(ChannelHandlerContext ctx,
                                           final ChannelStateEvent e)
                throws Exception
        {
            Singleton.Timer.newTimeout(new TimerTask()
            {
                @Override
                public void run(Timeout timeout) throws Exception
                {
                    PersistentClientChannelManager.this.connect(e.getChannel().getRemoteAddress());
                }
            }, _reconnDelay, TimeUnit.SECONDS);

            ctx.sendUpstream(e);
        }

    }

    /**
     * Waiting time (in seconds) before attempting reconnection after the connection is lost.
     */
    private int _reconnDelay = 1;

    /**
     * Reconnection handler instance
     */
    private final KeepConnectionHandler _reconnector = new KeepConnectionHandler();

    /**
     * Constructor.
     *
     * @param name
     *          the name
     */
    public PersistentClientChannelManager(String name)
    {
        super(name);
        setReconnCount(-1);
    }

    /**
     * Sets the waiting time (in seconds) before attempting reconnection after disconnection.
     *
     * @param delay
     *          time in seconds
     */
    public void setReconnDelay(int delay)
    {
        if (delay <= 0)
            throw new IllegalArgumentException("delay > 0");

        _reconnDelay = delay;
    }

    @Override
    public ChannelPipeline getPipeline() throws Exception
    {
        ChannelPipeline cp = super.getPipeline();
        cp.addLast("RECONNECTOR", _reconnector);
        return cp;
    }

}