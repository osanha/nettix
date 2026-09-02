

package io.nettix.smpp.pdu;

import io.nettix.smpp.SmppCommand;

/**
 * Alert Notification PDU
 * 
 * @author sanha
 */
public class AlertNotification
    extends SmppPdu
{
  /**
   * Constructor.
   */
  public AlertNotification()
  {
    super(SmppCommand.ALERT_NOTIFICATION);
  }

}
