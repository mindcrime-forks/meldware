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
package org.buni.meldware.calendar.jmx;

import java.util.HashMap;

import javax.naming.InitialContext;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * DOCUMENT ME!
 * 
 * @author $Author: asogor $
 * @version $Revision: 1.4 $
 */
public class CalendarServer implements CalendarServerMBean {
	/**
	 * DOCUMENT ME!
	 */
	Log log = null;

	private HashMap state;
	
	private static final String NOTIFICATION_MAIL_ADDRESS = "notificationMailAddress";

	private static final String IS_CALENDAR_NOTIFICATION_ENABLED = "isCalendarNotificiationEnabled";

	private static final String FRIEND_SEARCH_LIMIT = "friendSearchLimit";

	private static final String TEMPLATE_DIRECTORY = "templateDirectory";

	private static final String WEB_CALENDAR_STARTTIME = "webCalendarStartTime";

	private static final String WEB_CALENDAR_DAYDURATION = "webCalendarDayDuration";
	
	private static final String RDBMS_DIALECT = "rdbmsDialect";

	/**
	 * Creates a new CalendarServer object.
	 */
	public CalendarServer() {
		log = LogFactory.getLog(this.getClass());
		state = new HashMap();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.buni.meldware.calendar.jmx.CalendarServerMBean#create()
	 */
	public void create() {
		log.debug("CalendarServer JMX created");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.buni.meldware.calendar.jmx.CalendarServerMBean#destroy()
	 */
	public void destroy() {
		log.debug("CalendarServer JMX destroyed");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.buni.meldware.calendar.jmx.CalendarServerMBean#getSearchLimit()
	 */
	public String getSearchLimit() {
		return (String) this.state.get(FRIEND_SEARCH_LIMIT);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.buni.meldware.calendar.jmx.CalendarServerMBean#isCalendarNotificiationEnabled()
	 */
	public boolean isCalendarNotificiationEnabled() {
		return ((Boolean) this.state.get(IS_CALENDAR_NOTIFICATION_ENABLED))
				.booleanValue();
	}

	public String getNotificationMailAddress() {
		return (String)this.state.get(NOTIFICATION_MAIL_ADDRESS);
	}
	
	public void setNotificationMailAddress(String notificationMailAddress) {
		this.state.put(NOTIFICATION_MAIL_ADDRESS,notificationMailAddress);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.buni.meldware.calendar.jmx.CalendarServerMBean#setCalendarNotificiationEnabled(boolean)
	 */
	public void setCalendarNotificiationEnabled(boolean calendarNotification) {
		log.debug("Set CalendarNotificiationEnabled:" + calendarNotification);
		this.state.put(IS_CALENDAR_NOTIFICATION_ENABLED, new Boolean(
				calendarNotification));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.buni.meldware.calendar.jmx.CalendarServerMBean#setSearchLimit(java.lang.String)
	 */
	public void setSearchLimit(String limit) {
		log.debug("Set SearchLimit:" + limit);
		this.state.put(FRIEND_SEARCH_LIMIT, limit);
	}

	public String getTemplateDirectory() {
		return (String) this.state.get(TEMPLATE_DIRECTORY);
	}

	public void setTemplateDirectory(String templateDirectory) {
		this.state.put(TEMPLATE_DIRECTORY, templateDirectory);
	}

	public void setWebCalendarStartTime(String webCalendarStartTime) {
		this.state.put(WEB_CALENDAR_STARTTIME, webCalendarStartTime);
	}

	public String getWebCalendarStartTime() {
		return (String) this.state.get(WEB_CALENDAR_STARTTIME);
	}

	public void setWebCalendarDuration(String webCalendarEndTime) {
		this.state.put(WEB_CALENDAR_DAYDURATION, webCalendarEndTime);
	}

	public String getWebCalendarDuration() {
		return (String) this.state.get(WEB_CALENDAR_DAYDURATION);
	}

	public void setRDBMSDialect(String dialect) {
		this.state.put(RDBMS_DIALECT, dialect);
	}

	public String getRDBMSDialect() {
		return (String) this.state.get(RDBMS_DIALECT);
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.buni.meldware.calendar.jmx.CalendarServerMBean#start()
	 */
	public void start() {
		log.debug("CalendarServer JMX start");

		try {
			InitialContext initialContext = new InitialContext();
			try {
				initialContext.createSubcontext("calendarServer");
			} catch (NameAlreadyBoundException e) {
				// do nothing all good;
			}
			initialContext.bind("/calendarServer/Configuration", state);
			log.debug("Moses variable bind successful");
		} catch (NamingException e) {
			log.error("Failed to bind Moses variables", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.buni.meldware.calendar.jmx.CalendarServerMBean#stop()
	 */
	public void stop() {
		log.debug("CalendarServer JMX stop");

		try {
			InitialContext initialContext = new InitialContext();
			initialContext.unbind("/calendarServer/Configuration");
			log.debug("Moses variable unbind successful");
		} catch (NamingException e) {
			log.error("Failed to unbind Moses variables", e);
		}
	}
}
