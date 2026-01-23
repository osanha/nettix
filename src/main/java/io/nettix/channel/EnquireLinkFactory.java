package io.nettix.channel;

import org.jboss.netty.channel.ChannelFuture;

/**
 * Factory for creating messages to check the connection status.
 *
 * @author sanha
 *
 * @param <T>
 *          the type of message
 */
public interface EnquireLinkFactory<T>
        extends HeartbeatFactory<T>
{
    /**
     * Stores the result object for a sent message.
     *
     * @param heartbeat
     *          the message that was sent
     * @param future
     *          the channel result
     */
    void putChannelFuture(T heartbeat, ChannelFuture future);
}