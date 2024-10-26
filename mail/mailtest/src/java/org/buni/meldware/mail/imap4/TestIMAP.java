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
package org.buni.meldware.mail.imap4;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.buni.meldware.mail.MailUtil;
import org.buni.meldware.mail.mailbox.Folder;
import org.buni.meldware.mail.mailbox.FolderEntry;
import org.buni.meldware.mail.mailbox.Mailbox;
import org.buni.meldware.mail.mailbox.MailboxService;
import org.buni.meldware.mail.mailbox.MessageData;
import org.buni.meldware.mail.message.Mail;
import org.buni.meldware.mail.test.DataSet;
import org.buni.meldware.mail.test.DataSetFactory;
import org.buni.meldware.mail.tx.TxRunner;
import org.buni.meldware.mail.tx.TxRunnerFactory;
import org.buni.meldware.mail.tx.VoidTx;
import org.columba.ristretto.imap.IMAPException;
import org.columba.ristretto.imap.IMAPFlags;
import org.columba.ristretto.imap.IMAPHeader;
import org.columba.ristretto.imap.IMAPProtocol;
import org.columba.ristretto.imap.ListInfo;
import org.columba.ristretto.imap.MailboxStatus;
import org.columba.ristretto.imap.SearchKey;
import org.columba.ristretto.imap.SequenceSet;
import org.columba.ristretto.log.RistrettoLogger;
import org.columba.ristretto.message.MailboxInfo;
import org.columba.ristretto.message.MimeTree;

/**
 * @author Michael.Barker
 * 
 */
public abstract class TestIMAP extends TestCase {

    static DataSet ds = DataSetFactory.get("tom");

    TxRunner txr = TxRunnerFactory.create();

    MailboxService service = MailUtil.getMailbox();

    IMAPProtocol protocol;

    public TestIMAP(String name) {
        super(name);
    }

    public void setUp() throws Exception {
        MailUtil.clearData();
        protocol = new IMAPProtocol(ds.getHost(), ds.getImapPort());
        RistrettoLogger.setLogStream(System.out);
    }

    protected void createMailbox2Messages() {
        createMailbox2Messages(true);
    }

    // TODO Consider moving this into the dataset.
    protected void createMailbox2Messages(final boolean seen) {
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

                FolderEntry fe1 = service.createMail(f, md1);
                fe1.setFlagged(true);
                fe1.setSeen(seen);
                service.updateFolderEntry(fe1);

                Mail mail2 = MailUtil.create(to, from, subject, body);
                service.createMail(f, new MessageData(mail2));
            }
        });
    }

    public void testConnect() throws Exception {
        service.createMailbox(ds.getAlias());
        protocol.openPort();
        protocol.login(ds.getUser(), ds.getPass().toCharArray());
        protocol.logout();
    }

    public void testSelect() throws Exception {
        createMailbox2Messages();

        protocol.openPort();
        protocol.login(ds.getUser(), ds.getPass().toCharArray());
        MailboxInfo info = protocol.select("INBOX");
        assertEquals(2, info.getRecent());
        assertEquals(2, info.getExists());
        protocol.close();
        protocol.logout();
    }

    public void testSelectUnseen() throws Exception {
        createMailbox2Messages();

        protocol.openPort();
        protocol.login(ds.getUser(), ds.getPass().toCharArray());
        MailboxInfo info = protocol.select("INBOX");
        assertTrue("Should be 1 unseen message", 0 < info.getFirstUnseen());
        protocol.close();
        protocol.logout();
    }

    public void testExamine() throws Exception {
        createMailbox2Messages();

        protocol.openPort();
        protocol.login(ds.getUser(), ds.getPass().toCharArray());
        MailboxInfo info = protocol.examine("INBOX");
        assertEquals(2, info.getRecent());
        assertEquals(2, info.getExists());
        assertFalse(info.isWriteAccess());
        assertFalse(info.getUidNext() == 0);
        protocol.close();
        protocol.logout();
    }

    public void testCreateSimple() throws Exception {
        createMailbox2Messages();

        protocol.openPort();
        protocol.login(ds.getUser(), ds.getPass().toCharArray());
        protocol.create("foo");
        protocol.select("foo");
        protocol.close();
        protocol.logout();
    }

    public void testCreateExisting() throws Exception {
        createMailbox2Messages();

        protocol.openPort();
        protocol.login(ds.getUser(), ds.getPass().toCharArray());
        protocol.create("foo");
        try {
            protocol.create("foo");
            assertTrue("Exception should be thrown", false);
        } catch (IMAPException e) {
            System.out.println(e.getMessage());
        }
        protocol.logout();
    }

    public void testCreateSimpleWithTrailingSlash() throws Exception {
        createMailbox2Messages();

        protocol.openPort();
        protocol.login(ds.getUser(), ds.getPass().toCharArray());
        protocol.create("foo/");
        protocol.select("foo");
        protocol.close();
        protocol.logout();
    }

    public void testCreateNested() throws Exception {
        createMailbox2Messages();

        protocol.openPort();
        protocol.login(ds.getUser(), ds.getPass().toCharArray());
        protocol.create("foo/bar");
        protocol.select("foo");
        protocol.select("foo/bar");
        protocol.close();
        protocol.logout();
    }

    public void testDeleteSimple() throws Exception {
        createMailbox2Messages();

        protocol.openPort();
        protocol.login(ds.getUser(), ds.getPass().toCharArray());
        protocol.create("foo");
        protocol.delete("foo");
        // protocol.close();
        protocol.logout();
    }

    public void testDeleteNonExisting() throws Exception {
        createMailbox2Messages();

        protocol.openPort();
        protocol.login(ds.getUser(), ds.getPass().toCharArray());
        try {
            protocol.delete("foo");
            assertTrue("Exception should be thrown", false);
        } catch (IMAPException e) {
            System.out.println(e.getMessage());
        }
        protocol.logout();
    }

    public void testRename() throws Exception {
        createMailbox2Messages();

        protocol.openPort();
        protocol.login(ds.getUser(), ds.getPass().toCharArray());
        protocol.create("foo");
        protocol.rename("foo", "bar");
        protocol.select("bar");
        protocol.close();
        protocol.logout();
    }

    public void testRenameInboxChild() throws Exception {
        createMailbox2Messages();

        protocol.openPort();
        protocol.login(ds.getUser(), ds.getPass().toCharArray());
        protocol.create("INBOX/foo");
        protocol.rename("INBOX/foo", "Trash/foo");
        protocol.select("Trash/foo");
        protocol.close();
        protocol.logout();
    }

    public void testRenameNonExisting() throws Exception {
        createMailbox2Messages();

        protocol.openPort();
        protocol.login(ds.getUser(), ds.getPass().toCharArray());
        try {
            protocol.rename("foo", "bar");
            assertTrue("Exception should be thrown", false);
        } catch (IMAPException e) {
            System.out.println(e.getMessage());
        }
        protocol.logout();
    }

    public void testRenameExisting() throws Exception {
        createMailbox2Messages();

        protocol.openPort();
        protocol.login(ds.getUser(), ds.getPass().toCharArray());
        protocol.create("foo");
        protocol.create("bar");
        try {
            protocol.rename("foo", "bar");
            assertTrue("Exception should be thrown", false);
        } catch (IMAPException e) {
            System.out.println(e.getMessage());
        }
        protocol.logout();
    }

    public void testSubscribe() throws Exception {
        createMailbox2Messages();

        protocol.openPort();
        protocol.login(ds.getUser(), ds.getPass().toCharArray());
        protocol.create("foo");
        protocol.subscribe("foo");
        protocol.logout();
    }

    public void testSubscribeFail() throws Exception {
        createMailbox2Messages();

        protocol.openPort();
        protocol.login(ds.getUser(), ds.getPass().toCharArray());
        try {
            protocol.subscribe("foo");
            assertFalse("Should be an error for non-existent folder", true);
        } catch (Exception e) {
        }
        protocol.logout();
    }

    public void testUnsubscribe() throws Exception {
        createMailbox2Messages();

        protocol.openPort();
        protocol.login(ds.getUser(), ds.getPass().toCharArray());
        protocol.create("foo");
        protocol.subscribe("foo");
        protocol.unsubscribe("foo");
        protocol.logout();
    }

    public void testListDelimiter() throws Exception {
        createMailbox2Messages();

        protocol.openPort();
        protocol.login(ds.getUser(), ds.getPass().toCharArray());
        protocol.create("foo");
        ListInfo[] info = protocol.list("", "");
        assertEquals("/", info[0].getDelimiter());
        protocol.logout();
    }

    public void testList() throws Exception {
        createMailbox2Messages();

        protocol.openPort();
        protocol.login(ds.getUser(), ds.getPass().toCharArray());
        protocol.create("foo");
        ListInfo[] infos = protocol.list("", "*");
        assertEquals(5, infos.length);
        Set<String> folders = new HashSet<String>();
        for (ListInfo info : infos) {
            folders.add(info.getName());
        }
        System.out.println(folders);
        assertTrue("Should contain 'INBOX'", folders.contains("INBOX"));
        assertTrue("Should contain 'Sent'", folders.contains("Sent"));
        assertTrue("Should contain 'Drafts'", folders.contains("Drafts"));
        assertTrue("Should contain 'Trash'", folders.contains("Trash"));
        assertTrue("Should contain 'foo'", folders.contains("foo"));
        protocol.logout();
    }

    public void testListUnknownReference() throws Exception {
        createMailbox2Messages();

        protocol.openPort();
        protocol.login(ds.getUser(), ds.getPass().toCharArray());
        protocol.create("foo");
        ListInfo[] infos = protocol.list("users.mbarker", "*");
        assertEquals(0, infos.length);
        protocol.logout();
    }

    public void testLSub() throws Exception {
        createMailbox2Messages();

        protocol.openPort();
        protocol.login(ds.getUser(), ds.getPass().toCharArray());
        protocol.create("foo");
        ListInfo[] infos = protocol.lsub("", "*");
        assertEquals(5, infos.length);
        Set<String> folders = new HashSet<String>();
        for (ListInfo info : infos) {
            folders.add(info.getName());
        }
        assertTrue("Should contain 'INBOX'", folders.contains("INBOX"));
        assertTrue("Should contain 'Sent'", folders.contains("Sent"));
        assertTrue("Should contain 'Drafts'", folders.contains("Drafts"));
        assertTrue("Should contain 'Trash'", folders.contains("Trash"));
        assertTrue("Should contain 'foo'", folders.contains("foo"));
        protocol.logout();
    }

    public void testLSubWildcard() throws Exception {
        createMailbox2Messages();

        protocol.openPort();
        protocol.login(ds.getUser(), ds.getPass().toCharArray());
        protocol.create("foo/bar");
        ListInfo[] infos = protocol.lsub("", "%/%");
        assertEquals(6, infos.length);
        Set<String> folders = new HashSet<String>();
        for (ListInfo info : infos) {
            folders.add(info.getName());
        }
        assertTrue("Should contain 'foo/bar'", folders.contains("foo/bar"));
        assertTrue("Should contain 'foo'", folders.contains("foo/bar"));
        assertTrue("Should contain 'INBOX'", folders.contains("foo/bar"));
        assertTrue("Should contain 'Trash'", folders.contains("foo/bar"));
        assertTrue("Should contain 'Sent'", folders.contains("foo/bar"));
        assertTrue("Should contain 'Draft'", folders.contains("foo/bar"));
        protocol.logout();
    }

    public void testStatus() throws Exception {
        createMailbox2Messages();

        protocol.openPort();
        protocol.login(ds.getUser(), ds.getPass().toCharArray());
        MailboxStatus status = protocol.status("INBOX", new String[] {
                "MESSAGES", "RECENT", "UIDNEXT", "UIDVALIDITY", "UNSEEN" });
        assertEquals(2, status.getMessages());
        assertEquals(2, status.getRecent());
        assertEquals(1, status.getUnseen());
        protocol.logout();
    }

    public void testAppend() throws Exception {
        createMailbox2Messages();
        StringBuffer sb = new StringBuffer();
        sb.append("Message-ID: <3F328D84.3080108@localhost>\r\n");
        sb.append("Date: Thu, 07 Aug 2003 13:33:56 -0400\r\n");
        sb.append("From: Test User <test@localhost>\r\n");
        sb.append("X-Accept-Language: en-us, en\r\n");
        sb.append("To: tom@localhost\r\n");
        sb.append("Subject: Test Subject\r\n");
        sb.append("\r\n");
        sb.append("Message body\r\n");

        protocol.openPort();
        protocol.login(ds.getUser(), ds.getPass().toCharArray());
        byte[] data = sb.toString().getBytes("US-ASCII");
        System.out.println(data.length);
        protocol.append("Sent", new ByteArrayInputStream(data));
        MailboxInfo info = protocol.select("Sent");
        assertEquals(1, info.getExists());
        protocol.logout();
    }

    public void testCheck() throws Exception {
        createMailbox2Messages();

        protocol.openPort();
        protocol.login(ds.getUser(), ds.getPass().toCharArray());
        protocol.create("foo");
        protocol.select("foo");
        protocol.check();
        protocol.close();
        protocol.logout();
    }

    public void testFetchFlags() throws Exception {
        createMailbox2Messages();

        protocol.openPort();
        protocol.login(ds.getUser(), ds.getPass().toCharArray());
        protocol.select("INBOX");
        SequenceSet ss = new SequenceSet(1, 2);
        IMAPFlags[] flags = protocol.fetchFlags(ss);
        assertEquals(2, flags.length);
        assertTrue(flags[0].getSeen());
        assertFalse(flags[1].getSeen());
        protocol.close();
        protocol.logout();
    }

    public void testFetchHeaders() throws Exception {
        createMailbox2Messages();

        protocol.openPort();
        protocol.login(ds.getUser(), ds.getPass().toCharArray());
        protocol.select("INBOX");
        SequenceSet ss = new SequenceSet(1, 2);
        IMAPHeader[] headers = protocol.fetchHeader(ss);
        assertEquals(2, headers.length);
        protocol.close();
        protocol.logout();
    }

    // TODO: Looks like this can't be done with ristretto.
    public void testFetchEnvelope() throws Exception {
        createMailbox2Messages();

        protocol.openPort();
        protocol.login(ds.getUser(), ds.getPass().toCharArray());
        protocol.select("INBOX");
        SequenceSet ss = new SequenceSet(1, 2);
        IMAPHeader[] headers = protocol.fetchHeaderFields(ss,
                new String[] { "SUBJECT" });
        assertEquals(2, headers.length);
        for (Enumeration e = headers[0].getHeader().getKeys(); e
                .hasMoreElements();) {
            System.out.println(e.nextElement());
        }
        protocol.close();
        protocol.logout();
    }

    public void testFetchMessage() throws Exception {
        createMailbox2Messages();

        protocol.openPort();
        protocol.login(ds.getUser(), ds.getPass().toCharArray());
        protocol.select("INBOX");
        InputStream in1 = protocol.fetchMessage(1);
        while (in1.read() != -1)
            ;
        InputStream in2 = protocol.fetchMessage(2);
        while (in2.read() != -1)
            ;
        protocol.close();
        protocol.logout();
    }

    // public void testUidFetchFlags() throws Exception {
    // createMailbox2Messages();
    //        
    // protocol.openPort();
    // protocol.login(ds.getUser(), ds.getPass().toCharArray());
    // protocol.select("INBOX");
    // Integer[] uids = protocol.fetchUid(new SequenceSet(1,2));
    // SequenceSet ss = new SequenceSet(uids);
    // IMAPFlags[] flags = protocol.uidFetchFlags(ss);
    // assertEquals(2, flags.length);
    // assertTrue(flags[0].getSeen());
    // assertFalse(flags[1].getSeen());
    // protocol.close();
    // protocol.logout();
    // }

    public void testUidFetchHeaders() throws Exception {
        createMailbox2Messages();

        protocol.openPort();
        protocol.login(ds.getUser(), ds.getPass().toCharArray());
        protocol.select("INBOX");
        Integer[] uids = protocol.fetchUid(new SequenceSet(1, 2));
        SequenceSet ss = new SequenceSet(uids);
        IMAPHeader[] headers = protocol.uidFetchHeader(ss);
        assertEquals(2, headers.length);
        protocol.close();
        protocol.logout();
    }

    // TODO: Looks like this can't be done with ristretto.
    public void testUidFetchEnvelope() throws Exception {
        createMailbox2Messages();

        protocol.openPort();
        protocol.login(ds.getUser(), ds.getPass().toCharArray());
        protocol.select("INBOX");
        Integer[] uids = protocol.fetchUid(new SequenceSet(1, 2));
        SequenceSet ss = new SequenceSet(uids);
        IMAPHeader[] headers = protocol.uidFetchHeaderFields(ss,
                new String[] { "SUBJECT" });
        assertEquals(2, headers.length);
        for (Enumeration e = headers[0].getHeader().getKeys(); e
                .hasMoreElements();) {
            System.out.println(e.nextElement());
        }
        protocol.close();
        protocol.logout();
    }

    public void testUidFetchMessage() throws Exception {
        createMailbox2Messages();

        protocol.openPort();
        protocol.login(ds.getUser(), ds.getPass().toCharArray());
        protocol.select("INBOX");
        Integer[] uids = protocol.fetchUid(new SequenceSet(1, 2));
        InputStream in1 = protocol.uidFetchMessage(uids[0]);
        while (in1.read() != -1)
            ;
        InputStream in2 = protocol.uidFetchMessage(uids[1]);
        while (in2.read() != -1)
            ;
        protocol.close();
        protocol.logout();
    }

    public void testStoreAnswered() throws Exception {
        createMailbox2Messages();

        protocol.openPort();
        protocol.login(ds.getUser(), ds.getPass().toCharArray());
        protocol.select("INBOX");
        SequenceSet ss = new SequenceSet(1, 2);
        IMAPFlags flagsToSet = new IMAPFlags();
        flagsToSet.setAnswered(true);
        protocol.store(ss, true, flagsToSet);
        IMAPFlags[] flags = protocol.fetchFlags(ss);
        assertEquals("Should be 2 messages", 2, flags.length);
        assertTrue("Message should be answered", flags[0].getAnswered());
        assertTrue("Message should be answered", flags[1].getAnswered());
        protocol.close();
        protocol.logout();
    }

    public void testStoreFlagged() throws Exception {
        createMailbox2Messages();

        protocol.openPort();
        protocol.login(ds.getUser(), ds.getPass().toCharArray());
        protocol.select("INBOX");
        SequenceSet ss = new SequenceSet(1, 2);
        IMAPFlags flagsToSet = new IMAPFlags();
        flagsToSet.setFlagged(true);
        protocol.store(ss, true, flagsToSet);
        IMAPFlags[] flags = protocol.fetchFlags(ss);
        assertEquals(2, flags.length);
        assertTrue(flags[0].getFlagged());
        assertTrue(flags[1].getFlagged());
        protocol.close();
        protocol.logout();
    }

    public void testDeleteMessages() throws Exception {
        createMailbox2Messages();

        protocol.openPort();
        protocol.login(ds.getUser(), ds.getPass().toCharArray());
        protocol.select("INBOX");
        SequenceSet ss = new SequenceSet(1, 2);
        IMAPFlags flagsToSet = new IMAPFlags();
        flagsToSet.setDeleted(true);
        protocol.store(ss, true, flagsToSet);
        int[] ids = protocol.expunge();
        assertEquals(2, ids.length);
        protocol.close();
        MailboxInfo info = protocol.select("INBOX");
        assertEquals(0, info.getExists());
        protocol.logout();
    }

    public void testCopy() throws Exception {
        createMailbox2Messages();

        protocol.openPort();
        protocol.login(ds.getUser(), ds.getPass().toCharArray());
        protocol.create("foo");
        protocol.select("INBOX");
        SequenceSet ss = new SequenceSet(1, 2);
        protocol.copy(ss, "foo");
        protocol.close();
        MailboxInfo info = protocol.select("foo");
        assertEquals(2, info.getExists());
        protocol.logout();

    }

    public void testNoop() throws Exception {
        createMailbox2Messages();

        protocol.openPort();
        protocol.login(ds.getUser(), ds.getPass().toCharArray());
        protocol.select("INBOX");
        protocol.noop();
        protocol.close();
        protocol.logout();
    }

    public void testBodystructure() throws Exception {
        createMailbox2Messages();

        protocol.openPort();
        protocol.login(ds.getUser(), ds.getPass().toCharArray());
        protocol.select("INBOX");
        MimeTree mt1 = protocol.fetchBodystructure(1);
        assertEquals("text", mt1.get(0).getHeader().getMimeType().getType());
        assertEquals("plain", mt1.get(0).getHeader().getMimeType().getSubtype());
        MimeTree mt2 = protocol.fetchBodystructure(1);
        assertEquals("text", mt2.get(0).getHeader().getMimeType().getType());
        assertEquals("plain", mt2.get(0).getHeader().getMimeType().getSubtype());
        protocol.close();
        protocol.logout();
    }

    public void testSearch01() throws Exception {
        createMailbox2Messages();

        protocol.openPort();
        protocol.login(ds.getUser(), ds.getPass().toCharArray());
        protocol.select("INBOX");
        SearchKey sk = new SearchKey("FLAGGED");
        Integer[] ids = protocol.search(new SearchKey[] { sk });
        assertEquals(1, ids.length);
        protocol.close();
        protocol.logout();
    }

    public void testSearch02() throws Exception {
        createMailbox2Messages();

        protocol.openPort();
        protocol.login(ds.getUser(), ds.getPass().toCharArray());
        protocol.select("INBOX");
        SearchKey sk = new SearchKey("UNFLAGGED");
        Integer[] ids = protocol.search(new SearchKey[] { sk });
        assertEquals(1, ids.length);
        protocol.close();
        protocol.logout();
    }

    public void testSearch03() throws Exception {
        createMailbox2Messages();

        protocol.openPort();
        protocol.login(ds.getUser(), ds.getPass().toCharArray());
        protocol.select("INBOX");
        SearchKey sk = new SearchKey("ANSWERED");
        Integer[] ids = protocol.search(new SearchKey[] { sk });
        assertEquals(0, ids.length);
        protocol.close();
        protocol.logout();
    }

    public void testIdle() throws Exception {
        createMailbox2Messages();

        protocol.openPort();
        protocol.login(ds.getUser(), ds.getPass().toCharArray());
        protocol.select("INBOX");
        // protocol.idle();
        // protocol.done();
        protocol.close();
        protocol.logout();
    }

}