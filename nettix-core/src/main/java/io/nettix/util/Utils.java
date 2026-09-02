package io.nettix.util;

import static io.nettix.util.Character.LF;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;
import java.util.zip.DataFormatException;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterOutputStream;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBufferOutputStream;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class providing various helper methods.
 *
 * @author sanha
 */
public class Utils
{
    /**
     * Logger instance
     */
    private static final Logger _logger = LoggerFactory.getLogger(Utils.class);

    /**
     * HTTP Date header format
     */
    private static final SimpleDateFormat DATE_FORMAT;

    static
    {
        DATE_FORMAT = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss", Locale.US);
        DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    /**
     * Result type for line parsing
     *
     * @author sanha
     */
    public static class LineResult
    {
        /**
         * Parsed string
         */
        public String str;

        /**
         * Next index
         */
        public int next;
    }

    /**
     * Inflates a deflated buffer.
     *
     * @param input
     *            Source channel buffer
     * @return Inflated buffer
     * @throws IOException
     * @throws DataFormatException
     */
    public static ChannelBuffer inflate(ChannelBuffer input) throws IOException, DataFormatException
    {
        ChannelBuffer output = ChannelBuffers.dynamicBuffer();
        int inputLen = input.readableBytes();

        InflaterOutputStream os = new InflaterOutputStream(new ChannelBufferOutputStream(output));
        input.readBytes(os, inputLen);
        os.close();

        _logger.info("Inflation report (before/after): {}/{}", inputLen, output.readableBytes());

        return output;
    }

    /**
     * Logs the current stack trace with a custom message.
     *
     * @param msg
     *            Custom message
     */
    public static void printStackTrace(String msg)
    {
        StringBuilder buf = new StringBuilder(msg);

        for (StackTraceElement e : Thread.currentThread().getStackTrace())
        {
            buf.append(e.getClassName());
            buf.append('.');
            buf.append(e.getMethodName());
            buf.append(':');
            buf.append(e.getLineNumber());
            buf.append('\n');
        }

        _logger.info(buf.toString());
    }

    /**
     * Returns the remote address of a channel.
     *
     * @param ch
     *            Channel
     * @return Remote IP address
     */
    public static String getRemoteAddress(Channel ch)
    {
        return ((InetSocketAddress) ch.getRemoteAddress()).getAddress().getHostAddress();
    }

    /**
     * Returns the set of local system IP addresses.
     *
     * @return Set of local IP addresses
     * @throws SocketException
     */
    public static Set<String> getLocalAddresses() throws SocketException
    {
        Set<String> ipSet = new HashSet<String>();

        try
        {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

            while (interfaces.hasMoreElements())
            {
                NetworkInterface iface = interfaces.nextElement();

                if (iface.isLoopback() || !iface.isUp())
                    continue;

                Enumeration<InetAddress> addresses = iface.getInetAddresses();

                while (addresses.hasMoreElements())
                {
                    InetAddress addr = addresses.nextElement();

                    if (addr.isLinkLocalAddress())
                        continue;

                    ipSet.add(addr.getHostAddress());
                }
            }
        }
        catch (SocketException e)
        {
            _logger.error("Failed to retrieve local addresses", e);
        }

        return ipSet;
    }

    /**
     * Deflates a buffer.
     *
     * @param input
     *            Source channel buffer
     * @return Deflated buffer
     * @throws IOException
     */
    public static ChannelBuffer deflate(ChannelBuffer input) throws IOException
    {
        ChannelBuffer output = ChannelBuffers.dynamicBuffer();
        int inputLen = input.readableBytes();

        DeflaterOutputStream os = new DeflaterOutputStream(new ChannelBufferOutputStream(output));
        input.readBytes(os, inputLen);
        os.close();

        _logger.info("Deflation report (before/after): {}/{}", inputLen, output.readableBytes());

        return output;
    }

    /**
     * Splits a string by a given delimiter character.
     *
     * @param src
     *            Source string
     * @param c
     *            Delimiter character
     * @return Array of split strings
     */
    public static String[] split(String src, char c)
    {
        List<String> list = new ArrayList<String>();
        int begin = 0;

        for (int i = 0; i < src.length(); i++)
        {
            if (src.charAt(i) == c)
            {
                list.add(src.substring(begin, i));
                begin = i + 1;
            }
        }
        list.add(src.substring(begin));

        return list.toArray(new String[0]);
    }

    /**
     * Reads a line from a channel buffer.
     *
     * @param buf
     *            Channel buffer
     * @return Line string
     */
    public static String getByteStringLine(ChannelBuffer buf)
    {
        StringBuilder sb = new StringBuilder();

        while (buf.readable())
        {
            char c = (char) buf.readByte();

            if (c == LF)
                break;

            sb.append(c);
        }

        return sb.toString();
    }

    /**
     * Reads a line from a string starting at a specific index.
     *
     * @param str
     *            Source string
     * @param index
     *            Start index
     * @return LineResult object containing the line and next index
     */
    public static LineResult getLine(String str, int index)
    {
        if (index >= str.length())
            return null;

        int i = str.indexOf('\n', index);
        LineResult result = new LineResult();

        if (i == -1)
        {
            result.str = str.substring(index);
            result.next = -1;
        }
        else
        {
            result.str = str.substring(index, i);
            result.next = i + 1;
        }

        return result;
    }

    /**
     * Reads a line from a string starting at index 0.
     *
     * @param str
     *            Source string
     * @return LineResult object containing the line and next index
     */
    public static LineResult getLine(String str)
    {
        return getLine(str, 0);
    }

    /**
     * Returns the current UTC time as a string.
     *
     * @return UTC time string, e.g., Tue, 01 Mar 2011 02:54:50 GMT
     */
    public static String getUtcTime()
    {
        return getUtcTime(new Date());
    }

    /**
     * Returns a UTC time string for a given date.
     *
     * @param date
     *            Date to convert
     * @return UTC time string, e.g., Tue, 01 Mar 2011 02:54:50 GMT
     */
    public static String getUtcTime(Date date)
    {
        return DATE_FORMAT.format(date) + " GMT";
    }
}