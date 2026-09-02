package io.nettix.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An abstract class that manages the life-cycle of a thread-based execution.
 *
 * @author sanha
 */
public abstract class AbstractRunnable
        extends AbstractStartable
        implements Runnable
{
    /**
     * Logger instance
     */
    private static final Logger _logger = LoggerFactory.getLogger(AbstractRunnable.class);

    /**
     * The thread used for execution
     */
    private Thread _thread;

    /**
     * Constructor
     *
     * @param name the name of this runnable
     */
    public AbstractRunnable(String name)
    {
        super(name);
    }

    /**
     * Invoked when start() is called, before the thread begins execution.
     */
    @Override
    public void setUp()
    {
        _thread = new Thread(this, name());
        _thread.start();
    }

    /**
     * Invoked when stop() is called, before shutting down the thread.
     *
     * @throws Exception if an error occurs during teardown
     */
    @Override
    public void tearDown() throws Exception
    {
        _thread.interrupt();
    }

    /**
     * The thread's main execution method.
     */
    @Override
    public void run()
    {
        while (state() == State.RUNNING)
        {
            try
            {
                running();
            }
            catch (Exception e)
            {
                if (!(e instanceof InterruptedException))
                    _logger.error(name() + " thread encountered an exception", e);
            }
        }
    }

    /**
     * The method to be implemented by subclasses to define the thread's behavior.
     *
     * @throws Exception if an error occurs during execution
     */
    protected abstract void running() throws Exception;

}