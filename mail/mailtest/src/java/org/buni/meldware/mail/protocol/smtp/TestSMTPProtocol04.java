/**
 * 
 */
package org.buni.meldware.mail.protocol.smtp;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;

import org.apache.bsf.BSFException;
import org.buni.meldware.mail.protocol.RubyProtocolRunner;

/**
 * @author Michael Barker
 *
 */
public class TestSMTPProtocol04 extends TestCase {

    File dir = new File("mail/mailtest/src/java/org/buni/meldware/mail/protocol/smtp");
        
    public TestSMTPProtocol04(String name) {
        super(name);
    }
    
    private static final int NUM_MAILS = 20;
    
    public void testProtocol01() throws Throwable {
        
        ExecutorService e = Executors.newFixedThreadPool(10);
        
        final File f = new File(dir, "smtp_gen.rb");
        for (int j = 0; j < 10; j++) {
            e.execute(new Runnable() {
               public void run() {
                   RubyProtocolRunner runner = new RubyProtocolRunner();
                   Map<String,Object> params = new HashMap<String,Object>();
                   //String email = "<user_" + j + "@localhost>";
                   String email = "<tom@localhost>";
                   params.put("address", email);
                   params.put("host", "localhost");
                   params.put("port", 9025);
                   //int n = r.nextInt(MAILS.length);
                   //params.put("filename", MAILS[n]);
                   params.put("count", NUM_MAILS);
                   try {
                       runner.run(f, params);
                   } catch (BSFException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                  } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
               }
            });
        }
        
        e.shutdown();
        //e.awaitTermination(20, TimeUnit.MINUTES);
    }

}
