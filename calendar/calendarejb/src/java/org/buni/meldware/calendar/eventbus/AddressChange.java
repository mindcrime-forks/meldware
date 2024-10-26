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

import org.buni.meldware.calendar.data.Address;

/**
 * Event logged when an address is created, changed or deleted.
 * 
 * @author $Author: andy $
 * @version $Revision: 1.3 $
 */
public class AddressChange extends Event {
	static final long serialVersionUID = "$Id: AddressChange.java,v 1.3 2007/12/30 01:59:58 andy Exp $".hashCode(); //$NON-NLS-1$

	private String address = null;

	private int type = 0;

	public static final int ADDRESS_CREATED = 1;

	public static final int ADDRESS_CHANGED = 2;

	public static final int ADDRESS_DELETED = 3;

	public static final int ADDRESS_SHARED = 4;

	public AddressChange(String aactor, String oldAddress, int changeType) {
		super(aactor);
		this.type = changeType;
		this.address = oldAddress;
	}

	/**
	 * @return
	 */
	public String getAddress() {
		return this.address;
	}

	/**
	 * @param aaddress
	 */
	public void setAddress(String aaddress) {
		this.address = aaddress;
	}

	/**
	 * @return
	 */
	public int getType() {
		return this.type;
	}

	/**
	 * @param i
	 */
	public void setType(int i) {
		this.type = i;
	}

}
