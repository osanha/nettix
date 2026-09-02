package io.nettix.channel;

import org.jboss.netty.bootstrap.Bootstrap;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.bootstrap.ConnectionlessBootstrap;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.ServerChannelFactory;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.socket.DatagramChannelFactory;
import org.jboss.netty.handler.ssl.SslHandler;

import io.nettix.channel.handler.ChannelGroupHandler;
import io.nettix.log.LoggingHandler;
import io.nettix.ssl.SslEngineFactory;
import io.nettix.ssl.SslHandshaker;
import io.nettix.util.AbstractStartable;
import io.nettix.util.Singleton;

/**
 * Abstract channel manager to simplify creating servers and clients.
 * Provides setup for pipelines, SSL, and connection management.
 *
 * @author sanha
 */
public abstract class AbstractChannelManager
        extends AbstractStartable
        implements ChannelPipelineFactory
{
    /**
     * Handler managing the group of connected channels for this manager.
     */
    private ChannelGroupHandler _chGroup;

    /**
     * Bootstrap instance for initializing channels.
     */
    private Bootstrap _bootstrap;

    /**
     * Handler for logging IO events.
     */
    private final LoggingHandler _ioLogger;

    /**
     * Factory for creating SSL engines.
     */
    private SslEngineFactory _sslFactory;

    /**
     * Factory for creating channels.
     */
    private ChannelFactory _chFactory;

    /**
     * SSL handshake timeout in milliseconds.
     */
    private long _sslTimeout = 30000;

    /**
     * Constructor.
     *
     * @param name the name, also used as a logger suffix
     */
    public AbstractChannelManager(String name)
    {
        this(name, null);
    }

    /**
     * Constructor.
     *
     * @param name the name, also used as a logger suffix
     * @param factory channel factory
     */
    public AbstractChannelManager(String name, ChannelFactory factory)
    {
        super(name);
        _chFactory = factory;
        _ioLogger = new LoggingHandler(name);
    }

    /**
     * Returns the channel factory.
     *
     * @return channel factory
     */
    public ChannelFactory getChannelFactory()
    {
        return _chFactory;
    }

    /**
     * Sets the channel factory.
     *
     * @param factory channel factory
     */
    public void setChannelFactory(ChannelFactory factory)
    {
        _chFactory = factory;
    }

    /**
     * Sets the SSL engine factory.
     *
     * @param factory SSL engine factory
     */
    public void setSslEngineFactory(SslEngineFactory factory)
    {
        _sslFactory = factory;
    }

    /**
     * Enables or disables management of the connected channel group.
     *
     * @param enabled true to manage connected channels
     */
    public void useChannelGroup(boolean enabled)
    {
        if (enabled)
            _chGroup = new ChannelGroupHandler();
        else
            _chGroup = null;
    }

    /**
     * Sets the SSL handshake timeout.
     *
     * @param timeout timeout in seconds
     */
    public void setSslHandshakeTimeout(int timeout)
    {
        if (timeout <= 0)
            throw new IllegalArgumentException("timeout > 0");

        _sslTimeout = timeout * 1000L;
    }

    /**
     * Returns the bootstrap instance.
     *
     * @return bootstrap
     */
    public Bootstrap bootstrap()
    {
        return _bootstrap;
    }

    /**
     * Returns the currently connected channel group.
     *
     * @return connected channel group, or null if not used
     */
    public ChannelGroup connections()
    {
        if (_chGroup != null)
            return _chGroup.connections();
        else
            return null;
    }

    /**
     * Returns the IO logging handler.
     *
     * @return IO logging handler
     */
    public LoggingHandler getIoLogger()
    {
        return _ioLogger;
    }

    @Override
    public ChannelPipeline getPipeline() throws Exception
    {
        ChannelPipeline cp = Channels.pipeline();

        cp.addLast("IO_LOGGER", _ioLogger);

        if (_sslFactory != null)
        {
            SslHandler ssl = new SslHandler(_sslFactory.createSslEngine(),
                    SslHandler.getDefaultBufferPool(),
                    false, Singleton.Timer, _sslTimeout);
            cp.addFirst("SSL_HANDLER", ssl);
            cp.addLast("SSL_HANDSHAKER", new SslHandshaker(ssl, name()));
        }

        if (_chGroup != null)
            cp.addLast("CHANNEL_GROUP", _chGroup);

        return cp;
    }

    /**
     * Configures the bootstrap instance.
     *
     * @param bootstrap bootstrap to configure
     */
    protected abstract void configure(Bootstrap bootstrap);

    /**
     * Called before start. Sets up and configures the bootstrap.
     */
    @Override
    public void setUp() throws Exception
    {
        if (_chFactory == null)
            throw new IllegalStateException("ChannelFactory is not set yet.");

        if (_chFactory instanceof DatagramChannelFactory)
            _bootstrap = new ConnectionlessBootstrap();
        else if (_chFactory instanceof ServerChannelFactory)
            _bootstrap = new ServerBootstrap();
        else
            _bootstrap = new ClientBootstrap();

        _bootstrap.setFactory(_chFactory);
        _bootstrap.setPipelineFactory(this);
        configure(_bootstrap);
    }

    /**
     * Called before stop. Releases bootstrap resources.
     */
    @Override
    public void tearDown() throws Exception
    {
        _bootstrap.shutdown();
    }

}