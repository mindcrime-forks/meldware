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

import org.buni.meldware.calendar.data.Task;
import org.buni.meldware.calendar.data.TaskSortKeys;
import org.buni.meldware.calendar.session.exception.UserTaskUnknownException;
import org.buni.meldware.calendar.session.exception.UserUnknownException;
import org.buni.meldware.calendar.util.HibernateLookUp;
import org.buni.meldware.common.preferences.UserProfile;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

/**
 * Service to manage user's tasks
 * 
 * @ejb.bean name="UserTaskManager" description="Service to manage user's tasks"
 *           jndi-name="ejb/session/UserTaskManager" type="Stateless"
 * @ejb.transaction type="Required"
 * @ejb.transaction-type type="Container"
 * @ejb.util generate="physical"
 * @ejb.security-role-ref role-name="calendaruser" role-link="calendaruser"
 * @ejb.security-role-ref role-name="calendaradmin" role-link="calendaradmin"
 * @ejb.permission unchecked="true"
 */
public class UserTaskManagerBean extends Service implements SessionBean {
	static final long serialVersionUID = "$Id: UserTaskManagerBean.java,v 1.4 2007/12/30 01:58:40 andy Exp $".hashCode(); //$NON-NLS-1$

	/**
	 * Create Task for user.
	 * 
	 * @param userName
	 *            DOCUMENT ME!
	 * @param task
	 *            DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 * 
	 * @throws EJBException
	 *             DOCUMENT ME!
	 * @throws UserUnknownException
	 *             DOCUMENT ME!
	 * 
	 * @ejb.interface-method view-type="local"
	 * @ejb.permission role-name="calendaruser,calendaradmin"
	 */
	public Task[] createTasks(String userName, Task[] task)
			throws EJBException, UserUnknownException {
		Session session = null;

		try {
			session = HibernateLookUp.getSessionFactory().openSession();

			this.log.debug("Creating tasks for user:" + userName);

			UserProfile user = getUser(userName, session);

			for (int i = 0; i < task.length; i++) {
				this.log.debug("Creating task:" + task[i].getTitle());
				task[i].setUserName(user.getUsername());
				task[i].setCreateDate(new Date());

				if ((task[i].getGUID() == null)
						|| task[i].getGUID().trim().equals("")) {
					task[i].setGUID(createGUID());
				}
				if (!TaskSortKeys.isValidPriority(task[i].getPriority()))
					task[i].setPriority(TaskSortKeys.PRIORTIY_CODE_MEDIUM);
				if (!TaskSortKeys.isValidStatus(task[i].getStatus()))
					task[i].setStatus(TaskSortKeys.STATUS_CODE_OPEN);

				session.save(task[i]);
				Set<Task> tasks = getTasks(user.getUsername());
			}

			session.flush();

			return task;
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
	 * Update Tasks for user.
	 * 
	 * @param userName
	 *            DOCUMENT ME!
	 * @param task
	 *            DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 * 
	 * @throws EJBException
	 *             DOCUMENT ME!
	 * @throws UserUnknownException
	 *             DOCUMENT ME!
	 * 
	 * @ejb.interface-method view-type="local"
	 * @ejb.permission role-name="calendaruser,calendaradmin"
	 */
	public Task[] updateTasks(String userName, Task[] task)
			throws EJBException, UserUnknownException {
		Session session = null;

		try {
			session = HibernateLookUp.getSessionFactory().openSession();

			this.log.debug("Update task for user:" + userName);

			UserProfile user = getUser(userName, session);

			for (int i = 0; i < task.length; i++) {
				replaceTask(getTasks(user.getUsername()), task[i]);
				this.log.debug("Update task:" + task[i].getTitle());
			}

			session.flush();

			return task;
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

	private Set<Task> getTasks(String username) {
        Session session = HibernateLookUp.getSessionFactory().openSession();
        Query q = session.createQuery("from Task where task.userName = :username").setParameter("username", username);
        Set<Task> retval = new HashSet<Task>();
        retval.addAll(q.list());
        return retval;
    }

    /**
	 * Remove Tasks for user.
	 * 
	 * @param userName
	 *            DOCUMENT ME!
	 * @param taskids
	 *            DOCUMENT ME!
	 * 
	 * @throws EJBException
	 *             DOCUMENT ME!
	 * @throws UserUnknownException
	 *             DOCUMENT ME!
	 * 
	 * @ejb.interface-method view-type="local"
	 * @ejb.permission role-name="calendaruser,calendaradmin"
	 */
	public void removeTasks(String userName, long[] taskids)
			throws EJBException, UserUnknownException {
		Session session = null;

		try {
			session = HibernateLookUp.getSessionFactory().openSession();

			this.log.debug("Remove task for user:" + userName);

			UserProfile user = getUser(userName, session);

			for (int i = 0; i < taskids.length; i++) {
				Query query = session.createFilter(getTasks(user.getUsername()),
						"where this.taskId = :taskId");
				query.setLong("taskId", taskids[i]);

				List result = query.list();
				Iterator iterator = result.iterator();

				while (iterator.hasNext()) {
					Task task = (Task) iterator.next();
					this.log.debug("Remove task:" + task.getTitle());
					session.delete(task);
				}

				session.flush();
			}
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
	 * List Tasks for user.
	 * 
	 * @param userName
	 *            DOCUMENT ME!
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
	 * @ejb.interface-method view-type="local"
	 * @ejb.permission role-name="calendaruser,calendaradmin"
	 */
	public Task[] listTasks(String userName, String[] guids)
			throws EJBException, UserUnknownException {
		Session session = null;
		this.log.debug("start list tasks");

		try {
			session = HibernateLookUp.getSessionFactory().openSession();

			UserProfile owner = getUser(userName, session);
			String filterString = "where this.GUID in (";

			for (int g = 0; g < guids.length; g++) {
				filterString += (((g > 0) ? "," : "") + "\'" + guids[g] + "\'");
			}

			filterString += ")";

			Query query = session.createFilter(getTasks(owner.getUsername()), filterString);

			return (Task[]) query.list().toArray(new Task[0]);
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
	 * List Tasks for user.
	 * 
	 * @param userName
	 *            DOCUMENT ME!
	 * @param priorityFilter
	 *            DOCUMENT ME!
	 * @param statusFilter
	 *            DOCUMENT ME!
	 * @param categoryFilter
	 *            DOCUMENT ME!
	 * @param dueDateStart
	 *            DOCUMENT ME!
	 * @param dueDateEnd
	 *            DOCUMENT ME!
	 * @param orderKey
	 *            DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 * 
	 * @throws EJBException
	 *             DOCUMENT ME!
	 * @throws UserUnknownException
	 *             DOCUMENT ME!
	 * 
	 * @ejb.interface-method view-type="local"
	 * @ejb.permission role-name="calendaruser,calendaradmin"
	 */
	public Task[] listTasks(String userName, int[] priorityFilter,
			int[] statusFilter, String[] categoryFilter, Date dueDateStart,
			Date dueDateEnd, int orderKey) throws EJBException,
			UserUnknownException {
		String queryString = "where ";

		if (dueDateStart != null) {
			queryString += "this.dueDate >= :dueDateStart ";
		}

		if (dueDateEnd != null) {
			queryString += ((dueDateStart != null) ? "and " : "");
			queryString += "this.dueDate <= :dueDateEnd ";
		}

		if (priorityFilter != null) {
			queryString += createHSQL_IN_String("priority",
					priorityFilter.length);
		}

		if (statusFilter != null) {
			queryString += createHSQL_IN_String("status", statusFilter.length);
		}

		if (categoryFilter != null) {
			queryString += createHSQL_IN_String("category",
					categoryFilter.length);
		}

		queryString += (" order by " + getSortKey(orderKey) + " asc");

		Session session = null;

		try {
			session = HibernateLookUp.getSessionFactory().openSession();

			this.log.debug("List task for user:" + userName);

			// get user
			UserProfile user = getUser(userName, session);

			// find tasks
			Query query = session.createFilter(getTasks(user.getUsername()), queryString);

			if (dueDateStart != null) {
				query.setDate("dueDateStart", dueDateStart);
			}

			if (dueDateEnd != null) {
				query.setDate("dueDateEnd", dueDateEnd);
			}

			if (priorityFilter != null) {
				bindHSQL_IN_clause(query, "priority", priorityFilter);
			}

			if (statusFilter != null) {
				bindHSQL_IN_clause(query, "status", statusFilter);
			}

			if (categoryFilter != null) {
				bindHSQL_IN_clause(query, "category", categoryFilter);
			}

			List list = query.list();

			return (Task[]) list.toArray(new Task[] {});
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
	 * Retrieve Task.
	 * 
	 * @param userName
	 *            DOCUMENT ME!
	 * @param taskId
	 *            DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 * 
	 * @throws UserUnknownException
	 *             DOCUMENT ME!
	 * @throws UserTaskUnknownException
	 *             DOCUMENT ME!
	 * @throws EJBException
	 *             DOCUMENT ME!
	 * 
	 * @ejb.interface-method view-type="local"
	 * @ejb.permission role-name="calendaruser,calendaradmin"
	 */
	public Task getTask(String userName, long taskId)
			throws UserUnknownException, UserTaskUnknownException {
		Session session = null;
		Task task = null;

		try {
			session = HibernateLookUp.getSessionFactory().openSession();

			this.log.debug("Load task for user:" + userName);

			UserProfile user = getUser(userName, session);

			Query query = session.createFilter(getTasks(user.getUsername()),
					"where this.taskId = :taskId");
			query.setLong("taskId", taskId);

			List result = query.list();
			Iterator iterator = result.iterator();

			if (iterator.hasNext()) {
				task = (Task) iterator.next();
			} else {
				throw new UserTaskUnknownException(taskId);
			}
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

		return task;
	}

	protected void replaceTask(Set tasks, Task task) {
		Iterator taskIterator = tasks.iterator();

		while (taskIterator.hasNext()) {
			Task oldtask = (Task) taskIterator.next();

			if (oldtask.getTaskId().longValue() == task.getTaskId().longValue()) {
				oldtask.setNote(task.getNote());
				if (TaskSortKeys.isValidPriority(task.getPriority()))
					oldtask.setPriority(task.getPriority());
				if (TaskSortKeys.isValidStatus(task.getStatus()))
					oldtask.setStatus(task.getStatus());
				oldtask.setTitle(task.getTitle());
				oldtask.setDueDate(task.getDueDate());
			}
		}
	}

	protected String getSortKey(int key) {
		switch (key) {
		case TaskSortKeys.SORT_BY_PRORITY_STATUS_TITLE:
			return "priority, status, title ";

		case TaskSortKeys.SORT_BY_TITLE_PRORITY_STATUS:
			return "title, priority, status ";

		case TaskSortKeys.SORT_BY_STATUS_PRORITY_TITLE:
			return "status, priority, title ";

		default:
			return "";
		}
	}
}
