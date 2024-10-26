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

import java.lang.reflect.Method;
import java.util.Map;

import javax.naming.InitialContext;
import javax.net.ssl.SSLSocketFactory;

import org.buni.meldware.mail.MailListenerChain;
import org.buni.meldware.mail.domaingroup.DomainGroupMBean;
import org.buni.meldware.mail.message.MailBodyManager;
import org.buni.meldware.mail.smtp.handlers.SMTPHandlers;
import org.buni.meldware.mail.userrepository.UserRepository;
import org.jboss.security.SecurityDomain;
import org.jboss.system.ServiceMBeanSupport;
import org.w3c.dom.Element;

/**
 * SMTPProtocol implements the SMTP Protocol for Server. SMTP is the protocol
 * used to receive mails from org.jboss.org.buni.meldware.mail clients and servers.
 * 
 * @author Andrew C. Oliver
 * @version $Revision: 1.4 $
 */
public class SMTPProtocol extends ServiceMBeanSupport implements SMTPConstants,
        SMTPProtocolMBean { 

    Map handlers;

    //private static final String SENDER = "SENDER";

    //Map properties;
    //private boolean propsconfigured;

    private DomainGroupMBean domainGroup;

    private MailBodyManager mailBodyManager;

    private UserRepository userRepository;

    private String servername;

    private boolean authRequired;

    private boolean authAllowed;

    private String authMethods;

    private boolean verifyIdentity;

    private int maxMessageSize;

    private int blockSize;

    private boolean requireTls;

    private boolean requireTlsForAuth;

    private boolean requireClientCert;

    private boolean tlsEnabled;

    private String postMaster;

    private int maxReceivedHdrs;

    private int receivedHdrsThreshold;

    private int maxOwnReceivedHdrs;

    private boolean relayByAddr;

    private boolean relayByDomain;

    private DomainGroupMBean relayDomainGroup;

    private SSLSocketFactory sslSocketFactory;

    /**
     * The name of the security domain to use with server sockets that support
     * SSL
     */
    protected String sslDomain;

    private MailListenerChain chain;

    /**
     * constructs an SMTPProtocol with no configuration
     */
    public SMTPProtocol() {
        handlers = SMTPHandlers.instance();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jboss.system.ServiceMBean#getName()
     */
    public String getName() {
        return "SMTP";
    }

    // documented in super class

    // TODO: return properties as XML
    public Element getProperties() {
        return null;
    }

    public SMTPProtocolInstance createInstance() {
        return new SMTPProtocolInstance(this, handlers, getMailBodyManager());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.buni.meldware.mail.smtp.SMTPProtocolMBean#setDomainGroup(javax.management.ObjectName)
     */
    public void setDomainGroup(DomainGroupMBean domainGroup) {
        this.domainGroup = domainGroup;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.buni.meldware.mail.smtp.SMTPProtocolMBean#getDomainGroup()
     */
    public DomainGroupMBean getDomainGroup() {
        return domainGroup;
    }

    /**
     * Set the JAAS security domain to be used with server sockets that support
     * SSL
     * 
     * @param domainName
     *            The JNDI name of the security domain used with server sockets
     *            that support SSL
     */
    public void setSecurityDomain(String domainName) {
        try {
            log.debug("setSecurityDomain()");
            this.sslDomain = domainName;
        } catch (RuntimeException e) {
            log.error("Error", e);
            throw e;
        }
    }

    /**
     * Get the JAAS security domain to be used with server sockets that support
     * SSL
     * 
     * @return The JNDI name of the security domain used with server sockets
     *         that support SSL
     */
    public String getSecurityDomain() {
        return sslDomain;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jboss.system.ServiceMBeanSupport#startService()
     */
    protected void startService() throws Exception {
        log.debug("startService()");
        setupSSLSocketFactory();
    }

    /**
     * Set up the SSLSocketFactory to be used to create the wrapper SSL Socket
     * when SSL/TLS is to be used
     */
    /**
     * @throws RuntimeException
     */
    private void setupSSLSocketFactory() throws RuntimeException {
        SSLSocketFactory sslSocketFactory = null;
        if (sslDomain != null) {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();

            try {
                // /////////////////////////////////////////////////////////
                // Here we want to use an SSLSocketFactory that understands
                // security domains. org.jboss.security.ssl.DomainSocket
                // does but doesn't exist in 3.2.3. Another implementation
                // has been provided for 3.2.3 hence this "clutter"!
                //
                // Once 3.2.4 has taken over the alternative implementation
                // should be scrapped.

                try {
                    // This only exists in 3.2.4 and not in 3.2.3
                    Class sfClass = loader
                            .loadClass("org.jboss.security.ssl.DomainSocketFactory");
                    sslSocketFactory = (SSLSocketFactory) sfClass.newInstance();
                } catch (Exception e) {
                    throw new RuntimeException(
                            "Could not create SSLSocketFactory", e);
                }

                Class<?> sfClass = sslSocketFactory.getClass();
                InitialContext ctx = new InitialContext();
                SecurityDomain domain = (SecurityDomain) ctx.lookup(sslDomain);
                Class[] parameterTypes = { SecurityDomain.class };
                Method m = sfClass.getMethod("setSecurityDomain",
                        parameterTypes);
                Object[] args = { domain };
                m.invoke(sslSocketFactory, args);

            } catch (NoSuchMethodException e) {
                log
                        .error("Socket factory does not support setSecurityDomain(SecurityDomain)");
                throw new RuntimeException("Could not set up security for TLS",
                        e);
            } catch (Exception e) {
                log.error("Failed to setSecurityDomain=" + sslDomain
                        + " on socket factory", e);
                throw new RuntimeException("Could not set up security for TLS",
                        e);
            }
        } else {
            // If no security domain has been created, it makes no sense to
            // have enabled tls and/or set tls to required
            //Boolean prop = (Boolean) properties.get(SMTPConstants.TLS_SUPPORT);
            //if (prop != null && prop.booleanValue()) {
            if (isTlsEnabled()) {
                throw new RuntimeException(
                        "tls-enabled was set to true, but no SecurityDomain was specified");
            }

            //prop = (Boolean) properties.get(SMTPConstants.REQUIRE_STARTTLS);
            //prop = new Boolean()
            //if (prop != null && prop.booleanValue()) {
            if (isRequireTls()) {
                throw new RuntimeException(
                        "requireSTARTTLS was set to true, but no SecurityDomain was specified");
            }
        }
        setSslSocketFactory(sslSocketFactory);
        //properties.put(SMTPConstants.SSL_SOCKET_FACTORY, sslSocketFactory);
    }

    public void setListenerChain(MailListenerChain chain) {
        this.chain = chain;
    }

    public MailListenerChain getListenerChain() {
        return chain;
    }

    public MailBodyManager getMailBodyManager() {
        return this.mailBodyManager;
    }

    public void setMailBodyManager(MailBodyManager mailBodyManager) {
        this.mailBodyManager = mailBodyManager;
    }

    /* (non-Javadoc)
     * @see org.buni.meldware.mail.smtp.Props#getAuthMethods()
     */
    public String getAuthMethods() {
        return authMethods;
    }

    /* (non-Javadoc)
     * @see org.buni.meldware.mail.smtp.Props#setAuthMethods(java.lang.String)
     */
    public void setAuthMethods(String authMethods) {
        this.authMethods = authMethods;
    }

    /* (non-Javadoc)
     * @see org.buni.meldware.mail.smtp.Props#isAuthAllowed()
     */
    public boolean isAuthAllowed() {
        return authAllowed;
    }

    /* (non-Javadoc)
     * @see org.buni.meldware.mail.smtp.Props#setAuthAllowed(boolean)
     */
    public void setAuthAllowed(boolean authAllowed) {
        this.authAllowed = authAllowed;
    }

    /* (non-Javadoc)
     * @see org.buni.meldware.mail.smtp.Props#isAuthRequired()
     */
    public boolean isAuthRequired() {
        return authRequired;
    }

    /* (non-Javadoc)
     * @see org.buni.meldware.mail.smtp.Props#setAuthRequired(boolean)
     */
    public void setAuthRequired(boolean authRequired) {
        this.authRequired = authRequired;
    }

    /* (non-Javadoc)
     * @see org.buni.meldware.mail.smtp.Props#getBlockSize()
     */
    public int getBlockSize() {
        return blockSize;
    }

    /* (non-Javadoc)
     * @see org.buni.meldware.mail.smtp.Props#setBlockSize(int)
     */
    public void setBlockSize(int blockSize) {
        this.blockSize = blockSize;
    }

    /* (non-Javadoc)
     * @see org.buni.meldware.mail.smtp.Props#getMaxMessageSize()
     */
    public int getMaxMessageSize() {
        return maxMessageSize;
    }

    /* (non-Javadoc)
     * @see org.buni.meldware.mail.smtp.Props#setMaxMessageSize(int)
     */
    public void setMaxMessageSize(int maxMessageSize) {
        this.maxMessageSize = maxMessageSize;
    }

    /* (non-Javadoc)
     * @see org.buni.meldware.mail.smtp.Props#getMaxOwnReceivedHdrs()
     */
    public int getMaxOwnReceivedHdrs() {
        return maxOwnReceivedHdrs;
    }

    /* (non-Javadoc)
     * @see org.buni.meldware.mail.smtp.Props#setMaxOwnReceivedHdrs(int)
     */
    public void setMaxOwnReceivedHdrs(int maxOwnReceivedHdrs) {
        this.maxOwnReceivedHdrs = maxOwnReceivedHdrs;
    }

    /* (non-Javadoc)
     * @see org.buni.meldware.mail.smtp.Props#getMaxRecievedHdrs()
     */
    public int getMaxReceivedHdrs() {
        return maxReceivedHdrs;
    }

    /* (non-Javadoc)
     * @see org.buni.meldware.mail.smtp.Props#setMaxRecievedHdrs(int)
     */
    public void setMaxReceivedHdrs(int maxReceivedHdrs) {
        this.maxReceivedHdrs = maxReceivedHdrs;
    }

    /* (non-Javadoc)
     * @see org.buni.meldware.mail.smtp.Props#getPostMaster()
     */
    public String getPostmaster() {
        return postMaster;
    }

    /* (non-Javadoc)
     * @see org.buni.meldware.mail.smtp.Props#setPostMaster(java.lang.String)
     */
    public void setPostmaster(String postMaster) {
        this.postMaster = postMaster;
    }

    /* (non-Javadoc)
     * @see org.buni.meldware.mail.smtp.Props#getReceivedHdrsThreshold()
     */
    public int getReceivedHdrsThreshold() {
        return receivedHdrsThreshold;
    }

    /* (non-Javadoc)
     * @see org.buni.meldware.mail.smtp.Props#setReceivedHdrsThreshold(int)
     */
    public void setReceivedHdrsThreshold(int receivedHdrsThreshold) {
        this.receivedHdrsThreshold = receivedHdrsThreshold;
    }

    /* (non-Javadoc)
     * @see org.buni.meldware.mail.smtp.Props#isRequireClientCert()
     */
    public boolean isRequireClientCert() {
        return requireClientCert;
    }

    /* (non-Javadoc)
     * @see org.buni.meldware.mail.smtp.Props#setRequireClientCert(boolean)
     */
    public void setRequireClientCert(boolean requireClientCert) {
        this.requireClientCert = requireClientCert;
    }

    /* (non-Javadoc)
     * @see org.buni.meldware.mail.smtp.Props#isRequireTls()
     */
    public boolean isRequireTls() {
        return requireTls;
    }

    /* (non-Javadoc)
     * @see org.buni.meldware.mail.smtp.Props#setRequireTls(boolean)
     */
    public void setRequireTls(boolean requireTls) {
        this.requireTls = requireTls;
    }

    /* (non-Javadoc)
     * @see org.buni.meldware.mail.smtp.Props#isRequireTlsForAuth()
     */
    public boolean isRequireTlsForAuth() {
        return requireTlsForAuth;
    }

    /* (non-Javadoc)
     * @see org.buni.meldware.mail.smtp.Props#setRequireTlsForAuth(boolean)
     */
    public void setRequireTlsForAuth(boolean requireTlsForAuth) {
        this.requireTlsForAuth = requireTlsForAuth;
    }

    /* (non-Javadoc)
     * @see org.buni.meldware.mail.smtp.Props#getServername()
     */
    public String getServername() {
        return servername;
    }

    /* (non-Javadoc)
     * @see org.buni.meldware.mail.smtp.Props#setServername(java.lang.String)
     */
    public void setServername(String servername) {
        this.servername = servername;
    }

    /* (non-Javadoc)
     * @see org.buni.meldware.mail.smtp.Props#getSslDomain()
     */
    public String getSslDomain() {
        return sslDomain;
    }

    /* (non-Javadoc)
     * @see org.buni.meldware.mail.smtp.Props#setSslDomain(java.lang.String)
     */
    public void setSslDomain(String sslDomain) {
        this.sslDomain = sslDomain;
    }

    /* (non-Javadoc)
     * @see org.buni.meldware.mail.smtp.Props#isTlsEnabled()
     */
    public boolean isTlsEnabled() {
        return tlsEnabled;
    }

    /* (non-Javadoc)
     * @see org.buni.meldware.mail.smtp.Props#setTlsEnabled(boolean)
     */
    public void setTlsEnabled(boolean tlsEnabled) {
        this.tlsEnabled = tlsEnabled;
    }

    /* (non-Javadoc)
     * @see org.buni.meldware.mail.smtp.Props#getUserRepository()
     */
    public UserRepository getUserRepository() {
        return userRepository;
    }

    /* (non-Javadoc)
     * @see org.buni.meldware.mail.smtp.Props#setUserRepository(org.buni.meldware.mail.userrepository.UserRepository)
     */
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /* (non-Javadoc)
     * @see org.buni.meldware.mail.smtp.Props#isVerifyIdentity()
     */
    public boolean isVerifyIdentity() {
        return verifyIdentity;
    }

    /* (non-Javadoc)
     * @see org.buni.meldware.mail.smtp.Props#setVerifyIdentity(boolean)
     */
    public void setVerifyIdentity(boolean verifyIdentity) {
        this.verifyIdentity = verifyIdentity;
    }

    /**
     * @return Returns the relayByAddr.
     */
    public boolean isRelayByAddr() {
        return relayByAddr;
    }

    /**
     * @param relayByAddr The relayByAddr to set.
     */
    public void setRelayByAddr(boolean relayByAddr) {
        this.relayByAddr = relayByAddr;
    }

    /**
     * @return Returns the relayByDomain.
     */
    public boolean isRelayByDomain() {
        return relayByDomain;
    }

    /**
     * @param relayByDomain The relayByDomain to set.
     */
    public void setRelayByDomain(boolean relayByDomain) {
        this.relayByDomain = relayByDomain;
    }

    /**
     * @return Returns the relayDomainGroup.
     */
    public DomainGroupMBean getRelayDomainGroup() {
        return relayDomainGroup;
    }

    /**
     * @param relayDomainGroup The relayDomainGroup to set.
     */
    public void setRelayDomainGroup(DomainGroupMBean relayDomainGroup) {
        this.relayDomainGroup = relayDomainGroup;
    }

    /**
     * @return Returns the sslSocketFactory.
     */
    public SSLSocketFactory getSslSocketFactory() {
        return sslSocketFactory;
    }

    /**
     * @param sslSocketFactory The sslSocketFactory to set.
     */
    public void setSslSocketFactory(SSLSocketFactory sslSocketFactory) {
        this.sslSocketFactory = sslSocketFactory;
    }

    public static class State {
        public final static String USER = "USER";
    }

    public Class getProtocolInterfaceClass() {
        return SMTPProtocolMBean.class;
    }
}
