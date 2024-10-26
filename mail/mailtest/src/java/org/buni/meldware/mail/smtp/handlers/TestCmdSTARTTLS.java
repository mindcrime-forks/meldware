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

import junit.framework.TestSuite;

import org.buni.meldware.mail.TestConstants;
import org.buni.meldware.mail.dev.prot.ProtocolHandler;
import org.buni.meldware.test.JMXTestWrapper;

/**
 * Test the CmdSTARTTLS handler
 * @author Michael Krause
 * "author Kabir Khan
 */
public class TestCmdSTARTTLS extends HandlerBase {
	 
    public static TestSuite suite() {
        return JMXTestWrapper.suite(TestCmdSTARTTLS.class);
    }
    
    public TestCmdSTARTTLS(String name){
        super(name);
     }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(TestCmdSTARTTLS.class);
    }

    protected void tearDown() throws Exception{
        //fact.setTlsEnabled(false);
        //super.tearDown();
    }

    /*
     * @see HandlerBase#setUp()
     */
    protected void setUp() throws Exception {
        //super.setUp();
        //fact.setTlsEnabled(true);
    }
    

    /* (non-Javadoc)
     * @see org.buni.meldware.mail.smtp.handlers.HandlerBase#testHandleRequest()
     */
    public void testHandleRequestELHO() throws Exception {
    	//Send HELO
    	String request = "EHLO jboss.org"; 
        ProtocolHandler ph = new ProtocolHandler("localhost", TestConstants.SMTP_PORT);
    	
		//Send Get response to HELO
        ph.checkInput("220.*"); //220 localhost SMTP Server (Meldware Mail SMTP Server version 0.7) ready...
        ph.writeln(request);
        ph.checkInput("250-.*"); //250-localhost Hello jboss.org (127.0.0.1 (127.0.0.1))
        ph.checkInput("250-STARTTLS.*"); 
        ph.checkInput("250 AUTH LOGIN PLAIN.*"); //250 AUTH LOGIN PLAIN 
		
		request = "STARTTLS";
		ph.writeln(request);
		ph.checkInput("220 Ready to start TLS.*");
    }
    

    public void testHandleRequest() throws Exception {
    	//Send HELO
    	String request = "HELO jboss.org"; 
        ProtocolHandler ph = new ProtocolHandler("localhost", TestConstants.SMTP_PORT);
    	
		//Send Get response to HELO
        ph.checkInput("220.*"); //220 localhost SMTP Server (Meldware Mail SMTP Server version 0.7) ready...
        ph.writeln(request);
        ph.checkInput("250 .*"); //250-localhost Hello jboss.org (127.0.0.1 (127.0.0.1))
        //ph.checkInput("250-STARTTLS.*"); 
        //ph.checkInput("250 AUTH LOGIN PLAIN.*"); //250 AUTH LOGIN PLAIN 
		
		request = "STARTTLS";
		ph.writeln(request);
		ph.checkInput("220 Ready to start TLS.*");
    }
    
    
}
