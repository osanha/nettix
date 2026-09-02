package io.nettix.channel;

import org.jboss.netty.bootstrap.Bootstrap;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.bootstrap.ServerBootstrap;

/**
 * Socket options configurable via Bootstrap.
 *
 * @author sanha
 */
public class SocketOptions
{
    /**
     * Option for socket receive buffer size.
     */
    public static final String RECV_BUFF_SIZE = "receiveBufferSize";

    /**
     * Option for socket send buffer size.
     */
    public static final String SEND_BUFF_SIZE = "sendBufferSize";

    /**
     * Option for handling send buffer when socket is closed.
     */
    public static final String SO_LINGER = "soLinger";

    /**
     * Option for checking socket keep-alive status.
     */
    public static final String KEEP_ALIVE = "keepAlive";

    /**
     * Option for using TCP Nagle algorithm.
     */
    public static final String TCP_NO_DELAY = "tcpNoDelay";

    /**
     * Option for reusing socket address.
     */
    public static final String REUSE_ADDRESS = "reuseAddress";

    /**
     * Sets the socket receive buffer size.
     *
     * @param bs
     *          Target Bootstrap
     * @param size
     *          Buffer size in bytes
     */
    public static void recvBuffSize(Bootstrap bs, int size)
    {
        bs.setOption(RECV_BUFF_SIZE, size);
    }

    /**
     * Sets the socket send buffer size.
     *
     * @param bs
     *          Target Bootstrap
     * @param size
     *          Buffer size in bytes
     */
    public static void sendBuffSize(Bootstrap bs, int size)
    {
        bs.setOption(SEND_BUFF_SIZE, size);
    }

    /**
     * Configures how the socket handles remaining data in the send buffer when closed.
     * By default, the socket waits to send all remaining data. This option allows
     * immediate closure or waiting for a specified duration, but does not guarantee
     * all data is sent.
     *
     * @param bs
     *          Target Bootstrap
     * @param soLinger
     *          0 for immediate close, positive value to wait for that duration
     */
    public static void soLinger(Bootstrap bs, int soLinger)
    {
        bs.setOption(SO_LINGER, soLinger);
    }

    /**
     * Enables periodic detection of broken connections to close unnecessary sockets.
     * Note that this TCP option is optional and may not be implemented consistently
     * across systems. Application-level keep-alive is recommended for reliability.
     *
     * @param bs
     *          Target Bootstrap
     * @param isKeepAlive
     *          true to enable, false to disable
     */
    public static void keepAlive(Bootstrap bs, boolean isKeepAlive)
    {
        bs.setOption(KEEP_ALIVE, isKeepAlive);
    }

    /**
     * Configures whether to use the Nagle algorithm.
     * Nagle batches small packets to improve transmission efficiency but may increase
     * latency due to waiting for ACKs. TCP enables Nagle by default.
     *
     * @param bs
     *          Target Bootstrap
     * @param isNoDelay
     *          true to disable Nagle, false to enable
     */
    public static void tcpNoDelay(Bootstrap bs, boolean isNoDelay)
    {
        bs.setOption(TCP_NO_DELAY, isNoDelay);
    }

    /**
     * Configures whether the socket address can be reused.
     * Useful for servers that need to restart immediately without waiting for the port
     * to be released.
     *
     * @param bs
     *          Target Bootstrap
     * @param isReUse
     *          true to enable reuse, false to disable
     */
    public static void reuseAddress(Bootstrap bs, boolean isReUse)
    {
        bs.setOption(REUSE_ADDRESS, isReUse);
    }

    /**
     * Server socket specific options.
     *
     * @author sanha
     */
    public static final class Server
    {

        /**
         * Option for connection backlog size.
         */
        public static final String BACK_LOG = "backlog";

        /**
         * Sets the connection backlog size. If too many connections arrive at once,
         * the backlog queue may fill and reject connections. High-concurrency servers
         * should increase this value.
         *
         * @param bs
         *          Target ServerBootstrap
         * @param size
         *          Backlog queue size
         */
        public static void backlog(ServerBootstrap bs, int size)
        {
            bs.setOption(BACK_LOG, size);
        }

        /**
         * Options for child channels of the server socket.
         * Each option follows the same semantics as SocketOptions.
         *
         * @author sanha
         */
        public static final class Child
        {
            private static final String PREFIX = "child.";

            public static final String RECV_BUFF_SIZE = PREFIX
                    + SocketOptions.RECV_BUFF_SIZE;

            public static final String SEND_BUFF_SIZE = PREFIX
                    + SocketOptions.SEND_BUFF_SIZE;

            public static final String SO_LINGER = PREFIX + SocketOptions.SO_LINGER;

            public static final String KEEP_ALIVE = PREFIX + SocketOptions.KEEP_ALIVE;

            public static final String TCP_NO_DELAY = PREFIX
                    + SocketOptions.TCP_NO_DELAY;

            public static final String REUSE_ADDRESS = PREFIX
                    + SocketOptions.REUSE_ADDRESS;

            public static void recvBuffSize(ServerBootstrap bs, int size)
            {
                bs.setOption(RECV_BUFF_SIZE, size);
            }

            public static void sendBuffSize(ServerBootstrap bs, int size)
            {
                bs.setOption(SEND_BUFF_SIZE, size);
            }

            public static void soLinger(ServerBootstrap bs, int soLinger)
            {
                bs.setOption(SO_LINGER, soLinger);
            }

            public static void keepAlive(ServerBootstrap bs, boolean isKeepAlive)
            {
                bs.setOption(KEEP_ALIVE, isKeepAlive);
            }

            public static void tcpNoDelay(ServerBootstrap bs, boolean isNoDelay)
            {
                bs.setOption(TCP_NO_DELAY, isNoDelay);
            }

            public static void reuseAddress(ServerBootstrap bs, boolean isReUse)
            {
                bs.setOption(REUSE_ADDRESS, isReUse);
            }
        }

    }

    /**
     * Client socket specific options.
     *
     * @author sanha
     */
    public static final class Client
    {
        /**
         * Option for connection timeout in milliseconds.
         */
        public static final String CONNECT_TIMEOUT = "connectTimeoutMillis";

        /**
         * Sets the connection timeout.
         *
         * @param bs
         *          Target ClientBootstrap
         * @param timeout
         *          Timeout duration in milliseconds
         */
        public static void connectTimeout(ClientBootstrap bs, long timeout)
        {
            bs.setOption(CONNECT_TIMEOUT, timeout);
        }
    }

}