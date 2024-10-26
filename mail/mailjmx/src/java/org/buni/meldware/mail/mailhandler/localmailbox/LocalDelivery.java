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
package org.buni.meldware.mail.mailhandler.localmailbox;



import static org.buni.meldware.common.util.ArrayUtil.join;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.buni.meldware.common.logging.Log;
import org.buni.meldware.mail.MailException;
import org.buni.meldware.mail.MailListener;
import org.buni.meldware.mail.mailbox.MailboxService;
import org.buni.meldware.mail.mailbox.MessageData;
import org.buni.meldware.mail.mailbox.MessageDataUtil;
import org.buni.meldware.mail.message.EnvelopedAddress;
import org.buni.meldware.mail.message.Mail;
import org.buni.meldware.mail.message.MailAddress;
import org.buni.meldware.mail.message.MailRetryWrapper;
import org.buni.meldware.mail.message.Message;
import org.jboss.aspects.tx.Tx;
import org.jboss.aspects.tx.TxType;

/**
 * @author Michael Barker
 * @author Andrew C. Oliver
 * @version $Revision: 1.16 $
 */
public class LocalDelivery implements LocalDeliveryMBean {

    private final Log log = Log.getLog(LocalDelivery.class);

    private MailboxService mailboxManager;
    private MessageDataUtil mdf;

    private MailListener router;
    
    private boolean isParseMime = true;

    public Message send(Message msg) throws MailException {
        if(msg == null) {
            return null;
        }
        
        log.info("Delivering message locally: %s", join(msg.getTo(), ","));
        
        Mail mail = (Mail) msg;
        //ArrayList<EnvelopedAddress> failed = new ArrayList<EnvelopedAddress>();
        Map<EnvelopedAddress,String[]> to = new HashMap<EnvelopedAddress,String[]>();

        // Assume that there will be at least 1 local address.
        MessageData md = createMessage(mail);
        for (MailAddress address : mail.getTo()) {
            EnvelopedAddress ea = new EnvelopedAddress(address);
            if (ea.getLocal()) {
                @SuppressWarnings("unchecked")
                List<String> targets = (List<String>)ea.getAttribute("targetFolder");
                Boolean noDelivery = ((Boolean)ea.getAttribute("noDelivery"));
                noDelivery = noDelivery != null ? noDelivery : false;
                if (!noDelivery) {
                    
                    if (targets == null) {
                        to.put(ea, MailboxService.DEFAULT_FOLDER);
                    } else {
                        for (String target : targets) {
                            String[] path = getPath(target);
                            to.put(ea, path);
                        }
                    }
                }
            }
        }
        
        Map<EnvelopedAddress,String[]> failed;
        failed = mailboxManager.deliver(md, mail.getSpamState(), to);
        List<EnvelopedAddress> redeliverTo = new ArrayList<EnvelopedAddress>(failed.keySet());

        //Retry the failed recipients
        if (failed.size() > 0) {
            MailRetryWrapper mailWrp = (mail instanceof MailRetryWrapper) 
            ? (MailRetryWrapper) mail
            : new MailRetryWrapper(mail,MailRetryWrapper.REDELIVERY_LOCAL);

            mailWrp.setTos(redeliverTo);
            router.send(mailWrp);
        }

        return msg;
    }
    
    private String[] getPath(String pathStr) {
        String[] path;
        if (pathStr.startsWith("/")) {
            pathStr = pathStr.substring(1);
        }
        if ("INBOX".equalsIgnoreCase(pathStr) || pathStr.trim().length() == 0) {
            path = MailboxService.DEFAULT_FOLDER;
        } else {
            path = pathStr.split("/");
        }
        
        return path;
    }
    
    
    /**
     * Create a message from a Mail object.  If it not a mime message or
     * the mime parse fails create a simple message.
     * 
     * TODO: Move back into a listener.  Needs the delivery objects to
     * be rethought.
     * 
     * @param mail
     * @return
     */
    @Tx(TxType.REQUIRED)
    private MessageData createMessage(Mail mail) {
        long t0 = System.currentTimeMillis();
        MessageData md = mdf.create(mail, isParseMime);
        long t1 = System.currentTimeMillis();
        log.debug("Time to create message: %d", (t1-t0));
        return md;
    }    

    public void setMailboxManager(MailboxService mailboxManager) {
        this.mailboxManager = mailboxManager;
        this.mdf = new MessageDataUtil(mailboxManager.getBodyManager());
    }

    public void setRouter(MailListener router) {
        this.router = router;
    }
    
    public void setParseMime(boolean isParseMime) {
        this.isParseMime = isParseMime;
    }
    
    public boolean getParseMime() {
        return isParseMime;
    }
    
}
