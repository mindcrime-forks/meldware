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
package org.buni.meldware.mail.smtp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.buni.meldware.mail.MailListener;
import org.buni.meldware.mail.Protocol;
import org.buni.meldware.mail.message.MailAddress;
import org.buni.meldware.mail.message.Message;
import org.buni.meldware.test.JMXTestWrapper;
import org.buni.meldware.mail.userrepository.UserRepository;
import org.buni.meldware.mail.util.MMJMXUtil;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;

/**
 * Tests the SMTPProtocol Object.  Basic testing is done here, more extensive testing is done
 * in the individual command handlers.  As the implementation progresses some scenario testing
 * should also be done, but might be best done in other tests to ensure manageability.
 * @author Andrew C. Oliver
 */
public class TestSMTPProtocol extends TestCase {
    SMTPProtocolMBean protocolmbean;
    SMTPProtocolInstance protocol;

    public static TestSuite suite() {
        return JMXTestWrapper.suite(TestSMTPProtocol.class);
    }
    
    public TestSMTPProtocol(String name){
        super(name);
    }

	public static void main(String[] args) {
		junit.textui.TestRunner.run(TestSMTPProtocol.class);
	}

	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
//		System.out.println("SMTPProtocol setup");
//		MBeanServerUtil.configureMBeanServerFactory();
//		MBeanServerUtil.registerStaticUserRepository();
//		MBeanServerUtil.registerSysOutMailListener();
//      MBeanServerUtil.registerMailBodyManager();
//		protocolmbean = new SMTPProtocol();
//        protocolmbean.setProperties(createProps());
	    protocolmbean = (SMTPProtocolMBean) MMJMXUtil.getMBean("meldware.mail:type=Protocol,name=SMTPProtocol", SMTPProtocolMBean.class);
        protocol = (SMTPProtocolInstance) protocolmbean.createInstance();

        //System.out.println("SMTPProtocol setup done");
	}


	/**
     * @return
     */
    private Element createProps() {
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
        Element retval = doc.createElement("properties");
        Element property = getProperty(doc, SMTPConstants.SERVER_NAME, "localhost");
        retval.appendChild(property);
        property = getProperty(doc, SMTPConstants.AUTH_REQUIRED, "true");
        retval.appendChild(property);
        property = getProperty(doc, SMTPConstants.AUTH_METHODS, "AUTH LOGIN PLAIN");
        retval.appendChild(property);
        property = getProperty(doc, SMTPConstants.USER_REPOSITORY, "meldware.mail:type=MailServices,name=UserRepository");
        retval.appendChild(property);
        property = getProperty(doc, SMTPConstants.VERIFY_IDENTITY, "true");
        retval.appendChild(property);
        property = getProperty(doc, SMTPConstants.MAX_MSG_SIZE, "1000000");
        retval.appendChild(property);
        property = getProperty(doc, SMTPConstants.MAIL_LISTENERS, new String[]{"meldware.mail:type=MailServices,name=MailListener"});
        retval.appendChild(property);
        

        return retval;
    }
    
    /**
     * @param doc
     * @param string
     * @param strings
     * @return
     */
    private Element getProperty(Document doc, String keyval, String[] strings) {
        Element property = doc.createElement("property");
         Element key = doc.createElement("name");
         key.appendChild(doc.createTextNode(keyval));
         Element values = doc.createElement("values");
         for (int k = 0; k < strings.length; k++) {
            Element value = doc.createElement("value");
            value.appendChild(doc.createTextNode(strings[k]));
            values.appendChild(value);
         }         
         property.appendChild(key);
         property.appendChild(values);
         return property;

    }

    private Element getProperty(Document doc, String keyval, String val) {
        Element property = doc.createElement("property");
         Element key = doc.createElement("name");
         key.appendChild(doc.createTextNode(keyval));
         Element value = doc.createElement("value");
         value.appendChild(doc.createTextNode(val));  
         property.appendChild(key);
         property.appendChild(value);           
         return property;
    }

    /*
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}


    /**
     * An SMTP greeting should contain the "220 servername SMTP Server (softwareversion) ready"
     * string followed by a long date (not checked).  This verifies the greet method behaves as
     * expected.
     * @throws IOException
     */
	public void testGreet() throws IOException {
		System.out.println("in greet");
		String compare = "220 localhost SMTP Server (Meldware Mail SMTP Server version 0.8) ready";
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		protocol.greet(stream);
		byte[] b = (stream.toByteArray());
		String s = new String(b);
		System.out.println(s);
		assertTrue("Expected "+compare+"\n got"+s, s.substring(0,compare.length()).equals(compare));
	}

    /**
     * Test to verify that a properly formed request is parsed into a request object properly
     * @throws IOException
     */
	public void testParseRequest() throws IOException {
		String request = "EHLO me\n";
		ByteArrayInputStream stream = new ByteArrayInputStream(request.getBytes());
		SMTPRequest r = (SMTPRequest) protocol.parseRequest(stream,createSocket());
		assertTrue("Expected EHLO got "+r.getCommand(),
		            request.substring(0,4).equals(r.getCommand()));
	}

    /**
     * tests that a response to a written command can be read off of the stream
     * @throws IOException
     */
	public void testReadResponse() throws IOException {
		String request = "Test response string\r\n";
		ByteArrayInputStream stream = new ByteArrayInputStream((request).getBytes());
	    String input = protocol.readResponse(createRequest(null,stream));
	    assertEquals(request.trim(), input.trim());
	}

    /**
     * does nothing at the moment... The only thing it shouldn't do is throw an exception.
     * Later we should log or something
     *
     */
	public void testHandleIOError() {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		protocol.handleIOError(bos);
		createInvalidOutputStream();
	}
	
	/**
	 * creates an invalid input stream to simulate a closed one.
	 */
	private OutputStream createInvalidOutputStream() {
		return new OutputStream() {
			public void close() throws IOException {
				super.close();
			}

			public void flush() throws IOException {
				throw new IOException("Invalid stream test (stream closed)");
			}

			public void write(byte[] arg0, int arg1, int arg2)
				throws IOException {
					throw new IOException("Invalid stream test (stream closed)");

			}

			public void write(byte[] arg0) throws IOException {
				throw new IOException("Invalid stream test (stream closed)");

			}

			public void write(int arg0) throws IOException {
				throw new IOException("Invalid stream test (stream closed)");

			}
		};
		
	}

	public Socket createSocket() {
		Socket s= new Socket() {
			
		};
		return s;
	}

	/**
	 * @param string
	 * @return
	 */
	private MailAddress createMailAddr(String string) {
		MailAddress addy = MailAddress.parseSMTPStyle(string);
		return addy;
	}

	

	/**
	 * @param stream
	 * @return
	 */
	private SMTPRequest createRequest(final String command, final ByteArrayInputStream stream) {
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
				return null;
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
			private static final long serialVersionUID = 3834307353989428791L;

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



}

class ConcreteMailListener implements MailListener {
   public Message send( Message msg ) {
      System.out.println("Concrete Mail Listener sending "+msg);
      //return true;
      return msg;
   }
}