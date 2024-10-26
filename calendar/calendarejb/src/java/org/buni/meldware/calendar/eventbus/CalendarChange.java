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
package org.buni.meldware.calendar.eventbus;

/**
 * Event logged when an invite is created, changed or event changed.
 * 
 * @author $Author: andy $
 * @version $Revision: 1.2 $
 */
public class CalendarChange extends Event {
	static final long serialVersionUID = "$Id: CalendarChange.java,v 1.2 2007/12/30 01:59:58 andy Exp $".hashCode(); //$NON-NLS-1$

	protected int type = 0;

	protected long inviteId = 0;

	protected long eventId = 0;

	public static final int INVITE_CREATED = 1;

	public static final int INVITE_CHANGED = 2;

	public static final int EVENT_CHANGED = 3;

	public static final int EVENT_CANCELLED = 4;

	public static final int EVENT_RESCHEDULED = 5;

	public CalendarChange(String aactor, int atype, long ainviteId,
			long aeventId) {
		super(aactor);
		this.eventId = aeventId;
		this.inviteId = ainviteId;
		this.type = atype;
	}

	/**
	 * Returns the eventId.
	 * 
	 * @return long
	 */
	public long getEventId() {
		return this.eventId;
	}

	/**
	 * Returns the inviteId.
	 * 
	 * @return long
	 */
	public long getInviteId() {
		return this.inviteId;
	}

	/**
	 * Returns the type.
	 * 
	 * @return int
	 */
	public int getType() {
		return this.type;
	}

	/**
	 * Sets the eventId.
	 * 
	 * @param eventId
	 *            The eventId to set
	 */
	public void setEventId(long aeventId) {
		this.eventId = aeventId;
	}

	/**
	 * Sets the inviteId.
	 * 
	 * @param inviteId
	 *            The inviteId to set
	 */
	public void setInviteId(long ainviteId) {
		this.inviteId = ainviteId;
	}

	/**
	 * Sets the type.
	 * 
	 * @param type
	 *            The type to set
	 */
	public void setType(int atype) {
		this.type = atype;
	}

}
