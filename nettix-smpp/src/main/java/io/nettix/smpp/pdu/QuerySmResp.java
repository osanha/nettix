

package io.nettix.smpp.pdu;

import io.nettix.smpp.SmppCommand;

/**
 * query_sm response PDU
 *
 * @author sanha
 */
public class QuerySmResp
    extends ResponsePdu
{
  /**
   * Constructor
   */
  public QuerySmResp()
  {
    super(SmppCommand.QUERY_SM_RESP);
  }

}
