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
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestSuite;

import org.buni.meldware.mail.ServerThread;
import org.buni.meldware.mail.message.MailAddress;
import org.buni.meldware.mail.smtp.SMTPConstants;
import org.buni.meldware.mail.smtp.SMTPRequest;
import org.buni.meldware.mail.smtp.SMTPResponse;
import org.buni.meldware.test.JMXTestWrapper;

/**
 * Tests the CmdDATA handler
 * @author Andrew C. Oliver
 */
public class TestCmdDATA extends HandlerBase {

    public static TestSuite suite() {
        return JMXTestWrapper.suite(TestCmdDATA.class);
    }
    
    public TestCmdDATA(String name){
        super(name);
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(TestCmdDATA.class);
    }

    /*
     * @see HandlerBase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        handler = new CmdDATA();
		InetAddress localAddress	= InetAddress.getByName("localhost"); 
		protocol.setState(ServerThread.STATE_LOCAL_ADDRESS, localAddress);

    }

	public void testHandleRequest() throws Exception {
		StringBuffer sb = new StringBuffer();
		sb.append("Message-ID: <3F328D84.3080108@localhost>\r\n");
		sb.append("Date: Thu, 07 Aug 2003 13:33:56 -0400\r\n");
		sb.append("From: Andrew Oliver <test@localhost>\r\n");
		sb.append(
			"User-Agent: Mozilla/5.0 (Macintosh; U; PPC Mac OS X Mach-O; en-US; rv:1.3) Gecko/20030312\r\n");
		sb.append("X-Accept-Language: en-us, en\r\n");
		sb.append("MIME-Version: 1.0\r\n");
		sb.append("To:  test@localhost\r\n");
		sb.append("Subject: Test Subject\r\n");
		sb.append(
			"Content-Type: text/plain; charset=us-ascii; format=flowed\r\n");
		sb.append("Content-Transfer-Encoding: 7bit\r\n\r\n");
		sb.append("Message body\r\n");
		sb.append(".\r\n");

		//set sender (this WOULD happen in the MAIL command)
		MailAddress address = MailAddress.parseSMTPStyle("<test@localhost>");
		super.setProtocolState(SMTPConstants.SENDER, address);
		//set receipient (this would happen in the RCPT command)
		List rcpts = new ArrayList(1);
		rcpts.add(address);
		super.setProtocolState(SMTPConstants.RCPT_LIST, rcpts);

		ByteArrayInputStream in =
			new ByteArrayInputStream(sb.toString().getBytes());
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		SMTPRequest request = createRequest("DATA", in, new String[] { "" });

		SMTPResponse response = this.handleRequest(out, request);
		String res = out.toString();
		System.out.println("res was " + res);

		assertTrue("resposne can't be null", response != null);
		String expected =
			"354 Ok Send data ending with <CRLF>.<CRLF>"+newline+"250 message received";
//		assertTrue(
//			"expected: " + expected + ", got=" + res.toString().trim(),
//			expected.equals(res.toString().trim()));
        assertEquals(expected, res.toString().trim());
    }

	public void testLoopDetectionNoLoop() throws Exception {
		StringBuffer sb = new StringBuffer();
		String localHostAddress = ((InetAddress)protocol.getState(ServerThread.STATE_LOCAL_ADDRESS)).getHostAddress();
		String serverName = protocol.getServername();
		
		for (int i = 0 ; i < 99 ; i++){
			sb.append("Received: from mail.lala.com (mail.xyz.com 192.123.123.123) by mail.external.com\r\n");
		}
		
		sb.append("Message-ID: <3F328D84.3080108@localhost>\r\n");
		sb.append("Date: Thu, 07 Aug 2003 13:33:56 -0400\r\n");
		sb.append("From: Andrew Oliver <test@localhost>\r\n");
		sb.append(
			"User-Agent: Mozilla/5.0 (Macintosh; U; PPC Mac OS X Mach-O; en-US; rv:1.3) Gecko/20030312\r\n");
		sb.append("X-Accept-Language: en-us, en\r\n");
		sb.append("MIME-Version: 1.0\r\n");
		sb.append("To:  test@localhost\r\n");
		sb.append("Subject: Test Subject\r\n");
		sb.append(
			"Content-Type: text/plain; charset=us-ascii; format=flowed\r\n");
		sb.append("Content-Transfer-Encoding: 7bit\r\n\r\n");
		sb.append("Message body\r\n");
		sb.append(".\r\n");

		//set sender (this WOULD happen in the MAIL command)
		MailAddress address = MailAddress.parseSMTPStyle("<test@localhost>");
		super.setProtocolState(SMTPConstants.SENDER, address);
		//set receipient (this would happen in the RCPT command)
		List rcpts = new ArrayList(1);
		rcpts.add(address);
		super.setProtocolState(SMTPConstants.RCPT_LIST, rcpts);

		ByteArrayInputStream in =
			new ByteArrayInputStream(sb.toString().getBytes());
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		SMTPRequest request = createRequest("DATA", in, new String[] { "" });

		SMTPResponse response = this.handleRequest(out, request);
		String res = out.toString();
		System.out.println("res was " + res);

		assertTrue("resposne can't be null", response != null);
		String expected =
			"354 Ok Send data ending with <CRLF>.<CRLF>"+newline+"250 message received";
		assertTrue(
			"expected: " + expected + ", got=" + res.toString().trim(),
			expected.equals(res.toString().trim()));
	}

	public void testLoopDetectionWithTooManyReceivedHeadersLoop() throws Exception {
		StringBuffer sb = new StringBuffer();
		String localHostAddress = ((InetAddress)protocol.getState(ServerThread.STATE_LOCAL_ADDRESS)).getHostAddress();
		String serverName = protocol.getServername();
		
		for (int i = 0 ; i < 101 ; i++){
			sb.append("Received: from mail.lala.com (mail.xyz.com 192.123.123.123) by mail.external.com\r\n");
		}
		
		sb.append("Message-ID: <3F328D84.3080108@localhost>\r\n");
		sb.append("Date: Thu, 07 Aug 2003 13:33:56 -0400\r\n");
		sb.append("From: Andrew Oliver <test@localhost>\r\n");
		sb.append(
			"User-Agent: Mozilla/5.0 (Macintosh; U; PPC Mac OS X Mach-O; en-US; rv:1.3) Gecko/20030312\r\n");
		sb.append("X-Accept-Language: en-us, en\r\n");
		sb.append("MIME-Version: 1.0\r\n");
		sb.append("To:  test@localhost\r\n");
		sb.append("Subject: Test Subject\r\n");
		sb.append(
			"Content-Type: text/plain; charset=us-ascii; format=flowed\r\n");
		sb.append("Content-Transfer-Encoding: 7bit\r\n\r\n");
		sb.append("Message body\r\n");
		sb.append(".\r\n");

		//set sender (this WOULD happen in the MAIL command)
		MailAddress address = MailAddress.parseSMTPStyle("<test@localhost>");
		super.setProtocolState(SMTPConstants.SENDER, address);
		//set receipient (this would happen in the RCPT command)
		List rcpts = new ArrayList(1);
		rcpts.add(address);
		super.setProtocolState(SMTPConstants.RCPT_LIST, rcpts);

		ByteArrayInputStream in =
			new ByteArrayInputStream(sb.toString().getBytes());
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		SMTPRequest request = createRequest("DATA", in, new String[] { "" });

		SMTPResponse response = this.handleRequest(out, request);
		String res = out.toString();
		System.out.println("res was " + res);

		assertTrue("resposne can't be null", response != null);
		String expected =
			"354 Ok Send data ending with <CRLF>.<CRLF>"+newline+"550 SMTP loop? Too many Received headers. Max allowed is 100";
		assertTrue(
			"expected: " + expected + ", got=" + res.toString().trim(),
			expected.equals(res.toString().trim()));
	}

	public void testLoopDetectionWithTooManyOwnReceivedHeadersLoop() throws Exception {
		StringBuffer sb = new StringBuffer();
		String localHostAddress = ((InetAddress)protocol.getState(ServerThread.STATE_LOCAL_ADDRESS)).getHostAddress();
		String serverName = protocol.getServername();

		sb.append("Received: from lala.com (mail.xyz.com 192.123.123.123) by " + serverName + " (" + localHostAddress + ")\r\n");
		sb.append("Received: from lala.com (mail.xyz.com 192.123.123.123) by " + serverName + " (" + localHostAddress + ")\r\n");
		
		for (int i = 0 ; i < 20 ; i++){
			sb.append("Received: from mail.lala.com (mail.xyz.com 192.123.123.123) by mail.external.com\r\n");
		}
		sb.append("Received: from lala.com (mail.xyz.com 192.123.123.123) by " + serverName + " (" + localHostAddress + ")\r\n");
		sb.append("Received: from lala.com (mail.xyz.com 192.123.123.123) by " + serverName + " (" + localHostAddress + ")\r\n");
		sb.append("Received: from lala.com (mail.xyz.com 192.123.123.123) by " + serverName + " (" + localHostAddress + ")\r\n");
		sb.append("Received: from lala.com (mail.xyz.com 192.123.123.123) by " + serverName + " (" + localHostAddress + ")\r\n");
		
		
		sb.append("Message-ID: <3F328D84.3080108@localhost>\r\n");
		sb.append("Date: Thu, 07 Aug 2003 13:33:56 -0400\r\n");
		sb.append("From: Andrew Oliver <test@localhost>\r\n");
		sb.append(
			"User-Agent: Mozilla/5.0 (Macintosh; U; PPC Mac OS X Mach-O; en-US; rv:1.3) Gecko/20030312\r\n");
		sb.append("X-Accept-Language: en-us, en\r\n");
		sb.append("MIME-Version: 1.0\r\n");
		sb.append("To:  test@localhost\r\n");
		sb.append("Subject: Test Subject\r\n");
		sb.append(
			"Content-Type: text/plain; charset=us-ascii; format=flowed\r\n");
		sb.append("Content-Transfer-Encoding: 7bit\r\n\r\n");
		sb.append("Message body\r\n");
		sb.append(".\r\n");

		//set sender (this WOULD happen in the MAIL command)
		MailAddress address = MailAddress.parseSMTPStyle("<test@localhost>");
		super.setProtocolState(SMTPConstants.SENDER, address);
		//set receipient (this would happen in the RCPT command)
		List rcpts = new ArrayList(1);
		rcpts.add(address);
		super.setProtocolState(SMTPConstants.RCPT_LIST, rcpts);

		ByteArrayInputStream in =
			new ByteArrayInputStream(sb.toString().getBytes());
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		SMTPRequest request = createRequest("DATA", in, new String[] { "" });

		SMTPResponse response = this.handleRequest(out, request);
		String res = out.toString();
		System.out.println("res was " + res);

		assertTrue("resposne can't be null", response != null);
		String expected =
			"354 Ok Send data ending with <CRLF>.<CRLF>"+newline+"550 SMTP loop? More than 5 Received headers contain the name of our server: localhost";
		assertTrue(
			"expected: " + expected + ", got=" + res.toString().trim(),
			expected.equals(res.toString().trim()));
	}

	public void testLoopDetectionWithOKOwnAndTotalReceivedHeadersNoLoop() throws Exception {
		StringBuffer sb = new StringBuffer();
		String localHostAddress = ((InetAddress)protocol.getState(ServerThread.STATE_LOCAL_ADDRESS)).getHostAddress();
		String serverName = protocol.getServername();

		sb.append("Received: from lala.com (mail.xyz.com 192.123.123.123) by " + serverName + " (" + localHostAddress + ")\r\n");
		sb.append("Received: from lala.com (mail.xyz.com 192.123.123.123) by " + serverName + " (" + localHostAddress + ")\r\n");
		
		for (int i = 0 ; i < 30 ; i++){
			sb.append("Received: from mail.lala.com (mail.xyz.com 192.123.123.123) by mail.external.com\r\n");
		}
		sb.append("Received: from lala.com (mail.xyz.com 192.123.123.123) by " + serverName + " (" + localHostAddress + ")\r\n");
		sb.append("Received: from lala.com (mail.xyz.com 192.123.123.123) by " + serverName + " (" + localHostAddress + ")\r\n");
		
		
		sb.append("Message-ID: <3F328D84.3080108@localhost>\r\n");
		sb.append("Date: Thu, 07 Aug 2003 13:33:56 -0400\r\n");
		sb.append("From: Andrew Oliver <test@localhost>\r\n");
		sb.append(
			"User-Agent: Mozilla/5.0 (Macintosh; U; PPC Mac OS X Mach-O; en-US; rv:1.3) Gecko/20030312\r\n");
		sb.append("X-Accept-Language: en-us, en\r\n");
		sb.append("MIME-Version: 1.0\r\n");
		sb.append("To:  test@localhost\r\n");
		sb.append("Subject: Test Subject\r\n");
		sb.append(
			"Content-Type: text/plain; charset=us-ascii; format=flowed\r\n");
		sb.append("Content-Transfer-Encoding: 7bit\r\n\r\n");
		sb.append("Message body\r\n");
		sb.append(".\r\n");

		//set sender (this WOULD happen in the MAIL command)
		MailAddress address = MailAddress.parseSMTPStyle("<test@localhost>");
		super.setProtocolState(SMTPConstants.SENDER, address);
		//set receipient (this would happen in the RCPT command)
		List rcpts = new ArrayList(1);
		rcpts.add(address);
		super.setProtocolState(SMTPConstants.RCPT_LIST, rcpts);

		ByteArrayInputStream in =
			new ByteArrayInputStream(sb.toString().getBytes());
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		SMTPRequest request = createRequest("DATA", in, new String[] { "" });

		SMTPResponse response = this.handleRequest(out, request);
		String res = out.toString();
		System.out.println("res was " + res);

		assertTrue("resposne can't be null", response != null);
		String expected =
			"354 Ok Send data ending with <CRLF>.<CRLF>"+newline+"250 message received";
		assertTrue(
			"expected: " + expected + ", got=" + res.toString().trim(),
			expected.equals(res.toString().trim()));
	}

}