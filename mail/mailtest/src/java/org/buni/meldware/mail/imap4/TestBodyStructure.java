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

import junit.framework.TestCase;

/**
 * @author andy
 *
 */
public class TestBodyStructure extends TestCase {
    /*
	public void testBodyStructurePlain() throws Exception {
		BodyStructure bs = new BodyStructure("text/plain", null, "en", constructPlainMailBodyList());
		String foo = bs.toString();
		System.out.println(foo);
		assertTrue(foo != null);
	}
	
	public void testBodyStructureMultipart() throws Exception {
		BodyStructure bs = new BodyStructure("mixed", "------------050602060007020106060003",null,constructMultipartMailBodyList());
		String foo = bs.toString();
		System.out.println(bs);
		assertTrue (foo!= null);
	}*/

	/**
	 * @return
	 */
    /*
	private List<MailBody> constructPlainMailBodyList() {
		MailBody body = new MessageBody(){
			String thebody = "This is a plain text message.\r\n";
			@Override
			public void write(OutputStream out) throws MailException {
				try {
					out.write(thebody.getBytes());
				} catch (IOException e) {
				    throw new MailException(e);
				}
			}
		
			@Override
			public void write(OutputStream out, Copier copier) throws MailException {
				// TODO Auto-generated method stub
				
			}
		
			@Override
			public int read(InputStream in) throws MailException {
				// TODO Auto-generated method stub
				return 0;
			}
		
			@Override
			public int read(InputStream in, Copier copier) throws MailException {
				// TODO Auto-generated method stub
				return 0;
			}
		
			@Override
			public int getType() {
				// TODO Auto-generated method stub
				return 0;
			}
		
			@Override
			public Long getStoreId() throws StoreException {
				// TODO Auto-generated method stub
				return null;
			}
		
			@Override
			public int getSize() throws StoreException {
				// TODO Auto-generated method stub
				return thebody.length();
			}
		
			@Override
			public InputStream getInputStream() throws MailException {
				// TODO Auto-generated method stub
				return new ByteArrayInputStream(thebody.getBytes());
			}
		
			@Override
			public byte[] getData() {
				// TODO Auto-generated method stub
				return thebody.getBytes();
			}
		
		};
		
		List<MailBody> retval = new ArrayList<MailBody>();
		retval.add(body);
		return retval;
	}
	*/
    
	/**
	 * @return
	 */
	/*private List<MailBody> constructMultipartMailBodyList() {
		String theMail =
"From - Thu Jul 20 13:25:15 2006\r\n"+
"X-Mozilla-Status: 0001\r\n"+
"X-Mozilla-Status2: 00000000\r\n"+
"Return-Path: <Tom@localhost>\r\n"+
"Received: from [127.0.0.1] (localhost.localdomain 127.0.0.1) by localhost/MeldwareMail\r\n"+
" Received: from [127.0.0.1] (localhost.localdomain 127.0.0.1) by localhost/MeldwareMail 1.0M7-pre (127.0.0.1)\r\n"+
"	with SMTP id 115341629992482.14306002150317; Thu, 20\r\n"+
"  Jul 2006 13:24:59 -0400 (EDT)\r\n"+
"Message-ID: <44BFBC6B.3090201@localhost>\r\n"+
"Date: Thu, 20 Jul 2006 13:24:59 -0400\r\n"+
"From: Tom <Tom@localhost>\r\n"+
"User-Agent: Thunderbird 1.5.0.2 (X11/20060501)\r\n"+
"MIME-Version: 1.0\r\n"+
"To: Tom <Tom@localhost>\r\n"+
"Subject: Re: this happy test\r\n"+
"References: <44B69152.9020001@localhost> <44BFBC48.1060401@localhost>\r\n"+
"In-Reply-To: <44BFBC48.1060401@localhost>\r\n"+
"Content-Type: multipart/mixed; boundary=\"------------020508040109080107020006\"\r\n"+
"\r\n"+
"This is a multi-part message in MIME format.\r\n"+
"--------------020508040109080107020006\r\n"+
"Content-Type: text/plain; charset=ISO-8859-1; format=flowed\r\n"+
"Content-Transfer-Encoding: 7bit\r\n"+
"\r\n"+
"No sucker...you test THIS!\r\n"+
"\r\n"+
"Tom wrote:\r\n"+
"> Tom wrote:\r\n"+
">> test this\r\n"+
">>\r\n"+
">> -Andy\r\n"+
"> No.  Test THIS!\r\n"+
"\r\n"+
"\r\n"+
"--------------020508040109080107020006\r\n"+
"Content-Type: message/rfc822;\r\n"+
" name=\"Attached Message\"\r\n"+
"Content-Transfer-Encoding: 7bit\r\n"+
"Content-Disposition: inline;\r\n"+
" filename=\"Attached Message\"\r\n"+
" \r\n"+
" Jul 20 13:24:08 2006\r\n"+
"Return-Path: <Tom@localhost>\r\n"+
"Received: from [127.0.0.1] (localhost.localdomain 127.0.0.1) by localhost/JBossMail\r\n"+
" Received: from [127.0.0.1] (localhost.localdomain 127.0.0.1) by localhost/JBossMail 1.0M5-pre1 (127.0.0.1)\r\n"+
"	with SMTP id 1152815444700168.07397990858635; Thu,\r\n"+
"  13 Jul 2006 14:30:44 -0400 (EDT)\r\n"+
"Message-ID: <44B69152.9020001@localhost>\r\n"+
"Date: Thu, 13 Jul 2006 14:30:42 -0400\r\n"+
"From: Tom <Tom@localhost>\r\n"+
"User-Agent: Thunderbird 1.5.0.2 (X11/20060501)\r\n"+
"MIME-Version: 1.0\r\n"+
"To: Unknown <tom@localhost>\r\n"+
"Subject: this happy test\r\n"+
"Content-Type: text/plain; charset=ISO-8859-1; format=flowed\r\n"+
"Content-Transfer-Encoding: 7bit\r\n"+
"\r\n"+
"test this\r\n"+
"\r\n"+
"-Andy\r\n"+
"From - Thu\r\n"+
"\r\n"+
"--------------020508040109080107020006--\r\n\r\n.\r\n";
		MailBodyManagerMBean mbm = new MailBodyManager();
		ByteArrayInputStream in = new ByteArrayInputStream(theMail.getBytes());
		MailboxService mbox = new MailboxServiceImpl();
		mbox.setBodyManager(mbm);
		IMAP4ProtocolInstance instance = new IMAP4ProtocolInstance(null,null, mbox);
		Mail mail = Mail.create(in, theMail.length(), instance, theMail.getBytes().length);
		//List<MailBody> bodies = mail.getMailBody();
		//TODO FIXME
		List<MailBody> bodies = null;
		return bodies;
	}
    */
	
}
