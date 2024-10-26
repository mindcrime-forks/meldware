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
package org.buni.meldware.calendar.data;

import java.util.HashMap;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * ServerINFO constants for JNDI
 * 
 * @author Aron Sogor
 * @version $Revision: 1.3 $
 */
public abstract class ServerInfo {
	
	public static final String NOTIFICATION_MAIL_ADDRESS = "notificationMailAddress"; //$NON-NLS-1$
	
	public static final String NOTIFICATION_ENABLED = "isCalendarNotificiationEnabled"; //$NON-NLS-1$

	public static final String SEARCH_LIMIT = "friendSearchLimit"; //$NON-NLS-1$

	public static final String TEMPLATE_DIRECTORY = "templateDirectory"; //$NON-NLS-1$

	public static final String WEB_CALENDAR_STARTTIME = "webCalendarStartTime"; //$NON-NLS-1$

	public static final String WEB_CALENDAR_DAYDURATION = "webCalendarDayDuration"; //$NON-NLS-1$
	
	public static final String RDBMS_DIALECT = "rdbmsDialect"; //$NON-NLS-1$

	private static Log log = LogFactory.getLog(ServerInfo.class);

	private static InitialContext ctx = null;

	public static Object getInfo(String key) {
		try {
			ctx = new InitialContext();
			Object value = ((HashMap) ctx.lookup("/calendarServer/Configuration")).get(key); //$NON-NLS-1$
			ctx.close();
			return value;
		} catch (NamingException ne) {
			log.error("Server URL lookup failed", ne); //$NON-NLS-1$
		}
		return null;
	}
}
