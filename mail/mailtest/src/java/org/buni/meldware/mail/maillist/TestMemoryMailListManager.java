/*
 * Bunisoft the Open Source Communications Company
 * Copyright 2006, Bunisoft Inc.,
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
package org.buni.meldware.mail.maillist;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import junit.framework.TestCase;

import org.buni.meldware.mail.MBeanServerUtil;
import org.buni.meldware.mail.message.MailAddress;
import org.buni.meldware.mail.util.MMJMXUtil;

/**
 * @author <a href="kabirkhan@bigfoot.com">Kabir Khan</a>
 * @todo JAVADOC ME
 */
public class TestMemoryMailListManager extends TestCase {

    MailListManager manager;

    public TestMemoryMailListManager(String name) {
        super(name);
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(TestMemoryMailListManager.class);
    }

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        MBeanServerUtil.configureMBeanServerFactory();
        //MBeanServerUtil.registerMemoryListServ();
        MBeanServer mbserver = MMJMXUtil.locateJBoss();
        ObjectName oname = new ObjectName(
                "meldware.mail:type=MailServices,name=MailListManager");

        manager = (MailListManager) mbserver.invoke(oname,
                "produceServiceProxy", new Object[0], new String[0]);
    }

    public void atestBasicMembers() throws Exception {
        // Search for non-existent list
        MailAddress address = MailAddress
                .parseSMTPStyle("<nolist@localhost.localdomain>");
        assertNull("List " + address + " should be null", manager
                .findList(address));

        // /////////////////////////////////////////////////////
        // Get first list
        address = MailAddress.parseSMTPStyle("<alist@localhost.localdomain>");
        MailList listA = manager.findList(address);
        assertEquals("name of list should be same as passed in", address, listA
                .getListAddress());

        // /////////////////////////////////////////////////////
        // Do simple membership tests in first list
        address = MailAddress.parseSMTPStyle("1@abc.com");
        assertTrue(address + " should be a member", listA.isMember(address));

        address = MailAddress.parseSMTPStyle("2@def.com");
        assertTrue(address + " should be a member", listA.isMember(address));

        address = MailAddress.parseSMTPStyle("not_there@abc.com");
        assertTrue(address + " should not be a member", !listA
                .isMember(address));

        address = MailAddress.parseSMTPStyle("a@abc.com");
        assertTrue(address + " should not be a member", !listA
                .isMember(address));

        MailAddress[] addrA = listA.getMembers();
        assertTrue(listA.getListAddress() + " should have 2 members",
                addrA.length == 2);

        // /////////////////////////////////////////////////////
        // Get second list
        address = MailAddress.parseSMTPStyle("blist@localhost.localdomain");
        MailList listB = manager.findList(address);
        assertEquals("name of list should be same as passed in", address, listB
                .getListAddress());

        // Do simple membership tests in second list
        address = MailAddress.parseSMTPStyle("a@abc.com");
        assertTrue(address + " should be a member", listB.isMember(address));

        address = MailAddress.parseSMTPStyle("b@def.com");
        assertTrue(address + " should be a member", listB.isMember(address));

        address = MailAddress.parseSMTPStyle("c@ghi.com");
        assertTrue(address + " should be a member", listB.isMember(address));

        address = MailAddress.parseSMTPStyle("not_there@abc.com");
        assertTrue(address + " should not be a member", !listB
                .isMember(address));

        address = MailAddress.parseSMTPStyle("1@abc.com");
        assertTrue(address + " should not be a member", !listB
                .isMember(address));

        MailAddress[] addrB = listB.getMembers();
        assertTrue(listB.getListAddress() + " should have 3 members, had "
                + addrB.length, addrB.length == 3);
    }

    public void atestProperties() throws Exception {
        // Search for non-existent list
        MailAddress address = MailAddress
                .parseSMTPStyle("<alist@localhost.localdomain>");
        MailList listA = manager.findList(address);

        MailListProperties propsA = listA.getProperties();
        assertEquals(MailListPropertyConstants.REPLY_TO_LIST
                + " should be false", false, propsA.getPropertyBool(
                MailListPropertyConstants.REPLY_TO_LIST).booleanValue());

        assertEquals(MailListPropertyConstants.SUBJECT_PREFIX
                + "should be true", "Test List", propsA
                .getProperty(MailListPropertyConstants.SUBJECT_PREFIX));

        assertEquals(MailListPropertyConstants.PREFIX_AUTO_BRACKETED
                + "should be false", false, propsA.getPropertyBool(
                MailListPropertyConstants.PREFIX_AUTO_BRACKETED).booleanValue());

        assertEquals(MailListPropertyConstants.ATTACHMENT_ALLOWED
                + "should be true", true, propsA.getPropertyBool(
                MailListPropertyConstants.ATTACHMENT_ALLOWED).booleanValue());

        assertEquals(
                MailListPropertyConstants.MEMBERS_ONLY + "should be false",
                false, propsA.getPropertyBool(
                        MailListPropertyConstants.MEMBERS_ONLY).booleanValue());

        // /////////////////////////////////////////////////////
        // Get second list
        address = MailAddress.parseSMTPStyle("blist@localhost.localdomain");
        MailList listB = manager.findList(address);

        MailListProperties propsB = listB.getProperties();
        assertEquals(MailListPropertyConstants.REPLY_TO_LIST
                + " should be true", true, propsB.getPropertyBool(
                MailListPropertyConstants.REPLY_TO_LIST).booleanValue());

        assertNull(MailListPropertyConstants.SUBJECT_PREFIX + "should be null",
                propsB.getProperty(MailListPropertyConstants.SUBJECT_PREFIX));

        assertEquals(MailListPropertyConstants.PREFIX_AUTO_BRACKETED
                + "should be false", false, propsB.getPropertyBool(
                MailListPropertyConstants.ATTACHMENT_ALLOWED).booleanValue());

        assertEquals(MailListPropertyConstants.ATTACHMENT_ALLOWED
                + "should be false", false, propsB.getPropertyBool(
                MailListPropertyConstants.ATTACHMENT_ALLOWED).booleanValue());

        assertEquals(MailListPropertyConstants.MEMBERS_ONLY + "should be true",
                true, propsB.getPropertyBool(
                        MailListPropertyConstants.MEMBERS_ONLY).booleanValue());

    }

    public void atestCreateAndChange() throws Exception {
        MailAddress newListAddr = MailAddress
                .parseSMTPStyle("<newlist@localhost.localdomain>");
        assertNull("List " + newListAddr + " should not exist", manager
                .findList(newListAddr));

        MailListProperties props = new MailListProperties();
        props.setProperty(MailListPropertyConstants.REPLY_TO_LIST, new Boolean(
                true));
        props.setProperty(MailListPropertyConstants.SUBJECT_PREFIX, "Hello");
        props.setProperty(MailListPropertyConstants.PREFIX_AUTO_BRACKETED,
                new Boolean(true));
        props.setProperty(MailListPropertyConstants.ATTACHMENT_ALLOWED,
                new Boolean(true));
        props.setProperty(MailListPropertyConstants.MEMBERS_ONLY, new Boolean(
                true));

        boolean created = manager.createList(newListAddr, props);
        assertTrue("create should return true", created);

        created = manager.createList(newListAddr, props);
        assertFalse("create should return false, list already exists", created);

        MailList list = manager.findList(newListAddr);
        assertNotNull(newListAddr + " should exist", list);

        // Just check the properties, we've done this before in other tests, but
        // anyway..
        props = list.getProperties();
        assertEquals(MailListPropertyConstants.REPLY_TO_LIST
                + " should be true", true, props.getPropertyBool(
                MailListPropertyConstants.REPLY_TO_LIST).booleanValue());

        assertEquals(MailListPropertyConstants.SUBJECT_PREFIX
                + "should be null", "Hello", props
                .getProperty(MailListPropertyConstants.SUBJECT_PREFIX));

        assertEquals(MailListPropertyConstants.PREFIX_AUTO_BRACKETED
                + "should be true", true, props.getPropertyBool(
                MailListPropertyConstants.ATTACHMENT_ALLOWED).booleanValue());

        assertEquals(MailListPropertyConstants.ATTACHMENT_ALLOWED
                + "should be true", true, props.getPropertyBool(
                MailListPropertyConstants.ATTACHMENT_ALLOWED).booleanValue());

        assertEquals(MailListPropertyConstants.MEMBERS_ONLY + "should be true",
                true, props.getPropertyBool(
                        MailListPropertyConstants.MEMBERS_ONLY).booleanValue());

        // Change the properties, and check they get set ok
        props = new MailListProperties();
        props.setProperty(MailListPropertyConstants.REPLY_TO_LIST, new Boolean(
                false));
        props.setProperty(MailListPropertyConstants.SUBJECT_PREFIX, "What");
        props.setProperty(MailListPropertyConstants.PREFIX_AUTO_BRACKETED,
                new Boolean(false));
        props.setProperty(MailListPropertyConstants.ATTACHMENT_ALLOWED,
                new Boolean(false));
        props.setProperty(MailListPropertyConstants.MEMBERS_ONLY, new Boolean(
                false));

        list.setProperties(props);
        props = list.getProperties();
        assertEquals(MailListPropertyConstants.REPLY_TO_LIST
                + " should be false", false, props.getPropertyBool(
                MailListPropertyConstants.REPLY_TO_LIST).booleanValue());

        assertEquals(MailListPropertyConstants.SUBJECT_PREFIX
                + "should be null", "What", props
                .getProperty(MailListPropertyConstants.SUBJECT_PREFIX));

        assertEquals(MailListPropertyConstants.PREFIX_AUTO_BRACKETED
                + "should be false", false, props.getPropertyBool(
                MailListPropertyConstants.ATTACHMENT_ALLOWED).booleanValue());

        assertEquals(MailListPropertyConstants.ATTACHMENT_ALLOWED
                + "should be false", false, props.getPropertyBool(
                MailListPropertyConstants.ATTACHMENT_ALLOWED).booleanValue());

        assertEquals(
                MailListPropertyConstants.MEMBERS_ONLY + "should be false",
                false, props.getPropertyBool(
                        MailListPropertyConstants.MEMBERS_ONLY).booleanValue());

        MailAddress addr1 = MailAddress.parseSMTPStyle("1@test.com");
        MailAddress addr2 = MailAddress.parseSMTPStyle("2@test.com");
        MailAddress addr3 = MailAddress.parseSMTPStyle("3@test.com");
        MailAddress addr4 = MailAddress.parseSMTPStyle("4@test.com");

        list.addMember(addr1);
        list.addMember(addr2);
        list.addMember(addr3);
        list.addMember(addr3);
        assertEquals("member count should be 3", 3, list.getMembers().length);
        assertTrue(addr1 + " should be in list of members", list
                .isMember(addr1));
        assertTrue(addr2 + " should be in list of members", list
                .isMember(addr2));
        assertTrue(addr3 + " should be in list of members", list
                .isMember(addr3));
        assertFalse(addr4 + " should not be in list of members", list
                .isMember(addr4));

        list.addMember(addr4);
        assertTrue(addr4 + " should be in list of members", list
                .isMember(addr4));

        list.removeMember(addr2);
        list.removeMember(addr3);
        assertTrue(addr1 + " should be in list of members", list
                .isMember(addr1));
        assertFalse(addr2 + " should not be in list of members", list
                .isMember(addr2));
        assertFalse(addr3 + " should not be in list of members", list
                .isMember(addr3));
        assertTrue(addr4 + " should be in list of members", list
                .isMember(addr4));

        list.removeMember(addr1);
        list.removeMember(addr4);
        assertFalse(addr1 + " should not be in list of members", list
                .isMember(addr1));
        assertFalse(addr2 + " should not be in list of members", list
                .isMember(addr2));
        assertFalse(addr3 + " should not be in list of members", list
                .isMember(addr3));
        assertFalse(addr4 + " should not be in list of members", list
                .isMember(addr4));
    }

    public void atestDeletion() {
        MailAddress newListAddr = MailAddress
                .parseSMTPStyle("<somelist@localhost.localdomain>");
        MailListProperties props = new MailListProperties();
        manager.createList(newListAddr, props);

        manager.deleteList(newListAddr);
        assertNull(newListAddr + " should not exist", manager
                .findList(newListAddr));
    }

}
