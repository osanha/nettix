

package io.nettix.smpp.pdu;

import java.util.Formatter;

import org.jboss.netty.buffer.ChannelBuffer;

import io.nettix.smpp.SmppCommand;

/**
 * Base PDU for BIND
 *
 * @author sanha
 */
public abstract class BindPdu
    extends SmppPdu
{
  /**
   * Constructor.
   *
   * @param id
   *          SMPP PDU type
   */
  public BindPdu(SmppCommand id)
  {
    super(id);
  }

  /**
   * Identifies the ESME system requesting to bind as a transmitter with the
   * SMSC.
   */
  private String systemId;

  /**
   * The password may be used by the SMSC to authenticate the ESME requesting to
   * bind.
   */
  private String password;

  /**
   * Identifies the type of ESME system requesting to bind as a transmitter with
   * the SMSC.
   */
  private String systemType;

  /**
   * Indicates the version of the SMPP protocol supported by the ESME.
   */
  private int interfaceVersion;

  /**
   * Indicates Type of Number of the ESME address. If not known set to NULL
   */
  private int addrTon;

  /**
   * Numbering Plan Indicator for ESME address. If not known set to NULL.
   */
  private int addrNpi;

  /**
   * The ESME address. If not known set to NULL.
   */
  private String addressRange;

  /**
   * Returns the system_id.
   *
   * @return system_id
   */
  public String getSystemId()
  {
    return systemId;
  }

  /**
   * Sets the system_id.
   *
   * @param systemId
   */
  public void setSystemId(String systemId)
  {
    this.systemId = systemId;
  }

  /**
   * Returns the password.
   *
   * @return Password
   */
  public String getPassword()
  {
    return password;
  }

  /**
   * Sets the password.
   *
   * @param password
   */
  public void setPassword(String password)
  {
    this.password = password;
  }

  /**
   * Returns the system_type.
   *
   * @return system_type
   */
  public String getSystemType()
  {
    return systemType;
  }

  /**
   * Sets the system_type.
   *
   * @param systemType
   */
  public void setSystemType(String systemType)
  {
    this.systemType = systemType;
  }

  /**
   * Returns the interface_version.
   *
   * @return interface_version
   */
  public int getInterfaceVersion()
  {
    return interfaceVersion;
  }

  /**
   * Sets the interface_version.
   *
   * @param interfaceVersion
   */
  public void setInterfaceVersion(int interfaceVersion)
  {
    this.interfaceVersion = interfaceVersion;
  }

  /**
   * Returns the addr_ton.
   *
   * @return addr_ton
   */
  public int getAddrTon()
  {
    return addrTon;
  }

  /**
   * Sets the addr_ton.
   *
   * @param addrTon
   */
  public void setAddrTon(int addrTon)
  {
    this.addrTon = addrTon;
  }

  /**
   * Returns the addr_npi.
   *
   * @return addr_npi
   */
  public int getAddrNpi()
  {
    return addrNpi;
  }

  /**
   * Sets the addr_npi.
   *
   * @param addrNpi
   */
  public void setAddrNpi(int addrNpi)
  {
    this.addrNpi = addrNpi;
  }

  /**
   * Returns the address_range.
   *
   * @return address_range
   */
  public String getAddressRange()
  {
    return addressRange;
  }

  /**
   * Sets the address_range.
   *
   * @param addressRange
   */
  public void setAddressRange(String addressRange)
  {
    this.addressRange = addressRange;
  }

  @Override
  public void encodeMandatory(ChannelBuffer cb)
  {
    this.writeString(cb, this.systemId);
    this.writeString(cb, this.password);
    this.writeString(cb, this.systemType);
    cb.writeByte(this.interfaceVersion);
    cb.writeByte(this.addrTon);
    cb.writeByte(this.addrNpi);
    this.writeString(cb, this.addressRange);
  }

  @Override
  protected void decodeMandatory(ChannelBuffer cb)
  {
    this.systemId = this.readString(cb);
    this.password = this.readString(cb);
    this.systemType = this.readString(cb);
    this.interfaceVersion = cb.readByte();
    this.addrTon = cb.readByte();
    this.addrNpi = cb.readByte();
    this.addressRange = this.readString(cb);
  }

  @Override
  public void toStringMandatory(Formatter fmt, boolean isDebug)
  {
    fmt.format("%17s : %s\n", "system_id", this.systemId);
    fmt.format("%17s : %s\n", "password", this.password);
    fmt.format("%17s : %s\n", "system_type", this.systemType);
    fmt.format("%17s : 0x%08X\n", "interface_version", this.interfaceVersion);
    fmt.format("%17s : 0x%08X\n", "addr_ton", this.addrTon);
    fmt.format("%17s : 0x%08X\n", "addr_npi", this.addrNpi);
    fmt.format("%17s : %s\n", "address_range", this.addressRange);
  }

}
