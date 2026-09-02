

package io.nettix.smpp;

import io.nettix.util.StringUtil;

/**
 * SMPP status code definitions
 *
 * @author sanha
 */
public enum SmppStatus
{
  /**
   * No Error
   */
  ESME_ROK(0x00000000, "No Error"),

  /**
   * Message Length is invalid
   */
  ESME_RINVMSGLEN(0x00000001, "Message Length is invalid"),

  /**
   * Command Length is invalid
   */
  ESME_RINVCMDLEN(0x00000002, "Command Length is invalid"),

  /**
   * Invalid Command ID
   */
  ESME_RINVCMDID(0x00000003, "Invalid Command ID"),

  /**
   * Incorrect BIND Status for given command
   */
  ESME_RINVBNDSTS(0x00000004, "Incorrect BIND Status for given command"),

  /**
   * ESME Already in Bound State
   */
  ESME_RALYBND(0x00000005, "ESME Already in Bound State"),

  /**
   * Invalid Priority Flag
   */
  ESME_RINVPRTFLG(0x00000006, "Invalid Priority Flag"),

  /**
   * Invalid Registered Delivery Flag
   */
  ESME_RINVREGDLVFLG(0x00000007, "Invalid Registered Delivery Flag"),

  /**
   * System Error
   */
  ESME_RSYSERR(0x00000008, "System Error"),

  // Reserved 0x00000009

  /**
   * Invalid Source Address
   */
  ESME_RINVSRCADR(0x0000000A, "Invalid Source Address"),

  /**
   * Invalid Dest Addr
   */
  ESME_RINVDSTADR(0x0000000B, "Invalid Dest Addr"),

  /**
   * Message ID is invalid
   */
  ESME_RINVMSGID(0x0000000C, "Message ID is invalid"),

  /**
   * Bind Failed
   */
  ESME_RBINDFAIL(0x0000000D, "Bind Failed"),

  /**
   * Invalid Password
   */
  ESME_RINVPASWD(0x0000000E, "Invalid Password"),

  /**
   * Invalid System ID
   */
  ESME_RINVSYSID(0x0000000F, "Invalid System ID"),

  // Reserved 0x00000010

  /**
   * Cancel SM Failed
   */
  ESME_RCANCELFAIL(0x00000011, "Cancel SM Failed"),

  // Reserved 0x00000012

  /**
   * Replace SM Failed
   */
  ESME_RREPLACEFAIL(0x00000013, "Replace SM Failed"),

  /**
   * Message Queue Full
   */
  ESME_RMSGQFUL(0x00000014, "Message Queue Full"),

  /**
   * Invalid Service Type
   */
  ESME_RINVSERTYP(0x00000015, "Invalid Service Type"),

  // Reserved 0x00000016- 0x00000032

  /**
   * Invalid number of destinations
   */
  ESME_RINVNUMDESTS(0x00000033, "Invalid number of destinations"),

  /**
   * Invalid Distribution List name
   */
  ESME_RINVDLNAME(0x00000034, "Invalid Distribution List name"),

  // Reserved 0x00000035- 0x0000003F

  /**
   * Destination flag is invalid (submit_multi)
   */
  ESME_RINVDESTFLAG(0x00000040, "Destination flag is invalid (submit_multi)"),

  // Reserved 0x00000041

  /**
   * Invalid 'submit with replace' request (i.e. submit_sm with
   * replace_if_present_flag set)
   */
  ESME_RINVSUBREP(
      0x00000042,
      "Invalid 'submit with replace' request (i.e. submit_sm with replace_if_present_flag set)"),

  /**
   * Invalid esm_class field data
   */
  ESME_RINVESMCLASS(0x00000043, "Invalid esm_class field data"),

  /**
   * Cannot Submit to Distribution List
   */
  ESME_RCNTSUBDL(0x00000044, "Cannot Submit to Distribution List"),

  /**
   * submit_sm or submit_multi failed
   */
  ESME_RSUBMITFAIL(0x00000045, "submit_sm or submit_multi failed"),

  // Reserved 0x00000046- 0x00000047

  /**
   * Invalid Source address TON
   */
  ESME_RINVSRCTON(0x00000048, "Invalid Source address TON"),

  /**
   * Invalid Source address NPI
   */
  ESME_RINVSRCNPI(0x00000049, "Invalid Source address NPI"),

  /**
   * Invalid Destination address TON
   */
  ESME_RINVDSTTON(0x00000050, "Invalid Destination address TON"),

  /**
   * Invalid Destination address NPI
   */
  ESME_RINVDSTNPI(0x00000051, "Invalid Destination address NPI"),

  // Reserved 0x00000052

  /**
   * Invalid system_type field
   */
  ESME_RINVSYSTYP(0x00000053, "Invalid system_type field"),

  /**
   * Invalid replace_if_present flag
   */
  ESME_RINVREPFLAG(0x00000054, "Invalid replace_if_present flag"),

  /**
   * Invalid number of messages
   */
  ESME_RINVNUMMSGS(0x00000055, "Invalid number of messages"),

  // Reserved 0x00000056- 0x00000057

  /**
   * Throttling error (ESME has exceeded allowed message limits)
   */
  ESME_RTHROTTLED(0x00000058,
      "Throttling error (ESME has exceeded allowed message limits)"),

  // Reserved 0x00000059- 0x00000060

  /**
   * Invalid Scheduled Delivery Time
   */
  ESME_RINVSCHED(0x00000061, "Invalid Scheduled Delivery Time"),

  /**
   * Invalid message validity period (Expiry time)
   */
  ESME_RINVEXPIRY(0x00000062, "Invalid message validity period (Expiry time)"),

  /**
   * Predefined Message Invalid or Not Found
   */
  ESME_RINVDFTMSGID(0x00000063, "Predefined Message Invalid or Not Found"),

  /**
   * ESME Receiver Temporary App Error Code
   */
  ESME_RX_T_APPN(0x00000064, "ESME Receiver Temporary App Error Code"),

  /**
   * ESME Receiver Permanent App Error Code
   */
  ESME_RX_P_APPN(0x00000065, "ESME Receiver Permanent App Error Code"),

  /**
   * ESME Receiver Reject Message Error Code
   */
  ESME_RX_R_APPN(0x00000066, "ESME Receiver Reject Message Error Code"),

  /**
   * query_sm request failed
   */
  ESME_RQUERYFAIL(0x00000067, "query_sm request failed"),

  // Reserved 0x00000068 - 0x000000BF

  /**
   * Error in the optional part of the PDU Body.
   */
  ESME_RINVOPTPARSTREAM(0x000000C0,
      "Error in the optional part of the PDU Body."),

  /**
   * Optional Parameter not allowed
   */
  ESME_ROPTPARNOTALLWD(0x000000C1, "Optional Parameter not allowed"),

  /**
   * Invalid Parameter Length.
   */
  ESME_RINVPARLEN(0x000000C2, "Invalid Parameter Length."),

  /**
   * Expected Optional Parameter missing
   */
  ESME_RMISSINGOPTPARAM(0x000000C3, "Expected Optional Parameter missing"),

  /**
   * Invalid Optional Parameter Value
   */
  ESME_RINVOPTPARAMVAL(0x000000C4, "Invalid Optional Parameter Value"),

  // Reserved 0x000000C5 - 0x000000FD

  /**
   * Delivery Failure (used for data_sm_resp)
   */
  ESME_RDELIVERYFAILURE(0x000000FE, "Delivery Failure (used for data_sm_resp)"),

  /**
   * Unknown Error
   */
  ESME_RUNKNOWNERR(0x000000FF, "Unknown Error");

  // Reserved for SMPP extension 0x00000100- 0x000003FF
  // Reserved for SMSC vendor specific errors 0x00000400- 0x000004FF
  // Reserved 0x00000500 - 0xFFFFFFFF

  /**
   * Status description string
   */
  private String reason;

  /**
   * Status code
   */
  private int code;

  /**
   * Returns the status description string.
   *
   * @return description string
   */
  public String getReason()
  {
    return reason;
  }

  /**
   * Returns the status code value.
   *
   * @return code value
   */
  public int value()
  {
    return this.code;
  }

  /**
   * Constructor.
   *
   * @param code
   *          status code value
   * @param reason
   *          description string
   */
  SmppStatus(int code, String reason)
  {
    this.code = code;
    this.reason = reason;
  }

  @Override
  public String toString()
  {
    return StringUtil.concat(String.valueOf(code), " - ", reason);
  }

}
