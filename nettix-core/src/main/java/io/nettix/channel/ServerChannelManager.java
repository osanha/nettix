package io.nettix.channel;

import static io.nettix.util.Singleton.Executor;
import static io.nettix.util.Singleton.getNioWorkerPool;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

import org.jboss.netty.bootstrap.Bootstrap;
import org.jboss.netty.bootstrap.ConnectionlessBootstrap;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.local.LocalAddress;
import org.jboss.netty.channel.local.LocalServerChannelFactory;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for creating server protocol handlers easily.
 *
 * @author sanha
 */
public class ServerChannelManager
        extends AbstractChannelManager
{
    /**
     * Logger instance.
     */
    private static final Logger _logger = LoggerFactory.getLogger(ServerChannelManager.class);

    /**
     * Default size of the channel connection backlog.
     */
    private int _backlog = 1024;

    /**
     * Server port to listen on.
     */
    private final int _port;

    /**
     * Server channel that is listening for incoming connections.
     */
    private Channel _serverCh;

    /**
     * Constructor.
     *
     * @param name
     *          Name of the server manager
     * @param port
     *          Port to listen on
     */
    public ServerChannelManager(String name, int port)
    {
        super("Server." + name);
        _port = port;
    }

    /**
     * Returns the port this server is listening on.
     *
     * @return the listening port
     */
    public int port()
    {
        return _port;
    }

    /**
     * Sets the size of the channel connection backlog.
     *
     * @param backlog
     *          the backlog size
     */
    public void setBacklog(int backlog)
    {
        _backlog = backlog;
    }

    /**
     * Returns the server channel that is currently listening.
     *
     * @return the server channel
     */
    public Channel serverChannel()
    {
        return _serverCh;
    }

    @Override
    public void setUp() throws Exception
    {
        if (getChannelFactory() == null)
            setChannelFactory(new NioServerSocketChannelFactory(Executor, 1,
                    getNioWorkerPool()));

        super.setUp();
        SocketAddress addr;

        if (getChannelFactory() instanceof LocalServerChannelFactory)
            addr = new LocalAddress(_port);
        else
            addr = new InetSocketAddress(_port);

        Bootstrap bs = bootstrap();

        if (bs instanceof ConnectionlessBootstrap)
            _serverCh = ((ConnectionlessBootstrap) bs).bind(addr);
        else
            _serverCh = ((ServerBootstrap) bs).bind(addr);

        _logger.info("{} is binding to port {}", name(), _port);
    }

    @Override
    public void configure(Bootstrap bootstrap)
    {
        if (bootstrap instanceof ServerBootstrap)
        {
            ServerBootstrap bs = (ServerBootstrap) bootstrap;
            SocketOptions.Server.backlog(bs, _backlog);
            SocketOptions.reuseAddress(bs, true);
        }
    }

}