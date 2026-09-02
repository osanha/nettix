package io.nettix.http.client;

import static io.nettix.util.Character.COLON;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import io.nettix.ssl.SslManager;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.local.DefaultLocalClientChannelFactory;
import org.jboss.netty.channel.local.LocalAddress;
import org.jboss.netty.handler.codec.http.HttpClientCodec;

import io.nettix.channel.CallableChannelFuture;
import io.nettix.channel.ClientChannelManager;
import io.nettix.http.HttpContentCompressor;
import io.nettix.http.HttpContentDecompressor;
import io.nettix.http.HttpContentEncoding;
import io.nettix.http.HttpContentHandler;
import io.nettix.http.HttpHeadersExt;
import io.nettix.http.HttpLoggingHandler;
import io.nettix.http.HttpRequest;
import io.nettix.http.HttpResponse;

/**
 * HTTP client for sending asynchronous requests and receiving responses.
 *
 * @author sanha
 */
public class HttpClient extends ClientChannelManager
{
    /**
     * Compression encoding for the request entity.
     */
    private HttpContentEncoding _encoding;

    /**
     * Target address to connect to.
     */
    private final SocketAddress _addr;

    /**
     * Whether to enable logging of channel attachments.
     */
    private boolean _isLogging;

    /**
     * Value of the Host header.
     */
    private final String _host;

    /**
     * Sending host name.
     */
    private String _from;

    /**
     * Receiving host name.
     */
    private String _to;

    /**
     * Response timeout in seconds.
     */
    private int _resTimeout = 60;

    /**
     * Maximum allowed total content length (in bytes).
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
     * Compression encoding to check in received messages.
     */
    private HttpContentEncoding _checkEncoding;

    /**
     * Connection pool for managing persistent channels.
     */
    private final Queue<Channel> _connPool = new ConcurrentLinkedQueue<Channel>();

    /**
     * Handler to remove closed channels from the pool.
     */
    private final ChannelFutureListener _poolRemover = new ChannelFutureListener()
    {
        @Override
        public void operationComplete(ChannelFuture future) throws Exception
        {
            _connPool.remove(future.getChannel());
        }
    };

    /**
     * Constructor for logical (non-physical) channel connections.
     *
     * @param name Client name
     * @param port Target port
     */
    public HttpClient(String name, int port)
    {
        this(name, null, port, null);
    }

    /**
     * Constructor.
     *
     * @param name Client name
     * @param addr Target address
     * @param port Target port
     */
    public HttpClient(String name, String addr, int port)
    {
        this(name, addr, port, null);
    }

    /**
     * Constructor.
     *
     * @param name Client name
     * @param addr Target address
     * @param port Target port
     * @param sslCtxId SSLContext ID (must be pre-loaded via {@link SslManager#loadKeyStore})
     */
    public HttpClient(String name, String addr, int port, String sslCtxId)
    {
        super(name);

        if (addr != null)
        {
            _addr = new InetSocketAddress(addr, port);

            // Some servers may return an error if the Host header includes the port even when using the default port.
            if ((port == 80) || (port == 443))
                _host = addr;
            else
                _host = addr + COLON + port;
        }
        else
        {
            _addr = new LocalAddress(port);
            _host = "local:" + port;
            setChannelFactory(new DefaultLocalClientChannelFactory());
        }

        if (sslCtxId != null)
            setSslEngineFactory(SslManager.createClientSslFactory(sslCtxId, addr, port));
    }

    /**
     * Returns the host address.
     *
     * @return The address
     */
    public SocketAddress getAddress()
    {
        return _addr;
    }

    /**
     * Sets whether to keep the connection alive.
     *
     * @param keepAlive True to keep the connection alive
     */
    public void setKeepAlive(boolean keepAlive)
    {
        _isKeepAlive = keepAlive;
    }

    /**
     * Sets the maximum allowed HTTP content length.
     *
     * @param length Maximum total content size (in bytes)
     * @param isChunkAggregate True to aggregate HTTP chunks
     */
    public void setMaxContentLength(int length, boolean isChunkAggregate)
    {
        if (length <= 0)
            throw new IllegalArgumentException("length > 0");

        _maxContentLength = length;
        _isChunkAggregate = isChunkAggregate;
    }

    /**
     * Enables or disables logging of channel attachments.
     *
     * @param isLogging True to enable logging
     */
    public void setAttachementLogging(boolean isLogging)
    {
        _isLogging = isLogging;
    }

    /**
     * Sets the sending host name.
     *
     * @param from Host name
     * @return This instance for method chaining
     */
    public HttpClient setFrom(String from)
    {
        _from = from;
        return this;
    }

    /**
     * Sets the destination host name.
     *
     * @param to Host name
     * @return This instance for method chaining
     */
    public HttpClient setTo(String to)
    {
        _to = to;
        return this;
    }

    /**
     * Sets the entity compression encoding.
     *
     * @param encoding Compression encoding
     */
    public void setContentEncoding(HttpContentEncoding encoding)
    {
        _encoding = encoding;
    }

    /**
     * Sets the maximum waiting time for a response.
     *
     * @param timeout Timeout in seconds
     */
    public void setResponseTimeout(int timeout)
    {
        if (timeout <= 0)
            throw new IllegalArgumentException("timeout > 0");

        _resTimeout = timeout;
    }

    /**
     * Sends an HTTP request asynchronously.
     *
     * @param req HTTP request
     * @param handler Response handler
     * @return Future result
     */
    public CallableChannelFuture<HttpResponse> execute(HttpRequest req,
                                                       HttpResponseHandler handler)
    {
        return execute(req, handler, _resTimeout);
    }

    /**
     * Sets the content encoding type to check in received messages.
     *
     * @param encoding Compression encoding type
     */
    public void setCheckContentEncoding(HttpContentEncoding encoding)
    {
        _checkEncoding = encoding;
    }

    /**
     * Sends an HTTP request asynchronously.
     *
     * @param req HTTP request
     * @return Future result
     */
    public CallableChannelFuture<HttpResponse> execute(HttpRequest req)
    {
        return execute(req, null, _resTimeout);
    }

    /**
     * Sends an HTTP request asynchronously.
     *
     * @param req HTTP request
     * @param timeout Response timeout in seconds
     * @return Future result
     */
    public CallableChannelFuture<HttpResponse> execute(HttpRequest req,
                                                       int timeout)
    {
        return execute(req, null, timeout);
    }

    @Override
    public ChannelFuture connect(SocketAddress addr)
    {
        if (_isKeepAlive)
        {
            Channel ch = _connPool.poll();

            if ((ch != null) && ch.isConnected())
                return Channels.succeededFuture(ch);
        }

        return super.connect(addr);
    }

    /**
     * Sends an HTTP request asynchronously.
     *
     * @param req HTTP request
     * @param handler Response handler
     * @param timeout Response timeout in seconds
     * @return Future result
     */
    public CallableChannelFuture<HttpResponse> execute(final HttpRequest req,
                                                       final HttpResponseHandler handler,
                                                       final int timeout)
    {
        ChannelFuture connFuture = connect(_addr);
        final Channel ch = connFuture.getChannel();
        final CallableChannelFuture<HttpResponse> finalFuture = new CallableChannelFuture<HttpResponse>(ch);
        connFuture.addListener(new ChannelFutureListener()
        {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception
            {
                if (future.isSuccess())
                {
                    HttpClientHandler hch = ch.getPipeline().get(HttpClientHandler.class);

                    // A new connection has been established.
                    if (hch == null)
                    {
                        hch = new HttpClientHandler(_connPool, _isKeepAlive);
                        ch.getPipeline().addLast("HTTP_CLIENT_HANDLER", hch);

                        if (_isKeepAlive)
                            ch.getCloseFuture().addListener(_poolRemover);

                        if (handler != null)
                            handler.connected(ch);
                    }

                    HttpHeadersExt.setHost(req, _host);

                    // Always set the content length, even if length is 0.
                    if (!req.isChunked() && req.canHasContent())
                        HttpHeadersExt.setContentLength(req, req.getContent().readableBytes());

                    if (_isKeepAlive)
                        HttpHeadersExt.removeConnectionClose(req);
                    else
                        HttpHeadersExt.setConnectionClose(req);

                    if (handler != null)
                        handler.beforeRequest(ch);

                    hch.execute(ch, req, handler, timeout, finalFuture);
                }
                else
                {
                    if (handler != null)
                        handler.exceptionCaught(ch, future.getCause());

                    finalFuture.setFailure(future.getCause());
                }
            }
        });

        return finalFuture;
    }

    @Override
    public ChannelPipeline getPipeline() throws Exception
    {
        ChannelPipeline cp = super.getPipeline();

        cp.addLast("HTTP_CLIENT", new HttpClientCodec());
        cp.addLast("CONTENT_LENGTH", new HttpContentHandler(_maxContentLength,
                _isChunkAggregate));
        if (_checkEncoding != null)
            cp.addLast("DECOMPRESSOR", new HttpContentDecompressor(_checkEncoding));
        else
            cp.addLast("DECOMPRESSOR", new HttpContentDecompressor());

        if (_encoding != null)
            cp.addLast("COMPRESSOR", new HttpContentCompressor(_encoding));

        cp.addLast("HTTP_LOGGER",
                new HttpLoggingHandler(name()).setFrom(_from).setTo(_to).setAttachmentLogging(_isLogging));

        return cp;
    }
}