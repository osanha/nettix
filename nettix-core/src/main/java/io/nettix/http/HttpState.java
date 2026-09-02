package io.nettix.http;

/**
 * HTTP messaging states.
 *
 * Represents the current state of an HTTP channel or message.
 *
 * @author sanha
 */
public enum HttpState
{
    /**
     * Channel is connected. If SSL is enabled, the handshake is completed.
     */
    CONNECTED,

    /**
     * HTTP request message has been sent or received.
     */
    REQUESTED,

    /**
     * HTTP response message has been sent or received.
     */
    RESPONSED,

    /**
     * An exception has been handled.
     */
    EXCEPTION_HANDLED;
}