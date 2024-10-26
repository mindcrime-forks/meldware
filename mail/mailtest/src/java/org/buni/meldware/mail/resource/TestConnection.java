/*
 * Bunisoft the Open Source Communications Company
 * Copyright 2006, Bunisoft Inc., and individual contributors as
 * indicated by the @authors tag.  See the copyright.txt in the 
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; version 2.1 of
 * the License.
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
package org.buni.meldware.mail.resource;

import javax.naming.InitialContext;
import javax.resource.cci.Connection;
import javax.resource.cci.ConnectionFactory;
import javax.resource.cci.IndexedRecord;
import javax.resource.cci.Interaction;
import javax.resource.cci.MappedRecord;
import javax.resource.cci.Record;
import javax.resource.cci.RecordFactory;

import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author Michael Barker
 *
 */
public class TestConnection extends TestCase {
    
    private final static String DS = "java:MeldwareMail";
    
    public static TestSuite suite() {
        return new TestSuite(TestConnection.class);
    }
    
    public TestConnection(String name) {
        super(name);
    }
    
    public void testGetConnection() throws Exception {
        InitialContext ic = new InitialContext();
        ConnectionFactory cf = (ConnectionFactory) ic.lookup(DS);
        Connection cn = cf.getConnection();
        assertNotNull(cn);
    }

    public void testRecordFactory() throws Exception {
        InitialContext ic = new InitialContext();
        ConnectionFactory cf = (ConnectionFactory) ic.lookup(DS);
        RecordFactory rf = cf.getRecordFactory();
        assertNotNull("Record Factory is null", rf);
        assertNotNull("Mapped Record is null", rf.createMappedRecord("Test"));
        assertNotNull("Indexed Record is null", rf.createIndexedRecord("Test"));
    }
    
    @SuppressWarnings("unchecked")
    public void testEmail() throws Exception {
        InitialContext ic = new InitialContext();
        ConnectionFactory cf = (ConnectionFactory) ic.lookup(DS);
        Connection cn = cf.getConnection();
        Interaction i = cn.createInteraction();
        RecordFactory rf = cf.getRecordFactory();
        
        MappedRecord mr = rf.createMappedRecord("Test");
        mr.put("To", new String[] { "<test@localhost>" });
        mr.put("From", "<test2@localhost>");
        mr.put("Subject", "This is a test");
        mr.put("_body_", "This is some test content");
        
        Record r = i.execute(null, mr);
        
        assertTrue("Response is not an indexed record", r instanceof IndexedRecord);
        IndexedRecord ir = (IndexedRecord) r;
        assertTrue("At least one result should be returned", ir.size() > 0);
        MappedRecord result = (MappedRecord) ir.get(0);
        String address = (String) result.get("address");
        assertEquals("<test@middlesoft.co.uk>", address);
        Object status = result.get("status");
        assertNotNull(status);
        System.out.println(status);
    }
}
