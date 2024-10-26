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

import java.io.Serializable;
import java.util.Vector;

/**
 * Bacth load meta-data collector class
 * 
 * @author $Author: andy $
 * @version $Revision: 1.2 $
 */
public class ActivityReport implements Serializable {
	static final long serialVersionUID = "$Id: ActivityReport.java,v 1.2 2007/12/30 02:00:09 andy Exp $".hashCode(); //$NON-NLS-1$

	private long startTime = 0;

	private long stopTime = 0;

	private Vector logs = new Vector();

	/**
	 * start timer for process
	 */
	public void startTimer() {
		this.startTime = System.currentTimeMillis();
		this.stopTime = 0;
	}

	/**
	 * stop timer for process
	 */
	public void stopTimer() {
		this.stopTime = System.currentTimeMillis();
	}

	/**
	 * Calculate the process executeion time.
	 * 
	 * @return miliseconds for the process
	 */
	public long getLoadTime() {
		return this.startTime - this.stopTime;
	}

	/**
	 * Log Error that occured during the process.
	 * 
	 * @param msg
	 *            error message
	 * @param excp
	 *            exception occured
	 */
	public void logError(String msg, Exception excp) {
		this.logs.add(new Item(msg, excp));
	}

	/**
	 * Error Item.
	 * 
	 * @author $Author $
	 * @version $Revision $
	 */
	public class Item implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = -3223643766689526461L;

		private String message = null;

		private Exception excp = null;

		/**
		 * Creates a new Item.
		 * 
		 * @param amessage
		 *            for error
		 * @param aexcp
		 *            cause
		 */
		public Item(String amessage, Exception aexcp) {
			this.message = amessage;
			this.excp = aexcp;
		}

		/**
		 * Returns the excp.
		 * 
		 * @return cause
		 */
		public Exception getExcp() {
			return this.excp;
		}

		/**
		 * Returns the message.
		 * 
		 * @return message
		 */
		public String getMessage() {
			return this.message;
		}
	}
}
