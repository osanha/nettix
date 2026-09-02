

package io.nettix.smpp.pdu;

import io.nettix.smpp.SmppCommand;

/**
 * bind_transceiver PDU
 * 
 * @author sanha
 */
public class BindTransceiver
    extends BindPdu
{
  /**
   * Constructor
   */
  public BindTransceiver()
  {
    super(SmppCommand.BIND_TRANSCEIVER);
  }

}
