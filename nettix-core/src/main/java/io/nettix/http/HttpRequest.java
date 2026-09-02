package io.nettix.http;

import static io.nettix.util.Character.LF;
import static io.nettix.util.Character.SPACE;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.jboss.netty.handler.codec.http.QueryStringDecoder;

/**
 * Represents an HTTP request message.
 *
 * @author sanha
 */
public class HttpRequest
        extends HttpMessage
        implements org.jboss.netty.handler.codec.http.HttpRequest
{
    /**
     * HTTP method of the request.
     */
    private HttpMethod _method;

    /**
     * Request URI.
     */
    private String _uri;

    /**
     * Decoder for query string parameters.
     */
    private QueryStringDecoder _uriDecoder;

    /**
     * String representation of the parameters.
     */
    private String _strParameters;

    /**
     * Constructs an HTTP request with a specific version, method, and URI.
     *
     * @param version
     *          HTTP version
     * @param method
     *          HTTP method
     * @param uri
     *          Request URI
     */
    public HttpRequest(HttpVersion version, HttpMethod method, String uri)
    {
        super(version);
        setMethod(method);
        setUri(uri);
    }

    @Override
    public String toString()
    {
        StringBuilder buf = new StringBuilder();
        buf.append(getMethod());
        buf.append(SPACE);
        buf.append(getPath());
        buf.append(LF);
        buf.append(super.toString());
        return buf.toString();
    }

    /**
     * Constructs an HTTP request with HTTP 1.1 version, a given method, and URI.
     *
     * @param method
     *          HTTP method
     * @param uri
     *          Request URI
     */
    public HttpRequest(HttpMethod method, String uri)
    {
        this(HttpVersion.HTTP_1_1, method, uri);
    }

    @Override
    public HttpMethod getMethod()
    {
        return _method;
    }

    @Override
    public void setMethod(HttpMethod method)
    {
        _method = method;
    }

    @Override
    public String getUri()
    {
        return _uri;
    }

    /**
     * Returns the path component of the URI.
     *
     * @return the path string
     */
    public String getPath()
    {
        return _uriDecoder.getPath();
    }

    /**
     * Returns the query parameters as a map.
     *
     * @return Map of query parameters
     */
    public Map<String, List<String>> getParameters()
    {
        return _uriDecoder.getParameters();
    }

    /**
     * Returns the query parameters as a formatted string.
     *
     * @return String representation of query parameters
     */
    public String toStringParameters()
    {
        Map<String, List<String>> parameters = _uriDecoder.getParameters();

        if (_strParameters == null)
        {
            if (parameters.isEmpty())
            {
                _strParameters = "";
            }
            else
            {
                StringBuilder buf = new StringBuilder();

                for (Entry<String, List<String>> param : parameters.entrySet())
                {
                    buf.append(param.getKey());
                    buf.append(" = ");
                    List<String> v = param.getValue();

                    if (v.size() == 1)
                        buf.append(v.get(0));
                    else
                        buf.append(v.toString());

                    buf.append(LF);
                }

                _strParameters = buf.toString();
            }
        }

        return _strParameters;
    }

    @Override
    public void setUri(String uri)
    {
        if (uri == null)
            throw new NullPointerException("uri");

        _uri = uri;
        _uriDecoder = new QueryStringDecoder(uri);
        _strParameters = null;
    }

    /**
     * Checks whether the request can have a content body.
     *
     * @return true if the HTTP method allows content, false otherwise
     */
    public boolean canHasContent()
    {
        return _method.equals(HttpMethod.POST) || _method.equals(HttpMethod.PUT);
    }

}