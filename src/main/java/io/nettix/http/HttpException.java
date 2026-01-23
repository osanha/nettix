package io.nettix.http;

import static io.nettix.util.StringUtil.NEWLINE;

import org.jboss.netty.handler.codec.http.HttpResponseStatus;

/**
 * Exception class that holds an HttpResponseStatus.
 *
 * @author sanha
 */
@SuppressWarnings("serial")
public class HttpException
        extends Exception
{
    /**
     * The HTTP response status.
     */
    private final HttpResponseStatus _status;

    /**
     * Optional detailed message.
     */
    private final String _detailMessage;

    /**
     * Constructor.
     *
     * @param status
     *          The HTTP response status.
     */
    public HttpException(HttpResponseStatus status)
    {
        this(status, null);
    }

    /**
     * Constructor.
     *
     * @param status
     *          The HTTP response status.
     * @param message
     *          Additional detail message.
     */
    public HttpException(HttpResponseStatus status, String message)
    {
        super((message == null) ? status.getReasonPhrase()
                : status.getReasonPhrase() + NEWLINE + message);
        _status = status;
        _detailMessage = message;
    }

    /**
     * Returns the HTTP response status.
     *
     * @return The HTTP response status.
     */
    public HttpResponseStatus status()
    {
        return _status;
    }

    /**
     * Returns the additional detail message.
     *
     * @return The detail message.
     */
    public String detailMessage()
    {
        return _detailMessage;
    }

    @Override
    public String toString()
    {
        return _status.toString();
    }
}