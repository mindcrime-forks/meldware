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
package org.buni.meldware.mail.imap4;

import java.io.File;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.bsf.BSFException;
import org.buni.meldware.mail.protocol.RubyProtocolRunner;


/**
 * @author Michael Barker
 */
public class TestIMAPProtocol extends TestCase {

    File dir = new File("mail/mailtest/src/java/org/buni/meldware/mail/imap4");
    
    public static TestSuite suite() {
        TestSuite ts = new TestSuite();
        ts.addTest(new TestIMAPProtocol("testFetchBody"));
        return ts;
    }
    
    public TestIMAPProtocol(String name){
        super(name);
    }

	public static void main(String[] args) {
		junit.textui.TestRunner.run(TestIMAPProtocol.class);
	}
    
	public void testFetchHeaders() throws BSFException {
        File f = new File(dir, "imap_fetch_headers.rb");
        RubyProtocolRunner.run("localhost", 9143, f);	    
    }

    public void testFetchBody_2_2() throws BSFException {
        File f = new File(dir, "imap_fetch_body[2.2].rb");
        RubyProtocolRunner.run("localhost", 9143, f);       
    }
    
    public void testFetchBody_2_2_MIME() throws BSFException {
        File f = new File(dir, "imap_fetch_body[2.2.MIME].rb");
        RubyProtocolRunner.run("localhost", 9143, f);       
    }
    
    public void testFetchBody() throws BSFException {
        File f = new File(dir, "imap_fetch_body.rb");
        RubyProtocolRunner.run("localhost", 9143, f);       
    }
}
