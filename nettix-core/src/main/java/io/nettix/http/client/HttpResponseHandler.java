package io.nettix.http.client;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.handler.codec.http.HttpChunk;

import io.nettix.http.HttpResponse;

/**
 * Interface for handling HTTP responses.
 *
 * @author sanha
 */
public interface HttpResponseHandler
{
    /**
     * Invoked when the socket connection is successfully established.
     *
     * @param ch
     *          the channel
     */
    void connected(Channel ch);

    /**
     * Invoked when the socket connection is closed.
     *
     * @param ch
     *          the channel
     */
    void disconnected(Channel ch);

    /**
     * Called right before sending the request message.
     * This can be used to attach something to the channel.
     *
     * @param ch
     *          the channel
     */
    void beforeRequest(Channel ch);

    /**
     * Called when the request message has been fully sent.
     * Use this point to send additional chunks if needed.
     *
     * @param ch
     *          the channel
     */
    void requestComplete(Channel ch);

    /**
     * Invoked when an HTTP response message is received.
     *
     * @param ch
     *          the channel
     * @param res
     *          the HTTP response message
     */
    void responseReceived(Channel ch, HttpResponse res);

    /**
     * Invoked when an HTTP chunk message is received.
     *
     * @param ch
     *          the channel
     * @param chunk
     *          the HTTP chunk message
     */
    void chunkReceived(Channel ch, HttpChunk chunk);

    /**
     * Invoked when an exception is caught during processing.
     *
     * @param ch
     *          the channel
     * @param e
     *          the exception
     */
    void exceptionCaught(Channel ch, Throwable e);
}