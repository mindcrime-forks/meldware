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
public class TestPOPProtocol01 extends TestCase {

    File dir = new File("mail/mailtest/src/java/org/buni/meldware/mail/protocol/smtp");
    static long sleep = 0;
        
    public TestPOPProtocol01(String name) {
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
    
    public void testProtocol01() throws Throwable {
        addUser("tom", "tom");
    }
    
    public void addUser(String user, String pass) throws BSFException {
        File f = new File(dir, "pop_login.rb");
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("user", user);
        params.put("pass", pass);
        params.put("filename", "pop_login.rb");
        RubyProtocolRunner.run("mail.buni.org", 110, f, params);
    }
}
