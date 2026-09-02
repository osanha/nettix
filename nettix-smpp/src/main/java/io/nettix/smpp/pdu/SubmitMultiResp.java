

package io.nettix.smpp.pdu;

import io.nettix.smpp.SmppCommand;

/**
 * submit_multi response PDU
 *
 * @author sanha
 */
public class SubmitMultiResp
    extends ResponsePdu
{
  /**
   * Constructor
   */
  public SubmitMultiResp()
  {
    super(SmppCommand.SUBMIT_MULTI_RESP);
  }

}
