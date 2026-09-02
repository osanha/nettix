package io.nettix.util;

import java.io.IOException;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.handler.codec.base64.Base64;
import org.jboss.netty.handler.codec.base64.Base64Dialect;
import org.jboss.netty.util.CharsetUtil;

/**
 * Utility class for string manipulation and conversions.
 * Provides methods for hex encoding, UUID generation, buffer handling, and more.
 *
 * @author sanha
 */
public class StringUtil
{
    /**
     * Newline character sequence.
     */
    public static final String NEWLINE = String.format("%n");

    /**
     * Hexadecimal characters.
     */
    private static final String HEXES = "0123456789abcdef";

    /**
     * Lookup table for mapping bytes to hex strings.
     */
    private static final String[] BYTE2HEX = new String[256];

    /**
     * Lookup table for hex padding in hex dumps.
     */
    private static final String[] HEXPADDING = new String[16];

    /**
     * Lookup table for byte padding in hex dumps.
     */
    private static final String[] BYTEPADDING = new String[16];

    /**
     * Lookup table for mapping bytes to printable characters.
     */
    private static final char[] BYTE2CHAR = new char[256];

    static
    {
        int i;

        // Generate the lookup table for byte-to-hex conversion
        for (i = 0; i < 10; i++)
        {
            StringBuilder buf = new StringBuilder(3);
            buf.append(" 0");
            buf.append(i);
            BYTE2HEX[i] = buf.toString();
        }
        for (; i < 16; i++)
        {
            StringBuilder buf = new StringBuilder(3);
            buf.append(" 0");
            buf.append((char) ('a' + i - 10));
            BYTE2HEX[i] = buf.toString();
        }
        for (; i < BYTE2HEX.length; i++)
        {
            StringBuilder buf = new StringBuilder(3);
            buf.append(' ');
            buf.append(Integer.toHexString(i));
            BYTE2HEX[i] = buf.toString();
        }

        // Generate lookup table for hex dump paddings
        for (i = 0; i < HEXPADDING.length; i++)
        {
            int padding = HEXPADDING.length - i;
            StringBuilder buf = new StringBuilder(padding * 3);
            for (int j = 0; j < padding; j++)
            {
                buf.append("   ");
            }
            HEXPADDING[i] = buf.toString();
        }

        // Generate lookup table for byte dump paddings
        for (i = 0; i < BYTEPADDING.length; i++)
        {
            int padding = BYTEPADDING.length - i;
            StringBuilder buf = new StringBuilder(padding);
            for (int j = 0; j < padding; j++)
            {
                buf.append(' ');
            }
            BYTEPADDING[i] = buf.toString();
        }

        // Generate lookup table for byte-to-char conversion
        for (i = 0; i < BYTE2CHAR.length; i++)
        {
            if (i <= 0x1f || i >= 0x7f)
            {
                BYTE2CHAR[i] = '.';
            }
            else
            {
                BYTE2CHAR[i] = (char) i;
            }
        }
    }

    /**
     * Converts a Reader to a String.
     *
     * @param reader the Reader to read from
     * @return the resulting String
     * @throws IOException if an I/O error occurs
     */
    public static String readerToString(Reader reader) throws IOException
    {
        StringBuilder sb = new StringBuilder();
        char[] buff = new char[1024];
        int len;

        while ((len = reader.read(buff)) > 0)
            sb.append(buff, 0, len);

        return sb.toString();
    }

    /**
     * Prints a formatted string to standard output.
     *
     * @param format the format string
     * @param args the arguments referenced by the format specifiers
     */
    public static void stdOut(String format, Object... args)
    {
        System.out.println(String.format(format, args));
    }

    /**
     * Concatenates two strings.
     *
     * @param a first string
     * @param b second string (can be any object)
     * @return concatenated string
     */
    public static String concat(String a, Object b)
    {
        StringBuilder buf = new StringBuilder(a);

        if (b != null)
            buf.append(b);

        return buf.toString();
    }

    /**
     * Concatenates a string with multiple objects.
     *
     * @param a first string
     * @param objs objects to append
     * @return concatenated string
     */
    public static String concat(String a, Object... objs)
    {
        StringBuilder buf = new StringBuilder(a);

        for (Object o : objs)
            buf.append(o);

        return buf.toString();
    }

    /**
     * Returns a ChannelBuffer as a hex string.
     *
     * @param buf the ChannelBuffer
     * @return hex representation of the buffer
     */
    public static String toHexString(ChannelBuffer buf)
    {
        buf.markReaderIndex();
        StringBuilder sb = new StringBuilder(buf.readableBytes() * 2);

        while (buf.readable())
            sb.append(BYTE2HEX[buf.readUnsignedByte()]);

        buf.resetReaderIndex();
        return sb.toString();
    }

    /**
     * Reads a null-terminated string from a ChannelBuffer.
     *
     * @param cb the ChannelBuffer
     * @return the resulting string
     */
    public static String readString(ChannelBuffer cb)
    {
        return readString(cb, (byte) 0x00);
    }

    /**
     * Reads a string from a ChannelBuffer until the given termination byte.
     *
     * @param cb the ChannelBuffer
     * @param terminate termination byte
     * @return the resulting string
     */
    public static String readString(ChannelBuffer cb, byte terminate)
    {
        int start = cb.readerIndex();

        while (cb.readable())
        {
            if (cb.readByte() == terminate)
            {
                int length = cb.readerIndex() - start - 1;

                if (length == 0)
                    return "";
                else
                    return cb.toString(start, length, CharsetUtil.UTF_8);
            }
        }

        return null;
    }

    /**
     * Returns a ChannelBuffer as a formatted hex dump.
     *
     * @param buf the buffer
     * @return formatted hex dump string
     */
    public static String toHexDump(ChannelBuffer buf)
    {
        int length = buf.readableBytes();
        int rows = length / 16 + (length % 15 == 0 ? 0 : 1) + 4;
        StringBuilder dump = new StringBuilder(rows * 80);

        dump.append(NEWLINE
                + "         +-------------------------------------------------+"
                + NEWLINE
                + "         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |"
                + NEWLINE
                + "+--------+-------------------------------------------------+----------------+");

        final int startIndex = buf.readerIndex();
        final int endIndex = buf.writerIndex();

        int i;
        for (i = startIndex; i < endIndex; i++)
        {
            int relIdx = i - startIndex;
            int relIdxMod16 = relIdx & 15;
            if (relIdxMod16 == 0)
            {
                dump.append(NEWLINE);
                dump.append(Long.toHexString(relIdx & 0xFFFFFFFFL | 0x100000000L));
                dump.setCharAt(dump.length() - 9, '|');
                dump.append('|');
            }
            dump.append(BYTE2HEX[buf.getUnsignedByte(i)]);
            if (relIdxMod16 == 15)
            {
                dump.append(" |");
                for (int j = i - 15; j <= i; j++)
                {
                    dump.append(BYTE2CHAR[buf.getUnsignedByte(j)]);
                }
                dump.append('|');
            }
        }

        if ((i - startIndex & 15) != 0)
        {
            int remainder = length & 15;
            dump.append(HEXPADDING[remainder]);
            dump.append(" |");
            for (int j = i - remainder; j < i; j++)
            {
                dump.append(BYTE2CHAR[buf.getUnsignedByte(j)]);
            }
            dump.append(BYTEPADDING[remainder]);
            dump.append('|');
        }

        dump.append(NEWLINE
                + "+--------+-------------------------------------------------+----------------+");

        return dump.toString();
    }

    /**
     * Converts a hex string to a byte array.
     *
     * @param str hex string
     * @return byte array
     */
    public static byte[] readHexString(String str)
    {
        CharBuffer cb = CharBuffer.allocate(3);
        ByteBuffer bb = ByteBuffer.allocate(str.length() / 2);

        for (int i = 0; i < str.length(); i++)
        {
            char c = str.charAt(i);
            if ((c == ' ') || (c == '\n') || (c == '\t'))
                continue;

            cb.append(c);
            if (cb.position() == 2)
            {
                cb.flip();
                String hex = cb.toString();
                int v = Integer.parseInt(hex, 16);
                bb.put((byte) v);
            }
        }

        bb.flip();
        byte[] result = new byte[bb.limit()];
        for (int i = 0; i < bb.limit(); i++)
            result[i] = bb.get();

        return result;
    }

    /**
     * Generates a random UUID encoded in URL-safe Base64.
     *
     * @param length desired byte length of the UUID
     * @return URL-safe Base64 string
     */
    public static String randomUUID(int length)
    {
        int m = length / 16;
        int n = length % 16;

        UUID id;
        ChannelBuffer bb = ChannelBuffers.buffer(length + 7);

        for (int i = 0; i < m; i++)
        {
            id = UUID.randomUUID();
            bb.writeLong(id.getMostSignificantBits());
            bb.writeLong(id.getLeastSignificantBits());
        }

        if (n > 0)
        {
            id = UUID.randomUUID();
            bb.writeLong(id.getMostSignificantBits());

            if (n > 8)
                bb.writeLong(id.getLeastSignificantBits());
        }

        bb = Base64.encode(bb, 0, length, Base64Dialect.URL_SAFE);
        int padding = length % 3;

        if (padding > 0)
            bb.writerIndex(bb.writerIndex() + padding - 3);

        return bb.toString(CharsetUtil.US_ASCII);
    }

    /**
     * Returns the caller class of the current thread.
     *
     * @return the caller class
     */
    public static Class<?> getCaller()
    {
        return Thread.currentThread().getStackTrace()[2].getClass();
    }

    /**
     * Trims a string at the first occurrence of a specific character.
     *
     * @param str the original string
     * @param ch character to trim at
     * @return trimmed string
     */
    public static String trimTail(String str, int ch)
    {
        int i = str.indexOf(ch);

        if (i > -1)
            return str.substring(0, i);
        else
            return str;
    }

    /**
     * Converts a byte array to a hex string.
     *
     * @param ptr byte array
     * @return hex string
     */
    public static String toHexString(byte[] ptr)
    {
        StringBuilder sb = new StringBuilder();

        for (byte b : ptr)
            sb.append(HEXES.charAt((b & 0xF0) >> 4)).append(HEXES.charAt((b & 0x0F)));

        return sb.toString();
    }

    /**
     * Generates a message digest as a hex string.
     *
     * @param str input string
     * @param cs charset to use
     * @param digest hashing algorithm (e.g., MD5, SHA-1)
     * @return hex-encoded digest
     * @throws NoSuchAlgorithmException if the algorithm is not available
     */
    public static String toDigestHexString(String str, Charset cs, String digest)
            throws NoSuchAlgorithmException
    {
        MessageDigest md = MessageDigest.getInstance(digest);
        md.reset();
        byte[] d = md.digest(str.getBytes(cs));
        return toHexString(d);
    }

    /**
     * Parses command-line arguments into a list and a map.
     *
     * @param args the arguments array
     * @param list list to store non-option arguments
     * @param map map to store option arguments (key/value)
     */
    public static void parseArgs(String[] args, List<String> list,
                                 Map<String, String> map)
    {
        String opt = null;

        for (String arg : args)
        {
            if (arg.startsWith("-"))
            {
                if (opt != null)
                    map.put(opt, null);

                opt = arg;
            }
            else if (opt != null)
            {
                map.put(opt, arg);
                opt = null;
            }
            else
            {
                list.add(arg);
            }
        }
    }
}