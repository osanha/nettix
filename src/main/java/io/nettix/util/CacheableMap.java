package io.nettix.util;

import java.util.concurrent.TimeUnit;

import org.jboss.netty.util.Timeout;

/**
 * A map for handling caching conveniently.
 *
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of cached values
 *
 * @author sanha
 */
public class CacheableMap<K, V>
        extends TimeoutableMap<K, V>
{
    /**
     * Helper class representing a cacheable entry.
     */
    class Cacheable
            extends Timeoutable
    {
        /**
         * The timestamp of the last access to this cached object.
         */
        volatile long lastTime = System.currentTimeMillis();

        /**
         * Constructor.
         *
         * @param key the key
         * @param value the value
         * @param timeout timeout in seconds
         */
        Cacheable(K key, V value, int timeout)
        {
            super(key, value, timeout);
        }
    }

    /**
     * Constructor.
     *
     * @param name the name used for logging
     */
    public CacheableMap(String name)
    {
        this(name, 0);
    }

    /**
     * Constructor.
     *
     * @param name the name used for logging
     * @param timeout the timeout in seconds
     */
    public CacheableMap(String name, int timeout)
    {
        super(name, timeout);
        setTimeoutIsError(false);
    }

    @Override
    protected Timeoutable create(K key, V value, int timeout)
    {
        return new Cacheable(key, value, timeout);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void afterGet(Timeoutable o)
    {
        ((Cacheable) o).lastTime = System.currentTimeMillis();
    }

    @SuppressWarnings("unchecked")
    @Override
    protected boolean isTimeout(Timeoutable o, Timeout task)
    {
        long nextDelay = (o.timeout * 1000L)
                - (System.currentTimeMillis() - ((Cacheable) o).lastTime);

        if (nextDelay <= 0)
        {
            return true;
        }
        else
        {
            o.task = task.getTimer().newTimeout(task.getTask(), nextDelay,
                    TimeUnit.MILLISECONDS);
            return false;
        }
    }

}