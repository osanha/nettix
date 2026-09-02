package io.nettix.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Properties;

/**
 * Configuration based on a properties file.
 *
 * @author sanha
 */
public class PropertiesConfig
{
    /**
     * Properties file holding the configuration.
     */
    private final Properties _properties = new Properties();

    /**
     * Constructor.
     *
     * @param properties
     *          Path to the configuration file.
     *
     * @throws FileNotFoundException if the file does not exist.
     * @throws IOException if an I/O error occurs.
     */
    public PropertiesConfig(String properties) throws FileNotFoundException,
            IOException
    {
        _properties.load(new FileInputStream(properties));
    }

    /**
     * Checks if the configuration contains the given key.
     *
     * @param key
     *          The key to check.
     * @return true if the key exists in the configuration.
     */
    public boolean contains(String key)
    {
        return _properties.containsKey(key);
    }

    /**
     * Returns the value associated with the key as a trimmed string.
     *
     * @param key
     *          The key to look up.
     * @param defaultValue
     *          The default value to return if the key does not exist. If both
     *          the key is missing and defaultValue is null, a NoSuchElementException
     *          is thrown.
     * @return The value associated with the key.
     */
    public String getString(String key, String defaultValue)
    {
        String property = _properties.getProperty(key);

        if (property == null)
        {
            if (defaultValue != null)
                property = defaultValue;
            else
                throw new NoSuchElementException(key);
        }

        return property.trim();
    }

    /**
     * Returns the value associated with the key as a trimmed string.
     *
     * @param key
     *          The key to look up.
     * @return The value associated with the key. Throws NoSuchElementException
     *         if the key is not found.
     */
    public String getString(String key)
    {
        return getString(key, null);
    }

    /**
     * Returns the value associated with the key as an int.
     *
     * @param key
     *          The key to look up.
     * @return The integer value associated with the key. Throws
     *         NoSuchElementException if the key is not found.
     */
    public int getInt(String key)
    {
        return Integer.valueOf(getString(key));
    }

    /**
     * Returns the value associated with the key as a long.
     *
     * @param key
     *          The key to look up.
     * @return The long value associated with the key. Throws
     *         NoSuchElementException if the key is not found.
     */
    public long getLong(String key)
    {
        return Long.valueOf(getString(key));
    }

    /**
     * Returns the value associated with the key as an Integer.
     *
     * @param key
     *          The key to look up.
     * @return The Integer value associated with the key, or null if the key
     *         does not exist.
     */
    public Integer getIntegerOrNull(String key)
    {
        if (_properties.containsKey(key))
            return Integer.valueOf(getString(key));
        else
            return null;
    }

    /**
     * Returns the value associated with the key as a String.
     *
     * @param key
     *          The key to look up.
     * @return The string value associated with the key, or null if the key
     *         does not exist.
     */
    public String getStringOrNull(String key)
    {
        if (_properties.containsKey(key))
            return getString(key);
        else
            return null;
    }

    /**
     * Returns the value associated with the key as a boolean.
     *
     * @param key
     *          The key to look up.
     * @return The boolean value associated with the key. Throws
     *         NoSuchElementException if the key is not found.
     */
    public boolean getBoolean(String key)
    {
        return Boolean.valueOf(getString(key));
    }

    /**
     * Returns the value associated with the key as a double.
     *
     * @param key
     *          The key to look up.
     * @return The double value associated with the key. Throws
     *         NoSuchElementException if the key is not found.
     */
    public double getDouble(String key)
    {
        return Double.valueOf(getString(key));
    }

    /**
     * Returns the value associated with the key as a string array. The value
     * should be in CSV format.
     *
     * @param key
     *          The key to look up.
     * @return The array of strings associated with the key. Throws
     *         NoSuchElementException if the key is not found.
     */
    public String[] getStringArray(String key)
    {
        return getString(key).split("\\s*,\\s*");
    }

}