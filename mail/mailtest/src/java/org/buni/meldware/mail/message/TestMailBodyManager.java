/**
 * 
 */
package org.buni.meldware.mail.message;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.buni.meldware.mail.MailUtil;
import org.buni.meldware.test.JMXTestWrapper;
import org.buni.meldware.mail.tx.TxRunner;
import org.buni.meldware.mail.tx.TxRunnerFactory;
import org.buni.meldware.mail.tx.VoidTx;

/**
 * @author Michael.Barker
 *
 */
public class TestMailBodyManager extends TestCase {

    public static TestSuite suite() {
        return JMXTestWrapper.suite(TestMailBodyManager.class);
    }
    
    private TxRunner txr = TxRunnerFactory.create();
    
    public TestMailBodyManager(String name) {
        super(name);
    }
    
    public void testReadWrite() {
        
        byte[] data = createData(4096, (byte)96, (byte)188);
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        
        MailBodyManager mgr = MailUtil.getMailBodyManager();
        Body b = mgr.createMailBody();
        
        mgr.read(b, bais);
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        mgr.write(b, baos);
        
        assertTrue(Arrays.equals(data, baos.toByteArray()));
        assertEquals(4096L, b.getSize());
    }
    
    private byte[] createData(int size, byte min, byte max) {
        
        byte[] data = new byte[size];
        byte diff = (byte)(max - min);
        for (int i = 0; i < data.length; i++) {
            data[i] = (byte)((i % diff) + min);
        }
        
        return data;
    }
    
    public void testPurge() {
    	
        final MailBodyManager mgr = MailUtil.getMailBodyManager();
        
        final byte[] data = createData(4096, (byte)96, (byte)188);
        
    	txr.required(new VoidTx() {
			public void run() {
				Body body = mgr.createMailBody();
		        ByteArrayInputStream bais = new ByteArrayInputStream(data);
				mgr.read(body, bais);
			}
    	});
        
    	int n = mgr.getStore().purge();
    	assertTrue(n > 0);
    }
    
}
