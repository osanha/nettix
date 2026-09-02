

package io.nettix.smpp.pdu;

import io.nettix.smpp.SmppCommand;

/**
 * bind_receiver response PDU
 *
 * @author sanha
 */
public class BindReceiverResp
    extends BindRespPdu
{
  /**
   * Constructor
   */
  public BindReceiverResp()
  {
    super(SmppCommand.BIND_RECEIVER_RESP);
  }

}
