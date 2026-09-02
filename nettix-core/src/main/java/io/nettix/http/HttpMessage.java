package io.nettix.http;

import static io.nettix.http.HttpContentType.APPLICATION_OCTET_STREAM;
import static io.nettix.util.Character.LF;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.handler.codec.http.DefaultHttpMessage;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.jboss.netty.util.CharsetUtil;

/**
 * Base class for HTTP request/response messages.
 * Provides common functionality for headers and content.
 *
 * @author sanha
 */
public class HttpMessage
        extends DefaultHttpMessage
{
    /**
     * String representation of headers.
     */
    private String _strHeaders;

    /**
     * String representation of content.
     */
    private String _strContent;

    /**
     * Headers for logging purposes only.
     */
    private Map<String, Object> _strOnlyHeaders;

    /**
     * Default constructor.
     */
    protected HttpMessage()
    {
        super(HttpVersion.HTTP_1_1);
    }

    @Override
    public String toString()
    {
        return toStringHeaders();
    }

    /**
     * Constructor with HTTP version.
     *
     * @param version HTTP version
     */
    protected HttpMessage(HttpVersion version)
    {
        super(version);
    }

    /**
     * Sets the content as a UTF-8 string.
     *
     * @param content the content string
     * @param type the Content-Type header value
     */
    public void setContent(String content, String type)
    {
        setContent(content, CharsetUtil.UTF_8, type);
    }

    /**
     * Sets the content as a string with a specific charset.
     *
     * @param content the content string
     * @param cs the character set
     * @param type the Content-Type header value
     */
    public void setContent(String content, Charset cs, String type)
    {
        setContent(ChannelBuffers.copiedBuffer(content, cs), type);
    }

    /**
     * Sets the content with a ChannelBuffer.
     *
     * @param content the content buffer
     * @param type the Content-Type header value
     */
    public void setContent(ChannelBuffer content, String type)
    {
        setContent(content);
        HttpHeadersExt.setContentType(this, type);
    }

    @Override
    public void setContent(ChannelBuffer content)
    {
        super.setContent(content);
        _strContent = null;
    }

    @Override
    public void setChunked(boolean chunked)
    {
        if (getContent().readable())
            throw new IllegalArgumentException("Content already exists");

        super.setChunked(chunked);
    }

    /**
     * Returns the content as a string.
     *
     * @return content as string
     */
    public String toStringContent()
    {
        if (_strContent == null)
        {
            ChannelBuffer cb = this.getContent();

            if (cb.readable())
            {
                String type = HttpHeadersExt.getContentType(this);

                if (type == null)
                    _strContent = "(Content-Type is missing, cannot represent as string)";
                else if (type.equalsIgnoreCase(APPLICATION_OCTET_STREAM))
                    _strContent = "(Content-Type is octet-stream, cannot represent as string)";
                else
                    _strContent = cb.toString(CharsetUtil.UTF_8);
            }
            else
            {
                _strContent = "";
            }
        }

        return _strContent;
    }

    /**
     * Returns headers as a string.
     *
     * @return headers as string
     */
    public String toStringHeaders()
    {
        if (_strHeaders == null)
        {
            StringBuilder buf = new StringBuilder();

            for (Entry<String, String> header : headers())
            {
                String name = header.getKey();
                Object value = header.getValue();

                if (_strOnlyHeaders != null)
                {
                    Object newValue = _strOnlyHeaders.get(name);

                    if (newValue != null)
                        value = newValue;
                }

                buf.append(name);
                buf.append(": ");
                buf.append(value);
                buf.append(LF);
            }

            _strHeaders = buf.toString();
        }

        return _strHeaders;
    }

    /**
     * Copies headers from another message.
     *
     * @param msg source message
     */
    public void setHeaders(HttpMessage msg)
    {
        _strHeaders = null;

        for (Map.Entry<String, String> e : msg.headers())
            headers().add(e.getKey(), e.getValue());
    }

    /**
     * Adds a header only for logging purposes.
     *
     * @param name header name
     * @param value header value
     */
    public void setStrOnlyHeader(final String name, final Object value)
    {
        if (_strOnlyHeaders == null)
            _strOnlyHeaders = new HashMap<String, Object>();

        _strOnlyHeaders.put(name, value);
        _strHeaders = null;
    }

    /**
     * Sets a header.
     *
     * @param name header name
     * @param value header value
     */
    public void setHeader(final String name, final Object value)
    {
        _strHeaders = null;
        headers().set(name, value);
    }

    /**
     * Returns the value of a header.
     *
     * @param name header name
     * @return header value
     */
    public String getHeader(final String name)
    {
        return headers().get(name);
    }

    /**
     * Removes a header.
     *
     * @param name header name
     */
    public void removeHeader(final String name)
    {
        _strHeaders = null;
        headers().remove(name);
    }

}