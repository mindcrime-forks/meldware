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

import java.util.Date;

import org.buni.meldware.calendar.data.Task;
import org.buni.meldware.calendar.data.TaskSortKeys;


/**
 * DOCUMENT ME!
 * 
 * @author $Author: andy $
 * @version $Revision: 1.2 $
 */
public class TaskManagerTest extends AbstractTestCase {
	/**
	 * Constructor for UserProfileBeanTest.
	 * 
	 * @param arg0
	 */
	public TaskManagerTest(String arg0) {
		super(arg0);
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @throws Exception
	 *             DOCUMENT ME!
	 */
	public void testCreateTasks() throws Exception {
		System.out.println("** Create tasks for tom");
		try {
			Task task1 = new Task(null, null, "task1",
					TaskSortKeys.STATUS_CODE_OPEN,
					TaskSortKeys.PRIORTIY_CODE_HIGH, "Test", "Note, note",
					new Date(), new Date());
			Task task2 = new Task(null, null, "task2",
					TaskSortKeys.STATUS_CODE_OPEN,
					TaskSortKeys.PRIORTIY_CODE_HIGH, "Test", "Note, note",
					new Date(), new Date());
			Task task3 = new Task(null, null, "task3",
					TaskSortKeys.STATUS_CODE_OPEN,
					TaskSortKeys.PRIORTIY_CODE_HIGH, "Other", "Note, note",
					new Date(), new Date());
			PIMService service = PIMServiceUtil.getHome(
					getContext("tom", "tom")).create();
			service.addTask(new Task[] { task1, task2, task3 });
			service.remove();
		} catch (Exception e) {
			fail("testCreateTasks", e);
		}
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @throws Exception
	 *             DOCUMENT ME!
	 */
	public void testListTasks() throws Exception {
		System.out.println("** Create tasks for tom");
		try {
			PIMService service = PIMServiceUtil.getHome(
					getContext("tom", "tom")).create();
			// service.l
			service.remove();
		} catch (Exception e) {
			fail("testCreateTasks", e);
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
