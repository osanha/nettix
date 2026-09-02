

package io.nettix.smpp.pdu;

import io.nettix.smpp.SmppCommand;

/**
 * submit_sm response PDU
 *
 * @author sanha
 */
public class SubmitSmResp
    extends SmRespPdu
{
  /**
   * Constructor
   */
  public SubmitSmResp()
  {
    super(SmppCommand.SUBMIT_SM_RESP);
  }

}
