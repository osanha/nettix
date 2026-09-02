

package io.nettix.smpp.pdu;

import io.nettix.smpp.SmppCommand;

/**
 * bind_transmitter response PDU
 *
 * @author sanha
 */
public class BindTransmitterResp
    extends BindRespPdu
{
  /**
   * Constructor
   */
  public BindTransmitterResp()
  {
    super(SmppCommand.BIND_TRANSMITTER_RESP);
  }

}
