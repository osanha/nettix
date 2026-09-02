package io.nettix.channel;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.DefaultChannelFuture;

/**
 * A future that aggregates the results of multiple channel futures.
 * It completes successfully when all contained futures succeed, or
 * fails if any one of them fails.
 *
 * @author sanha
 */
public class CollectionChannelFuture
        extends DefaultChannelFuture
        implements ChannelFutureListener
{
    /**
     * An empty CollectionChannelFuture to be used when no clients are connected.
     */
    public static final CollectionChannelFuture EMPTY = new CollectionChannelFuture(
            Collections.<ChannelFuture> emptyList());

    /**
     * Counter to track the completion of all channel futures.
     */
    private final AtomicInteger _counter;

    /**
     * Constructs a CollectionChannelFuture that aggregates the given channel futures.
     *
     * @param futures
     *          the collection of channel futures to track
     */
    public CollectionChannelFuture(Collection<ChannelFuture> futures)
    {
        super(null, false);
        _counter = new AtomicInteger(futures.size());

        for (ChannelFuture future : futures)
            future.addListener(this);
    }

    @Override
    public void operationComplete(ChannelFuture future) throws Exception
    {
        if (future.isSuccess())
        {
            if (_counter.decrementAndGet() == 0)
                setSuccess();
        }
        else
        {
            setFailure(future.getCause());
        }
    }

}