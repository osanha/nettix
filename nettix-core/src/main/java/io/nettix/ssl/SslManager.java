package io.nettix.ssl;

import java.io.FileInputStream;
import java.net.InetSocketAddress;
import java.security.KeyStore;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * SSL manager for handling SSL contexts and keystores.
 *
 * @author sanha
 */
public class SslManager {
    /**
     * Logger instance
     */
    private static final Logger _logger = LoggerFactory.getLogger(SslManager.class);

    /**
     * Default SSL protocol
     */
    private static final String PROTOCOL = "TLS";

    /**
     * Default SSL algorithm
     */
    private static final String ALGORITHM = KeyManagerFactory.getDefaultAlgorithm();

    /**
     * Map storing SSL contexts
     */
    private static final Map<String, SSLContext> _map = new HashMap<String, SSLContext>();

    /**
     * Loads a keystore, initializes an SSLContext, and caches it.
     *
     * @param id Unique ID to associate with the SSLContext
     * @param type      Keystore type (e.g., "PKCS12", "JKS"). Defaults to "PKCS12" if null.
     * @param file  Path to the keystore file
     * @param pw  Keystore password
     * @param keyPw Key password; null if the keystore is used as a TrustStore
     * @throws Exception If keystore loading or SSLContext initialization fails
     */
    public static void loadKeyStore(String id, String type, String file, String pw, String keyPw) throws Exception
    {
        _logger.info("Loading keystore from {}", file);
        KeyStore ks = KeyStore.getInstance((type != null) ? type : "PKCS12");
        ks.load(new FileInputStream(file), pw.toCharArray());
        SSLContext ctx = SSLContext.getInstance(PROTOCOL);

        if (keyPw != null) {
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(ALGORITHM);
            kmf.init(ks, keyPw.toCharArray());
            ctx.init(kmf.getKeyManagers(), null, null);
        } else {
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(ALGORITHM);
            tmf.init(ks);
            ctx.init(null, tmf.getTrustManagers(), null);
        }

        _map.put(id, ctx);
    }

    /**
     * Returns a server SSL engine factory.
     *
     * @param id SSLContext ID
     * @return SSL engine factory for server
     */
    public static SslEngineFactory createServerSslFactory(String id) {
        return new ServerSslEngineFactory(_map.get(id));
    }

    /**
     * Returns a client SSL engine factory.
     *
     * @param id SSLContext ID
     * @param addr server address to connect from the client, used for session reuse
     * @return SSL engine factory for client
     */
    public static SslEngineFactory createClientSslFactory(String id, InetSocketAddress addr) {
        return createClientSslFactory(id, addr.getHostName(), addr.getPort());
    }

    /**
     * Returns a client SSL engine factory.
     *
     * @param id SSLContext ID
     * @param addr server address to connect from the client, used for session reuse
     * @param port server port to connect from the client, used for session reuse
     * @return SSL engine factory for client
     */
    public static SslEngineFactory createClientSslFactory(String id, String addr, int port) {
        return new ClientSslEngineFactory(_map.get(id), addr, port);
    }

}