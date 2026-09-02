

package io.nettix.smpp.pdu;

import io.nettix.smpp.SmppCommand;

/**
 * bind_transmitter PDU
 * 
 * @author sanha
 */
public class BindTransmitter
    extends BindPdu
{
  /**
   * Constructor
   */
  public BindTransmitter()
  {
    super(SmppCommand.BIND_TRANSMITTER);
  }

}
