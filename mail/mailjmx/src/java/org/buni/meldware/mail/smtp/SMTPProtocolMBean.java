/*
 * Bunisoft the Open Source Communications Company
 * Copyright 2006, Bunisoft Inc.,
 *
 * Portions of this software are Copyright 2006, JBoss Inc., and 
 * individual contributors as indicated by the @authors tag.
 * See the copyright.txt in the distribution for a full listing of
 * individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.buni.meldware.mail.smtp;

import javax.net.ssl.SSLSocketFactory;

import org.buni.meldware.mail.MailListenerChain;
import org.buni.meldware.mail.ProtocolFactory;
import org.buni.meldware.mail.domaingroup.DomainGroupMBean;
import org.buni.meldware.mail.message.MailBodyManager;
import org.buni.meldware.mail.protocol.ProtocolSettings;
import org.buni.meldware.mail.userrepository.UserRepository;

/**
 * SMTPProtocol is used to send mails.  We've implemented the Server side of it.  JavaMail implements 
 * ONLY the client side of it... 
 *
 * @author Andrew C. Oliver
 * @version $Revision: 1.4 $
 */
public interface SMTPProtocolMBean extends ProtocolSettings, ProtocolFactory {

    /**
     * used by the XMBean stuff to set up our properties (see sample)
     * @param properties Element from JBoss
     */
    //void setProperties(Element properties);
    /**
     * return the proeprties as a DOM object
     * @return Element containing our properties...
     */
    //Element getProperties();
    /**
     * set the domain group that we depend on for VerifyIdentiy and other such properties
     * @param domainGroup
     */
    void setDomainGroup(DomainGroupMBean domainGroup);

    /**
     * @return the domain group that we depend on for VerifyIdentity.
     */
    DomainGroupMBean getDomainGroup();

    /**
     * set the mail listener chain which will handle this protocol
     * @param chain
     */
    public void setListenerChain(MailListenerChain chain);

    /**
     * get the mail listener chain which handles this protocol
     * @return chain
     */
    public MailListenerChain getListenerChain();

    public SMTPProtocolInstance createInstance();

    /**
     * Set the mail body manager for the SMTP protocol to use.
     * 
     * @return
     */
    public MailBodyManager getMailBodyManager();

    public void setMailBodyManager(MailBodyManager mailBodyManager);

    /**
     * @return Returns the authMethods.
     */
    public abstract String getAuthMethods();

    /**
     * @param authMethods The authMethods to set.
     */
    public abstract void setAuthMethods(String authMethods);

    /**
     * @return Returns the authRequired.
     */
    public abstract boolean isAuthRequired();

    /**
     * @param authRequired The authRequired to set.
     */
    public abstract void setAuthRequired(boolean authRequired);

    /**
     * @return Returns the authAllowed
     */
    public abstract boolean isAuthAllowed();

    /**
     * @param authAllowed The authAllowed to set.
     */
    public abstract void setAuthAllowed(boolean authRequired);

    /**
     * @return Returns the blockSize.
     */
    public abstract int getBlockSize();

    /**
     * @param blockSize The blockSize to set.
     */
    public abstract void setBlockSize(int blockSize);

    /**
     * @return Returns the maxMessageSize.
     */
    public abstract int getMaxMessageSize();

    /**
     * @param maxMessageSize The maxMessageSize to set.
     */
    public abstract void setMaxMessageSize(int maxMessageSize);

    /**
     * @return Returns the maxOwnReceivedHdrs.
     */
    public abstract int getMaxOwnReceivedHdrs();

    /**
     * @param maxOwnReceivedHdrs The maxOwnReceivedHdrs to set.
     */
    public abstract void setMaxOwnReceivedHdrs(int maxOwnReceivedHdrs);

    /**
     * @return Returns the maxRecievedHdrs.
     */
    public abstract int getMaxReceivedHdrs();

    /**
     * @param maxRecievedHdrs The maxRecievedHdrs to set.
     */
    public abstract void setMaxReceivedHdrs(int maxRecievedHdrs);

    /**
     * @return Returns the postMaster.
     */
    public abstract String getPostmaster();

    /**
     * @param postMaster The postMaster to set.
     */
    public abstract void setPostmaster(String postMaster);

    /**
     * @return Returns the receivedHdrsThreshold.
     */
    public abstract int getReceivedHdrsThreshold();

    /**
     * @param receivedHdrsThreshold The receivedHdrsThreshold to set.
     */
    public abstract void setReceivedHdrsThreshold(int receivedHdrsThreshold);

    /**
     * @return Returns the requireTlsForAuth.
     */
    public abstract boolean isRequireTlsForAuth();

    /**
     * @param requireTlsForAuth The requireTlsForAuth to set.
     */
    public abstract void setRequireTlsForAuth(boolean requireTlsForAuth);

    /**
     * @return Returns the verifyIdentity.
     */
    public abstract boolean isVerifyIdentity();

    /**
     * @param verifyIdentity The verifyIdentity to set.
     */
    public abstract void setVerifyIdentity(boolean verifyIdentity);

    /**
     * Determines if unauthenticated clients are able relay using
     * this server if their IP falls within a specified range of
     * ip addresses.
     * 
     * @return
     */
    boolean isRelayByAddr();

    void setRelayByAddr(boolean relayByIp);

    /**
     * Determines if unauthenticated clients are able to relay using
     * this server if their domain name falls within a specified range
     * of domain names.  It is recommended to use ip addresses over
     * domains, as using this will force a reverse domain lookup for
     * every relay request.
     * 
     * @return
     */
    boolean isRelayByDomain();

    void setRelayByDomain(boolean relayByDomain);

    /**
     * Set the domain group which relay client must be part of.
     * 
     * @return
     */
    DomainGroupMBean getRelayDomainGroup();

    void setRelayDomainGroup(DomainGroupMBean domainGroup);

    SSLSocketFactory getSslSocketFactory();

    void setSslSocketFactory(SSLSocketFactory sslSocketFactory);
    
    //methods also in protocol settings generic interface that must be repeated due to mbeans
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
