package org.buni.meldware.mail.data;

import java.util.Collection;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.buni.meldware.mail.MailUtil;
import org.buni.meldware.mail.api.Hints;
import org.buni.meldware.mail.api.Range;
import org.buni.meldware.mail.mailbox.Folder;
import org.buni.meldware.mail.mailbox.FolderEntry;
import org.buni.meldware.mail.mailbox.Mailbox;
import org.buni.meldware.mail.mailbox.MailboxService;
import org.buni.meldware.mail.mailbox.MessageData;
import org.buni.meldware.mail.message.Mail;
import org.buni.meldware.test.JMXTestWrapper;
import org.buni.meldware.mail.tx.TxRunner;
import org.buni.meldware.mail.tx.TxRunnerFactory;
import org.buni.meldware.mail.tx.VoidTx;

public class TestData extends TestCase {

    TxRunner txr = TxRunnerFactory.create();
    MailboxService service = MailUtil.getMailbox();
    
    public static TestSuite suite() {
        return JMXTestWrapper.suite(TestData.class);
    }
    
    public void setUp() throws Exception {
        MailUtil.clearData();
        final String to = "<noone@nowhere.com>";
        final String from = "<noone@nowhere.com>";
        final String subject = "Test Subject";
        final String body = "Test Email Body";
        final String name = "tom";
        final String alias = "tom@localhost";
        
        txr.requiresNew(new VoidTx() {
            public void run() {
                Mailbox mbox = service.createMailbox(name);
                service.createAlias(mbox.getId(), alias);
                Folder f = mbox.getDefaultInFolder();
                Mail mail1 = MailUtil.create(to, from, subject, body);
                service.createMail(f, new MessageData(mail1));
                Mail mail2 = MailUtil.create(to, from, subject, body);
                service.createMail(f, new MessageData(mail2));
                Collection<FolderEntry> fes = service.getMessages(f, Range.EMPTY, Hints.FLAGS);
                FolderEntry fe = fes.iterator().next();
                fe.setSeen(true);
                service.updateFolderEntry(fe);
            }
        });        
    }
    
    public TestData(String name) {
        super(name);
    }
    
    public void testNoop() {
        // No-op;
    }
}
