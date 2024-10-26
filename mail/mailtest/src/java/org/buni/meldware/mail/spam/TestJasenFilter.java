package org.buni.meldware.mail.spam;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.MalformedURLException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.buni.meldware.mail.mailbox.MailboxService;
import org.buni.meldware.mail.mailbox.MailboxServiceImpl;
import org.buni.meldware.mail.message.Mail;
import org.buni.meldware.mail.message.MailBodyManagerImpl;
import org.buni.meldware.mail.store.memory.MemoryStore;
import org.buni.meldware.mail.util.io.Copier;
import org.buni.meldware.mail.util.io.SimpleCopier;
import org.jasen.error.JasenException;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public class TestJasenFilter extends TestCase {

    Copier c = new SimpleCopier();
    final static File dir = new File("mail/mailtest/etc/layout");
    final static File cdir = new File("mail/mailjmx/etc/layout");
    static MailBodyManagerImpl mgr;
    static JasenFilter filter;
        
    private final static String s = "<Properties>" 
        + "</Properties>";
    
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(TestJasenFilter.class);
        
        TestSetup wrapper = new TestSetup(suite) {
            protected void setUp() throws Exception {
                mgr = new MailBodyManagerImpl();
                mgr.setStore(new MemoryStore());
                filter = new JasenFilterImpl();
                MailboxService ms = new MailboxServiceImpl(5);
                ms.setBodyManager(mgr);
                filter.setMailboxService(ms);
                DocumentBuilder b = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                Document d = b.parse(new InputSource(new StringReader(s)));
                filter.setJasenConfig(d.getDocumentElement());
                filter.start();
            }
        };
        return wrapper;
    }
    
    public TestJasenFilter(String name) {
        super(name);
    }
    
    public void setUp() throws JasenException, MalformedURLException {
    }
        
    public void testTextMessage() throws FileNotFoundException {
        InputStream in = new BufferedInputStream(new FileInputStream(new File(dir, "text-mail.msg")));
        Mail m = Mail.create(mgr, in, c);
        m = (Mail) filter.send(m);
        String[] s = m.getMailHeaders().getHeader("X-meldware-spam-score");
        assertNotNull(s);
        assertTrue(s.length > 0);
        System.out.println("Score: " + s[0]);
    }
    
    public void testMimeMessage() throws FileNotFoundException {
        InputStream in = new BufferedInputStream(new FileInputStream(new File(dir, "single-image-attached.msg")));
        Mail m = Mail.create(mgr, in, c);
        m = (Mail) filter.send(m);
        String[] s = m.getMailHeaders().getHeader("X-meldware-spam-score");
        assertNotNull(s);
        assertTrue(s.length > 0);
        System.out.println("Score: " + s[0]);
    }
}
