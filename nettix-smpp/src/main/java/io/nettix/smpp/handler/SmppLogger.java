

package io.nettix.smpp.handler;

import org.jboss.netty.channel.ChannelHandler.Sharable;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.nettix.smpp.pdu.SmppPdu;

/**
 * SMPP message logger
 *
 * @author sanha
 */
@Sharable
public class SmppLogger
    extends SimpleChannelHandler
{
  /**
   * Logger
   */
  private final Logger _logger;

  public SmppLogger(String id)
  {
    _logger = LoggerFactory.getLogger(SmppLogger.class.getName() + '.' + id);
  }

  @Override
  public void writeRequested(ChannelHandlerContext ctx, MessageEvent e)
      throws Exception
  {
    log((SmppPdu) e.getMessage(), "Outbound message");
    ctx.sendDownstream(e);
  }

  @Override
  public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
      throws Exception
  {
    log((SmppPdu) e.getMessage(), "Inbound message");
    ctx.sendUpstream(e);
  }

  /**
   * Performs logging.
   *
   * @param pdu
   *          SMPP PDU
   * @param msg
   *          message
   */
  private void log(SmppPdu pdu, String msg)
  {
    _logger.info("{}\n{}", msg, pdu.toString(_logger.isDebugEnabled()));
  }

}
