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
import org.buni.meldware.mail.imap4.commands.AbstractImapCommand;
import org.buni.meldware.mail.message.Message;
import org.buni.meldware.mail.userrepository.UserRepository;
import org.buni.meldware.mail.util.MMJMXUtil;

/**
 * base test for Handlers, gives us the basic setup
 * @author Andrew C. Oliver
 */
public abstract class HandlerBase extends TestCase {
    IMAP4ProtocolMBean fact;
    IMAP4ProtocolInstance protocol;
    AbstractImapCommand handler;
    
    public static final String newline = "\r\n";

    public HandlerBase(String name){
        super(name);
    }
    
    //protected abstract AbstractImapCommand createHandler();

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        //MailBodyManager mgr = new MailBodyManager();
        //mgr.setUseStore(false);
        fact = (IMAP4ProtocolMBean) MMJMXUtil.getMBean(TestConstants.IMAP_MBEAN, IMAP4ProtocolMBean.class);
        protocol = (IMAP4ProtocolInstance) fact.createInstance();
        //protocol = new ProtocolInstance(IMAP4Handlers.instance(),createMap(), mgr);
        // Inject the manager so that it does need to be looked up.
    }
    

    protected IMAP4Response handleRequest(OutputStream out, IMAP4Request request) 
    throws Exception {
    	//return handler.handleRequest(out,request,protocol);
        return handler.execute();
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
		props.put(org.buni.meldware.mail.imap4.IMAP4Constants.SERVER_NAME,"localhost");
		props.put(org.buni.meldware.mail.imap4.IMAP4Constants.USER_REPOSITORY, createFakeUR());
		
		List lsnrs = new ArrayList();
		lsnrs.add(new ConcreteMailListener());
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
	protected IMAP4Request createRequest(final Protocol protocol,
                                            final String tag,
                                            final String command, 
	                                    final ByteArrayInputStream stream,
	                                    final String[] parms) {
		IMAP4Request request = new IMAP4Request() {
			/* (non-Javadoc)
			 * @see org.buni.meldware.mail.imap4.IMAP4Request#getCommand()
			 */
			public org.buni.meldware.mail.imap4.commands.AbstractImapCommand getCommand() {
 System.err.println("TODO FIX THIS imap4 test HandlerBase");
				return null;//command; MAKE COMPILE
			}
	
			/* (non-Javadoc)
			 * @see org.buni.meldware.mail.imap4.IMAP4Request#arguments()
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
				return protocol;
			}
	
			/* (non-Javadoc)
			 * @see org.buni.meldware.mail.imap4.IMAP4Request#getInputStream()
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
				return parms;
			}

                        public void setTag(String tag) {
                        }

                        public String getTag() {
                                return tag;
                        }

                        public java.net.Socket getSocket() {
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
