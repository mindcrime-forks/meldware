package org.buni.meldware.mail.imap4;

import java.util.Properties;

import javax.mail.FetchProfile;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;

import junit.framework.TestSuite;

import org.buni.meldware.test.JMXTestWrapper;

import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPStore;

public class TestIMAP2 extends TestIMAP {

    public static TestSuite suite() {
        return JMXTestWrapper.suite(TestIMAP2.class);
    }
    
    public TestIMAP2(String name) {
        super(name);
    }
    
    private IMAPStore getIMAPStore() throws MessagingException {
        Properties p = System.getProperties();
        Session session = Session.getInstance(p);
        session.setDebug(true);
        
        Store store = null;
        
        store = session.getStore("imap");
        store.connect(ds.getHost(), ds.getImapPort(), ds.getUser(), ds.getPass());
        return (IMAPStore) store;
    }
    
    
    public void testEnvelope2() throws Exception {
        createMailbox2Messages();

        IMAPStore store = getIMAPStore();
        
        IMAPFolder folder = (IMAPFolder) store.getFolder("INBOX");
        folder.open(Folder.READ_ONLY);
        FetchProfile fp = new FetchProfile();
        fp.add(FetchProfile.Item.ENVELOPE);
        
        int count = folder.getMessageCount();
        
        Message[] messages = new Message[count];
        messages[0] = folder.getMessage(1);
        messages[1] = folder.getMessage(2);
        
        folder.fetch(messages, fp);
        
    }

    public void testEnvelope() throws Exception {
        createMailbox2Messages();
        
        Store store = getIMAPStore();
        
        Folder f = null;
        f = store.getDefaultFolder();
        f = f.getFolder("INBOX");
        f.open(Folder.READ_ONLY);
        
        Message[] ms = f.getMessages();
        
        for (Message m : ms) {
            System.out.printf("Subject %s\n", m.getSubject());
        }
        
        f.close(false);
        store.close();            
    }    
}