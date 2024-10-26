package org.buni.meldware.mail.mailbox;

import java.text.ParseException;

import javax.mail.internet.MailDateFormat;

import junit.framework.TestCase;

public class TestMailDate extends TestCase {

    
    public void testMailFormat() throws ParseException {
        MailDateFormat df = new MailDateFormat();
        System.out.println(df.parse("2007-03-25 05:28:02"));
    }
}
