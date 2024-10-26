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
header {
package org.buni.meldware.mail.imap4.parser;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.ArrayList;

import org.buni.meldware.mail.api.Range;
import org.buni.meldware.mail.api.SearchKey;
import org.buni.meldware.mail.api.SearchKey.KeyName;
import org.buni.meldware.mail.imap4.IMAP4ProtocolInstance.ImapState;
import org.buni.meldware.mail.imap4.InvalidStateException;
import org.buni.meldware.mail.imap4.commands.AbstractImapCommand;
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
import org.buni.meldware.mail.imap4.commands.LoginCommand;
import org.buni.meldware.mail.imap4.commands.LogoutCommand;
import org.buni.meldware.mail.imap4.commands.ListCommand;
import org.buni.meldware.mail.imap4.commands.LsubCommand;
import org.buni.meldware.mail.imap4.commands.NoopCommand;
import org.buni.meldware.mail.imap4.commands.RenameCommand;
import org.buni.meldware.mail.imap4.commands.SearchCommand;
import org.buni.meldware.mail.imap4.commands.SelectCommand;
import org.buni.meldware.mail.imap4.commands.StatusCommand;
import org.buni.meldware.mail.imap4.commands.StoreCommand;
import org.buni.meldware.mail.imap4.commands.StoreCommand.Action;
import org.buni.meldware.mail.imap4.commands.SubscribeCommand;
import org.buni.meldware.mail.imap4.commands.UnsubscribeCommand;
import org.buni.meldware.mail.imap4.commands.fetch.BodyPart;
import org.buni.meldware.mail.imap4.commands.fetch.BodyPartRequest;
import org.buni.meldware.mail.imap4.commands.fetch.FetchPart;
import org.buni.meldware.mail.imap4.commands.fetch.MacroFetchPart;
import org.buni.meldware.mail.imap4.commands.fetch.MessagePropertyPart;
import org.buni.meldware.mail.imap4.commands.fetch.MsgRangeFilter;
import org.buni.meldware.mail.imap4.commands.fetch.MsgSetFilter;
import org.buni.meldware.mail.imap4.commands.fetch.RFC822PartRequest;
import org.buni.meldware.mail.message.Message;
import org.buni.meldware.mail.imap4.parser.Continuation;


}
class ImapLexer extends Lexer;

options {
	exportVocab=CommonLex;
	defaultErrorHandler=false;
	caseSensitive = false;
}

{
	private String tag;
	private AbstractImapCommand command;
	private boolean isUid = false;
	private int continuationCount = 0;
	private List<String> continuationValues = new ArrayList<String>();
	private Message message = null;
	private ImapState state;
	
    public String getTag() {
        return tag;
    }
    
    public AbstractImapCommand getCommand() {
    	if (command == null) {
    		throw new RuntimeException("Command not specified");
    	}
    	command.setTag(tag);
        return command;
    }
    
    public int parseInt(Token t) {
        return Integer.parseInt(t.getText());
    }
    
    public Token doLex() throws TokenStreamException {
    	// Get the command.
        Token t = nextToken();
        // Get the EOF
        nextToken();
        return t;
    }
    
    public void setContinuationValues(List<String> ss) {
        continuationValues = ss;
    }
    
    public String nextContinuationValue(int size) {
        if (continuationCount < continuationValues.size()) {
            String s = continuationValues.get(continuationCount);
            continuationCount++;
            return s;
        } else {
            throw new Continuation(size);
        }
    }
    
    public void setMessage(Message m) {
        this.message = m;
    }
    
    public void setState(ImapState state) {
        this.state = state;
    }
    
    public void validateState(ImapState... states) {
        for (int i = 0; i < states.length; i++) {
            if (states[i] == state) {
                return;
            }
        }
        throw new InvalidStateException("Invalid state for command");
    }
}

COMMAND
    : ("done") => "done"
    {
        validateState(ImapState.IDLE);
        command = new DoneCommand();
    }
    | t:TAG { tag = t.getText(); } SP ( COMMAND_ANY ) 
    ;

    
protected
COMMAND_ANY
    {
    	String mailbox;
    	String listMailbox;
    	String user;
    	String pass;
    	String type;
    	List attList;
    }
    : ("capability") => "capability" { command = new CapabilityCommand(); }
    | ("logout") => "logout"         { command = new LogoutCommand(); }
    | "noop"                         { command = new NoopCommand(); }
    | ("select") => "select" SP mailbox=ASTRING
    {
        validateState(ImapState.AUTHENTICATED, ImapState.SELECTED);
        SelectCommand sc = new SelectCommand();
        sc.setFolder(mailbox);
        command = sc;
    }
    | ("append") => APPEND
    | ("create") => "create" SP mailbox=ASTRING
    {
        validateState(ImapState.AUTHENTICATED, ImapState.SELECTED);
        CreateCommand cc = new CreateCommand();
        cc.setFolder(mailbox);
        command = cc;
    }
    | ("delete") => "delete" SP mailbox=ASTRING
    {
        validateState(ImapState.AUTHENTICATED, ImapState.SELECTED);
        DeleteCommand dc = new DeleteCommand();
        dc.setFolder(mailbox);
        command = dc;
    }
    | ("examine") => "examine" SP mailbox=ASTRING
    {
        validateState(ImapState.AUTHENTICATED, ImapState.SELECTED);
        ExamineCommand ec = new ExamineCommand();
        ec.setFolder(mailbox);
        command = ec;
    }
    | ("list") => "list" SP mailbox=ASTRING SP listMailbox=LIST_MAILBOX
    {
        validateState(ImapState.AUTHENTICATED, ImapState.SELECTED);
        ListCommand lc = new ListCommand();
        lc.setReference(mailbox);
        lc.setFolder(listMailbox);
        command = lc;
    }
    | ("lsub") => "lsub" SP mailbox=ASTRING SP listMailbox=LIST_MAILBOX
    {
        validateState(ImapState.AUTHENTICATED, ImapState.SELECTED);
        LsubCommand lc = new LsubCommand();
        lc.setReference(mailbox);
        lc.setFolder(listMailbox);
        command = lc;
    }
    | "rename" SP mailbox=ASTRING SP listMailbox=ASTRING
    {
        validateState(ImapState.AUTHENTICATED, ImapState.SELECTED);
        RenameCommand rc = new RenameCommand();
        rc.setOldFolder(mailbox);
        rc.setNewFolder(listMailbox);
        command = rc;
    }
    | ("status") => "status" SP mailbox=ASTRING SP attList=STATUS_ATT_LIST
    {
        validateState(ImapState.AUTHENTICATED, ImapState.SELECTED);
        StatusCommand sc = new StatusCommand();
        sc.setFolder(mailbox);
        sc.setRequests(attList);
        command = sc;
    }
    | ("subscribe") => "subscribe" SP mailbox=ASTRING
    {
        validateState(ImapState.AUTHENTICATED, ImapState.SELECTED);
        SubscribeCommand sc = new SubscribeCommand();
        sc.setFolder(mailbox);
        command = sc;
    }
    | ("unsubscribe") => "unsubscribe" SP mailbox=ASTRING
    {
        validateState(ImapState.AUTHENTICATED, ImapState.SELECTED);
        UnsubscribeCommand uc = new UnsubscribeCommand();
        uc.setFolder(mailbox);
        command = uc;
    }
    | ("login") => "login" SP user=ASTRING SP pass=ASTRING
    {
        validateState(ImapState.NOT_AUTHENTICATED);
        LoginCommand lc = new LoginCommand();
        lc.setUser(user);
        lc.setPassword(pass);
        command = lc;
    }
    | ("authenticate") => "authenticate" SP type=ATOM
    {
        validateState(ImapState.NOT_AUTHENTICATED);
        AuthenticateCommand ac = new AuthenticateCommand();
        ac.setType(type);
        command = ac;
    }
    | ("check") => "check"
    { 
        validateState(ImapState.SELECTED);
        command = new CheckCommand();
    }
    | ("close") => "close"
    {
        validateState(ImapState.SELECTED);
        command = new CloseCommand();
    }
    | ("expunge") => "expunge"
    {
        validateState(ImapState.SELECTED);
        command = new ExpungeCommand();
    }
    | ("copy") => COPY
    | ("fetch") => FETCH
    | ("store") => STORE
    | ("search") => SEARCH
    | ("uid" SP "copy")   => "uid" { isUid = true; } SP COPY 
    | ("uid" SP "fetch")  => "uid" { isUid = true; } SP FETCH 
    | ("uid" SP "store")  => "uid" { isUid = true; } SP STORE 
    | ("uid" SP "search") => "uid" { isUid = true; } SP SEARCH
    | ("idle") => "idle"
    {
        validateState(ImapState.SELECTED);
        command = new IdleCommand();
    }
    | ("done") => "done"
    {
        validateState(ImapState.IDLE);
        command = new DoneCommand();
    }
    ;

protected
APPEND
    {
        validateState(ImapState.AUTHENTICATED, ImapState.SELECTED);
        AppendCommand ac = new AppendCommand();
        command = ac;
        String mailbox;
    }
    : "append" SP mailbox=ASTRING SP APPEND_PARAMS[ac]
    { 
        ac.setFolder(mailbox);
    }
    ;

protected
COPY
    {
        validateState(ImapState.SELECTED);
        MsgSetFilter filter;
        String mailbox;
    	CopyCommand cc = new CopyCommand(isUid);
    	command = cc;
    }
    : "copy" SP filter=SEQUENCE_SET SP mailbox=ASTRING
    {
    	cc.setFolder(mailbox);
    	cc.setRange(filter);
    }
    ;

protected
STORE
    {
        validateState(ImapState.SELECTED);
        MsgSetFilter filter;
        String mailbox;
        Action a = Action.REPLACE;
        boolean silent = false;
        List flags = null;
        StoreCommand sc = new StoreCommand(isUid);
        command = sc;
    }
    : "store" SP filter=SEQUENCE_SET 
         SP ('+' { a = Action.ADD; } | '-' { a = Action.REMOVE; })? 
         "flags" (".silent" { silent = true; })? 
         SP flags=FLAG_LIST
    {
        sc.setRange(filter);
        sc.setAction(a);
        sc.setSilent(silent);
        sc.setFlags(flags);
    }
    ;

protected
FETCH
    {
        validateState(ImapState.SELECTED);
        List<FetchPart> parts;
        MsgSetFilter filter;
        FetchCommand fc = new FetchCommand(isUid);
        command = fc;
    }
    : "fetch" SP filter=SEQUENCE_SET SP parts=FETCH_LIST
    {
        fc.setRange(filter);
        for (FetchPart part : parts) {
            fc.appendPartRequest(part);
        }
    }
    ;
    
protected
SEARCH
    {
        validateState(ImapState.SELECTED);
        SearchCommand cs = new SearchCommand(isUid);
        command = cs;
        List<SearchKey> keys = new ArrayList<SearchKey>();
        SearchKey key;
    }
    : ("search" SP "charset") => 
       "search" SP "charset" SP ASTRING (SP key=SEARCH_KEY { keys.add(key); })+
    {
        cs.setKey(SearchKey.createAnd(keys));
    }
    | "search" (SP key=SEARCH_KEY { keys.add(key); })+
    {
        cs.setKey(SearchKey.createAnd(keys));
    }
    ;

protected
FETCH_LIST returns [List<FetchPart> parts]
    {
        parts = new ArrayList<FetchPart>();
        FetchPart part;
    }
	: "all"              { parts.add(new MacroFetchPart("ALL")); }
	| ("full") => "full" { parts.add(new MacroFetchPart("FULL")); }
	| ("fast") => "fast" { parts.add(new MacroFetchPart("FAST")); }
	| part=FETCH_ATT     { parts.add(part); }
	| '(' part=FETCH_ATT { parts.add(part); } 
	      (SP part=FETCH_ATT { parts.add(part); })* ')'
	;

protected
FETCH_ATT returns [FetchPart part]
    {
        part = null;
        String suffix = "";
        boolean peek = false;
        String section = null;
    }
    : "envelope"     { part = new MessagePropertyPart("ENVELOPE"); }
    | "flags"        { part = new MessagePropertyPart("FLAGS"); }
    | "internaldate" { part = new MessagePropertyPart("INTERNALDATE"); }
    | "uid"          { part = new MessagePropertyPart("UID"); }
    | "rfc822" ( '.' ( "header" { suffix = "HEADER"; } 
                     | "size"   { suffix = "SIZE"; }
                     | "text"   { suffix = "TEXT"; } ))?
    { part = new RFC822PartRequest(suffix); }
    | ("bodystructure") => "bodystructure" 
    { part = new MessagePropertyPart("BODYSTRUCTURE"); }
    | ("body") => part=BODY_PART
    ;

protected
BODY_PART returns [FetchPart part]
    {
        part = null;
        BodyPartRequest bPart = new BodyPartRequest();
        bPart.setName("BODY");
        int start = -1;
        int length = -1;
    }
    : ("body.peek") => "body.peek" {bPart.setPeek(true);} SECTION[bPart]
    { part = bPart; }
    | ("body[") => "body" SECTION[bPart]
    { part = bPart; }
    | ("body") => "body" { part = new MessagePropertyPart("BODY"); }
    ;

protected
SECTION [BodyPartRequest part]
    : '[' ( SECTION_MSGTEXT[part] | SECTION_PART[part] )? ']'
       ('<' n:NUMBER '.' m:NZ_NUMBER '>' 
       {
             part.setRange(parseInt(n), parseInt(m));
       })?
    ;


protected
SECTION_TEXT [BodyPartRequest part]
    : "mime" { part.setType(BodyPart.Type.MIME); }
    | SECTION_MSGTEXT[part]
    ;
    
protected
SECTION_MSGTEXT [BodyPartRequest part]
    : "header" {part.setType(BodyPart.Type.HEADER);}
        (".fields" {part.setType(BodyPart.Type.HEADER_FIELDS);} 
            (".not" {part.setType(BodyPart.Type.HEADER_FIELDS_NOT);})? 
                SP HEADER_LIST[part])?
    |  "text" {part.setType(BodyPart.Type.TEXT);}
    ;
    
protected 
SECTION_PART [BodyPartRequest part]
    : n:NZ_NUMBER {part.appendAddressId(parseInt(n));} 
        ('.' SECTION_PART_FRAGMENT[part])?
    ;

protected
SECTION_PART_FRAGMENT [BodyPartRequest part]
    : SECTION_TEXT[part]
    | (NZ_NUMBER '.') => 
        n:NZ_NUMBER {part.appendAddressId(parseInt(n));}
        '.' SECTION_PART_FRAGMENT[part]
    | m:NZ_NUMBER {part.appendAddressId(parseInt(m));}
    ;

protected
HEADER_LIST [BodyPartRequest part] { String s; }
    : '(' s=ASTRING {part.addPart(s);} (SP s=ASTRING {part.addPart(s);})* ')'
    ;
    
protected 
SEARCH_KEY returns [SearchKey key]
    {
        key = null;
        SearchKey child;
        List<SearchKey> childKeys = new ArrayList<SearchKey>();
        SearchKey left,right;
        String name,value;
        Calendar cal;
        MsgSetFilter filter;
    }
    : ("all") => "all" 
    {  key = SearchKey.create(KeyName.ALL, true); }
    | ("answered") => "answered" 
    { key = SearchKey.create(KeyName.ANSWERED, true); }
    | ("bcc") => "bcc" SP value=ASTRING
    { key = SearchKey.create(KeyName.BCC, value); }
    | ("before") => "before" SP cal=DATE
    { key = SearchKey.create(KeyName.BEFORE, cal); }
    | ("body") => "body" SP value=ASTRING
    { key = SearchKey.create(KeyName.BODY, value); }
    | "cc" SP value=ASTRING
    { key = SearchKey.create(KeyName.CC, value); }
    | ("deleted") => "deleted"
    { key = SearchKey.create(KeyName.DELETED, true); }
    | ("draft") => "draft"
    { key = SearchKey.create(KeyName.DRAFT, true); }
    | ("flagged") => "flagged"
    { key = SearchKey.create(KeyName.FLAGGED, true); }
    | ("from") => "from" SP value=ASTRING
    { key = SearchKey.create(KeyName.FROM, value); }
    | ("header") => "header" SP name=ASTRING SP value=ASTRING
    { key = SearchKey.createHeader(name, value); }
    | "keyword" SP value=ATOM
    { key = SearchKey.create(KeyName.KEYWORD, value); }
    | "larger" SP n1:NUMBER
    { key = SearchKey.create(KeyName.LARGER, parseInt(n1)); }
    | ("new") => "new"
    { key = SearchKey.create(KeyName.NEW, true); }
    | ("not") => "not" SP child=SEARCH_KEY
    { key = SearchKey.createNot(child); }
    | ("old") => "old"
    { key = SearchKey.create(KeyName.RECENT, false); }
    | ("on") => "on" SP cal=DATE
    { key = SearchKey.create(KeyName.ON, cal); }
    | ("or") => "or" SP left=SEARCH_KEY SP right=SEARCH_KEY
    { key = SearchKey.createOr(left, right); }
    | "recent"
    { key = SearchKey.create(KeyName.RECENT, true); }
    | ("seen") => "seen"
    { key = SearchKey.create(KeyName.SEEN, true); }
    | ("sentbefore") => "sentbefore" SP cal=DATE
    { key = SearchKey.create(KeyName.SENTBEFORE, cal); }
    | ("senton") => "senton" SP cal=DATE
    { key = SearchKey.create(KeyName.SENTON, cal); }
    | ("since") => "since" SP cal=DATE
    { key = SearchKey.create(KeyName.SINCE, cal); }
    | ("smaller") => "smaller" SP n2:NUMBER
    { key = SearchKey.create(KeyName.SMALLER, parseInt(n2)); }
    | ("subject") => "subject" SP value=ASTRING
    { key = SearchKey.create(KeyName.SUBJECT, value); }
    | ("text") => "text" SP value=ASTRING
    { key = SearchKey.create(KeyName.TEXT, value); }
    | ("to") => "to" SP value=ASTRING
    { key = SearchKey.create(KeyName.TO, value); }
    | ("uid") => "uid" filter=SEQUENCE_SET
    {
        Range[] r = filter.getRanges();
        key = SearchKey.createUid(r);
    }
    | (SEQUENCE_SET) => filter=SEQUENCE_SET
    {
        Range[] r = filter.getRanges();
        key = SearchKey.createSeqNum(r);
    }
    | ("unanswered") => "unanswered"
    { key = SearchKey.create(KeyName.ANSWERED, false); }
    | ("undeleted") => "undeleted"
    { key = SearchKey.create(KeyName.DELETED, false); }
    | ("unflagged") => "unflagged"
    { key = SearchKey.create(KeyName.FLAGGED, false); }
    | ("unkeyword") => "unkeyword" SP ASTRING
    { key = SearchKey.createNot(SearchKey.create(KeyName.KEYWORD, true)); }
    | ("unseen") => "unseen"
    { key = SearchKey.create(KeyName.SEEN, false); }
    | '(' child=SEARCH_KEY { childKeys.add(child); } (SP SEARCH_KEY { childKeys.add(child); } )* ')'
    { key = SearchKey.createAnd(childKeys); }
    ;


protected
SEQUENCE_SET returns [MsgSetFilter filter]
    {
        filter = null;
        List ranges = new ArrayList();
        MsgRangeFilter r;
	}
    : r=SEQ_RANGE { ranges.add(r); } (',' r=SEQ_RANGE { ranges.add(r); })*
    {
        MsgRangeFilter[] rangeFilter = new MsgRangeFilter[0];
        rangeFilter = (MsgRangeFilter[]) ranges.toArray(rangeFilter);
        filter = new MsgSetFilter(rangeFilter);
    }
    ;
    
protected
SEQ_RANGE returns [MsgRangeFilter filter]
    {
        filter = null;
        int left = -1;
        int right = 0;
    }
    : left=SEQ_NUMBER (':' right=SEQ_NUMBER)?
    {
    	if (right == 0) {
    		right = left;
    	}
    	
    	filter = new MsgRangeFilter(left, right);
    }
    ;

protected
SEQ_NUMBER returns [int val] { val = 0; }
	: n:NZ_NUMBER  { val = parseInt(n); }
	| '*'          { val = -1; }
	;
	
protected
STATUS_ATT_LIST returns [List attList] 
    { 
        attList = new ArrayList();
        String s;
    }
    : '(' s=STATUS_ATT {attList.add(s);} (SP s=STATUS_ATT {attList.add(s);})* ')'
    ;

protected
STATUS_ATT returns [String s] { s = null; }
    : "messages"     { s = "MESSAGES"; }
    | "recent"       { s = "RECENT"; }
    | ("uidnext")     => "uidnext"      { s = "UIDNEXT"; }
    | ("uidvalidity") => "uidvalidity"  { s = "UIDVALIDITY"; }
    | ("unseen")      => "unseen"       { s = "UNSEEN"; }
    ;

protected 
APPEND_PARAMS [AppendCommand ac]
    {
        List flags = null;
        Calendar date = null;
        // TODO Change to Message object.
        //String message = null;
    }
    : (FLAG_LIST SP DATE_TIME SP MESSAGE_LITERAL) 
        => flags=FLAG_LIST SP date=DATE_TIME SP message=MESSAGE_LITERAL
    {
        ac.setFlags(flags);
        ac.setMessage(message);
        //ac.setDate(date);
    }
    | (FLAG_LIST SP MESSAGE_LITERAL) 
        => flags=FLAG_LIST SP message=MESSAGE_LITERAL
    {
        ac.setFlags(flags);
        ac.setMessage(message);
        //ac.setDate(date);
    }
    | (DATE_TIME SP MESSAGE_LITERAL)
        => date=DATE_TIME SP message=MESSAGE_LITERAL
    {
        ac.setMessage(message);
    }
    | (MESSAGE_LITERAL)
        => message=MESSAGE_LITERAL
    {
        ac.setMessage(message);
    }
    ;
    
protected
FLAG_LIST returns [List flags]
    {
    	String f;
        flags = new ArrayList();
    }
    : '(' (f=FLAG { flags.add(f); } (SP f=FLAG { flags.add(f); })*)? ')'
    ;

protected
FLAG returns [String s]
    {
        s = "";
    }
    : ('\\' { s = "\\"; })? a:ATOM { s += a.getText(); }
    ;

protected
DATE returns [Calendar c] { c = null; }
    : c=DATE_TEXT
    | '"' c=DATE_TEXT '"'
    ;

protected
DATE_TEXT returns [Calendar c] 
    {
        c = Calendar.getInstance();
        int year;
        int month;
        int day;
    }
    : day=DATE_DAY '-' month=DATE_MONTH '-' year=DATE_YEAR
    {
        c.set(year, month, day, 0, 0, 0);
    }
    ;

protected
DATE_DAY returns [int day] { day = -1; }
    : day=DIGIT2
    ;

protected
DATE_DAY_FIXED returns [int day] { day = -1; }
    : SP day=DIGIT1
    | day=DATE_DAY
    ;
    
protected
DATE_MONTH returns [int month] { month = -1; }
    : ("jan") => "jan" { month = Calendar.JANUARY; }
    | "feb"            { month = Calendar.FEBRUARY; }
    | ("mar") => "mar" { month = Calendar.MARCH; }
    | ("apr") => "apr" { month = Calendar.APRIL; }
    | ("may") => "may" { month = Calendar.MAY; }
    | ("jun") => "jun" { month = Calendar.JUNE; }
    | ("jul") => "jul" { month = Calendar.JULY; }
    | ("aug") => "aug" { month = Calendar.AUGUST; }
    | "sep"            { month = Calendar.SEPTEMBER; }
    | "oct"            { month = Calendar.OCTOBER; }
    | "nov"            { month = Calendar.NOVEMBER; }
    | "dec"            { month = Calendar.DECEMBER; }
    ;
    
protected
DATE_YEAR returns [int year] { year = -1; }
    : year=DIGIT4
    ;
    
protected
DATE_TIME returns [Calendar c]
    {
    	c = Calendar.getInstance();
        int day;
        int month;
        int year;
        int hour;
        int minute;
        int second;
    }
    : '"' day=DATE_DAY_FIXED '-' month=DATE_MONTH '-' year=DATE_YEAR SP 
        hour=DIGIT2 ':' minute=DIGIT2 ':' second=DIGIT2 SP ZONE '"'
    {
    	c.set(year, month, day, hour, minute, second);
    }
    ;
    
protected
ZONE
    : ('+' | '-') DIGIT4;

protected
DIGIT1 returns [int n] { n = 0; }
    : d:DIGIT1_VAL {n = Integer.parseInt(d.getText());}
    ;

protected
DIGIT1_VAL : ('0'..'9');

protected
DIGIT2 returns [int n] { n = 0; }
    : d:DIGIT2_VAL {n = Integer.parseInt(d.getText());}
    ;

protected
DIGIT2_VAL : ('0'..'9')('0'..'9');

protected
DIGIT4 returns [int n] { n = 0; }
    : d:DIGIT4_VAL {n = Integer.parseInt(d.getText());}
    ;

protected
DIGIT4_VAL : ('0'..'9')('0'..'9')('0'..'9')('0'..'9');

protected
TAG
	: (TAG_CHAR)+ 
    ;
	
protected
TAG_CHAR
    : ~( '(' | ')' | '{' | ' ' | '\u007f'..'\u00FF' | '\u0000'..'\u001f' 
       | '*' | '%' | ']' | '"' | '\\' | '+'  )
    ;
    
protected
ATOM returns [String s]
    {
        s = null;
        StringBuilder sb = new StringBuilder();
    }
    : (a:ATOM_CHAR { sb.append(a.getText()); })+ 
    { s = sb.toString(); }
    ;
	
protected
ATOM_CHAR
    : ~( '(' | ')' | '{' | ' ' | '\u007f'..'\u00FF' | '\u0000'..'\u001f' 
       | '*' | '%' | ']' | '"' | '\\' )
    ;
    
protected
ASTRING returns [String s] 
    {
        s = "";
        StringBuilder sb = new StringBuilder();
    }
    : (a:ASTRING_CHAR { sb.append(a.getText()); })+ 
    { s = sb.toString(); }
    | '"' (q:QUOTED_CHAR { sb.append(q.getText()); })* '"'
    { s = sb.toString(); }
    | LITERAL
    ;

protected
ASTRING_CHAR
    :  ~( '(' | ')' | '{' | ' ' | '\u007f'..'\u00FF' | '\u0000'..'\u001f' 
       | '*' | '%' | '"' | '\\' )
    ;

protected
LIST_MAILBOX returns [String s] 
    {
        s = "";
        StringBuilder sb = new StringBuilder();
    }
    : (a:LIST_CHAR { sb.append(a.getText()); })+ 
    { s = sb.toString(); }
    | '"' (q:QUOTED_CHAR { sb.append(q.getText()); })* '"'
    { s = sb.toString(); }
    | LITERAL
    ;

protected
LIST_CHAR
    : ~( '(' | ')' | '{' | ' ' | '\u007f'..'\u00FF' | '\u0000'..'\u001f' | '"' | '\\' )
    ;
    
protected
QUOTED_CHAR
    : ~( '\\' | '"' )
    ;

protected
LITERAL returns [String s] { s = ""; }
    : '{' n:NUMBER '}' /*'\r' '\n'*/
    {
    	int i = Integer.parseInt(n.getText());
    	if (i != 0) {
    	    s = nextContinuationValue(i);
    	}
    }
    ;

protected
MESSAGE_LITERAL returns [Message m] { m = null; }
    : '{' n:NUMBER '}' /*'\r' '\n'*/
    {
    	int i = Integer.parseInt(n.getText());
    	if (i != 0) {
    	    if (message != null) {
    	        m = message;
    	    } else {
    	        throw new Continuation(i, Continuation.Type.MESSAGE);
    	    }
    	}
    }
    ;

protected 
SP : ' ';

protected
NZ_NUMBER
    : '1'..'9' ('0'..'9')*
    ;

protected
NUMBER
    : ('0'..'9')+
    ;
    