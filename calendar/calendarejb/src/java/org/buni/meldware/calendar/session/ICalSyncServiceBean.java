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
import java.util.HashMap;
import java.util.Iterator;

import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.SessionBean;
import javax.naming.NamingException;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VToDo;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.DtEnd;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.Due;
import net.fortuna.ical4j.model.property.Location;
import net.fortuna.ical4j.model.property.Priority;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Sequence;
import net.fortuna.ical4j.model.property.Status;
import net.fortuna.ical4j.model.property.Summary;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.Version;

import org.buni.meldware.calendar.data.CalendarEvent;
import org.buni.meldware.calendar.data.Invite;
import org.buni.meldware.calendar.data.InviteStatus;
import org.buni.meldware.calendar.data.Task;
import org.buni.meldware.calendar.data.TaskSortKeys;
import org.buni.meldware.calendar.interfaces.ScheduleManagerLocal;
import org.buni.meldware.calendar.interfaces.ScheduleManagerUtil;
import org.buni.meldware.calendar.interfaces.UserTaskManagerLocal;
import org.buni.meldware.calendar.interfaces.UserTaskManagerUtil;
import org.buni.meldware.calendar.session.exception.InvalidCalendarEventException;
import org.buni.meldware.calendar.session.exception.RecordNotFoundException;
import org.buni.meldware.calendar.session.exception.UserNotAuthorizedException;
import org.buni.meldware.calendar.session.exception.UserUnknownException;


/**
 * DOCUMENT ME!
 * 
 * @ejb.bean name="ICalSyncService" description="Service to manage calendar
 *           records" jndi-name="ejb/session/ICalSyncService" type="Stateless"
 * @ejb.transaction type="Required"
 * @ejb.transaction-type type="Container"
 * @ejb.util generate="physical"
 * @ejb.security-role-ref role-name="calendaruser" role-link="calendaruser"
 * @ejb.permission unchecked="true"
 */
public class ICalSyncServiceBean extends Service implements SessionBean {
	static final long serialVersionUID = "$Id: ICalSyncServiceBean.java,v 1.5 2007/12/30 01:58:40 andy Exp $".hashCode(); //$NON-NLS-1$

	/**
	 * Creates a new ScheduleManagerBean object.
	 */
	public ICalSyncServiceBean() {
		super();
	}

	/**
	 * Generate a VCalendar
	 * 
	 * @return DOCUMENT ME!
	 * 
	 * @throws EJBException
	 *             DOCUMENT ME!
	 * 
	 * @ejb.interface-method view-type="both"
	 * @ejb.permission role-name="calendaruser"
	 */
	public net.fortuna.ical4j.model.Calendar getICalendar() throws EJBException {
		java.util.Calendar calendar = java.util.Calendar.getInstance();
		net.fortuna.ical4j.model.Calendar vCalendar = new Calendar();
		VEvent event = null;
		VToDo task = null;
		ScheduleManagerLocal scheduleManager = null;
		UserTaskManagerLocal taskManager = null;

		calendar.add(java.util.Calendar.MONTH, -3);

		Date startDate = calendar.getTime();
		calendar.add(java.util.Calendar.MONTH, 6);

		Date endDate = calendar.getTime();

		vCalendar.getProperties().add(new ProdId("MOSES"));
		vCalendar.getProperties().add(new Version("@CVS_TAG@", ""));

		try {
			scheduleManager = ScheduleManagerUtil.getLocalHome().create();

			Invite[] invites = scheduleManager.listInvites(startDate, endDate,
					this.sessionContext.getCallerPrincipal().getName());

			for (int x = 0; x < invites.length; x++) {
				event = new VEvent(new net.fortuna.ical4j.model.DateTime(
						invites[x].getEvent().getStartDate()),
						new net.fortuna.ical4j.model.DateTime(invites[x]
								.getEvent().getEndDate()), invites[x]
								.getEvent().getTitle());

				event.getProperties().add(
						new Sequence(invites[x].getEvent().getVersion()
								.intValue()));

				if (invites[x].getEvent().getNote() != null) {
					event.getProperties().add(
							new Description(invites[x].getEvent().getNote()));
				}

				if (invites[x].getEvent().getLocation() != null) {
					event.getProperties().add(
							new Location(invites[x].getEvent().getLocation()));
				}

				if (invites[x].getStatus() == 0) {
					event.getProperties().add(Status.VEVENT_TENTATIVE);
				}

				if (invites[x].getStatus() == 1) {
					event.getProperties().add(Status.VEVENT_CONFIRMED);
				}

				if ((invites[x].getStatus() == 2)
						|| (invites[x].getStatus() == 3)) {
					event.getProperties().add(Status.VEVENT_CANCELLED);
				}

				event.getProperties().add(
						new Uid(invites[x].getEvent().getGUID()));
				vCalendar.getComponents().add(event);
			}

			taskManager = UserTaskManagerUtil.getLocalHome().create();

			Task[] tasks = taskManager.listTasks(this.sessionContext
					.getCallerPrincipal().getName(), null, null, null,
					startDate, endDate,
					TaskSortKeys.SORT_BY_PRORITY_STATUS_TITLE);

			for (int i = 0; i < tasks.length; i++) {
				task = new VToDo(new net.fortuna.ical4j.model.DateTime(tasks[i]
						.getDueDate()), new net.fortuna.ical4j.model.DateTime(
						tasks[i].getDueDate()), tasks[i].getTitle());

				task.getProperties().add(
						new Sequence(tasks[i].getVersion().intValue()));

				task.getProperties().add(new Uid(tasks[i].getGUID()));

				switch (tasks[i].getStatus()) {
				case TaskSortKeys.STATUS_CODE_OPEN:
					task.getProperties().add(Status.VTODO_IN_PROCESS);

					break;

				case TaskSortKeys.STATUS_CODE_CANCELLED:
					task.getProperties().add(Status.VTODO_CANCELLED);

					break;

				case TaskSortKeys.STATUS_CODE_COMPLETED:
					task.getProperties().add(Status.VTODO_COMPLETED);

					break;

				default:
					task.getProperties().add(Status.VTODO_IN_PROCESS);

					break;
				}

				if (tasks[i].getNote() != null) {
					task.getProperties().add(
							new Description(tasks[i].getNote()));
				}

				vCalendar.getComponents().add(task);
			}

			return vCalendar;
		} catch (Exception exp) {
			this.log.error("failed to load schedule", exp);
			throw new EJBException(exp);
		} finally {
			try {
				if (scheduleManager != null) {
					scheduleManager.remove();
				}
			} catch (Exception e) {
				this.log.error("failed to remove PIMServiceLocal ", e);
			}
		}
	}

	/**
	 * Upload a VCalendar
	 * 
	 * @param vCalendar
	 *            DOCUMENT ME!
	 * 
	 * @throws EJBException
	 *             DOCUMENT ME!
	 * 
	 * @ejb.interface-method view-type="both"
	 * @ejb.permission role-name="calendaruser"
	 */
	public void publishCalendar(net.fortuna.ical4j.model.Calendar vCalendar)
			throws EJBException {
		this.log.debug("Calendar parsed" + vCalendar.toString());

		Iterator compIterator = vCalendar.getComponents().iterator();
		HashMap veventsByUID = new HashMap();
		HashMap vtodoByUID = new HashMap();

		while (compIterator.hasNext()) {
			Component component = (Component) compIterator.next();

			if (component instanceof VEvent) {
				VEvent vevent = (VEvent) component;
				Uid uid = (Uid) vevent.getProperties()
						.getProperty(Property.UID);
				this.log.debug("Parsed VEvent" + uid.getValue());
				veventsByUID.put(uid.getValue(), component);
			}

			if (component instanceof VToDo) {
				VToDo vtodo = (VToDo) component;
				Uid uid = (Uid) vtodo.getProperties().getProperty(Property.UID);
				this.log.debug("Parsed VTodo" + uid.getValue());
				vtodoByUID.put(uid.getValue(), component);
			}
		}

		loadVEvents(veventsByUID);
		loadVTodos(vtodoByUID);
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param vtodoByUID
	 */
	private void loadVTodos(HashMap vtodoByUID) {
		Date today = null;
		java.util.Calendar cal = java.util.Calendar.getInstance();
		cal.set(java.util.Calendar.HOUR_OF_DAY, 12);
		cal.set(java.util.Calendar.MINUTE, 0);
		cal.set(java.util.Calendar.SECOND, 0);
		cal.set(java.util.Calendar.MILLISECOND, 0);
		today = cal.getTime();
		// if there are tasks load them
		if (vtodoByUID.keySet().size() > 0) {
			String[] uids = (String[]) vtodoByUID.keySet().toArray(
					new String[0]);

			try {
				String username = this.sessionContext.getCallerPrincipal()
						.getName();
				UserTaskManagerLocal taskManager = UserTaskManagerUtil
						.getLocalHome().create();
				Task[] tasks = (Task[]) taskManager.listTasks(username, uids);

				for (int i = 0; i < tasks.length; i++) {
					Task task = tasks[i];
					VToDo vtodo = (VToDo) vtodoByUID.get(task.getGUID());
					vtodoByUID.remove(task.getGUID());

					try {
						if (isTaskNeedUpdate(vtodo, task)) {
							taskManager.updateTasks(username,
									new Task[] { task });
						}
					} catch (UserUnknownException e) {
						this.log.error("Failed to update Task with Task:"
								+ vtodo.toString(), e);
					}
				}

				// do all imports
				Iterator imports = vtodoByUID.keySet().iterator();

				while (imports.hasNext()) {
					String eventUid = (String) imports.next();
					VToDo vtodo = (VToDo) vtodoByUID.get(eventUid);
					Task calTask = parseToDo(vtodo);

					if (calTask.getDueDate() == null)
						calTask.setDueDate(today);
					try {
						taskManager.createTasks(username,
								new Task[] { calTask });
						this.log.debug("import new Tasks:" + eventUid);
					} catch (Exception e) {
						this.log.error("import new Tasks failed!", e);
					}
				}
			} catch (UserUnknownException e) {
				this.log.error("Task Import failed", e);
			} catch (CreateException e) {
				this.log.error("Task Import failed", e);
			} catch (NamingException e) {
				this.log.error("Task Import failed", e);
			}
		}
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param veventsByUID
	 */
	private void loadVEvents(HashMap veventsByUID) {
		// if there are events load them
		if (veventsByUID.keySet().size() > 0) {
			String[] uids = (String[]) veventsByUID.keySet().toArray(
					new String[0]);

			try {
				String username = this.sessionContext.getCallerPrincipal()
						.getName();
				ScheduleManagerLocal scheduleManager = ScheduleManagerUtil
						.getLocalHome().create();
				Invite[] invites = scheduleManager.listInvites(uids, username);

				// do all updates
				for (int i = 0; i < invites.length; i++) {
					Invite oldInvite = invites[i];
					VEvent vevent = (VEvent) veventsByUID.get(oldInvite
							.getEvent().getGUID());
					veventsByUID.remove(oldInvite.getEvent().getGUID());

					try {
						if (isInviteNeedUpdate(vevent, oldInvite)) {
							scheduleManager.updateInvite(oldInvite, username);
						}
					} catch (RecordNotFoundException e) {
						this.log.error("Failed to update invite with Event:"
								+ vevent.toString(), e);
					} catch (InvalidCalendarEventException e) {
						this.log.error("Failed to update invite with Event:"
								+ vevent.toString(), e);
					} catch (UserNotAuthorizedException e) {
						this.log.error("Failed to update invite with Event:"
								+ vevent.toString(), e);
					}

					try {
						if (isEventNeedUpdate(vevent, oldInvite.getEvent())) {
							scheduleManager.updateEvent(oldInvite.getEvent(),
									username);
						}
					} catch (RecordNotFoundException e) {
						this.log.error("Failed to update Event with Event:"
								+ vevent.toString(), e);
					} catch (InvalidCalendarEventException e) {
						this.log.error("Failed to update Event with Event:"
								+ vevent.toString(), e);
					} catch (UserNotAuthorizedException e) {
						this.log.error("Failed to update Event with Event:"
								+ vevent.toString(), e);
					}
				}

				// do all imports
				Iterator imports = veventsByUID.keySet().iterator();

				while (imports.hasNext()) {
					String eventUid = (String) imports.next();
					VEvent event = (VEvent) veventsByUID.get(eventUid);
					CalendarEvent calEvent = parseEvent(event);

					try {
						scheduleManager.scheduleEvent(
								new String[] { username }, calEvent, username);
						this.log.debug("import new Event:" + eventUid);
					} catch (Exception e) {
						this.log.error("import new Event failed!" + event, e);
					}
				}
			} catch (UserUnknownException e) {
				this.log.error("Calendar Import failed", e);
			} catch (CreateException e) {
				this.log.error("Calendar Import failed", e);
			} catch (NamingException e) {
				this.log.error("Calendar Import failed", e);
			}
		}
	}

	protected boolean isInviteNeedUpdate(VEvent event, Invite invite) {
		this.log.debug("Maybe should update Invite :" + invite.getInviteId());

		int statusCode = invite.getStatus();
		int newStatusCode = this.parseStatusCode((Status) event.getProperties()
				.getProperty(Property.STATUS), statusCode);

		if (statusCode != newStatusCode) {
			invite.setStatus(newStatusCode);
			this.log.debug("Do update to " + invite);

			return true;
		}

		this.log.debug("No update needed");

		return false;
	}

	protected boolean isTaskNeedUpdate(VToDo vtodo, Task task) {
		boolean needUpdate = false;
		this.log.debug("Maybe should update Task :" + task.getTaskId());

		Task newTask = parseToDo(vtodo);

		if (newTask.getVersion() == null) {
			newTask.setVersion(newTask.getVersion());
		} else if (newTask.getVersion().longValue() > newTask.getVersion()
				.longValue()) {
			this.log
					.debug("Old Version of records, client needs to update record first");
			this.log.debug("No update needed");

			return false;
		}

		if ((newTask.getTitle() != null)
				&& (!newTask.getTitle().equals(task.getTitle()))) {
			task.setTitle(newTask.getTitle());
			needUpdate = true;
		}

		if ((newTask.getNote() != null)
				&& (!newTask.getNote().equals(task.getNote()))) {
			task.setNote(newTask.getNote());
			needUpdate = true;
		}

		this.log.debug("Update" + (needUpdate ? "" : " NOT") + " needed");

		return needUpdate;
	}

	protected boolean isEventNeedUpdate(VEvent event, CalendarEvent calEvent) {
		boolean needUpdate = false;
		this.log.debug("Maybe should update Event :" + calEvent.getRecordId());

		CalendarEvent newEvent = parseEvent(event);

		if (newEvent.getVersion() == null) {
			newEvent.setVersion(calEvent.getVersion());
		} else if (calEvent.getVersion().longValue() > newEvent.getVersion()
				.longValue()) {
			this.log
					.debug("Old Version of records, client needs to update record first");
			this.log.debug("No update needed");

			return false;
		}

		if ((newEvent.getTitle() != null)
				&& (!newEvent.getTitle().equals(calEvent.getTitle()))) {
			calEvent.setTitle(newEvent.getTitle());
			needUpdate = true;
		}

		if ((newEvent.getNote() != null)
				&& (!newEvent.getNote().equals(calEvent.getNote()))) {
			calEvent.setNote(newEvent.getNote());
			needUpdate = true;
		}

		if ((newEvent.getLocation() != null)
				&& (!newEvent.getLocation().equals(calEvent.getLocation()))) {
			calEvent.setLocation(newEvent.getLocation());
			needUpdate = true;
		}

		if ((newEvent.getStartDate() != null)
				&& (!newEvent.getStartDate().equals(calEvent.getStartDate()))) {
			calEvent.setStartDate(newEvent.getStartDate());
			needUpdate = true;
		}

		if ((newEvent.getEndDate() != null)
				&& (!newEvent.getEndDate().equals(calEvent.getEndDate()))) {
			calEvent.setEndDate(newEvent.getEndDate());
			needUpdate = true;
		}

		if ((newEvent.getVersion() != null)
				&& (!newEvent.getVersion().equals(calEvent.getVersion()))) {
			calEvent.setVersion(newEvent.getVersion());
			needUpdate = true;
		}

		this.log.debug("Update" + (needUpdate ? "" : " NOT") + " needed");

		return needUpdate;
	}

	private int parseStatusCode(Status code, int currentCode) {
		if (code.getValue().equals(Status.VEVENT_TENTATIVE)) {
			return InviteStatus.TENATIVE;
		}

		if (code.getValue().equals(Status.VEVENT_CONFIRMED)) {
			return InviteStatus.ACCEPTED;
		}

		// NOTE This should be improved
		// Based on invite context the logic should work this:
		// if the invite is the Event Organizer set it to CANCELLED
		// if the invite is NOT the Event Organizer set it to DECLINED
		if (code.getValue().equals(Status.VEVENT_CANCELLED)) {
			return ((currentCode == InviteStatus.CANCELED) ? InviteStatus.CANCELED
					: InviteStatus.DECLINED);
		}

		// leave it alone
		return currentCode;
	}

	private CalendarEvent parseEvent(VEvent event) {
		CalendarEvent calevent = new CalendarEvent();

		Uid guid = (Uid) event.getProperties().getProperty(Property.UID);

		if (guid != null) {
			calevent.setGUID(guid.getValue());
		}

		Description desc = (Description) event.getProperties().getProperty(
				Property.DESCRIPTION);

		if (desc != null) {
			calevent.setNote(desc.getValue());
		}

		Location loc = (Location) event.getProperties().getProperty(
				Property.LOCATION);

		if (loc != null) {
			calevent.setLocation(loc.getValue());
		}

		Summary title = (Summary) event.getProperties().getProperty(
				Property.SUMMARY);

		if (title != null) {
			calevent.setTitle(title.getValue());
		}

		DtStart startDate = (DtStart) event.getProperties().getProperty(
				Property.DTSTART);

		if (startDate != null) {
			calevent.setStartDate(startDate.getDate());
		}

		DtEnd endDate = (DtEnd) event.getProperties().getProperty(
				Property.DTEND);

		if (endDate != null) {
			calevent.setEndDate(endDate.getDate());
		}

		Sequence version = (Sequence) event.getProperties().getProperty(
				Property.SEQUENCE);

		if (version != null) {
			calevent.setVersion(new Integer(Integer
					.parseInt(version.getValue())));
		}

		return calevent;
	}

	private Task parseToDo(VToDo vtask) {
		Task task = new Task();

		Uid guid = (Uid) vtask.getProperties().getProperty(Property.UID);

		if (guid != null) {
			task.setGUID(guid.getValue());
		}

		Description desc = (Description) vtask.getProperties().getProperty(
				Property.DESCRIPTION);

		if (desc != null) {
			task.setNote(desc.getValue());
		}

		Summary title = (Summary) vtask.getProperties().getProperty(
				Property.SUMMARY);

		if (title != null) {
			task.setTitle(title.getValue());
		}

		Due due = (Due) vtask.getProperties().getProperty(Property.DUE);

		if (due != null) {
			task.setDueDate(due.getDate());
		}

		Sequence version = (Sequence) vtask.getProperties().getProperty(
				Property.SEQUENCE);

		if (version != null) {
			task.setVersion(new Integer(Integer.parseInt(version.getValue())));
		}

		Status status = (Status) vtask.getProperties().getProperty(
				Property.STATUS);

		if (title != null) {
			if (status.getValue().toUpperCase().equals(
					Status.VTODO_NEEDS_ACTION)) {
				task.setStatus(TaskSortKeys.STATUS_CODE_OPEN);
			}

			if (status.getValue().toUpperCase().equals(Status.VTODO_IN_PROCESS)) {
				task.setStatus(TaskSortKeys.STATUS_CODE_OPEN);
			}

			if (status.getValue().toUpperCase().equals(Status.VTODO_CANCELLED)) {
				task.setStatus(TaskSortKeys.STATUS_CODE_CANCELLED);
			}

			if (status.getValue().toUpperCase().equals(Status.VTODO_COMPLETED)) {
				task.setStatus(TaskSortKeys.STATUS_CODE_COMPLETED);
			}
		}

		Priority priority = (Priority) vtask.getProperties().getProperty(
				Property.PRIORITY);

		if (priority != null) {
			int level = priority.getLevel();
			while (level > 5)
				level = level / 5;
			task.setPriority(level);
		}

		return task;
	}
}
