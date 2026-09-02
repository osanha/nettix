

package io.nettix.smpp.pdu;

import io.nettix.smpp.SmppCommand;

/**
 * deliver_sm response PDU
 *
 * @author sanha
 */
public class DeliverSmResp
    extends SmRespPdu
{
  /**
   * Constructor
   */
  public DeliverSmResp()
  {
    super(SmppCommand.DELIVER_SM_RESP);
  }

}
