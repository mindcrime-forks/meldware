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

/**
 * DOCUMENT ME!
 * 
 * @author $Author: andy $
 * @version $Revision: 1.2 $
 */
public class RecordUpdate implements Comparable, Serializable {
	static final long serialVersionUID = "$Id: RecordUpdate.java,v 1.2 2007/12/30 02:00:09 andy Exp $".hashCode(); //$NON-NLS-1$

	private long recordId = 0;

	private int version = 0;

	/**
	 * Creates a new RecordUpdate object.
	 * 
	 * @param arecordId
	 *            DOCUMENT ME!
	 * @param aversion
	 *            DOCUMENT ME!
	 */
	public RecordUpdate(long arecordId, int aversion) {
		this.recordId = arecordId;
		this.version = aversion;
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @return
	 */
	public long getRecordId() {
		return this.recordId;
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @return
	 */
	public int getVersion() {
		return this.version;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object o) {
		if (this.recordId < ((RecordUpdate) o).recordId) {
			return -1;
		}

		if (this.recordId == ((RecordUpdate) o).recordId) {
			return -0;
		}

		return 1;
	}
}
