

package io.nettix.smpp.pdu;

import io.nettix.smpp.SmppCommand;

/**
 * bind_transceiver response PDU
 *
 * @author sanha
 */
public class BindTransceiverResp
    extends BindRespPdu
{
  /**
   * Constructor
   */
  public BindTransceiverResp()
  {
    super(SmppCommand.BIND_TRANSCEIVER_RESP);
  }

}
