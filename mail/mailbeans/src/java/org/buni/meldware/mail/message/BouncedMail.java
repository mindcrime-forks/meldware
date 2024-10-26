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

import java.io.ByteArrayInputStream;
import java.util.ArrayList;

import org.buni.meldware.mail.MailException;

/**
 * Class representing a failed mail message. It resets the headers as required
 * for a bounce message.
 *
 * @author KKhan
 * @author Michael Barker
 * @version $Revision: 1.3 $
 */
public class BouncedMail extends Mail {

	private static final long serialVersionUID = 4048794550518689846L;

	/**
     * @todo JAVADOC
	 * @param mail
	 * @param failed
	 * @throws MailException
	 */
	/**
	 * @param mail
	 * @param failed
	 * @throws MailException
	 */
	public BouncedMail (MailBodyManager mgr, Mail mail, MailAddress[] failed, MailAddress postmaster) throws MailException{
		super(mail);
		//Set the envelope from address to be empty
		MailAddress from = MailAddress.parseSMTPStyle("<>");
		super.to = new ArrayList<EnvelopedAddress>();
		super.to.add(new EnvelopedAddress(from, javax.mail.Message.RecipientType.TO));
		//super.ih = mail.ih;

		String[] hdr = super.ih.getHeader(StandardMailHeaders.SUBJECT);
		String subject = "";
		if (hdr.length > 0){
			super.ih.removeHeader(StandardMailHeaders.SUBJECT);
			subject = hdr[0];
		}
		super.ih.addHeader(StandardMailHeaders.SUBJECT, "Undeliverable: " + subject);

		//Set from and return-path headers to be postmaster
		//String postmaster = "<" + System.getProperty(SMTPConstants.POSTMASTER) + ">";
		super.ih.removeHeader(StandardMailHeaders.FROM);
		super.ih.addHeader(StandardMailHeaders.FROM, postmaster.toString());
		super.ih.removeHeader(StandardMailHeaders.RETURN_PATH);
		super.ih.addHeader(StandardMailHeaders.RETURN_PATH, postmaster.toString());

		//Get rid of CC and BCC headers and make sure there is only one recipient in
		//the to header
		super.ih.removeHeader(StandardMailHeaders.TO);
		super.ih.addHeader(StandardMailHeaders.TO, super.to.get(0).toString());
		super.ih.removeHeader(StandardMailHeaders.CC);
		super.ih.removeHeader(StandardMailHeaders.BCC);

		//Make sure the content type of this bounce message is plain text
		super.ih.removeHeader(StandardMailHeaders.CONTENT_TYPE);
		super.ih.addHeader(StandardMailHeaders.CONTENT_TYPE, "text/plain");


		StringBuffer bodybuf = new StringBuffer("Your message could not be delivered to the following recipients:\r\n");
		for (int i = 0 ; i < failed.length ; i++){
			bodybuf.append(failed[i].toString());
			bodybuf.append("\r\n");
		}

		body = mgr.createMailBody();
		mgr.read(body, new ByteArrayInputStream(bodybuf.toString().getBytes()));
	}
}
