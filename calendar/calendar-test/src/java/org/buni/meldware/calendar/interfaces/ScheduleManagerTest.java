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
package org.buni.meldware.calendar.interfaces;

import java.util.Calendar;
import java.util.Date;

import org.buni.meldware.calendar.data.CalendarEvent;
import org.buni.meldware.calendar.data.CursorDirection;
import org.buni.meldware.calendar.data.Invite;
import org.buni.meldware.calendar.data.InviteStatus;
import org.buni.meldware.calendar.session.exception.InvalidCalendarEventException;
import org.buni.meldware.calendar.session.exception.UserNotAuthorizedException;
import org.buni.meldware.common.preferences.UserProfile;


/**
 * DOCUMENT ME!
 * 
 * @author $Author: andy $
 * @version $Revision: 1.2 $
 */
public class ScheduleManagerTest extends AbstractTestCase {
	/**
	 * Constructor for UserProfileBeanTest.
	 * 
	 * @param arg0
	 */
	public ScheduleManagerTest(String arg0) {
		super(arg0);
	}

	/**
	 * DOCUMENT ME!
	 */
	public void testSchedule() {
		System.out.println("** Create event for jerry");

		try {
			Date testStart = new Date();
			PIMService pim = PIMServiceUtil.getHome(
					getContext("jerry", "jerry")).create();
			UserProfile currentUser = pim.loadCurrentUserProfile();
			CalendarEvent event = new CalendarEvent();
			event.setTitle("testSchedule");
			event.setStartDate(new Date());
			event.setEndDate(new Date());

			Invite[] invites = pim.scheduleEvent(new String[] { currentUser.getUsername()
					 }, event);
			assertTrue("Invite count is:" + invites.length, invites.length == 1);
			System.out.println("Created invite Id:" + invites[0].getInviteId());

			Invite[] savedInvites = pim.listInvites(testStart, new Date());
			assertTrue("Saved invite count is:" + savedInvites.length,
					savedInvites.length == 1);
			System.out.println("Saved invite Id:"
					+ savedInvites[0].getInviteId());
			pim.remove();
		} catch (Exception e) {
			fail("testCreateView", e);
		}
	}

	/**
	 * DOCUMENT ME!
	 */
	public void testGroupSchedule() {
		System.out.println("** Create event for jerry");

		try {
			// first get testuser one login id and schedule meeting1

			PIMService pim = PIMServiceUtil.getHome(
					getContext("tom", "tom")).create();
			UserProfile userone = pim.loadCurrentUserProfile();
			CalendarEvent event1 = new CalendarEvent();
			event1.setTitle("groupevent1");
			event1.setStartDate(new Date());
			event1.setEndDate(new Date());

			Invite[] invites = pim.scheduleEvent(new String[] { userone
					.getUsername() }, event1);
			assertTrue("Invite count is:" + invites.length, invites.length == 1);
			System.out.println("Created invite Id:" + invites[0].getInviteId()
					+ " for event:"
					+ invites[0].getEvent().getRecordId().longValue());
			event1 = invites[0].getEvent();
			pim.remove();

			// login with user 2, invite user 1 for meeting2.
			pim = PIMServiceUtil.getHome(getContext("jerry", "jerry"))
					.create();

			UserProfile usertwo = pim.loadCurrentUserProfile();
			CalendarEvent event2 = new CalendarEvent();
			event2.setStartDate(new Date());
			event2.setEndDate(new Date());
			event2.setTitle("groupevent2");
			invites = pim.scheduleEvent(new String[] { userone.getUsername(),
					usertwo.getUsername() }, event2);
			assertTrue("Invite count is:" + invites.length, invites.length == 2);
			System.out.println("Created invite Id:" + invites[0].getInviteId()
					+ " and " + invites[1].getInviteId() + " for event:"
					+ invites[0].getEvent().getRecordId().longValue());
			event2 = invites[0].getEvent();

			// login with user 1, invite user 2 for meeting1.
			// and accept invitation for meeting 2.
			Invite firstInvite = null;

			for (int i = 0; i < invites.length; i++) {
				if (invites[i].getUserName().equals("tom")) {
					firstInvite = invites[i];
				}
			}

			assertTrue("First invite not found.", firstInvite != null);
			pim = PIMServiceUtil.getHome(getContext("tom", "tom"))
					.create();
			firstInvite.setStatus(InviteStatus.ACCEPTED);
			pim.updateInvite(firstInvite);
			invites = pim.addInvitesToEvent(event1.getRecordId().longValue(),
					new String[] { usertwo.getUsername() });

			// login as user 2 and decline meeting 1
			// update meeting 2 with new notes.
			Invite secondInvite = null;

			for (int i = 0; i < invites.length; i++) {
				if (invites[i].getUserName().equals("jerry")) {
					secondInvite = invites[i];
				}
			}

			assertTrue("Second invite not found.", secondInvite != null);
			pim = PIMServiceUtil.getHome(getContext("jerry", "jerry"))
					.create();
			secondInvite.setStatus(InviteStatus.DECLINED);
			pim.updateInvite(secondInvite);
			event2.setNote("Something new and important");
			pim.updateEvent(event2);
		} catch (Exception e) {
			fail("testCreateView", e);
		}
	}

	/**
	 * DOCUMENT ME!
	 */
	public void testUnAuthorizedInviteChange() {
		System.out.println("** Create event for jerry");

		try {
			PIMService pim = PIMServiceUtil.getHome(
					getContext("jerry", "jerry")).create();
			UserProfile currentUser = pim.loadCurrentUserProfile();
			CalendarEvent event = new CalendarEvent();
			event.setTitle("testUnAuthorizedInviteChange");
			event.setStartDate(new Date());
			event.setEndDate(new Date());

			Invite[] invites = pim.scheduleEvent(new String[] { currentUser
					.getUsername() }, event);
			assertTrue("Invite count is:" + invites.length, invites.length == 1);
			System.out.println("Created invite Id:" + invites[0].getInviteId());
			pim.remove();
			Invite invite = invites[0];
			pim = PIMServiceUtil.getHome(getContext("tom", "tom"))
					.create();
			invite.setStatus(InviteStatus.ACCEPTED);
			pim.updateInvite(invite);
		} catch (UserNotAuthorizedException e) {
			assertTrue(true);

			return;
		} catch (Exception e) {
			fail("testUnAuthorizedInviteChange", e);
		}

		fail("Not Authorized Exception not thrown.");
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @throws Exception
	 *             DOCUMENT ME!
	 */
	public void testUnAuthorizedEventChange() throws Exception {
		PIMService service = null;

		try {
			// cheap trick to get a clean time slice.
			// MySQL maaping uses DATETIME versus TIMESTAMP
			Thread.sleep(1000);

			Date testStart = new Date();

			CalendarEvent event = new CalendarEvent();
			service = PIMServiceUtil.getHome(
					getContext("tom", "tom")).create();

			UserProfile currentUser = service.loadCurrentUserProfile();
			event.setTitle("testUnAuthorizedEventChange");
			event.setStartDate(new Date());
			event.setEndDate(new Date());

			String[] list = new String[]{"tom","jerry"};

			service.scheduleEvent(list, event);
			service.remove();

			Date testEnd = new Date();
			service = PIMServiceUtil.getHome(
					getContext("jerry", "jerry")).create();

			Invite[] invites = service.listInvites(testStart, testEnd);
			assertTrue(invites.length == 1);
			invites[0].getEvent().setLocation("Someting else");
			service.updateEvent(invites[0].getEvent());
		} catch (UserNotAuthorizedException e) {
			service.remove();
			assertTrue(true);

			return;
		} catch (Exception e) {
			fail("testUnAuthorizedEventChange", e);
		}

		fail("Not Authorized Exception not thrown.");
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @throws Exception
	 *             DOCUMENT ME!
	 */
	public void testScheduleMeetingMultipleInvite() throws Exception {
		try {
			CalendarEvent event = new CalendarEvent();
			PIMService service = PIMServiceUtil.getHome(
					getContext("tom", "tom")).create();
			UserProfile currentUser = service.loadCurrentUserProfile();
			event.setTitle("testScheduleMeetingMultipleInvite");
			event.setStartDate(new Date());
			event.setEndDate(new Date());

			UserProfile[] users = service.listUsers("tom", 0,
					CursorDirection.FORWARD);
			String[] list = new String[users.length];

			for (int i = 0; i < list.length; i++) {
				list[i] = users[i].getUsername();
			}

			service.scheduleEvent(list, event);
			service.remove();
		} catch (Exception e) {
			fail("testScheduleMeeting", e);
		}
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @throws Exception
	 *             DOCUMENT ME!
	 */
	public void testLoadInvitesByGUID() throws Exception {
		try {
			PIMService service = PIMServiceUtil.getHome(
					getContext("tom", "tom")).create();
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DATE, -1);

			Date startDate = cal.getTime();
			cal.add(Calendar.DATE, 2);

			Date endDate = cal.getTime();

			Invite[] invites = service.listInvites(startDate, endDate);
			String[] guids = new String[invites.length];

			for (int i = 0; i < invites.length; i++) {
				guids[i] = invites[i].getEvent().getGUID();
			}

			Invite[] invitesByGuid = service.listInvites(guids);

			assertTrue("Invites: " + invites.length + " InvitesByGuid:"
					+ invitesByGuid.length,
					invites.length == invitesByGuid.length);
			service.remove();
		} catch (Exception e) {
			fail("testScheduleMeeting", e);
		}
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @throws Exception
	 *             DOCUMENT ME!
	 */
	public void testScheduleMeetingMultipleInviteCancelAndRestart()
			throws Exception {
		try {
			CalendarEvent event = new CalendarEvent();
			PIMService service = PIMServiceUtil.getHome(
					getContext("tom", "tom")).create();
			UserProfile currentUser = service.loadCurrentUserProfile();
			event.setTitle("testCancelRestart");
			event.setStartDate(new Date());
			event.setEndDate(new Date());

			UserProfile[] users = service.listUsers("jerry", 0,
					CursorDirection.FORWARD);
			String[] list = new String[users.length];

			for (int i = 0; i < list.length; i++) {
				list[i] = users[i].getUsername();
			}

			Invite[] invites = service.scheduleEvent(list, event);
			Invite myinvite = null;
			System.out.println("Number of invites:" + invites.length);

			for (int i = 0; i < invites.length; i++) {
				System.out.println("Check invite:"
						+ invites[i].getUserName());

				if (invites[i].getUserName().equals(currentUser.getUsername())) {
					myinvite = invites[i];
				}
			}

			assertTrue("Current invite not found.", myinvite != null);
			myinvite.setStatus(InviteStatus.CANCELED);
			service.updateInvite(myinvite);
			myinvite.setStatus(InviteStatus.ACCEPTED);
			service.updateInvite(myinvite);
			service.remove();
		} catch (Exception e) {
			fail("testScheduleMeeting", e);
		}
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @throws Exception
	 *             DOCUMENT ME!
	 */
	public void testScheduleMeetingNullTest() throws Exception {
		try {
			PIMService service = PIMServiceUtil.getHome(
					getContext("tom", "tom")).create();
			service.scheduleEvent(null, null);
		} catch (InvalidCalendarEventException e) {
			assertTrue(true);
		}
	}

	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	/**
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}
}
