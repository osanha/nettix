package io.nettix.util;

/**
 * A callback interface to perform actions after a certain processing is completed.
 *
 * @param <T>
 *          the type of the parameter passed to the callback
 * @author sanha
 */
public interface Callback<T>
{
    /**
     * Executes the post-processing action.
     *
     * @param obj
     *          the parameter object to be processed
     */
    void run(T obj);
}