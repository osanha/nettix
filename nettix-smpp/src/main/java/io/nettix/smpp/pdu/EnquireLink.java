

package io.nettix.smpp.pdu;

import io.nettix.smpp.SmppCommand;

/**
 * enquire_link PDU
 * 
 * @author sanha
 */
public class EnquireLink
    extends SmppPdu
{
  /**
   * Constructor
   */
  public EnquireLink()
  {
    super(SmppCommand.ENQUIRE_LINK);
  }

}
