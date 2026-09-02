

package io.nettix.smpp.pdu;

import io.nettix.smpp.SmppCommand;

/**
 * generic_nack PDU
 * 
 * @author sanha
 */
public class GenericNack
    extends ResponsePdu
{
  /**
   * Constructor
   */
  public GenericNack()
  {
    super(SmppCommand.GENERIC_NACK);
  }

}
