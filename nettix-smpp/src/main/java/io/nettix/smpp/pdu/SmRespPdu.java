

package io.nettix.smpp.pdu;

import java.util.Formatter;

import org.jboss.netty.buffer.ChannelBuffer;

import io.nettix.smpp.SmppCommand;

/**
 * Response PDU for SM PDU
 *
 * @author sanha
 */
public abstract class SmRespPdu
    extends ResponsePdu
{
  /**
   * message_id
   */
  private String messageId;

  /**
   * Returns the message_id.
   *
   * @return message_id
   */
  public String getMessageId()
  {
    return messageId;
  }

  /**
   * Sets the message_id.
   *
   * @param messageId
   */
  public void setMessageId(String messageId)
  {
    this.messageId = messageId;
  }

  /**
   * Constructor.
   *
   * @param id
   *          command_id
   */
  public SmRespPdu(SmppCommand id)
  {
    super(id);
  }

  @Override
  public void encodeMandatory(ChannelBuffer cb)
  {
    this.writeString(cb, this.messageId);
  }

  @Override
  public void decodeMandatory(ChannelBuffer cb)
  {
    this.messageId = this.readString(cb);
  }

  @Override
  public void toStringMandatory(Formatter fmt, boolean isDebug)
  {
    fmt.format("message_id : %s\n", this.messageId);
  }

}
