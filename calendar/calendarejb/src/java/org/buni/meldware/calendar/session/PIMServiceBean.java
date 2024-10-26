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

import java.util.Date;

import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.RemoveException;
import javax.ejb.SessionBean;
import javax.naming.NamingException;

import org.buni.meldware.calendar.data.Address;
import org.buni.meldware.calendar.data.CalendarEvent;
import org.buni.meldware.calendar.data.Invite;
import org.buni.meldware.calendar.data.PreferenceConstants;
import org.buni.meldware.calendar.data.Task;
import org.buni.meldware.calendar.interfaces.AddressManagerLocal;
import org.buni.meldware.calendar.interfaces.AddressManagerUtil;
import org.buni.meldware.calendar.interfaces.ScheduleManagerLocal;
import org.buni.meldware.calendar.interfaces.ScheduleManagerUtil;
import org.buni.meldware.calendar.interfaces.UserTaskManagerLocal;
import org.buni.meldware.calendar.interfaces.UserTaskManagerUtil;
import org.buni.meldware.calendar.session.exception.DuplicateInviteException;
import org.buni.meldware.calendar.session.exception.DuplicateUserAddressException;
import org.buni.meldware.calendar.session.exception.InvalidCalendarEventException;
import org.buni.meldware.calendar.session.exception.RecordNotFoundException;
import org.buni.meldware.calendar.session.exception.UserAddressUnknownException;
import org.buni.meldware.calendar.session.exception.UserNotAuthorizedException;
import org.buni.meldware.calendar.session.exception.UserTaskUnknownException;
import org.buni.meldware.calendar.session.exception.UserUnknownException;
import org.buni.meldware.calendar.util.HibernateLookUp;
import org.buni.meldware.common.preferences.UserProfile;

/**
 * PIMServiceBean is the session facade for all the business funcionality. The
 * goal is that this Session facade should hide all the backend processing.
 * 
 * @author $Author: andy $
 * @version $Revision: 1.8 $
 * 
 * @ejb.bean name="PIMService" description="Public service to manage PIM"
 *           jndi-name="ejb/session/PIMService" type="Stateless"
 * @ejb.transaction type="Required"
 * @ejb.transaction-type type="Container"
 * @ejb.util generate="physical"
 * @ejb.security-role-ref role-name="calendaruser" role-link="calendaruser"
 * @ejb.security-role-ref role-name="calendaradmin" role-link="calendaradmin"
 * @ejb.permission unchecked="true"
 * 
 * @ejb.env-entry name="profileServiceName" type="java.lang.String" value="meldware.base:type=UserProfileService,name=UserProfileService"
 */ 
public class PIMServiceBean extends Service implements SessionBean {
	static final long serialVersionUID = "$Id: PIMServiceBean.java,v 1.8 2007/12/30 01:58:40 andy Exp $".hashCode(); //$NON-NLS-1$

	/**
	 * Create addresses and assign to current user based on security context.
	 * 
	 * @param addresses
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
	 * @ejb.permission role-name="calendaruser,calendaradmin"
	 */
	public String[] addAddresses(String[] addresses) throws EJBException,
			UserUnknownException {
		AddressManagerLocal addressManager = null;
		String[] result = null;

		if (addresses == null) {
			this.log.debug("addresses are null nothing to add.");
 
			return null;
		}

		try {
			addressManager = AddressManagerUtil.getLocalHome().create();
			result = addressManager.createAddresses(addresses,
					this.sessionContext.getCallerPrincipal().getName());
		} catch (NamingException e) {
			throw new EJBException(e);
		} catch (CreateException e) {
			throw new EJBException(e);
		} finally {
			try {
				addressManager.remove();
			} catch (RemoveException re) {
				this.log.warn("EJB Remove failed");
			}
		}

		return result;
	}

	/**
	 * Create tasks and assign to current user based on security context.
	 * 
	 * @param tasks
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
	 * @ejb.permission role-name="calendaruser,calendaradmin"
	 */
	public Task[] addTask(Task[] tasks) throws EJBException,
			UserUnknownException {
		UserTaskManagerLocal taskManager = null;
		Task[] result = null;

		if (tasks == null) {
			this.log.debug("tasks are null nothing to add.");

			return null;
		}

		try {
			taskManager = UserTaskManagerUtil.getLocalHome().create();
			result = taskManager.createTasks(this.sessionContext
					.getCallerPrincipal().getName(), tasks);
		} catch (NamingException e) {
			throw new EJBException(e);
		} catch (CreateException e) {
			throw new EJBException(e);
		} finally {
			try {
				taskManager.remove();
			} catch (RemoveException re) {
				this.log.warn("EJB Remove failed");
			}
		}

		return result;
	}

	/**
	 * Assign existing addreses to current user based on security context.
	 * 
	 * @param userNames
	 *            DOCUMENT ME!
	 * 
	 * @throws EJBException
	 *             DOCUMENT ME!
	 * @throws UserUnknownException
	 *             DOCUMENT ME!
	 * 
	 * @ejb.interface-method view-type="both"
	 * @ejb.permission role-name="calendaruser,calendaradmin"
	 */
	public void addFriends(String[] userNames) throws EJBException,
			UserUnknownException {
		AddressManagerLocal addressManager = null;

		if (userNames == null) {
			this.log.debug("addresses are null nothing to add.");

			return;
		}

		try {
			addressManager = AddressManagerUtil.getLocalHome().create();
			addressManager.addFriends(userNames, this.sessionContext
					.getCallerPrincipal().getName());
		} catch (NamingException e) {
			throw new EJBException(e);
		} catch (CreateException e) {
			throw new EJBException(e);
		} finally {
			try {
				addressManager.remove();
			} catch (RemoveException re) {
				this.log.warn("EJB Remove failed");
			}
		}
	}

	/**
	 * Create addressses and assign to current user based on security context.
	 * 
	 * @param eventId
	 *            DOCUMENT ME!
	 * @param userNames
	 *            DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 * 
	 * @throws EJBException
	 *             DOCUMENT ME!
	 * @throws InvalidCalendarEventException
	 *             DOCUMENT ME!
	 * @throws UserUnknownException
	 *             DOCUMENT ME!
	 * @throws RecordNotFoundException
	 *             DOCUMENT ME!
	 * @throws DuplicateInviteException
	 *             DOCUMENT ME!
	 * 
	 * @ejb.interface-method view-type="both"
	 * @ejb.permission role-name="calendaruser,calendaradmin"
	 */
	public Invite[] addInvitesToEvent(long eventId, String[] userNames)
			throws EJBException, InvalidCalendarEventException,
			UserUnknownException, RecordNotFoundException,
			DuplicateInviteException {
		ScheduleManagerLocal scheduler = null;
		Invite[] result = null;

		try {
			scheduler = ScheduleManagerUtil.getLocalHome().create();
			result = scheduler.addInvitesToEvent(eventId, userNames);
		} catch (NamingException e) {
			throw new EJBException(e);
		} catch (CreateException e) {
			throw new EJBException(e);
		} finally {
			try {
				scheduler.remove();
			} catch (RemoveException re) {
				this.log.warn("EJB Remove failed");
			}
		}

		return result;
	}

	/**
	 * Create a new userAddress if one does not exist already
	 * 
	 * @param userAddress
	 *            the address
	 * 
	 * @return updated useraddress
	 * 
	 * @throws EJBException
	 *             conatiner error
	 * @throws UserUnknownException
	 *             user not found
	 * @throws DuplicateUserAddressException
	 *             user already has an address
	 * 
	 * @ejb.interface-method view-type="both"
	 * @ejb.permission role-name="calendaruser,calendaradmin"
	 */
	public String addUserAddress(String userAddress) throws EJBException,
			UserUnknownException, DuplicateUserAddressException {
		AddressManagerLocal addressManager = null;
		String result = null;

		if (userAddress == null) {
			this.log.debug("addresses are null nothing to add.");

			return userAddress;
		}

		try {
			addressManager = AddressManagerUtil.getLocalHome().create();
			result = addressManager.createUserAddress(userAddress,
					this.sessionContext.getCallerPrincipal().getName());
		} catch (NamingException e) {
			throw new EJBException(e);
		} catch (CreateException e) {
			throw new EJBException(e);
		} finally {
			try {
				addressManager.remove();
			} catch (RemoveException re) {
				this.log.warn("EJB Remove failed");
			}
		}

		return result;
	}

	/**
	 * Get addresses for the current user based on security context.
	 * 
	 * @param id
	 *            DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 * 
	 * @throws EJBException
	 *             DOCUMENT ME!
	 * @throws UserUnknownException
	 *             DOCUMENT ME!
	 * @throws RecordNotFoundException
	 *             DOCUMENT ME!
	 * 
	 * @ejb.interface-method view-type="both"
	 * @ejb.permission role-name="calendaruser,calendaradmin"
	 */
	public String getAddressesById(long id) throws EJBException,
			UserUnknownException, RecordNotFoundException {
		AddressManagerLocal am = null;
		String result = null;

		try {
			am = AddressManagerUtil.getLocalHome().create();

			result = am.getAddressById(this.sessionContext.getCallerPrincipal()
					.getName(), id);
		} catch (NamingException e) {
			throw new EJBException(e);
		} catch (CreateException e) {
			throw new EJBException(e);
		} finally {
			try {
				am.remove();
			} catch (RemoveException re) {
				this.log.warn("EJB Remove failed");
			}
		}

		return result;
	}

	/**
	 * Returns the current users UserAddress.
	 * 
	 * @return User Address
	 * 
	 * @throws EJBException
	 *             DOCUMENT ME!
	 * @throws UserUnknownException
	 *             DOCUMENT ME!
	 * @throws UserAddressUnknownException
	 *             DOCUMENT ME!
	 * 
	 * @ejb.interface-method view-type="both"
	 * @ejb.permission role-name="calendaruser,calendaradmin"
	 */
	public String getUserAddress() throws EJBException, UserUnknownException,
			UserAddressUnknownException {
		AddressManagerLocal am = null;
		String result = null;

		try {
			am = AddressManagerUtil.getLocalHome().create();

			result = am.getUserAddress(this.sessionContext.getCallerPrincipal()
					.getName());
		} catch (NamingException e) {
			throw new EJBException(e);
		} catch (CreateException e) {
			throw new EJBException(e);
		} finally {
			try {
				am.remove();
			} catch (RemoveException re) {
				this.log.warn("EJB Remove failed");
			}
		}

		return result;
	}

    /**
     * Returns the current users account name.
     * 
     * @return User account name
     * 
     * @throws EJBException
     *             DOCUMENT ME!
     * @throws UserUnknownException
     *             DOCUMENT ME!
     * @throws UserAddressUnknownException
     *             DOCUMENT ME!
     * 
     * @ejb.interface-method view-type="both"
     * @ejb.permission role-name="calendaruser,calendaradmin"
     */
    public String getUserName() throws EJBException, UserUnknownException,
            UserAddressUnknownException {
        String result = null;


            result = this.sessionContext.getCallerPrincipal()
                    .getName();


        return result;
    }    
    
    
	/**
	 * Returns the current users UserAddress.
	 * 
	 * @param taskId
	 *            DOCUMENT ME!
	 * 
	 * @return User Address
	 * 
	 * @throws EJBException
	 *             DOCUMENT ME!
	 * @throws UserUnknownException
	 *             DOCUMENT ME!
	 * @throws UserTaskUnknownException
	 *             DOCUMENT ME!
	 * 
	 * @ejb.interface-method view-type="both"
	 * @ejb.permission role-name="calendaruser,calendaradmin"
	 */
	public Task getUserTask(long taskId) throws EJBException,
			UserUnknownException, UserTaskUnknownException {
		UserTaskManagerLocal tm = null;
		Task result = null;

		try {
			tm = UserTaskManagerUtil.getLocalHome().create();

			result = tm.getTask(this.sessionContext.getCallerPrincipal()
					.getName(), taskId);
		} catch (NamingException e) {
			throw new EJBException(e);
		} catch (CreateException e) {
			throw new EJBException(e);
		} finally {
			try {
				tm.remove();
			} catch (RemoveException re) {
				this.log.warn("EJB Remove failed");
			}
		}

		return result;
	}

	/**
	 * Get calendar Item for the current user based on security context.
	 * 
	 * @param id
	 *            DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 * 
	 * @throws EJBException
	 *             DOCUMENT ME!
	 * @throws RecordNotFoundException
	 *             DOCUMENT ME!
	 * 
	 * @ejb.interface-method view-type="both"
	 * @ejb.permission role-name="calendaruser,calendaradmin"
	 */
	public CalendarEvent getCalendarEventById(long id) throws EJBException,
			RecordNotFoundException {
		ScheduleManagerLocal scheduleManager = null;
		CalendarEvent result = null;

		try {
			scheduleManager = ScheduleManagerUtil.getLocalHome().create();

			result = scheduleManager.getCalendarEventById(id);
		} catch (NamingException e) {
			throw new EJBException(e);
		} catch (CreateException e) {
			throw new EJBException(e);
		} finally {
			try {
				scheduleManager.remove();
			} catch (RemoveException re) {
				this.log.warn("EJB Remove failed");
			}
		}

		return result;
	}

	/**
	 * Get addresses for the current user based on security context.
	 * 
	 * @param mark
	 *            DOCUMENT ME!
	 * @param sizeLimit
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
	 * @ejb.permission role-name="calendaruser,calendaradmin"
	 */
	public String[] listAddresses(long mark, int sizeLimit, int direction)
			throws EJBException, UserUnknownException {
		AddressManagerLocal am = null;
		String[] result = null;

		try {
			am = AddressManagerUtil.getLocalHome().create();

			result = am.listAddresses(this.sessionContext.getCallerPrincipal()
					.getName(), mark, sizeLimit, direction);
		} catch (NamingException e) {
			throw new EJBException(e);
		} catch (CreateException e) {
			throw new EJBException(e);
		} finally {
			try {
				am.remove();
			} catch (RemoveException re) {
				this.log.warn("EJB Remove failed");
			}
		}

		return result;
	}

	/**
	 * Get addresses for the current user based on security context.
	 * 
	 * @param startWord
	 *            DOCUMENT ME!
	 * @param endWord
	 *            DOCUMENT ME!
	 * @param sortKey
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
	 * @ejb.permission role-name="calendaruser,calendaradmin"
	 */
	public String[] listAddresses(String startWord, String endWord, int sortKey)
			throws EJBException, UserUnknownException {
		AddressManagerLocal am = null;
		String[] result = null;

		try {
			am = AddressManagerUtil.getLocalHome().create();

			result = am.listAddressesSortedByAlfa(this.sessionContext
					.getCallerPrincipal().getName(), startWord, endWord,
					sortKey);
		} catch (NamingException e) {
			throw new EJBException(e);
		} catch (CreateException e) {
			throw new EJBException(e);
		} finally {
			try {
				am.remove();
			} catch (RemoveException re) {
				this.log.warn("EJB Remove failed");
			}
		}

		return result;
	}

	/**
	 * List friends.
	 * 
	 * @return DOCUMENT ME!
	 * 
	 * @throws UserUnknownException
	 *             DOCUMENT ME!
	 * @throws EJBException
	 *             DOCUMENT ME!
	 * 
	 * @ejb.interface-method view-type="both"
	 * @ejb.permission role-name="calendaruser,calendaradmin"
	 */
	public String[] listFriends() throws UserUnknownException {
		AddressManagerLocal am = null;
		String[] result = null;

		try {
			am = AddressManagerUtil.getLocalHome().create();

			result = am.listFriends(this.sessionContext.getCallerPrincipal()
					.getName());
		} catch (NamingException e) {
			throw new EJBException(e);
		} catch (CreateException e) {
			throw new EJBException(e);
		} finally {
			try {
				am.remove();
			} catch (RemoveException re) {
				this.log.warn("EJB Remove failed");
			}
		}

		return result;
	}

	/**
	 * List invites of current user.
	 * 
	 * @param guids
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
	 * @ejb.permission role-name="calendaruser,calendaradmin"
	 */
	public Invite[] listInvites(String[] guids) throws EJBException,
			UserUnknownException {
		Invite[] result = null;
		ScheduleManagerLocal scheduler = null;

		try {
			scheduler = ScheduleManagerUtil.getLocalHome().create();
			result = scheduler.listInvites(guids, this.sessionContext
					.getCallerPrincipal().getName());
		} catch (NamingException e) {
			throw new EJBException(e);
		} catch (CreateException e) {
			throw new EJBException(e);
		} finally {
			try {
				scheduler.remove();
			} catch (RemoveException re) {
				this.log.warn("EJB Remove failed");
			}
		}

		return result;
	}

	/**
	 * @param guid
	 * @param ownerName
	 * @return
	 * @throws EJBException
	 * @throws UserUnknownException
	 * 
	 * @ejb.interface-method view-type="both"
	 * @ejb.permission unchecked="true"
	 */
	public CalendarEvent loadEventByGUID(String guid) throws UserUnknownException
	{
		ScheduleManagerLocal scheduler = null;

		try {
			scheduler = ScheduleManagerUtil.getLocalHome().create();
			return scheduler.loadEventByGUID(guid, this.sessionContext
					.getCallerPrincipal().getName());
		} catch (NamingException e) {
			throw new EJBException(e);
		} catch (CreateException e) {
			throw new EJBException(e);
		} finally {
			try {
				scheduler.remove();
			} catch (RemoveException re) {
				this.log.warn("EJB Remove failed");
			}
		}

	}
	/**
	 * List invites of current user.
	 * 
	 * @param startDate
	 *            DOCUMENT ME!
	 * @param endDate
	 *            DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 * 
	 * @throws EJBException
	 *             DOCUMENT ME!
	 * @throws UserUnknownException
	 *             DOCUMENT ME!
	 * @throws InvalidCalendarEventException
	 *             DOCUMENT ME!
	 * 
	 * @ejb.interface-method view-type="both"
	 * @ejb.permission role-name="calendaruser,calendaradmin"
	 */
	public Invite[] listInvites(Date startDate, Date endDate)
			throws EJBException, UserUnknownException,
			InvalidCalendarEventException {
		Invite[] result = null;
		ScheduleManagerLocal scheduler = null;

		try {
			scheduler = ScheduleManagerUtil.getLocalHome().create();

			result = scheduler.listInvites(startDate, endDate,
					this.sessionContext.getCallerPrincipal().getName());
		} catch (NamingException e) {
			throw new EJBException(e);
		} catch (CreateException e) {
			throw new EJBException(e);
		} finally {
			try {
				scheduler.remove();
			} catch (RemoveException re) {
				this.log.warn("EJB Remove failed");
			}
		}

		return result;
	}

	/**
	 * List invites of any user.
	 * 
	 * @param startDate
	 *            DOCUMENT ME!
	 * @param endDate
	 *            DOCUMENT ME!
	 * @param userName
	 *            DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 * 
	 * @throws EJBException
	 *             DOCUMENT ME!
	 * @throws UserUnknownException
	 *             DOCUMENT ME!
	 * @throws UserNotAuthorizedException
	 *             DOCUMENT ME!
	 * @throws InvalidCalendarEventException
	 *             DOCUMENT ME!
	 * 
	 * @ejb.interface-method view-type="both"
	 * @ejb.permission unchecked="true"
	 */
	public Invite[] listInvites(Date startDate, Date endDate, String userName)
			throws EJBException, UserUnknownException,
			UserNotAuthorizedException, InvalidCalendarEventException {
		Invite[] result = null;
		ScheduleManagerLocal scheduler = null;

		try {
			scheduler = ScheduleManagerUtil.getLocalHome().create();
            UserProfile profile = this.profileService.findProfile(userName);
            boolean publicFreeBusy = profile.getPreferenceAsBoolean(PreferenceConstants.PUBLIC_FREEBUSY);

			if (!publicFreeBusy && !this.sessionContext.getCallerPrincipal().getName().equals(userName)) {
				this.log.debug("User caledar is not public for:" + userName);
				throw new UserNotAuthorizedException("unknown");
			}
			result = scheduler.listInvites(startDate, endDate, userName);

		} catch (NamingException e) {
			throw new EJBException(e);
		} catch (CreateException e) {
			throw new EJBException(e);
		} finally {
			try {
				scheduler.remove();
			} catch (RemoveException re) {
				this.log.warn("EJB Remove failed");
			}
		}

		return result;
	}

	/**
	 * List users from system.
	 * 
	 * @param userName
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
	 * @ejb.permission role-name="calendaruser,calendaradmin"
	 */
	public UserProfile[] listUsers(String userName, int fetchSize, int direction)
			throws EJBException, UserUnknownException {
		UserProfile[] result = null;
 
			result = profileService.findProfiles(userName).toArray(new UserProfile[]{});

		return result;
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param priorityFilter
	 *            array of priority codes used to filter, if null no filter
	 * @param statusFilter
	 *            array of statusFilter codes used to filter, if null no filter
	 * @param categoryFilter
	 *            array of categories codes used to filter, if null no filter
	 * @param dueDateStart
	 *            DOCUMENT ME!
	 * @param dueDateEnd
	 *            DOCUMENT ME!
	 * @param orderKey
	 *            DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 * 
	 * @throws UserUnknownException
	 *             DOCUMENT ME!
	 * @throws EJBException
	 *             DOCUMENT ME!
	 * 
	 * @ejb.interface-method view-type="both"
	 * @ejb.permission role-name="calendaruser,calendaradmin"
	 */
	public Task[] listTasks(int[] priorityFilter, int[] statusFilter,
			String[] categoryFilter, Date dueDateStart, Date dueDateEnd,
			int orderKey) throws UserUnknownException {
		UserTaskManagerLocal service = null;
		Task[] result = null;

		try {
			service = UserTaskManagerUtil.getLocalHome().create();
			result = service.listTasks(this.sessionContext.getCallerPrincipal()
					.getName(), priorityFilter, statusFilter, categoryFilter,
					dueDateStart, dueDateEnd, orderKey);
		} catch (NamingException e) {
			throw new EJBException(e);
		} catch (CreateException e) {
			throw new EJBException(e);
		} finally {
			try {
				service.remove();
			} catch (RemoveException re) {
				this.log.warn("EJB Remove failed");
			}
		}

		return result;
	}

	/**
	 * Load Current UserProfile based on security context.
	 * 
	 * @return DOCUMENT ME!
	 * 
	 * @throws EJBException
	 *             DOCUMENT ME!
	 * @throws UserUnknownException
	 *             DOCUMENT ME!
	 * 
	 * @ejb.interface-method view-type="both"
	 * @ejb.permission role-name="calendaruser,calendaradmin"
	 */
	public UserProfile loadCurrentUserProfile() throws EJBException,
			UserUnknownException {
		UserProfile result = null;

			result = this.profileService.findProfile(this.sessionContext
					.getCallerPrincipal().getName());

		return result;
	}

	/**
	 * Remove/delete addressses from the current user based on security context.
	 * 
	 * @param addresses
	 *            DOCUMENT ME!
	 * 
	 * @throws EJBException
	 *             DOCUMENT ME!
	 * @throws UserUnknownException
	 *             DOCUMENT ME!
	 * 
	 * @ejb.interface-method view-type="both"
	 * @ejb.permission role-name="calendaruser,calendaradmin"
	 */
	public void removeAddresses(long[] addresses) throws EJBException,
			UserUnknownException {
		AddressManagerLocal addressManager = null;

		try {
			addressManager = AddressManagerUtil.getLocalHome().create();
			addressManager.removeAddresses(this.sessionContext
					.getCallerPrincipal().getName(), addresses);
		} catch (NamingException e) {
			throw new EJBException(e);
		} catch (CreateException e) {
			throw new EJBException(e);
		} finally {
			try {
				addressManager.remove();
			} catch (RemoveException re) {
				this.log.warn("EJB Remove failed");
			}
		}

		return;
	}

	/**
	 * Remove/delete tasks from the current user based on security context.
	 * 
	 * @param taskids
	 *            DOCUMENT ME!
	 * 
	 * @throws EJBException
	 *             DOCUMENT ME!
	 * @throws UserUnknownException
	 *             DOCUMENT ME!
	 * 
	 * @ejb.interface-method view-type="both"
	 * @ejb.permission role-name="calendaruser,calendaradmin"
	 */
	public void removeTasks(long[] taskids) throws EJBException,
			UserUnknownException {
		UserTaskManagerLocal taskManager = null;

		try {
			taskManager = UserTaskManagerUtil.getLocalHome().create();
			taskManager.removeTasks(this.sessionContext.getCallerPrincipal()
					.getName(), taskids);
		} catch (NamingException e) {
			throw new EJBException(e);
		} catch (CreateException e) {
			throw new EJBException(e);
		} finally {
			try {
				taskManager.remove();
			} catch (RemoveException re) {
				this.log.warn("EJB Remove failed");
			}
		}

		return;
	}

	/**
	 * Schedule a new Event
	 * 
	 * @param userNames
	 *            DOCUMENT ME!
	 * @param event
	 *            DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 * 
	 * @throws EJBException
	 *             DOCUMENT ME!
	 * @throws UserUnknownException
	 *             DOCUMENT ME!
	 * @throws InvalidCalendarEventException
	 *             DOCUMENT ME!
	 * @throws DuplicateInviteException
	 *             DOCUMENT ME!
	 * 
	 * @ejb.interface-method view-type="both"
	 * @ejb.permission role-name="calendaruser,calendaradmin"
	 */
	public Invite[] scheduleEvent(String[] userNames, CalendarEvent event)
			throws EJBException, UserUnknownException,
			InvalidCalendarEventException, DuplicateInviteException {
		ScheduleManagerLocal scheduler = null;
		Invite[] result = null;

		try {
			scheduler = ScheduleManagerUtil.getLocalHome().create();

			result = scheduler.scheduleEvent(userNames, event,
					this.sessionContext.getCallerPrincipal().getName());
		} catch (NamingException e) {
			throw new EJBException(e);
		} catch (CreateException e) {
			throw new EJBException(e);
		} finally {
			try {
				scheduler.remove();
			} catch (RemoveException re) {
				this.log.warn("EJB Remove failed");
			}
		}

		return result;
	}

	/**
	 * Search existing contacts based on field and serachValue
	 * 
	 * @param searchValue
	 *            DOCUMENT ME!
	 * @param fieldKey
	 *            DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 * 
	 * @throws EJBException
	 *             DOCUMENT ME!
	 * 
	 * @ejb.interface-method view-type="both"
	 * @ejb.permission role-name="calendaruser,calendaradmin"
	 */
	public Address[] searchContacts(String searchValue, int fieldKey)
			throws EJBException {
		AddressManagerLocal addressManager = null;

		try {
			addressManager = AddressManagerUtil.getLocalHome().create();

			return addressManager.searchContacts(this.sessionContext
					.getCallerPrincipal().getName(), searchValue, fieldKey);
		} catch (NamingException e) {
			throw new EJBException(e);
		} catch (CreateException e) {
			throw new EJBException(e);
		} catch (UserUnknownException e) {
			this.log.error("System error, current user not found !?!?", e);
			throw new EJBException(e);
		} finally {
			try {
				addressManager.remove();
			} catch (RemoveException re) {
				this.log.warn("EJB Remove failed");
			}
		}
	}

	/**
	 * Search for friends
	 * 
	 * @param userName
	 *            DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 * 
	 * @throws EJBException
	 *             DOCUMENT ME!
	 * 
	 * @ejb.interface-method view-type="both"
	 * @ejb.permission role-name="calendaruser,calendaradmin"
	 */
	public Address[] searchNewContacts(String userName) throws EJBException {
		AddressManagerLocal addressManager = null;

		try {
			addressManager = AddressManagerUtil.getLocalHome().create();

			return addressManager.searchNewContacts(this.sessionContext
					.getCallerPrincipal().getName(), userName);
		} catch (NamingException e) {
			throw new EJBException(e);
		} catch (CreateException e) {
			throw new EJBException(e);
		} finally {
			try {
				addressManager.remove();
			} catch (RemoveException re) {
				this.log.warn("EJB Remove failed");
			}
		}
	}


	/**
	 * Update addressses from the current user based on security context.
	 * 
	 * @param addresses
	 *            DOCUMENT ME!
	 * 
	 * @throws EJBException
	 *             DOCUMENT ME!
	 * @throws UserUnknownException
	 *             DOCUMENT ME!
	 * @throws UserNotAuthorizedException
	 *             DOCUMENT ME!
	 * 
	 * @ejb.interface-method view-type="both"
	 * @ejb.permission role-name="calendaruser,calendaradmin"
	 */
	public String[] updateAddresses(String[] addresses) throws EJBException,
			UserUnknownException, UserNotAuthorizedException {
		AddressManagerLocal am = null;
 
		try {
			am = AddressManagerUtil.getLocalHome().create();
			return am.updateAddresses(this.sessionContext.getCallerPrincipal()
					.getName(), addresses);
		} catch (NamingException e) {
			throw new EJBException(e);
		} catch (CreateException e) {
			throw new EJBException(e);
		} finally {
			try {
				am.remove();
			} catch (RemoveException re) {
				this.log.warn("EJB Remove failed");
			}
		}
	}

	/**
	 * Update tasks from the current user based on security context.
	 * 
	 * @param tasks
	 *            DOCUMENT ME!
	 * 
	 * @throws EJBException
	 *             DOCUMENT ME!
	 * @throws UserUnknownException
	 *             DOCUMENT ME!
	 * @throws UserNotAuthorizedException
	 *             DOCUMENT ME!
	 * 
	 * @ejb.interface-method view-type="both"
	 * @ejb.permission role-name="calendaruser,calendaradmin"
	 */
	public void updateTasks(Task[] tasks) throws EJBException,
			UserUnknownException, UserNotAuthorizedException {
		UserTaskManagerLocal taskService = null;

		try {
			taskService = UserTaskManagerUtil.getLocalHome().create();
			taskService.updateTasks(this.sessionContext.getCallerPrincipal()
					.getName(), tasks);
		} catch (NamingException e) {
			throw new EJBException(e);
		} catch (CreateException e) {
			throw new EJBException(e);
		} finally {
			try {
				taskService.remove();
			} catch (RemoveException re) {
				this.log.warn("EJB Remove failed");
			}
		}

		return;
	}

	/**
	 * Update and event status for current user.
	 * 
	 * @param invite
	 *            DOCUMENT ME!
	 * 
	 * @throws EJBException
	 *             DOCUMENT ME!
	 * @throws RecordNotFoundException
	 *             DOCUMENT ME!
	 * @throws InvalidCalendarEventException
	 *             DOCUMENT ME!
	 * @throws UserNotAuthorizedException
	 *             DOCUMENT ME!
	 * 
	 * @ejb.interface-method view-type="both"
	 * @ejb.permission role-name="calendaruser,calendaradmin"
	 */
	public void updateInvite(Invite invite) throws EJBException,
			RecordNotFoundException, InvalidCalendarEventException,
			UserNotAuthorizedException {
		ScheduleManagerLocal scheduler = null;

		try {
			scheduler = ScheduleManagerUtil.getLocalHome().create();
			scheduler.updateInvite(invite, this.sessionContext
					.getCallerPrincipal().getName());
		} catch (NamingException e) {
			throw new EJBException(e);
		} catch (CreateException e) {
			throw new EJBException(e);
		} finally {
			try {
				scheduler.remove();
			} catch (RemoveException re) {
				this.log.warn("EJB Remove failed");
			}
		}
	}

	/**
	 * Update and event.
	 * 
	 * @param event
	 *            DOCUMENT ME!
	 * 
	 * @throws EJBException
	 *             DOCUMENT ME!
	 * @throws RecordNotFoundException
	 *             DOCUMENT ME!
	 * @throws InvalidCalendarEventException
	 *             DOCUMENT ME!
	 * @throws UserNotAuthorizedException
	 *             DOCUMENT ME!
	 * 
	 * @ejb.interface-method view-type="both"
	 * @ejb.permission role-name="calendaruser,calendaradmin"
	 */
	public CalendarEvent updateEvent(CalendarEvent event) throws EJBException,
			RecordNotFoundException, InvalidCalendarEventException,
			UserNotAuthorizedException {
		ScheduleManagerLocal scheduler = null;

		try {
			scheduler = ScheduleManagerUtil.getLocalHome().create();
			return scheduler.updateEvent(event, this.sessionContext
					.getCallerPrincipal().getName());
		} catch (NamingException e) {
			throw new EJBException(e);
		} catch (CreateException e) {
			throw new EJBException(e);
		} finally {
			try {
				scheduler.remove();
			} catch (RemoveException re) {
				this.log.warn("EJB Remove failed");
			}
		}
	}
    

    /**
     * List invites of current user searched by their last modification date
     * 
     * @param guids
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
     * @ejb.permission role-name="calendaruser,calendaradmin"
     */     
    public Invite[] listInvitesByLastMod(Date startDate, Date endDate) throws EJBException, UserUnknownException, InvalidCalendarEventException {
        Invite[] result = null; 
        ScheduleManagerLocal scheduler = null;

        try {
            scheduler = ScheduleManagerUtil.getLocalHome().create();

            result = scheduler.listInvitesByLastMod(startDate, endDate,
                    this.sessionContext.getCallerPrincipal().getName());
        } catch (NamingException e) {
            throw new EJBException(e);
        } catch (CreateException e) {
            throw new EJBException(e);
        } finally {
            try {
                scheduler.remove();
            } catch (RemoveException re) {
                this.log.warn("EJB Remove failed");
            }
        }
        return result;
    }
    
    /**
     * List invites of current user searched by their last modification date and status = canceled
     * 
     * @param guids
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
     * @ejb.permission role-name="calendaruser,calendaradmin"
     */     
    public Invite[] listDeletedInvites(Date startDate, Date endDate) throws EJBException, UserUnknownException, InvalidCalendarEventException {
        Invite[] result = null; 
        ScheduleManagerLocal scheduler = null;

        try {
            scheduler = ScheduleManagerUtil.getLocalHome().create();

            result = scheduler.listDeletedInvites(startDate, endDate,
                    this.sessionContext.getCallerPrincipal().getName());
        } catch (NamingException e) {
            throw new EJBException(e);
        } catch (CreateException e) {
            throw new EJBException(e);
        } finally {
            try {
                scheduler.remove();
            } catch (RemoveException re) {
                this.log.warn("EJB Remove failed");
            }
        }
        return result;
    }

	/**
	 * Init app called when the app is deployed
	 * 
	 * @ejb.interface-method view-type="both"
	 * @ejb.transaction type="Never"
	 */
	public void initApp() {
		HibernateLookUp.checkSchema();
	}
	
	
}
