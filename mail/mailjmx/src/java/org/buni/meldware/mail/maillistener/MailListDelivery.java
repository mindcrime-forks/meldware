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
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.buni.meldware.mail.MailException;
import org.buni.meldware.mail.MailListenerChain;
import org.buni.meldware.mail.maillist.MailList;
import org.buni.meldware.mail.maillist.MailListManager;
import org.buni.meldware.mail.maillist.MailListProperties;
import org.buni.meldware.mail.maillist.MailListPropertyConstants;
import org.buni.meldware.mail.message.Mail;
import org.buni.meldware.mail.message.MailAddress;
import org.buni.meldware.mail.message.Message;

/**
 * intercepts mails delivered to a list and creates a copy for each member
 * of that list 
 *
 * @author Andrew C. Oliver
 */
public class MailListDelivery implements MailListDeliveryMBean {
    private static final Logger log = Logger
    .getLogger(MailListDelivery.class);    
    
    private MailListManager mgr; 
    private MailListenerChain chain;
    /** Creates a new instance of MailListDeliveryListener */
    public MailListDelivery() {
        log.debug("mail list delivery listener started");
    }
    
    public Message send(Message msg) throws MailException {
        
        Mail mail = (Mail)msg;
        MailAddress[] listAddresses = mail != null ? getListAddresses(mail) :
                                      null;
        if (listAddresses != null) {
            log.debug("got at least one list address");
            for (int i = 0; i < listAddresses.length; i++) {
                Mail xmail = new Mail(mail);
                xmail.removeTo(listAddresses[i]);

                //xmail.addHeader("To",listAddresses[i].toString()); appears unnecessary
                MailAddress[] listmembers = getListMembers(listAddresses[i]);
                boolean breakLoop = setOptions(listAddresses[i], xmail, listmembers);
                
                if (breakLoop) {
                    break;  //for instance the From != list member
                }
                
                for (int j = 0; j < listmembers.length; j++) {
                 //  System.err.println("adding "+listmembers[j].toString());
                   xmail.addBCC(listmembers[j]);
                }
                chain.processMail(xmail);
                mail = null;
            }
        }
        return mail;
    }

    private boolean setOptions(MailAddress address, Mail xmail, MailAddress[] listmembers) {
        boolean breakloop = false;
        MailList lst = mgr.findList(address);
        MailListProperties props = lst.getProperties();
        Boolean replyto = props.getPropertyBool(MailListPropertyConstants.REPLY_TO_LIST);
        Boolean membersOnly = props.getPropertyBool(MailListPropertyConstants.MEMBERS_ONLY);
        Boolean subjectPrefix = props.getPropertyBool(MailListPropertyConstants.SUBJECT_PREFIX);
        membersOnly = membersOnly != null ? membersOnly : false;
        replyto = replyto != null ? replyto : false;
        subjectPrefix = subjectPrefix != null ? subjectPrefix : false;
       
            //array is a bad collection choice
        if(membersOnly) {
            Set<MailAddress> lms = new HashSet<MailAddress>();
            lms.addAll(Arrays.asList(listmembers));
            List<MailAddress> froms = xmail.getFrom();
            for (MailAddress from : froms) {
                if(!lms.contains(from)) {
                   breakloop = true;
                   return breakloop;
                }
            }
        }
        if (replyto) {
            xmail.addHeader("Reply-To", address.toSMTPString());
        }
        if (subjectPrefix) {
            String subj = xmail.getSubject();
            subj = "["+address.getUser()+"] "+subj;   ///todo list should have a name and that should go here not the address "user"
        }
        
        return breakloop;
    }

    private MailAddress[] getListAddresses(Mail mail) {
       List<MailAddress> results = new ArrayList<MailAddress>();
       for (MailAddress address : mail.getTo()) {
           if(mgr.findList(address.toString()) != null) {
              results.add(address);
           }
       }
       MailAddress[] r = new MailAddress[results.size()];
       return results.toArray(r);
    }

    private MailAddress[] getListMembers(MailAddress addy) {
        return mgr.findList(addy).getMembers();
    }

    public void setMailListManager(MailListManager mgr) {
        this.mgr = mgr;
    }

    public MailListManager getMailListManager() {
        return mgr;
    }

    public void setDeliveryChain(MailListenerChain chain) {
        this.chain = chain;
    }

    public MailListenerChain getDeliveryChain() {
        return this.chain;
    }
}
