package org.buni.meldware.calendar.jmx;

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

import java.io.IOException;
import java.net.InetAddress;
import java.nio.charset.Charset;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.columba.ristretto.auth.AuthenticationException;
import org.columba.ristretto.auth.AuthenticationFactory;
import org.columba.ristretto.auth.NoSuchAuthenticationException;
import org.columba.ristretto.composer.MimeTreeRenderer;
import org.columba.ristretto.io.Source;
import org.columba.ristretto.message.Address;
import org.columba.ristretto.message.BasicHeader;
import org.columba.ristretto.message.Header;
import org.columba.ristretto.message.LocalMimePart;
import org.columba.ristretto.message.MimeHeader;
import org.columba.ristretto.message.MimePart;
import org.columba.ristretto.message.MimeType;
import org.columba.ristretto.parser.AddressParser;
import org.columba.ristretto.parser.ParserException;
import org.columba.ristretto.smtp.SMTPProtocol;

public class SendMail implements SendMailMBean {

	private Address fromAddress = null;

	Log log = null;

	private String password = null;

	private int port = 25;

	private String smtpServer = null;

	private String username = null;

	private boolean verbose = false;

	public SendMail() {
		log = LogFactory.getLog(this.getClass());
	}

	/* (non-Javadoc)
	 * @see org.buni.meldware.calendar.jmx.SendMailMBean#createAllAlternativeMessage(java.lang.String, org.columba.ristretto.message.Address[], org.columba.ristretto.message.Address[], org.columba.ristretto.io.Source, org.columba.ristretto.io.Source, org.columba.ristretto.message.LocalMimePart)
	 */
	public MimePart createAllAlternativeMessage(String subject, Address[] toAddress,
			Address[] ccAddress, Source textBody, Source htmlBody, LocalMimePart icsAttachement) throws ParserException {
		// Header is the actual header while BasicHeader wraps
		// a Header object to give easy access to the Header.
		Header header = createRootHeader(subject, toAddress, ccAddress);

		// Now a mimepart is prepared which actually holds the message
		// The mimeHeader is another convienice wrapper for the header
		// object
		MimeHeader mimeHeader = new MimeHeader(header);
		mimeHeader.set("Mime-Version", "1.0");
		LocalMimePart root = new LocalMimePart(mimeHeader);
		mimeHeader.setMimeType(new MimeType("multipart", "alternative"));
		packMessage(textBody, htmlBody, root);
		if (icsAttachement != null) {
			root.addChild(icsAttachement);
		}
		
		return root;
	}

	/* (non-Javadoc)
	 * @see org.buni.meldware.calendar.jmx.SendMailMBean#createAlternativeWithAttachmentMessage(java.lang.String, org.columba.ristretto.message.Address[], org.columba.ristretto.message.Address[], org.columba.ristretto.io.Source, org.columba.ristretto.io.Source, org.columba.ristretto.message.LocalMimePart)
	 */
	public MimePart createAlternativeWithAttachmentMessage(String subject, Address[] toAddress,
			Address[] ccAddress, Source textBody, Source htmlBody, LocalMimePart icsAttachement)  throws ParserException {
		// Header is the actual header while BasicHeader wraps
		// a Header object to give easy access to the Header.
		Header header = createRootHeader(subject, toAddress, ccAddress);

		// Now a mimepart is prepared which actually holds the message
		// The mimeHeader is another convienice wrapper for the header
		// object
		MimeHeader mimeHeader = new MimeHeader(header);
		mimeHeader.set("Mime-Version", "1.0");
		LocalMimePart root = new LocalMimePart(mimeHeader);
		mimeHeader.setMimeType(new MimeType("multipart","mixed"));
		MimeHeader subrootHeader = new MimeHeader("multipart", "alternative");
		LocalMimePart messageroot = new LocalMimePart(subrootHeader);
		packMessage(textBody, htmlBody, messageroot);
		root.addChild(messageroot);
		if (icsAttachement != null) {
			root.addChild(icsAttachement);
		}
		
		return root;
	}
	
	/* (non-Javadoc)
	 * @see org.buni.meldware.calendar.jmx.SendMailMBean#createICSAttachment(java.lang.String, org.columba.ristretto.io.Source)
	 */
	public LocalMimePart createICSAttachment(String filename, Source source)
			throws IOException {
		MimeHeader attachmentHeader = new MimeHeader("text","calendar; method=request;");
		attachmentHeader.setContentTransferEncoding("8bit");
		attachmentHeader.set("Filename",filename);
		attachmentHeader.set("Path",filename);
		//attachmentHeader.set("Content-Type", "text/calendar");
		LocalMimePart attachmentPart = new LocalMimePart(attachmentHeader);
		attachmentPart.setBody(source);
		return attachmentPart;
	}
	
	private Header createRootHeader(String subject, Address[] toAddress, Address[] ccAddress)  throws ParserException{
		Header header = new Header();
		BasicHeader basicHeader = new BasicHeader(header);

		// Add the fields to the header
		// Note that the basicHeader is only a convienience wrapper
		// for our header object.
		basicHeader.setFrom(this.fromAddress);
		basicHeader.setTo(toAddress);
		if (ccAddress != null)
			basicHeader.setCc(ccAddress);
		basicHeader.setSubject(subject, Charset.forName("ISO-8859-1"));
		basicHeader.set("X-Mailer", getVersion());
		return header;
	}

	/* (non-Javadoc)
	 * @see org.buni.meldware.calendar.jmx.SendMailMBean#getFromAddress()
	 */
	public String getFromAddress() {
		return fromAddress.toString();
	}

	/* (non-Javadoc)
	 * @see org.buni.meldware.calendar.jmx.SendMailMBean#getPassword()
	 */
	public String getPassword() {
		return password;
	}
	
	/* (non-Javadoc)
	 * @see org.buni.meldware.calendar.jmx.SendMailMBean#getPort()
	 */
	public int getPort() {
		return port;
	}

	/* (non-Javadoc)
	 * @see org.buni.meldware.calendar.jmx.SendMailMBean#getSmtpServer()
	 */
	public String getSmtpServer() {
		return smtpServer;
	}

	/* (non-Javadoc)
	 * @see org.buni.meldware.calendar.jmx.SendMailMBean#getUsername()
	 */
	public String getUsername() {
		return username;
	}

	private String getVersion() {
		return "Hello mailer";
	}

	/* (non-Javadoc)
	 * @see org.buni.meldware.calendar.jmx.SendMailMBean#isVerbose()
	 */
	public boolean isVerbose() {
		return verbose;
	}

	private void packMessage(Source textBody, Source htmlBody, LocalMimePart root) {
		// Now we can add some message text
		MimeHeader textHeader = new MimeHeader();
		textHeader.setMimeType(new MimeType("text", "plain"));
		LocalMimePart textPart = new LocalMimePart(textHeader);
		textPart.setBody(textBody);
		root.addChild(textPart);
		// Now we can add some message html
		MimeHeader htmlHeader = new MimeHeader();
		htmlHeader.setMimeType(new MimeType("text", "html"));
		LocalMimePart htmlPart = new LocalMimePart(htmlHeader);
		htmlPart.setBody(htmlBody);
		root.addChild(htmlPart);
	}
	
	/* (non-Javadoc)
	 * @see org.buni.meldware.calendar.jmx.SendMailMBean#sendMail(org.columba.ristretto.message.Address[], org.columba.ristretto.message.Address[], org.columba.ristretto.message.MimePart)
	 */
	public void sendMail(Address[] toAddress, Address[] ccAddress,
			MimePart message) throws Exception {
		// Construct the proctol that is bound to the SMTP server
		SMTPProtocol protocol = new SMTPProtocol(smtpServer, port);
		// Open the port
		protocol.openPort();

		// The EHLO command gives us the capabilities of the server
		String capabilities[] = protocol.ehlo(InetAddress.getLocalHost());

		// Authenticate
		if (username != null) {
			String authCapability = null;
			for (int i = 0; i < capabilities.length; i++) {
				if (capabilities[i].startsWith("AUTH")) {
					authCapability = capabilities[i];
					break;
				}
			}
			if (authCapability != null) {
				try {
					protocol.auth(AuthenticationFactory.getInstance()
							.getSecurestMethod(authCapability), username,
							password.toCharArray());
				} catch (NoSuchAuthenticationException e3) {
					System.err.println(e3.getLocalizedMessage());
					return;
				} catch (AuthenticationException e) {
					System.err.println(e.getMessage());
					return;

				}
			} else {
				System.err.println("Server does not support Authentication!");
				return;
			}

		}

		// Setup from and recipient
		protocol.mail(fromAddress);

		for (Address to : toAddress) {
			protocol.rcpt(SMTPProtocol.TO, to);
		}
		if (ccAddress != null) {
			for (Address cc : ccAddress) {
				protocol.rcpt(SMTPProtocol.CC, cc);
			}
		}

		// Finally send the data
		protocol.data(MimeTreeRenderer.getInstance().renderMimePart(message));

		// And close the session
		protocol.quit();

	}

	public void setFromAddress(String fromaddress) {
		try {
			this.fromAddress = AddressParser.parseAddress(fromaddress);
		} catch (ParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see org.buni.meldware.calendar.jmx.SendMailMBean#setPassword(java.lang.String)
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/* (non-Javadoc)
	 * @see org.buni.meldware.calendar.jmx.SendMailMBean#setPort(int)
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/* (non-Javadoc)
	 * @see org.buni.meldware.calendar.jmx.SendMailMBean#setSmtpServer(java.lang.String)
	 */
	public void setSmtpServer(String smtp_server) {
		this.smtpServer = smtp_server;
	}

	/* (non-Javadoc)
	 * @see org.buni.meldware.calendar.jmx.SendMailMBean#setUsername(java.lang.String)
	 */
	public void setUsername(String username) {
		this.username = username;
	}
	
	/* (non-Javadoc)
	 * @see org.buni.meldware.calendar.jmx.SendMailMBean#setVerbose(boolean)
	 */
	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.buni.meldware.calendar.jmx.CalendarServerMBean#start()
	 */
	/* (non-Javadoc)
	 * @see org.buni.meldware.calendar.jmx.SendMailMBean#start()
	 */
	public void start() {
		log.debug("CalendarMailer JMX start");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.buni.meldware.calendar.jmx.CalendarServerMBean#stop()
	 */
	/* (non-Javadoc)
	 * @see org.buni.meldware.calendar.jmx.SendMailMBean#stop()
	 */
	public void stop() {
		log.debug("CalendarMailer JMX stop");
	}
}
