package org.jboss.netty.handler.codec.http;

/**
 * Decodes HTTP response messages and generates a custom {@code HttpResponse}.
 *
 * @author sanha
 */
public class HttpResponseDecoder
        extends HttpMessageDecoder
{

    /**
     * Creates a new instance with default values: {@code maxInitialLineLength (4096)},
     * {@code maxHeaderSize (8192)}, and {@code maxChunkSize (8192)}.
     */
    public HttpResponseDecoder()
    {
    }

    /**
     * Constructs a new {@code HttpResponseDecoder} with specified limits.
     *
     * @param maxInitialLineLength
     *          Maximum length of the initial line of the HTTP message
     * @param maxHeaderSize
     *          Maximum size of the HTTP message headers
     * @param maxChunkSize
     *          Maximum size of each HTTP chunk. Messages exceeding this size
     *          are split into multiple chunks of this size.
     */
    public HttpResponseDecoder(int maxInitialLineLength, int maxHeaderSize,
                               int maxChunkSize)
    {
        super(maxInitialLineLength, maxHeaderSize, maxChunkSize);
    }

    @Override
    protected HttpMessage createMessage(String[] initialLine)
    {
        return new io.nettix.http.HttpResponse(
                HttpVersion.valueOf(initialLine[0]),
                new HttpResponseStatus(
                        Integer.valueOf(initialLine[1]),
                        initialLine[2]));
    }

    @Override
    protected boolean isDecodingRequest()
    {
        return false;
    }
}