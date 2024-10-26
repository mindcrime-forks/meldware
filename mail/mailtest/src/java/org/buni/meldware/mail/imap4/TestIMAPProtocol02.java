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

import org.apache.bsf.BSFException;
import org.buni.meldware.mail.protocol.RubyProtocolRunner;


/**
 * @author Michael Barker
 */
public class TestIMAPProtocol02 extends TestCase {

    File dir = new File("mail/mailtest/src/java/org/buni/meldware/mail/imap4");
        
    public TestIMAPProtocol02(String name){
        super(name);
    }

	public static void main(String[] args) {
		junit.textui.TestRunner.run(TestIMAPProtocol02.class);
	}
    
	public void testFetchBodystructure() throws BSFException {
        File f = new File(dir, "imap_fetch_bodystructure.rb");
        RubyProtocolRunner.run("localhost", 9143, f);	    
    }

}
