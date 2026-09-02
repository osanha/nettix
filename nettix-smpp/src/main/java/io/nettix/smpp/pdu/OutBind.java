

package io.nettix.smpp.pdu;

import io.nettix.smpp.SmppCommand;

/**
 * out_bind PDU
 * 
 * @author sanha
 */
public class OutBind
    extends SmppPdu
{
  /**
   * Constructor
   */
  public OutBind()
  {
    super(SmppCommand.OUTBIND);
  }

}
