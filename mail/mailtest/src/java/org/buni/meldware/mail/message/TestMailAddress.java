/*
 * Bunisoft the Open Source Communications Company
 * Copyright 2006, Bunisoft Inc.,
 *
 * Portions of this software are Copyright 2006, JBoss Inc., and 
 * individual contributors as indicated by the @authors tag.
 * See the copyright.txt in the distribution for a full listing of
 * individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
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
package org.buni.meldware.mail.message;

import java.util.List;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.columba.ristretto.parser.ParserException;

/**
 * Tests that the MailAddress parses valid addresses and property returns invalid state for invalid 
 * addresses and that the properties (user/domain) are what we imagine.
 * @author Andrew C. Oliver
 */
public class TestMailAddress extends TestCase {

    /**
     * @return
     */
    public static TestSuite suite() {
        return new TestSuite(TestMailAddress.class);
    }
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(TestMailAddress.class);
    }

    public TestMailAddress(String name){
        super(name);
    }

    public void testIsValid() {
        MailAddress addr = MailAddress.parseSMTPStyle("invalid address");
        System.err.println(addr.getUser());
        System.err.println(addr.getDomain());
        assertTrue("addr isvalid should be false "+addr.isValid(), addr.isValid() == false);
        addr = MailAddress.parseSMTPStyle("<noone@nowhere.com>");
        assertTrue("addr isvalid should be true "+addr.isValid(), addr.isValid() == true);
		assertEquals("<noone@nowhere.com>", addr.toString());
    }
    
    public void testIPLiteral() {
        MailAddress addr = MailAddress.parseSMTPStyle("<mike@[127.0.0.1]>");
        assertTrue(addr.isValid());
    }
    
    public void testMixedCaseLiteral() {
        MailAddress addr = MailAddress.parseSMTPStyle("<mike@LoCalHoSt>");
        assertTrue(addr.isValid());
    }
    
    public void testEquals() {
        MailAddress a1 = MailAddress.parseSMTPStyle("<mike@localhost>");
        MailAddress a2 = MailAddress.parseSMTPStyle("<mike@localhost>");
        assertTrue(a1.equals(a2));
    }
    
    public void testNotEqualsDomain() {
        MailAddress a1 = MailAddress.parseSMTPStyle("<mike@foo.com>");
        MailAddress a2 = MailAddress.parseSMTPStyle("<mike@localhost>");
        assertFalse(a1.equals(a2));
    }
    
    public void testNotEqualsUser() {
        MailAddress a1 = MailAddress.parseSMTPStyle("<bart@localhost>");
        MailAddress a2 = MailAddress.parseSMTPStyle("<mike@localhost>");
        assertFalse(a1.equals(a2));
    }
    
    /**
     * 
     */
    public void testIMAPFormat() {
        MailAddress a1 = MailAddress.parseSMTPStyle("<bart@localhost>");
        String expected = "(NIL NIL \"bart\" \"localhost\")";
        assertEquals(expected, a1.toIMAPString());
    }
    
    public void testParseList() throws ParserException {
        List<MailAddress> as = MailAddressFactory.parseAddressList("Aron Sogor <bigman@gmail.com>,\"Mike\" <mike@middlesoft.co.uk>, bart@localhost, mike");
        for (MailAddress a : as) {
            System.out.println(a.toIMAPString());
            System.out.println(a.toString());
        }
    }
    
    public void testQuoted() {
        MailAddress m2 = MailAddress.parseSMTPStyle("\"Height, Jason\" <Jason.Height@asc.com.au>");
        assertTrue(!m2.isEmpty());
    }
    
	public void testEmtpyAddress(){
		MailAddress addr = MailAddress.parseSMTPStyle("<>");
		assertTrue("addr isvalid should be false "+addr.isValid(), addr.isValid() == false);
		assertTrue("addr isempty should be true "+addr.isEmpty(), addr.isEmpty() == true);
	}

	public void testEmtpyFromAddress(){
		MailAddress addr = MailAddress.parseSMTPStyle("<>", true);
		assertTrue("addr isvalid should be true "+addr.isValid(), addr.isValid() == true);
		assertTrue("addr isempty should be true "+addr.isEmpty(), addr.isEmpty() == true);
		assertEquals("<>", addr.toString());
	}
    
    public void testParseAddressSourceRouting() {
        
        MailAddress m1 = MailAddress.parseSMTPStyle("Jimbo Jones <mike@middlesoft.co.uk>");
        assertEquals("middlesoft.co.uk", m1.getDomain());
        assertEquals("mike", m1.getUser());
        assertEquals("Jimbo Jones", m1.getPrettyName());
        assertTrue(m1.isValid());
        MailAddress m2 = MailAddress.parseSMTPStyle("<mike@middlesoft.co.uk>");
        assertEquals("middlesoft.co.uk", m2.getDomain());
        assertEquals("mike", m2.getUser());
        assertEquals("", m2.getPrettyName());
        assertTrue(m2.isValid());
        MailAddress m3 = MailAddress.parseSMTPStyle("<@foo.com,@jboss.org:mike@middlesoft.co.uk>");
        assertEquals("middlesoft.co.uk", m3.getDomain());
        assertEquals("mike", m3.getUser());
        assertEquals("", m3.getPrettyName());
        assertTrue(m3.isValid());
        MailAddress m4 = MailAddress.parseSMTPStyle("Jimbo Jones <@foo.com,@jboss.org:mike@middlesoft.co.uk>");
        assertEquals("middlesoft.co.uk", m4.getDomain());
        assertEquals("mike", m4.getUser());
        assertEquals("Jimbo Jones", m4.getPrettyName());
        assertTrue(m4.isValid());
        MailAddress m5 = MailAddress.parseSMTPStyle("Jimbo Jones <@foo.com,@jboss.org:mike>");
        assertEquals("localhost", m5.getDomain());
        assertEquals("mike", m5.getUser());
        assertEquals("Jimbo Jones", m5.getPrettyName());
        assertTrue(m5.isValid());
    }

    public void testGetUser() {
        MailAddress addr = MailAddress.parseSMTPStyle("<testaddr@nowhere.com>");
        assertTrue("addr should be testaddr "+addr.getUser(), 
                    addr.getUser().equals("testaddr"));
    }

    public void testGetDomain() {
        MailAddress addr = MailAddress.parseSMTPStyle("<testaddr@nowhere.com>");
        assertTrue("addr should be testaddr "+addr.getDomain(), 
                    addr.getDomain().equals("nowhere.com"));
    }
    
    public void testConvert() throws AddressException {
        
        InternetAddress ia = new InternetAddress("<mike@foo.org>");
        MailAddress ma = MailAddress.parseSMTPStyle(ia.getAddress());
        assertEquals("<mike@foo.org>", ma.toString());
        
    }

}
