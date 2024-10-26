package org.buni.meldware.mail.mailbox;

import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.buni.meldware.test.JMXTestWrapper;
import org.buni.meldware.mail.tx.TxRunner;
import org.buni.meldware.mail.tx.TxRunnerFactory;
import org.buni.meldware.mail.tx.VoidTx;

public class TestMailboxQueries extends TestCase {

    
    public static TestSuite suite() {
        return JMXTestWrapper.suite(TestMailboxQueries.class);
        //return JMXTestWrapper.suite(TestMailboxQueries.class, "testFetchFlags02");
    }
    
    EntityManager em;
    TxRunner txr;
    
    public TestMailboxQueries(String name) {
        super(name);
    }
    
    public void setUp() throws NamingException {
        InitialContext ctx = new InitialContext();
        em = ((EntityManager) ctx.lookup("java:/EntityManagers/mail"));
        txr = TxRunnerFactory.create();
    }
    
    public void testFetchFlags01() {
        txr.requiresNew(new VoidTx() { 
            public void run() {
                String query = "from MessageData m join fetch m.flags";
                List l = em.createQuery(query).getResultList();
                assertEquals(1, l.size());
            }
        });
    }
    
    public void testFetchFlags02() {
        txr.requiresNew(new VoidTx() { 
            public void run() {
                String query = "from MessageData m";
                List l = em.createQuery(query).getResultList();
                assertTrue(l.size() > 0);
                MessageData m = (MessageData) l.get(0);
                //Set<Flag> fs = m.getFlags();
                //fs.iterator();
            }
        });
    }
    
}
