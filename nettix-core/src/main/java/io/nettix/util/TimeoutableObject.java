package io.nettix.util;

import java.util.concurrent.TimeUnit;

import org.jboss.netty.util.Timeout;
import org.jboss.netty.util.TimerTask;

/**
 * Wrapper class for handling a timeout for a single object.
 *
 * @param <T> the type of the object
 */
public class TimeoutableObject<T>
{
    /**
     * Interface for handling timeouts.
     *
     * @param <T> the type of the object
     */
    public interface TimeoutHandler<T>
    {
        /**
         * Handles the timeout event.
         *
         * @param value the object whose timeout occurred
         */
        void handleTimeout(T value);
    }

    /**
     * Timeout duration in seconds.
     */
    private final int _delay;

    /**
     * Timeout handler.
     */
    private TimeoutHandler<T> _handler;

    /**
     * The object being managed.
     */
    private volatile T _value;

    /**
     * Timeout task reference.
     */
    private volatile Timeout _timeout;

    /**
     * Default constructor.
     */
    public TimeoutableObject()
    {
        _delay = 0;
    }

    /**
     * Constructor with timeout duration.
     *
     * @param delay timeout duration in seconds
     * @throws IllegalArgumentException if delay is not positive
     */
    public TimeoutableObject(int delay)
    {
        if (delay <= 0)
            throw new IllegalArgumentException("Invalid delay - " + delay);

        _delay = delay;
    }

    /**
     * Sets the timeout handler.
     *
     * @param handler the handler to be called on timeout
     */
    public void setTimeoutHandler(TimeoutHandler<T> handler)
    {
        _handler = handler;
    }

    /**
     * Sets the object with the default timeout.
     *
     * @param value the object to manage
     */
    public void set(T value)
    {
        set(value, _delay);
    }

    /**
     * Sets the object with a specific handler and timeout.
     *
     * @param value the object to manage
     * @param handler the handler to be called on timeout
     * @param delay timeout duration in seconds
     */
    public void set(T value, TimeoutHandler<T> handler, int delay)
    {
        set(value, delay);
        _handler = handler;
    }

    /**
     * Sets the object with a specific timeout duration.
     *
     * @param value the object to manage
     * @param delay timeout duration in seconds
     * @throws IllegalArgumentException if value is null or delay is not positive
     */
    public void set(T value, int delay)
    {
        if (delay <= 0)
            throw new IllegalArgumentException("Invalid delay - " + delay);

        if (value == null)
            throw new IllegalArgumentException("value is null");

        cancel();
        _value = value;

        _timeout = Singleton.Timer.newTimeout(new TimerTask()
        {
            @Override
            public void run(Timeout timeout) throws Exception
            {
                T value = _value;
                _value = null;
                _timeout = null;

                if (_handler != null)
                    _handler.handleTimeout(value);
            }
        }, delay, TimeUnit.SECONDS);
    }

    /**
     * Cancels the active timeout.
     *
     * @return true if a timeout was active and canceled
     */
    public boolean cancel()
    {
        if (_timeout != null)
        {
            _timeout.cancel();
            _timeout = null;
            return true;
        }

        return false;
    }

    /**
     * Removes and returns the managed object.
     *
     * @return the managed object, or null if none
     */
    public T remove()
    {
        cancel();

        T value = _value;
        _value = null;
        return value;
    }

    /**
     * Checks if the managed object exists.
     *
     * @return true if the object exists
     */
    public boolean contains()
    {
        return _value != null;
    }

    /**
     * Returns the managed object without removing it.
     *
     * @return the managed object
     */
    public T get()
    {
        return _value;
    }

}