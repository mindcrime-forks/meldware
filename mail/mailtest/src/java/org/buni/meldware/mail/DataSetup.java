package org.buni.meldware.mail;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.buni.meldware.mail.mailbox.Mailbox;
import org.buni.meldware.mail.mailbox.MailboxService;
import org.buni.meldware.mail.tx.TxRunner;
import org.buni.meldware.mail.tx.TxRunnerFactory;
import org.buni.meldware.mail.tx.VoidTx;
import org.jboss.mx.util.MBeanServerLocator;

public class DataSetup {

    TxRunner txr = TxRunnerFactory.create();
    MailboxService service = MailUtil.getMailbox();
    
    protected void createMailbox(final String user) {
        
        final String alias = user + "@localhost";
        txr.requiresNew(new VoidTx() {
            public void run() {
                try {
                    MBeanServer server = MBeanServerLocator.locateJBoss();
                    server.invoke(new ObjectName("meldware.mail:type=MailServices,name=UserEditor"), 
                            "addUser", new Object[] { user, user },
                            new String[] {"java.lang.String", "java.lang.String"});
                    server.invoke(new ObjectName("meldware.mail:type=MailServices,name=UserEditor"), 
                            "addRole", new Object[] { user, "mailuser" },
                            new String[] {"java.lang.String", "java.lang.String"});
                } catch (Exception e) {
                    throw new MailException(e);
                }
            }
        });

        txr.requiresNew(new VoidTx() {
            public void run() {
                service.createMailbox(user);
            }
        });

        txr.requiresNew(new VoidTx() {
            public void run() {
                Mailbox mbox = service.getMailboxByAlias(user);
                service.createAlias(mbox.getId(), alias);
            }
        });
    }

}
