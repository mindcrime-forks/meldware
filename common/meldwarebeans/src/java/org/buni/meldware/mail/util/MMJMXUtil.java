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
package org.buni.meldware.mail.util;

import java.util.Iterator;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.MBeanServerInvocationHandler;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.jboss.mx.util.MBeanServerLocator;

/**
 * Utility to handle JMX stuff so that I don't have to think very hard about it
 * 
 * @author Andrew C. Oliver
 * @version $Revision: 1.1 $
 */
public class MMJMXUtil {

    /**
     * Looks up mbean and returns a dynamic proxy
     * 
     * @param jmxName
     * @param clazz
     * @return
     * @throws Exception
     */
    public static <T> T getMBean(String jmxName, Class<T> clazz) {
        MBeanServer server = MBeanServerLocator.locateJBoss();
        ObjectName objectName;
        try {
            objectName = new ObjectName(jmxName);
            @SuppressWarnings("unchecked")
            T t = (T) MBeanServerInvocationHandler.newProxyInstance(server,
                    objectName, clazz, false);
            return t;
        } catch (MalformedObjectNameException e) {
            throw new JMXLookupException("Unable to get MBean: " + jmxName, e);
        } catch (NullPointerException e) {
            throw new JMXLookupException("Unable to get MBean: " + jmxName, e);
        }

    }

    /**
     * Looks up mbean and returns a dynamic proxy, using an ObjectName.
     * 
     * @param jmxName
     * @param clazz
     * @return
     * @throws Exception
     */
    public static <T> T getMBean(ObjectName objectName, Class<T> clazz) {
        MBeanServer server = MBeanServerLocator.locateJBoss();
        @SuppressWarnings("unchecked")
        T t = (T) MBeanServerInvocationHandler.newProxyInstance(server,
                objectName, clazz, false);
        return t;
    }

    /**
     * @param JMX
     *            name of the MBean
     * @return Object implementing ServiceProxy
     */
    //public static Object getService(String key) throws Exception {
    //    ObjectName name = new ObjectName(key);
    //    return getService(name);
    //}
    /**
     * @return the MBeanServer whcih has "jboss" as the default domain
     */
    public static MBeanServer locateJBoss() {
        for (Iterator i = MBeanServerFactory.findMBeanServer(null).iterator(); i
                .hasNext();) {
            MBeanServer server = (MBeanServer) i.next();
            if (server.getDefaultDomain().equals("jboss"))
                return server;
        }
        throw new IllegalStateException("JBoss MBeanServer not found!");
    }
}