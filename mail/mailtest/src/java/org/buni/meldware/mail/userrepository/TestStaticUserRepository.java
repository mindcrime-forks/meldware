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
package org.buni.meldware.mail.userrepository;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.buni.meldware.test.JMXTestWrapper;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;

/**
 * Test for the Static User Repository MBean
 * @author Andrew C. Oliver
 */
public class TestStaticUserRepository extends TestCase {
    private StaticUserRepository sur;

    public static TestSuite suite() {
        return JMXTestWrapper.suite(TestStaticUserRepository.class);
    }
    
    public TestStaticUserRepository(String name){
        super(name);
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(TestStaticUserRepository.class);
    }

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        sur = new StaticUserRepository();
        Map users = new HashMap();
        users.put("testuser1", "testuser1pwd");
        users.put("testuser2", "testuser2pwd");
        users.put("testuser3", "testuser3pwd");

        sur.setUsers(createTestUsers(users));
        sur.start();
    }

    /**
     * @return
     */
    private Element createTestUsers(Map users) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {

            e.printStackTrace();
        }
        DOMImplementation impl = builder.getDOMImplementation();

        DocumentType DOCTYPE = impl.createDocumentType("non", "non", "non");
        Document doc = impl.createDocument("non", "non", DOCTYPE);
        Element retval = doc.createElement("users");
        Iterator i = null;
        for (i = users.keySet().iterator(); i.hasNext();) {
            String key = (String) i.next();
            String val = (String) users.get(key);
            Element user = doc.createElement("user");
            Element id = doc.createElement("id");
            Element pw = doc.createElement("password");
            id.appendChild(doc.createTextNode(key));
            pw.appendChild(doc.createTextNode(val));
            user.appendChild(id);
            user.appendChild(pw);
            retval.appendChild(user);
        }

        return retval;
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        sur.stop();
    }

    public void testStaticUserRepository() throws Exception {
    }

    public void testProduceServiceProxy() {
        //ServiceProxy sp = sur.produceServiceProxy();
        //assertTrue(
        //    "service proxy must be an instance of UserRepository",
        //    sp instanceof UserRepository);
        UserRepository ur = sur;
        assertTrue(
            "testuser1,testuser1pwd should be valid",
            ur.test("testuser1", "testuser1pwd"));
        assertTrue(
            "testuser2,testuser2pwd should be valid",
            ur.test("testuser2", "testuser2pwd"));
        assertTrue(
            "testuser3,testuser3pwd should be valid",
            ur.test("testuser3", "testuser3pwd"));
        assertFalse(
            "invaliduser,invalidpassword should be invalid",
            ur.test("invaliduser", "invalidpassword"));
        assertFalse(
            "testuser1, invalidpassword should be false",
            ur.test("testuser1", "invalidpassword"));
    }

    public void testSetUsers() {
        Map users = new HashMap();
        users.put("newuser1", "newuser1pwd");
        users.put("newuser2", "newuser2pwd");
        users.put("newuser3", "newuser3pwd");
        sur.setUsers(this.createTestUsers(users));
        //ServiceProxy sp = sur.produceServiceProxy();
        UserRepository ur = (UserRepository) sur;
        assertTrue(
            "newuser1,newuser1pwd should be valid",
            ur.test("newuser1", "newuser1pwd"));
        assertTrue(
            "newuser2,newuser2pwd should be valid",
            ur.test("newuser2", "newuser2pwd"));
        assertTrue(
            "newuser3,newuser3pwd should be valid",
            ur.test("newuser3", "newuser3pwd"));
        assertFalse(
            "testuser1,testuser1pwd should be invalid",
            ur.test("testuser1", "testuser1pwd"));
        assertFalse(
            "testuser2,testuser2pwd should be invalid",
            ur.test("testuser2", "testuser2pwd"));
        assertFalse(
            "testuser3,testuser3pwd should be invalid",
            ur.test("testuser3", "testuser3pwd"));

    }

    public void testGetUsers() {
        Map users = new HashMap();
        users.put("testuser1", "testuser1pwd");
        users.put("testuser2", "testuser2pwd");
        users.put("testuser3", "testuser3pwd");
        Element source = createTestUsers(users);
        Element target = sur.getUsers();
        System.out.println("source=" + source);
        System.out.println("target=" + target);

        //     assertEquals(source, target);
    }

    public void testAddUser() {
        sur.addUser("testuser4", "testuser4pwd");
        //ServiceProxy sp = sur.produceServiceProxy();
        UserRepository ur = (UserRepository) sur;
        assertTrue(
            "testuser1,testuser1pwd should be valid",
            ur.test("testuser1", "testuser1pwd"));
        assertTrue(
            "testuser2,testuser2pwd should be valid",
            ur.test("testuser2", "testuser2pwd"));
        assertTrue(
            "testuser3,testuser3pwd should be valid",
            ur.test("testuser3", "testuser3pwd"));
        assertTrue(
            "testuser4,testuser4pwd should be valid",
            ur.test("testuser4", "testuser4pwd"));
        assertFalse(
            "invaliduser,invalidpassword should be invalid",
            ur.test("invaliduser", "invalidpassword"));
        assertFalse(
            "testuser1, invalidpassword should be false",
            ur.test("testuser1", "invalidpassword"));

    }
}
