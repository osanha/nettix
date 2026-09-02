

package io.nettix.smpp.pdu;

import io.nettix.smpp.SmppCommand;

/**
 * query_sm PDU
 * 
 * @author sanha
 */
public class QuerySm
    extends SmppPdu
{
  /**
   * Constructor
   */
  public QuerySm()
  {
    super(SmppCommand.QUERY_SM);
  }

}
