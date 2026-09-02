package io.nettix.util;

import static io.nettix.util.Singleton.Timer;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.jboss.netty.util.Timeout;
import org.jboss.netty.util.TimerTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A map that handles object timeouts conveniently.
 *
 * @author sanha
 *
 * @param <K> the key type
 * @param <V> the value type
 */
public class TimeoutableMap<K, V>
{
    /**
     * Timeout handler interface.
     *
     * @param <K> the type of the key for the timed-out object
     * @param <V> the type of the timed-out object
     */
    public interface TimeoutHandler<K, V>
    {
        /**
         * Handles a timeout event.
         *
         * @param key the key of the timed-out object
         * @param value the timed-out object
         */
        void handleTimeout(K key, V value);
    }

    /**
     * Logger instance.
     */
    private static final Logger _logger = LoggerFactory.getLogger(TimeoutableMap.class);

    /**
     * Whether to log timeouts at ERROR level.
     */
    private boolean _timeoutIsError = true;

    /**
     * Name of the map, used in logging.
     */
    private final String _name;

    /**
     * Default timeout in seconds.
     */
    private final int _timeout;

    /**
     * Map to store timeoutable objects.
     */
    private final ConcurrentHashMap<K, Timeoutable> _map = new ConcurrentHashMap<K, Timeoutable>();

    /**
     * Timeout handler.
     */
    private TimeoutHandler<K, V> _handler;

    /**
     * Helper class for timeoutable objects.
     */
    class Timeoutable
    {
        /**
         * The key.
         */
        final K key;

        /**
         * The value.
         */
        final V value;

        /**
         * Timeout in seconds.
         */
        final int timeout;

        /**
         * Task scheduled for timeout execution.
         */
        volatile Timeout task;

        /**
         * Constructor.
         *
         * @param key the key
         * @param value the value
         * @param time timeout in seconds
         */
        Timeoutable(K key, V value, int time)
        {
            this.key = key;
            this.value = value;
            this.timeout = time;
        }
    }

    /**
     * Constructor.
     *
     * @param name the name of the map, used in logging
     */
    public TimeoutableMap(String name)
    {
        this(name, 0);
    }

    /**
     * Constructor.
     *
     * @param name the name of the cache map, used in logging
     * @param timeout default timeout in seconds
     */
    public TimeoutableMap(String name, int timeout)
    {
        _name = name;
        _timeout = timeout;
    }

    /**
     * Creates an internal helper object.
     *
     * @param key the key
     * @param value the value
     * @param timeout timeout in seconds
     * @return a new Timeoutable object
     */
    protected Timeoutable create(K key, V value, int timeout)
    {
        return new Timeoutable(key, value, timeout);
    }

    /**
     * Determines whether a timeout should be processed.
     *
     * @param o the helper object
     * @param task the scheduled timeout task
     * @return true if the timeout should be handled
     */
    protected boolean isTimeout(Timeoutable o, Timeout task)
    {
        return true;
    }

    /**
     * Post-processing after an object is retrieved.
     *
     * @param o the helper object
     */
    protected void afterGet(Timeoutable o)
    {
    }

    /**
     * Sets whether timeouts should be logged at ERROR level.
     *
     * @param isError true for ERROR level, false for INFO level
     */
    public void setTimeoutIsError(boolean isError)
    {
        _timeoutIsError = isError;
    }

    /**
     * Adds an object to the map.
     *
     * @param key the key
     * @param value the value
     * @return the previous value if present, otherwise null
     */
    public V put(K key, V value)
    {
        return put(key, value, _timeout);
    }

    /**
     * Adds an object if the key is absent.
     *
     * @param key the key
     * @param value the value
     * @return the existing value if present, otherwise null
     */
    public V putIfAbsent(K key, V value)
    {
        return putIfAbsent(key, value, _timeout);
    }

    /**
     * Adds an object if the key is absent.
     *
     * @param key the key
     * @param value the value
     * @param timeout timeout in seconds
     * @return the existing value if present, otherwise null
     */
    public V putIfAbsent(K key, V value, int timeout)
    {
        Timeoutable current = create(key, value, timeout);
        Timeoutable prev = _map.putIfAbsent(key, current);

        if (prev != null)
            return prev.value;

        if (timeout > 0)
            current.task = newTimeout(current);

        return current.value;
    }

    /**
     * Adds an object to the map with a specific timeout.
     *
     * @param key the key
     * @param value the value
     * @param timeout timeout in seconds
     * @return the previous value if present, otherwise null
     */
    public V put(K key, V value, int timeout)
    {
        Timeoutable current = create(key, value, timeout);
        Timeoutable prev = _map.put(key, current);

        if (timeout > 0)
            current.task = newTimeout(current);

        if (prev != null)
        {
            if (prev.task != null)
                prev.task.cancel();

            return prev.value;
        }
        else
        {
            return null;
        }
    }

    /**
     * Creates a new timeout task for the given object.
     *
     * @param o the helper object
     * @return the scheduled timeout task
     */
    private Timeout newTimeout(final Timeoutable o)
    {
        return Timer.newTimeout(new TimerTask()
        {
            @Override
            public void run(Timeout task) throws Exception
            {
                if (!task.isCancelled() && isTimeout(o, task))
                {
                    _map.remove(o.key);

                    if (_timeoutIsError)
                        _logger.error("{} [{}] has timed out", _name, o.key);
                    else
                        _logger.info("{} [{}] has timed out", _name, o.key);

                    if (_handler != null)
                        _handler.handleTimeout(o.key, o.value);
                }
            }
        }, o.timeout, TimeUnit.SECONDS);
    }

    /**
     * Retrieves an object from the map.
     *
     * @param key the key
     * @return the object, or null if not present
     */
    public V get(K key)
    {
        Timeoutable o = _map.get(key);

        if (o != null)
        {
            afterGet(o);
            return o.value;
        }
        else
        {
            return null;
        }
    }

    /**
     * Removes an object from the map.
     *
     * @param key the key
     * @return the removed object, or null if not present
     */
    public V remove(K key)
    {
        Timeoutable o = _map.remove(key);

        if (o != null)
        {
            if (o.task != null)
                o.task.cancel();

            return o.value;
        }
        else
        {
            return null;
        }
    }

    /**
     * Registers a timeout handler.
     *
     * @param handler the handler to call on timeout
     */
    public void setTimeoutHandler(TimeoutHandler<K, V> handler)
    {
        _handler = handler;
    }

    /**
     * Checks whether a key exists in the map.
     *
     * @param key the key
     * @return true if present, false otherwise
     */
    public boolean containsKey(K key)
    {
        return _map.containsKey(key);
    }

    /**
     * Clears all objects from the map.
     */
    public void clear()
    {
        for (K key : _map.keySet())
        {
            Timeoutable o = _map.remove(key);

            if (o.task != null)
                o.task.cancel();
        }
    }

    /**
     * Cancels an ongoing timeout for a specific key.
     *
     * @param key the key
     * @return true if the timeout was canceled
     */
    public boolean cancel(K key)
    {
        Timeoutable o = _map.get(key);

        if ((o != null) && (o.task != null))
        {
            o.task.cancel();
            o.task = null;
            return true;
        }

        return false;
    }

    /**
     * Returns the number of stored objects.
     *
     * @return the size of the map
     */
    public int size()
    {
        return _map.size();
    }

}