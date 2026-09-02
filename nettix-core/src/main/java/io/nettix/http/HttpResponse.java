package io.nettix.http;

import static io.nettix.util.Character.LF;
import static io.nettix.util.Character.SPACE;

import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;

/**
 * Represents an HTTP response message.
 *
 * @author sanha
 */
public class HttpResponse
        extends HttpMessage
        implements org.jboss.netty.handler.codec.http.HttpResponse
{
    /**
     * HTTP response status.
     */
    private HttpResponseStatus _status;

    /**
     * Constructs an HttpResponse with the specified HTTP version and status.
     *
     * @param version
     *          the HTTP version
     * @param status
     *          the HTTP response status
     */
    public HttpResponse(HttpVersion version, HttpResponseStatus status)
    {
        super(version);
        setStatus(status);
    }

    /**
     * Constructs an HttpResponse with the default HTTP version (1.1) and the specified status.
     *
     * @param status
     *          the HTTP response status
     */
    public HttpResponse(HttpResponseStatus status)
    {
        this(HttpVersion.HTTP_1_1, status);
    }

    @Override
    public HttpResponseStatus getStatus()
    {
        return _status;
    }

    @Override
    public void setStatus(HttpResponseStatus status)
    {
        if (status == null)
            throw new NullPointerException("HTTP response status cannot be null");

        _status = status;
    }

    @Override
    public String toString()
    {
        StringBuilder buf = new StringBuilder();
        HttpResponseStatus status = getStatus();
        buf.append(status.getCode());
        buf.append(SPACE);
        buf.append(status.getReasonPhrase());
        buf.append(LF);
        buf.append(super.toString());
        return buf.toString();
    }

    /**
     * Determines whether this response can have content.
     *
     * @return true if content can be set, false otherwise
     */
    public boolean canHasContent()
    {
        int code = _status.getCode();

        if ((code < 200) || (code == 204) || (code == 304))
            return false;
        else
            return true;
    }

}