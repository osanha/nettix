

package io.nettix.smpp.mt;

import org.jboss.netty.buffer.ChannelBuffer;

/**
 * Delivery receipt message handler
 *
 * @author sanha
 */
public interface DeliveryReceipt
{
  /**
   * Parses delivery receipt message.
   *
   * @param msg
   *          message
   * @return parse result
   */
  Result parse(ChannelBuffer msg);
}
