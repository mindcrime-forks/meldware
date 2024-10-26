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
package org.buni.meldware.mail.domaingroup;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
  

/**
 * Test for DomainGroupMBean. Ensures that we can add domains, and that 
 * they'll correctly tell us if email addresses are in that domain group.
 * @author Andrew C. Oliver <acoliver@jboss.org>
 */
public class TestDomainGroup extends TestCase {
    DomainGroupMBean domaingroup;
    public static void main(String[] args) {
    //    junit.textui.TestRunner.run(TestDomainGroup.suite());
    }

    public static TestSuite suite() {
        return new TestSuite(TestDomainGroup.class);
    }
    
    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        domaingroup = new DomainGroup();
        domaingroup.start();
    }
    
    /**
     * tests the normal use of domain group
     * @throws Exception
     */
    public void testNormal() throws Exception {
        domaingroup = new DomainGroup();
        domaingroup.setDomains(createDomains());
        String[] domains = domaingroup.listDomains();
        assertTrue("localhost was in domaingroup", isIn(domains,"localhost"));
        assertTrue("jboss.org was in domaingroup", isIn(domains,"jboss.org"));
        assertTrue("mail.jboss.org was in domaingroup", isIn(domains,"mail.jboss.org"));                
    }

    /**
     * creates the expected domains/domain xml input element
     * @return list of domains for testing
     */
    private Element createDomains() {
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
        Element retval = doc.createElement("domains");

        Element domain = doc.createElement("domain");
        domain.appendChild(doc.createTextNode("localhost"));
        retval.appendChild(domain);

        domain = doc.createElement("domain");
        domain.appendChild(doc.createTextNode("jboss.org"));
        retval.appendChild(domain);
        
        domain = doc.createElement("domain");
        domain.appendChild(doc.createTextNode("mail.jboss.org"));
        retval.appendChild(domain);        
        
        return retval;
    }

    /**
     * tests that we can add and list domains
     * @throws Exception
     */
    public void testAddDomain() throws Exception {
        domaingroup.add("localhost");
        domaingroup.add("jboss.org");
        domaingroup.add("mail.jboss.org");
        String[] domains = domaingroup.listDomains();
        assertTrue("localhost was in domaingroup", isIn(domains,"localhost"));
        assertTrue("jboss.org was in domaingroup", isIn(domains,"jboss.org"));
        assertTrue("mail.jboss.org was in domaingroup", isIn(domains,"mail.jboss.org"));        
    }
    
    
    public void testHostInDomain() throws Exception {
        domaingroup.add("jboss.org");
        assertTrue("localhost was in domaingroup", domaingroup.isHostInGroup("mail.jboss.org"));
    }
    
    
    /**
     * tests that we can correctly get back taht domains are in a group
     * @throws Exception
     */
    public void testIsInGroup() throws Exception {
        domaingroup.add("localhost");
        domaingroup.add("jboss.org");
        domaingroup.add("mail.jboss.org");
        assertTrue("localhost was in domaingroup", domaingroup.isInGroup("localhost"));
        assertTrue("jboss.org was in domaingroup", domaingroup.isInGroup("jboss.org"));
        assertTrue("mail.jboss.org was in domaingroup", domaingroup.isInGroup("mail.jboss.org"));        
    }
    
    private boolean isIn(String[] strings, String string) {
        for (int k = 0; k < strings.length; k++) {
            if(strings[k].equals(string)) {
                return true;
            }
        }
        return false;
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }
}
