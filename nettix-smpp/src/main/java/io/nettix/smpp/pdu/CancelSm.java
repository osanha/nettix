

package io.nettix.smpp.pdu;

import io.nettix.smpp.SmppCommand;

/**
 * cancel_sm PDU
 * 
 * @author sanha
 */
public class CancelSm
    extends SmppPdu
{
  /**
   * Constructor
   */
  public CancelSm()
  {
    super(SmppCommand.CANCEL_SM);
  }

}
