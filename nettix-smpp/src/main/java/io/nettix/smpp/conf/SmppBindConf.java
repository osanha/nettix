
package io.nettix.smpp.conf;

/**
 * SMPP bind parameter configuration
 */
public class SmppBindConf {

    private String _systemId;

    private String _password;

    private String _systemType;

    private Integer _interfaceVersion;

    private Integer _addrTon;

    private Integer _addrNpi;

    private String _addressRange;

    public SmppBindConf(String systemId, String password) {
        _systemId = systemId;
        _password = password;
    }

    /**
     * Gets the systemId value.
     */
    public String getSystemId() {
        return _systemId;
    }

    /**
     * Gets the password value.
     */
    public String getPassword() {
        return _password;
    }

    /**
     * Gets the systemType value. Default: ""
     */
    public String getSystemType() {
        return (_systemType != null) ? _systemType : "";
    }

    /**
     * Sets the systemType value.
     */
    public void setSystemType(String value) {
        this._systemType = value;
    }

    /**
     * Gets the interfaceVersion value. Default: 0x34
     */
    public Integer getInterfaceVersion() {
        return (_interfaceVersion != null) ? _interfaceVersion : 0x34;
    }

    /**
     * Sets the interfaceVersion value.
     */
    public void setInterfaceVersion(Integer value) {
        _interfaceVersion = value;
    }

    /**
     * Gets the addrTon value. Default: 0x01
     */
    public Integer getAddrTon() {
        return (_addrTon != null) ? _addrTon : 0x01;
    }

    /**
     * Sets the addrTon value.
     */
    public void setAddrTon(Integer value) {
        _addrTon = value;
    }

    /**
     * Gets the addrNpi value. Default: 0x01
     */
    public Integer getAddrNpi() {
        return (_addrNpi != null) ? _addrNpi : 0x01;
    }

    /**
     * Sets the addrNpi value.
     */
    public void setAddrNpi(Integer value) {
        _addrNpi = value;
    }

    /**
     * Gets the addressRange value. Default: ".*"
     */
    public String getAddressRange() {
        return (_addressRange != null) ? _addressRange : ".*";
    }

    /**
     * Sets the addressRange value.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setAddressRange(String value) {
        _addressRange = value;
    }

}
