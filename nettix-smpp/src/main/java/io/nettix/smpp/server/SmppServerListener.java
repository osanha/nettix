

package io.nettix.smpp.server;

import org.jboss.netty.buffer.ChannelBuffer;

/**
 * MT message listener
 *
 * @author sanha
 */
public interface SmppServerListener
{
  /**
   * Handles MT message.
   *
   * @param msgId
   *          SMS ID
   * @param sender
   *          sender number
   * @param receiver
   *          receiver number
   * @param msg
   *          SMS message
   */
  void messageReceived(String msgId, String sender, String receiver,
                       ChannelBuffer msg);
}
