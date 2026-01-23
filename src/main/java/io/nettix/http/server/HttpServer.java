package io.nettix.http.server;

import static io.nettix.util.Singleton.Timer;

import java.util.List;

import io.nettix.ssl.SslManager;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.local.DefaultLocalServerChannelFactory;
import org.jboss.netty.handler.codec.http.HttpServerCodec;

import io.nettix.channel.ServerChannelManager;
import io.nettix.channel.handler.ChannelReadTimeoutHandler;
import io.nettix.http.HttpContentCompressor;
import io.nettix.http.HttpContentDecompressor;
import io.nettix.http.HttpContentEncoding;
import io.nettix.http.HttpContentHandler;
import io.nettix.http.HttpLoggingHandler;

/**
 * HTTP server implementation.
 *
 * @author sanha
 */
public class HttpServer extends ServerChannelManager
{
    /**
     * Compression method for outgoing response messages.
     */
    private HttpContentEncoding _encoding;

    /**
     * Compression method to validate in incoming request messages.
     */
    private HttpContentEncoding _checkEncoding;

    /**
     * Source host name.
     */
    private String _from;

    /**
     * Destination host name.
     */
    private String _to;

    /**
     * Channel handler for setting static headers.
     */
    private ChannelHandler _headerSetter;

    /**
     * Channel read timeout in seconds.
     */
    private int _timeout = 60;

    /**
     * Channel handler for request read timeout.
     */
    private ChannelReadTimeoutHandler _timeoutHandler;

    /**
     * Maximum allowed content length (in bytes).
     */
    private int _maxContentLength = 65536;

    /**
     * Whether to aggregate HTTP chunks.
     */
    private boolean _isChunkAggregate = true;

    /**
     * Whether to keep the connection alive.
     */
    private boolean _isKeepAlive = true;

    /**
     * Whether to log attached channel attributes.
     */
    private boolean _isLogging;

    /**
     * Request message handler.
     */
    private HttpRequestHandler _handler;

    /**
     * Constructs an HTTP server.
     *
     * @param name the server name
     * @param port the port to bind
     */
    public HttpServer(String name, int port)
    {
        this(name, port, null, false);
    }

    /**
     * Constructs an HTTP server.
     *
     * @param name the server name
     * @param port the port to bind
     * @param sslCtxId SSLContext ID (must be pre-loaded via {@link SslManager#loadKeyStore})
     */
    public HttpServer(String name, int port, String sslCtxId)
    {
        this(name, port, sslCtxId, false);
    }


    /**
     * Constructs an HTTP server.
     *
     * @param name the server name
     * @param port the port to bind
     * @param sslCtxId SSLContext ID (must be pre-loaded via {@link SslManager#loadKeyStore})
     * @param useLocal whether to use a local virtual channel
     */
    public HttpServer(String name, int port, String sslCtxId, boolean useLocal)
    {
        super(name, port);

        if (useLocal)
            this.setChannelFactory(new DefaultLocalServerChannelFactory());

        if (sslCtxId != null)
            setSslEngineFactory(SslManager.createServerSslFactory(sslCtxId));
    }

    /**
     * Sets whether to keep the connection alive.
     *
     * @param keepAlive {@code true} to keep the connection alive
     */
    public void setKeepAlive(boolean keepAlive)
    {
        _isKeepAlive = keepAlive;
    }

    /**
     * Sets whether to log attached channel attributes.
     *
     * @param isLogging {@code true} to enable logging
     */
    public void setAttachementLogging(boolean isLogging)
    {
        _isLogging = isLogging;
    }

    /**
     * Sets the request read timeout in seconds.
     *
     * @param timeout timeout value in seconds (must be > 0)
     */
    public void setRequestTimeout(int timeout)
    {
        if (timeout <= 0)
            throw new IllegalArgumentException("timeout > 0");

        _timeout = timeout;
    }

    @Override
    public void setUp() throws Exception
    {
        _timeoutHandler = new ChannelReadTimeoutHandler(Timer, _timeout, false);
        super.setUp();
    }

    /**
     * Sets the maximum allowed HTTP content length.
     *
     * @param length maximum allowed content length in bytes (must be > 0)
     * @param isChunkAggregate {@code true} to aggregate HTTP chunks
     */
    public void setMaxContentLength(int length, boolean isChunkAggregate)
    {
        if (length <= 0)
            throw new IllegalArgumentException("length > 0");

        _maxContentLength = length;
        _isChunkAggregate = isChunkAggregate;
    }

    /**
     * Sets the user-defined request handler.
     *
     * @param handler the request handler
     */
    public void setHandler(HttpRequestHandler handler)
    {
        _handler = handler;
    }

    /**
     * Sets the source host name.
     *
     * @param from source host name
     * @return this instance for method chaining
     */
    public HttpServer setFrom(String from)
    {
        _from = from;
        return this;
    }

    /**
     * Sets the destination host name.
     *
     * @param to destination host name
     * @return this instance for method chaining
     */
    public HttpServer setTo(String to)
    {
        _to = to;
        return this;
    }

    /**
     * Sets the compression encoding for outgoing response entities.
     *
     * @param encoding content encoding to apply
     */
    public void setContentEncoding(HttpContentEncoding encoding)
    {
        _encoding = encoding;
    }

    /**
     * Sets the compression encoding for outgoing response entities.
     *
     * @param encoding content encoding to apply
     * @param isCheck whether to validate the encoding in incoming messages
     */
    public void setContentEncoding(HttpContentEncoding encoding, boolean isCheck)
    {
        _encoding = encoding;

        if (isCheck)
            _checkEncoding = encoding;
    }

    /**
     * Sets the content encoding to validate in incoming request entities.
     *
     * @param encoding encoding type to validate
     */
    public void setCheckContentEncoding(HttpContentEncoding encoding)
    {
        _checkEncoding = encoding;
    }

    @Override
    public ChannelPipeline getPipeline() throws Exception
    {
        ChannelPipeline cp = super.getPipeline();

        cp.addLast("HTTP_SERVER", new HttpServerCodec());

        if (_encoding != null)
            cp.addLast("COMPRESSOR", new HttpContentCompressor(_encoding));

        cp.addLast("CONTENT_HANDLER", new HttpContentHandler(_maxContentLength, _isChunkAggregate));
        cp.addLast("REQUEST_TIMEOUT", _timeoutHandler);

        if (_checkEncoding != null)
            cp.addLast("DECOMPRESSOR", new HttpContentDecompressor(_checkEncoding));
        else
            cp.addLast("DECOMPRESSOR", new HttpContentDecompressor());

        if (_headerSetter != null)
            cp.addLast("STATIC_HEADER", _headerSetter);

        cp.addLast("HTTP_LOGGER",
                new HttpLoggingHandler(name())
                        .setFrom(_from)
                        .setTo(_to)
                        .setAttachmentLogging(_isLogging));

        cp.addLast("HTTP_SERVER_HANDLER",
                new HttpServerHandler(_handler, _timeoutHandler, _isKeepAlive));

        return cp;
    }
}