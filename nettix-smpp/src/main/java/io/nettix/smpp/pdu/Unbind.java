

package io.nettix.smpp.pdu;

import io.nettix.smpp.SmppCommand;

/**
 * unbind PDU
 * 
 * @author sanha
 */
public class Unbind
    extends SmppPdu
{
  /**
   * Constructor
   */
  public Unbind()
  {
    super(SmppCommand.UNBIND);
  }

}
