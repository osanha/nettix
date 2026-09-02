package io.nettix.ssl;

import javax.net.ssl.SSLEngine;

/**
 * Interface for creating SSL engines.
 *
 * <p>This interface defines a factory for generating instances of {@link SSLEngine}.
 * Implementations are responsible for providing properly configured SSL engines
 * suitable for their specific use cases.</p>
 *
 * @author sanha
 */
public interface SslEngineFactory
{
    /**
     * Creates a new {@link SSLEngine} instance.
     *
     * @return a newly created {@link SSLEngine}
     */
    SSLEngine createSslEngine();
}