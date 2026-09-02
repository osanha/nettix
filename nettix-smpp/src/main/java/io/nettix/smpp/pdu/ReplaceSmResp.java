

package io.nettix.smpp.pdu;

import io.nettix.smpp.SmppCommand;

/**
 * replace_sm response PDU
 *
 * @author sanha
 */
public class ReplaceSmResp
    extends ResponsePdu
{
  /**
   * Constructor
   */
  public ReplaceSmResp()
  {
    super(SmppCommand.REPLACE_SM_RESP);
  }

}
