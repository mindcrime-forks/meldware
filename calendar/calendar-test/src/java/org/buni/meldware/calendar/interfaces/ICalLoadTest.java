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

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Calendar;

/**
 * DOCUMENT ME!
 * 
 * @author $Author: andy $
 * @version $Revision: 1.2 $
 */
public class ICalLoadTest extends AbstractTestCase {
	/**
	 * Constructor for BulkLoadTest.
	 */
	public ICalLoadTest() {
		super();
	}

	/**
	 * Constructor for BulkLoadTest.
	 * 
	 * @param arg0
	 */
	public ICalLoadTest(String arg0) {
		super(arg0);
	}

	/**
	 * DOCUMENT ME!
	 */
	public void testLoadCalendarData() {
		System.out.println("Load CalendarData");

		try {
			CalendarBuilder builder = new CalendarBuilder();
			Calendar vcal = builder.build(this.getClass().getResourceAsStream(
					"/TestCalendar.ics"));
			System.out.println("Load CalendarData");
			ICalSyncService iCalLoader = ICalSyncServiceUtil.getHome(
					getContext("tom", "tom")).create();
			iCalLoader.publishCalendar(vcal);
			iCalLoader.remove();
		} catch (Exception e) {
			fail("testLoadCalendarData", e);
		}
	}

	public void testReloadCalendarData() {
		System.out.println("Generate ICal file");

		try {
			CalendarOutputter outputer = new CalendarOutputter();
			System.out.println("Generate ICal file");
			ICalSyncService iCalLoader = ICalSyncServiceUtil.getHome(
					getContext("tom", "tom")).create();
			Calendar calendar = iCalLoader.getICalendar();
			iCalLoader.publishCalendar(calendar);
			iCalLoader.remove();
		} catch (Exception e) {
			fail("testLoadCalendarData", e);
		}
	}

	/**
	 * DOCUMENT ME!
	 */
	public void testGetCalendarData() {
		System.out.println("Generate ICal file");

		try {
			CalendarOutputter outputer = new CalendarOutputter();
			System.out.println("Generate ICal file");

			ICalSyncService iCalLoader = ICalSyncServiceUtil.getHome(
					getContext("tom", "tom")).create();
			Calendar calendar = iCalLoader.getICalendar();
			iCalLoader.remove();
			outputer.output(calendar, System.out);
		} catch (Exception e) {
			fail("testLoadCalendarData", e);
		}
	}
}
