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
package org.buni.meldware.mail.fetchmail;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Header;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.InternetAddress;

import org.buni.meldware.mail.MailListenerChain;
import org.buni.meldware.mail.message.Mail;
import org.buni.meldware.mail.message.MailAddress;
import org.buni.meldware.mail.message.MailBodyManager;
import org.buni.meldware.mail.message.MailCreateListener;
import org.buni.meldware.mail.smtp.SMTPConstants;
import org.buni.meldware.mail.smtp.SMTPProtocolInstance;
import org.buni.meldware.mail.smtp.SMTPProtocolMBean;
import org.jboss.logging.Logger;
import org.jboss.system.ServiceMBean;
import org.jboss.system.ServiceMBeanSupport;

/**
 * Fetchmail over POP3 Deployment descriptor for this MBean: <![CDATA[ <mbean
 * code="org.buni.meldware.mail.fetchmail.Popper"
 * name="meldware.mail:type=Fetchmail,name=Popper"> <attribute
 * name="Servername">localhost</attribute> <attribute name="PopPort">110</attribute>
 * <attribute name="PopUser">heiko-pop</attribute> <attribute
 * name="Password">secret</attribute> <attribute
 * name="LocalUser">heiko-local@localhost</attribute> <attribute
 * name="DeleteAfterPop">true</attribute> <depends
 * optional-attribute-name="SMTPProtocol"
 * proxy-type="org.buni.meldware.mail.smtp.SMTPProtocol">meldware.mail:type=MailServices,name=SMTPProtocol</depends>
 * </mbean> ]]>
 * 
 * @author hwr@pilhuhn.de
 * @author acoliver@jboss.org
 * @version $Revision: 1.2 $
 */
public class Popper extends ServiceMBeanSupport implements PopperMBean,
        ServiceMBean, SMTPConstants {

    private static final Logger log = Logger.getLogger(Popper.class);

    private String popUser;

    private String pass;

    private String popHost;

    private int popPort;

    private String localUser;

    private boolean doDeleteAfterPop;

    private SMTPProtocolMBean smtp;

    /**
     * Try to fetch mail from a remote pop account and put it on our internal
     * delivery queue.
     */
    public void pop() {

        Properties p = new Properties();
        p.put("mail.host", popHost);
        p.put("mail.user", popUser);
        p.put("mail.store.protocol", "pop3");
        p.put("mail.pop3.host", popHost);

        Session ses = Session.getInstance(p);
        if (ses != null) {
            // ses.setDebug(true);
            Store store = null;
            try {
                store = ses.getStore();
                store.connect(popHost, popUser, pass);
                Folder folder = store.getDefaultFolder();
                folder = folder.getFolder("INBOX");
                folder.open(Folder.READ_WRITE);
                int count = folder.getMessageCount();
                int nCount = folder.getNewMessageCount();
                log.info("Found " + count + " messages (" + nCount
                        + ") new ones");

                SMTPProtocolMBean spro = smtp;

                SMTPProtocolInstance proto = (SMTPProtocolInstance) spro
                        .createInstance();

                MailListenerChain chain = spro.getListenerChain();

                /*
                 * Loop over all messages, retreive them, create a local Mail
                 * object, send the message to it and possibly delete it on
                 * server TODO if deleteOnServer is false, one might to only
                 * loop over new messages to prevent duplicates in the local
                 * mailboxes while still allowing to access the messages in the
                 * remote mailbox from another client.
                 */
                for (int i = 0; i < count; i++) {
                    log.info("Trying to get message " + i);
                    Message msg = folder.getMessage(i + 1);

                    /*
                     * If mail is not deleted after pop, only retreive new mails
                     */
                    if (!doDeleteAfterPop) {
                        if (msg.isSet(Flags.Flag.SEEN)) {
                            log
                                    .info("  mail is old and DeleteAfterPop is false, so we don't fetch it");
                            continue;
                        }

                    }

                    log.info("Message " + i + " Subject: " + msg.getSubject());
                    Enumeration hdrs = msg.getAllHeaders();

                    byte[] endinput = new byte[] { '\r', '\n', '.', '\r', '\n' };
                    /*
                     * The input stream is body only, so we need to stuff the
                     * full headers into the inStream as well.
                     */
                    InputStream inStream = new SequenceInputStream(msg
                            .getInputStream(), new ByteArrayInputStream(
                            endinput));
                    inStream = new BufferedInputStream(inStream);

                    if (inStream == null) {
                        log.warn("InputStream from Message is null");
                    }

                    Address[] from = msg.getFrom();
                    InternetAddress[] to = new InternetAddress[1];
                    to[0] = new InternetAddress();
                    to[0].setAddress(localUser);

                    MailAddress mFrom = MailAddress.parseAddress(from[0]);
                    MailAddress[] mTo = MailAddress.parseAddressArray(to);
                    Map<String,String> hdrMap = new HashMap<String,String>();
                    while (hdrs.hasMoreElements()) {
                        Header hdr = (Header) hdrs.nextElement();
                        hdrMap.put(hdr.getName(), hdr.getValue());
                    }
                    MailCreateListener mcl = 
                        proto.getMailCreateListener(mFrom, mTo, hdrMap);
                    MailBodyManager mgr = proto.getManager();
                    Mail mail = Mail.create(mgr, inStream, mcl);
                    
//                    Mail mail = Mail.create(hdrs, inStream, // InputStream
//                            256, // blocksize
//                            MailAddress.parseAddress(from[0]), // sender
//                            MailAddress.parseAddressArray(to), // receipients
//                            proto); // SMPT

                    log.info("Mail created");
                    // Iterator mailListeners = lstListeners.iterator();
                    org.buni.meldware.mail.message.Message mes = mail;
                    /*
                     * while (mailListeners.hasNext() && mes != null) {
                     * MailListener lsner = (MailListener) mailListeners.next();
                     * log.info("sending mail to MailListener: " + lsner); mes =
                     * lsner.send(mes); }
                     */
                    chain.processMail(mes);
                    if (doDeleteAfterPop) {
                        log.debug("Marking this message to be deleted");
                        msg.setFlag(Flags.Flag.DELETED, true);
                    } else
                        msg.setFlag(Flags.Flag.SEEN, true);

                    proto.resetState();
                }
                if (doDeleteAfterPop)
                    folder.close(true); // really delete messages now
                else
                    folder.close(false);
                store.close();
            } catch (Exception e) {
                log.error(e);
                e.printStackTrace();
                if (store != null)
                    try {
                        store.close();
                    } catch (MessagingException me) {
                    } // DUMMY, do nothing
            }
        } else {
            log.error("Can't obtain session");
        }
    }

    public String getPassword() {
        return pass;
    }

    public int getPopPort() {
        return popPort;
    }

    public String getServername() {
        return popHost;
    }

    public String getPopUser() {
        return popUser;
    }

    public void setPassword(String password) {
        pass = password;

    }

    public void setPopPort(int port) {
        popPort = port;

    }

    public void setServername(String name) {
        popHost = name;

    }

    public void setPopUser(String user) {
        this.popUser = user;

    }

    /**
     * The local user the mail is directed to.
     * 
     * @see org.buni.meldware.mail.fetchmail.PopperMBean#getLocalUser()
     */
    public String getLocalUser() {
        return localUser;
    }

    /**
     * Set the local user the mail is directed to
     * 
     * @see org.buni.meldware.mail.fetchmail.PopperMBean#setLocalUser(java.lang.String)
     */
    public void setLocalUser(String user) {
        localUser = user;
    }

    /**
     * Should a message be deleted after it is popped?
     * 
     * @see org.buni.meldware.mail.fetchmail.PopperMBean#isDeleteAfterPop()
     */
    public boolean isDeleteAfterPop() {
        return doDeleteAfterPop;
    }

    /**
     * Set if a message should be deleted after it is popped.
     * 
     * @see org.buni.meldware.mail.fetchmail.PopperMBean#setDeleteAfterPop(boolean)
     */
    public void setDeleteAfterPop(boolean doIt) {
        doDeleteAfterPop = doIt;
    }

    /**
     * Standard MBean create method
     */
    public void create() throws Exception {
        popPort = 110;
        doDeleteAfterPop = false;

    }

    public void setSMTPProtocol(SMTPProtocolMBean smtp) {
        this.smtp = smtp;
    }

}
