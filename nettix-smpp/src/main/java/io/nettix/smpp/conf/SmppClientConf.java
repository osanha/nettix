
package io.nettix.smpp.conf;

/**
 * SMS client configuration
 */
public class SmppClientConf
{
    private final SmppServerConf _serverConf;

    private final SmppBindConf _bindConf;

    private final SmppSubmitConf _submitConf;

    public SmppClientConf(SmppServerConf serverConf, SmppBindConf bindConf, SmppSubmitConf submitConf) {
        _serverConf = serverConf;
        _bindConf = bindConf;
        _submitConf = submitConf;
    }

    /**
     * Gets the SMSC configuration.
     */
    public SmppServerConf serverConf() {
        return _serverConf;
    }

    /**
     * Gets the bind configuration.
     */
    public SmppBindConf bindConf() {
        return _bindConf;
    }

    /**
     * Gets the submit configuration.
     */
    public SmppSubmitConf submitConf() {
        return _submitConf;
    }
}
