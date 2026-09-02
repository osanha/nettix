

package io.nettix.smpp.pdu;

import io.nettix.smpp.SmppCommand;

/**
 * enquire_link response PDU
 *
 * @author sanha
 */
public class EnquireLinkResp
    extends ResponsePdu
{
  /**
   * Constructor
   */
  public EnquireLinkResp()
  {
    super(SmppCommand.ENQUIRE_LINK_RESP);
  }

}
