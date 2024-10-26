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
package org.buni.meldware.mail.smtp.sender;

import java.net.UnknownHostException;

import org.buni.meldware.mail.message.Mail;
import org.buni.meldware.mail.message.MailAddress;
import org.buni.meldware.mail.message.MailBodyManager;
import org.jboss.system.ServiceMBean;
import org.w3c.dom.Element;

/**
 * SMTPSender MBean provides the SMTP client funcionality for sending mails to other servers.  Messages that must be
 * sent remotely must be sent via this mbean to another SMTP server.  Potentially it may relay them as well.  In the 
 * present version we only intend to allow authenticated local users to relay messages.  Later we'll allow defined 
 * relays.  
 * @todo revise this comment when appropriate (12-31-03)
 * @author Andrew C. Oliver
 */
public interface SMTPSenderMBean extends ServiceMBean {

    /**
     * get the DNS routes to use when sending mails
     * @return  routes Mbean names of route instances
     */
    Element getDNSRoutes() throws Exception;

    /**
     * @return DNS servers to use
     */
    Element getDNSServers();

    /**
     * set the DNS routes to use when sending mails
     * @param routes Mbean names of route instances
     */
    void setDNSRoutes(Element routes) throws Exception;

    /**
     * set the DNS servers to use for looking up MX records
     * @param servers
     */
    void setDNSServers(Element servers) throws Exception;

    /**
     * add a DNS server
     * @param dns server name
     * @see #setDNSServers(Element)
     * @throws RuntimeException in the event the server cannot be resolved
     */
    void addDNSServer(String dns) throws UnknownHostException;

    /**
     * add a DNS route
     * @param Route MBean Name
     */
    void addDNSRoute(String dnsRoute);

    /**
     * remove a DNS server from the list
     * @param dns server name
     * @throws RuntimeException if the server isn't used
     */
    void removeDNSServer(String dns);

    /**
     * remove a DNS route from the list
     * @param Route MBean Name 
     */
    void removeDNSRoute(String dnsRoute);

    /**
     * enable the DNS cache
     * @param enabled boolean value 
     */
    void setCacheEnabled(boolean enabled);

    /**
     * @return is the DNS cache enabled?
     */
    boolean getCacheEnabled();

    /**
     * lookup the 
     * @return ...
     */
    public String[] mxLookup(String address);

    void clearDNSCache();

    /**
     * send mail to all of its receipients except those listed
     * @param message to send
     * @param exclude list of receipients not to send it to (Mail.getTo() - excluded = sendees) 
     * @return result of sending
     */
    SMTPResult[] send(Mail message, MailAddress[] exclude);

    //void testSend(String to, String from, String subject, String message);

    /** Send mail to one recipient 
     * @param to recipient to receive the mail
     * @param message message to be sent
     */
    SMTPResult send(MailAddress to, Mail message);

    /**
     * List the configured DNS servers in 
     * comma separated form
     * @return
     */
    String listDNSServers();

    /**
     * @return allow direct TCP/IP lookups (non-MX) as a fallback
     */
    public boolean getAllowAddressLookups();

    /**
     * @param allow direct TCP/IP lookups (non-MX) as a fallback
     */
    public void setAllowAddressLookups(boolean allowAddressLookups);
    
    void setMailBodyManager(MailBodyManager mgr);
    MailBodyManager getMailBodyManager();

}
