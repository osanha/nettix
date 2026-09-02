

package io.nettix.smpp.pdu;

import io.nettix.smpp.SmppCommand;

/**
 * Base class for all response PDUs
 *
 * @author sanha
 */
public abstract class ResponsePdu
    extends SmppPdu
{
  /**
   * Constructor.
   *
   * @param id
   *          command ID
   */
  public ResponsePdu(SmppCommand id)
  {
    super(id);
  }

}
