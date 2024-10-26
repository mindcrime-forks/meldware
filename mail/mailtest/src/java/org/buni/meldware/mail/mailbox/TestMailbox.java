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
package org.buni.meldware.mail.mailbox;

import java.io.ByteArrayOutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.buni.meldware.mail.MailException;
import org.buni.meldware.mail.MailUtil;
import org.buni.meldware.mail.TestConstants;
import org.buni.meldware.mail.api.FolderMessage;
import org.buni.meldware.mail.api.Hints;
import org.buni.meldware.mail.api.Range;
import org.buni.meldware.mail.message.Body;
import org.buni.meldware.mail.message.EnvelopedAddress;
import org.buni.meldware.mail.message.Mail;
import org.buni.meldware.mail.message.MailAddress;
import org.buni.meldware.mail.message.MailBodyManager;
import org.buni.meldware.mail.tx.AOPTxRunner;
import org.buni.meldware.mail.tx.Tx;
import org.buni.meldware.mail.tx.TxRunner;
import org.buni.meldware.mail.tx.VoidTx;
import org.buni.meldware.mail.util.MMJMXUtil;
import org.buni.meldware.mail.util.Node;
import org.buni.meldware.mail.util.Visitor;
import org.buni.meldware.test.JMXTestWrapper;

/**
 * @author acoliver
 * @author Michael Barker
 *
 */ 
public class TestMailbox extends TestCase {
    
    public static TestSuite suite() {
        return JMXTestWrapper.suite(TestMailbox.class);
        //return JMXTestWrapper.suite(TestMailbox.class, "testCreateMessageInFolderService");
    }
    
    public TestMailbox(String name) {
        super(name);
    }
    
    MailboxService service;
    MailBodyManager mgr;
    TxRunner txr;
    
    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        service = getMailbox();
        mgr = MailUtil.getMailBodyManager();
        txr = new AOPTxRunner();
        MailUtil.clearData();
    }
    
    
    protected MailboxService getMailbox() {
        return MMJMXUtil.getMBean(TestConstants.MAILBOX_SERVICE_MBEAN,
                MailboxService.class);
    }

    /**
     * Tests the creation and deletion of the mailbox.
     * 
     * @throws MailException
     */
    public void testCreateAndDelete() throws MailException
    {
        String mailboxName = "mailbox1";
        Folder mailbox = service.createMailbox(mailboxName);
        assertNotNull(mailbox);
        service.deleteMailboxByAlias(mailboxName);
    }
    
    /**
     * Test Getting the mailbox.
     * 
     * @throws MailException
     */
    public void testGet() throws MailException
    {
        String mailboxName = "mailbox2";
        service.createMailbox(mailboxName);
        Mailbox mailbox = service.getMailboxByAlias(mailboxName);
        assertNotNull(mailbox);
        service.deleteMailboxByAlias(mailboxName);
    }
    
    /**
     * Test the addition and remove of message from the mailbox.
     * @throws Exception
     */
    public void testAddRemoveMessages() throws Exception
    {
        final String mailboxName = "mailbox3";
        final String to = "<noone@nowhere.com>";
        final String from = "<noone@nowhere.com>";
        final String subject = "Test Subject";
        final String body = "Test Email Body";

        txr.requiresNew(new VoidTx() { 
            public void run() {
                service.createMailbox(mailboxName);
            }
        });
        
        txr.requiresNew(new VoidTx() { 
            public void run() {
                Mailbox mailbox = service.getMailboxByAlias(mailboxName);
                Mail mail = MailUtil.create(to, from, subject, body);
                Folder f = mailbox.getDefaultInFolder();
                service.createMail(f, new MessageData(mail));
                
                Collection<FolderEntry> fes = service.getMessages(f, Range.EMPTY, Hints.FLAGS);
                assertEquals("There should be 1 message in the mailbox", 1, fes.size());
                
                FolderEntry fe = fes.iterator().next();
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                Body b = service.getMailBody(fe.getMessage()).get(0);
                mgr.write(b, out);
                String s = new String(out.toByteArray());
                assertEquals(body, s);
                fe.setDeleted(true);
                Range[] r = Range.create(fe.uid);
                service.setDeleted(f, r, true);
                service.expunge(f, false);
            }
        });
        
        txr.requiresNew(new VoidTx() {
            public void run() {
                Mailbox mailbox = service.getMailboxByAlias(mailboxName);
                Folder f = mailbox.getDefaultInFolder();
                assertEquals(0, service.getMessages(f, Range.ALL).size());
            }
        });
    }
    
    
    public void testDelete() {
        
        final String user = "tom";
        final String otherUser = "fred";
        final String alias = "tom@localhost";
        
        // Create the mailbox.
        txr.requiresNew(new VoidTx() {
            public void run() {
                Mailbox mailbox = service.createMailbox(user);
                service.createAlias(mailbox.getId(), "tom@localhost");
                service.createMailbox(otherUser);
            }
        });
        
        // Deliver the mail.
        txr.requiresNew(new VoidTx() {
            public void run() {
                String from = "<foobar@nowhere.com>";
                String[] to = { "<tom@localhost>", "<fred@localhost>" };
                String[] cc = {};
                String[] bcc = {};
                Mail m = Mail.create(mgr, from, to, cc, bcc, "Test", "Test");
                Mailbox mailbox1 = service.getMailboxByAlias(alias);
                MessageData message1 = new MessageData(m);
                service.createMail(mailbox1.getDefaultInFolder(), message1);
                
                Mailbox mailbox2 = service.getMailboxByAlias(otherUser);
                MessageData message2 = new MessageData(m);
                service.createMail(mailbox2.getDefaultInFolder(), message2);
            }
        });
        
        // Then mark it for deletion.
        txr.requiresNew(new VoidTx() {
            public void run() {
                Mailbox mailbox = service.getMailboxByAlias(alias);
                Folder f = mailbox.getDefaultInFolder();
                int count = f.getMessages().size();
                
                assertEquals("Should only be one message", 1, count);
                
                service.setDeleted(f, Range.ALL, true);
            }
        });

        // Then delete all
        txr.requiresNew(new VoidTx() {
            public void run() {
                Mailbox mailbox = service.getMailboxByAlias(alias);
                service.expunge(mailbox.getDefaultInFolder(), false);
            }
        });
        
        //System.out.println("DELETED");
        
        // Check the mailboxes still have the correct messages.
        txr.requiresNew(new VoidTx() {
            public void run() {
                Mailbox mailbox1 = service.getMailboxByAlias(alias);
                Folder f1 = mailbox1.getDefaultInFolder();
                
                assertEquals("Should be one message", 0, service.getMessages(f1, Range.ALL).size());
                
                Mailbox mailbox2 = service.getMailboxByAlias(otherUser);
                Folder f2 = mailbox2.getDefaultInFolder();
                
                assertEquals("Should be one message", 1, service.getMessages(f2, Range.ALL).size());
                assertEquals("Should have at least one body", 1, service.getMessages(f2, Range.ALL).iterator().next().getMessage().getMessageBodies().size());
            }
        });
    }
    
    public void testCreateMessageInFolderService() throws Exception {
        final String to = "<andy@localhost>";
        final String from = "<test@nowhere>";
        final String subject = "Foobar";
        final String bodyStr = "Bla bla\r\n";
        Mail mail = MailUtil.create(to, from, subject, bodyStr);        
        createMessageAndMailbox("foobar1", mail);
        
        txr.requiresNew(new VoidTx() {
            public void run() {
                Mailbox box = service.getMailboxByAlias("foobar1@nowhere.com");
                Folder folder = box.getDefaultInFolder();
                long mcount = service.getMessages(folder, Range.ALL).size();
                assertEquals("only one message should be in the folder after this operation", 1, mcount);
                MessageData md = service.getMessages(folder, Range.ALL, Hints.NONE).iterator().next().getMessage();
                assertEquals("To address", to, md.getTo().get(0).toSMTPString());
                assertEquals("From address", from, md.getFrom());
                assertEquals("Subject", subject, md.getSubject());                    
                service.deleteMailboxById(box.getId());
            }
        });
    }


    public void testCreateMessageInFolderAndGetBody() {
        String to = "<andy@localhost>";
        String from = "<test@nowhere>";
        String subject = "Foobar";
        final String bodyStr = "Bla bla\r\n";
        Mail mail = MailUtil.create(to, from, subject, bodyStr);        
        createMessageAndMailbox("foobar2", mail);
        
        txr.requiresNew(new VoidTx() {
            public void run() {
                Mailbox box = service.getMailboxByAlias("foobar2@nowhere.com");
                Folder folder = box.getDefaultInFolder();
                MessageData md = service.getMessages(folder, Range.ALL, Hints.NONE).iterator().next().getMessage();
                Body body = service.getMailBody(md).get(0);
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                mgr.write(body, out);
                byte[] bytes = out.toByteArray();
                String message =  new String(bytes) ; 
                service.deleteMailboxById(box.getId());
                assertEquals("The message body is wrong", bodyStr, message);                
            }
        });
    }


    private void createMessageAndMailbox(final String mailbox, final Mail mail) {
        txr.requiresNew(new VoidTx() {
            public void run() {
                service.createMailbox(mailbox);
            }
        });
        
        txr.requiresNew(new VoidTx() {
            public void run() {
                Mailbox box = service.getMailboxByAlias(mailbox);
                long id = box.getId();
                service.createAlias(id, mailbox + "@nowhere.com");
                Folder folder = box.getDefaultInFolder();
                MessageData data = new MessageData(mail);
                service.createMail(folder, data);                
            }
        });
    }

   public void testSummary() {
       String mboxName = "foobar3";
       service.createMailbox(mboxName);
       Node<FolderSummary> summary = service.folderSummary(mboxName);
       assertTrue(summary.getChildren().hasNext());
       Visitor<FolderSummary> v = new Visitor<FolderSummary>() {
           public void visit(Node<FolderSummary> n) {
               //System.out.println(n.getValue());
               for (Node<FolderSummary> child : n) {
                   child.accept(this);
               }
           }
       };
       summary.accept(v);
       service.deleteMailboxByAlias(mboxName);
   }
   
   public void testNextUid() {
       final Long folderId = txr.requiresNew(new Tx<Long>() {
           public Long run() {
               Mailbox m = service.createMailbox("tom");
               Folder f = m.getDefaultInFolder();
               return f.getId();
           }
       });
       
       txr.requiresNew(new VoidTx() {
           public void run() {
               assertEquals(1, service.getNextUid(folderId));
           }
       });
       
       txr.requiresNew(new VoidTx() {
           public void run() {
               assertEquals(2, service.getNextUid(folderId));
           }
       });
   }
   
   public void testQuota() {
       
       final String name = "tom";
       final String alias = "tom@localhost";
       String to = "<tom@localhost>";
       String from = "<test@nowhere>";
       String subject = "Foobar";
       final String bodyStr = "Bla bla\r\n";
       final Mail mail = MailUtil.create(to, from, subject, bodyStr);        
       
       txr.requiresNew(new VoidTx() {
           public void run() {
               Mailbox mbox = service.createMailbox(name);
               service.createAlias(mbox.getId(), alias);
               mbox.setHardSizeLimit(10);
               mbox.setSoftSizeLimit(10);
               service.save(mbox);
           }
       });
       
       txr.requiresNew(new VoidTx() {
           public void run() {
               MessageData md = new MessageData(mail);
               Map<EnvelopedAddress,String[]> to = new HashMap<EnvelopedAddress,String[]>();
               EnvelopedAddress ea = new EnvelopedAddress(MailAddress.parseSMTPStyle("<" + alias + ">"));
               to.put(ea, new String[] {"INBOX"});
               Map<EnvelopedAddress,String[]> failed = service.deliver(md, 
                       FolderMessage.SpamState.NOT_SPAM, to);
               assertEquals(0, failed.size());
           }
       });
       
       txr.requiresNew(new VoidTx() {
           public void run() {
               MessageData md = new MessageData(mail);
               Map<EnvelopedAddress,String[]> to = new HashMap<EnvelopedAddress,String[]>();
               EnvelopedAddress ea = new EnvelopedAddress(MailAddress.parseSMTPStyle("<" + alias + ">"));
               to.put(ea, new String[] {"INBOX"});
               Map<EnvelopedAddress,String[]> failed = service.deliver(md, 
                       FolderMessage.SpamState.NOT_SPAM, to);
               assertEquals(1, failed.size());
           }
       });
   }
 
   
   public void testDeliver() {
       
       final String name = "tom";
       final String alias = "tom@localhost";
       String to = "<tom@localhost>";
       String from = "<test@nowhere>";
       String subject = "Foobar";
       final String bodyStr = "Bla bla\r\n";
       final Mail mail = MailUtil.create(to, from, subject, bodyStr);        
       
       txr.requiresNew(new VoidTx() {
           public void run() {
               Mailbox mbox = service.createMailbox(name);
               service.createAlias(mbox.getId(), alias);
               service.save(mbox);
           }
       });
       
       txr.requiresNew(new VoidTx() {
           public void run() {
               MessageData md = new MessageData(mail);
               Map<EnvelopedAddress,String[]> to = new HashMap<EnvelopedAddress,String[]>();
               EnvelopedAddress ea = new EnvelopedAddress(MailAddress.parseSMTPStyle("<" + alias + ">"));
               to.put(ea, new String[] {"INBOX"});
               Map<EnvelopedAddress,String[]> failed = service.deliver(md, 
                       FolderMessage.SpamState.NOT_SPAM, to);
               assertEquals(0, failed.size());
           }
       });
   }
   

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }

}
