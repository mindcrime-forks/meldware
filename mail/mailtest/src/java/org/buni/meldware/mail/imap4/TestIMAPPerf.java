package org.buni.meldware.mail.imap4;

import java.io.IOException;
import java.io.InputStream;

import org.buni.meldware.mail.test.DataSet;
import org.buni.meldware.mail.test.DataSetFactory;
import org.columba.ristretto.imap.IMAPException;
import org.columba.ristretto.imap.IMAPProtocol;
import org.columba.ristretto.log.RistrettoLogger;

import junit.framework.TestCase;

public class TestIMAPPerf extends TestCase {
    
    static DataSet ds = DataSetFactory.get("tom");

    public void testLargeMessage() throws IOException, IMAPException {
        RistrettoLogger.setLogStream(System.out);
        IMAPProtocol protocol = new IMAPProtocol(ds.getHost(), ds.getImapPort());
        protocol.openPort();
        protocol.login(ds.getUser(), ds.getPass().toCharArray());
        protocol.select("INBOX");
        InputStream in1 = protocol.fetchMessage(1);
        while (in1.read() != -1);
        protocol.close();
        protocol.logout();        
        
    }
}
