

package io.nettix.smpp.mt;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.util.CharsetUtil;

import io.nettix.util.Character;
import io.nettix.util.StringUtil;

/**
 * Standard delivery receipt message handler
 *
 * @author sanha
 */
public class DefaultDeliveryReceipt
    implements DeliveryReceipt
{
  @Override
  public Result parse(ChannelBuffer msg)
  {
    msg.skipBytes(3);
    String msgId = StringUtil.readString(msg, (byte) Character.SPACE);
    msg.skipBytes(67);
    String state = msg.toString(msg.readerIndex(), 7, CharsetUtil.US_ASCII);
    return new Result(msgId, state);
  }

}
