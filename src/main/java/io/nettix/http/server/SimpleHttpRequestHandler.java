package io.nettix.http.server;

import java.net.SocketAddress;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.handler.codec.http.HttpChunk;

/**
 * A simple HTTP request handler.
 * <p>
 * This handler only implements the logic for receiving {@code HttpRequest} messages.
 * </p>
 *
 * @author sanha
 */
public abstract class SimpleHttpRequestHandler
        implements HttpRequestHandler
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
    public void chunkReceived(Channel ch, SocketAddress addr, HttpChunk chunk)
            throws Exception
    {
    }

}