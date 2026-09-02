

package io.nettix.smpp.handler;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandler.Sharable;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

import io.nettix.smpp.pdu.SmppPdu;

/**
 * SMPP message encoder
 *
 * @author sanha
 */
@Sharable
public class SmppEncoder
    extends OneToOneEncoder
{
  @Override
  protected Object encode(ChannelHandlerContext ctx, Channel channel, Object msg)
      throws Exception
  {
    return ((SmppPdu) msg).encode();
  }

}
