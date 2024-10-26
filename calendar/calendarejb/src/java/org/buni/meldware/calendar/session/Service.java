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
package org.buni.meldware.calendar.session;

import javax.ejb.SessionContext;
import javax.jms.ObjectMessage;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.buni.meldware.calendar.eventbus.Event;
import org.buni.meldware.calendar.session.exception.UserUnknownException;
import org.buni.meldware.common.preferences.UserProfile;
import org.buni.meldware.common.preferences.UserProfileService;
import org.buni.meldware.mail.util.MMJMXUtil;
import org.hibernate.HibernateException;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.Query;
import org.hibernate.Session;

import com.activescript.guid.GUIDException;
import com.activescript.guid.GUIDGenerator;

/**
 * Abstract class with common functionality for all session bean.
 * 
 * @author $Author: andy $
 * @version $Revision: 1.6 $
 */
public class Service {
	// SessionBean implementation -----------------------------------
	protected SessionContext sessionContext;

	protected Log log = null;

    protected UserProfileService profileService;

	private static GUIDGenerator guidGen = null;

	/**
	 * Creates a new Service object.
	 */
	public Service() {
		this.log = LogFactory.getLog(this.getClass());
		if (guidGen == null) {
			synchronized (this.getClass()) {
				try {
					if (guidGen == null)
						guidGen = new GUIDGenerator();
				} catch (GUIDException e) {
					this.log.fatal(
							"GUIDGenerator must work! - I will blow the stack",
							e);
					throw new RuntimeException("GUIDGenerator did not start");
				}
			}
		}
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param context
	 *            DOCUMENT ME!
	 */
	public void setSessionContext(SessionContext context) {
		this.sessionContext = context;
	}

    public void ejbCreate() {
        loadServices();
    }
    
	/**
	 * DOCUMENT ME!
	 */
	public void ejbRemove() {
	}

	/**
	 * DOCUMENT ME!
	 */
	public void ejbActivate() {
	}

	/**
	 * DOCUMENT ME!
	 */
	public void ejbPassivate() {
	}

	protected UserProfile getUser(String userName, Session session)
			throws HibernateException, UserUnknownException {
		UserProfile user;
		try {
			user = this.profileService.findProfile(userName);
		} catch (ObjectNotFoundException e) {
			throw new UserUnknownException(userName);
		}
		this.log.info("loaded user: " + user != null ? user.getUsername(): "with null profile: "+userName);

		return user;
	}

	protected String createHSQL_IN_String(String fieldName, int size) {
		String hsql = "";
		boolean first = true;
		if (size > 0) {
			hsql = " and " + fieldName + " in (";
			for (int i = 0; i < size; i++) {
				hsql += (first ? " " : ", ") + ":" + fieldName + i;
				first = false;
			}
			hsql += " )";
		}
		return hsql;
	}

	protected void bindHSQL_IN_clause(Query query, String fieldName, int[] field) {
		for (int i = 0; i < field.length; i++) {
			query.setInteger(fieldName + i, field[i]);
		}
	}

	protected void bindHSQL_IN_clause(Query query, String fieldName,
			String[] field) {
		for (int i = 0; i < field.length; i++) {
			query.setString(fieldName + i, field[i]);
		}
	}

	protected String createGUID() {
		return Service.guidGen.getUUID();
	}

	protected void publishEvent(Event event, String topicBindingName,
			String tcfBindingName) {

		try {
			TopicSession topicSession;
			TopicPublisher topicPublisher;
			TopicConnection topicConnection;
			InitialContext jndi = new InitialContext();
			this.log.debug("Looking up [" + tcfBindingName + "]");

			TopicConnectionFactory topicConnectionFactory = (TopicConnectionFactory) jndi
					.lookup(tcfBindingName);
			this.log.debug("About to create TopicConnection.");
			topicConnection = topicConnectionFactory.createTopicConnection();
			topicSession = topicConnection.createTopicSession(true,
					javax.jms.Session.AUTO_ACKNOWLEDGE);
			this.log.debug("Looking up topic name [" + topicBindingName + "].");

			Topic topic = (Topic) jndi.lookup(topicBindingName);
			this.log.debug("Creating TopicPublisher.");
			topicPublisher = topicSession.createPublisher(topic);
			this.log.debug("Starting TopicConnection.");

			ObjectMessage msg = topicSession.createObjectMessage();
			msg.setObject(event);
			topicPublisher.publish(msg);
			topicSession.close();
			topicConnection.close();
			jndi.close();
		} catch (Exception e) {
			this.log.error("Could not publish " + event.getClass().getName()
					+ " to topic.", e);
		}
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