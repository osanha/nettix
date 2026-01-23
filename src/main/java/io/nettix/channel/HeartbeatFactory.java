package io.nettix.channel;

/**
 * Generates messages to be sent at regular intervals.
 *
 * @param <T> the type of message
 * @author sanha
 */
public interface HeartbeatFactory<T>
{
    /**
     * Creates a message to be sent.
     *
     * @return the message object
     */
    T createHeartbeat();
}