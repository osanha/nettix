

package io.nettix.smpp.pdu;

import io.nettix.smpp.SmppCommand;
import io.nettix.smpp.conf.SmppSubmitConf;

/**
 * submit_sm PDU
 * 
 * @author sanha
 */
public class SubmitSm
    extends SmPdu
{
  /**
   * Constructor
   */
  public SubmitSm()
  {
    super(SmppCommand.SUBMIT_SM);
  }

  /**
   * Constructor
   *
   * @param conf
   *          configuration
   */
  public SubmitSm(SmppSubmitConf conf)
  {
    super(SmppCommand.SUBMIT_SM, conf);
  }

}
