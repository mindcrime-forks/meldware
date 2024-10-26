/**
 * 
 */
package org.buni.meldware.mail.wcap;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.buni.meldware.mail.protocol.RubyProtocolRunner;

/**
 * @author Michael Barker
 *
 */
public class TestWCAP extends TestCase {

    File dir = new File("mail/mailtest/src/java/org/buni/meldware/mail/wcap");
        
    public TestWCAP(String name) {
        super(name);
    }
    
    public void testLoginCreate() throws Throwable {
        
        File f = new File(dir, "wcap_login_create.rb");
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("path", dir.getAbsolutePath());
        params.put("user", "jdoe");
        params.put("pass", "jdoe");
        RubyProtocolRunner.run("sd-calendar.staroffice.de", 80, f, params);
    }
}
