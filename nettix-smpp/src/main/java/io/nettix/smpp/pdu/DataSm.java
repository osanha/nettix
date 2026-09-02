

package io.nettix.smpp.pdu;

import io.nettix.smpp.SmppCommand;

/**
 * data_sm PDU
 * 
 * @author sanha
 */
public class DataSm
    extends SmppPdu
{
  /**
   * Constructor
   */
  public DataSm()
  {
    super(SmppCommand.DATA_SM);
  }

}
