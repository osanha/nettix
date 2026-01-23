package io.nettix.util;

/**
 * A utility class to easily register a shutdown hook that runs automatically
 * when a daemon application terminates.
 *
 * @author sanha
 */
public abstract class AbstractStoppable
{
    public AbstractStoppable()
    {
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                stop();
            }
        }));
    }

    /**
     * Method executed when the daemon is stopping.
     */
    protected abstract void stop();
}