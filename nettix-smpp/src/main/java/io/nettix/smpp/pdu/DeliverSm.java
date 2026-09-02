

package io.nettix.smpp.pdu;

import io.nettix.smpp.SmppCommand;

/**
 * deliver_sm PDU
 * 
 * @author sanh
 */
public class DeliverSm
    extends SmPdu
{
  /**
   * Constructor
   */
  public DeliverSm()
  {
    super(SmppCommand.DELIVER_SM);
  }

}
