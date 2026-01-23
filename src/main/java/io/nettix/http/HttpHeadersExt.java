package io.nettix.http;

import org.jboss.netty.handler.codec.http.HttpHeaders;

import io.nettix.util.StringUtil;

/**
 * Extended HTTP headers utility class.
 * Provides convenience methods to access and manipulate HTTP headers.
 *
 * @author sanha
 */
public class HttpHeadersExt
{
    /**
     * Checks if the Connection header is set to "close".
     *
     * @param msg HTTP message
     * @return true if the Connection header is "close", false otherwise
     */
    public static boolean isConnectionClose(HttpMessage msg)
    {
        String connection = msg.headers().get(HttpHeaders.Names.CONNECTION);
        return HttpHeaders.Values.CLOSE.equals(connection);
    }

    /**
     * Returns the value of the Content-Type header.
     *
     * @param msg HTTP message
     * @return Content-Type header value
     */
    public static String getContentType(HttpMessage msg)
    {
        return msg.getHeader(HttpHeaders.Names.CONTENT_TYPE);
    }

    /**
     * Returns the value of the From header.
     *
     * @param msg HTTP message
     * @return From header value
     */
    public static String getFrom(HttpMessage msg)
    {
        return msg.getHeader(HttpHeaders.Names.FROM);
    }

    /**
     * Sets the value of the From header.
     *
     * @param msg HTTP message
     * @param from From header value
     */
    public static void setFrom(HttpMessage msg, String from)
    {
        msg.setHeader(HttpHeaders.Names.FROM, from);
    }

    /**
     * Returns the value of the Date header.
     *
     * @param msg HTTP message
     * @return Date header value
     */
    public static String getDate(HttpMessage msg)
    {
        return msg.getHeader(HttpHeaders.Names.DATE);
    }

    /**
     * Returns the value of the Via header.
     *
     * @param msg HTTP message
     * @return Via header value
     */
    public static String getVia(HttpMessage msg)
    {
        return msg.getHeader(HttpHeaders.Names.VIA);
    }

    /**
     * Checks if the Date header is present.
     *
     * @param msg HTTP message
     * @return true if the Date header exists, false otherwise
     */
    public static boolean hasDate(HttpMessage msg)
    {
        return msg.headers().contains(HttpHeaders.Names.DATE);
    }

    /**
     * Removes the Via header.
     *
     * @param msg HTTP message
     */
    public static void removeVia(HttpMessage msg)
    {
        msg.removeHeader(HttpHeaders.Names.VIA);
    }

    /**
     * Sets the value of the Date header.
     *
     * @param msg HTTP message
     * @param date Date header value
     */
    public static void setDate(HttpMessage msg, String date)
    {
        msg.setHeader(HttpHeaders.Names.DATE, date);
    }

    /**
     * Sets the Connection header to "close".
     *
     * @param msg HTTP message
     */
    public static void setConnectionClose(HttpMessage msg)
    {
        msg.setHeader(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.CLOSE);
    }

    /**
     * Returns the media type from the Content-Type header, excluding charset if present.
     *
     * @param msg HTTP message
     * @return media type value or null if Content-Type is not set
     */
    public static String getMediaType(HttpMessage msg)
    {
        String contentType = getContentType(msg);

        if (contentType == null)
            return null;

        return StringUtil.trimTail(contentType, ';');
    }

    /**
     * Removes the Host header.
     *
     * @param msg HTTP message
     */
    public static void removeHost(HttpMessage msg)
    {
        msg.removeHeader(HttpHeaders.Names.HOST);
    }

    /**
     * Sets the value of the Host header.
     *
     * @param msg HTTP message
     * @param host Host header value
     */
    public static void setHost(HttpMessage msg, String host)
    {
        msg.setHeader(HttpHeaders.Names.HOST, host);
    }

    /**
     * Sets the value of the Content-Type header.
     *
     * @param msg HTTP message
     * @param type Content-Type value
     */
    public static void setContentType(HttpMessage msg, String type)
    {
        msg.setHeader(HttpHeaders.Names.CONTENT_TYPE, type);
    }

    /**
     * Returns the value of the Content-Encoding header.
     *
     * @param msg HTTP message
     * @return Content-Encoding header value
     */
    public static String getContentEncoding(HttpMessage msg)
    {
        return msg.getHeader(HttpHeaders.Names.CONTENT_ENCODING);
    }

    /**
     * Removes the Authorization header and returns its previous value.
     *
     * @param msg HTTP message
     * @return previous Authorization header value, or null if not set
     */
    public static String removeAuthorization(HttpMessage msg)
    {
        String auth = msg.getHeader(HttpHeaders.Names.AUTHORIZATION);

        if (auth != null)
            msg.removeHeader(HttpHeaders.Names.AUTHORIZATION);

        return auth;
    }

    /**
     * Checks if the Content-Type is "application/octet-stream".
     *
     * @param msg HTTP message
     * @return true if Content-Type is "application/octet-stream", false otherwise
     */
    public static boolean isOctetStream(HttpMessage msg)
    {
        String contentType = getContentType(msg);
        return HttpContentType.APPLICATION_OCTET_STREAM.equalsIgnoreCase(contentType);
    }

    /**
     * Sets the Accept header value.
     *
     * @param msg HTTP message
     * @param type Accept header value
     */
    public static void setAccept(HttpMessage msg, String type)
    {
        msg.setHeader(HttpHeaders.Names.ACCEPT, type);
    }

    /**
     * Removes the From header.
     *
     * @param msg HTTP message
     */
    public static void removeFrom(HttpMessage msg)
    {
        msg.removeHeader(HttpHeaders.Names.FROM);
    }

    /**
     * Removes the Connection header.
     *
     * @param msg HTTP message
     */
    public static void removeConnectionClose(HttpMessage msg)
    {
        msg.removeHeader(HttpHeaders.Names.CONNECTION);
    }

    /**
     * Sets the content length.
     * Note: Not using Netty's HttpHeaders.setContentLength to ensure changes are reflected when calling toStringHeaders().
     *
     * @param msg HTTP message
     * @param length content length
     */
    public static void setContentLength(HttpMessage msg, long length)
    {
        msg.setHeader(HttpHeaders.Names.CONTENT_LENGTH, length);
    }
}