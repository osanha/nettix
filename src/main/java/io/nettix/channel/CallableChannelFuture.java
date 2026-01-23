package io.nettix.channel;

import java.util.concurrent.TimeUnit;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.DefaultChannelFuture;

/**
 * A {@code ChannelFuture} that can carry a return object.
 *
 * @param <T> the type of the object to be carried
 * @author sanha
 */
public class CallableChannelFuture<T>
        extends DefaultChannelFuture
{
    /**
     * The object to be carried.
     */
    private T _obj;

    /**
     * Default constructor.
     */
    public CallableChannelFuture()
    {
        super(null, false);
    }

    /**
     * Constructs a {@code CallableChannelFuture} for the specified channel.
     *
     * @param channel the channel associated with this future
     */
    public CallableChannelFuture(Channel channel)
    {
        super(channel, false);
    }

    /**
     * Constructs a {@code CallableChannelFuture} that has already failed.
     *
     * @param channel the channel associated with this future
     * @param cause the exception that caused the failure
     */
    public CallableChannelFuture(Channel channel, Throwable cause)
    {
        super(channel, false);
        setFailure(cause);
    }

    /**
     * Adds a result listener.
     *
     * @param listener the listener to be notified when the future completes
     */
    public void addListener(CallableChannelFutureListener<T> listener)
    {
        super.addListener((ChannelFutureListener) listener);
    }

    /**
     * Sets the result of this future to success with the specified object.
     *
     * @param obj the object to be carried as the result
     * @return {@code true} if this method successfully marked the future as
     *         successful, {@code false} if the future was already completed
     */
    public boolean setSuccess(T obj)
    {
        _obj = obj;
        return super.setSuccess();
    }

    /**
     * Returns the result object. If the operation is not yet complete,
     * waits until it is done.
     *
     * @return the result object
     */
    public T get()
    {
        if (!this.isDone())
            this.awaitUninterruptibly();

        return _obj;
    }

    /**
     * Returns the result object. If the operation is not yet complete,
     * waits up to the specified time.
     *
     * @param time the maximum time to wait
     * @param unit the time unit of the {@code time} argument
     * @return the result object
     */
    public T get(long time, TimeUnit unit)
    {
        if (!this.isDone())
            this.awaitUninterruptibly(time, unit);

        return _obj;
    }

}