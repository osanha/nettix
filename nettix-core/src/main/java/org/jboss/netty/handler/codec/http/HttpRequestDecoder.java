package org.jboss.netty.handler.codec.http;

/**
 * Decoder for HTTP request messages. Constructs custom {@link HttpRequest} instances.
 *
 * @author sanha
 */
public class HttpRequestDecoder
        extends HttpMessageDecoder
{

    /**
     * Creates a new instance with the default {@code maxInitialLineLength (4096)},
     * {@code maxHeaderSize (8192)}, and {@code maxChunkSize (8192)}.
     */
    public HttpRequestDecoder()
    {
    }

    /**
     * Constructs a new instance with the specified limits.
     *
     * @param maxInitialLineLength
     *          The maximum length of the initial line of an HTTP message.
     * @param maxHeaderSize
     *          The maximum size of HTTP headers.
     * @param maxChunkSize
     *          The maximum size of an HTTP chunk. Messages exceeding this size are
     *          split into multiple chunks of this maximum size.
     */
    public HttpRequestDecoder(int maxInitialLineLength, int maxHeaderSize,
                              int maxChunkSize)
    {
        super(maxInitialLineLength, maxHeaderSize, maxChunkSize);
    }

    @Override
    protected HttpMessage createMessage(String[] initialLine) throws Exception
    {
        return new io.nettix.http.HttpRequest(HttpVersion.valueOf(initialLine[2]),
                HttpMethod.valueOf(initialLine[0]),
                initialLine[1]);
    }

    @Override
    protected boolean isDecodingRequest()
    {
        return true;
    }
}