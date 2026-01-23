package io.nettix.ssl;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;

/**
 * Factory for creating server-side {@link SSLEngine} instances.
 *
 * @author sanha
 */
public class ServerSslEngineFactory
        implements SslEngineFactory
{
    /**
     * The SSL context used to initialize SSL engines.
     */
    private final SSLContext _ctx;

    /**
     * Constructs a new {@code ServerSslEngineFactory}.
     *
     * @param ctx
     *          the SSL context to be used
     */
    public ServerSslEngineFactory(SSLContext ctx)
    {
        _ctx = ctx;
    }

    @Override
    public SSLEngine createSslEngine()
    {
        SSLEngine e = _ctx.createSSLEngine();
        e.setUseClientMode(false);
        return e;
    }

}