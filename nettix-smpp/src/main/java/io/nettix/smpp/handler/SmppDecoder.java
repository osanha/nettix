

package io.nettix.smpp.handler;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandler.Sharable;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneDecoder;

import io.nettix.smpp.SMPP;
import io.nettix.smpp.SmppCommand;
import io.nettix.smpp.pdu.SmppPdu;

/**
 * SMPP message decoder
 *
 * @author sanha
 */
@Sharable
public class SmppDecoder
    extends OneToOneDecoder
{
  @Override
  protected Object decode(ChannelHandlerContext ctx, Channel channel, Object msg)
      throws Exception
  {
    ChannelBuffer cb = (ChannelBuffer) msg;
    int code = cb.readInt();
    SmppCommand command = SMPP.getCommand(code);
    SmppPdu pdu = (SmppPdu) command.clazz().newInstance();
    pdu.decode(cb);
    return pdu;
  }
}
