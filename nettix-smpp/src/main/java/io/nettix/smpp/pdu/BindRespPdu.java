

package io.nettix.smpp.pdu;

import java.util.Formatter;

import org.jboss.netty.buffer.ChannelBuffer;

import io.nettix.smpp.SmppCommand;

/**
 * Response PDU for Bind PDU
 *
 * @author sanha
 */
public abstract class BindRespPdu
    extends ResponsePdu
{
  /**
   * system_id
   */
  private String systemId;

  /**
   * Returns the system_id.
   *
   * @return system_id
   */
  public String getSystemId()
  {
    return systemId;
  }

  /**
   * Sets the system_id.
   *
   * @param systemId
   */
  public void setSystemId(String systemId)
  {
    this.systemId = systemId;
  }

  /**
   * Constructor.
   *
   * @param id
   *          SMPP PDU type
   */
  public BindRespPdu(SmppCommand id)
  {
    super(id);
  }

  @Override
  public void decodeMandatory(ChannelBuffer cb)
  {
    this.systemId = this.readString(cb);
  }

  @Override
  public void encodeMandatory(ChannelBuffer cb)
  {
    this.writeString(cb, this.systemId);
  }

  @Override
  public void toStringMandatory(Formatter fmt, boolean isDebug)
  {
    fmt.format("system_id : %s\n", this.systemId);
  }

}
