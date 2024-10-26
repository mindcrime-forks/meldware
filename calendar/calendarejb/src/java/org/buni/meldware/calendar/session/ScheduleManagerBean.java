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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.ejb.EJBException;
import javax.ejb.SessionBean;

import org.buni.meldware.calendar.data.CalendarEvent;
import org.buni.meldware.calendar.data.Invite;
import org.buni.meldware.calendar.data.InviteStatus;
import org.buni.meldware.calendar.eventbus.CalendarChange;
import org.buni.meldware.calendar.session.exception.DuplicateInviteException;
import org.buni.meldware.calendar.session.exception.InvalidCalendarEventException;
import org.buni.meldware.calendar.session.exception.RecordNotFoundException;
import org.buni.meldware.calendar.session.exception.UserNotAuthorizedException;
import org.buni.meldware.calendar.session.exception.UserUnknownException;
import org.buni.meldware.calendar.util.HibernateLookUp;
import org.buni.meldware.common.preferences.UserProfile;
import org.buni.meldware.common.util.StatusUtil;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.Query;
import org.hibernate.Session;

/**
 * DOCUMENT ME!
 * 
 * @ejb.bean name="ScheduleManager" description="Service to manage calendar
 *           records" jndi-name="ejb/session/ScheduleManager" type="Stateless"
 * @ejb.transaction type="Required"
 * @ejb.transaction-type type="Container"
 * @ejb.util generate="physical"
 * @ejb.security-role-ref role-name="calendaruser" role-link="calendaruser"
 * @ejb.permission unchecked="true"
 * 
 * @ejb.env-entry name="profileServiceName" type="java.lang.String" value="meldware.base:type=UserProfileService,name=UserProfileService"
 */
public class ScheduleManagerBean extends Service implements SessionBean {
	static final long serialVersionUID = "$Id: ScheduleManagerBean.java,v 1.13 2007/12/30 01:58:40 andy Exp $".hashCode(); //$NON-NLS-1$

	/**
	 * Creates a new ScheduleManagerBean object.
	 */
	public ScheduleManagerBean() {
	}

	/**
	 * Create a new Event
	 * 
	 * @param userNames
	 *            DOCUMENT ME!
	 * @param event
	 *            DOCUMENT ME!
	 * @param ownerName
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
	 * @ejb.interface-method view-type="local"
	 * @ejb.permission role-name="calendaruser"
	 */
	public Invite[] scheduleEvent(String[] userNames, CalendarEvent event,
			String ownerName) throws EJBException, UserUnknownException,
			InvalidCalendarEventException, DuplicateInviteException {
		HashSet invites = new HashSet();
		HashSet organizers = new HashSet();
		Session session = null;
		this.log.debug("start scheduleing event");

		if ((userNames == null) || (event == null) || (ownerName == null)) {
			throw new InvalidCalendarEventException(
					InvalidCalendarEventException.MISSING_PARAMETERS);
		}

		// check if owner is in invitelist if not add it

		boolean included = false;
		for (int i = 0; ((i < userNames.length) && (!included)); i++) {
			included = userNames[i].equals(ownerName);
		}
		if (!included) {
			String[] oldUserNames = userNames;
			userNames = new String[userNames.length + 1];
			System
					.arraycopy(oldUserNames, 0, userNames, 0,
							oldUserNames.length);
			userNames[oldUserNames.length] = ownerName;
		}

		try {
			session = HibernateLookUp.getSessionFactory().openSession();

			// check start date vs. end date.
			if ((event.getStartDate() == null)
					|| (event.getStartDate().after(event.getEndDate()))) {
				throw new InvalidCalendarEventException(
						InvalidCalendarEventException.STARTDATE_BEFORE_ENDDATE);
			}

			// make sure insert version 1
			event.setVersion(new Integer(1));

			// set new GUID
			if (event.getGUID() == null || event.getGUID().trim().equals(""))
				event.setGUID(createGUID());

			// set create Date
			event.setCreateDate(new Date());
            event.setLastModified(new Date());

			// save invites
			event.setInvites(invites);
			event.setOrganizer(organizers);
			session.save(event);

			// create invites
			this.log.debug("Create Invites: " + userNames.length);
			for (int i = 0; i < userNames.length; i++) {
				Invite invite = createInvite(userNames[i], event, session);
				session.save(invite);
				invites.add(invite);
				if (invite.getUserName().equals(ownerName)) {
					organizers.add(invite.getUserName());
					invite.setStatus(InviteStatus.ACCEPTED);
				}
				
				// fire of an invitation notifictaion
				publishEvent(new CalendarChange(this.sessionContext
						.getCallerPrincipal().getName(),
						CalendarChange.INVITE_CREATED, invite.getInviteId()
								.longValue(), event.getRecordId().longValue()));
			}

			session.flush();
			this.log.debug("Event scheduled");

			return (Invite[]) invites.toArray(new Invite[0]);
		} catch (HibernateException e) {
			this.log.error("Error creating invite: " + e);
			this.sessionContext.setRollbackOnly();
			throw new EJBException(e);
		} catch (DuplicateInviteException e) {
			this.log.error("Error creating invite: " + e);
			this.sessionContext.setRollbackOnly();
			throw e;
		} catch (UserUnknownException e) {
			this.log.error("Error creating invite: " + e);
			this.sessionContext.setRollbackOnly();
			throw e;
		} finally {
			try {
				if (session != null) {
					session.close();
				}
			} catch (HibernateException e) {
				this.log.error("Failed to close Hibernate Session", e);
			}
		}
	}

	protected Invite createInvite(String userName, CalendarEvent event,
			Session session) throws HibernateException, UserUnknownException,
			DuplicateInviteException {
		UserProfile invitee = null;
        String nonUser = null;
		try {
			invitee = profileService.findProfile(userName);
            
		} catch (ObjectNotFoundException onfe) {

		}

       if (invitee == null) {
            nonUser = userName;
        }
        String test = invitee != null ? invitee.getUsername() : nonUser;
        if (event.getInvites().contains(test)) {
            throw new DuplicateInviteException(invitee.getUsername());            
        }
		this.log.debug("create invite for:" + invitee.getUsername() == null ? nonUser : invitee.getUsername());

		// for user created accepted... if you schedule a meeting I hope you
		// plan to attend
		Invite invite = new Invite(invitee.getUsername(), event,
				(userName.equals(this.sessionContext.getCallerPrincipal()
						.getName()) ? InviteStatus.ACCEPTED
						: InviteStatus.TENATIVE));

		return invite;
	}

	/**
	 * Get invite.
	 * 
	 * @param id
	 * @return
	 * @ejb.interface-method view-type="local"
	 * @ejb.permission unchecked="true"
	 */
	public Invite getInvite(long id)
	{
		Session session = null;
		try {
			session = HibernateLookUp.getSessionFactory().openSession();

			Query query = session
					.createQuery("select invite from "
							+ Invite.class.getName()
							+ " as invite inner join fetch invite.event as event where invite.inviteId = :inviteId ");
			query.setParameter("inviteId", new Long(id));
			Object result = query.list().get(0);
			return ((Invite)result);
		} catch (HibernateException e) {
			throw new EJBException(e);
		} finally {
			try {
				if (session != null) {
					session.close();
				}
			} catch (HibernateException e) {
				this.log.error("Failed to close Hibernate Session", e);
			}
		}
	}
	
	
	/**
	 * @param id
	 * @return
	 * @ejb.interface-method view-type="local"
	 * @ejb.permission unchecked="true"
	 */
	public Invite[] listResourceInvitesForEvent(long id)
	{
		Session session = null;
		try {
			session = HibernateLookUp.getSessionFactory().openSession();

			Query query = session
					.createQuery("select invite from "
							+ Invite.class.getName()
							+ " as invite inner join fetch invite.event as event"
							+ " where invite.event.recordId = :eventId and invite.user.preference.isResource = :isresource");
			query.setParameter("eventId", new Long(id));
			query.setParameter("isresource", true);
			return (Invite[])query.list().toArray(new Invite[]{});
		} catch (HibernateException e) {
			throw new EJBException(e);
		} finally {
			try {
				if (session != null) {
					session.close();
				}
			} catch (HibernateException e) {
				this.log.error("Failed to close Hibernate Session", e);
			}
		}
	}
	
	/**
	 * list users invites Event
	 * 
	 * @param startDate
	 *            DOCUMENT ME!
	 * @param endDate
	 *            DOCUMENT ME!
	 * @param ownerName
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
	 * @ejb.interface-method view-type="local"
	 * @ejb.permission unchecked="true"
	 */
	public Invite[] listInvites(Date startDate, Date endDate, String ownerName)
			throws EJBException, UserUnknownException,
			InvalidCalendarEventException {
		Session session = null;
		this.log.debug("start list invites");

		try {
			session = HibernateLookUp.getSessionFactory().openSession();

			UserProfile owner = getUser(ownerName, session);
			Query query = session
					.createQuery("select distinct invite from "
							+ Invite.class.getName()
							+ " as invite inner join fetch invite.event as event inner join fetch invite.event.invites"
							+ " where event.startDate >= :startDate "
							+ " and event.startDate  <= :endDate "
							+ " and invite.userName = :user and invite.status != :status order by event.startDate ");
			query.setParameter("startDate", startDate);
			query.setParameter("endDate", endDate);
			query.setParameter("user", owner.getUsername());
			query.setParameter("status", new Integer(InviteStatus.CANCELED));
			this.log.debug("invites loaded for timeFrame: " + startDate
					+ " to:" + endDate);
            List<Invite> is = (List<Invite>)query.list();
			Invite[] invites = (Invite[])is.toArray(new Invite[0]);
//          TODO HACK for dealing with query issues/lazy load issues
//          as a fetch this blows up. 
            for (Invite invite : invites) {
                CalendarEvent event = invite.getEvent();
                Set<String> orgs = (Set<String>)event.getOrganizer();
                for (String string : orgs) {
                    String org = string;
                    log.debug("organizer "+org);
                }
            }
			return invites;
		} catch (HibernateException e) {
			throw new EJBException(e);
		} finally {
			try {
				if (session != null) {
					session.close();
				}
			} catch (HibernateException e) {
				this.log.error("Failed to close Hibernate Session", e);
			}
		}
	}

	/**
	 * list users invites Event
	 * 
	 * @param startDate
	 *            DOCUMENT ME!
	 * @param endDate
	 *            DOCUMENT ME!
	 * @param ownerName
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
	 * @ejb.interface-method view-type="local"
	 * @ejb.permission unchecked="true"
	 */
	public Invite[] listInvites(String[] guids, String ownerName)
			throws EJBException, UserUnknownException {
		Session session = null;
		this.log.debug("start list invites");

		try {
			session = HibernateLookUp.getSessionFactory().openSession();

			UserProfile owner = getUser(ownerName, session);
			String filterString = "where this.event.GUID in (";
			for (int g = 0; g < guids.length; g++) {
				filterString += (g > 0 ? "," : "") + "\'" + guids[g] + "\'";
			}
			filterString += ")";
            //TODO ACO temprorily disable
			Query query = session.createQuery("from Invite where userName= :user")
                                 .setString("user", ownerName);//session
					//.createFilter(owner.getInvites(), filterString);

			return (Invite[]) query.list().toArray(new Invite[0]);
		} catch (HibernateException e) {
			throw new EJBException(e);
		} finally {
			try {
				if (session != null) {
					session.close();
				}
			} catch (HibernateException e) {
				this.log.error("Failed to close Hibernate Session", e);
			}
		}
	}
	
	/**
	 * @param guid
	 * @param ownerName
	 * @return
	 * @throws EJBException
	 * @throws UserUnknownException
	 * @ejb.interface-method view-type="local"
	 * @ejb.permission unchecked="true"
	 */
	public CalendarEvent loadEventByGUID(String guid, String ownerName)
			throws EJBException, UserUnknownException {
		Session session = null;
		this.log.debug("start list invites");

		try {
			session = HibernateLookUp.getSessionFactory().openSession();

			UserProfile owner = getUser(ownerName, session);
			String queryString = " from "	+ CalendarEvent.class.getName()
			+ " as event inner join fetch event.invites inner join fetch event.organizer "
			+ "where event.GUID = :guid ";
			Query query = session.createQuery(queryString);
			query.setParameter("guid", guid);
            List events =  query.list();
            CalendarEvent event = null;
            if (events != null && events.size() > 0) {
                event = (CalendarEvent)events.get(0);
            }
			return event;
		} catch (HibernateException e) {
			throw new EJBException(e);
		} finally {
			try {
				if (session != null) {
					session.close();
				}
			} catch (HibernateException e) {
				this.log.error("Failed to close Hibernate Session", e);
			}
		}
	}
	
	/**
	 * Load a specific Event
	 * 
	 * @param id
	 *            DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 * 
	 * @throws RecordNotFoundException
	 *             DOCUMENT ME!
	 * @throws EJBException
	 *             DOCUMENT ME!
	 * 
	 * @ejb.interface-method view-type="local"
	 * @ejb.permission role-name="calendaruser"
	 */
	public CalendarEvent getCalendarEventById(long id)
			throws RecordNotFoundException {
		Session session = null;
		this.log.debug("start loading event");

		try {
			session = HibernateLookUp.getSessionFactory().openSession();

			CalendarEvent event = (CalendarEvent) session.load(
					CalendarEvent.class, new Long(id));

			if (event == null) {
				throw new RecordNotFoundException(
						CalendarEvent.class.getName(), id);
			}

			Hibernate.initialize(event.getInvites());
			Hibernate.initialize(event.getOrganizer());
			this.log.debug("event loaded");

			return event;
		} catch (HibernateException e) {
			throw new EJBException(e);
		} finally {
			try {
				if (session != null) {
					session.close();
				}
			} catch (HibernateException e) {
				this.log.error("Failed to close Hibernate Session", e);
			}
		}
	}

	/**
	 * Update a specific Event
	 * 
	 * @param event
	 *            DOCUMENT ME!
	 * @param userName
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
	 * @ejb.interface-method view-type="local"
	 * @ejb.permission role-name="calendaruser"
	 */
	public CalendarEvent updateEvent(CalendarEvent event, String userName)
			throws EJBException, RecordNotFoundException,
			InvalidCalendarEventException, UserNotAuthorizedException {
		Session session = null;
		this.log.debug("start updateing event");

		try {
			session = HibernateLookUp.getSessionFactory().openSession();

			CalendarEvent savedEvent = null;
			if(event.getRecordId() != null)
			{
				savedEvent = (CalendarEvent) session.load(
					CalendarEvent.class, event.getRecordId());
			}
			else if(event.getGUID() != null)
			{
				Query query = session.createQuery("from " + CalendarEvent.class.getName() + " as cal where cal.GUID = :guid");
				query.setParameter("guid", event.getGUID());
				Iterator result = query.iterate();
				if(result.hasNext())
					savedEvent = (CalendarEvent)result.next();
			}
				
			if (savedEvent == null) {
				throw new RecordNotFoundException(
						CalendarEvent.class.getName(), event.getRecordId()
								.longValue());
			}

			// check if user is owner of event
			this.log.debug("check if event is organized by:" + userName);

			boolean authorized = false;
			Iterator organizers = savedEvent.getOrganizer().iterator();

			while (organizers.hasNext() && !authorized) {
				if (((String) organizers.next()).equals(userName)) {
					authorized = true;
					this.log.debug("User: " + userName
							+ " is a meeting organizer");
				}
			}

			if (!authorized) {
				throw new UserNotAuthorizedException(userName);
			}

			boolean rescheduled = false;
            short oldStatus = savedEvent.getStatus();

			if (savedEvent.getStartDate().getTime() != event.getStartDate()
					.getTime()) {
				savedEvent.setStartDate(event.getStartDate());
				rescheduled = true;
			}
			if (savedEvent.getEndDate().getTime() != event.getEndDate()
					.getTime()) {
				savedEvent.setEndDate(event.getEndDate());
				rescheduled = true;
			}
			savedEvent.setLocation(event.getLocation());
			savedEvent.setTitle(event.getTitle());
			savedEvent.setNote(event.getNote());
			savedEvent.setSeeURL(event.getSeeURL());
            savedEvent.setStatus(event.getStatus());
            savedEvent.setLastModified(new Date());
			session.flush();
			if (rescheduled) {
				publishEvent(new CalendarChange(this.sessionContext
						.getCallerPrincipal().getName(),
						CalendarChange.EVENT_RESCHEDULED, 0, savedEvent
								.getRecordId().longValue()));
			} else {
				publishEvent(new CalendarChange(this.sessionContext
						.getCallerPrincipal().getName(),
						CalendarChange.EVENT_CHANGED, 0, savedEvent
								.getRecordId().longValue()));
			}

            Hibernate.initialize(savedEvent.getInvites());
            //if the meeting is cancelled then cancel all invites.
            if (savedEvent.getStatus() == StatusUtil.CANCELLED && oldStatus != savedEvent.getStatus()) {
                for (Invite invite : (Set<Invite>)savedEvent.getInvites()) {
                    invite.setStatus(InviteStatus.CANCELED);
                    updateInvite(invite, userName);
                }
            }
            
			this.log.debug("event updated");
			return savedEvent;
		} catch (HibernateException e) {
			throw new EJBException(e);
		} finally {
			try {
				if (session != null) {
					session.close();
				}
			} catch (HibernateException e) {
				this.log.error("Failed to close Hibernate Session", e);
			}
		}
	}

	/**
	 * Invite more sers for the event
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
	 * @throws RecordNotFoundException
	 *             DOCUMENT ME!
	 * @throws InvalidCalendarEventException
	 *             DOCUMENT ME!
	 * @throws UserUnknownException
	 *             DOCUMENT ME!
	 * @throws DuplicateInviteException
	 *             DOCUMENT ME!
	 * 
	 * @ejb.interface-method view-type="local"
	 * @ejb.permission role-name="calendaruser"
	 */
	public Invite[] addInvitesToEvent(long eventId, String[] userNames)
			throws EJBException, RecordNotFoundException,
			InvalidCalendarEventException, UserUnknownException,
			DuplicateInviteException {
		Session session = null;
		this.log.debug("start updateing event with new invites");

		try {
			session = HibernateLookUp.getSessionFactory().openSession();

			CalendarEvent savedEvent = (CalendarEvent) session.load(
					CalendarEvent.class, new Long(eventId));

			if (savedEvent == null) {
				throw new RecordNotFoundException(
						CalendarEvent.class.getName(), eventId);
			}

			Set invites = savedEvent.getInvites();

			// create invites
			for (int i = 0; i < userNames.length; i++) {
				Invite invite = createInvite(userNames[i], savedEvent, session);
				session.save(invite);
				publishEvent(new CalendarChange(this.sessionContext
						.getCallerPrincipal().getName(),
						CalendarChange.INVITE_CREATED, invite.getInviteId()
								.longValue(), eventId));

				if (invites.add(invite)) {
					this.log.debug("invite added");
				}
			}
            savedEvent.setLastModified(new Date());
 
			session.flush();
			this.log.debug("event updated");

			return (Invite[]) invites.toArray(new Invite[0]);
		} catch (HibernateException e) {
			throw new EJBException(e);
		} finally {
			try {
				if (session != null) {
					session.close();
				}
			} catch (HibernateException e) {
				this.log.error("Failed to close Hibernate Session", e);
			}
		}
	}

	/**
	 * Update and event status for user.
	 * 
	 * @param invite
	 *            DOCUMENT ME!
	 * @param userName
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
	 * @ejb.permission role-name="calendaruser"
	 */

	public void updateInvite(Invite invite, String userName)
			throws EJBException, RecordNotFoundException,
			InvalidCalendarEventException, UserNotAuthorizedException {
		Session session = null;
		this.log.debug("start loading event");
		boolean isOrgnanizer = false;

		// No invite no record
		if (invite == null) {
			throw new RecordNotFoundException(Invite.class.getName(), invite
					.getInviteId().longValue());
		}

		try {
			session = HibernateLookUp.getSessionFactory().openSession();

			Invite savedInvite = (Invite) session.load(Invite.class, invite
					.getInviteId());

			// only can change own invite unless organizer
			if (!savedInvite.getUserName().equals(userName) && !savedInvite.getEvent().getOrganizer().contains(userName)) {
				throw new UserNotAuthorizedException(userName);
			}
            
			if ((invite.getStatus() == InviteStatus.CANCELED)
					|| (savedInvite.getStatus() == InviteStatus.CANCELED)) {

				if (invite.getStatus() == InviteStatus.CANCELED) {
//					Iterator iterator = savedInvite.getEvent().getInvites()
//							.iterator();
//					while (iterator.hasNext()) {
                    
                    if (!savedInvite.getEvent().getOrganizer().contains(userName)) {
                        throw new UserNotAuthorizedException(userName);
                    }
               
               //         savedInvite = (Invite) iterator.next();
						savedInvite.setStatus(InviteStatus.CANCELED);
						this.log.debug("Invite "
								+ savedInvite.getInviteId().longValue()
								+ " is updated to status:"
								+ savedInvite.getStatus());
				//	}
					// the event got rescheduled notify all
                        publishEvent(new CalendarChange(this.sessionContext
                                .getCallerPrincipal().getName(),
                                CalendarChange.INVITE_CHANGED, savedInvite
                                        .getInviteId().longValue(), savedInvite
                                        .getEvent().getRecordId().longValue()));
				}
				// Event RESTART
				else if (savedInvite.getStatus() == InviteStatus.CANCELED) {
					savedInvite.setStatus(invite.getStatus());
					this.log.debug("Invite "
							+ savedInvite.getInviteId().longValue()
							+ " is updated to status:"
							+ savedInvite.getStatus());
					Iterator iterator = savedInvite.getEvent().getInvites()
							.iterator();
					while (iterator.hasNext()) {
						Invite userInvite = (Invite) iterator.next();
						if (!userInvite.equals(savedInvite)) {
							userInvite.setStatus(InviteStatus.TENATIVE);
							this.log.debug("Invite "
									+ userInvite.getInviteId().longValue()
									+ " is updated to status:"
									+ userInvite.getStatus());
						}
					}
					// the event got rescheduled notify all
					publishEvent(new CalendarChange(this.sessionContext
							.getCallerPrincipal().getName(),
							CalendarChange.EVENT_RESCHEDULED, savedInvite
									.getInviteId().longValue(), savedInvite
									.getEvent().getRecordId().longValue()));
				}
			}
			// if inivte is not cancelled only update current status
			else {
				savedInvite.setStatus(invite.getStatus());
				this.log.debug("Invite " + invite.getInviteId().longValue()
						+ " is updated to status:" + invite.getStatus());
				// only the user's invite changed fire event
				publishEvent(new CalendarChange(this.sessionContext
						.getCallerPrincipal().getName(),
						CalendarChange.INVITE_CHANGED, savedInvite
								.getInviteId().longValue(), savedInvite
								.getEvent().getRecordId().longValue()));
			}
			session.flush();
		} catch (HibernateException e) {
			throw new EJBException(e);
		} finally {
			try {
				if (session != null) {
					session.close();
				}
			} catch (HibernateException e) {
				this.log.error("Failed to close Hibernate Session", e);
			}
		}
	}

	protected void publishEvent(CalendarChange event) {
		String topicBindingName = "topic/calendar";
		String tcfBindingName = "java:/JmsXA";
		publishEvent(event, topicBindingName, tcfBindingName);
	}

    /**
     * list users invites to Events searched by lastmodified date of the event
     * 
     * @param startDate
     *            DOCUMENT ME!
     * @param endDate
     *            DOCUMENT ME!
     * @param ownerName
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
     * @ejb.interface-method view-type="local"
     * @ejb.permission unchecked="true"
     */
    public Invite[] listInvitesByLastMod(Date startDate, Date endDate, String ownerName) throws EJBException, UserUnknownException, InvalidCalendarEventException {
        Session session = null;
        this.log.debug("start list invites");

        try {
            session = HibernateLookUp.getSessionFactory().openSession();

            UserProfile owner = getUser(ownerName, session);
            Query query = session
                    .createQuery("select distinct invite from "
                            + Invite.class.getName()
                            + " as invite inner join fetch invite.event as event inner join fetch invite.event.invites"
                            + " where event.lastModified >= :startDate "
                            + " and event.lastModified  <= :endDate "
                            + " and invite.userName = :user and invite.status != :status order by event.startDate ");
            query.setParameter("startDate", startDate);
            query.setParameter("endDate", endDate);
            query.setParameter("user", owner.getUsername());
            query.setParameter("status", new Integer(InviteStatus.CANCELED));
            this.log.debug("invites loaded for timeFrame: " + startDate
                    + " to:" + endDate);
            List<Invite> is = (List<Invite>)query.list();
            Invite[] invites = (Invite[])is.toArray(new Invite[0]);
//          TODO HACK for dealing with query issues/lazy load issues
//          as a fetch this blows up. 
            for (Invite invite : invites) {
                CalendarEvent event = invite.getEvent();
                Set<String> orgs = (Set<String>)event.getOrganizer();
                for (String string : orgs) {
                    String org = string;
                    log.debug("organizer "+org);
                }
            }
            return invites;
        } catch (HibernateException e) {
            throw new EJBException(e);
        } finally {
            try {
                if (session != null) {
                    session.close();
                }
            } catch (HibernateException e) {
                this.log.error("Failed to close Hibernate Session", e);
            }
        }
    }
    
    /**
     * list users invites to Events searched by lastmodified date of the event and status = canceled
     * 
     * @param startDate
     *            DOCUMENT ME!
     * @param endDate
     *            DOCUMENT ME!
     * @param ownerName
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
     * @ejb.interface-method view-type="local"
     * @ejb.permission unchecked="true"
     */
    public Invite[] listDeletedInvites(Date startDate, Date endDate, String ownerName) throws EJBException, UserUnknownException, InvalidCalendarEventException {
        Session session = null;
        this.log.debug("start list invites");

        try {
            session = HibernateLookUp.getSessionFactory().openSession();

            UserProfile owner = getUser(ownerName, session);
            Query query = session
                    .createQuery("select distinct invite from "
                            + Invite.class.getName()
                            + " as invite inner join fetch invite.event as event inner join fetch invite.event.invites"
                            + " where event.lastModified >= :startDate "
                            + " and event.lastModified  <= :endDate "
                            + " and invite.userName = :user and invite.status = :status order by event.startDate ");
            query.setParameter("startDate", startDate);
            query.setParameter("endDate", endDate);
            query.setParameter("user", owner.getUsername());
            query.setParameter("status", new Integer(InviteStatus.CANCELED));
            this.log.debug("invites loaded for timeFrame: " + startDate
                    + " to:" + endDate);
            List<Invite> is = (List<Invite>)query.list();
            Invite[] invites = (Invite[])is.toArray(new Invite[0]);
//          TODO HACK for dealing with query issues/lazy load issues
//          as a fetch this blows up. 
            for (Invite invite : invites) {
                CalendarEvent event = invite.getEvent();
                Set<String> orgs = (Set<String>)event.getOrganizer();
                for (String string : orgs) {
                    String org = string;
                    log.debug("organizer "+org);
                }
            }
            return invites;
        } catch (HibernateException e) {
            throw new EJBException(e);
        } finally {
            try {
                if (session != null) {
                    session.close();
                }
            } catch (HibernateException e) {
                this.log.error("Failed to close Hibernate Session", e);
            }
        }
    }

}
