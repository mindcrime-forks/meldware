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

/**
 * DOCUMENT ME!
 * 
 * @author $Author $
 * @version $Revision $
 */
public interface CalendarServerMBean {

	public void setCalendarNotificiationEnabled(boolean calendarNotification);

	public boolean isCalendarNotificiationEnabled();

	public void setTemplateDirectory(String templateDirectory);

	public String getTemplateDirectory();

	public void setWebCalendarStartTime(String webCalendarStartTime);

	public String getWebCalendarStartTime();

	public void setWebCalendarDuration(String webCalendarEndTime);

	public String getWebCalendarDuration();

	public void setSearchLimit(String limit);

	public String getSearchLimit();

	public void setRDBMSDialect(String dialect);

	public String getRDBMSDialect();
	
	public String getNotificationMailAddress();
	
	public void setNotificationMailAddress(String notificationMailAddress);
	
	public void start();

	public void stop();

	public void create();

	public void destroy();
}