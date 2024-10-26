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
package org.buni.meldware.calendar.session.exception;


/**
 * Exception Invalid calendar Entry is thrown if the calendar entry violates
 * business rules.
 * 
 * @author $Author: andy $
 * @version $Revision: 1.2 $
 */
public class InvalidCalendarEventException extends UserException {
	static final long serialVersionUID = "$Id: InvalidCalendarEventException.java,v 1.2 2007/12/30 01:58:41 andy Exp $".hashCode(); //$NON-NLS-1$

	/** DOCUMENT ME! */
	public static final long STARTDATE_BEFORE_ENDDATE = 1;

	/** DOCUMENT ME! */
	public static final long OWNER_MISSING = 2;

	/** DOCUMENT ME! */
	public static final long MISSING_PARAMETERS = 3;

	private long code = 0;

	/**
	 * Creates a new InvalidCalendarEventException object.
	 * 
	 * @param acode
	 *            DOCUMENT ME!
	 */
	public InvalidCalendarEventException(long acode) {
		super();
		this.code = acode;
	}
}
