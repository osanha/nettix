package io.nettix.smpp.client;

import org.jboss.netty.buffer.ChannelBuffer;

/**
 * SMS event handler
 */
public interface SmppClientListener {

    public void mtSuccess(String smscId, String msgId);

    public void mtFailure(String smscId, String msgId, String msg);

    public void moReceived(String smscId, String sender, ChannelBuffer msg);

}
