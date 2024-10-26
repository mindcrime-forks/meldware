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

/**
 * UserPreference constants to indicate user preference settings.
 * 
 * @author $Author: andy $
 * @version $Revision: 1.4 $
 */
public interface PreferenceConstants {
    
    /**
     * Whether the user wants to be annoyed with "you have a friend" messages
     */
    public static final String FRIEND_NOTIFICATION="friendNotification";
    /**
     * Whether the user wants others to be able to check their freebusy
     */
    public static final String PUBLIC_FREEBUSY = "publicFreebusy";
    
    /**
     * get he calendar notification preference
     */
    public static final String CALENDAR_NOTIFICATION = "calendarNotification";
    
	/** DOCUMENT ME! */
	public static final int CALENDAR_NOTIFICATION_NONE = 0;

	/** DOCUMENT ME! */
	public static final int CALENDAR_NOTIFICATION_EMAIL = 1;

	/** DOCUMENT ME! */
	public static final int CALENDAR_NOTIFICATION_EXTERNAL = 2;

}
