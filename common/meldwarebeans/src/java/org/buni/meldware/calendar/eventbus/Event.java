/*
 * JBoss Calendar Server.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.buni.meldware.calendar.eventbus;

import java.io.Serializable;
import java.util.Date;

/**
 * DOCUMENT ME!
 * 
 * @author $Author: asogor $
 * @version $Revision: 1.1 $
 */
public class Event implements Serializable {
	static final long serialVersionUID = "$Id: Event.java,v 1.1 2007/03/22 01:23:41 asogor Exp $".hashCode(); //$NON-NLS-1$

	protected Date createDate;

	protected String actor;

	public Event(String aactor) {
		this.createDate = new Date();
		this.actor = aactor;
	}

	/**
	 * Returns the actor.
	 * 
	 * @return String
	 */
	public String getActor() {
		return this.actor;
	}

}