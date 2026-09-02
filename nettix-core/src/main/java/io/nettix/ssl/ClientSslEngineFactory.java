package io.nettix.ssl;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;

/**
 * Factory for creating client-side SSL engines.
 *
 * <p>This class is responsible for generating {@link SSLEngine} instances 
 * configured for client mode. It optionally supports SSL session reuse 
 * by associating an address and port.</p>
 *
 * @author sanha
 */
public class ClientSslEngineFactory
        implements SslEngineFactory
{
    /**
     * SSL context used to create SSL engines.
     */
    private final SSLContext _ctx;

    /**
     * Server address used to determine SSL session reuse.
     */
    private String _addr;

    /**
     * Server port used to determine SSL session reuse.
     */
    private int _port;

    /**
     * Constructs a factory with the given SSL context.
     *
     * @param ctx
     *          the SSL context
     */
    public ClientSslEngineFactory(SSLContext ctx)
    {
        _ctx = ctx;
    }

    /**
     * Constructs a factory with the given SSL context, address, and port.
     *
     * @param ctx
     *          the SSL context
     * @param addr
     *          the server address
     * @param port
     *          the server port
     */
    public ClientSslEngineFactory(SSLContext ctx, String addr, int port)
    {
        _ctx = ctx;
        _addr = addr;
        _port = port;
    }

    @Override
    public SSLEngine createSslEngine()
    {
        SSLEngine e;

        if (_addr != null)
            e = _ctx.createSSLEngine(_addr, _port);
        else
            e = _ctx.createSSLEngine();

        e.setUseClientMode(true);
        return e;
    }
}