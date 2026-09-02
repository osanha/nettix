

package io.nettix.smpp;

import io.nettix.smpp.pdu.AlertNotification;
import io.nettix.smpp.pdu.BindReceiver;
import io.nettix.smpp.pdu.BindReceiverResp;
import io.nettix.smpp.pdu.BindTransceiver;
import io.nettix.smpp.pdu.BindTransceiverResp;
import io.nettix.smpp.pdu.BindTransmitter;
import io.nettix.smpp.pdu.BindTransmitterResp;
import io.nettix.smpp.pdu.CancelSm;
import io.nettix.smpp.pdu.CancelSmResp;
import io.nettix.smpp.pdu.DataSm;
import io.nettix.smpp.pdu.DataSmResp;
import io.nettix.smpp.pdu.DeliverSm;
import io.nettix.smpp.pdu.DeliverSmResp;
import io.nettix.smpp.pdu.EnquireLink;
import io.nettix.smpp.pdu.EnquireLinkResp;
import io.nettix.smpp.pdu.GenericNack;
import io.nettix.smpp.pdu.OutBind;
import io.nettix.smpp.pdu.QuerySm;
import io.nettix.smpp.pdu.QuerySmResp;
import io.nettix.smpp.pdu.ReplaceSm;
import io.nettix.smpp.pdu.ReplaceSmResp;
import io.nettix.smpp.pdu.SubmitMulti;
import io.nettix.smpp.pdu.SubmitMultiResp;
import io.nettix.smpp.pdu.SubmitSm;
import io.nettix.smpp.pdu.SubmitSmResp;
import io.nettix.smpp.pdu.Unbind;
import io.nettix.smpp.pdu.UnbindResp;

/**
 * SMPP PDU types
 *
 * @author sanha
 */
public enum SmppCommand
{
  /**
   * This is a generic negative acknowledgement to an SMPP PDU submitted with an
   * invalid message header.
   */
  GENERIC_NACK(0x80000000, GenericNack.class),

  /**
   * An ESME bound as a Receiver is authorised to receive short messages from
   * the SMSC and to return the corresponding SMPP message responses to the
   * SMSC.
   */
  BIND_RECEIVER(0x00000001, BindReceiver.class),

  /**
   * bind_receiver response
   */
  BIND_RECEIVER_RESP(0x80000001, BindReceiverResp.class),

  /**
   * An ESME bound as a Transmitter is authorised to send short messages to the
   * SMSC and to receive the corresponding SMPP responses from the SMSC. An ESME
   * indicates its desire not to receive (mobile) originated messages from other
   * SME’s (e.g. mobile stations) by binding as a Transmitter.
   */
  BIND_TRANSMITTER(0x00000002, BindTransmitter.class),

  /**
   * bind_transmitter response
   */
  BIND_TRANSMITTER_RESP(0x80000002, BindTransmitterResp.class),

  /**
   * This command is issued by the ESME to query the status of a previously
   * submitted short message.
   */
  QUERY_SM(0x00000003, QuerySm.class),

  /**
   * query_sm response
   */
  QUERY_SM_RESP(0x80000003, QuerySmResp.class),

  /**
   * This operation is used by an ESME to submit a short message to the SMSC for
   * onward transmission to a specified short message entity (SME). The
   * submit_sm PDU does not support the transaction message mode.
   */
  SUBMIT_SM(0x00000004, SubmitSm.class),

  /**
   * submit_sm response
   */
  SUBMIT_SM_RESP(0x80000004, SubmitSmResp.class),

  /**
   * The operation is issued by the SMSC to send a message to an ESME. Using
   * this command, the SMSC may route a short message to the ESME for delivery.
   */
  DELIVER_SM(0x00000005, DeliverSm.class),

  /**
   * deliver_sm response
   */
  DELIVER_SM_RESP(0x80000005, DeliverSmResp.class),

  /**
   * The purpose of the SMPP unbind operation is to deregister an instance of an
   * ESME from the SMSC and inform the SMSC that the ESME no longer wishes to
   * use this network connection for the submission or delivery of messages.
   */
  UNBIND(0x00000006, Unbind.class),

  /**
   * unbind response
   */
  UNBIND_RESP(0x80000006, UnbindResp.class),

  /**
   * This command is issued by the ESME to replace a previously submitted short
   * message that is still pending delivery. The matching mechanism is based on
   * the message_id and source address of the original message.
   */
  REPLACE_SM(0x00000007, ReplaceSm.class),

  /**
   * replace_sm response
   */
  REPLACE_SM_RESP(0x80000007, ReplaceSmResp.class),

  /**
   * This command is issued by the ESME to cancel one or more previously
   * submitted short messages that are still pending delivery. The command may
   * specify a particular message to cancel, or all messages for a particular
   * source, destination and service_type are to be cancelled.
   */
  CANCEL_SM(0x00000008, CancelSm.class),

  /**
   * cancel_sm response
   */
  CANCEL_SM_RESP(0x80000008, CancelSmResp.class),

  /**
   * An ESME bound as a Transceiver is allowed to send messages to the SMSC and
   * receive messages from the SMSC over a single SMPP session.
   */
  BIND_TRANSCEIVER(0x00000009, BindTransceiver.class),

  /**
   * bind_transceiver response
   */
  BIND_TRANSCEIVER_RESP(0x80000009, BindTransceiverResp.class),

  /**
   * This operation is used by the SMSC to signal an ESME to originate a
   * bind_receiver request to the SMSC.
   */
  OUTBIND(0x0000000B, OutBind.class),

  /**
   * This message can be sent by either the ESME or SMSC and is used to provide
   * a confidence- check of the communication path between an ESME and an SMSC.
   * On receipt of this request the receiving party should respond with an
   * enquire_link_resp, thus verifying that the application level connection
   * between the SMSC and the ESME is functioning. The ESME may also respond by
   * sending any valid SMPP primitive.
   */
  ENQUIRE_LINK(0x00000015, EnquireLink.class),

  /**
   * enquire_link response
   */
  ENQUIRE_LINK_RESP(0x80000015, EnquireLinkResp.class),

  /**
   * The operation may be used to submit an SMPP message for delivery to
   * multiple recipients or to one or more Distribution Lists. The submit_multi
   * PDU does not support the transaction message mode.
   */
  SUBMIT_MULTI(0x00000021, SubmitMulti.class),

  /**
   * submit_multi response
   */
  SUBMIT_MULTI_RESP(0x80000021, SubmitMultiResp.class),

  /**
   * This message is sent by the SMSC to the ESME, when the SMSC has detected
   * that a particular mobile subscriber has become available and a delivery
   * pending flag had been set for that subscriber from a previous data_sm
   * operation.
   */
  ALERT_NOTIFICATION(0x00000102, AlertNotification.class),

  /**
   * This command is used to transfer data between the SMSC and the ESME. It may
   * be used by both the ESME and SMSC. This command is an alternative to the
   * submit_sm and deliver_sm commands. It is introduced as a new command to be
   * used by interactive applications such as those provided via a WAP
   * framework. The ESME may use this command to request the SMSC to transfer a
   * message to an MS. The SMSC may also use this command to transfer an MS
   * originated message to an ESME.
   */
  DATA_SM(0x00000103, DataSm.class),

  /**
   * data_sm response
   */
  DATA_SM_RESP(0x80000103, DataSmResp.class);

  /**
   * command id
   */
  private int id;

  /**
   * Class for instantiation
   */
  private Class<?> clazz;

  /**
   * Returns the class.
   *
   * @return class
   */
  public Class<?> clazz()
  {
    return clazz;
  }

  /**
   * Returns the id value.
   *
   * @return id
   */
  public int value()
  {
    return id;
  }

  /**
   * Constructor.
   *
   * @param id
   *          command_id
   * @param clazz
   *          class
   */
  private SmppCommand(int id, Class<?> clazz)
  {
    this.id = id;
    this.clazz = clazz;
  }

}
