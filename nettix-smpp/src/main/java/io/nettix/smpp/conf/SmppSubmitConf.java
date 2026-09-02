
package io.nettix.smpp.conf;

import io.nettix.smpp.SMPP;

/**
 * SMPP MT-SMS parameter configuration
 */
public class SmppSubmitConf {

    private final String _sourceAddr;

    private String _deliveryReceipt;

    private String _serviceType = "";

    private int _sourceAddrTon = 0x01;

    private int _sourceAddrNpi = 0x01;

    private int _destAddrTon = 0x01;

    private int _destAddrNpi = 0x01;

    private int _esmClass = 0x00;

    private int _protocolId = 0x00;

    private int _priorityFlag = 0x01;

    private int _registeredDelivery = 0x01;

    private int _replaceIfPresentFlag = 0x00;

    private int _dataCoding = 0x04;

    private int _smDefaultMsgId = 0x01;

    private String _callbackNum;

    private String _validityPeriod = SMPP.getValidityPeriod(3600 * 24);

    /**
     * Constructor.
     *
     * @param sourceAddr source address (sender number)
     */
    public SmppSubmitConf(String sourceAddr) {
        _sourceAddr = sourceAddr;
    }

    /**
     * Sets validity_period (seconds). Default: 3600 * 24
     */
    public void setValidityPeriod(int sec) {
       _validityPeriod = SMPP.getValidityPeriod(sec);
    }

    /**
     * Gets validity_period value.
     * @return validity period string
     */
    public String getValidityPeriod() {
        return _validityPeriod;
    }

    /**
     * Gets sourceAddr value.
     */
    public String getSourceAddr() {
        return _sourceAddr;
    }

    /**
     * Gets deliveryReceipt value.
     */
    public String getDeliveryReceipt() {
        return _deliveryReceipt;
    }

    /**
     * Sets deliveryReceipt value.
     */
    public void setDeliveryReceipt(String value) {
        _deliveryReceipt = value;
    }

    /**
     * Gets serviceType value. Default: ""
     */
    public String getServiceType() {
        return _serviceType;
    }

    /**
     * Sets serviceType value.
     */
    public void setServiceType(String value) {
        _serviceType = value;
    }

    /**
     * Gets sourceAddrTon value. Default: 0x01
     */
    public int getSourceAddrTon() {
        return _sourceAddrTon;
    }

    /**
     * Sets sourceAddrTon value.
     */
    public void setSourceAddrTon(int value) {
        _sourceAddrTon = value;
    }

    /**
     * Gets sourceAddrNpi value. Default: 0x01
     */
    public int getSourceAddrNpi() {
        return _sourceAddrNpi;
    }

    /**
     * Sets sourceAddrNpi value.
     */
    public void setSourceAddrNpi(int value) {
        _sourceAddrNpi = value;
    }

    /**
     * Gets destAddrTon value. Default: 0x01
     */
    public int getDestAddrTon() {
        return _destAddrTon;
    }

    /**
     * Sets destAddrTon value.
     */
    public void setDestAddrTon(int value) {
        _destAddrTon = value;
    }

    /**
     * Gets destAddrNpi value. Default: 0x01
     */
    public int getDestAddrNpi() {
        return _destAddrNpi;
    }

    /**
     * Sets destAddrNpi value.
     */
    public void setDestAddrNpi(int value) {
        _destAddrNpi = value;
    }

    /**
     * Gets esmClass value. Default: 0x00
     */
    public int getEsmClass() {
        return _esmClass;
    }

    /**
     * Sets esmClass value.
     */
    public void setEsmClass(int value) {
        _esmClass = value;
    }

    /**
     * Gets protocolId value. Default: 0x00
     */
    public int getProtocolId() {
        return _protocolId;
    }

    /**
     * Sets protocolId value.
     */
    public void setProtocolId(int value) {
        _protocolId = value;
    }

    /**
     * Gets priorityFlag value. Default: 0x01
     */
    public int getPriorityFlag() {
        return _priorityFlag;
    }

    /**
     * Sets priorityFlag value.
     */
    public void setPriorityFlag(int value) {
        _priorityFlag = value;
    }

    /**
     * Gets registeredDelivery value. Default: 0x01
     */
    public int getRegisteredDelivery() {
        return _registeredDelivery;
    }

    /**
     * Sets registeredDelivery value.
     */
    public void setRegisteredDelivery(int value) {
        _registeredDelivery = value;
    }

    /**
     * Gets replaceIfPresentFlag value. Default: 0x00
     */
    public int getReplaceIfPresentFlag() {
        return _replaceIfPresentFlag;
    }

    /**
     * Sets replaceIfPresentFlag value.
     */
    public void setReplaceIfPresentFlag(int value) {
        _replaceIfPresentFlag = value;
    }

    /**
     * Gets dataCoding value. Default: 0x04
     */
    public int getDataCoding() {
        return _dataCoding;
    }

    /**
     * Sets dataCoding value.
     */
    public void setDataCoding(int value) {
        _dataCoding = value;
    }

    /**
     * Gets smDefaultMsgId value. Default: 0x01
     */
    public int getSmDefaultMsgId() {
        return _smDefaultMsgId;
    }

    /**
     * Sets smDefaultMsgId value.
     */
    public void setSmDefaultMsgId(int value) {
        _smDefaultMsgId = value;
    }

    /**
     * Gets callbackNum value.
     */
    public String getCallbackNum() {
        return _callbackNum;
    }

    /**
     * Sets callbackNum value.
     */
    public void setCallbackNum(String value) {
        _callbackNum = value;
    }

}
