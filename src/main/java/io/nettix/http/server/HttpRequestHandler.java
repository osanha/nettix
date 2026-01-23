package io.nettix.http.server;

import java.net.SocketAddress;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.handler.codec.http.HttpChunk;

import io.nettix.http.HttpRequest;
import io.nettix.http.HttpUtil;

/**
 * Interface for handling HTTP requests.
 *
 * @author sanha
 */
public interface HttpRequestHandler
{
    /**
     * Default handler that responds with 204 No Content.
     */
    HttpRequestHandler DEFAULT = new HttpRequestHandler()
    {
        @Override
        public void connected(Channel ch)
        {
        }

        @Override
        public void disconnected(Channel ch)
        {
        }

        @Override
        public void requestReceived(Channel ch, SocketAddress addr, HttpRequest req)
                throws Exception
        {
            if (!req.isChunked())
                HttpUtil.writeNoContentResponse(ch);
        }

        @Override
        public void chunkReceived(Channel ch, SocketAddress addr, HttpChunk chunk)
                throws Exception
        {
            if (chunk.isLast())
                HttpUtil.writeNoContentResponse(ch);
        }
    };

    /**
     * Invoked when a socket connection is successfully established.
     *
     * @param ch
     *        the channel associated with the connection
     */
    void connected(Channel ch);

    /**
     * Invoked when a socket connection is closed.
     *
     * @param ch
     *        the channel associated with the connection
     */
    void disconnected(Channel ch);

    /**
     * Invoked when an HTTP request message is received.
     *
     * @param ch
     *        the channel associated with the connection
     * @param addr
     *        the remote address of the event object; required for connectionless protocols such as UDP
     * @param req
     *        the HTTP request message
     * @throws Exception
     *         if an error occurs while handling the request
     */
    void requestReceived(Channel ch, SocketAddress addr, HttpRequest req)
            throws Exception;

    /**
     * Invoked when an HTTP chunk message is received.
     *
     * @param ch
     *        the channel associated with the connection
     * @param addr
     *        the remote address of the event object; required for connectionless protocols such as UDP
     * @param chunk
     *        the HTTP chunk message
     * @throws Exception
     *         if an error occurs while handling the chunk
     */
    void chunkReceived(Channel ch, SocketAddress addr, HttpChunk chunk)
            throws Exception;
}