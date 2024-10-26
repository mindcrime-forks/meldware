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
package org.buni.meldware.mail.smtp.handlers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import junit.framework.TestSuite;

import org.buni.meldware.mail.message.MailAddress;
import org.buni.meldware.mail.smtp.SMTPConstants;
import org.buni.meldware.mail.smtp.SMTPRequest;
import org.buni.meldware.mail.smtp.SMTPResponse;
import org.buni.meldware.test.JMXTestWrapper;

/**
 * Test the CmdRCPT handler
 * @author Andrew C. Oliver
 */
public class TestCmdRCPT extends HandlerBase {

    public static TestSuite suite() {
        return JMXTestWrapper.suite(TestCmdRCPT.class);
    }
    
    public TestCmdRCPT(String name){
        super(name);
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(TestCmdRCPT.class);
    }

    /*
     * @see HandlerBase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        handler = new CmdRCPT();
    }

    /* (non-Javadoc)
     * @see org.buni.meldware.mail.smtp.handlers.HandlerBase#testHandleRequest()
     */
    public void testHandleRequest() throws Exception {
        ByteArrayInputStream in = new ByteArrayInputStream("".getBytes());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        SMTPRequest request =
            createRequest("RCPT", in, new String[] { "TO:<test@localhost>" });

        //set sender (this WOULD happen in the MAIL command)
        MailAddress address = MailAddress.parseSMTPStyle("<test@localhost>");
        super.setProtocolState(SMTPConstants.SENDER, address);

        SMTPResponse response = this.handleRequest(out, request);
        String res = out.toString();
        System.out.println("res was " + res);

        assertTrue("response can't be null", response != null);
        String expected = "250 Recipient <test@localhost> OK";
        assertTrue(
            "expected: " + expected + ", got=" + res.toString().trim(),
            expected.equals(res.toString().trim()));
    }

    public void testHandleSourceRouted() throws Exception {
        ByteArrayInputStream in = new ByteArrayInputStream("".getBytes());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        SMTPRequest request =
            createRequest("RCPT", in, new String[] { "TO:<@jboss.org:test@localhost>" });

        //set sender (this WOULD happen in the MAIL command)
        MailAddress address = MailAddress.parseSMTPStyle("<test@localhost>");
        super.setProtocolState(SMTPConstants.SENDER, address);

        SMTPResponse response = this.handleRequest(out, request);
        String res = out.toString();
        System.out.println("res was " + res);

        assertTrue("response can't be null", response != null);
        String expected = "250 Recipient <@jboss.org:test@localhost> OK";
        assertTrue(
            "expected: " + expected + ", got=" + res.toString().trim(),
            expected.equals(res.toString().trim()));
    }

    public void testHandleRelayDisAllowed() throws Exception {
        
        boolean authRequired = fact.isAuthRequired();
        fact.setAuthRequired(false);
        
        ByteArrayInputStream in = new ByteArrayInputStream("".getBytes());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        SMTPRequest request =
            createRequest("RCPT", in, new String[] { "TO:<test@foo.org>" });

        //set sender (this WOULD happen in the MAIL command)
        MailAddress address = MailAddress.parseSMTPStyle("<user@localhost>");
        super.setProtocolState(SMTPConstants.SENDER, address);
        //super.setProtocolState(ServerThread.STATE_CLIENT_ADDRESS, InetAddress.getLocalHost());

        SMTPResponse response = this.handleRequest(out, request);
        String res = out.toString();
        System.out.println("res was " + res);

        assertTrue("response can't be null", response != null);
        String expected = "553 Relaying disallowed";
        assertTrue(
            "expected: " + expected + ", got=" + res.toString().trim(),
            expected.equals(res.toString().trim()));
        
        fact.setAuthRequired(authRequired);        
    }
    
    public void testHandleRelayAllowed() throws Exception {
        
        boolean authRequired = fact.isAuthRequired();
        fact.setAuthRequired(false);
        
        ByteArrayInputStream in = new ByteArrayInputStream("".getBytes());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        SMTPRequest request =
            createRequest("RCPT", in, new String[] { "TO:<test@foo.com>" });

        //set sender (this WOULD happen in the MAIL command)
        MailAddress address = MailAddress.parseSMTPStyle("<user@localhost>");
        super.setProtocolState(SMTPConstants.SENDER, address);
        //super.setProtocolState(ServerThread.STATE_CLIENT_ADDRESS, InetAddress.getLocalHost());

        SMTPResponse response = this.handleRequest(out, request);
        String res = out.toString();
        System.out.println("res was " + res);

        assertTrue("response can't be null", response != null);
        String expected = "250 Recipient <test@foo.com> OK";
        assertTrue(
            "expected: " + expected + ", got=" + res.toString().trim(),
            expected.equals(res.toString().trim()));
        
        fact.setAuthRequired(authRequired);
    }
    
}