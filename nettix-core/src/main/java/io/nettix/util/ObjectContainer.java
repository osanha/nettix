package io.nettix.util;

/**
 * A class that wraps and holds an object.
 *
 * @param <T> the type of the object
 * @author sanha
 */
public class ObjectContainer<T>
{
    /**
     * The contained object.
     */
    private T _value;

    /**
     * Default constructor.
     */
    public ObjectContainer()
    {
    }

    /**
     * Constructor with an initial object.
     *
     * @param value the object to hold
     */
    public ObjectContainer(T value)
    {
        _value = value;
    }

    /**
     * Sets the contained object.
     *
     * @param value the object to set
     */
    public void set(T value)
    {
        _value = value;
    }

    /**
     * Returns the contained object.
     *
     * @return the contained object
     */
    public T get()
    {
        return _value;
    }

    /**
     * Checks if an object is currently held.
     *
     * @return true if an object is held, false otherwise
     */
    public boolean contains()
    {
        return _value != null;
    }

    /**
     * Removes and returns the held object.
     *
     * @return the previously held object, or null if none
     */
    public T remove()
    {
        T tmp = _value;
        _value = null;
        return tmp;
    }

    @Override
    public String toString()
    {
        if (_value != null)
            return _value.toString();
        else
            return "null";
    }

}