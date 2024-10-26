package org.buni.meldware.mail.userrepository.jaas;

import javax.security.auth.callback.Callback;

public class ConfigurationCallback implements Callback {

    private String hostname;
    private String mechanism;
    private String protocol;
    /**
     * @return the hostname
     */
    public String getHostname() {
        return hostname;
    }
    /**
     * @param hostname the hostname to set
     */
    public void setHostname(String hostname) {
        this.hostname = hostname;
    }
    /**
     * @return the mechanism
     */
    public String getMechanism() {
        return mechanism;
    }
    /**
     * @param mechanism the mechanism to set
     */
    public void setMechanism(String mechanism) {
        this.mechanism = mechanism;
    }
    /**
     * @return the protocol
     */
    public String getProtocol() {
        return protocol;
    }
    /**
     * @param protocol the protocol to set
     */
    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }
    
    
}
