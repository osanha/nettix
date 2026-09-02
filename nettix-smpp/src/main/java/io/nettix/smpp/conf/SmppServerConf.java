
package io.nettix.smpp.conf;


/**
 * SMS server configuration
 */
public class SmppServerConf {

    /**
     * Server address
     */
    private final String _address;

    /**
     * Port to connect for receiving MO. If same as MT port, uses Transceiver mode with single channel.
     */
    private final int _moPort;

    /**
     * Port to connect for sending MT. If same as MO port, uses Transceiver mode with single channel.
     */
    private final int _mtPort;

    /**
     * Keep-alive check interval (seconds)
     */
    private int _enquireLink = 10;

    /**
     * Delay before reconnection attempt when disconnected (seconds)
     */
    private int _reconnDelay = 5;

    /**
     * Reconnection retry interval after connection failure (seconds)
     */
    private int _connInterval = 1;

    /**
     * Connection timeout (seconds)
     */
    private int _connTimeout = 30;

    /**
     * Response PDU timeout
     */
    private int _respTimeout = 30;

    /**
     * Constructor.
     *
     * <b>Note:</b> If MO/MT ports are the same, uses Transceiver mode with single channel.
     *
     * @param address server address
     * @param moPort port to connect for receiving MO
     * @param mtPort port to connect for sending MT
     */
    public SmppServerConf(String address, int moPort, int mtPort) {
        _address = address;
        _moPort = moPort;
        _mtPort = mtPort;
    }

    /**
     * Sets the response PDU timeout.
     *
     * @param respTimeout response timeout (seconds)
     */
    public void setRespTimeout(int respTimeout) {
        _respTimeout = respTimeout;
    }

    /**
     * Gets the response PDU timeout.
     *
     * @return response PDU timeout
     */
    public int getRespTimeout() {
        return _respTimeout;
    }

    /**
     * Returns the server address.
     */
    public String getAddress() {
        return _address;
    }

     /**
     * Returns the port for receiving MO.
     */
    public int getMoPort() {
        return _moPort;
    }

    /**
     * Returns the port for sending MT.
     */
    public int getMtPort() {
        return _mtPort;
    }

    /**
     * Returns the keep-alive check interval (seconds)
     */
    public int getEnquireLink() {
        return _enquireLink;
    }

    /**
     * Sets the keep-alive check interval (seconds)
     */
    public void setEnquireLink(int value) {
        _enquireLink = value;
    }

    /**
     * Returns the delay before reconnection attempt when disconnected (seconds)
     */
    public int getReconnDelay() {
        return _reconnDelay;
    }

    /**
     * Sets the delay before reconnection attempt when disconnected (seconds)
     */
    public void setReconnDelay(int value) {
        _reconnDelay = value;
    }

    /**
     * Returns the reconnection retry interval (seconds)
     */
    public int getConnInterval() {
        return _connInterval;
    }

    /**
     * Sets the reconnection retry interval (seconds)
     */
    public void setConnInteral(int value) {
        _connInterval = value;
    }

    /**
     * Returns the connection timeout (seconds)
     */
    public int getConnTimeout() {
        return _connTimeout;
    }

    /**
     * Sets the connection timeout (seconds)
     */
    public void setConnTimeout(int value) {
        _connTimeout = value;
    }

}
