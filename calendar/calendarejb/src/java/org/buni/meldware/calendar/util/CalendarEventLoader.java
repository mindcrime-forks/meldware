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
package org.buni.meldware.calendar.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.buni.meldware.calendar.data.CalendarEvent;

/**
 * DOCUMENT ME!
 * 
 * @author $Author: andy $
 * @version $Revision: 1.5 $
 */
public class CalendarEventLoader extends CalendarEvent {
	static final long serialVersionUID = "$Id: CalendarEventLoader.java,v 1.5 2007/12/30 01:58:40 andy Exp $".hashCode(); //$NON-NLS-1$

	/** DOCUMENT ME! */
	public static final String IMPORT_FORMAT = "yyyy.MM.dd HH:mm z";

	protected Log log = null;

	private List invites = new Vector();

	private String organizer = null;

	private SimpleDateFormat format = new SimpleDateFormat(IMPORT_FORMAT);

	/**
	 * Creates a new CalendarEventLoader object.
	 */
	public CalendarEventLoader() {
		this.log = LogFactory.getLog(CalendarEventLoader.class);
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param date
	 *            DOCUMENT ME!
	 */
	public void setStartDate(String date) {
		this.log.debug("startDate parseing");

		try {
			this.setStartDate(this.format.parse(date));
		} catch (ParseException e) {
			this.log.error("startDate parseing failed", e);
		}
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param date
	 *            DOCUMENT ME!
	 */
	public void setEndDate(String date) {
		this.log.debug("endDate parseing");

		try {
			this.setEndDate(this.format.parse(date));
		} catch (ParseException e) {
			this.log.debug("endDate parseing failed", e);
		}
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param user
	 *            DOCUMENT ME!
	 */
	public void setOrganizer(String user) {
		this.log.debug("organizer parsed:" + user);
		this.organizer = user;
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param aorganizer
	 *            DOCUMENT ME!
	 */
	public void setOrganizerAsString(String aorganizer) {
		this.organizer = aorganizer;
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public String getOrganizerAsString() {
		return this.organizer;
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public String[] getInvitesAsString() {
		return (String[]) this.invites.toArray(new String[0]);
	}

	/** 
	 * DOCUMENT ME!
	 * 
	 * @param user
	 *            DOCUMENT ME!
	 */
	public void addInvite(String user) {
		this.log.debug("invite parsed:" + user); 
		this.invites.add(user);
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public CalendarEvent getCalendarEvent() {
		return new CalendarEvent(null, this.getCreateDate(), this.getTitle(),
				this.getLocation(), this.getSeeURL(), this.getStartDate(), this
						.getEndDate(), this.getLastModified(), this.getStatus(), this.getNote(), this.getInvites(), this
						.getOrganizer());
	}
}
