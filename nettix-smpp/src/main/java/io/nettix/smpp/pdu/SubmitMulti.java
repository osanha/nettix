

package io.nettix.smpp.pdu;

import io.nettix.smpp.SmppCommand;

/**
 * submit_multi PDU
 * 
 * @author sanha
 */
public class SubmitMulti
    extends SmppPdu
{
  /**
   * Constructor
   */
  public SubmitMulti()
  {
    super(SmppCommand.SUBMIT_MULTI);
  }

}
