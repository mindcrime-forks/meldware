package org.buni.meldware.mail.imap4.parser;

import java.io.StringReader;
import java.lang.reflect.Constructor;
import java.util.List;

import junit.framework.TestCase;

import org.buni.meldware.mail.imap4.IMAP4ProtocolInstance.ImapState;
import org.buni.meldware.mail.imap4.commands.AppendCommand;
import org.buni.meldware.mail.imap4.commands.AuthenticateCommand;
import org.buni.meldware.mail.imap4.commands.CapabilityCommand;
import org.buni.meldware.mail.imap4.commands.CheckCommand;
import org.buni.meldware.mail.imap4.commands.CloseCommand;
import org.buni.meldware.mail.imap4.commands.CopyCommand;
import org.buni.meldware.mail.imap4.commands.CreateCommand;
import org.buni.meldware.mail.imap4.commands.DeleteCommand;
import org.buni.meldware.mail.imap4.commands.DoneCommand;
import org.buni.meldware.mail.imap4.commands.ExamineCommand;
import org.buni.meldware.mail.imap4.commands.ExpungeCommand;
import org.buni.meldware.mail.imap4.commands.FetchCommand;
import org.buni.meldware.mail.imap4.commands.IdleCommand;
import org.buni.meldware.mail.imap4.commands.ListCommand;
import org.buni.meldware.mail.imap4.commands.LoginCommand;
import org.buni.meldware.mail.imap4.commands.LogoutCommand;
import org.buni.meldware.mail.imap4.commands.LsubCommand;
import org.buni.meldware.mail.imap4.commands.NoopCommand;
import org.buni.meldware.mail.imap4.commands.RenameCommand;
import org.buni.meldware.mail.imap4.commands.SearchCommand;
import org.buni.meldware.mail.imap4.commands.StatusCommand;
import org.buni.meldware.mail.imap4.commands.StoreCommand;
import org.buni.meldware.mail.imap4.commands.SubscribeCommand;
import org.buni.meldware.mail.imap4.commands.UnsubscribeCommand;
import org.buni.meldware.mail.imap4.commands.StoreCommand.Action;
import org.buni.meldware.mail.imap4.commands.fetch.BodyPartRequest;
import org.buni.meldware.mail.imap4.commands.fetch.FetchPart;
import org.buni.meldware.mail.imap4.commands.fetch.MacroFetchPart;
import org.buni.meldware.mail.imap4.commands.fetch.MessagePropertyPart;
import org.buni.meldware.mail.imap4.commands.fetch.MsgSetFilter;
import org.buni.meldware.mail.mailbox.MessageData;

import antlr.TokenStreamException;

public class TestLexer extends TestCase {

    public void testCapability() throws TokenStreamException {
        String s = "1 CAPABILITY";
        ImapLexer lex = new ImapLexer(new StringReader(s));
        lex.doLex();
        assertEquals("Tag Incorrect", "1", lex.getTag());
        assertTrue("Command Incorrect, expected Capability", 
                lex.getCommand() instanceof CapabilityCommand);
    }
    
    public void testLogout() throws TokenStreamException {
        String s = "1 LOGOUT";
        ImapLexer lex = new ImapLexer(new StringReader(s));
        lex.doLex();
        assertTrue("Command Incorrect, expected Logout", 
                lex.getCommand() instanceof LogoutCommand);
    }
    
    
    public void testNoop() throws TokenStreamException {
        String s = "1 noop";
        ImapLexer lex = new ImapLexer(new StringReader(s));
        lex.doLex();
        assertTrue("Command Incorrect, expected Noop", 
                lex.getCommand() instanceof NoopCommand);
    }
    
    public void testCreate() throws TokenStreamException {
        String s = "1 create foo";
        ImapLexer lex = new ImapLexer(new StringReader(s));
        lex.setState(ImapState.AUTHENTICATED);
        lex.doLex();
        assertTrue("Command Incorrect, expected Create", 
                lex.getCommand() instanceof CreateCommand);
        CreateCommand cc = (CreateCommand) lex.getCommand();
        assertEquals("foo", cc.getFolder());
    }
    
    public void testAppend01() throws Exception {
        String s = "1 append foo {5}";
        ImapLexer lex;
        try {
            lex = new ImapLexer(new StringReader(s));
            lex.setState(ImapState.AUTHENTICATED);
            lex.doLex();            
        } catch (Continuation c) {
            lex = new ImapLexer(new StringReader(s));
            Constructor<MessageData> ct = MessageData.class.getDeclaredConstructor(new Class[0]);
            ct.setAccessible(true);
            lex.setState(ImapState.AUTHENTICATED);
            lex.setMessage(ct.newInstance(new Object[0]));
            lex.doLex();
        }
        assertTrue("Command Incorrect, expected Append", 
                lex.getCommand() instanceof AppendCommand);
    }
    
    public void testAppend02() throws Exception {
        String s = "1 append foo () {5}";
        ImapLexer lex;
        try {
            lex = new ImapLexer(new StringReader(s));
            lex.setState(ImapState.AUTHENTICATED);
            lex.doLex();            
        } catch (Continuation c) {
            lex = new ImapLexer(new StringReader(s));
            Constructor<MessageData> ct = MessageData.class.getDeclaredConstructor(new Class[0]);
            ct.setAccessible(true);
            lex.setState(ImapState.AUTHENTICATED);
            lex.setMessage(ct.newInstance(new Object[0]));
            lex.doLex();
        }
        assertTrue("Command Incorrect, expected Append", 
                lex.getCommand() instanceof AppendCommand);
    }
    
    public void testAppend03() throws Exception {
        String s = "1 append foo \"19-Nov-1977 12:00:00 +0000\" {5}";
        ImapLexer lex;
        try {
            lex = new ImapLexer(new StringReader(s));
            lex.setState(ImapState.AUTHENTICATED);
            lex.doLex();            
        } catch (Continuation c) {
            lex = new ImapLexer(new StringReader(s));
            Constructor<MessageData> ct = MessageData.class.getDeclaredConstructor(new Class[0]);
            ct.setAccessible(true);
            lex.setState(ImapState.AUTHENTICATED);
            lex.setMessage(ct.newInstance(new Object[0]));
            lex.doLex();
        }
        assertTrue("Command Incorrect, expected Append", 
                lex.getCommand() instanceof AppendCommand);
    }

    public void testAppend04() throws Exception {
        String s = "1 append foo () \"19-Nov-1977 12:00:00 +0000\" {5}";
        ImapLexer lex;
        try {
            lex = new ImapLexer(new StringReader(s));
            lex.setState(ImapState.AUTHENTICATED);
            lex.doLex();            
        } catch (Continuation c) {
            lex = new ImapLexer(new StringReader(s));
            Constructor<MessageData> ct = MessageData.class.getDeclaredConstructor(new Class[0]);
            ct.setAccessible(true);
            lex.setState(ImapState.AUTHENTICATED);
            lex.setMessage(ct.newInstance(new Object[0]));
            lex.doLex();
        }

        assertTrue("Command Incorrect, expected Append", 
                lex.getCommand() instanceof AppendCommand);
    }
    
    public void testAppend05() throws Exception {
        String s = "1 append foo () \"19-Nov-1977 12:00:00 +0000\" {5}";
        ImapLexer lex;
        try {
            lex = new ImapLexer(new StringReader(s));
            lex.setState(ImapState.AUTHENTICATED);
            lex.doLex();            
        } catch (Continuation c) {
            lex = new ImapLexer(new StringReader(s));
            Constructor<MessageData> ct = MessageData.class.getDeclaredConstructor(new Class[0]);
            ct.setAccessible(true);
            lex.setState(ImapState.AUTHENTICATED);
            lex.setMessage(ct.newInstance(new Object[0]));
            lex.doLex();
        }
        assertTrue("Command Incorrect, expected Append", 
                lex.getCommand() instanceof AppendCommand);
    }
    
    public void testDelete() throws TokenStreamException {
        String s = "1 delete foo";
        ImapLexer lex = new ImapLexer(new StringReader(s));
        lex.setState(ImapState.AUTHENTICATED);
        lex.doLex();
        assertTrue("Command Incorrect, expected Delete", 
                lex.getCommand() instanceof DeleteCommand);
        DeleteCommand c = (DeleteCommand) lex.getCommand();
        assertEquals("foo", c.getFolder());
    }
    
    public void testExamine() throws TokenStreamException {
        String s = "1 examine foo";
        ImapLexer lex = new ImapLexer(new StringReader(s));
        lex.setState(ImapState.AUTHENTICATED);
        lex.doLex();
        assertTrue("Command Incorrect, expected Examine", 
                lex.getCommand() instanceof ExamineCommand);
        ExamineCommand c = (ExamineCommand) lex.getCommand();
        assertEquals("foo", c.getFolder());
    }
    
    public void testList1() throws TokenStreamException {
        String s = "1 list foo bar";
        ImapLexer lex = new ImapLexer(new StringReader(s));
        lex.setState(ImapState.AUTHENTICATED);
        lex.doLex();
        assertTrue("Command Incorrect, expected List", 
                lex.getCommand() instanceof ListCommand);
        ListCommand c = (ListCommand) lex.getCommand();
        assertEquals("foo", c.getReference());
        assertEquals("bar", c.getFolder());
    }
    
    public void testList2() throws TokenStreamException {
        String s = "1 list \"\" %/%";
        ImapLexer lex = new ImapLexer(new StringReader(s));
        lex.setState(ImapState.AUTHENTICATED);
        lex.doLex();
        assertTrue("Command Incorrect, expected List", 
                lex.getCommand() instanceof ListCommand);
        ListCommand c = (ListCommand) lex.getCommand();
        assertEquals("", c.getReference());
        assertEquals("%/%", c.getFolder());
    }

    public void testList3() throws TokenStreamException {
        String s = "1 list \"\" *";
        ImapLexer lex = new ImapLexer(new StringReader(s));
        lex.setState(ImapState.AUTHENTICATED);
        lex.doLex();
        assertTrue("Command Incorrect, expected List", 
                lex.getCommand() instanceof ListCommand);
        ListCommand c = (ListCommand) lex.getCommand();
        assertEquals("", c.getReference());
        assertEquals("*", c.getFolder());
    }
    
    public void testLsub1() throws TokenStreamException {
        String s = "1 lsub foo bar";
        ImapLexer lex = new ImapLexer(new StringReader(s));
        lex.setState(ImapState.AUTHENTICATED);
        lex.doLex();
        assertTrue("Command Incorrect, expected Lsub", 
                lex.getCommand() instanceof LsubCommand);
        LsubCommand c = (LsubCommand) lex.getCommand();
        assertEquals("foo", c.getReference());
        assertEquals("bar", c.getFolder());
    }
    
    public void testLSub2() throws TokenStreamException {
        String s = "1 lsub \"\" %/%";
        ImapLexer lex = new ImapLexer(new StringReader(s));
        lex.setState(ImapState.AUTHENTICATED);
        lex.doLex();
        assertTrue("Command Incorrect, expected LSub", 
                lex.getCommand() instanceof LsubCommand);
        ListCommand c = (ListCommand) lex.getCommand();
        assertEquals("", c.getReference());
        assertEquals("%/%", c.getFolder());
    }

    public void testLSub3() throws TokenStreamException {
        String s = "1 lsub \"\" *";
        ImapLexer lex = new ImapLexer(new StringReader(s));
        lex.setState(ImapState.AUTHENTICATED);
        lex.doLex();
        assertTrue("Command Incorrect, expected LSub", 
                lex.getCommand() instanceof LsubCommand);
        ListCommand c = (ListCommand) lex.getCommand();
        assertEquals("", c.getReference());
        assertEquals("*", c.getFolder());
    }
    
    public void testRename() throws TokenStreamException {
        String s = "1 rename foo bar";
        ImapLexer lex = new ImapLexer(new StringReader(s));
        lex.setState(ImapState.AUTHENTICATED);
        lex.doLex();
        assertTrue("Command Incorrect, expected Rename", 
                lex.getCommand() instanceof RenameCommand);
        RenameCommand c = (RenameCommand) lex.getCommand();
        assertEquals("foo", c.getOldFolder());
        assertEquals("bar", c.getNewFolder());
    }
    
    public void testStatus() throws TokenStreamException {
        String s = "1 status foo (MESSAGES RECENT)";
        ImapLexer lex = new ImapLexer(new StringReader(s));
        lex.setState(ImapState.AUTHENTICATED);
        lex.doLex();
        assertTrue("Command Incorrect, expected Rename", 
                lex.getCommand() instanceof StatusCommand);
        StatusCommand c = (StatusCommand) lex.getCommand();
        assertEquals("foo", c.getFolder());
        assertEquals(2, c.getRequests().size());
        assertEquals("MESSAGES", c.getRequests().get(0));
        assertEquals("RECENT", c.getRequests().get(1));
    }
    
    public void testSubscribe() throws TokenStreamException {
        String s = "1 subscribe foo";
        ImapLexer lex = new ImapLexer(new StringReader(s));
        lex.setState(ImapState.AUTHENTICATED);
        lex.doLex();
        assertTrue("Command Incorrect, expected Subscribe", 
                lex.getCommand() instanceof SubscribeCommand);
        SubscribeCommand c = (SubscribeCommand) lex.getCommand();
        assertEquals("foo", c.getFolder());
    }

    public void testUnsubscribe() throws TokenStreamException {
        String s = "1 unsubscribe foo";
        ImapLexer lex = new ImapLexer(new StringReader(s));
        lex.setState(ImapState.AUTHENTICATED);
        lex.doLex();
        assertTrue("Command Incorrect, expected Unsubscribe", 
                lex.getCommand() instanceof UnsubscribeCommand);
        UnsubscribeCommand c = (UnsubscribeCommand) lex.getCommand();
        assertEquals("foo", c.getFolder());
    }
    
    public void testLogin() throws TokenStreamException {
        String s = "1 login tom tom";
        ImapLexer lex = new ImapLexer(new StringReader(s));
        lex.setState(ImapState.NOT_AUTHENTICATED);
        lex.doLex();
        assertTrue("Command Incorrect, expected Login", 
                lex.getCommand() instanceof LoginCommand);
        LoginCommand c = (LoginCommand) lex.getCommand();
        assertEquals("tom", c.getUser());
        assertEquals("tom", c.getPassword());
    }
    
    public void testAuthenticate() throws TokenStreamException {
        String s = "1 authenticate SASL";
        ImapLexer lex = new ImapLexer(new StringReader(s));
        lex.setState(ImapState.NOT_AUTHENTICATED);
        lex.doLex();
        assertTrue("Command Incorrect, expected Authenticate", 
                lex.getCommand() instanceof AuthenticateCommand);
        AuthenticateCommand c = (AuthenticateCommand) lex.getCommand();
        assertEquals("SASL", c.getType());
    }
    
    public void testCheck() throws TokenStreamException {
        String s = "1 check";
        ImapLexer lex = new ImapLexer(new StringReader(s));
        lex.setState(ImapState.SELECTED);
        lex.doLex();
        assertTrue("Command Incorrect, expected Check", 
                lex.getCommand() instanceof CheckCommand);
    }
    
    public void testClose() throws TokenStreamException {
        String s = "1 close";
        ImapLexer lex = new ImapLexer(new StringReader(s));
        lex.setState(ImapState.SELECTED);
        lex.doLex();
        assertTrue("Command Incorrect, expected Close", 
                lex.getCommand() instanceof CloseCommand);
    }
    
    public void testExpunge() throws TokenStreamException {
        String s = "1 expunge";
        ImapLexer lex = new ImapLexer(new StringReader(s));
        lex.setState(ImapState.SELECTED);
        lex.doLex();
        assertTrue("Command Incorrect, expected Expunge", 
                lex.getCommand() instanceof ExpungeCommand);
    }
    
    public void testCopy01() throws TokenStreamException {
        String s = "1 copy 1 foo";
        ImapLexer lex = new ImapLexer(new StringReader(s));
        lex.setState(ImapState.SELECTED);
        lex.doLex();
        assertTrue("Command Incorrect, expected Copy", 
                lex.getCommand() instanceof CopyCommand);
        CopyCommand cc = (CopyCommand) lex.getCommand();
        MsgSetFilter f = (MsgSetFilter) cc.getRange();
        assertEquals(1, f.getMessageRangeFilters()[0].getTop());
        assertEquals(1, f.getMessageRangeFilters()[0].getBottom());
    }
    
    public void testCopy02() throws TokenStreamException {
        String s = "1 copy 1:10 foo";
        ImapLexer lex = new ImapLexer(new StringReader(s));
        lex.setState(ImapState.SELECTED);
        lex.doLex();
        assertTrue("Command Incorrect, expected Copy", 
                lex.getCommand() instanceof CopyCommand);
        CopyCommand cc = (CopyCommand) lex.getCommand();
        MsgSetFilter f = (MsgSetFilter) cc.getRange();
        assertEquals(10, f.getMessageRangeFilters()[0].getTop());
        assertEquals(1, f.getMessageRangeFilters()[0].getBottom());
    }
    
    public void testCopy03() throws TokenStreamException {
        String s = "1 copy *:10 foo";
        ImapLexer lex = new ImapLexer(new StringReader(s));
        lex.setState(ImapState.SELECTED);
        lex.doLex();
        assertTrue("Command Incorrect, expected Copy", 
                lex.getCommand() instanceof CopyCommand);
        CopyCommand cc = (CopyCommand) lex.getCommand();
        MsgSetFilter f = (MsgSetFilter) cc.getRange();
        assertEquals(10, f.getMessageRangeFilters()[0].getTop());
        assertEquals(-1, f.getMessageRangeFilters()[0].getBottom());
    }

    public void testCopy04() throws TokenStreamException {
        String s = "1 copy 1:* foo";
        ImapLexer lex = new ImapLexer(new StringReader(s));
        lex.setState(ImapState.SELECTED);
        lex.doLex();
        assertTrue("Command Incorrect, expected Copy", 
                lex.getCommand() instanceof CopyCommand);
        CopyCommand cc = (CopyCommand) lex.getCommand();
        MsgSetFilter f = (MsgSetFilter) cc.getRange();
        assertEquals(-1, f.getMessageRangeFilters()[0].getTop());
        assertEquals(1, f.getMessageRangeFilters()[0].getBottom());
    }

    public void testCopy05() throws TokenStreamException {
        String s = "1 copy 1:*,4:75,6 foo";
        ImapLexer lex = new ImapLexer(new StringReader(s));
        lex.setState(ImapState.SELECTED);
        lex.doLex();
        assertTrue("Command Incorrect, expected Copy", 
                lex.getCommand() instanceof CopyCommand);
        CopyCommand cc = (CopyCommand) lex.getCommand();
        MsgSetFilter f = (MsgSetFilter) cc.getRange();
        assertEquals(-1, f.getMessageRangeFilters()[0].getTop());
        assertEquals(1, f.getMessageRangeFilters()[0].getBottom());
        assertEquals(75, f.getMessageRangeFilters()[1].getTop());
        assertEquals(4, f.getMessageRangeFilters()[1].getBottom());
        assertEquals(6, f.getMessageRangeFilters()[2].getTop());
        assertEquals(6, f.getMessageRangeFilters()[2].getBottom());
    }
    
    public void testStore01() throws TokenStreamException {
        String s = "1 store 1 +FLAGS (\\Seen \\Answered)";
        ImapLexer lex = new ImapLexer(new StringReader(s));
        lex.setState(ImapState.SELECTED);
        lex.doLex();
        assertTrue("Command Incorrect, expected Copy", 
                lex.getCommand() instanceof StoreCommand);
        StoreCommand c = (StoreCommand) lex.getCommand();
        assertEquals("\\Seen", c.getFlags().get(0));
        assertEquals("\\Answered", c.getFlags().get(1));
        assertEquals(Action.ADD, c.getAction());
        assertEquals(false, c.isSilent());
    }
    
    public void testStore02() throws TokenStreamException {
        String s = "1 store 1 -FLAGS.SILENT (\\Seen \\Answered)";
        ImapLexer lex = new ImapLexer(new StringReader(s));
        lex.setState(ImapState.SELECTED);
        lex.doLex();
        assertTrue("Command Incorrect, expected Copy", 
                lex.getCommand() instanceof StoreCommand);
        StoreCommand c = (StoreCommand) lex.getCommand();
        assertEquals("\\Seen", c.getFlags().get(0));
        assertEquals("\\Answered", c.getFlags().get(1));
        assertEquals(Action.REMOVE, c.getAction());
        assertEquals(true, c.isSilent());
    }
    
    public void testStore03() throws TokenStreamException {
        String s = "1 store 1 FLAGS (\\Seen \\Answered)";
        ImapLexer lex = new ImapLexer(new StringReader(s));
        lex.setState(ImapState.SELECTED);
        lex.doLex();
        assertTrue("Command Incorrect, expected Copy", 
                lex.getCommand() instanceof StoreCommand);
        StoreCommand c = (StoreCommand) lex.getCommand();
        assertEquals("\\Seen", c.getFlags().get(0));
        assertEquals("\\Answered", c.getFlags().get(1));
        assertEquals(Action.REPLACE, c.getAction());
        assertEquals(false, c.isSilent());
    }
    
    public void testFetch01() throws TokenStreamException {
        String s = "1 fetch 1 FLAGS";
        ImapLexer lex = new ImapLexer(new StringReader(s));
        lex.setState(ImapState.SELECTED);
        lex.doLex();
        assertTrue("Command Incorrect, expected Fetch", 
                lex.getCommand() instanceof FetchCommand);
        FetchCommand c = (FetchCommand) lex.getCommand();
        List<FetchPart> parts = c.getParts();
        assertTrue(parts.get(0) instanceof MessagePropertyPart);
        assertEquals("FLAGS", parts.get(0).toString());
        assertFalse(c.isUid());
    }
    
    public void testFetch02() throws TokenStreamException {
        String s = "1 fetch 1 ALL";
        ImapLexer lex = new ImapLexer(new StringReader(s));
        lex.setState(ImapState.SELECTED);
        lex.doLex();
        assertTrue("Command Incorrect, expected Fetch", 
                lex.getCommand() instanceof FetchCommand);
        FetchCommand c = (FetchCommand) lex.getCommand();
        List<FetchPart> parts = c.getParts();
        assertTrue(parts.get(0) instanceof MacroFetchPart);
        assertEquals("ALL", parts.get(0).toString());
    }
    
    public void testFetch03() throws TokenStreamException {
        String s = "1 fetch 1 (ENVELOPE INTERNALDATE)";
        ImapLexer lex = new ImapLexer(new StringReader(s));
        lex.setState(ImapState.SELECTED);
        lex.doLex();
        assertTrue("Command Incorrect, expected Fetch", 
                lex.getCommand() instanceof FetchCommand);
        FetchCommand c = (FetchCommand) lex.getCommand();
        List<FetchPart> parts = c.getParts();
        assertTrue(parts.get(0) instanceof MessagePropertyPart);
        assertEquals("ENVELOPE", parts.get(0).toString());
        assertTrue(parts.get(1) instanceof MessagePropertyPart);
        assertEquals("INTERNALDATE", parts.get(1).toString());
    }
    
    public void testFetch04() throws TokenStreamException {
        String s = "1 fetch 1 BODY[]";
        ImapLexer lex = new ImapLexer(new StringReader(s));
        lex.setState(ImapState.SELECTED);
        lex.doLex();
        assertTrue("Command Incorrect, expected Fetch", 
                lex.getCommand() instanceof FetchCommand);
        FetchCommand c = (FetchCommand) lex.getCommand();
        List<FetchPart> parts = c.getParts();
        assertTrue(parts.get(0) instanceof BodyPartRequest);
        assertEquals("BODY[]", parts.get(0).toString());
    }
    
    public void testFetch05() throws TokenStreamException {
        String s = "1 fetch 1 BODY[1.2.MIME]<0.99>";
        ImapLexer lex = new ImapLexer(new StringReader(s));
        lex.setState(ImapState.SELECTED);
        lex.doLex();
        assertTrue("Command Incorrect, expected Fetch", 
                lex.getCommand() instanceof FetchCommand);
        FetchCommand c = (FetchCommand) lex.getCommand();
        List<FetchPart> parts = c.getParts();
        assertTrue(parts.get(0) instanceof BodyPartRequest);
        assertEquals("BODY[1.2.MIME]<0>", parts.get(0).toString());
        BodyPartRequest r = (BodyPartRequest) parts.get(0);
        assertEquals(1, r.getAddress().get(0).intValue());
        assertEquals(2, r.getAddress().get(1).intValue());
        assertEquals("MIME", r.getType().toString());
        assertEquals(0, r.getRangeStart());
        assertEquals(99, r.getRangeLength());
    }
    
    public void testUidFetch() throws TokenStreamException {
        String s = "1 uid fetch 1 FLAGS";
        ImapLexer lex = new ImapLexer(new StringReader(s));
        lex.setState(ImapState.SELECTED);
        lex.doLex();
        assertTrue("Command Incorrect, expected Fetch", 
                lex.getCommand() instanceof FetchCommand);
        FetchCommand c = (FetchCommand) lex.getCommand();
        assertTrue(c.isUid());
    }

    public void testUidCopy() throws TokenStreamException {
        String s = "1 uid copy 1 foo";
        ImapLexer lex = new ImapLexer(new StringReader(s));
        lex.setState(ImapState.SELECTED);
        lex.doLex();
        assertTrue("Command Incorrect, expected Copy", 
                lex.getCommand() instanceof CopyCommand);
        CopyCommand c = (CopyCommand) lex.getCommand();
        assertTrue(c.isUid());
    }

    public void testUidStore() throws TokenStreamException {
        String s = "1 uid store 1 FLAGS (\\Seen)";
        ImapLexer lex = new ImapLexer(new StringReader(s));
        lex.setState(ImapState.SELECTED);
        lex.doLex();
        assertTrue("Command Incorrect, expected Store", 
                lex.getCommand() instanceof StoreCommand);
        StoreCommand c = (StoreCommand) lex.getCommand();
        assertTrue(c.isUid());
    }

    public void testUidSearch() throws TokenStreamException {
        String s = "1 uid search all";
        ImapLexer lex = new ImapLexer(new StringReader(s));
        lex.setState(ImapState.SELECTED);
        lex.doLex();
        assertTrue("Command Incorrect, expected Search", 
                lex.getCommand() instanceof SearchCommand);
        SearchCommand c = (SearchCommand) lex.getCommand();
        assertTrue(c.isUid());
    }

    
    public void testSearch01() throws TokenStreamException {
        String s = "1 search all";
        ImapLexer lex = new ImapLexer(new StringReader(s));
        lex.setState(ImapState.SELECTED);
        lex.doLex();
        assertTrue("Command Incorrect, expected Search", 
                lex.getCommand() instanceof SearchCommand);
        //SearchCommand c = (SearchCommand) lex.getCommand();
    }
    
    public void testSearch02() throws TokenStreamException {
        String s = "1 search flagged bcc \"buni\"";
        ImapLexer lex = new ImapLexer(new StringReader(s));
        lex.setState(ImapState.SELECTED);
        lex.doLex();
        assertTrue("Command Incorrect, expected Search", 
                lex.getCommand() instanceof SearchCommand);
        //SearchCommand c = (SearchCommand) lex.getCommand();
    }
    
    public void testSearch03() throws TokenStreamException {
        String s = "1 search flagged bcc \"buni\" or (answered seen) draft";
        ImapLexer lex = new ImapLexer(new StringReader(s));
        lex.setState(ImapState.SELECTED);
        lex.doLex();
        assertTrue("Command Incorrect, expected Search", 
                lex.getCommand() instanceof SearchCommand);
        //SearchCommand c = (SearchCommand) lex.getCommand();
    }
    
    public void testSearch04() throws TokenStreamException {
        String s = "1 search on \"19-Nov-1977\"";
        ImapLexer lex = new ImapLexer(new StringReader(s));
        lex.setState(ImapState.SELECTED);
        lex.doLex();
        assertTrue("Command Incorrect, expected Search", 
                lex.getCommand() instanceof SearchCommand);
        //SearchCommand c = (SearchCommand) lex.getCommand();
    }
    
    public void testSearch05() throws TokenStreamException {
        String s = "1 search charset utf-8 all";
        ImapLexer lex = new ImapLexer(new StringReader(s));
        lex.setState(ImapState.SELECTED);
        lex.doLex();
        assertTrue("Command Incorrect, expected Search", 
                lex.getCommand() instanceof SearchCommand);
        //SearchCommand c = (SearchCommand) lex.getCommand();
    }
    
    public void testSearch06() throws TokenStreamException {
        String s = "1 search 3 DELETED";
        ImapLexer lex = new ImapLexer(new StringReader(s));
        lex.setState(ImapState.SELECTED);
        lex.doLex();
        assertTrue("Command Incorrect, expected Search", 
                lex.getCommand() instanceof SearchCommand);
        //SearchCommand c = (SearchCommand) lex.getCommand();
    }
    
    public void testTag() throws TokenStreamException {
        String s = "1asdf$!^&<>@'#~:;|`-_=?/ noop";
        ImapLexer lex = new ImapLexer(new StringReader(s));
        lex.doLex();
        assertEquals("1asdf$!^&<>@'#~:;|`-_=?/", lex.getCommand().getTag());
    }
    
    public void testIdle() throws TokenStreamException {
        String s = "1 idle";
        ImapLexer lex = new ImapLexer(new StringReader(s));
        lex.setState(ImapState.SELECTED);
        lex.doLex();
        assertEquals(IdleCommand.class.getName(),
                lex.getCommand().getClass().getName());
    }
    
    public void testDone() throws TokenStreamException {
        String s = "done";
        ImapLexer lex = new ImapLexer(new StringReader(s));
        lex.setState(ImapState.IDLE);
        lex.doLex();
        assertEquals(DoneCommand.class.getName(), 
                lex.getCommand().getClass().getName());
    }
    
}
