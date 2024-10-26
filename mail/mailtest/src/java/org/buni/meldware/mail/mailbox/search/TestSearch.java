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
 */package org.buni.meldware.mail.mailbox.search;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import javax.naming.InitialContext;
import javax.persistence.EntityManager;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.buni.filestore.util.Pair;
import org.buni.meldware.mail.MailUtil;
import org.buni.meldware.mail.TestConstants;
import org.buni.meldware.mail.api.Hints;
import org.buni.meldware.mail.api.Range;
import org.buni.meldware.mail.api.SearchKey;
import org.buni.meldware.mail.api.SearchKey.KeyName;
import org.buni.meldware.mail.mailbox.Folder;
import org.buni.meldware.mail.mailbox.FolderEntry;
import org.buni.meldware.mail.mailbox.FolderProxy;
import org.buni.meldware.mail.mailbox.Mailbox;
import org.buni.meldware.mail.mailbox.MailboxService;
import org.buni.meldware.mail.mailbox.MessageData;
import org.buni.meldware.mail.mailbox.PassiveFolderProxy;
import org.buni.meldware.mail.message.Mail;
import org.buni.meldware.mail.message.MailBodyManager;
import org.buni.meldware.test.JMXTestWrapper;
import org.buni.meldware.mail.tx.AOPTxRunner;
import org.buni.meldware.mail.tx.Tx;
import org.buni.meldware.mail.tx.TxRunner;
import org.buni.meldware.mail.tx.VoidTx;
import org.buni.meldware.mail.util.MMJMXUtil;

/**
 * @author Michael.Barker
 *
 */
public class TestSearch extends TestCase {

    MailboxService service;
    MailBodyManager mgr;
    TxRunner txr;
    EntityManager em;
    Pair<Long,Long> messageRef;
    
    public static TestSuite suite() {
        return JMXTestWrapper.suite(TestSearch.class);
        //return JMXTestWrapper.suite(TestSearch.class, "testSeqNum");
    }
    
    public TestSearch(String name) {
        super(name);
    }
    
    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        service = getMailbox();
        mgr = MailUtil.getMailBodyManager();
        txr = new AOPTxRunner();
        MailUtil.clearData();
        createSearchData();
        InitialContext ctx = new InitialContext();
        em = ((EntityManager) ctx.lookup("java:/EntityManagers/mail"));
    }
    
    protected MailboxService getMailbox() {
        return MMJMXUtil.getMBean(TestConstants.MAILBOX_SERVICE_MBEAN,
                MailboxService.class);
    }
    
    private void createSearchData() {
        final String mailboxName = "tom";
        final String to = "<tom@localhost>";
        final String from = "<jerry@localhost>";
        final String subject = "Test Subject";
        final String body = "Test Email Body";
        try {
            txr.requiresNew(new VoidTx() { 
                public void run() {
                    service.createMailbox(mailboxName);
                }
            });
            
            messageRef = txr.requiresNew(new Tx<Pair<Long,Long>>() { 
                public Pair<Long,Long> run() {
                    Mailbox mailbox = service.getMailboxByAlias(mailboxName);
                    Mail mail = MailUtil.create(to, from, subject, body);
                    Folder f = mailbox.getDefaultInFolder();
                    MessageData msg = new MessageData(mail);
                    FolderEntry fe = service.createMail(f, msg);
                    fe.setAnswered(true);
                    fe.setSeen(true);
                    fe.setRecent(true);
                    fe.setFlagged(true);
                    List<String> fl = fe.getFlagList().getFlagList();
                    fl.add("\\SPAM");
                    fl.add("\\DRAFT");
                    fe.getFlagList().setFlags(fl);
                    service.updateFolderEntry(fe);
                    Pair<Long,Long> mr = new Pair<Long,Long>(f.getId(), fe.getUid());
                    return mr;
                }
            });         
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }
    
    private void search(final SearchKey key, final Class type, final int numResults) {
        txr.requiresNew(new VoidTx() { 
            public void run() {
                if (key.getPartName() != SearchKey.KeyName.SEQ_NUM) {
                    SearchQueryFactory sf = new SearchQueryFactory();
                    SearchQuery sq = sf.create(key);
                    assertEquals(type, sq.getClass());
                }
                Folder f = em.find(Folder.class, messageRef.first());
                FolderProxy fp = new PassiveFolderProxy(service, f, Hints.NONE);
                Collection<Long> l = fp.search(key, true);
                assertEquals("There should be " + numResults + " found", numResults, l.size());
            }
        });        
    }
    
    public void testAll() {
        SearchKey sk1 = SearchKey.create(KeyName.ALL, true);
        search(sk1, AllSearchQuery.class, 1);
    }
    
    public void testAnswered() {
        SearchKey sk1 = SearchKey.create(KeyName.ANSWERED, true);
        search(sk1, EJBQLSearchQuery.class, 1);
        
        SearchKey sk2 = SearchKey.create(KeyName.ANSWERED, false);
        search(sk2, EJBQLSearchQuery.class, 0);
    }
    
    public void testSubject() {
        SearchKey sk1 = SearchKey.create(KeyName.SUBJECT, "Test Subject");
        search(sk1, EJBQLSearchQuery.class, 1);
        
        SearchKey sk2 = SearchKey.create(KeyName.SUBJECT, "subject");
        search(sk2, EJBQLSearchQuery.class, 1);
        
        SearchKey sk3 = SearchKey.create(KeyName.SUBJECT, "Foo Foo Foo");
        search(sk3, EJBQLSearchQuery.class, 0);
    }
    
    public void testAnd() {
        SearchKey sk1a = SearchKey.create(KeyName.SUBJECT, "test SUBJECT");
        SearchKey sk1b = SearchKey.create(KeyName.ANSWERED, true);
        SearchKey sk1 = SearchKey.createAnd(Arrays.asList(sk1a, sk1b));
        search(sk1, AndSearchQuery.class, 1);
        
        SearchKey sk2a = SearchKey.create(KeyName.SUBJECT, "test SUBJECT");
        SearchKey sk2b = SearchKey.create(KeyName.ANSWERED, false);
        SearchKey sk2 = SearchKey.createAnd(Arrays.asList(sk2a, sk2b));
        search(sk2, AndSearchQuery.class, 0);
        
        SearchKey sk3a = SearchKey.create(KeyName.SUBJECT, "Foo Foo Foo");
        SearchKey sk3b = SearchKey.create(KeyName.ANSWERED, true);
        SearchKey sk3 = SearchKey.createAnd(Arrays.asList(sk3a, sk3b));
        search(sk3, AndSearchQuery.class, 0);
    }
    
    public void testOr() {
        SearchKey sk1a = SearchKey.create(KeyName.SUBJECT, "test SUBJECT");
        SearchKey sk1b = SearchKey.create(KeyName.ANSWERED, false);
        SearchKey sk1 = SearchKey.createOr(Arrays.asList(sk1a, sk1b));
        search(sk1, OrSearchQuery.class, 1);
        
        SearchKey sk2a = SearchKey.create(KeyName.SUBJECT, "foo foo foo");
        SearchKey sk2b = SearchKey.create(KeyName.ANSWERED, false);
        SearchKey sk2 = SearchKey.createOr(Arrays.asList(sk2a, sk2b));
        search(sk2, OrSearchQuery.class, 0);
    }
    
    public void testHeader() {
        SearchKey sk1 = SearchKey.createHeader("User-Agent", "Mozilla");
        search(sk1, HeaderSearchQuery.class, 1);        
        
        SearchKey sk2 = SearchKey.createHeader("User-Agent", "Outlook");
        search(sk2, HeaderSearchQuery.class, 0);        
        
        SearchKey sk3 = SearchKey.createHeader("Foo", "Does not exist");
        search(sk3, HeaderSearchQuery.class, 0);
    }
    
    public void testBody() {
        SearchKey sk1 = SearchKey.create(KeyName.BODY, "email");
        search(sk1, ScanBodySearchQuery.class, 1);
        
        SearchKey sk2 = SearchKey.create(KeyName.BODY, "donkey");
        search(sk2, ScanBodySearchQuery.class, 0);        
    }
    
    public void testText() {
        SearchKey sk1 = SearchKey.create(KeyName.TEXT, "Mozilla");
        search(sk1, ScanBodySearchQuery.class, 1);
        
        SearchKey sk2 = SearchKey.create(KeyName.TEXT, "email");
        search(sk2, ScanBodySearchQuery.class, 1);
        
        SearchKey sk3 = SearchKey.create(KeyName.TEXT, "donkey");
        search(sk3, ScanBodySearchQuery.class, 0);        
    }
    
    public void testDeleted() {
        SearchKey sk1 = SearchKey.create(KeyName.DELETED, true);
        search(sk1, EJBQLSearchQuery.class, 0);
        
        SearchKey sk2 = SearchKey.create(KeyName.DELETED, false);
        search(sk2, EJBQLSearchQuery.class, 1);
    }
    
    public void testSeen() {
        SearchKey sk1 = SearchKey.create(KeyName.SEEN, true);
        search(sk1, EJBQLSearchQuery.class, 1);
        
        SearchKey sk2 = SearchKey.create(KeyName.SEEN, false);
        search(sk2, EJBQLSearchQuery.class, 0);
    }
    
    public void testRecent() {
        SearchKey sk1 = SearchKey.create(KeyName.RECENT, true);
        search(sk1, EJBQLSearchQuery.class, 1);
        
        SearchKey sk2 = SearchKey.create(KeyName.RECENT, false);
        search(sk2, EJBQLSearchQuery.class, 0);
    }
    
    public void testFlagged() {
        SearchKey sk1 = SearchKey.create(KeyName.FLAGGED, true);
        search(sk1, EJBQLSearchQuery.class, 1);
        
        SearchKey sk2 = SearchKey.create(KeyName.FLAGGED, false);
        search(sk2, EJBQLSearchQuery.class, 0);
    }
    
    public void testNotFlagged() {
        SearchKey sk1 = SearchKey.create(KeyName.FLAGGED, true);
        SearchKey nsk1 = SearchKey.createNot(sk1);
        search(nsk1, NotSearchQuery.class, 0);
        
        SearchKey sk2 = SearchKey.create(KeyName.FLAGGED, false);
        SearchKey nsk2 = SearchKey.createNot(sk2);
        search(nsk2, NotSearchQuery.class, 1);        
    }
    
    
    public void testDraft() {
        SearchKey sk1 = SearchKey.create(KeyName.DRAFT, true);
        search(sk1, EJBQLSearchQuery.class, 1);
        
        SearchKey sk2 = SearchKey.create(KeyName.DRAFT, false);
        search(sk2, EJBQLSearchQuery.class, 0);
    }
    
    public void testTo() {
        SearchKey sk1 = SearchKey.create(KeyName.TO, "tom");
        search(sk1, EJBQLSearchQuery.class, 1);
        
        SearchKey sk2 = SearchKey.create(KeyName.TO, "fred");
        search(sk2, EJBQLSearchQuery.class, 0);
    }
    
    public void testNotTo() {
        SearchKey sk1 = SearchKey.create(KeyName.TO, "tom");
        SearchKey nsk1 = SearchKey.createNot(sk1);
        search(nsk1, NotSearchQuery.class, 0);
        
        SearchKey sk2 = SearchKey.create(KeyName.TO, "fred");
        SearchKey nsk2 = SearchKey.createNot(sk2);
        search(nsk2, NotSearchQuery.class, 1);
    }
    
    public void testFrom() {
        SearchKey sk1 = SearchKey.create(KeyName.FROM, "jerry");
        search(sk1, EJBQLSearchQuery.class, 1);
        
        SearchKey sk2 = SearchKey.create(KeyName.FROM, "fred");
        search(sk2, EJBQLSearchQuery.class, 0);
    }
    
    public void testCC() {
        SearchKey sk1 = SearchKey.create(KeyName.CC, "tweety");
        search(sk1, HeaderSearchQuery.class, 1);
        
        SearchKey sk2 = SearchKey.create(KeyName.CC, "fred");
        search(sk2, HeaderSearchQuery.class, 0);
    }

    public void testBCC() {
        SearchKey sk1 = SearchKey.create(KeyName.BCC, "roadrunner");
        search(sk1, HeaderSearchQuery.class, 1);
        
        SearchKey sk2 = SearchKey.create(KeyName.BCC, "fred");
        search(sk2, HeaderSearchQuery.class, 0);
    }

    public void testLarger() {
        SearchKey sk1 = SearchKey.create(KeyName.LARGER, 300L);
        search(sk1, EJBQLSearchQuery.class, 1);
        
        SearchKey sk2 = SearchKey.create(KeyName.LARGER, 1000L);
        search(sk2, EJBQLSearchQuery.class, 0);        
    }

    public void testSmaller() {
        SearchKey sk1 = SearchKey.create(KeyName.SMALLER, 1000L);
        search(sk1, EJBQLSearchQuery.class, 1);
        
        SearchKey sk2 = SearchKey.create(KeyName.SMALLER, 300L);
        search(sk2, EJBQLSearchQuery.class, 0);        
    }
    
    public void testOn() {
        Calendar c1 = Calendar.getInstance();
        c1.set(2003, 7, 7);
        SearchKey sk1 = SearchKey.create(KeyName.ON, c1);
        search(sk1, EJBQLSearchQuery.class, 1);
        
        Calendar c2 = Calendar.getInstance();
        c2.set(2003, 8, 8);
        SearchKey sk2 = SearchKey.create(KeyName.ON, c2);
        search(sk2, EJBQLSearchQuery.class, 0);        
    }
    
    public void testSince() {
        Calendar c1 = Calendar.getInstance();
        c1.set(2003, 7, 6);
        SearchKey sk1 = SearchKey.create(KeyName.SINCE, c1);
        search(sk1, EJBQLSearchQuery.class, 1);
        
        Calendar c2 = Calendar.getInstance();
        c2.set(2003, 8, 8);
        SearchKey sk2 = SearchKey.create(KeyName.SINCE, c2);
        search(sk2, EJBQLSearchQuery.class, 0);        
    }
    
    public void testBefore() {
        Calendar c1 = Calendar.getInstance();
        c1.set(2003, 8, 8);
        SearchKey sk1 = SearchKey.create(KeyName.BEFORE, c1);
        search(sk1, EJBQLSearchQuery.class, 1);
        
        Calendar c2 = Calendar.getInstance();
        c2.set(2003, 7, 6);
        SearchKey sk2 = SearchKey.create(KeyName.BEFORE, c2);
        search(sk2, EJBQLSearchQuery.class, 0);        
    }
    
    public void testSentOn() {
        Calendar c1 = Calendar.getInstance();
        c1.set(2003, 7, 7);
        SearchKey sk1 = SearchKey.create(KeyName.SENTON, c1);
        search(sk1, EJBQLSearchQuery.class, 1);
        
        Calendar c2 = Calendar.getInstance();
        c2.set(2003, 8, 8);
        SearchKey sk2 = SearchKey.create(KeyName.SENTON, c2);
        search(sk2, EJBQLSearchQuery.class, 0);        
    }
    
    public void testSentSince() {
        Calendar c1 = Calendar.getInstance();
        c1.set(2003, 7, 6);
        SearchKey sk1 = SearchKey.create(KeyName.SENTSINCE, c1);
        search(sk1, EJBQLSearchQuery.class, 1);
        
        Calendar c2 = Calendar.getInstance();
        c2.set(2003, 8, 8);
        SearchKey sk2 = SearchKey.create(KeyName.SENTSINCE, c2);
        search(sk2, EJBQLSearchQuery.class, 0);        
    }
    
    public void testSentBefore() {
        Calendar c1 = Calendar.getInstance();
        c1.set(2003, 8, 8);
        SearchKey sk1 = SearchKey.create(KeyName.SENTBEFORE, c1);
        search(sk1, EJBQLSearchQuery.class, 1);
        
        Calendar c2 = Calendar.getInstance();
        c2.set(2003, 7, 6);
        SearchKey sk2 = SearchKey.create(KeyName.SENTBEFORE, c2);
        search(sk2, EJBQLSearchQuery.class, 0);        
    }
    
    public void testKeyword() {
        SearchKey sk1 = SearchKey.create(KeyName.KEYWORD, "\\Spam");
        search(sk1, KeywordSearchQuery.class, 1);
        
        SearchKey sk2 = SearchKey.create(KeyName.KEYWORD, "\\NotSpam");
        search(sk2, KeywordSearchQuery.class, 0);        
    }
    
    public void testNotKeyword() {
        SearchKey sk1 = SearchKey.create(KeyName.KEYWORD, "\\Spam");
        SearchKey nsk1 = SearchKey.createNot(sk1);
        search(nsk1, NotSearchQuery.class, 0);
        
        SearchKey sk2 = SearchKey.create(KeyName.KEYWORD, "\\NotSpam");
        SearchKey nsk2 = SearchKey.createNot(sk2);
        search(nsk2, NotSearchQuery.class, 1);
    }
    
    
    public void testUid() {
        long uid = messageRef.second();
        SearchKey sk1 = SearchKey.createUid(new Range[] { new Range(uid, uid) });
        search(sk1, UIDSearchQuery.class, 1);
        
        SearchKey sk2 = SearchKey.createUid(new Range[] { new Range(uid + 10, uid + 10) });
        search(sk2, UIDSearchQuery.class, 0);                
    }
    
    public void testSeqNum() {
        SearchKey sk1 = SearchKey.createSeqNum(new Range[] { new Range(1, 1) });
        search(sk1, UIDSearchQuery.class, 1);
        
        SearchKey sk2 = SearchKey.createSeqNum(new Range[] { new Range(10, 10) });
        search(sk2, UIDSearchQuery.class, 0);                
    }
    
}
