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
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.buni.meldware.mail.MailListener;
import org.buni.meldware.mail.Protocol;
import org.buni.meldware.mail.TestConstants;
import org.buni.meldware.mail.message.Message;
import org.buni.meldware.mail.smtp.SMTPProtocolInstance;
import org.buni.meldware.mail.smtp.SMTPProtocolMBean;
import org.buni.meldware.mail.smtp.SMTPRequest;
import org.buni.meldware.mail.smtp.SMTPResponse;
import org.buni.meldware.mail.userrepository.UserRepository;
import org.buni.meldware.mail.util.MMJMXUtil;

/**
 * base test for Handlers, gives us the basic setup
 * @author Andrew C. Oliver
 */
public abstract class HandlerBase extends TestCase {
    SMTPProtocolMBean fact;
    SMTPProtocolInstance protocol;
    SMTPHandler handler;
    
    public static final String newline = "\r\n";

    public HandlerBase(String name){
        super(name);
    }

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        //MailBodyManager mgr = new MailBodyManager();
        //mgr.setUseStore(false);
        fact = (SMTPProtocolMBean) MMJMXUtil.getMBean(TestConstants.SMTP_MBEAN, SMTPProtocolMBean.class);
        protocol = (SMTPProtocolInstance) fact.createInstance();
        //protocol = new SMTPProtocolInstance(SMTPHandlers.instance(),createMap(), mgr);
        // Inject the manager so that it does need to be looked up.
    }
    
    public abstract void testHandleRequest() throws Exception;

    protected SMTPResponse handleRequest(OutputStream out, SMTPRequest request) 
    throws Exception {
    	return handler.handleRequest(out,request,protocol);
    }
    
    /**
     * testcases that require state can set it here
     * @param key name of the state variable
     * @param value of the state
     */
    protected void setProtocolState(String key, Object value) {
        protocol.setState(key, value);		
    
    }

	/**
	 * create a Map of properties
	 */
	private Map createMap() {
		Map props = new HashMap();
		props.put(org.buni.meldware.mail.smtp.SMTPConstants.SERVER_NAME,"localhost");
		props.put(org.buni.meldware.mail.smtp.SMTPConstants.AUTH_REQUIRED, "true");
		props.put(org.buni.meldware.mail.smtp.SMTPConstants.STATUS, "ready");
		props.put(org.buni.meldware.mail.smtp.SMTPConstants.AUTH_METHODS, "AUTH LOGIN PLAIN");
		props.put(org.buni.meldware.mail.smtp.SMTPConstants.USER_REPOSITORY, createFakeUR());
		props.put(org.buni.meldware.mail.smtp.SMTPConstants.MAX_MSG_SIZE, new Long(1000000));
		props.put(org.buni.meldware.mail.smtp.SMTPConstants.MAX_RECEIVED_HEADERS, new Long(100));
		props.put(org.buni.meldware.mail.smtp.SMTPConstants.RECEIVED_HEADERS_THRESHOLD, new Long(20));
		props.put(org.buni.meldware.mail.smtp.SMTPConstants.MAX_OWN_RECEIVED_HEADERS, new Long(5));        	
		
		List lsnrs = new ArrayList();
		lsnrs.add(new ConcreteMailListener());
		props.put(org.buni.meldware.mail.smtp.SMTPConstants.MAIL_LISTENERS,lsnrs);
		return props;
	}

	/**
	 * create a mock UserRepository that always says TRUE
	 */
	private UserRepository createFakeUR() {
		return new UserRepository() {
		   /**
			 * 
			 */
			private static final long serialVersionUID = 3978138838017519668L;

		public boolean test(String user, String password) {
			  System.out.println("FAKE UR "+user+", "+password);
			  return true;
		   }

                   public boolean test(String user, String password, String apop) {
                          System.out.println("FAKE UR "+user+", "+password+","+apop);
                          return true;
                   }

		   public boolean test(String user) {
			  System.out.println("FAKE UR "+user);
			  return true;
		   }

        public String getName() {
            // TODO Auto-generated method stub
            return null;
        }

        public int getState() {
            // TODO Auto-generated method stub
            return 0;
        }

        public String getStateString() {
            // TODO Auto-generated method stub
            return null;
        }

        public void jbossInternalLifecycle(String arg0) throws Exception {
            // TODO Auto-generated method stub
            
        }

        public void create() throws Exception {
            // TODO Auto-generated method stub
            
        }

        public void destroy() {
            // TODO Auto-generated method stub
            
        }

        public void start() throws Exception {
            // TODO Auto-generated method stub
            
        }

        public void stop() {
            // TODO Auto-generated method stub
            
        }

        public String getType() {
            // TODO Auto-generated method stub
            return null;
        }
		};		
	}

	/**
	 * @param stream
	 * @return
	 */
	protected SMTPRequest createRequest(final String command, 
	                                    final ByteArrayInputStream stream,
	                                    final String[] parms) {
		SMTPRequest request = new SMTPRequest() {
			/* (non-Javadoc)
			 * @see org.buni.meldware.mail.smtp.SMTPRequest#getCommand()
			 */
			public String getCommand() {
				return command;
			}
	
			/* (non-Javadoc)
			 * @see org.buni.meldware.mail.smtp.SMTPRequest#arguments()
			 */
			public Iterator arguments() {
				List plist = new ArrayList(parms.length);
				for (int k = 0; k < parms.length;k++) {
					plist.add(parms[k]);
				}
				return plist.iterator();
			}
	
			/* (non-Javadoc)
			 * @see org.buni.meldware.mail.Request#getProtocol()
			 */
			public Protocol getProtocol() {
				return null;
			}
	
			/* (non-Javadoc)
			 * @see org.buni.meldware.mail.smtp.SMTPRequest#getInputStream()
			 */
			public InputStream getInputStream() {
				return stream;
			}
	
			/* (non-Javadoc)
			 * @see org.buni.meldware.mail.Request#getRemoteAddr()
			 */
			public String getRemoteAddr() {
				return null;
			}

			public String[] getArguments() {
				// TODO Auto-generated method stub
				return null;
			}
		};		
		return request;
	}
}

class ConcreteMailListener implements MailListener {
   public Message send( Message msg ) {
       System.out.println("Concrete Mail Listener sending "+msg);
       //return true;
       return msg;
   }
}
