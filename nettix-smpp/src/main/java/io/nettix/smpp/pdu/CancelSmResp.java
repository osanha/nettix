

package io.nettix.smpp.pdu;

import io.nettix.smpp.SmppCommand;

/**
 * cancel_sm response PDU
 *
 * @author sanha
 */
public class CancelSmResp
    extends ResponsePdu
{
  /**
   * Constructor
   */
  public CancelSmResp()
  {
    super(SmppCommand.CANCEL_SM_RESP);
  }

}
