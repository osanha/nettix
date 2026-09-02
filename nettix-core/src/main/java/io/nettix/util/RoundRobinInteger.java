package io.nettix.util;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * A round-robin sequencer that cycles between a minimum and maximum value.
 *
 * @author sanha
 */
public class RoundRobinInteger
{
    /**
     * Maximum possible value.
     */
    public static final int MAX_POSITIVE_VALUE = 2147483647;

    /**
     * Minimum possible value.
     */
    public static final int MAX_NEGATIVE_VALUE = -2147483648;

    /**
     * Minimum value.
     */
    private final int _min;

    /**
     * Maximum value.
     */
    private final int _max;

    /**
     * Current value.
     */
    private AtomicInteger _value;

    /**
     * Constructor with only maximum value. Minimum is set to 0.
     *
     * @param max
     *          Maximum value.
     */
    public RoundRobinInteger(int max)
    {
        this(0, max);
    }

    /**
     * Constructor with minimum and maximum values.
     *
     * @param min
     *          Minimum value.
     * @param max
     *          Maximum value.
     */
    public RoundRobinInteger(int min, int max)
    {
        _min = min;
        _max = max;
        _value = new AtomicInteger(min);
    }

    /**
     * Returns the current value and increments it. Rolls over to minimum value if maximum is exceeded.
     *
     * @return The current value before incrementing.
     */
    public int next()
    {
        int value = _value.getAndIncrement();

        if (value > _max)
        {
            synchronized (_value)
            {
                if (_value.get() > _max)
                {
                    _value.set(_min + 1);
                    return _min;
                }
                else
                {
                    return _value.getAndIncrement();
                }
            }
        }
        else
        {
            return value;
        }
    }

}