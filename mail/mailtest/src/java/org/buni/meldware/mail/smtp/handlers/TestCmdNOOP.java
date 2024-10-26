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
package org.buni.meldware.mail.smtp.handlers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import junit.framework.TestSuite;

import org.buni.meldware.mail.smtp.SMTPRequest;
import org.buni.meldware.mail.smtp.SMTPResponse;
import org.buni.meldware.test.JMXTestWrapper;

/**
 * Test the CmdRCPT handler
 * @author Kabir Khan
 */
public class TestCmdNOOP extends HandlerBase {

    public static TestSuite suite() {
        return JMXTestWrapper.suite(TestCmdNOOP.class);
    }
    
    public TestCmdNOOP(String name){
        super(name);
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(TestCmdNOOP.class);
    }

    /*
     * @see HandlerBase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        handler = new CmdNOOP();
    }

    /* (non-Javadoc)
     * @see org.buni.meldware.mail.smtp.handlers.HandlerBase#testHandleRequest()
     */
	public void testHandleRequest() throws Exception {
		ByteArrayInputStream in = new ByteArrayInputStream("".getBytes());
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		SMTPRequest request =
			createRequest("NOOP", in, new String[] {});
			
		SMTPResponse response = this.handleRequest(out, request);
		String res = out.toString();

		assertTrue("response can't be null", response != null);
		String expected = "250 NOOP OK";
		
		assertTrue(
			"expected: " + expected + ", got=" + res.toString().trim(),
			expected.equals(res.toString().trim()));
		System.out.println("testHandleRequest()- DONE");
	}

}
