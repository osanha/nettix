

package io.nettix.smpp.pdu;

import java.util.Formatter;

import io.nettix.smpp.conf.SmppSubmitConf;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import io.nettix.smpp.SmppCommand;
import io.nettix.smpp.SmppOption;
import io.nettix.util.StringUtil;

/**
 * SM base PDU
 *
 * @author sanha
 */
public abstract class SmPdu
    extends SmppPdu
{
  /**
   * Constructor.
   *
   * @param id
   *          SMPP PDU type
   */
  public SmPdu(SmppCommand id)
  {
    super(id);
  }

  /**
   * Constructor.
   *
   * @param id SMPP PDU type
   * @param conf MT-SMS parameter configuration
   */
  public SmPdu(SmppCommand id, SmppSubmitConf conf)
  {
    super(id);
    this.serviceType = conf.getServiceType();
    this.sourceAddrTon = conf.getSourceAddrTon();
    this.sourceAddrNpi = conf.getSourceAddrNpi();
    this.sourceAddr = conf.getSourceAddr();
    this.destAddrTon = conf.getDestAddrTon();
    this.destAddrNpi = conf.getDestAddrNpi();
    this.esmClass = conf.getEsmClass();
    this.protocolId = conf.getProtocolId();
    this.priorityFlag = conf.getPriorityFlag();
    this.registeredDelivery = conf.getRegisteredDelivery();
    this.replaceIfPresentFlag = conf.getReplaceIfPresentFlag();
    this.dataCoding = conf.getDataCoding();
    this.smDefaultMsgId = conf.getSmDefaultMsgId();

    if (conf.getCallbackNum() != null)
    {
      ChannelBuffer buf = ChannelBuffers.dynamicBuffer();
      buf.writeZero(3);
      this.writeString(buf, conf.getCallbackNum());
      getOptions().add(new OptionalParam(SmppOption.CALLBACK_NUM, buf));
    }
  }

  /**
   * The serviceType parameter can be used to indicate the SMS Application
   * service associated with the message.
   */
  private String serviceType;

  /**
   * Type of Number for source address. If not known, set to NULL
   */
  private int sourceAddrTon;

  /**
   * Numbering Plan Indicator for source address. If not known, set to NULL
   */
  private int sourceAddrNpi;

  /**
   * Address of SME which originated this message. If not known, set to NULL
   */
  private String sourceAddr;

  /**
   * Type of Number for destination.
   */
  private int destAddrTon;

  /**
   * Numbering Plan Indicator for destination.
   */
  private int destAddrNpi;

  /**
   * Destination address of this short message. For mobile terminated messages,
   * this is the directory number of the recipient MS.
   */
  private String destinationAddr;

  /**
   * Indicates Message Mode & Message Type.
   */
  private int esmClass;

  /**
   * Protocol Identifier. Network specific field.
   */
  private int protocolId;

  /**
   * Designates the priority level of the message.
   */
  private int priorityFlag;

  /**
   * The short message is to be scheduled by the SMSC for delivery. Set to NULL
   * for immediate message delivery.
   */
  private String scheduleDeliveryTime;

  /**
   * The validity period of this message. Set to NULL to request the SMSC
   * default validity period.
   */
  private String validityPeriod;

  /**
   * Indicator to signify if an SMSC delivery receipt or an SME acknowledgement
   * is required.
   */
  private int registeredDelivery;

  /**
   * Flag indicating if submitted message should replace an existing message.
   */
  private int replaceIfPresentFlag;

  /**
   * Defines the encoding scheme of the short message user data.
   */
  private int dataCoding;

  /**
   * Indicates the short message to send from a list of pre- defined (‘canned’)
   * short messages stored on the SMSC. If not using an SMSC canned message, set
   * to NULL.hhhhhhhh
   */
  private int smDefaultMsgId;

  /**
   * Length in octets of the shortMessage user data.
   */
  private int smLength;

  /**
   * Up to 254 octets of short message user data. The exact physical limit for
   * shortMessage size may vary according to the underlying network.
   * 
   */
  private ChannelBuffer shortMessage;

  public String getServiceType()
  {
    return serviceType;
  }

  /**
   * Sets the serviceType.
   *
   * @param serviceType
   */
  public void setServiceType(String serviceType)
  {
    this.serviceType = serviceType;
  }

  /**
   * Returns the sourceAddrTon.
   *
   * @return sourceAddrTon
   */
  public int getSourceAddrTon()
  {
    return sourceAddrTon;
  }

  /**
   * Sets the sourceAddrTon.
   *
   * @param sourceAddrTon
   */
  public void setSourceAddrTon(int sourceAddrTon)
  {
    this.sourceAddrTon = sourceAddrTon;
  }

  /**
   * Returns the sourceAddrNpi.
   *
   * @return sourceAddrNpi
   */
  public int getSourceAddrNpi()
  {
    return sourceAddrNpi;
  }

  /**
   * Sets the sourceAddrNpi.
   *
   * @param sourceAddrNpi
   */
  public void setSourceAddrNpi(int sourceAddrNpi)
  {
    this.sourceAddrNpi = sourceAddrNpi;
  }

  /**
   * Returns the sourceAddr.
   *
   */
  public String getSourceAddr()
  {
    return sourceAddr;
  }

  /**
   * Sets the sourceAddr.
   *
   * @param sourceAddr
   */
  public void setSourceAddr(String sourceAddr)
  {
    this.sourceAddr = sourceAddr;
  }

  /**
   * Returns the destAddrTon.
   *
   * @return destAddrTon
   */
  public int getDestAddrTon()
  {
    return destAddrTon;
  }

  /**
   * Sets the destAddrTon.
   *
   * @param destAddrTon
   */
  public void setDestAddrTon(int destAddrTon)
  {
    this.destAddrTon = destAddrTon;
  }

  /**
   * Returns the destAddrNpi.
   *
   * @return destAddrNpi
   */
  public int getDestAddrNpi()
  {
    return destAddrNpi;
  }

  /**
   * Sets the destAddrNpi.
   *
   * @param destAddrNpi
   */
  public void setDestAddrNpi(int destAddrNpi)
  {
    this.destAddrNpi = destAddrNpi;
  }

  /**
   * Returns the destinationAddr.
   *
   * @return destinationAddr
   */
  public String getDestinationAddr()
  {
    return destinationAddr;
  }

  /**
   * Sets the destinationAddr.
   *
   * @param destinationAddr
   */
  public void setDestinationAddr(String destinationAddr)
  {
    this.destinationAddr = destinationAddr;
  }

  /**
   * Returns the esmClass.
   *
   * @return esmClass
   */
  public int getEsmClass()
  {
    return esmClass;
  }

  /**
   * Sets the esmClass.
   *
   * @param esmClass
   */
  public void setEsmClass(int esmClass)
  {
    this.esmClass = esmClass;
  }

  /**
   * Returns the protocolId.
   *
   * @return protocolId
   */
  public int getProtocolId()
  {
    return protocolId;
  }

  /**
   * Sets the protocolId.
   *
   * @param protocolId
   */
  public void setProtocolId(int protocolId)
  {
    this.protocolId = protocolId;
  }

  /**
   * Returns the priorityFlag.
   *
   * @return priorityFlag
   */
  public int getPriorityFlag()
  {
    return priorityFlag;
  }

  /**
   * Sets the priorityFlag.
   *
   * @param priorityFlag
   */
  public void setPriorityFlag(int priorityFlag)
  {
    this.priorityFlag = priorityFlag;
  }

  /**
   * Returns the scheduleDeliveryTime.
   *
   * @return scheduleDeliveryTime
   */
  public String getScheduleDeliveryTime()
  {
    return scheduleDeliveryTime;
  }

  /**
   * Sets the scheduleDeliveryTime.
   *
   * @param scheduleDeliveryTime
   */
  public void setScheduleDeliveryTime(String scheduleDeliveryTime)
  {
    this.scheduleDeliveryTime = scheduleDeliveryTime;
  }

  /**
   * Returns the validityPeriod.
   *
   * @return validityPeriod
   */
  public String getValidityPeriod()
  {
    return validityPeriod;
  }

  /**
   * Sets the validityPeriod.
   *
   * @param validityPeriod
   */
  public void setValidityPeriod(String validityPeriod)
  {
    this.validityPeriod = validityPeriod;
  }

  /**
   * Returns the registeredDelivery.
   *
   * @return registeredDelivery
   */
  public int getRegisteredDelivery()
  {
    return registeredDelivery;
  }

  /**
   * Sets the registeredDelivery.
   *
   * @param registeredDelivery
   */
  public void setRegisteredDelivery(int registeredDelivery)
  {
    this.registeredDelivery = registeredDelivery;
  }

  /**
   * Returns the replaceIfPresentFlag.
   *
   * @return replaceIfPresentFlag
   */
  public int getReplaceIfPresentFlag()
  {
    return replaceIfPresentFlag;
  }

  /**
   * Sets the replaceIfPresentFlag.
   *
   * @param replaceIfPresentFlag
   */
  public void setReplaceIfPresentFlag(int replaceIfPresentFlag)
  {
    this.replaceIfPresentFlag = replaceIfPresentFlag;
  }

  /**
   * Returns the dataCoding.
   *
   * @return dataCoding
   */
  public int getDataCoding()
  {
    return dataCoding;
  }

  /**
   * Sets the dataCoding.
   *
   * @param dataCoding
   */
  public void setDataCoding(int dataCoding)
  {
    this.dataCoding = dataCoding;
  }

  /**
   * Returns the smDefaultMsgId.
   *
   * @return smDefaultMsgId
   */
  public int getSmDefaultMsgId()
  {
    return smDefaultMsgId;
  }

  /**
   * Sets the smDefaultMsgId.
   *
   * @param smDefaultMsgId
   */
  public void setSmDefaultMsgId(int smDefaultMsgId)
  {
    this.smDefaultMsgId = smDefaultMsgId;
  }

  /**
   * Returns the smLength.
   *
   * @return smLength
   */
  public int getSmLength()
  {
    return smLength;
  }

  /**
   * Sets the smLength.
   *
   * @param smLength
   */
  public void setSmLength(int smLength)
  {
    this.smLength = smLength;
  }

  /**
   * Returns the shortMessage.
   *
   * @return shortMessage
   */
  public ChannelBuffer getShortMessage()
  {
    return shortMessage;
  }

  /**
   * Sets the shortMessage.
   *
   * @param shortMessage
   */
  public void setShortMessage(ChannelBuffer shortMessage)
  {
    this.shortMessage = shortMessage;
  }

  @Override
  public void encodeMandatory(ChannelBuffer cb)
  {
    this.writeString(cb, this.serviceType);
    cb.writeByte(this.sourceAddrTon);
    cb.writeByte(this.sourceAddrNpi);
    this.writeString(cb, this.sourceAddr);
    cb.writeByte(this.destAddrTon);
    cb.writeByte(this.destAddrNpi);
    this.writeString(cb, this.destinationAddr);
    cb.writeByte(this.esmClass);
    cb.writeByte(this.protocolId);
    cb.writeByte(this.priorityFlag);
    this.writeString(cb, this.scheduleDeliveryTime);
    this.writeString(cb, this.validityPeriod);
    cb.writeByte(this.registeredDelivery);
    cb.writeByte(this.replaceIfPresentFlag);
    cb.writeByte(this.dataCoding);
    cb.writeByte(this.smDefaultMsgId);
    cb.writeByte(this.smLength);
    cb.writeBytes(this.shortMessage);
  }

  @Override
  public void decodeMandatory(ChannelBuffer cb)
  {
    this.serviceType = this.readString(cb);
    this.sourceAddrTon = cb.readByte();
    this.sourceAddrNpi = cb.readByte();
    this.sourceAddr = this.readString(cb);
    this.destAddrTon = cb.readByte();
    this.destAddrNpi = cb.readByte();
    this.destinationAddr = this.readString(cb);
    this.esmClass = cb.readByte();
    this.protocolId = cb.readByte();
    this.priorityFlag = cb.readByte();
    this.scheduleDeliveryTime = this.readString(cb);
    this.validityPeriod = this.readString(cb);
    this.registeredDelivery = cb.readByte();
    this.replaceIfPresentFlag = cb.readByte();
    this.dataCoding = cb.readByte();
    this.smDefaultMsgId = cb.readByte();
    this.smLength = cb.readUnsignedByte();
    this.shortMessage = cb.readSlice(this.smLength);
  }

  @Override
  public void toStringMandatory(Formatter fmt, boolean isDebug)
  {
    fmt.format("%23s : %s\n", "serviceType", this.serviceType);
    fmt.format("%23s : 0x%02X\n", "sourceAddrTon", this.sourceAddrTon);
    fmt.format("%23s : 0x%02X\n", "sourceAddrNpi", this.sourceAddrNpi);
    fmt.format("%23s : %s\n", "sourceAddr", this.sourceAddr);
    fmt.format("%23s : 0x%02X\n", "destAddrTon", this.destAddrTon);
    fmt.format("%23s : 0x%02X\n", "destAddrNpi", this.destAddrNpi);
    fmt.format("%23s : %s\n", "destinationAddr", this.destinationAddr);
    fmt.format("%23s : 0x%02X\n", "esmClass", this.esmClass);
    fmt.format("%23s : 0x%02X\n", "protocolId", this.protocolId);
    fmt.format("%23s : 0x%02X\n", "priorityFlag", this.priorityFlag);
    fmt.format("%23s : %s\n", "scheduleDeliveryTime",
               this.scheduleDeliveryTime);
    fmt.format("%23s : %s\n", "validityPeriod", this.validityPeriod);
    fmt.format("%23s : 0x%02X\n", "registeredDelivery",
               this.registeredDelivery);
    fmt.format("%23s : 0x%02X\n", "replaceIfPresentFlag",
               this.replaceIfPresentFlag);
    fmt.format("%23s : 0x%02X\n", "dataCoding", this.dataCoding);
    fmt.format("%23s : 0x%02X\n", "smDefaultMsgId", this.smDefaultMsgId);
    fmt.format("%23s : 0x%02X\n", "smLength", this.smLength);

    if (isDebug)
      fmt.format("%23s :\n%s\n", "shortMessage",
                 StringUtil.toHexDump(shortMessage));
  }

  @Override
  protected void decodeOptional(ChannelBuffer cb)
  {
    super.decodeOptional(cb);

    for (OptionalParam op : getOptions())
      {
        if (op.getTag() == SmppOption.MESSAGE_PAYLOAD)
          {
            this.shortMessage = op.getValue();
            break;
          }
      }
  }

}
