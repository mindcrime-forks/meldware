/*
 * Bunisoft the Open Source Communications Company
 * Copyright 2006, Bunisoft Inc., and individual contributors as
 * indicated by the @authors tag.  See the copyright.txt in the 
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; version 2.1 of
 * the License.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.buni.meldware.mail.pop3;

import java.util.Collection;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.buni.meldware.mail.MailUtil;
import org.buni.meldware.mail.api.Hints;
import org.buni.meldware.mail.api.Range;
import org.buni.meldware.mail.mailbox.Folder;
import org.buni.meldware.mail.mailbox.FolderEntry;
import org.buni.meldware.mail.mailbox.Mailbox;
import org.buni.meldware.mail.mailbox.MailboxService;
import org.buni.meldware.mail.mailbox.MessageData;
import org.buni.meldware.mail.message.Mail;
import org.buni.meldware.test.JMXTestWrapper;
import org.buni.meldware.mail.test.DataSet;
import org.buni.meldware.mail.test.DataSetFactory;
import org.buni.meldware.mail.tx.TxRunner;
import org.buni.meldware.mail.tx.TxRunnerFactory;
import org.buni.meldware.mail.tx.VoidTx;
import org.buni.meldware.mail.util.io.IOUtil;
import org.columba.ristretto.pop3.POP3Protocol;
import org.columba.ristretto.pop3.ScanListEntry;
import org.columba.ristretto.pop3.UidListEntry;

/**
 * @author Michael.Barker
 *
 */
public class TestPOP extends TestCase {

    static DataSet ds = DataSetFactory.get("tom");
    
    TxRunner txr = TxRunnerFactory.create();
    MailboxService service = MailUtil.getMailbox();
    POP3Protocol protocol;
    
    public static TestSuite suite() {
        return JMXTestWrapper.suite(TestPOP.class);
        //return JMXTestWrapper.suite(TestPOP.class, "testList");
    }
    
    public TestPOP(String name) {
        super(name);
    }
    
    public void setUp() throws Exception {
        MailUtil.clearData();
        protocol = new POP3Protocol(ds.getHost(), ds.getPopPort());
    }
    
    // TODO Consider moving this into the dataset.
    protected void createMailbox2Messages() {
        final String to = "<noone@nowhere.com>";
        final String from = "<noone@nowhere.com>";
        final String subject = "Test Subject";
        final String body = "Test Email Body";
        
        txr.requiresNew(new VoidTx() {
            public void run() {
                service.createMailbox(ds.getUser());
            }
        });
        
        txr.requiresNew(new VoidTx() {
            public void run() {
                Mailbox mbox = service.getMailboxByAlias(ds.getUser());
                service.createAlias(mbox.getId(), ds.getAlias());
                Folder f = mbox.getDefaultInFolder();
                Mail mail1 = MailUtil.create(to, from, subject, body);
                MessageData md1 = new MessageData(mail1);
                FolderEntry fe = service.createMail(f, md1);
                fe.setFlagged(true);
                
                Mail mail2 = MailUtil.create(to, from, subject, body);
                service.createMail(f, new MessageData(mail2));
                
                Collection<FolderEntry> mds = service.getMessages(f, Range.EMPTY, Hints.FLAGS);
                FolderEntry md = mds.iterator().next();
                md.setSeen(true);
                //service.updateFolderEntry(md);
            }
        });
    }
    
    public void testConnect() throws Exception {
        service.createMailbox(ds.getAlias());
        protocol.openPort();
        protocol.userPass(ds.getUser(), ds.getPass().toCharArray());
        protocol.quit();
    }
    
    public void testList() throws Exception {
        createMailbox2Messages();
        protocol.openPort();
        protocol.userPass(ds.getUser(), ds.getPass().toCharArray());
        ScanListEntry[] values = protocol.list();
        assertEquals(2, values.length);
        protocol.quit();
    }
    
    public void testList1() throws Exception {
        createMailbox2Messages();
        protocol.openPort();
        protocol.userPass(ds.getUser(), ds.getPass().toCharArray());
        ScanListEntry value = protocol.list(1);
        assertTrue(100 < value.getSize());
        protocol.quit();
    }
    
    public void testRetr() throws Exception {
        createMailbox2Messages();
        protocol.openPort();
        protocol.userPass(ds.getUser(), ds.getPass().toCharArray());
        ScanListEntry[] values = protocol.list();
        assertEquals(2, values.length);
        String s1 = IOUtil.toString(protocol.retr(1), "US-ASCII");
        assertTrue("Message is too small", 100 < s1.length());
        String s2 = IOUtil.toString(protocol.retr(2), "US-ASCII");
        assertTrue("Message is too small", 100 < s2.length());
        protocol.quit();
    }
    
    public void testPop() throws Exception {
        createMailbox2Messages();
        protocol.openPort();
        protocol.userPass(ds.getUser(), ds.getPass().toCharArray());
        ScanListEntry[] values = protocol.list();
        assertEquals(2, values.length);
        String s1 = protocol.top(1, 5).toString();
        assertTrue("Message is too small", 10 < s1.length());
        String s2 = protocol.top(2, 5).toString();
        assertTrue("Message is too small", 10 < s2.length());
        protocol.quit();
    }
    
    public void testDele() throws Exception {
        createMailbox2Messages();
        protocol.openPort();
        protocol.userPass(ds.getUser(), ds.getPass().toCharArray());
        protocol.dele(1);
        protocol.dele(2);
        protocol.quit();
    }
    
    public void testStat() throws Exception {
        createMailbox2Messages();
        protocol.openPort();
        protocol.userPass(ds.getUser(), ds.getPass().toCharArray());
        int[] is = protocol.stat();
        assertEquals(2, is[0]);
        protocol.quit();
    }
    
    public void testNoop() throws Exception {
        createMailbox2Messages();
        protocol.openPort();
        protocol.userPass(ds.getUser(), ds.getPass().toCharArray());
        protocol.noop();
        protocol.quit();
    }
    
    public void testUidl() throws Exception {
        createMailbox2Messages();
        protocol.openPort();
        protocol.userPass(ds.getUser(), ds.getPass().toCharArray());
        UidListEntry[] entries = protocol.uidl();
        assertEquals(2, entries.length);
        protocol.quit();
    }
    
    public void testUidl1() throws Exception {
        createMailbox2Messages();
        protocol.openPort();
        protocol.userPass(ds.getUser(), ds.getPass().toCharArray());
        UidListEntry entry = protocol.uidl(1);
        assertNotSame("0", entry.getUid());
        protocol.quit();
    }
    
    public void testApop() throws Exception {
        createMailbox2Messages();
        protocol.openPort();
        protocol.apop(ds.getUser(), ds.getPass().toCharArray());
        protocol.quit();
    }
    
    public void testAutoProvision() throws Exception {
        protocol.openPort();
        protocol.userPass(ds.getUser(), ds.getPass().toCharArray());
        protocol.quit();
    }
    
    public void testStls() throws Exception {
        protocol.openPort();
        protocol.startTLS();
        protocol.userPass(ds.getUser(), ds.getPass().toCharArray());
        protocol.quit();
    }
    
}
