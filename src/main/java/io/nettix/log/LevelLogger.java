package io.nettix.log;

/**
 * Logger that supports logging with a specified log level.
 *
 * @author sanha
 */
public class LevelLogger
{
    /**
     * Underlying SLF4J logger instance.
     */
    private final org.slf4j.Logger _logger;

    /**
     * Constructs a new {@code LevelLogger}.
     *
     * @param logger
     *          the SLF4J logger instance to delegate logging to
     */
    public LevelLogger(org.slf4j.Logger logger)
    {
        _logger = logger;
    }

    /**
     * Logs a message with the specified log level.
     *
     * @param level
     *          the log level
     * @param msg
     *          the log message
     */
    public void log(LogLevel level, String msg)
    {
        switch (level)
        {
            case DEBUG:
                _logger.debug(msg);
                break;
            case INFO:
                _logger.info(msg);
                break;
            case WARN:
                _logger.warn(msg);
                break;
            default:
                _logger.error(msg);
                break;
        }
    }

    /**
     * Logs a formatted message with a single argument.
     *
     * @param level
     *          the log level
     * @param format
     *          the message format string
     * @param arg
     *          the message argument
     */
    public void log(LogLevel level, String format, Object arg)
    {
        switch (level)
        {
            case DEBUG:
                _logger.debug(format, arg);
                break;
            case INFO:
                _logger.info(format, arg);
                break;
            case WARN:
                _logger.warn(format, arg);
                break;
            default:
                _logger.error(format, arg);
                break;
        }
    }

    /**
     * Logs a formatted message with two arguments.
     *
     * @param level
     *          the log level
     * @param format
     *          the message format string
     * @param arg1
     *          the first message argument
     * @param arg2
     *          the second message argument
     */
    public void log(LogLevel level, String format, Object arg1, Object arg2)
    {
        switch (level)
        {
            case DEBUG:
                _logger.debug(format, arg1, arg2);
                break;
            case INFO:
                _logger.info(format, arg1, arg2);
                break;
            case WARN:
                _logger.warn(format, arg1, arg2);
                break;
            default:
                _logger.error(format, arg1, arg2);
                break;
        }
    }

    /**
     * Logs a formatted message with multiple arguments.
     *
     * @param level
     *          the log level
     * @param format
     *          the message format string
     * @param args
     *          the list of message arguments
     */
    public void log(LogLevel level, String format, Object... args)
    {
        switch (level)
        {
            case DEBUG:
                _logger.debug(format, args);
                break;
            case INFO:
                _logger.info(format, args);
                break;
            case WARN:
                _logger.warn(format, args);
                break;
            default:
                _logger.error(format, args);
                break;
        }
    }

}