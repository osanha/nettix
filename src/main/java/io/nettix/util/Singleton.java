package io.nettix.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.socket.nio.NioDatagramWorker;
import org.jboss.netty.channel.socket.nio.NioDatagramWorkerPool;
import org.jboss.netty.channel.socket.nio.NioWorker;
import org.jboss.netty.channel.socket.nio.NioWorkerPool;
import org.jboss.netty.channel.socket.nio.ShareableWorkerPool;
import org.jboss.netty.channel.socket.nio.WorkerPool;
import org.jboss.netty.handler.execution.ExecutionHandler;
import org.jboss.netty.handler.execution.MemoryAwareThreadPoolExecutor;
import org.jboss.netty.util.HashedWheelTimer;
import org.jboss.netty.util.Timer;

/**
 * Repository for singleton instances.
 *
 * @author sanha
 */
public class Singleton
{

    /**
     * Executor for tasks scheduled to run after a certain delay.
     */
    public static final Timer Timer = new HashedWheelTimer();

    /**
     * Thread pool based executor service.
     */
    public static final ExecutorService Executor = Executors.newCachedThreadPool();

    /**
     * Worker thread pool for asynchronous socket IO event handling.
     */
    private static WorkerPool<NioWorker> _nioWorkerPool;

    /**
     * Worker thread pool for asynchronous datagram IO event handling.
     */
    private static WorkerPool<NioDatagramWorker> _nioDatagramWorkerPool;

    /**
     * Default number of threads in the pool.
     */
    public static final int WORKER_DEFAULT_COUNT = (int) (Runtime.getRuntime().availableProcessors() * 2.5);

    /**
     * Default size of the NioWorkerPool.
     */
    private static int _nioWorkerCount = WORKER_DEFAULT_COUNT;

    /**
     * Default size of the NioDatagramWorkerPool.
     */
    private static int _nioDatagramWorkerCount = WORKER_DEFAULT_COUNT;

    /**
     * Asynchronous execution handler.
     */
    public static final ChannelHandler ExecutionHandler = new ExecutionHandler(
            new MemoryAwareThreadPoolExecutor(
                    WORKER_DEFAULT_COUNT,
                    10 * 1024 * 1024,
                    100 * 1024 * 1024));

    /**
     * Sets the size of the NioWorkerPool.
     *
     * @param count the desired number of worker threads
     */
    public static void setNioWorkerCount(int count)
    {
        _nioWorkerCount = count;
    }

    /**
     * Sets the size of the NioDatagramWorkerPool.
     *
     * @param count the desired number of datagram worker threads
     */
    public static void setNioDatagramWorkerCount(int count)
    {
        _nioDatagramWorkerCount = count;
    }

    /**
     * Returns the TCP-based asynchronous worker thread pool.
     *
     * @return the worker thread pool
     */
    public static WorkerPool<NioWorker> getNioWorkerPool()
    {
        if (_nioWorkerPool == null)
        {
            synchronized (Singleton.class)
            {
                if (_nioWorkerPool == null)
                {
                    WorkerPool<NioWorker> pool = new NioWorkerPool(Executor,
                            _nioWorkerCount);
                    _nioWorkerPool = new ShareableWorkerPool<NioWorker>(pool);
                }
            }
        }

        return _nioWorkerPool;
    }

    /**
     * Returns the UDP-based asynchronous worker thread pool.
     *
     * @return the datagram worker thread pool
     */
    public static WorkerPool<NioDatagramWorker> getNioDatagramWorkerPool()
    {
        if (_nioDatagramWorkerPool == null)
        {
            synchronized (Singleton.class)
            {
                if (_nioWorkerPool == null)
                {
                    WorkerPool<NioDatagramWorker> pool = new NioDatagramWorkerPool(
                            Executor,
                            _nioDatagramWorkerCount);

                    _nioDatagramWorkerPool = new ShareableWorkerPool<NioDatagramWorker>(
                            pool);
                }
            }
        }

        return _nioDatagramWorkerPool;
    }

}