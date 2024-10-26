package org.buni.meldware.mail.protocol;

import org.buni.meldware.mail.userrepository.UserRepository;
import org.jboss.system.ServiceMBean;


public interface ProtocolSettings extends ServiceMBean {
    
    Class getProtocolInterfaceClass();
    
    /**
     * @return Returns the sslDomain.
     */
    public abstract String getSslDomain();

    /**
     * @param sslDomain The sslDomain to set.
     */
    public abstract void setSslDomain(String sslDomain);
    
    /**
     * @return Returns the requireClientCert.
     */
    public abstract boolean isRequireClientCert();

    /**
     * @param requireClientCert The requireClientCert to set.
     */
    public abstract void setRequireClientCert(boolean requireClientCert);

    /**
     * @return Returns the requireTls.
     */
    public abstract boolean isRequireTls();

    /**
     * @param requireTls The requireTls to set.
     */
    public abstract void setRequireTls(boolean requireTls);
    
    /**
     * @return Returns the tlsEnabled.
     */
    public boolean isTlsEnabled();

    /**
     * @param tlsEnabled The tlsEnabled to set.
     */
    public void setTlsEnabled(boolean tlsEnabled);
    
    /**
     * @return Returns the userRepository.
     */
    public abstract UserRepository getUserRepository();

    /**
     * @param userRepository The userRepository to set.
     */
    public abstract void setUserRepository(UserRepository userRepository);
    
    /**
     * @return Returns the servername.
     */
    public abstract String getServername();

    /**
     * @param servername The servername to set.
     */
    public abstract void setServername(String servername);

}
