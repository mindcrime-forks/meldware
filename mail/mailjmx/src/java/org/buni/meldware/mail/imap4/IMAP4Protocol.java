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
package org.buni.meldware.mail.imap4;

import java.util.Map;

import org.buni.meldware.mail.Protocol;
import org.buni.meldware.mail.ProtocolTimer;
import org.buni.meldware.mail.mailbox.MailboxService;
import org.buni.meldware.mail.userrepository.UserRepository;
import org.jboss.system.ServiceMBeanSupport;
import org.w3c.dom.Element;

/**
 * IMAP4Protocol implements the IMAP4 Protocol for Server. IMAP4 is the protocol
 * used to retrieve and manage mailboxes, folders and mails by
 * org.jboss.org.buni.meldware.mail clients.
 * 
 * @author Eric Daugherty
 * @author Thorsten Kunz
 * @version $Revision: 1.7 $
 */
public class IMAP4Protocol extends ServiceMBeanSupport implements
        IMAP4ProtocolMBean, IMAP4Constants {
    Map handlers;

    Map properties;
    final ProtocolTimer timer = new ProtocolTimer();
    boolean timingEnabled = true;

    private MailboxService mailboxManager;

    /**
     * The name of the security domain to use with server sockets that support
     * SSL
     */
    protected String sslDomain;

    private String servername;

    private boolean tlsEnabled;

    private boolean requireClientCert;

    private UserRepository userRepository;

    private boolean requireTls;

    /**
     * constructs an SMTPProtocol with no configuration
     */
    public IMAP4Protocol() {
    }

    public String getName() {
        return "IMAP4";
    }

    // documented in super class

    /*
     * (non-Javadoc)
     * 
     * @see org.buni.meldware.mail.Protocol#getProperties()
     */
    // TODO: return properties as XML
    public Element getProperties() {
        return null;
    }

    public Protocol createInstance() {
        return new IMAP4ProtocolInstance(this, handlers, mailboxManager);
    }

    /**
     * Set the JAAS security domain to be used with server sockets that support
     * SSL
     * 
     * @param domainName The JNDI name of the security domain used with server
     *           sockets that support SSL
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
     * @return The JNDI name of the security domain used with server sockets that
     *         support SSL
     */
    public String getSecurityDomain() {
        return sslDomain;
    }

    protected void startService() throws Exception {
        log.debug("startService()");
        //setupSSLSocketFactory();
    }

    /**
     * Sets mailbox for this POP protocol to use.
     * 
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

    /**
     * @return Returns the userRepository.
     */
    public UserRepository getUserRepository() {
        return userRepository;
    }

    /**
     * @param userRepository The userRepository to set.
     */
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Class getProtocolInterfaceClass() {
        return IMAP4ProtocolMBean.class;
    }

    public String getSslDomain() {
        return this.getSecurityDomain();
    }

    public boolean isRequireTls() {
        return requireTls;
    }

    public void setRequireTls(boolean requireTls) {
        this.requireTls = requireTls;
    }

    public void setSslDomain(String sslDomain) {
        this.setSecurityDomain(sslDomain);
    }
    
    
    public String printPerformanceStats() {
        if (timingEnabled) {
            return timer.printPerformanceStats();
        } else {
            return "Timing Disabled";
        }
    }

    public void addTiming(String cmdName, String commandStr, long l) {
        if (timingEnabled) {
            timer.addTiming(cmdName, commandStr, l);
        }
    }

    public boolean isTimingEnabled() {
        return timingEnabled;
    }

    public void setTimingEnabled(boolean timingEnabled) {
        this.timingEnabled = timingEnabled;
    }
}
