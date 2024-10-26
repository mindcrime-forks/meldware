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
package org.buni.meldware.mail;

import java.io.File;
import java.util.Properties;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanInfo;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import org.buni.meldware.mail.domaingroup.DomainGroup;
import org.buni.meldware.mail.maillistener.SysOutMailListener;
import org.buni.meldware.mail.userrepository.StaticUserRepository;
import org.buni.meldware.mail.util.MMJMXUtil;
import org.jboss.system.server.ServerConfig;
import org.jboss.system.server.ServerImpl;

/**
 * Just some plumbing used to run unit tests outside of the appserver.
 * 
 * @author Andrew C. Oliver
 */
public class MBeanServerUtil {

    private static boolean alreadyrun;

    private static boolean sysoutreg;

    private static boolean surreg;

    private static boolean domaingroupreg;

    
    public static void configureMBeanServer() throws IllegalStateException, Exception
    {
        ServerImpl impl = new ServerImpl();
        System.out.println("got the server impl");        
        Properties prop = new Properties();
        String jbossHome = System.getenv("JBOSS_HOME");
        prop.setProperty(ServerConfig.HOME_DIR, jbossHome);
        prop.setProperty(ServerConfig.ROOT_DEPLOYMENT_FILENAME,
                jbossHome + "/server/default/conf/jboss-service.xml");
        prop.setProperty(ServerConfig.SERVER_CONFIG_URL, "file:"
                + (new File(".")).getAbsolutePath());
        impl.init(prop);
        System.out.println("impl.init is finished");
        impl.start();
    }
    
    public static void configureMBeanServerFactory()
            throws IllegalStateException, Exception {
        System.out.println("configureMBeanServerFactory");
        if (alreadyrun)
            return;
        System.out.println("not returning from already run");
        //server = MBeanServerFactory.createMBeanServer();
        //server.setAttribute();
        ServerImpl impl = new ServerImpl();
        System.out.println("got the server impl");
        Properties prop = new Properties();
        prop.setProperty(ServerConfig.HOME_DIR, (new File("."))
                .getAbsolutePath());
        prop.setProperty(ServerConfig.ROOT_DEPLOYMENT_FILENAME,
                "src/META-INF/depends-service.xml");
        prop.setProperty(ServerConfig.SERVER_CONFIG_URL, "file:"
                + (new File(".")).getAbsolutePath());
        System.out.println("done setting up properties");
        impl.init(prop);
        System.out.println("impl.init is finished");
        impl.start();
        System.out.println("impl.start done");
        alreadyrun = true;
        System.out.println("done configureMBeanServerFactory");
    }

//    public static void setupSysout() throws Exception {
//        registerStaticUserRepository();
//        registerSysOutMailListener();
//        registerSMTPProtocol();
//    }

    public static void registerStaticUserRepository() throws Exception {
        System.out.println("register static user repository");
        if (surreg)
            return;
        MBeanServer mbserver = MMJMXUtil.locateJBoss();
        ObjectName oname = new ObjectName(
                "meldware.mail:type=MailServices,name=UserRepository");
        StaticUserRepository sur = new StaticUserRepository();
        sur.addUser("acoliver", "test");
        sur.addUser("test", "testpw");
        sur.addUser("jboss", "jbosspw");
        mbserver.registerMBean(sur, oname);
        surreg = true;
    }

    public static void registerSysOutMailListener() throws Exception {
        if (sysoutreg)
            return;
        registerSysOutMailListener("meldware.mail:type=MailServices,name=MailListener");
        sysoutreg = true;
    }

    public static void registerSysOutMailListener(String name) throws Exception {
        MBeanServer mbserver = MMJMXUtil.locateJBoss();
        System.out.println("before object name construction");
        ObjectName oname = new ObjectName(name);
        MBeanInfo info = null;
        System.out.println("after object name construction");
        try {
            info = mbserver.getMBeanInfo(oname);
        } catch (Exception e) {
        }
        if (info == null) {
            SysOutMailListener soml = new SysOutMailListener();
            mbserver.registerMBean(soml, oname);
        }
    }

    public static void registerSysOutMailListeners(String[] mls)
            throws Exception {
        for (int k = 0; k < mls.length; k++) {
            registerSysOutMailListener(mls[k]);
        }
    }

    public static void registerDomainGroup() throws Exception {
        if (domaingroupreg)
            return;
        MBeanServer mbserver = MMJMXUtil.locateJBoss();
        ObjectName oname = new ObjectName(
                "meldware.mail:type=MailServices,name=DomainGroup,group=Local");
        DomainGroup dg = new DomainGroup();
        dg.add("badmojo");
        dg.add("localhost");
        dg.add("badmojo.superlinksoftware.com");
        dg.add("superlinksoftware.com");
        mbserver.registerMBean(dg, oname);
        domaingroupreg = true;
    }


    /*
     * public static void registerTwiddleProtocol() throws Exception { if
     * (twidreg) return; MBeanServer mbserver = MMJMXUtil.locateJBoss();
     * ObjectName oname = new
     * ObjectName("meldware.mail:type=MailServices,name=TwiddleProtocol");
     * TwiddleProtocol tp = new TwiddleProtocol(); //
     * tp.setProperties(createProps()); mbserver.registerMBean(tp, oname);
     * twidreg = true; }
     */

    public static void startServerService(Server bean) throws Exception {
        bean.startService();
    }

    public static void main(String[] args) throws Exception {
        MBeanServerUtil.configureMBeanServerFactory();
    }

    public static void unregister(String name) throws InstanceNotFoundException, MBeanRegistrationException, MalformedObjectNameException, NullPointerException {
        
        MBeanServer mbserver = MMJMXUtil.locateJBoss();
        ObjectName oname = new ObjectName(name);
        mbserver.unregisterMBean(oname);
        
    }

    public static void register(Object o, String name) throws MalformedObjectNameException, NullPointerException, InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException  {
        
        MBeanServer mbserver = MMJMXUtil.locateJBoss();
        ObjectName oname = new ObjectName(name);
        mbserver.registerMBean(o, oname);
        
    }
    
}
