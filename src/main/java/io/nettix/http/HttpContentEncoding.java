package io.nettix.http;

/**
 * Represents the encoding type for HTTP messages.
 *
 * @author sanha
 */
public enum HttpContentEncoding
{
    /**
     * Deflate compression (see <a href="http://www.ietf.org/rfc/rfc1951.txt">RFC 1951</a>).
     */
    deflate,

    /**
     * Gzip compression (see <a href="http://www.ietf.org/rfc/rfc1952.txt">RFC 1952</a>).
     */
    gzip;
}