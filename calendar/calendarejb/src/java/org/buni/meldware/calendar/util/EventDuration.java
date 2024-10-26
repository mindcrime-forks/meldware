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

import org.buni.meldware.calendar.data.CalendarEvent;

/**
 * DOCUMENT ME!
 * 
 * @author aron Calculate Duration of meeting
 */
public class EventDuration {
	private int durationHours = 0;

	private int durationMinutes = 0;

	/**
	 * Creates a new EventDuration object.
	 * 
	 * @param event
	 *            DOCUMENT ME!
	 */
	public EventDuration(CalendarEvent event) {
		float duration = event.getEndDate().getTime()
				- event.getStartDate().getTime();
		this.durationHours = (int) duration / (1000 * 60 * 60);
		this.durationMinutes = (int) (duration / (1000 * 60))
				- (this.durationHours * 60);
	}

	/**
	 * Returns the durationHours.
	 * 
	 * @return int
	 */
	public int getDurationHours() {
		return this.durationHours;
	}

	/**
	 * Returns the durationMinutes.
	 * 
	 * @return int
	 */
	public int getDurationMinutes() {
		return this.durationMinutes;
	}
}
