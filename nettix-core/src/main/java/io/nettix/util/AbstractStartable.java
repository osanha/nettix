package io.nettix.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract class for managing the execution life-cycle.
 *
 * @author sanha
 */
public abstract class AbstractStartable
{
    /**
     * Execution states.
     */
    public enum State
    {
        /**
         * Before execution.
         */
        READY,

        /**
         * Currently running.
         */
        RUNNING,

        /**
         * Terminated state.
         */
        TERMINATED;
    }

    /**
     * Logger instance.
     */
    private static final Logger _logger = LoggerFactory.getLogger(AbstractStartable.class);

    /**
     * Name of this startable.
     */
    private final String _name;

    /**
     * Current execution state.
     */
    private State _state = State.READY;

    /**
     * Constructor.
     *
     * @param name the name of this startable
     */
    public AbstractStartable(String name)
    {
        _name = name;
    }

    /**
     * Starts the execution.
     */
    public synchronized void start()
    {
        if (_state != State.READY)
            throw new IllegalStateException(_state.toString());

        _logger.info("Starting {}", _name);
        _state = State.RUNNING;

        try
        {
            setUp();
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * Stops the execution.
     */
    public synchronized void stop()
    {
        if (_state != State.RUNNING)
            return;

        _logger.info("Stopping {}", _name);
        _state = State.TERMINATED;

        try
        {
            tearDown();
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the name of this startable.
     *
     * @return the name
     */
    public String name()
    {
        return _name;
    }

    /**
     * Returns the current execution state.
     *
     * @return the state
     */
    public State state()
    {
        return _state;
    }

    /**
     * Called when start() is invoked.
     *
     * @throws Exception if setup fails
     */
    protected abstract void setUp() throws Exception;

    /**
     * Called when stop() is invoked.
     *
     * @throws Exception if teardown fails
     */
    protected abstract void tearDown() throws Exception;

}