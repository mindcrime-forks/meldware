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

import java.util.HashMap;
import java.util.Iterator;

import javax.ejb.MessageDrivenBean;
import javax.ejb.RemoveException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage; 

import org.buni.meldware.calendar.data.CalendarEvent;
import org.buni.meldware.calendar.data.Invite;
import org.buni.meldware.calendar.data.PreferenceConstants;
import org.buni.meldware.calendar.data.ServerInfo;
import org.buni.meldware.calendar.eventbus.CalendarChange;
import org.buni.meldware.calendar.interfaces.AddressManagerLocal;
import org.buni.meldware.calendar.interfaces.AddressManagerUtil;
import org.buni.meldware.calendar.interfaces.ScheduleManagerLocal;
import org.buni.meldware.calendar.interfaces.ScheduleManagerUtil;
import org.buni.meldware.calendar.session.exception.UserAddressUnknownException;
import org.buni.meldware.common.preferences.UserProfile;

import freemarker.ext.beans.BeansWrapper;
import freemarker.template.TemplateModelException;

/**
 * DOCUMENT ME!
 * 
 * @author $Author: andy $
 * @version $Revision: 1.5 $
 * 
 * @ejb.bean name="/ejb/reporting/EventMailMDB"
 *           acknowledge-mode="Auto-acknowledge"
 *           destination-type="javax.jms.Topic"
 *           subscription-durability="Durable" transaction-type="Container"
 * @ejb.transaction type="Required"
 * @ejb.transaction-type="Container"
 * @ejb.security-identity run-as="calendaruser"
 * @ejb.resource-ref res-ref-name="mail/Mail" res-type="javax.mail.Session"
 *                   res-auth="Container"
 * @jboss.destination-jndi-name name="topic/calendar"
 * @jboss:subscriber subscription-id="EventMailer" client-id="EventMailer"
 */
public class EventMailMDB extends BaseUserNotificationMDB implements
		MessageDrivenBean, MessageListener {
	static final long serialVersionUID = "$Id: EventMailMDB.java,v 1.5 2007/12/30 01:59:41 andy Exp $".hashCode(); //$NON-NLS-1$

	/** DOCUMENT ME! */
	public static final String TEMP_EVENTUPDATE = "EventUpdate";

	/** DOCUMENT ME! */
	public static final String TEMP_EVENTCANCEL = "EventCancel";

	/** DOCUMENT ME! */
	public static final String TEMP_EVENTRESCHEDULE = "InviteReschedule";

	/**
	 * DOCUMENT ME!
	 * 
	 * @param invite
	 *            DOCUMENT ME!
	 * @param preference
	 *            DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public void formatConfirmation(Invite invite, int preference,
			String toAddress) {
		HashMap values = new HashMap();

		BeansWrapper wrapper = new BeansWrapper();
		try {
			values.put("InviteUID", invite.getEvent().getGUID());
			values.put("calendarAcct", ServerInfo.getInfo(ServerInfo.NOTIFICATION_MAIL_ADDRESS));
			values.put("invite", wrapper.wrap(invite));
			this.processMessage(new String[] { toAddress }, values,
					"Confirmation", preference);
		} catch (TemplateModelException e) {
			this.log.error("Error creating template", e);
		} catch (Exception e) {
			this.log.error("Error sending message", e);
		}
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param templateName
	 *            DOCUMENT ME!
	 * @param meeting
	 *            DOCUMENT ME!
	 * @param preference
	 *            DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public void formatEventUpdate(String templateName, Invite invite,
			int preference, String toAddress) {
		HashMap values = new HashMap();

		try {
			BeansWrapper wrapper = new BeansWrapper();
			values.put("InviteUID", invite.getEvent().getGUID());
			values.put("calendarAcct", ServerInfo.getInfo(ServerInfo.NOTIFICATION_MAIL_ADDRESS));
			values.put("meeting", wrapper.wrap(invite.getEvent()));

			this.processMessage(new String[] { toAddress }, values,
					templateName, preference);
		} catch (TemplateModelException e) {
			this.log.error("Error creating template", e);
		} catch (Exception e) {
			this.log.error("Error sending message", e);
		}

	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param invite
	 *            DOCUMENT ME!
	 * @param preference
	 *            DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public void formatInvite(Invite invite, int preference, String toAddress) {
		HashMap values = new HashMap();

		try {
			BeansWrapper wrapper = new BeansWrapper();
			values.put("InviteUID", invite.getEvent().getGUID());
			values.put("calendarAcct", ServerInfo.getInfo(ServerInfo.NOTIFICATION_MAIL_ADDRESS));
			values.put("invite", wrapper.wrap(invite));

			this.processMessage(new String[] { toAddress }, values, "Invite",
					preference);

		} catch (TemplateModelException e) {
			this.log.error("Error creating template", e);
		} catch (Exception e) {
			this.log.error("Error sending message", e);
		}
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param aMessage
	 *            DOCUMENT ME!
	 */
	public void onMessage(Message aMessage) {
		// check if service is not disabled
		if (!((Boolean) ServerInfo.getInfo(ServerInfo.NOTIFICATION_ENABLED))
				.booleanValue()) {
			this.log.info("Notification is disabled, skipping event.");

			return;
		}

		try {
			CalendarChange event = (CalendarChange) ((ObjectMessage) aMessage)
					.getObject();

			if (event.getType() == CalendarChange.EVENT_CHANGED) {
				sendEventUpdate(TEMP_EVENTUPDATE, event);
			}

			if (event.getType() == CalendarChange.INVITE_CHANGED) {
				sendConfirmation(event);
			}

			if (event.getType() == CalendarChange.INVITE_CREATED) {
				sendInvite(event);
			}

			if (event.getType() == CalendarChange.EVENT_CANCELLED) {
				sendEventUpdate(TEMP_EVENTCANCEL, event);
			}

			if (event.getType() == CalendarChange.EVENT_RESCHEDULED) {
				sendEventUpdate(TEMP_EVENTRESCHEDULE, event);
			}
		} catch (Throwable exp) {
			this.log.error("Discard potential poison message", exp);
		}
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param event
	 *            DOCUMENT ME!
	 */
	public void sendConfirmation(CalendarChange event) {
		this.log.debug("Send confirmations for invite:" + event.getInviteId());

		ScheduleManagerLocal scheduleManager = null;
		AddressManagerLocal addressManager = null;
		Invite invite = null;

		try {
			scheduleManager = ScheduleManagerUtil.getLocalHome().create();
			addressManager = AddressManagerUtil.getLocalHome().create();

			CalendarEvent meeting = scheduleManager.getCalendarEventById(event
					.getEventId());

			Iterator iterator = meeting.getInvites().iterator();

			while (iterator.hasNext() && (invite == null)) {
				invite = (Invite) iterator.next();

				if (invite.getInviteId().longValue() != event.getInviteId()) {
					invite = null;
				}
			}

			Iterator organizers = meeting.getOrganizer().iterator();

			while (organizers.hasNext()) {
				String organizer = (String)organizers.next();
				UserProfile profile = profileService.findProfile(organizer);
				if ((profile.getPreferenceAsInt(PreferenceConstants.CALENDAR_NOTIFICATION) != PreferenceConstants.CALENDAR_NOTIFICATION_NONE)
						&& (!organizer.equals(invite.getUserName()))) {
					try {

						formatConfirmation(invite, profile.getPreferenceAsInt(PreferenceConstants.CALENDAR_NOTIFICATION)
								, addressManager
								.getUserAddress(organizer)
								);

					} catch (UserAddressUnknownException e) {
						this.log
								.warn(invite.getUserName()
										+ "'s address not registerd confirmation not sent");
					}
				}
			}
		} catch (Exception e) {
			this.log.error("Failed to e-mail confirmation:"
					+ event.getInviteId(), e);
		} finally {
			if (scheduleManager != null) {
				try {
					scheduleManager.remove();
				} catch (RemoveException e) {
					this.log.error("Failed to remove scheduleManager", e);
				}
			}

			if (addressManager != null) {
				try {
					addressManager.remove();
				} catch (RemoveException e) {
					this.log.error("Failed to remove addressManager", e);
				}
			}
		}
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param template
	 *            DOCUMENT ME!
	 * @param event
	 *            DOCUMENT ME!
	 */
	public void sendEventUpdate(String template, CalendarChange event) {
		this.log.debug("Send event update " + event.getEventId());

		ScheduleManagerLocal scheduleManager = null;
		AddressManagerLocal addressManager = null;
		Invite invite = null;
		String[] messagetext = null;

		try {
			scheduleManager = ScheduleManagerUtil.getLocalHome().create();
			addressManager = AddressManagerUtil.getLocalHome().create();

			CalendarEvent meeting = scheduleManager.getCalendarEventById(event
					.getEventId());

			Iterator invites = meeting.getInvites().iterator();

			// step through each invite that is impacted
			while (invites.hasNext()) {
				invite = (Invite) invites.next();

				UserProfile user = profileService.findProfile(invite.getUserName());

				// if user initiated the change than skip
				if ((!user.getUsername().equals(event.getActor()))
						&& (user.getPreferenceAsInt(PreferenceConstants.CALENDAR_NOTIFICATION) != PreferenceConstants.CALENDAR_NOTIFICATION_NONE)) {
					try {
						if (messagetext == null) {
							formatEventUpdate(template, invite, user.getPreferenceAsInt(PreferenceConstants.CALENDAR_NOTIFICATION),
									addressManager.getUserAddress(
											user.getUsername()));
						}
					} catch (Exception e) {
						this.log.warn("Failed to mail notification to user:"
								+ user.getUsername(), e);
					}
				}
			}
		} catch (Exception e) {
			this.log.error("Errors during e-mail distribution for event:"
					+ event.getEventId(), e);
		} finally {
			if (scheduleManager != null) {
				try {
					scheduleManager.remove();
				} catch (RemoveException e) {
					this.log.error("Failed to remove scheduleManager", e);
				}
			}

			if (addressManager != null) {
				try {
					addressManager.remove();
				} catch (RemoveException e) {
					this.log.error("Failed to remove addressManager", e);
				}
			}
		}
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param event
	 *            DOCUMENT ME!
	 */
	public void sendInvite(CalendarChange event) {
		this.log.debug("Send invite: " + event.getInviteId() + " and event:"
				+ event.getEventId());

		ScheduleManagerLocal scheduleManager = null;
		AddressManagerLocal addressManager = null;
		Invite invite = null;

		try {
			scheduleManager = ScheduleManagerUtil.getLocalHome().create();
			addressManager = AddressManagerUtil.getLocalHome().create();

			CalendarEvent meeting = scheduleManager.getCalendarEventById(event
					.getEventId());
			Iterator iterator = meeting.getInvites().iterator();

			while (iterator.hasNext() && (invite == null)) {
				Invite testinvite = (Invite) iterator.next();

				this.log.debug("Test id: "
						+ testinvite.getInviteId().longValue());

				if (testinvite.getInviteId().longValue() == event.getInviteId()) {
					invite = testinvite;
				}
			}

			// do not send invite to "self"
			if (invite.getUserName().equals(event.getActor())) {
				return;
			}
            UserProfile profile = profileService.findProfile(invite.getUserName());

			// check if user want to be notified
			if (profile.getPreferenceAsInt(PreferenceConstants.CALENDAR_NOTIFICATION) == PreferenceConstants.CALENDAR_NOTIFICATION_NONE) {
				return;
			}

			formatInvite(invite, profile.getPreferenceAsInt(PreferenceConstants.CALENDAR_NOTIFICATION), 
                    addressManager.getUserAddress(
					profile.getUsername()));
		} catch (UserAddressUnknownException e) {
			this.log.warn(invite.getUserName()
					+ "'s address not registerd invite not sent");
		} catch (Exception e) {
			this.log.error("Failed to e-mail invite:" + event.getInviteId(), e);
		} finally {
			if (scheduleManager != null) {
				try {
					scheduleManager.remove();
				} catch (RemoveException e) {
					this.log.error("Failed to remove scheduleManager", e);
				}
			}

			if (addressManager != null) {
				try {
					addressManager.remove();
				} catch (RemoveException e) {
					this.log.error("Failed to remove addressManager", e);
				}
			}
		}
	}
}
