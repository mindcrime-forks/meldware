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

import static org.buni.meldware.mail.MailUtil.createMail;

import java.net.InetAddress;
import java.util.Iterator;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.buni.meldware.mail.smtp.SMTPConstants;
import org.buni.meldware.mail.smtp.SMTPProtocolInstance;
import org.buni.meldware.mail.smtp.SMTPProtocolMBean;
import org.buni.meldware.test.JMXTestWrapper;
import org.buni.meldware.mail.util.MMJMXUtil;

/**
 * @author Kabir Khan
 */
public class TestMail extends TestCase{
	SMTPProtocolInstance smtpProtocolInstance;
	final static MailAddress FROM = MailAddress.parseSMTPStyle("sender@origin.com");
	final static MailAddress TO1 = MailAddress.parseSMTPStyle("receiver@destination.com");
	final static MailAddress TO2 = MailAddress.parseSMTPStyle("receiver2@destination.com");

	InetAddress remoteAddress;
	InetAddress localAddress;

    public static TestSuite suite() {
        return JMXTestWrapper.suite(TestMail.class);
    }
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(TestMail.class);
    }

    public TestMail(String name){
        super(name);
    }

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
	    SMTPProtocolMBean protocol = (SMTPProtocolMBean) MMJMXUtil.getMBean("meldware.mail:type=Protocol,name=SMTPProtocol", SMTPProtocolMBean.class);
	    smtpProtocolInstance = (SMTPProtocolInstance) protocol.createInstance();
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
//		super.tearDown();
	}


	public void testFullMail() throws Exception{
		MailAddress from = FROM;
		MailAddress[] to = {TO1, TO2};
        
        String prettyUser = "\"User, A\" <a.user@foo.com>";
        
		StringBuffer headers = getBasicMailHeaderBuffer(to, prettyUser);
		headers = addTraceInfoToHeaders(headers, from);
		headers = addFromToHeaders(headers, from);
		headers = addDateToHeaders(headers);
        
        to = new MailAddress[] {TO1, TO2, MailAddress.parseSMTPStyle(prettyUser) };

		Mail mail = createMail(headers, from, to, smtpProtocolInstance);


		assertNotNull("Mail object was null", mail);
		MailHeaders ih = mail.getMailHeaders();

		assertNotNull("From headers were null",
				ih.getHeader(StandardMailHeaders.FROM));
		assertNotNull("Date header was null",
				ih.getHeader(StandardMailHeaders.DATE));

		assertNotNull("Return-Path header was null",
				ih.getHeader(StandardMailHeaders.RETURN_PATH));

		Iterator it = ih.getAllHeaderLines();
		String header = it.next().toString();
		String expected = "Return-Path: " + FROM.toString();
		assertTrue("First header Expected: '" + expected + "'; got:'" + header + "'",
				expected.equals(header));

		header = it.next().toString();
        expected = "Received: ";
		assertTrue("Second header Expected: '" + expected + "..." + "'; got:'" + header + "'",
				header.startsWith(expected));

		header = it.next().toString();
		expected = "Received: ";
		assertTrue("Third header expected to be Received line" + header,
				header.startsWith(expected));

		header = it.next().toString();
		expected = "Received: ";
		assertTrue("There should be only 2 received lines; got:" + header,
				!header.startsWith(expected));
	}


	public void testMailMissingFromAndDateNotOriginator() throws Exception{
		MailAddress from = FROM;
		MailAddress[] to = {TO1, TO2};

		StringBuffer headers = getBasicMailHeaderBuffer(to);
		headers = addTraceInfoToHeaders(headers, from);

		//If we are not the originator we should not be adding Date and From headers
		Mail mail = createMail(headers, from, to, smtpProtocolInstance);

		assertNotNull("Mail object was null", mail);
		MailHeaders ih = mail.getMailHeaders();

		String[] header = ih.getHeader(StandardMailHeaders.FROM);
		assertEquals("From headers were not null", 0, header.length);

		header = ih.getHeader(StandardMailHeaders.DATE);
		assertEquals("Date header was not null", 0, header.length);
	}

	public void testMailMissingFromAndDateOriginator() throws Exception{
		MailAddress from = FROM;
		MailAddress[] to = {TO1, TO2};

		StringBuffer headers = getBasicMailHeaderBuffer(to);
		headers = addTraceInfoToHeaders(headers, from);

		//If we are the originator we should be adding Date and From headers
		smtpProtocolInstance.setState(SMTPConstants.USER, "x");
		Mail mail = createMail(headers, from, to, smtpProtocolInstance);

		assertNotNull("Mail object was null", mail);
		MailHeaders ih = mail.getMailHeaders();

		String[] header = ih.getHeader(StandardMailHeaders.TO);
		assertNotNull("To header was null", header);
		assertTrue("Expected first To was: " + TO1.toString()
				+ " got " + header[0], header[0].equals(TO1 + ", " + TO2));

		header = ih.getHeader(StandardMailHeaders.FROM);
		assertEquals("From headers was not null", 0, header.length);
        
		header = ih.getHeader(StandardMailHeaders.DATE);
		assertEquals("Date header was not null", 0, header.length);
	}


	/** Adds the trace headers to the headers
	 * @param headers StringBuffer containing the headers
	 * @param from The sender
	 * @return StringBuffer containing the headers with trace info added
	 */
	private StringBuffer addTraceInfoToHeaders(StringBuffer headers, MailAddress from){
		headers.append("Return-Path: ");
		headers.append(from.toString());
		headers.append("\r\n");

		headers.append("Received: from mailgw1.tiscali.no (213.142.64.211) by cpmail.dk.tiscali.com (6.7.018)");
		headers.append("id 3FF9222C0005C83E for kabirkhan@tiscali.no; Fri, 16 Jan 2004 16:54:44 +0100");
		headers.append("\r\n");
		headers.append("Received: by mailgw1.tiscali.no (Postfix, from userid 0)");
		headers.append("id 4AAECB55F; Fri, 16 Jan 2004 16:55:08 +0100 (CET)");
		headers.append("\r\n");

		return headers;
	}

	/** Adds the From header to the headers
	 * @param headers StringBuffer containing the headers
	 * @param from The sender
	 * @return StringBuffer containing headers with the From header added
	 */
	private StringBuffer addFromToHeaders(StringBuffer headers, MailAddress from){
		headers.append("From: ");
		headers.append(from.toString());
		headers.append("\r\n");
		return headers;
	}


	/** Adds the Date header to the headers
	 * @param headers StringBuffer containing the headers
	 * @return StringBuffer containing headers with the Date header added
	 */
	private  StringBuffer addDateToHeaders(StringBuffer headers){
		headers.append("Date: Mon, 15 Mar 2004 15:09:20 -0500 (EST)");
		headers.append("\r\n");
		return headers;
	}

    private StringBuffer getBasicMailHeaderBuffer(MailAddress[] to){
        return getBasicMailHeaderBuffer(to, null);
    }
    
    
	/**
	 * @return A set of headers without From, To, Date or trace headers set.
	 */
	private StringBuffer getBasicMailHeaderBuffer(MailAddress[] to, String s){
		StringBuffer headers = new StringBuffer();
		headers.append("Message-ID: <33490261.1079381360857.Javaheaders.root@nukes.jboss.org>");
		headers.append("\r\n");
		headers.append("Subject: [headers Services] - TODO: Script Runner");
		headers.append("\r\n");
		headers.append("To: ");
		for (int i = 0 ; i < to.length ; i++){
			if (i > 0){
				headers.append(",");
			}
			headers.append(" ");
			headers.append(to[i].toString());
		}
        if (s != null) {
            headers.append(", ");
            headers.append(s);
        }
		headers.append("\r\n");


		return headers;
	}

}
