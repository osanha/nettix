package io.nettix.channel;

import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;

/**
 * A channel future listener with a return value.
 *
 * @author sanha
 *
 * @param <T>
 *          Type of the return value
 */
public abstract class CallableChannelFutureListener<T>
        implements ChannelFutureListener
{

    @SuppressWarnings("unchecked")
    @Override
    public void operationComplete(ChannelFuture future) throws Exception
    {
        operationComplete((CallableChannelFuture<T>) future);
    }

    /**
     * Handles the completion when a result is available.
     *
     * @param future
     *          The completed future containing the result
     * @throws Exception
     *           if an error occurs while processing the result
     */
    protected abstract void operationComplete(CallableChannelFuture<T> future)
            throws Exception;

}