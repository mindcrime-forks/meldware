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

import org.buni.meldware.calendar.data.Address;
import org.buni.meldware.calendar.data.ContactSortKeys;
import org.buni.meldware.calendar.session.exception.UserUnknownException;


/**
 * DOCUMENT ME!
 * 
 * @author $Author: andy $
 * @version $Revision: 1.2 $
 */
public class UserProfileTest extends AbstractTestCase {
	/**
	 * Constructor for UserProfileBeanTest.
	 * 
	 * @param arg0
	 */
	public UserProfileTest(String arg0) {
		super(arg0);
	}

	/**
	 * DOCUMENT ME!
	 */
	public void testLoadUserAddress() {
		System.out.println("** Load user address tom");

        System.err.println("** disabled"); /*

		try {
			PIMService pim = PIMServiceUtil.getHome(
					getContext("tom", "tom")).create();
			Address address = pim.getUserAddress();
			assertTrue("NickName was:" + address.getNickName(), address
					.getNickName().equals("tom"));
			pim.remove();
		} catch (UserAddressUnknownException e) {
			fail("testLoadUserAddress failed becuse user's " + e.getParams()[0]
					+ " address not found", e);
		} catch (Exception e) {
			fail("testDeleteUser", e);
		}
        */
	}

	/**
	 * DOCUMENT ME!
	 */
	public void testLoadUserPreference() {
		System.out.println("** Load user address tom");
        System.err.println("** disabled"); /*
		try {
			PIMService pim = PIMServiceUtil.getHome(
					getContext("tom", "tom")).create();
			UserProfile user = pim.loadCurrentUserProfile();
			assertTrue(" Testuser1 notification preference was:"
					+ user.getPreferenceAsInt(PreferenceConstants.CALENDAR_NOTIFICATION), 
                    user.getPreferenceAsInt(PreferenceConstants.CALENDAR_NOTIFICATION) == 1);
			pim.remove();
			pim = PIMServiceUtil.getHome(getContext("jerry", "jerry"))
					.create();
			user = pim.loadCurrentUserProfile();
			assertTrue(" Testuser2 notification preference was:"
					+ user.getPreferenceAsInt(PreferenceConstants.CALENDAR_NOTIFICATION), user
                    .getPreferenceAsInt(PreferenceConstants.CALENDAR_NOTIFICATION) == 2);
			pim.remove();
		} catch (Exception e) {
			fail("testLoadUserPreference", e);
		}
        */
	}

	/**
	 * Test friendship by adding to tom, friend jerry. Watch if it
	 * will blow up removal tests later on.
	 */
	public void testFriendship() {
		System.out.println("** testFriendship");

		try {
			PIMService pim = PIMServiceUtil.getHome(
					getContext("tom", "tom")).create();
			pim.addFriends(new String[] { "jerry" });
			int fcount = pim.listFriends().length;
			assertTrue("Number of friends is :" + fcount, fcount == 1);
			pim.remove();
		} catch (UserUnknownException e) {
			fail("testFriendship failed because user " + e.getParams()[0]
					+ " not found", e);
		} catch (Exception e) {
			fail("testFriendship", e);
		}
	}

	/**
	 * Test listing contacts by alfa
	 */
	public void testListContactsByAlfa() {
		System.out.println("** testListContactsByAlfa");
        System.err.println("** disabled"); /*
		try {
			PIMService pim = PIMServiceUtil.getHome(
					getContext("tom", "tom")).create();
			Address[] addresses = pim.listAddresses("a", "h",
					ContactSortKeys.SORT_BY_FULLNAME);
			System.out.println("Listing A-H order by fullName size="
					+ addresses.length);
			for (int i = 0; i < addresses.length; i++) {
				System.out.println("Contact fullName: "
						+ addresses[i].getFullName());
			}
			assertTrue("Expected 10 got: " + addresses.length,
					addresses.length == 10);
			addresses = pim.listAddresses("a", "E",
					ContactSortKeys.SORT_BY_ORGANIZATION_FULLNAME);
			System.out.println("Listing A-E order by organization size="
					+ addresses.length);
			for (int i = 0; i < addresses.length; i++) {
				System.out.println("Contact org:"
						+ addresses[i].getOrganization() + "Contact fullName: "
						+ addresses[i].getFullName());
			}
			assertTrue("Expected 16 got: " + addresses.length,
					addresses.length == 16);
			pim.remove();
		} catch (UserUnknownException e) {
			fail("testFriendship failed because user " + e.getParams()[0]
					+ " not found", e);
		} catch (Exception e) {
			fail("testFriendship", e);
		}
        */
	}

	/**
	 * This the add hoc signup capability. For linkaway. Signup new users as
	 * friends, for testuser. The assumed usecase: - Company A have a common
	 * user repository. - In a remote system tom signed up but
	 * newguy1,newguy2,nuwguy3 are in the repository but never been provisioned
	 * in this system. - when a remote systems wants to schedule a meeting for
	 * tom,newguy1,... , the system has to be able to provision their
	 * accounts. - since the login comes from the common repository the new
	 * users will be able to log in.
	 */
	public void testLinkSignUP() {
		System.out.println("** testLinkSignUP");
        
        //ACO test disabled
/*
		try {
			PIMService pim = PIMServiceUtil.getHome(
					getContext("tom", "tom")).create();
			pim.signupUser("newguy1",
					PreferenceConstants.CALENDAR_NOTIFICATION_EMAIL,
					"newguy1@test.com");
			pim.signupUser("newguy2",
					PreferenceConstants.CALENDAR_NOTIFICATION_EMAIL,
					"newguy2@test.com");
			pim.signupUser("newguy3",
					PreferenceConstants.CALENDAR_NOTIFICATION_NONE,
					"newguy3@test.com");
			pim.remove();
		} catch (Exception e) {
			fail("testFriendship", e);
		}
        */
	}

	/**
	 * Test Searching current contacts for tom
	 */
	public void testCurrentContactSearch() {
		System.out.println("** testCurrentContactSearch");

		try {
			PIMService pim = PIMServiceUtil.getHome(
					getContext("tom", "tom")).create();
			Address[] friends = pim.searchContacts("dude*",
					ContactSortKeys.SORT_BY_FULLNAME);
			for (int i = 0; i < friends.length; i++) {
				System.out.println("BY FULLNAME- Found contact: "
						+ friends[i].getFullName());
			}
			assertTrue("FULLNAME Expected result 3 got: " + friends.length,
					friends.length == 3);
			friends = pim.searchContacts("ude*",
					ContactSortKeys.SORT_BY_NICKNAME);
			for (int i = 0; i < friends.length; i++) {
				System.out.println("BY NICKNAME- Found contact: "
						+ friends[i].getNickName());
			}
			assertTrue("NICKNAME Expected result 1 got: " + friends.length,
					friends.length == 1);
			friends = pim.searchContacts("FDUDE CLUB",
					ContactSortKeys.SORT_BY_ORGANIZATION_FULLNAME);
			for (int i = 0; i < friends.length; i++) {
				System.out.println("BY ORGANIZATION- Found contact: "
						+ friends[i].getOrganization());
			}
			assertTrue("ORGANIZATION Expected result 3 got: " + friends.length,
					friends.length == 3);
			pim.remove();
		} catch (Exception e) {
			fail("testCurrentContactSearch", e);
		}
	}

	/**
	 * Test friendship by adding to jerry, friend tom. Watch if it
	 * will blow up removal tests later on.
	 */
	public void testFriendSearch() {
		System.out.println("** testFriendSearch");
        System.err.println("** disabled"); /*
		try {
			PIMService pim = PIMServiceUtil.getHome(
					getContext("jerry", "jerry")).create();
			Address[] friends = pim.searchNewContacts("jerry");
			assertTrue("More than one test friend found: " + friends.length,
					friends.length == 1);
			pim.addFriends(new String[] { friends[0].getOwner().getUserName() });
			int fcount = pim.listFriends().length;
			assertTrue("Number of friends is: " + fcount, fcount == 1);
			pim.remove();
		} catch (UserUnknownException e) {
			fail("testFriendship failed because user " + e.getParams()[0]
					+ " not found", e);
		} catch (Exception e) {
			fail("testFriendship", e);
		}
        */
	}

	/**
	 * Remove tom as jerry's friend. Check if jerry still have
	 * it's address.
	 */
	public void testFriendRemove() {
		System.out.println("** testFriendRemove");
        System.err.println("** disabled"); /*
		try {
			PIMService pim = PIMServiceUtil.getHome(
					getContext("tom", "tom")).create();
			int fcount = pim.listFriends().length;
			assertTrue("Number of friends is :" + fcount, fcount == 1);
			Address[] address = pim.listAddresses(0, 100,
					CursorDirection.FORWARD);
			Address friend = null;
			for (int i = 0; i < address.length; i++) {
				if (address[i].getOwner().getUserName().equals("jerry"))
					friend = address[i];
			}
			assertTrue("No friend found!", friend != null);
			pim
					.removeAddresses(new long[] { friend.getRecordId()
							.longValue() });
			fcount = pim.listFriends().length;
			assertTrue("Number of friends is :" + fcount, fcount == 0);
			pim.remove();
			pim = PIMServiceUtil.getHome(getContext("jerry", "jerry"))
					.create();
			assertTrue("User address is lost", pim.getUserAddress() != null);
			pim.remove();
		} catch (UserUnknownException e) {
			fail("testFriendship failed because user " + e.getParams()[0]
					+ " not found", e);
		} catch (Exception e) {
			fail("testFriendship", e);
		}
        */
	}

	/**
	 * DOCUMENT ME!
	 */
	public void testDeleteUser() {
		System.out.println("** Delete tom");
//ACO test disabled
	/*	try {
			PIMService pim = PIMServiceUtil.getHome(
					getContext("tom", "tom")).create();
			pim.removeUser();
			pim.remove();
		} catch (UserUnknownException e) {
			fail("testDeleteUser failed becuse user " + e.getParams()[0]
					+ " not found", e);
		} catch (Exception e) {
			fail("testDeleteUser", e);
		}*/
	}

	/**
	 * DOCUMENT ME!
	 */
	public void testDeleteUserWithAddresses() {
		System.out.println("** Delete jerry");
        //ACO test disabled
        /*

		try {
			PIMService pim = PIMServiceUtil.getHome(
					getContext("jerry", "jerry")).create();
			pim.removeUser();
			pim.remove();
		} catch (Exception e) {
			fail("testDeleteUserWithAddresses", e);
		}
        */
	}

	public void testDeleteALLLeft() {
        //ACO test disabled
        /*
		try {
			PIMService pim = PIMServiceUtil.getHome(
					getContext("admin", "admin")).create();
			pim.removeUser("newguy1");
			pim.removeUser("newguy2");
			pim.removeUser("newguy3");
			pim.remove();
		} catch (Exception e) {
			fail("testDeleteUserWithAddresses", e);
		}
        */
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
