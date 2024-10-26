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
package org.buni.meldware.calendar.session;

import javax.ejb.EJBException;
import javax.ejb.SessionBean;

import org.buni.meldware.calendar.session.exception.UserUnknownException;
import org.buni.meldware.common.preferences.UserProfile;


/**
 * DOCUMENT ME!
 * 
 * @ejb.bean name="UserSearchService" description="Public service to search for
 *           users" jndi-name="ejb/session/UserSearchService" type="Stateless"
 *           transaction-type="Container"
 * @ejb.util generate="physical"
 * @ejb.security-role-ref role-name="calendaruser" role-link="calendaruser"
 * @ejb.security-role-ref role-name="calendaradmin" role-link="calendaradmin"
 * @ejb.permission role-name="calendaruser,calendaradmin"
 */
public class UserSearchServiceBean extends Service implements SessionBean {
	static final long serialVersionUID = "$Id: UserSearchServiceBean.java,v 1.3 2007/12/30 01:58:40 andy Exp $".hashCode(); //$NON-NLS-1$

	/**
	 * List visible users to current user based on security context.
	 * 
	 * @param userId
	 *            DOCUMENT ME!
	 * @param fetchSize
	 *            DOCUMENT ME!
	 * @param direction
	 *            DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 * 
	 * @throws EJBException
	 *             DOCUMENT ME!
	 * @throws UserUnknownException
	 *             DOCUMENT ME!
	 * 
	 * @ejb.interface-method view-type="both"
	 */
	public UserProfile[] listUsers(String userName, int fetchSize, int direction)
			throws EJBException, UserUnknownException {
			return this.profileService.findProfiles(userName).toArray(new UserProfile[]{});
	}
}
