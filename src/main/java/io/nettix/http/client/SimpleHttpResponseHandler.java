package io.nettix.http.client;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.handler.codec.http.HttpChunk;

/**
 * A simple HTTP response handler that only implements the handling of {@code HttpResponse}.
 * <p>
 * This abstract class provides default (no-op) implementations for all callback methods
 * defined in {@link HttpResponseHandler}, allowing subclasses to override only the methods
 * they need.
 * </p>
 *
 * @author sanha
 */
public abstract class SimpleHttpResponseHandler
        implements HttpResponseHandler
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
    public void chunkReceived(Channel ch, HttpChunk chunk)
    {
    }

    @Override
    public void beforeRequest(Channel ch)
    {
    }

    @Override
    public void requestComplete(Channel ch)
    {
    }

    @Override
    public void exceptionCaught(Channel ch, Throwable e)
    {
    }
}