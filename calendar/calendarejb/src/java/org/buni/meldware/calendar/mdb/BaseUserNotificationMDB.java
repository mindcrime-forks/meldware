/*
 * Bunisoft the Open Source Communications Company
 * Copyright 2007, Bunisoft LLC.,
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
package org.buni.meldware.calendar.mdb;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

import javax.ejb.MessageDrivenBean;
import javax.jms.MessageListener;
import javax.mail.MessagingException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.buni.meldware.calendar.data.ServerInfo;
import org.buni.meldware.calendar.jmx.SendMailMBean;
import org.buni.meldware.common.preferences.UserProfileService;
import org.buni.meldware.mail.util.MMJMXUtil;
import org.columba.ristretto.io.CharSequenceSource;
import org.columba.ristretto.message.Address;
import org.columba.ristretto.message.MimePart;
import org.columba.ristretto.parser.AddressParser;

import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * Basic functions for MDB's in moses.
 * 
 * @author $Author: andy $
 * @version $Revision: 1.6 $
 */
public abstract class BaseUserNotificationMDB extends BaseCalendarMDB implements
		MessageDrivenBean, MessageListener {
	/** DOCUMENT ME! */
	public static final String MIME_HTML = "text/html"; //$NON-NLS-1$

	/** DOCUMENT ME! */
	public static final String MIME_TEXT = "text/plain"; //$NON-NLS-1$

	protected static Configuration templateMaker = null;

    protected UserProfileService profileService;

	/**
	 * DOCUMENT ME!
	 * 
	 * @ejb.create-method
	 */
	public void ejbCreate() {
	    loadServices();
		if (templateMaker == null) {
			synchronized (this.getClass()) {
				if (templateMaker != null) {
					return;
				}

				templateMaker = new Configuration();

				try {
					ClassTemplateLoader ctl = new ClassTemplateLoader(
							getClass(), "/templates"); //$NON-NLS-1$
					TemplateLoader[] loaders = null;
					String filePath = (String) ServerInfo
							.getInfo(ServerInfo.TEMPLATE_DIRECTORY);

					if (filePath != null) {
						FileTemplateLoader ftl = new FileTemplateLoader(
								new File(filePath));
						loaders = new TemplateLoader[] { ftl, ctl };
					} else {
						loaders = new TemplateLoader[] { ctl };
					}

					MultiTemplateLoader mtl = new MultiTemplateLoader(loaders);
					templateMaker.setTemplateLoader(mtl);
					this.log.debug("Freemaker templateloader configured"); //$NON-NLS-1$
				} catch (IOException e) {
					this.log.warn(
							"Freemaker templateloader using classloader only", //$NON-NLS-1$
							e);
				}
			}
		}
	}

	/**
	 * DOCUMENT ME!
	 */
	public void ejbRemove() {
	}

	protected void processMessage(String[] toUserAddress, Map values,
			String templateName, int preference) throws Exception {
		StringWriter message = new StringWriter();
		String subject = ""; //$NON-NLS-1$
		String body = null;
		
		templateName += ".ftl"; //$NON-NLS-1$
		
		try {
			message.getBuffer().toString();

			Template template = templateMaker.getTemplate(templateName);
			template.process(values, message);
			body = message.getBuffer().toString();

			String[] parts = body.split("-##-TEXT-BODY-##-"); //$NON-NLS-1$

			// If there is a title split it out
			assert(parts.length == 2);
			subject = parts[0];
			body = parts[1];
			parts = body.split("-##-HTML-BODY-##-");
			assert(parts.length == 2);
			String textBody = parts[0];
			String htmlBody = parts[1];

			this.sendMail(toUserAddress, subject, textBody, htmlBody);
		} catch (IOException e) {
			this.log.error("Failed to generate e-mail for template: " + //$NON-NLS-1$
					templateName, e);
		} catch (Exception e) {
			this.log.error("Failed to generate e-mail" + templateName, e); //$NON-NLS-1$
		}
	}

	/**
	 * Utility method for sending out emails.
	 * 
	 * @param address
	 *            email addresses
	 * @param subject
	 *            email subject
	 * @param body
	 *            email body
	 * @param mimtype
	 *            DOCUMENT ME!
	 * 
	 * @throws MessagingException
	 *             DOCUMENT ME!
	 */
	private void sendMail(String[] address, String subject, String textBody, String htmlBody) throws Exception {
		SendMailMBean mailer = null;
		Address[] toAddress = new Address[address.length];
		CharSequenceSource textSource = new CharSequenceSource(textBody);
		CharSequenceSource htmlSource = new CharSequenceSource(htmlBody);
		
		mailer = MMJMXUtil.getMBean("meldware.calendar:service=CalendarMailer", SendMailMBean.class);
		
		for (int i = 0; i < toAddress.length; i++) {
			toAddress[i] = AddressParser.parseAddress(address[i]);
		}
		
		MimePart message = mailer.createAllAlternativeMessage(subject, toAddress, null, textSource, htmlSource, null);

		mailer.sendMail(toAddress, null, message);
	}
    
    private void loadServices() {
        
        try {
            Context ic = new InitialContext();
            String ps = (String)ic.lookup("java:/comp/env/profileServiceName");
            this.profileService = (UserProfileService)MMJMXUtil.getMBean(ps, UserProfileService.class);
        } catch (NamingException e) {
            
        }
    }
}
