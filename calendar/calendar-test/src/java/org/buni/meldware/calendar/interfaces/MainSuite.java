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

import java.net.URL;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * DOCUMENT ME!
 * 
 * @author $Author: andy $
 * @version $Revision: 1.2 $
 */
public class MainSuite extends TestSuite {
	/**
	 * Constructor for MainSuite.
	 */
	public MainSuite() {
		super();
	}

	/**
	 * Constructor for MainSuite.
	 * 
	 * @param arg0
	 * @param arg1
	 */
	public MainSuite(Class arg0, String arg1) {
		super(arg0, arg1);
	}

	/**
	 * Constructor for MainSuite.
	 * 
	 * @param arg0
	 */
	public MainSuite(Class arg0) {
		super(arg0);
	}

	/**
	 * Constructor for MainSuite.
	 * 
	 * @param arg0
	 */
	public MainSuite(String arg0) {
		super(arg0);
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public static Test suite() {
		try {
			String authFile = "";
			URL url = Runtime.getRuntime().getClass().getResource("/auth.conf");
			if (url != null)
				authFile = url.getFile();
			else
				authFile = "auth.conf";
			System.out.println("Auth file is:" + authFile);
			System.setProperty("java.security.auth.login.config", authFile);
			System.setProperty("java.naming.factory.initial",
					"org.jnp.interfaces.NamingContextFactory");
			System.setProperty("java.naming.provider.url",
					"jnp://localhost:1099");
		} catch (Throwable e) {
			System.err.println("JBOSS login exception");
			e.printStackTrace(System.err);
		}

		MainSuite suite = new MainSuite(
				"Test for org.buni.meldware.calendar.interfaces");

		// $JUnit-BEGIN$
	//	suite.addTest(new BulkLoadTest("testLoadUserData"));
		suite.addTest(new ScheduleManagerTest("testSchedule"));
		suite.addTest(new ScheduleManagerTest(
				"testScheduleMeetingMultipleInvite"));
		suite.addTest(new ScheduleManagerTest("testScheduleMeetingNullTest"));
		suite.addTest(new ScheduleManagerTest("testUnAuthorizedInviteChange"));
		suite.addTest(new ScheduleManagerTest("testUnAuthorizedEventChange"));
		suite.addTest(new ScheduleManagerTest("testGroupSchedule"));
		suite.addTest(new ScheduleManagerTest(
				"testScheduleMeetingMultipleInviteCancelAndRestart"));
		suite.addTest(new ScheduleManagerTest("testLoadInvitesByGUID"));
		suite.addTest(new UserProfileTest("testLoadUserPreference"));
		suite.addTest(new UserProfileTest("testLoadUserAddress"));
		suite.addTest(new UserProfileTest("testListContactsByAlfa"));
		suite.addTest(new UserProfileTest("testFriendship"));
		suite.addTest(new UserProfileTest("testCurrentContactSearch"));
		suite.addTest(new UserProfileTest("testFriendSearch"));
		suite.addTest(new UserProfileTest("testFriendRemove"));
		suite.addTest(new UserProfileTest("testLinkSignUP"));
		suite.addTest(new ICalLoadTest("testLoadCalendarData"));
		suite.addTest(new ICalLoadTest("testReloadCalendarData"));
		suite.addTest(new ICalLoadTest("testGetCalendarData"));
		suite.addTest(new UserProfileTest("testDeleteUser"));
		suite.addTest(new UserProfileTest("testDeleteUserWithAddresses"));
		suite.addTest(new UserProfileTest("testDeleteALLLeft"));

		// $JUnit-END$
		return suite;
	}
}
