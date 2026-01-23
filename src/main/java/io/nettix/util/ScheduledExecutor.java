package io.nettix.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Handles periodic task execution.
 *
 * @author sanha
 *
 * @param <K>
 *          The type of key identifying a scheduled task.
 */
public class ScheduledExecutor<K>
{
    /**
     * The executor service.
     */
    private final ScheduledExecutorService _executor;

    /**
     * Map storing scheduled task futures.
     */
    private final Map<K, ScheduledFuture<?>> _futureMap = new ConcurrentHashMap<K, ScheduledFuture<?>>();

    /**
     * Initial delay in seconds.
     */
    private final int _initDelay;

    /**
     * Interval or delay time in seconds.
     */
    private final int _time;

    /**
     * Constructor.
     *
     * @param size
     *          Size of the thread pool.
     */
    public ScheduledExecutor(int size)
    {
        this(size, 0, 0);
    }

    /**
     * Constructor.
     *
     * @param size
     *          Size of the thread pool.
     * @param initDelay
     *          Initial delay in seconds.
     * @param time
     *          Interval or delay time in seconds.
     */
    public ScheduledExecutor(int size, int initDelay, int time)
    {
        _executor = Executors.newScheduledThreadPool(size);

        _initDelay = initDelay;
        _time = time;
    }

    /**
     * Schedules a task to run at fixed intervals.
     *
     * @param key
     *          The key identifying the task.
     * @param task
     *          The task to run.
     */
    public void scheduleAtFixedRate(K key, ScheduledTask<K> task)
    {
        scheduleAtFixedRate(key, task, _initDelay, _time);
    }

    /**
     * Schedules a task to run at fixed intervals.
     *
     * @param key
     *          The key identifying the task.
     * @param task
     *          The task to run.
     * @param initDelay
     *          Initial delay in seconds.
     * @param time
     *          Interval time in seconds.
     */
    public void scheduleAtFixedRate(K key, ScheduledTask<K> task, int initDelay,
                                    int time)
    {
        schedule(key, task, initDelay, time, true);
    }

    /**
     * Schedules a task with a fixed delay between the end of one execution
     * and the start of the next.
     *
     * @param key
     *          The key identifying the task.
     * @param task
     *          The task to run.
     */
    public void scheduleWithFixedDelay(K key, ScheduledTask<K> task)
    {
        scheduleWithFixedDelay(key, task, _initDelay, _time);
    }

    /**
     * Schedules a task with a fixed delay between the end of one execution
     * and the start of the next.
     *
     * @param key
     *          The key identifying the task.
     * @param task
     *          The task to run.
     * @param initDelay
     *          Initial delay in seconds.
     * @param time
     *          Delay time in seconds.
     */
    public void scheduleWithFixedDelay(K key, ScheduledTask<K> task,
                                       int initDelay, int time)

    {
        schedule(key, task, initDelay, time, false);
    }

    /**
     * Schedules a task with the specified time unit behavior.
     *
     * @param key
     *          The key identifying the task.
     * @param task
     *          The task to run.
     * @param initDelay
     *          Initial delay in seconds.
     * @param time
     *          Interval or delay time in seconds.
     * @param isRate
     *          True if fixed rate, false if fixed delay.
     */
    public void schedule(final K key, final ScheduledTask<K> task, int initDelay,
                         int time, boolean isRate)
    {
        if (initDelay < 0)
            throw new IllegalArgumentException("Invalid initial delay: " + initDelay);

        if (time <= 0)
            throw new IllegalArgumentException("Invalid time: " + time);

        Runnable cmd = new Runnable()
        {
            @Override
            public void run()
            {
                task.run(key);
            }
        };

        if (isRate)
            _executor.scheduleAtFixedRate(cmd, initDelay, time, TimeUnit.SECONDS);
        else
            _executor.scheduleWithFixedDelay(cmd, initDelay, time, TimeUnit.SECONDS);
    }

    /**
     * Cancels a scheduled task.
     *
     * @param key
     *          The key identifying the task.
     * @param interruptable
     *          Whether the task should be interrupted.
     */
    public void cancel(K key, boolean interruptable)
    {
        ScheduledFuture<?> f = _futureMap.remove(key);

        if (f != null)
            f.cancel(interruptable);
    }

    /**
     * Shuts down the executor service.
     */
    public void shutdown()
    {
        _executor.shutdown();
    }

}