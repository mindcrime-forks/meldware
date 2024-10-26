/**
 * 
 */
package org.buni.meldware.mail.protocol.smtp;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.bsf.BSFException;
import org.buni.meldware.mail.protocol.RubyProtocolRunner;

/**
 * @author Michael Barker
 *
 */
public class TestSMTPProtocol02 extends TestCase {

    File dir = new File("mail/mailtest/src/java/org/buni/meldware/mail/protocol/smtp");
    static long sleep = 0;
        
    public TestSMTPProtocol02(String name) {
        super(name);
    }
    
    public static void setSleep(long l) {
        sleep = l;
    }
    
    public void setUp() {
        try {
            Thread.sleep(sleep);
        } catch (InterruptedException e) {}
    }
    
    private void sendMail(String name) throws BSFException {
        File f = new File(dir, "smtp_gen.rb");
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("address", "<tom@localhost>");
        params.put("filename", name);
        RubyProtocolRunner.run("localhost", 9025, f, params);
    }
    
    public void testProtocol01() throws Throwable {
        sendMail("mail/mailtest/etc/layout/text-attached.msg");
    }

    public void testProtocol02() throws Throwable {
        sendMail("mail/mailtest/etc/layout/single-image-attached.msg");
    }

    public void testProtocol03() throws Throwable {
        sendMail("mail/mailtest/etc/layout/single-image-multi.msg");
    }

    public void testProtocol04() throws Throwable {
        sendMail("mail/mailtest/etc/layout/single-image-inline.msg");
    }
    
    public void testProtocol05() throws Throwable {
        sendMail("mail/mailtest/etc/layout/single-image.msg");
    }
    
    public void testProtocol06() throws Throwable {
        
        File f = new File(dir, "smtp10.rb");
        RubyProtocolRunner.run("localhost", 9025, f);
        
    }
}
