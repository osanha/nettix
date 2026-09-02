

package io.nettix.smpp.pdu;

import io.nettix.smpp.SmppCommand;

/**
 * data_sm response PDU
 *
 * @author sanha
 */
public class DataSmResp
    extends ResponsePdu
{
  /**
   * Constructor
   */
  public DataSmResp()
  {
    super(SmppCommand.DATA_SM_RESP);
  }

}
