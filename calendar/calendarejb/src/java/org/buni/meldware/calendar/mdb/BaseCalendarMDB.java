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
package org.buni.meldware.calendar.mdb;

import javax.ejb.MessageDrivenBean;
import javax.ejb.MessageDrivenContext;
import javax.jms.MessageListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Basic functions for MDB's in moses.
 * 
 * @author $Author: andy $
 * @version $Revision: 1.3 $
 */
public abstract class BaseCalendarMDB implements MessageDrivenBean, MessageListener {
	protected MessageDrivenContext theMessageDrivenContext;

	protected Log log = LogFactory.getLog(this.getClass());

	/**
	 * DOCUMENT ME!
	 * 
	 * @param aDrivenContext
	 *            DOCUMENT ME!
	 */
	public void setMessageDrivenContext(MessageDrivenContext aDrivenContext) {
		this.theMessageDrivenContext = aDrivenContext;
	}
}
