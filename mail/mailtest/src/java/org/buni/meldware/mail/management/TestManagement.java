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
package org.buni.meldware.mail.management;

import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.management.ObjectName;
import javax.naming.Context;
import javax.naming.InitialContext;

import junit.framework.TestCase;

import org.buni.meldware.mail.ServerMBean;
import org.buni.meldware.mail.ThreadPoolMBean;
import org.buni.meldware.mail.domaingroup.DomainGroupMBean;
import org.buni.meldware.mail.message.TestMail;
import org.buni.meldware.mail.smtp.SMTPProtocolMBean;
import org.buni.meldware.test.ProxyMaker;
import org.jboss.jmx.adaptor.rmi.RMIAdaptor;

/**
 * @author andy
 * 
 */
public class TestManagement extends TestCase {
	private static String MBEAN_ADMIN = "meldware.mail:type=MailServices,name=AdminTool";
 
	private AdminTool admin;

	/*
	 * @see TestCase#setUp()  
	 */
	protected void setUp() throws Exception {
        Context ctx = new InitialContext();
      //  Context ctx = new InitialContext();
      RMIAdaptor rmi = (RMIAdaptor) ctx.lookup("jmx/rmi/RMIAdaptor");
      admin = (AdminTool) ProxyMaker.makeProxy(rmi, AdminTool.class, new ObjectName(MBEAN_ADMIN));
      
		//admin = (AdminTool) MMJMXUtil
				//.getMBean(MBEAN_ADMIN, AdminTool.class);
	}
        
        public void testQueue() throws Exception {
            ObjectName on = new ObjectName("jboss.mq.destination:service=Queue,name=foo"); 
            admin.readDescriptor();
            admin.installQueue("foo","A test queue created by junit");
            admin.sync();
            admin.writeDescriptor();
            Set<ObjectName> queues = admin.getQueues(); 
            assertTrue("we should see "+on.toString()+" in the queues "+queues.toString(), queues.contains(on));
            admin.readDescriptor();
            admin.removeQueue("foo");
            admin.sync();
            admin.writeDescriptor();
            queues = admin.getQueues();
            assertTrue("we should NOT see "+on.toString()+" in "+queues.toString(), !queues.contains(on));
        }

	public void testThreadPool() throws Exception {
		admin.readDescriptor();
		String name = "testThreadPool";
		String prettyName = "Test Service ThreadPool";
		Integer initial = 10;
		Integer min = 10;
		Integer idleKeepAlive = new Integer(10000);
		Integer max = 10;
		admin.createThreadPool(name, prettyName, initial, min, max,
				idleKeepAlive);
		admin.sync();
		admin.writeDescriptor();
		Set<ObjectName> pools = admin.getThreadPools();
		String mustContain = "meldware.mail:type=ThreadPool,name=" + name;
		assertTrue("returned set must contain " + pools, pools
				.contains(new ObjectName(mustContain)));
		ThreadPoolMBean mbean = (ThreadPoolMBean) getMBean (
				new ObjectName(mustContain), ThreadPoolMBean.class);
		int tmin = mbean.getMin();
		int tmax = mbean.getMax();
		int tinitial = mbean.getInitial();
		int tidleKeepAlive = mbean.getIdleKeepAlive();
		assertTrue("created initial must be " + initial + " but was "
				+ tinitial, tinitial == initial);
		assertTrue("created min must be " + min + " but was " + tmin,
				tmin == min);
		assertTrue("created max must be " + max + " but was " + tmax,
				tmax == max);
		assertTrue("created keep alive must be " + idleKeepAlive + " but was "
				+ tidleKeepAlive, tidleKeepAlive == idleKeepAlive);
                
		admin.readDescriptor();
		// todo this part will fail -- pools don't yet know how to resize
		// themselves
		initial += 5;
		min += 5; 
		max += 5;
		admin.editThreadPool(name, initial, min, max, null);
		admin.sync();
		admin.writeDescriptor();
		tmin = mbean.getMin();
		tmax = mbean.getMax();
		tinitial = mbean.getInitial();
		tidleKeepAlive = mbean.getIdleKeepAlive();
		assertTrue("created initial must be " + initial + " but was "
				+ tinitial, tinitial == initial);
		assertTrue("created min must be " + min + " but was " + tmin,
				tmin == min);
		assertTrue("created max must be " + max + " but was " + tmax,
				tmax == max);
		assertTrue("created keep alive must be " + idleKeepAlive + " but was "
				+ tidleKeepAlive, tidleKeepAlive == idleKeepAlive);
		admin.readDescriptor();
		admin.removeThreadPool(name);
		admin.sync();
		admin.writeDescriptor();
                
		pools = admin.getThreadPools();

		assertTrue("returned set must NOT contain " + pools, !pools
				.contains(new ObjectName(mustContain)));
	}

	/**
     * @param name
     * @param name2
     * @return
	 * @throws Exception 
     */
    private Object getMBean(ObjectName name, Class clazz) throws Exception {
      Context ctx = new InitialContext();
      //  Context ctx = new InitialContext();
      RMIAdaptor rmi = (RMIAdaptor) ctx.lookup("jmx/rmi/RMIAdaptor");
      return ProxyMaker.makeProxy(rmi, clazz, name);
    }

    public void testSMTPProtocol() throws Exception {
		admin.readDescriptor();
		String name = "testSMTP";
		String prettyName = "Test instance of SMTP Protocol created by unit tests";
		ObjectName expectedName = new ObjectName("meldware.mail:type=Protocol,name=testSMTP");
		ObjectName domainGroup = new ObjectName("meldware.mail:type=MailServices,name=DomainGroup,group=Local");
		ObjectName bodyMgr = new ObjectName("meldware.mail:type=MailBodyManager,name=MailBodyManager");
		ObjectName listenerChain = new ObjectName("meldware.mail:type=MailServices,name=MailListenerChain");
		ObjectName userRepository = new ObjectName("meldware.mail:type=MailServices,name=UserRepository,uimanageable=true");
		String sslSecurityDomain=null;
		String serverName="localhost";
		Boolean authRequired = new Boolean(true);
		Boolean authAllowed = new Boolean(true);
		Boolean verifyIdentity = new Boolean(false);
		Long maxMessageSize = new Long(10000);
		Integer blockSize = new Integer(1400);
		Boolean enableTls = new Boolean(false);
		Boolean requireTls = new Boolean(false);
		Boolean requireTlsForAuth = new Boolean(false);
		Boolean requireClientCert = new Boolean(false);
		String postMasterAddress = "noone@localhost";
		Integer maxReceivedHeaders = new Integer(100);
		Integer receivedHeadersThreshold = new Integer(100);
		Integer maxOwnReceivedHeaders = new Integer(10);
		
		admin.createSMTPProtocol(name, prettyName, domainGroup, bodyMgr, listenerChain, userRepository, 
				sslSecurityDomain, serverName, authRequired, authAllowed, verifyIdentity, maxMessageSize, 
				blockSize, enableTls, requireTls, requireTlsForAuth, requireClientCert, postMasterAddress, 
				maxReceivedHeaders, receivedHeadersThreshold, maxOwnReceivedHeaders);
		admin.sync();
		admin.writeDescriptor();
		Set<ObjectName> p = admin.getProtocols();
		assertTrue("we expect the returned set of protocols to contain the added "+expectedName.toString(),
				    p.contains(expectedName));
		
		admin.readDescriptor();
		admin.editSMTPProtocol(name, prettyName, null, null, null, null, null, "foobar", null, null, 
				null, null, null, null, null, null, null, null, null, null, null);
		admin.sync();
		admin.writeDescriptor();
		SMTPProtocolMBean smtp = (SMTPProtocolMBean) getMBean(expectedName, SMTPProtocolMBean.class);
		String tservername = smtp.getServername();
		assertTrue("we expect the servername to equal foobar but it was "+tservername,tservername.equals("foobar"));
		admin.readDescriptor();
		admin.removeProtocol(name);
		admin.sync();
		admin.writeDescriptor();
		p = admin.getProtocols();
		assertTrue("we expect the returned set of protocols to NOT contain the removed "+expectedName.toString(),
			    !p.contains(expectedName));
	}
        
        public void testSSLs() throws Exception {
            ObjectName expectedName = new ObjectName("meldware.mail:service=JaasSecurityDomain,name=test+SSL");
            String domain="test+SSL";
            String keystoreUrl="conf/meldware.store";
            String keystorePass="meldwarerulez123";
            admin.readDescriptor();
            admin.createSSL(domain, keystoreUrl, keystorePass);
            admin.sync();
            admin.writeDescriptor();
            Set<ObjectName> p = admin.getSSLs();
            assertTrue("we expect that the "+expectedName+" will be in "+p.toString(),p.contains(expectedName));
            admin.readDescriptor();
            admin.removeSSL(domain);
            admin.sync();
            admin.writeDescriptor();
            p = admin.getSSLs();
            assertTrue("we expect the returned set of ssls to NOT contain the removed "+expectedName.toString(),
			    !p.contains(expectedName));
        }

        /*
          <mbean code="org.buni.meldware.mail.domaingroup.DomainGroup" name="meldware.mail:type=MailServices,name=DomainGroup,group=Local">
     <attribute name="Postmaster" replace="true" serialDataType="text" trim="true">user@localhost</attribute>
     <attribute name="Domains" replace="true" serialDataType="text" trim="true">
        <domains includes-local-interfaces="true">
          <domain>localhost</domain>
          <domain>meldwaretest.net</domain>
        </domains>
     </attribute>
  </mbean>
*/
        public void testDomainGroup() throws Exception {
            admin.readDescriptor();
            String name="localTest";
            ObjectName expectedName= new ObjectName("meldware.mail:type=DomainGroup,name="+name);
            String postmaster="user@localhost";
            Boolean includesLocal = new Boolean(true);
            List<String> domains = new ArrayList<String>();
            domains.add("localhost");
            admin.createDomainGroup(name,postmaster,domains,includesLocal);
            admin.sync();
            admin.writeDescriptor();
            Set<ObjectName> p = admin.getDomainGroups();
            assertTrue("we expect that the "+expectedName+" will be in "+p.toString(),p.contains(expectedName));
            domains.add("foobar");
            admin.readDescriptor();
            admin.editDomainGroup(name,null,domains,new Boolean(true));
            admin.sync();
            admin.writeDescriptor();
            DomainGroupMBean dg = (DomainGroupMBean) getMBean(expectedName,DomainGroupMBean.class);
            assertTrue("isHostInGroup(foobar) should be true",dg.isHostInGroup("foobar"));
            admin.readDescriptor();
            admin.removeDomainGroup(name);
            admin.sync();
            admin.writeDescriptor();
            p = admin.getDomainGroups();
            assertTrue("we expect that the "+expectedName+" will NOT be in "+p.toString(),!p.contains(expectedName));
        }
        
        public void testSMTPService() throws Exception {
            admin.readDescriptor();
            ObjectName expectedName = new ObjectName("meldware.mail:type=Service,name=TestSMTPService");
            admin.createService("TestSMTPService", "ThreadPoolSMTP", "SMTPProtocol", 9077, "127.0.0.1", (long)99999, (long)99999, null);
            admin.sync();
            admin.writeDescriptor();
            Thread.sleep(10);
            InetAddress a = InetAddress.getByName("127.0.0.1");
            Socket s = new Socket(a, 9077);
            InputStream stream = s.getInputStream();
            int b = stream.read();
            stream.close();
            s.close();
            assertFalse("We expect a > 0 byte to be read", b <1);
            admin.readDescriptor();
            admin.editService("TestSMTPService", null, null, 9078, null, null, null, null);
            admin.sync();
            admin.writeDescriptor();
            ServerMBean service = (ServerMBean) getMBean(expectedName, ServerMBean.class);
            long p = service.getPort();
            assertEquals("The port should be 9078", 9078, p);
            admin.readDescriptor();
            admin.removeService("TestSMTPService");
            admin.sync();
            admin.writeDescriptor();
        }
        
       /*
        public void testJaasUserRepositoryUsersRoles() {
            admin.readDescriptor();
            String name="jaasTest";
            ObjectName expectedName = new ObjectName("meldware.mail:type=UserRepository,name="+jaasTest);
            String 
        }
        */

	/*
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(TestMail.class);
	}
}
