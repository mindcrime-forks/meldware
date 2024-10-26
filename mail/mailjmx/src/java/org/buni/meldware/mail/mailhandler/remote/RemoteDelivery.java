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
package org.buni.meldware.mail.mailhandler.remote;

import java.util.ArrayList;
import java.util.List;

import org.buni.meldware.mail.MailException;
import org.buni.meldware.mail.MailListener;
import org.buni.meldware.mail.domaingroup.DomainGroupMBean;
import org.buni.meldware.mail.message.BouncedMail;
import org.buni.meldware.mail.message.EnvelopedAddress;
import org.buni.meldware.mail.message.Mail;
import org.buni.meldware.mail.message.MailAddress;
import org.buni.meldware.mail.message.MailRetryWrapper;
import org.buni.meldware.mail.message.Message;
import org.buni.meldware.mail.smtp.sender.SMTPResult;
import org.buni.meldware.mail.smtp.sender.SMTPSenderMBean;
import org.jboss.logging.Logger;

/**
 *
 * @author Andrew C. Oliver
 * @version $Revision: 1.5 $
 */
public class RemoteDelivery implements RemoteDeliveryMBean {

    private Logger log = Logger.getLogger(RemoteDelivery.class);

    private MailListener router;

    private DomainGroupMBean domainGroup;

    private SMTPSenderMBean sender;

    public Message send(Message msg) throws MailException {

        // TODO: We need to refactor Messages.
        Mail smtpMessage = (Mail) msg;

        // Get all addresses by domain
        //MailAddress[] addresses = smtpMessage.getTo();
        ArrayList<EnvelopedAddress> excluded = new ArrayList<EnvelopedAddress>();
        for (MailAddress address : smtpMessage.getTo()) {
        //for (int index = 0; index < addresses.length; index++) {
            // MIKEA: SEEMS LIKE WE CAN AVOID CLASSCASTEXCEPTIONS THIS WAY...            
            EnvelopedAddress ea = new EnvelopedAddress(address);

            if (ea.getLocal()) {
                excluded.add(ea);
            }
        }

        // Kab: If it seems funny that I am stripping out the local
        // addresses and passing them
        // in to the remote SMTPSender, it is because I'm going with the
        // original SMTPSender interface.
        // I can change it if required
        MailAddress[] excludedArr = (MailAddress[]) excluded
                .toArray(new MailAddress[excluded.size()]);
        SMTPResult[] results = deliver(excludedArr, smtpMessage);

        for (int i = 0; i < results.length; i++) {
            log.debug("Delivery result - " + results[i]);
        }

        // Retry the failed recipients
        List<EnvelopedAddress> redeliveries = new ArrayList<EnvelopedAddress>();

        for (int i = 0; i < results.length; i++) {
            if (results[i].redeliver()) {
                redeliveries.add(EnvelopedAddress.wrap(results[i].getAddress()));
            }
        }

        List<MailAddress> invalidAddresses = new ArrayList<MailAddress>();

        for (int i = 0; i < results.length; i++) {
            if (results[i].getStatus() == SMTPResult.STATUS_INVALID_ADDRESS) {
                invalidAddresses.add(results[i].getAddress());
            }
        }

        log.debug("Redelivering to " + redeliveries.size() + " recipients");
        if (redeliveries.size() > 0) {
            MailRetryWrapper mailWrp = (smtpMessage instanceof MailRetryWrapper) ? (MailRetryWrapper) smtpMessage
                    : new MailRetryWrapper(smtpMessage,
                            MailRetryWrapper.REDELIVERY_REMOTE);

            mailWrp.setTos(redeliveries);
            router.send(mailWrp);
        }

        log.debug("Bouncing " + invalidAddresses.size() + " recipients: "
                + invalidAddresses);
        // All of the messages to invalid addresses should be bounced.
        if (invalidAddresses.size() > 0) {
            MailAddress[] bounced = (MailAddress[]) invalidAddresses
                    .toArray(new MailAddress[0]);
            BouncedMail bounceMail = new BouncedMail(sender.getMailBodyManager(), smtpMessage, bounced,
                    MailAddress.parseSMTPStyle(domainGroup.getPostmaster()));
            router.send(bounceMail);
        }

        return msg;
    }

    private SMTPResult[] deliver(MailAddress[] excluded, Mail message) {
        log.debug("deliver - exclude:" + excluded);
        // Don't catch any exceptions here.
        // Any runtime execptions should be propagated back up
        // and allow the transaction to roll back and retry.
        return sender.send(message, excluded);
    }

    /**
     * @return Returns the domainGroup.
     */
    public DomainGroupMBean getDomainGroup() {
        return domainGroup;
    }

    /**
     * @param domainGroup The domainGroup to set.
     */
    public void setDomainGroup(DomainGroupMBean domainGroup) {
        this.domainGroup = domainGroup;
    }

    /**
     * @return Returns the router.
     */
    public MailListener getRouter() {
        return router;
    }

    /**
     * @param router The router to set.
     */
    public void setRouter(MailListener router) {
        this.router = router;
    }

    /**
     * @return Returns the sender.
     */
    public SMTPSenderMBean getSender() {
        return sender;
    }

    /**
     * @param sender The sender to set.
     */
    public void setSender(SMTPSenderMBean sender) {
        this.sender = sender;
    }

}
