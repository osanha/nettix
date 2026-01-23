package io.nettix.util;

/**
 * Represents a task that is executed at scheduled intervals.
 *
 * @param <K>
 *          the type of the key associated with the task
 *
 * @author sanha
 */
public interface ScheduledTask<K>
{
    /**
     * Executes the task.
     *
     * @param key
     *          the key value associated with this execution
     */
    public void run(K key);
}