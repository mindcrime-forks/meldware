package org.buni.meldware.mail.message;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.buni.meldware.mail.MailException;
import org.buni.meldware.mail.mailbox.MessageData;
import org.buni.meldware.mail.mailbox.MessageDataUtil;
import org.buni.meldware.mail.store.memory.MemoryStore;
import org.buni.meldware.mail.util.io.SimpleCopier;


public class TestMessages extends TestCase {
    
    private static File dir = new File("mail/mailtest/etc/layout");
    
    public static TestSuite suite() {
        String[] files = dir.list(new FilenameFilter() {

            public boolean accept(File dir, String name) {
                return name.endsWith(".msg") || name.endsWith(".eml");
            }
            
        });
        
        TestSuite ts = new TestSuite();
        for (String file : files) {
            ts.addTest(new TestMessages(file));
        }
        return ts;
    }
    
    
    
    public TestMessages(String name) {
        super(name);
    }

    @Override
    protected void runTest() throws IOException {
        applyTest(getName());
    }
    
    public void applyTest(String name) throws IOException {
        MessageDataUtil mdf;
        MailBodyManagerImpl mbm;
        mbm = new MailBodyManagerImpl();
        mbm.setStore(new MemoryStore());
        mdf = new MessageDataUtil(mbm);
        InputStream in = new BufferedInputStream(new FileInputStream(new File(dir, name)));
        Mail m = Mail.create(mbm, in, new SimpleCopier());
        MessageData message = mdf.create(m, true);
        MessageData message2 = new MessageData(message);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayOutputStream out2 = new ByteArrayOutputStream();
        try {
            InputStream in2 = mdf.getInputStream(message, true);
            new SimpleCopier().copy(new BufferedInputStream(in2), out, 8192);
            mdf.printMessage(message, true, out2, new SimpleCopier());
        } catch (IOException e) {
            throw new MailException(e);
        }
        System.out.println(out.size() + ": " + message2.getSize());
        byte[] b1 = out.toByteArray();
        byte[] b2 = out2.toByteArray();
        //String s1 = new String(b1);
        //String s2 = new String(b2);        
        for (int i = 0; i < b1.length && i < b2.length; i++) {
            if (b1[i] != b2[i]) {
                int start1 = Math.max(0, i - 5);
                int end1 = Math.min(b1.length - i, 10);
                String s1 = new String(b1, start1, end1);
                int start2 = Math.max(0, i - 5);
                int end2 = Math.min(b2.length - i, 10);
                String s2 = new String(b2, start2, end2);
                System.out.println("A: [" + s1 + "]");
                System.out.println("B: [" + s2 + "]");
            }
            assertEquals(b1[i], b2[i]);
        }
        //assertEquals(new String(out.toByteArray()), new String(out2.toByteArray()));
        assertEquals(out.size(), message2.getSize());
    }
    
}
