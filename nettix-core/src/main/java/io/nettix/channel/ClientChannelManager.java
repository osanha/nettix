package io.nettix.channel;

import static io.nettix.util.Singleton.Executor;
import static io.nettix.util.Singleton.Timer;
import static io.nettix.util.Singleton.getNioWorkerPool;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.TimeUnit;

import org.jboss.netty.bootstrap.Bootstrap;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.bootstrap.ConnectionlessBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ConnectTimeoutException;
import org.jboss.netty.channel.DefaultChannelFuture;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.channel.local.LocalAddress;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.util.Timeout;
import org.jboss.netty.util.TimerTask;

import io.nettix.util.Singleton;

/**
 * Base class to simplify the creation of client protocol handlers.
 *
 * @author sanha
 */
public class ClientChannelManager
        extends AbstractChannelManager
{
    /**
     * Stores the handler for asynchronous connection events in a thread-local.
     */
    private static final ThreadLocal<ChannelHandler> _context = new ThreadLocal<ChannelHandler>();

    /**
     * Number of reconnection attempts.
     */
    private int _reconnCount = 2;

    /**
     * Interval between reconnection attempts in seconds.
     */
    private int _reconnInterval = 1;

    /**
     * Connection timeout in milliseconds.
     */
    private long _connTimeout = 30000;

    /**
     * Class representing the result of an asynchronous connection with
     * reconnection and timeout handling.
     */
    private class ChannelConnectFuture
            extends DefaultChannelFuture
            implements TimerTask
    {
        /**
         * Target address to connect to.
         */
        private final SocketAddress _addr;

        /**
         * Remaining number of reconnection attempts.
         */
        private int _count;

        /**
         * Channel created during the connection attempt.
         */
        private Channel _ch;

        /**
         * Handler for asynchronous connection events.
         */
        private final ChannelHandler _handler;

        /**
         * Current connection future.
         */
        private volatile ChannelFuture _connFuture;

        /**
         * Timeout task for delayed connection attempts.
         */
        private volatile Timeout _connTask;

        private boolean _isConnected;

        /**
         * Constructor.
         *
         * @param addr target address to connect to
         */
        public ChannelConnectFuture(SocketAddress addr)
        {
            super(null, true);
            _addr = addr;
            _count = _reconnCount;
            _handler = new SimpleChannelUpstreamHandler()
            {
                @Override
                public void channelConnected(ChannelHandlerContext ctx,
                                             ChannelStateEvent e) throws Exception
                {
                    _isConnected = true;
                    ChannelConnectFuture.this.setSuccess();
                    ctx.sendUpstream(e);
                }

                @Override
                public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
                        throws Exception
                {
                    if (_isConnected)
                    {
                        ctx.sendUpstream(e);
                    }
                    else
                    {
                        Throwable t = e.getCause();

                        if ((_count < 0)
                                || (!(t instanceof ConnectTimeoutException) && (_count-- > 0)))
                        {
                            _connTask = Singleton.Timer.newTimeout(ChannelConnectFuture.this,
                                    _reconnInterval,
                                    TimeUnit.SECONDS);
                        }
                        else
                        {
                            ChannelConnectFuture.this.setFailure(t);
                        }
                    }
                }
            };
        }

        /**
         * Returns the channel associated with this connection future.
         *
         * @return the channel
         */
        @Override
        public Channel getChannel()
        {
            return _ch;
        }

        /**
         * Cancels the asynchronous connection attempt.
         *
         * @return false if already completed
         */
        @Override
        public boolean cancel()
        {
            if (_connTask != null)
                _connTask.cancel();

            if (_connFuture != null)
                _connFuture.cancel();

            return super.cancel();
        }

        /**
         * Runs the task after a delay. Initiates the channel connection.
         *
         * @param task the timeout task
         */
        @Override
        public void run(Timeout task)
        {
            Bootstrap bs = bootstrap();
            _context.set(_handler);

            if (bs instanceof ClientBootstrap)
                _connFuture = ((ClientBootstrap) bs).connect(_addr);
            else
                _connFuture = ((ConnectionlessBootstrap) bs).connect(_addr);

            _ch = _connFuture.getChannel();
        }
    }

    /**
     * Constructor.
     *
     * @param name name of the client channel manager
     */
    public ClientChannelManager(String name)
    {
        super("Client." + name);
    }

    @Override
    public void setUp() throws Exception
    {
        if (getChannelFactory() == null)
            setChannelFactory(new NioClientSocketChannelFactory(Executor, 1,
                    getNioWorkerPool(),
                    Timer));

        super.setUp();
    }

    /**
     * Sets the number of reconnection attempts.
     *
     * @param count number of attempts
     */
    public void setReconnCount(int count)
    {
        _reconnCount = count;
    }

    /**
     * Sets the interval between reconnection attempts.
     *
     * @param time interval in seconds
     */
    public void setConnInterval(int time)
    {
        if (time <= 0)
            throw new IllegalArgumentException("time > 0");

        _reconnInterval = time;
    }

    /**
     * Sets the connection timeout.
     *
     * @param timeout timeout in seconds
     */
    public void setConnTimeout(int timeout)
    {
        if (timeout <= 0)
            throw new IllegalArgumentException("timeout > 0");

        _connTimeout = timeout * 1000L;
    }

    /**
     * Asynchronously connects to the specified host and port.
     *
     * @param host target host
     * @param port target port
     * @return asynchronous connection result
     */
    public ChannelFuture connect(String host, int port)
    {
        return connect(new InetSocketAddress(host, port));
    }

    /**
     * Asynchronously connects to a local virtual channel.
     *
     * @param port target port
     * @return asynchronous connection result
     */
    public ChannelFuture connect(int port)
    {
        return connect(new LocalAddress(port));
    }

    /**
     * Asynchronously connects to the specified address.
     *
     * @param addr target socket address
     * @return asynchronous connection result
     */
    public ChannelFuture connect(SocketAddress addr)
    {
        if (state() != State.RUNNING)
            throw new IllegalStateException(state().name());

        ChannelConnectFuture future = new ChannelConnectFuture(addr);
        future.run(null);
        return future;
    }

    @Override
    public ChannelPipeline getPipeline() throws Exception
    {
        ChannelPipeline pipeline = super.getPipeline();
        pipeline.addLast("CONNECT_EVENT_HANDLER", _context.get());
        return pipeline;
    }

    @Override
    public void configure(Bootstrap bootstrap)
    {
        if (bootstrap instanceof ClientBootstrap)
        {
            ClientBootstrap bs = (ClientBootstrap) bootstrap;
            SocketOptions.Client.connectTimeout(bs, _connTimeout);
        }
    }

}