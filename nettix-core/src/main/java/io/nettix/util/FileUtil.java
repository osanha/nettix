package io.nettix.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

/**
 * Utility class for file operations.
 *
 * @author sanha
 */
public class FileUtil
{
    /**
     * Property key for the current working directory.
     */
    private static final String WORK_DIR = "user.dir";

    /**
     * Reads the contents of a file as a string using the default charset.
     *
     * @param path
     *          the file path
     * @return the file content as a string
     * @throws IOException
     */
    public static String readAsString(String path) throws IOException
    {
        return readAsString(path, Charset.defaultCharset());
    }

    /**
     * Reads the contents of a file as a string with the specified charset.
     *
     * @param file
     *          the file to read
     * @param cs
     *          the character set to use
     * @return the file content as a string
     * @throws IOException
     */
    public static String readAsString(File file, Charset cs) throws IOException
    {
        Reader reader = getReader(file, cs);
        return StringUtil.readerToString(reader);
    }

    /**
     * Reads the contents of a file as a string with the specified charset.
     *
     * @param path
     *          the file path
     * @param cs
     *          the character set to use
     * @return the file content as a string
     * @throws IOException
     */
    public static String readAsString(String path, Charset cs) throws IOException
    {
        Reader reader = getReader(path, cs);
        return StringUtil.readerToString(reader);
    }

    /**
     * Creates a {@link Reader} for the specified file path with the given charset.
     *
     * @param path
     *          the file path
     * @param cs
     *          the character set to use
     * @return a {@link Reader} for the file
     * @throws FileNotFoundException
     *           if the file does not exist
     */
    public static Reader getReader(String path, Charset cs)
            throws FileNotFoundException
    {
        return getReader(new File(path), cs);
    }

    /**
     * Creates a {@link Reader} for the specified file with the given charset.
     *
     * @param file
     *          the file to read
     * @param cs
     *          the character set to use
     * @return a {@link Reader} for the file
     * @throws FileNotFoundException
     *           if the file does not exist
     */
    public static Reader getReader(File file, Charset cs)
            throws FileNotFoundException
    {
        FileInputStream is = new FileInputStream(file);
        return new InputStreamReader(is, cs);
    }

    /**
     * Returns the current working directory.
     *
     * @return the current working directory
     */
    public static String getWorkingDir()
    {
        return System.getProperty(WORK_DIR);
    }

    /**
     * Sets the current working directory.
     *
     * @param dir
     *          the directory to set as the working directory
     */
    public static void setWorkingDir(String dir)
    {
        System.setProperty(WORK_DIR, dir);
    }

    /**
     * Checks whether a file exists.
     *
     * @param path
     *          the file path
     * @return {@code true} if the file exists, {@code false} otherwise
     */
    public static boolean isExist(String path)
    {
        return new File(path).exists();
    }

    /**
     * Returns the path of the JAR file containing the specified class.
     *
     * @param clazz
     *          the class
     * @return the JAR file path
     */
    public static String getJarPath(Class<?> clazz)
    {
        return clazz.getProtectionDomain().getCodeSource().getLocation().getPath();
    }

    /**
     * Returns the version of the JAR file containing the specified object.
     * Format: libraryName-x.y.z.jar
     *
     * @param obj
     *          the object
     * @return the version string
     */
    public static String getJarVersion(Object obj)
    {
        return getJarVersion(obj.getClass());
    }

    /**
     * Returns the version of the JAR file containing the specified class.
     * Format: libraryName-x.y.z.jar
     *
     * @param clazz
     *          the class
     * @return the version string
     */
    public static String getJarVersion(Class<?> clazz)
    {
        String path = getJarPath(clazz);
        int begin = path.lastIndexOf('-') + 1;
        int end = path.lastIndexOf('.');

        if ((begin > 0) && (end > begin))
            return path.substring(begin, end);
        else
            return "unknown";
    }

}