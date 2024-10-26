/**
 * 
 */
package org.buni.meldware.mail.protocol.smtp;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.buni.meldware.mail.protocol.RubyProtocolRunner;

/**
 * @author Michael Barker
 *
 */
public class TestSMTPProtocol03 extends TestCase {

    File dir = new File("mail/mailtest/src/java/org/buni/meldware/mail/protocol/smtp");
        
    public TestSMTPProtocol03(String name) {
        super(name);
    }
    
    
    public void testProtocol01() throws Throwable {
        
        File[] mails = new File("mail/mailtest/etc/layout").listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".msg") || name.endsWith(".eml");
            }
        });
        
        File f = new File(dir, "smtp_gen.rb");
        
        for (File mail : mails) {
            Map<String,Object> params = new HashMap<String,Object>();
            String filename = "mail/mailtest/etc/layout/" + mail.getName();
            params.put("address", "<tom@localhost>");
            params.put("filename", filename);
            System.out.printf("Mail: %s\n", filename);
            RubyProtocolRunner.run("localhost", 9025, f, params);
        }
    }

}
