package org.buni.meldware.mail.mailbox;

import java.util.Collection;
import java.util.List;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.buni.meldware.mail.MailUtil;
import org.buni.meldware.mail.api.Hints;
import org.buni.meldware.mail.message.Mail;
import org.buni.meldware.test.JMXTestWrapper;
import org.buni.meldware.mail.tx.AOPTxRunner;
import org.buni.meldware.mail.tx.TxRunner;
import org.buni.meldware.mail.tx.VoidTx;

public class TestMailboxProxy extends TestCase {

    MailboxService service;
    TxRunner txr;
    
    public static TestSuite suite() {
        return JMXTestWrapper.suite(TestMailboxProxy.class);
    }
    
    public TestMailboxProxy(String name) {
        super(name);
    }
    
    public void setUp() throws Exception {
        service = MailUtil.getMailbox();
        txr = new AOPTxRunner();
        MailUtil.clearData();
    }
    
    public void testGetFolder() {
        
        final String alias = "tom";
        
        txr.requiresNew(new VoidTx() {
            public void run() {
                service.createMailbox(alias);
            }
        });
        
        txr.requiresNew(new VoidTx() {
            public void run() {
                Mailbox mailbox = service.getMailboxByAlias(alias);
                org.buni.meldware.mail.api.Mailbox mbox = new org.buni.meldware.mail.mailbox.MailboxProxy(service, mailbox, Hints.NONE);
                org.buni.meldware.mail.api.Folder f = mbox.getDefault();
                assertEquals("INBOX", f.getName());

                Collection<String> aliases = mbox.getAliases();
                assertEquals(1, aliases.size());
                assertEquals("tom", aliases.iterator().next());

                String[] path = { "INBOX" };
                org.buni.meldware.mail.api.Folder f2 = mbox.getFolder(path);
                assertEquals("INBOX", f2.getName());                
            }
        });        
    }
    
    public void testGetMessages() {
        
        final String alias = "tom";
        final String to = "<noone@nowhere.com>";
        final String from = "<noone@nowhere.com>";
        final String subject = "Test Subject";
        final String body = "Test Email Body";
        
        txr.requiresNew(new VoidTx() {
            public void run() {
                service.createMailbox(alias);
            }
        });
        
        txr.requiresNew(new VoidTx() {
            public void run() {
                Mailbox mbox = service.getMailboxByAlias(alias);
                Folder f = mbox.getDefaultInFolder();
                Mail mail1 = MailUtil.create(to, from, subject, body);
                service.createMail(f, new MessageData(mail1));
                Mail mail2 = MailUtil.create(to, from, subject, body);
                service.createMail(f, new MessageData(mail2));
            }
        });
        
        txr.requiresNew(new VoidTx() {
            public void run() {
                Mailbox mailbox = service.getMailboxByAlias(alias);
                org.buni.meldware.mail.api.Mailbox mbox = new org.buni.meldware.mail.mailbox.MailboxProxy(service, mailbox, Hints.NONE);
                org.buni.meldware.mail.api.Folder f = mbox.getDefault();
                List<org.buni.meldware.mail.api.FolderMessage> ms = f.getMessages();
                assertEquals(2, ms.size());
                assertEquals("<noone@nowhere.com>", ms.get(0).getTo().get(0).toSMTPString());
                assertEquals("<noone@nowhere.com>", ms.get(1).getTo().get(0).toSMTPString());
            }
        });        
    }
    
}