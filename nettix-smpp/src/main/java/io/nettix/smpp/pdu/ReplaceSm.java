

package io.nettix.smpp.pdu;

import io.nettix.smpp.SmppCommand;

/**
 * replace_sm PDU
 * 
 * @author sanha
 */
public class ReplaceSm
    extends SmppPdu
{
  /**
   * Constructor
   */
  public ReplaceSm()
  {
    super(SmppCommand.REPLACE_SM);
  }

}
