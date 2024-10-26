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
package org.buni.meldware.mail.maillistener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.naming.InitialContext;

import org.apache.log4j.Logger;
import org.buni.meldware.mail.MailException;
import org.buni.meldware.mail.MailListener;
import org.buni.meldware.mail.domaingroup.DomainGroupMBean;
import org.buni.meldware.mail.message.BouncedMail;
import org.buni.meldware.mail.message.EnvelopedAddress;
import org.buni.meldware.mail.message.Mail;
import org.buni.meldware.mail.message.MailAddress;
import org.buni.meldware.mail.message.MailBodyManager;
import org.buni.meldware.mail.message.MailRetryWrapper;
import org.buni.meldware.mail.message.Message;
import org.jboss.system.ServiceMBeanSupport;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * JMSMailListener - proof of concept code which posts mails to queues or topics.  It has both
 * onServer and offServer settings thus you can divide the processing up such that posts for users
 * in domains handled by this SMTP server are "onServer" and handled by one set of MDBeans and the
 * others are handled by the offServer queue and processed seperately.  If you don't want them
 * seperate, just set them both to the same queue.
 * 
 * Because JMS is asynchronous, the status cannot be returned, you must put it last in the chain.  It
 * will always say "yes I'm doing it"...
 *
 * @author Andrew C. Oliver
 * @author Michael Barker
 * @version $Revision: 1.6 $
 */
public class JMSMailListener extends ServiceMBeanSupport implements
        JMSMailListenerMBean, MailListener {

    private static final Logger jblog = Logger.getLogger(JMSMailListener.class);

    /**
     * onServer queue/topic name
     */
    private String onServer;

    /**
     * offServer queue/topic name
     */
    private String offServer;

    /**
     * DomainGroup MBean containing the domains to be considered as "local"
     */
    DomainGroupMBean domainGroupMBean;

    /**
     * Connection Factory from JNDI
     */
    private ConnectionFactory cf;
    
    /**
     * Destination for local delivery
     */
    private Destination onServerDest;
    
    /**
     * Destination for remote delivery.
     */
    private Destination offServerDest;
    
    private String connectionFactoryName;

    private long[] localRetryIntervals = new long[0];

    private long[] remoteRetryIntervals = new long[0];

    /**
     * Determines if XA transactions a being used.  I.e. do we let the 
     * server handle the commit for us.
     */
    private boolean useTx;

    private MailBodyManager mgr;


    public JMSMailListener(String connectionFactoryName, boolean useTx) {
        this.connectionFactoryName = connectionFactoryName;
        this.useTx = useTx;
    }
    
    public void startService() throws Exception {
        InitialContext ctx = new InitialContext();
        
        cf = (ConnectionFactory) ctx.lookup(connectionFactoryName);
        onServerDest = (Destination) ctx.lookup(onServer);
        offServerDest = (Destination) ctx.lookup(offServer);
    }

    public void stopService() throws Exception {

    }

   
    public void setOnServerPostDestination(String postDest) {
        this.onServer = postDest;
    }

    public void setOffServerPostDestination(String postDest) {
        this.offServer = postDest;
    }

    public String getOnServerPostDestination() {
        return onServer;
    }

    public String getOffServerPostDestination() {
        return offServer;
    }

    public void setLocalRetryTimes(Element localRetryTimes) {
        NodeList nodes = localRetryTimes.getElementsByTagName("retryTime");

        int length = nodes.getLength();
        this.localRetryIntervals = new long[length];

        for (int i = 0; i < length; i++) {
            Node node = nodes.item(i);
            String time = node.getFirstChild().getNodeValue();
            localRetryIntervals[i] = Long.parseLong(time);
        }
    }

    public Element getLocalRetryTimes() {
        //TODO: return xml representations of local retry times
        return null;
    }

    public void setRemoteRetryTimes(Element remoteRetryTimes) {
        NodeList nodes = remoteRetryTimes.getElementsByTagName("retryTime");

        int length = nodes.getLength();
        this.remoteRetryIntervals = new long[length];

        for (int i = 0; i < length; i++) {
            Node node = nodes.item(i);
            String time = node.getFirstChild().getNodeValue();
            remoteRetryIntervals[i] = Long.parseLong(time);
        }
    }

    public Element getRemoteRetryTimes() {
        //TODO: return xml representations of local retry times
        return null;
    }

    public void setDomainGroup(DomainGroupMBean domainGroup) {
        //TODO This has been copied from SMTPProtocol, move to common code?
    	this.domainGroupMBean = domainGroup;
    }

    public DomainGroupMBean getDomainGroup() {
        return domainGroupMBean;
    }

    public String getDestinationForMailAddress(MailAddress ma) {
        String domain = ma.getDomain();

        if (domainGroupMBean.isInGroup(domain)) {
            return onServer;
        }

        return offServer;
    }

    public Message send(Message msg) {
        try {
            Mail mail = (Mail) msg;
            
            PreparedMail preparedMail = prepareMail(mail);
            
            Connection cn = cf.createConnection();
            Session ses = cn.createSession(true, 0);
            
            try {
                if (preparedMail != null) {
                    if (preparedMail.getSendOnServer()) {
                        putMessage(ses, onServerDest, preparedMail);
                    }
            
                    if (preparedMail.getSendOffServer()) {
                        putMessage(ses, offServerDest, preparedMail);
                    }
                }
                else {
                    jblog.warn("Prepared mail was null.");
                }
                    
            } finally {
                if (!useTx) {
                    ses.commit();
                }
                ses.close();
                cn.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return msg;
    }

    
    /**
     * Puts the message on the necessary destination.
     * 
     * @param msg the message to be sent
     * @throws Exception if there is any problem (JMS related or otherwise)
     */

    private void putMessage(Session ses, Destination dest,
            PreparedMail preparedMail) throws Exception {
        MessageProducer sender = ses.createProducer(dest);

        log.debug("PUT MESSAGE ON QUEUE:" + preparedMail.getMail());

        ObjectMessage om = ses.createObjectMessage(preparedMail.getMail());
        long reference = mgr.addReference(preparedMail.getMail().getMailBody());
        om.setLongProperty(Mail.REFERENCE, reference);
        long secondsDelay = preparedMail.getRetrySeconds();

        if (secondsDelay > 0) {
            long deliveryTime = new Date().getTime() + (secondsDelay * 1000);
            //Note: will only work in JBoss >= 3.2.2 with the JBoss JMS provider
            om.setLongProperty("JMS_JBOSS_SCHEDULED_DELIVERY", deliveryTime);
        }
        sender.send(om);
        sender.close();
    }
    
    

    /** Prepares the mail for sending by figuring out things like the retry count,
     * the time to retry, what queue to put the message on etc. If too many retries
     * have taken place a bounce message is generated for the correct queue. 
     * @param mail The mail to be sent
     * @return A PreparedMail structure containing the mail and other useful info. Null if we should not send.
     * @throws MailException 
     */
    private PreparedMail prepareMail(Mail mail) throws MailException {
        if (mail instanceof MailRetryWrapper) {
            //MailRetryWrapper are created by the queues if a mail fails, hence this is 
            //a retried mail
            return prepareResentMail((MailRetryWrapper) mail);
        } else {
            //MailRetryWrapper are created by the queues if a mail fails, hence this is 
            //a new mail
            return prepareNewMail(mail);
        }
    }

    /** Prepares the mail for retry by figuring out things like the retry count,
     * the time to retry, what queue to put the message on etc. If too many retries
     * have taken place a bounce message is generated for the correct queue. 
     * @param mail The mail to be sent
     * @return A PreparedMail structure containing the mail and other useful info. Null if we should not send.
     * @throws MailException 
     */
    private PreparedMail prepareResentMail(MailRetryWrapper mail)
            throws MailException {
        //The message is being retried
        //We have already been through all the addresses and need to look at the extra information in
        //the MailWrapper to see what to do with the mail

        boolean mailIsLocal = (mail.getRedeliveryDestination() == MailRetryWrapper.REDELIVERY_LOCAL);

        //Get the interval to wait for a retry
        int retryNumber = mail.getRetryNumber();
        try {
            PreparedMail preparedMail = new PreparedMail(mail);

            if (mailIsLocal) {
                preparedMail.setRetrySeconds(localRetryIntervals[retryNumber]);
                preparedMail.setSendOnServer(true);
            } else {
                preparedMail.setRetrySeconds(remoteRetryIntervals[retryNumber]);
                preparedMail.setSendOffServer(true);
            }
            mail.increaseRetries();
            return preparedMail;

        } catch (ArrayIndexOutOfBoundsException e) {
            //We have exhausted the number of retries, generate a bounce reply to 
            //the receivers who have not been sent to
            return prepareBounceMessageForMail(mail, mailIsLocal);
        }
    }

    /** Prepares a bounce message. 
     * @param mail The mail to be sent
     * @return A PreparedMail structure containing the mail and other useful info. Null if 
     * we should not send (i.e. this message is a bounce mail itself)
     * @throws MailException 
     */
    private PreparedMail prepareBounceMessageForMail(MailRetryWrapper mail,
            boolean mailIsLocal) throws MailException {
        if (!mail.getSender().isEmpty()) {

            List<MailAddress> failed = new ArrayList<MailAddress>();

            //If this message was meant for local delivery, only failed local ones should be reported and vice versa
            for (MailAddress address : mail.getTo()) {
                EnvelopedAddress ea = new EnvelopedAddress(address);
                boolean local = ea.getLocal(); // CLASSCAST EXCEPTION!!!!

                if (local == mailIsLocal) {
                    failed.add(address);
                }
            }

            if (failed.size() > 0) {
                MailAddress[] failedaddrs = (MailAddress[]) failed
                        .toArray(new MailAddress[failed.size()]);
                BouncedMail bouncedMail = new BouncedMail(mgr, mail, failedaddrs,
                        MailAddress.parseSMTPStyle(domainGroupMBean
                                .getPostmaster()));
                PreparedMail preparedMail = new PreparedMail(bouncedMail);
                preparedMail = determineDestinationsInMail(preparedMail);
                return preparedMail;
            }
        }

        return null;
    }

    private PreparedMail prepareNewMail(Mail mail) {
        PreparedMail preparedMail = new PreparedMail(mail);
        return determineDestinationsInMail(preparedMail);
    }

    private PreparedMail determineDestinationsInMail(PreparedMail pMail) {
        //Go through and modify the addresses to be on or off server and determine
        //the queues to be used
        for (EnvelopedAddress address : pMail.getMail().getRecipients()) {
            String dest = getDestinationForMailAddress(address);
            if (dest.equals(onServer)) {
                address.setLocal(true);
                pMail.setSendOnServer(true);
            } else if (dest.equals(offServer)) {
                address.setLocal(false);
                pMail.setSendOffServer(true);
            }
        }

        return pMail;
    }

    /* (non-Javadoc)
     * @see org.buni.meldware.mail.maillistener.JMSMailListenerMBean#setMailBodyManager(org.buni.meldware.mail.message.MailBodyManager)
     */
    public void setMailBodyManager(MailBodyManager mgr) {
        this.mgr = mgr;
    }

}

/**
 * Class to hold data passed around within JMSMailListener 
 * @author Kabir Khan
 *
 */
class PreparedMail {
    /** Should mail be sent on server */
    private boolean sendOnServer;

    /** Should mail be sent off server */
    private boolean sendOffServer;

    /** Mail to be sent */
    private Mail mail;

    /** Delay before JMS send queue */
    private long retrySeconds = 0;

    PreparedMail(Mail mail) {
        this.mail = mail;
    }

    public Mail getMail() {
        return mail;
    }

    public long getRetrySeconds() {
        return retrySeconds;
    }

    public boolean getSendOffServer() {
        return sendOffServer;
    }

    public boolean getSendOnServer() {
        return sendOnServer;
    }

    public void setRetrySeconds(long l) {
        retrySeconds = l;
    }

    public void setSendOffServer(boolean b) {
        sendOffServer = b;
    }

    public void setSendOnServer(boolean b) {
        sendOnServer = b;
    }

}
