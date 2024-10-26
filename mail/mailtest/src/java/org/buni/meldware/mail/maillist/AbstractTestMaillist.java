/*
 * Bunisoft the Open Source Communications Company
 * Copyright 2006, Bunisoft Inc., and individual contributors as
 * indicated by the @authors tag.  See the copyright.txt in the 
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; version 2.1 of
 * the License.
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
package org.buni.meldware.mail.maillist;

import javax.management.ObjectName;

import junit.framework.TestCase;

import org.buni.meldware.mail.MailListener;
import org.buni.meldware.mail.MailUtil;
import org.buni.meldware.mail.api.Range;
import org.buni.meldware.mail.mailbox.Folder;
import org.buni.meldware.mail.mailbox.Mailbox;
import org.buni.meldware.mail.mailbox.MailboxService;
import org.buni.meldware.mail.message.Mail;
import org.buni.meldware.mail.message.MailAddress;
import org.buni.meldware.mail.message.MailBodyManager;
import org.buni.meldware.mail.tx.AOPTxRunner;
import org.buni.meldware.mail.tx.TxRunner;
import org.buni.meldware.mail.tx.VoidTx;
import org.buni.meldware.mail.util.MMJMXUtil;

/**
 * @author Michael Barker
 *
 */
public abstract class AbstractTestMaillist extends TestCase
{
    MailListManager mailListManager = null;
    TxRunner txr = null;
    MailboxService service = null;
    MailBodyManager mgr = null;
    MailListener chain = null;
    
    public AbstractTestMaillist(String name) {
        super(name);
    }
    
    public void setUp() throws Exception
    {
        mailListManager = (MailListManager) MMJMXUtil.getMBean(getMBeanName(), MailListManager.class);
        txr = new AOPTxRunner();
        service = MailUtil.getMailbox();
        mgr = MailUtil.getMailBodyManager();
        chain = MMJMXUtil.getMBean("meldware.mail:type=MailServices,name=MailListener", MailListener.class);
        MailUtil.clearData();
    }
    
    public void testManager()
    {
        MailAddress address = MailAddress.parseSMTPStyle("<noone@nowhere.com>");
        MailListProperties props = new MailListProperties();
        props.setProperty(MailListPropertyConstants.SUBJECT_PREFIX, "[STUFF]");
        boolean b = mailListManager.createList(address, props);
        assertTrue("Manager wasn't created", b);
        
        MailList mailList = mailListManager.findList(address);
        assertNotNull(mailList);
        
        mailListManager.deleteList(address);
    }
    
    public void testAddAddresses()
    {
        MailAddress address = MailAddress.parseSMTPStyle("<noone1@nowhere.com>");
        MailListProperties props = new MailListProperties();
        props.setProperty(MailListPropertyConstants.SUBJECT_PREFIX, "[STUFF]");
        mailListManager.createList(address, props);
        
        MailList mailList = mailListManager.findList(address);
        assertNotNull(mailList);
        
        mailList.addMember(MailAddress.parseSMTPStyle("<test1@nowhere.com>"));
        mailList.addMember(MailAddress.parseSMTPStyle("<test2@nowhere.com>"));
        mailList.addMember(MailAddress.parseSMTPStyle("<test3@nowhere.com>"));
        mailList.addMember(MailAddress.parseSMTPStyle("<test4@nowhere.com>"));
        mailList.addMember(MailAddress.parseSMTPStyle("<test1@nowhere.com>"));
        MailAddress[] addresses = mailList.getMembers();
        assertEquals(4, addresses.length);
        
        mailList.removeMember(MailAddress.parseSMTPStyle("<test1@nowhere.com>"));
        addresses = mailList.getMembers();
        assertEquals(3, addresses.length);
        
        assertTrue(mailList.isMember(MailAddress.parseSMTPStyle("<test2@nowhere.com>")));
        assertTrue(mailList.isMember(MailAddress.parseSMTPStyle("<test3@nowhere.com>")));
        assertTrue(mailList.isMember(MailAddress.parseSMTPStyle("<test4@nowhere.com>")));
        assertFalse(mailList.isMember(MailAddress.parseSMTPStyle("<test1@nowhere.com>")));
        assertFalse(mailList.isMember(MailAddress.parseSMTPStyle("<test5@nowhere.com>")));
        
        mailListManager.deleteList(address);
    }    
    
    public void test2Addresses() throws InterruptedException {
        // Create 2 mailboxes
        final String user1 = "tom";
        final String user2 = "fred";
        final String maillist = "mylist@localhost";
        final String domain = "@localhost";
        
        // Create all of the structures.
        txr.requiresNew(new VoidTx() {
            public void run() {
                Mailbox mbox1 = service.createMailbox(user1);
                service.createAlias(mbox1.getId(), user1 + domain);
                
                Mailbox mbox2 = service.createMailbox(user2);
                service.createAlias(mbox2.getId(), user2 + domain);
                
                mailListManager.createList(maillist);
                MailListProperties p = new MailListProperties();
                mailListManager.createList(MailAddress.parseSMTPStyle(maillist), p);
            }
        });
        
        txr.requiresNew(new VoidTx() {
            public void run() {
                mailListManager.addMember(maillist, user1 + domain);
                mailListManager.addMember(maillist, user2 + domain);
            }
        });
        
        txr.requiresNew(new VoidTx() {
            public void run() {
                String from = "<tom@localhost>";
                String[] to = { "<mylist@localhost>" };
                String[] cc = {};
                String[] bcc = {};
                Mail m = Mail.create(mgr, from, to, cc, bcc, "Test", "Test");
                chain.send(m);
            }
        });
        
        // Allow the messages to be delivered.
        Thread.sleep(5000);
        
        txr.requiresNew(new VoidTx() {
            public void run() {
                //Mailbox mbox1 = service.getMailboxByAlias(user1);
                Folder f1 = service.getDefaultInFolderByAlias(user1);
                assertEquals(user1, 1, service.getMessages(f1, Range.ALL).size());
                
                Folder f2 = service.getDefaultInFolderByAlias(user2);
                assertEquals(user2, 1, service.getMessages(f2, Range.ALL).size());
            }
        });
        
        txr.requiresNew(new VoidTx() {
            public void run() {
                service.deleteMailboxByAlias(user1);
                service.deleteMailboxByAlias(user2);
                mailListManager.deleteList(maillist);
            }
        });        
    }
    
    public abstract ObjectName getMBeanName() throws Exception;
}
