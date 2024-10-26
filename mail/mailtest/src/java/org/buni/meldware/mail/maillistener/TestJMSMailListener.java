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
package org.buni.meldware.mail.maillistener;

import javax.naming.Context;
import javax.naming.NamingException;

import junit.framework.TestCase;

import org.buni.meldware.mail.message.MailAddress;
import org.buni.meldware.mail.util.MMJMXUtil;

import com.sun.naming.internal.ResourceManager;

/**
 * Tests the JMS Mail Listener
 * TODO: Add queue test to make sure the right messages get put on the right queues
 * @author Andrew C. Oliver
 */
public class TestJMSMailListener extends TestCase {

    private JMSMailListenerMBean maillistener;

    public TestJMSMailListener(String name)
    {
        super (name);
		try {
			System.out.println("TestJMSMailListener Context.PROVIDER_URL: " + ResourceManager.getInitialEnvironment(null).get(Context.PROVIDER_URL));
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
   
    public static void main(String[] args) {
        junit.textui.TestRunner.run(TestJMSMailListener.class);
    }

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
		//Set up the DomainGroup needed by the maillistener
//		MBeanServerUtil.configureMBeanServerFactory();
//
//        
//        InitialContext ctx = new InitialContext();
//		ctx.bind("java:/ConnectionFactory", new TestQueueConnectionFactory());
//		//For some reason:
//		//	ctx.bind("queue/post", "classcastexceptioninabox");
//		//causes a NameNotFoundException "queue not bound"
//		Context qctx = ctx.createSubcontext("queue");
//		qctx.bind("post", new TestQueue("post"));
//		qctx.bind("off", new TestQueue("off"));    
//
//    	maillistener = new JMSMailListener("java:/ConnectionFactory","java:/ConnectionFactory");
//        maillistener.setDestinationType("queue");
//
//		MBeanServerUtil.registerDomainGroup();
//		maillistener.setDomainGroup(new ObjectName("meldware.mail:type=MailServices,name=DomainGroup,group=Local"));
//		maillistener.setOnServerPostDestination("post");
//		maillistener.setOffServerPostDestination("off");
//        maillistener.start();
//        
//      MBeanServerUtil.registerMailBodyManager();
        maillistener = (JMSMailListenerMBean) MMJMXUtil.getMBean("meldware.mail:type=MailServices,name=MailListener", JMSMailListenerMBean.class);
    }

	/*
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		//maillistener.stop();
		//InitialContext ctx = new InitialContext();
		//ctx.unbind("queue");
		//ctx.unbind("java:/ConnectionFactory");
	}

    public void testSetOnServerPostDestination() throws Exception {
        maillistener.setOnServerPostDestination("queue/post");
        maillistener.setOffServerPostDestination("queue/off");
        assertEquals("post", maillistener.getOnServerPostDestination());
        assertFalse(
            "on server and offserver should be not equal",
            maillistener.getOnServerPostDestination().equals(
                maillistener.getOffServerPostDestination()));
	}

    public void testSetOnServerDomains() throws Exception {
        maillistener.setOnServerPostDestination("queue/post");
        maillistener.setOffServerPostDestination("queue/off");

        MailAddress ma = MailAddress.parseSMTPStyle("<noone@localhost>");
        String queue = maillistener.getDestinationForMailAddress(ma);
        assertEquals(queue, "post");
        ma = MailAddress.parseSMTPStyle("<someone@spamreceipient.org>");
        queue = maillistener.getDestinationForMailAddress(ma);
        assertEquals(queue, "off");
    }

    /**
     * @return
     */
//    private Element createDomainsList() {
//        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//        DocumentBuilder builder = null;
//        try {
//            builder = factory.newDocumentBuilder();
//        } catch (ParserConfigurationException e) {
//
//            e.printStackTrace();
//        }
//        DOMImplementation impl = builder.getDOMImplementation();
//
//        DocumentType DOCTYPE = impl.createDocumentType("non", "non", "non");
//        Document doc = impl.createDocument("non", "non", DOCTYPE);
//        Element retval = doc.createElement("domains");
//
//        Element domain = doc.createElement("domain");
//        domain.appendChild(doc.createTextNode("localhost"));
//        retval.appendChild(domain);
//
//        domain = doc.createElement("domain");
//        domain.appendChild(doc.createTextNode("nowhere.org"));
//        retval.appendChild(domain);
//
//        return retval;
//    }


}


//class TestQueueConnectionFactory implements QueueConnectionFactory, Serializable{
//
//	/**
//	 * 
//	 */
//	private static final long serialVersionUID = 3256726186569970994L;
//
//	public QueueConnection createQueueConnection() throws JMSException {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	public QueueConnection createQueueConnection(String arg0, String arg1)
//			throws JMSException {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	// Added following two methods to conform to JBoss 4.0.0 spec
//	// by Dawie Malan
//	public Connection createConnection() throws JMSException {
//	    return (Connection)createQueueConnection();
//	}
//
//	public Connection createConnection(String arg0, String arg1)
//			throws JMSException {
//	    return (Connection)createQueueConnection(arg0,arg1);
//	}
//};
//
//class TestQueue implements Queue, Serializable{
//	/**
//	 * 
//	 */
//	private static final long serialVersionUID = 3617573803832914999L;
//	String queueName;
//	public TestQueue(String queueName){
//		this.queueName = queueName;
//	}
//
//	public String getQueueName() throws JMSException {
//		// TODO Auto-generated method stub
//		return null;
//	}

//}
