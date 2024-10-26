package org.buni.meldware.mail.message;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.List;

import junit.framework.TestCase;

import org.buni.meldware.mail.mailbox.MessageBody;
import org.buni.meldware.mail.mailbox.MessageData;
import org.buni.meldware.mail.mailbox.MessageDataUtil;
import org.buni.meldware.mail.store.memory.MemoryStore;
import org.buni.meldware.mail.util.io.SimpleCopier;

public class TestMimeParser extends TestCase {

    private File dir = new File("mail/mailtest/etc/layout");
    
    MessageDataUtil mdf;
    MailBodyManagerImpl mbm;
    
    public void setUp() throws MalformedURLException {
        mbm = new MailBodyManagerImpl();
        mbm.setStore(new MemoryStore());
        mdf = new MessageDataUtil(mbm);
    }
    
    private int getMessageSize(MessageData message) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        mdf.printMessage(message, true, out, new SimpleCopier());    
        return out.size();
    }
    
    private int getBodySize(MessageBody message) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        mdf.printBodyPart(message, true, out, new SimpleCopier());    
        return out.size();
    }
    
    public void testMultipartAttachedMesssage() throws Exception {
        InputStream in = new FileInputStream(new File(dir, 
                "single-image-attached.msg"));
        
        MessageData message = mdf.createMimeMessage(in);
        List<MessageBody> bodyParts1 = message.getBody();
        assertEquals("Should have 2 body parts", 2, bodyParts1.size());
        assertEquals("1st part should be simple", 0, bodyParts1.get(0).getChildren().size());
        assertEquals("2nd part should be complex", 2, bodyParts1.get(1).getChildren().size());
        assertNotNull("2nd part should be message", bodyParts1.get(1).getHeader());
        List<MessageBody> bodyParts2 = bodyParts1.get(1).getChildren();
        assertEquals("1st part should be simple", 0, bodyParts2.get(0).getChildren().size());
        assertEquals("2nd part should be simple", 0, bodyParts2.get(1).getChildren().size());
        
        assertEquals("Size is wrong", message.getSize(), getMessageSize(message));
        MessageBody mb1 = bodyParts1.get(0);
        assertEquals("Size is wrong", mb1.getSize(), getBodySize(mb1));
        MessageBody mb2 = bodyParts1.get(1);
        assertEquals("Size is wrong", mb2.getSize(), getBodySize(mb2));
        MessageBody mb3 = bodyParts2.get(0);
        assertEquals("Size is wrong", mb3.getSize(), getBodySize(mb3));
        MessageBody mb4 = bodyParts2.get(1);
        assertEquals("Size is wrong", mb4.getSize(), getBodySize(mb4));
    }
    
    public void testNestedTextMesssage() throws Exception {
        InputStream in = new FileInputStream(new File(dir, 
                "text-attached.msg"));
        
        MessageData message = mdf.createMimeMessage(in);
        List<MessageBody> bodyParts1 = message.getBody();
        assertEquals("Should have 2 body parts", 2, bodyParts1.size());
        assertEquals("1st part should be simple", 0, bodyParts1.get(0).getChildren().size());
        assertNotNull("2nd part should be message", bodyParts1.get(1).getHeader());
        assertEquals("Should contain seperate body part", 1, bodyParts1.get(1).getChildren().size());
        assertEquals("Size is incorrect", message.getSize(), getMessageSize(message));
    }
    
    public void testNestedMultipartMessage() throws Exception {
        MessageData message = createMessage("single-image-multi.msg", true);
        assertNotNull("Date should not be null", message.getTimestamp());
        List<MessageBody> bodyParts1 = message.getBody();
        assertEquals("Should have 2 body parts", 2, bodyParts1.size());
        assertEquals("1st part should be simple", 0, bodyParts1.get(0).getChildren().size());
        String mh1_0 = bodyParts1.get(0).getMimeheader();
        assertTrue("Should have a mime header", mh1_0 != null && mh1_0.length() > 0);
        assertEquals("2nd part should be complex", 2, bodyParts1.get(1).getChildren().size());
        List<MessageBody> bodyParts2 = bodyParts1.get(1).getChildren();
        assertEquals("1st part should be simple", 0, bodyParts2.get(0).getChildren().size());
        assertEquals("2nd part should be simple", 0, bodyParts2.get(1).getChildren().size());
        assertEquals("Size is incorrect", message.getSize(), getMessageSize(message));
    }
    
    public void testMultipartMessage() throws Exception {
        InputStream in = new FileInputStream(new File(dir, "test5.msg"));

        MessageData message = mdf.createMimeMessage(in);
        List<MessageBody> bodyParts1 = message.getBody();
        assertEquals("Should have 2 body parts", 2, bodyParts1.size());
        assertEquals("1st part should be simple", 0, bodyParts1.get(0).getChildren().size());
        String mh1_0 = bodyParts1.get(0).getMimeheader();
        assertTrue("Should have a mime header", mh1_0 != null && mh1_0.length() > 0);
        assertEquals("2nd part should be simple", 0, bodyParts1.get(1).getChildren().size());
        String mh1_1 = bodyParts1.get(1).getMimeheader();
        assertTrue("Should have a mime header", mh1_1 != null && mh1_1.length() > 0);
        assertEquals("Size is incorrect", message.getSize(), getMessageSize(message));
    }
    
    public void testInlineMessage() throws Exception {
        InputStream in = new FileInputStream(new File(dir, "single-image-inline.msg"));

        MessageData message = mdf.createMimeMessage(in);
        assertEquals("Size is incorrect", message.getSize(), getMessageSize(message));
    }
    
    public void testAttachedMessage() throws Exception {
        InputStream in = new FileInputStream(new File(dir, "single-image.msg"));

        MessageData message = mdf.createMimeMessage(in);
        assertEquals("Size is incorrect", message.getSize(), getMessageSize(message));
    }
    
    public void test3rdLine() throws Exception {
        InputStream in = new FileInputStream(new File(dir, "3rdline.msg"));

        MessageData message = mdf.createMimeMessage(in);
        assertNotNull(message.getBoundary());
        assertEquals("Size is incorrect", message.getSize(), getMessageSize(message));
    }
    
    public void testInvalidDate() throws Exception {
        MessageData message = createMessage("text-attached-invalid-date.msg", false);
        assertNotNull("Date should not be null", message.getTimestamp());
    }
    
    public void testMissingDate() throws Exception {
        MessageData message = createMessage("text-attached-missing-date.msg", false);
        assertNotNull("Date should not be null", message.getTimestamp());
    }
    
    public void testPerformance() throws Exception {
        
        InputStream in = new FileInputStream(new File(dir, "test2.eml"));
        long t0 = System.currentTimeMillis();
        mdf.createMimeMessage(in);
        long t1 = System.currentTimeMillis();
        System.out.println("Time: " + (t1-t0));
    }
    
    private MessageData createMessage(String filename, boolean parseMime) throws FileNotFoundException {
        InputStream in = new BufferedInputStream(
                new FileInputStream(new File(dir, filename)));

        //MessageData message = mdf.createMimeMessage(in);
        Mail m = Mail.create(mbm, in, new SimpleCopier());
        return mdf.create(m, parseMime);
    }
    
}