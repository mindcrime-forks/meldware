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
 * Exception thrown when user address is not found.
 * 
 * @author $Author $
 * @version $Revision $
 */
public class UserTaskUnknownException extends UserException {
	static final long serialVersionUID = "$Id: UserTaskUnknownException.java,v 1.2 2007/12/30 01:58:41 andy Exp $".hashCode(); //$NON-NLS-1$

	/**
	 * Creates a new UserTaskUnknownException object.
	 * 
	 * @param userName
	 *            the user whose task not found
	 */
	public UserTaskUnknownException(String userName) {
		super();
		this.params = new String[] { userName };
	}

	/**
	 * Creates a new UserTaskUnknownException object.
	 * 
	 * @param taskId
	 *            the id of the task not found
	 */
	public UserTaskUnknownException(long taskId) {
		super();
		this.params = new String[] { String.valueOf(taskId) };
	}
}
