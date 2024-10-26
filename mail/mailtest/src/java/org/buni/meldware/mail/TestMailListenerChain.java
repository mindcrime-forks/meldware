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
package org.buni.meldware.mail;

import java.io.ByteArrayInputStream;

import javax.management.MBeanInfo;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.buni.meldware.mail.message.Mail;
import org.buni.meldware.mail.message.Message;
import org.buni.meldware.test.JMXTestWrapper;
import org.buni.meldware.mail.util.MMJMXUtil;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Tests the decoupled MailListenerChain.
 * 
 * @author acoliver@jboss.org
 */
public class TestMailListenerChain extends TestCase {

    /**
     * a jboss generated proxy to the mailListenerChain
     */
    MailListenerChain mlchain;

    /**
     * Used in the processMail test. This is set by the FakeMailListener. It
     * should be the same as what we sent.
     */
    Message msg;
    
    public static TestSuite suite() {
        return JMXTestWrapper.suite(TestMailListenerChain.class);
    }
    
    public TestMailListenerChain(String name) {
        super(name);
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(TestMailListenerChain.class);
    }

    /**
     * sets the maillistener chain up with two fake listeners foo1 and 2. Most
     * tests use these, some (processMail) remove them and roll their own. These
     * don't actually have to exist (they don't in fact) through most of the
     * tests
     * 
     * @param mlchain
     *            should be the generated proxy to maillistenerchain
     */
    public void setListenerChain(MailListenerChain mlchain) {
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
        Element listeners = doc.createElement("listeners");
        Element firstlistener = doc.createElement("listener");
        firstlistener
                .appendChild(doc.createTextNode("mail.test:listener=foo1"));
        Element secondlistener = doc.createElement("listener");
        secondlistener.appendChild(doc
                .createTextNode("mail.test:listener=foo2"));
        listeners.appendChild(firstlistener);
        listeners.appendChild(secondlistener);
        mlchain.setListeners(listeners);
    }

    /**
     * checks that the listeners are there and the listListeners method returns
     * them in order
     */
    public void testListeners() {
        assertEquals("number of listners should be 2", 2, mlchain
                .getNumberListeners());
        String[] listeners = mlchain.listListeners();
        assertEquals("First listener should be mail.test:listener=foo1",
                "mail.test:listener=foo1", listeners[0]);
        assertEquals("Second listener should be mail.test:listener=foo2",
                "mail.test:listener=foo2", listeners[1]);

    }

    /**
     * tests that the getListeners attribute returns a valid Element with the
     * right XML
     */
    public void testGetListeners() {
        Element listenersElement = mlchain.getListeners();

        assertEquals("Parent node should be called <listeners>", "listeners",
                listenersElement.getNodeName());
        // Node child = listenersElement.getFirstChild();
        int k = 0;
        for (Node child = listenersElement.getFirstChild(); child != null; child = child
                .getNextSibling()) {
            assertEquals("each node should be called <listener>", "listener",
                    child.getNodeName());
            if (k == 0) {
                assertEquals(
                        "first node value should be mail.test:listener=foo1",
                        "mail.test:listener=foo1", child.getFirstChild()
                                .getNodeValue());
            } else if (k == 1) {
                assertEquals(
                        "second node value should be mail.test:listener=foo1",
                        "mail.test:listener=foo2", child.getFirstChild()
                                .getNodeValue());
            } else {
                fail("there should only be two child nodes");
            }
            k++;
        }
        assertEquals("there should be two child nodes", 2, k);
    }

    /**
     * tests that lsiteners can be removed by position and they maintain the
     * expected order. (removeListener(int) operation)
     */
    public void testRemoveListenerByPosition() {
        mlchain.removeListener(1);
        String[] listeners = mlchain.listListeners();
        assertEquals("there should be exactly 1 listener after remove", 1,
                listeners.length);
        assertEquals("listener 0 should be mail.test:listener=foo1",
                "mail.test:listener=foo1", listeners[0]);

        this.setListenerChain(mlchain);
        mlchain.removeListener(0);
        listeners = mlchain.listListeners();

        assertEquals("there should be exactly 1 listener after remove", 1,
                listeners.length);
        assertEquals("listener 0 should be mail.test:listener=foo2",
                "mail.test:listener=foo2", listeners[0]);
    }

    /**
     * tests that the listeners can be removed by ObjectName and they maintain
     * the expected order.
     * 
     * @throws Exception
     */
    public void testRemoveListenerByObjectName() throws Exception {
        ObjectName listener1 = new ObjectName("mail.test:listener=foo1");
        ObjectName listener2 = new ObjectName("mail.test:listener=foo2");

        mlchain.removeListener(listener1);
        String[] listeners = mlchain.listListeners();
        assertEquals("there should be exactly 1 listener after remove", 1,
                listeners.length);
        assertEquals("listener 0 should be mail.test:listener=foo2",
                "mail.test:listener=foo2", listeners[0]);

        this.setListenerChain(mlchain);
        mlchain.removeListener(listener2);
        listeners = mlchain.listListeners();
        assertEquals("there should be exactly 1 listener after remove", 1,
                listeners.length);
        assertEquals("listener 0 should be mail.test:listener=foo1",
                "mail.test:listener=foo1", listeners[0]);
    }

    /**
     * tests that listeners can be added and they appear in the correct order
     * (addListener(ObjectName) operation
     * 
     * @throws Exception
     */
    public void testAddListener() throws Exception {
        ObjectName listener1 = new ObjectName("mail.test:listener=foo3");

        mlchain.addListener(listener1);
        String[] listeners = mlchain.listListeners();
        assertEquals("there should be exactly 3 listeners after add", 3,
                listeners.length);
        assertEquals("listener 0 should be mail.test:listener=foo1",
                "mail.test:listener=foo1", listeners[0]);
        assertEquals("listener 1 should be mail.test:listener=foo2",
                "mail.test:listener=foo2", listeners[1]);
        assertEquals("listener 2 should be mail.test:listener=foo3",
                "mail.test:listener=foo3", listeners[2]);
    }

    /**
     * tests that listeners can be added at a specific postion and tehy appear
     * in the correct order (addListener(ObjectName,int))
     * 
     * @throws Exception
     */
    public void testAddListenerByPosition() throws Exception {
        ObjectName listener1 = new ObjectName("mail.test:listener=foo3");

        mlchain.addListener(listener1, 1);
        String[] listeners = mlchain.listListeners();
        assertEquals("there should be exactly 3 listeners after add", 3,
                listeners.length);
        assertEquals("listener 0 should be mail.test:listener=foo1",
                "mail.test:listener=foo1", listeners[0]);
        assertEquals("listener 1 should be mail.test:listener=foo3",
                "mail.test:listener=foo3", listeners[1]);
        assertEquals("listener 2 should be mail.test:listener=foo2",
                "mail.test:listener=foo2", listeners[2]);
    }

    /**
     * tests that the mlchain.processMail(Message) method properly sends the
     * mail to a listener (fakemaillistener)
     * 
     * @throws Exception
     */
    public void testSendMail() throws Exception {
        ObjectName oname = new ObjectName("mail.test:listener=fake");
        registerFakeMailListener(oname.toString());
        String[] listeners = mlchain.listListeners();
        System.out.println("Listeners: " + listeners.length);
        mlchain.removeListener(1);
        mlchain.removeListener(0);
        mlchain.addListener(oname);
        Mail m = MailUtil.create("test@localhost", 
                "Andrew Oliver <test@localhost>", "Test Subject", 
                "Message body");
        
        mlchain.processMail(m);
        assertNotNull("Message is null", this.msg);
        unregisterFakeMailListener(oname.toString());
    }

    /**
     * Generates a fake mail in the form of a ByteArrayStream
     * 
     * @return byte array steam containing fake mail
     */
    public static ByteArrayInputStream fakeMail() {
        StringBuffer sb = new StringBuffer();
        sb.append("\r\nMessage-ID: <3F328D84.3080108@localhost>\r\n"); // very
                                                                        // important
                                                                        // to
                                                                        // have
                                                                        // the
                                                                        // leading
                                                                        // blank!
        sb.append("Date: Thu, 07 Aug 2003 13:33:56 -0400\r\n");
        sb.append("From: Andrew Oliver <test@localhost>\r\n");
        sb
                .append("User-Agent: Mozilla/5.0 (Macintosh; U; PPC Mac OS X Mach-O; en-US; rv:1.3) Gecko/20030312\r\n");
        sb.append("X-Accept-Language: en-us, en\r\n");
        sb.append("MIME-Version: 1.0\r\n");
        sb.append("To:  test@localhost\r\n");
        sb.append("Subject: Test Subject\r\n");
        sb
                .append("Content-Type: text/plain; charset=us-ascii; format=flowed\r\n");
        sb.append("Content-Transfer-Encoding: 7bit\r\n\r\n");
        sb.append("Message body\r\n");
        sb.append(".\r\n");
        byte[] bmail = sb.toString().getBytes();
        System.out.println(new String(bmail));
        ByteArrayInputStream stream = new ByteArrayInputStream(bmail);
        stream.mark(2);
        return stream;
    }

    /**
     * registers FakeMailListener
     * 
     * @param name
     *            JMX name of the fake mail listener to register
     * @throws Exception
     */
    public void registerFakeMailListener(String name) throws Exception {
        MBeanServer mbserver = MMJMXUtil.locateJBoss();
        ObjectName oname = new ObjectName(name);
        MBeanInfo info = null;
        try {
            info = mbserver.getMBeanInfo(oname);
        } catch (Exception e) {
        }
        if (info == null) {
            FakeMailListener soml = new FakeMailListener(this);
            mbserver.registerMBean(soml, oname);
        }

    }

    /**
     * registers FakeMailListener
     * 
     * @param name
     *            JMX name of the fake mail listener to register
     * @throws Exception
     */
    public void unregisterFakeMailListener(String name) throws Exception {
        MBeanServer mbserver = MMJMXUtil.locateJBoss();
        ObjectName oname = new ObjectName(name);
        mbserver.unregisterMBean(oname);
    }

    
    private final static String CHAIN_NAME = "mail.test:service=mlchain";
    protected void setUp() throws Exception {
        //MBeanServerUtil.configureMBeanServerFactory();
        MBeanServerUtil.registerSysOutMailListeners(new String[] {
                "mail.test:listener=foo1", "mail.test:listener=foo2" });
        MBeanServerUtil.register(new MailListenerChainService(), CHAIN_NAME);
        mlchain = (MailListenerChain) MMJMXUtil.getMBean("mail.test:service=mlchain", MailListenerChain.class);
        //mlchain = (MailListenerChain) MMJMXUtil.getMBean("meldware.mail:type=MailServices,name=MailListenerChain", MailListenerChain.class);

        setListenerChain(mlchain);
    }

    protected void tearDown() throws Exception {
        MBeanServerUtil.unregister(CHAIN_NAME);
        mlchain = null;
        this.msg = null;
    }

    void setReceived(Message msg) {
        this.msg = msg;
    }

}
