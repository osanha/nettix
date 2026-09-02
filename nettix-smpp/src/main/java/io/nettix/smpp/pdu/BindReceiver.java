

package io.nettix.smpp.pdu;

import io.nettix.smpp.SmppCommand;

/**
 * bind_receiver PDU
 * 
 * @author sanha
 */
public class BindReceiver
    extends BindPdu
{
  /**
   * Constructor
   */
  public BindReceiver()
  {
    super(SmppCommand.BIND_RECEIVER);
  }

}
