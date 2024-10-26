/**
 * 
 */
package org.buni.meldware.mail.protocol.smtp;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.buni.meldware.mail.protocol.RubyProtocolRunner;

/**
 * @author Michael.Barker
 *
 */
public class TestSMTPProtocol extends TestCase {

    File dir = new File("mail/mailtest/src/java/org/buni/meldware/mail/protocol/smtp");
    
    public void testProtocol01() throws Throwable {
        
        File f = new File(dir, "smtp01.rb");
        RubyProtocolRunner.run("localhost", 9025, f);
        
    }

    public void testProtocol02() throws Throwable {
        
        File f = new File(dir, "smtp02.rb");
        RubyProtocolRunner.run("localhost", 9025, f);
        
    }
    
    public void testProtocol03() throws Throwable {
        
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("domain", "localhost");
        File f = new File(dir, "smtp03.rb");
        RubyProtocolRunner.run("localhost", 9025, f, params);
        
    }
    
    public void testProtocol04() throws Throwable {
        
        File f = new File(dir, "smtp04.rb");
        RubyProtocolRunner.run("localhost", 9025, f);
        
    }
}
