package org.buni.meldware.mail.imap4;

import java.io.InputStream;

import junit.framework.TestCase;

import org.buni.meldware.mail.test.DataSet;
import org.buni.meldware.mail.test.DataSetFactory;
import org.columba.ristretto.imap.IMAPProtocol;

public class TestIMAP4 extends TestCase {
    static DataSet ds = DataSetFactory.get("tom");
    
    IMAPProtocol protocol;
    
    public void setUp() throws Exception {
        protocol = new IMAPProtocol(ds.getHost(), ds.getImapPort());
    }

    public void testPartial() throws Exception {
        
        protocol.openPort();
        protocol.login(ds.getUser(), ds.getPass().toCharArray());
        protocol.select("INBOX");
        InputStream in = protocol.fetchBody(3, new Integer[] { 0 });
        int numBytes = 0;
        byte[] b = new byte[2048];
        while (numBytes != -1) {
            numBytes = in.read(b);
        }
        protocol.close();
        protocol.logout();        
    }
}
