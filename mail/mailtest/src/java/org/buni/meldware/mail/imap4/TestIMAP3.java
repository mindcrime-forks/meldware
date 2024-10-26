package org.buni.meldware.mail.imap4;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.buni.meldware.mail.MailUtil;
import org.buni.meldware.mail.mailbox.Folder;
import org.buni.meldware.mail.mailbox.Mailbox;
import org.buni.meldware.mail.mailbox.MailboxService;
import org.buni.meldware.mail.mailbox.MessageData;
import org.buni.meldware.mail.message.Mail;
import org.buni.meldware.test.JMXTestWrapper;
import org.buni.meldware.mail.test.DataSet;
import org.buni.meldware.mail.test.DataSetFactory;
import org.buni.meldware.mail.tx.TxRunner;
import org.buni.meldware.mail.tx.TxRunnerFactory;
import org.buni.meldware.mail.tx.VoidTx;
import org.columba.ristretto.imap.IMAPProtocol;

public class TestIMAP3 extends TestCase {

    static DataSet ds = DataSetFactory.get("tom");
    TxRunner txr = TxRunnerFactory.create();
    MailboxService service = MailUtil.getMailbox();
    IMAPProtocol protocol;
    
    public static TestSuite suite() {
        return JMXTestWrapper.suite(TestIMAP3.class);
    }
    
    public TestIMAP3(String name) {
        super(name);
    }

    public void setUp() throws Exception {
        MailUtil.clearData();
        protocol = new IMAPProtocol(ds.getHost(), ds.getImapPort());
    }
    
    protected void createMailboxNMessages(final int n) {
        final String to = "<noone@nowhere.com>";
        final String from = "<noone@nowhere.com>";
        final String subject = "Test Subject";
        final String body = "Test Email Body";
        
        txr.requiresNew(new VoidTx() {
            public void run() {
                Mailbox mbox = service.createMailbox(ds.getUser());
                service.createAlias(mbox.getId(), ds.getAlias());
            }
        });
        
        for (int i = 0; i < n; i++) {
            txr.requiresNew(new VoidTx() {
                public void run() {
                    Mailbox mbox = service.getMailboxByAlias(ds.getAlias());
                    Folder f = mbox.getDefaultInFolder();
                        Mail mail1 = MailUtil.create(to, from, subject, body);
                        MessageData md1 = new MessageData(mail1);
                        service.createMail(f, md1);
                }
            });
            if ((i + 1) % 100 == 0) {
                System.out.printf("Loaded %d messages\n", i+1);
            }
        }
    }
    
    public void testLoadData() {
        createMailboxNMessages(10);
    }
}
