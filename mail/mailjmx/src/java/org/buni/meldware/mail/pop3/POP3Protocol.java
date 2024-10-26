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
package org.buni.meldware.mail.pop3;

import java.lang.reflect.Method;
import java.util.Map;

import javax.naming.InitialContext;
import javax.net.ssl.SSLSocketFactory;

import org.buni.meldware.mail.Protocol;
import org.buni.meldware.mail.mailbox.MailboxService;
import org.buni.meldware.mail.pop3.handlers.POP3Handlers;
import org.buni.meldware.mail.userrepository.UserRepository;
import org.jboss.security.SecurityDomain;
import org.jboss.system.ServiceMBeanSupport;
import org.w3c.dom.Element;

/** 
 * POP3Protocol implements the POP3 Protocol for Server.  POP3 is the
 * protocol used to retrieve mails by org.jboss.org.buni.meldware.mail clients.
 *
 * @author Eric Daugherty
 * @author Andrew C. Oliver 
 * @version $Revision: 1.5 $
 */
public class POP3Protocol extends ServiceMBeanSupport implements
        POP3ProtocolMBean, POP3Constants {
    InitialContext ctx;

    Map handlers;

    Map properties;

    private MailboxService mailboxManager;

    /** The name of the security domain to use with server sockets that support SSL
     */
    protected String sslDomain;

    private String servername;

    private boolean tlsEnabled = false;

    private boolean tlsRequired = false;

    private boolean requireClientCert = false;

    private UserRepository userRepository;

    private UserRepository apopUserRepository;

    private SSLSocketFactory sslSocketFactory;

    /**
     * constructs an SMTPProtocol with no configuration
     */
    public POP3Protocol() {
        try {
            handlers = POP3Handlers.instance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getName() {
        return "POP3";
    }

    /* (non-Javadoc)
     * @see org.buni.meldware.mail.Protocol#getProperties()
     */
    //TODO: return properties as XML
    public Element getProperties() {
        return null;
    }

    public Protocol createInstance() {
        return new POP3ProtocolInstance(this, handlers, mailboxManager);
    }

    /** Set the JAAS security domain to be used with server sockets that support SSL
     * @param domainName The JNDI name of the security domain used with server sockets that support SSL
     */
    public void setSecurityDomain(String domainName) {
        try {
            log.info("pop3 security domain set to " + domainName);
            this.sslDomain = domainName;
        } catch (RuntimeException e) {
            log.error("Error", e);
            throw e;
        }
    }

    /** Get the JAAS security domain to be used with server sockets that support SSL
     * @return The JNDI name of the security domain used with server sockets that support SSL
     */
    public String getSecurityDomain() {
        return sslDomain;
    }

    protected void startService() throws Exception {
        log.debug("startService()");
        setupSSLSocketFactory();
    }

    /** Set up the SSLSocketFactory to be used to create the wrapper SSL Socket when
     * SSL/TLS is to be used
     */
    private void setupSSLSocketFactory() throws RuntimeException {
        log.info("setupSSLSocketFactory called.");
        //TODO: Duplicated from SMTPProtocol, refactor to common code?
        SSLSocketFactory sslSocketFactory = null;
        if (sslDomain != null) {
            log.info("setting up socket factory for domain " + sslDomain);
            ClassLoader loader = Thread.currentThread().getContextClassLoader();

            try {
                ///////////////////////////////////////////////////////////
                // Here we want to use an SSLSocketFactory that understands
                // security domains. org.jboss.security.ssl.DomainSocket
                // does but doesn't exist in 3.2.3. Another implementation
                // has been provided for 3.2.3 hence this "clutter"!
                //
                // Once 3.2.4 has taken over the alternative implementation
                // should be scrapped.

                try {
                    //This only exists in 3.2.4 and not in 3.2.3
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
            //If no security domain has been created, it makes no sense to
            //have enabled tls and/or set tls to required
            if (isTlsEnabled()) {
                throw new RuntimeException(
                        "tls-enabled was set to true, but no SecurityDomain was specified");
            } else {
                setTlsEnabled(false);
            }
        }
        log.info("socket factory is " + sslSocketFactory);
        setSslSocketFactory(sslSocketFactory);
    }

    /**
     * Sets mailbox for this POP protocol to use.
     * @param mailboxManager
     */
    public void setMailboxManager(MailboxService mailboxManager) {
        this.mailboxManager = mailboxManager;
    }

    public MailboxService getMailboxManager() {
        return this.mailboxManager;
    }

    /**
     * @return Returns the requireClientCert.
     */
    public boolean isRequireClientCert() {
        return requireClientCert;
    }

    /**
     * @param requireClientCert The requireClientCert to set.
     */
    public void setRequireClientCert(boolean requireClientCert) {
        this.requireClientCert = requireClientCert;
    }

    /**
     * @return Returns the servername.
     */
    public String getServername() {
        return servername;
    }

    /**
     * @param servername The servername to set.
     */
    public void setServername(String servername) {
        this.servername = servername;
    }

    /**
     * @return Returns the tlsEnabled.
     */
    public boolean isTlsEnabled() {
        return tlsEnabled;
    }

    /**
     * @param tlsEnabled The tlsEnabled to set.
     */
    public void setTlsEnabled(boolean tlsEnabled) {
        this.tlsEnabled = tlsEnabled;
    }

    public boolean isRequireTls() {
        return this.tlsRequired;
    }

    public void setRequireTls(boolean requireTls) {
        this.tlsRequired = requireTls;
    }

    public UserRepository getAPOPUserRepository() {
        return apopUserRepository;
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }

    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void setAPOPUserRepository(UserRepository userRepository) {
        this.apopUserRepository = userRepository;
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

    public Class getProtocolInterfaceClass() {
        return POP3ProtocolMBean.class;
    }

    public String getSslDomain() {
        return this.getSecurityDomain();
    }

    public void setSslDomain(String sslDomain) {
        this.setSecurityDomain(sslDomain);
    }

}
