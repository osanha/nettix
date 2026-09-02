

package io.nettix.smpp.pdu;

import io.nettix.smpp.SmppCommand;

/**
 * unbind response PDU
 *
 * @author sanha
 */
public class UnbindResp
    extends ResponsePdu
{
  /**
   * Constructor
   */
  public UnbindResp()
  {
    super(SmppCommand.UNBIND_RESP);
  }

}
