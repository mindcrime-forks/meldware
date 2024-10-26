/*
 * Bunisoft the Open Source Communications Company
 * Copyright 2006, Bunisoft LLC.,
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

import java.util.List;

import org.w3c.dom.Element;

/**
 *
 *
 * @author acoliver
 */
 public interface DumDOM {

    //attributes
     void setDeploymentURL(String url);

     String getDeploymentURL();

     void setConfigFile(String configfile);

     String getConfigFile();

    //operations
    /* (non-Javadoc)
     * @see org.buni.meldware.mail.management.DumDOM#start()
     */
     void start();

    /* (non-Javadoc)
     * @see org.buni.meldware.mail.management.DumDOM#stop()
     */
     void stop();

    /* (non-Javadoc)
     * @see org.buni.meldware.mail.management.DumDOM#read()
     */
     void read() throws Exception;

    /* (non-Javadoc)
     * @see org.buni.meldware.mail.management.DumDOM#write()
     */
     void write() throws Exception;

     List<Element> getMBeanAttributes();

     String getMBeanName(Element mbean);

     Element getMBeanAttribute(String name);

     Element createAttribute(String name, String value);

     Object getMBeanAttributeValue(Element attribute);

     void setMBeanAttributeValue(Element attribute, Object value);

     void deleteAttribute(Element attribute);

     void deleteMBean();

     Element findMBeanByName(String name);

     void selectMBean(Element mbean);

     void selectMBeanAttribute(Element attr);

     Element getSelectedMBean();

     Element getSelectedMBeanAttribute();
     
     Element createMBean(String name, String className, String xmbeanpath, String[] attributeNames, Object[] attributeVals,
             String[] proxyNames, boolean[] depends);
     
     public Element createMBean(String name, String className, String interfaceName, String xmbeanpath, String[] attributeNames, Object[] attributeVals,
             String[] proxyNames, boolean[] depends);
     
     Element createMBean(String name, String className, String xmbeanpath, String[] attributeNames, Object[] attributeVals,
             String[] proxyNames, String[] constructorArgTypes, String[] constructorArgs, boolean[] depends);

     Element createMBean(String name, String className, String interfaceName, String xmbeanpath, String[] attributeNames, Object[] attributeVals,
             String[] proxyNames, String[] constructorArgTypes, String[] constructorArgs, boolean[] depends);
     
     
     Element parse(String val);
     
    /* (non-Javadoc)
     * @see org.buni.meldware.mail.management.DumDOM#synch()
     */
     void sync() throws Exception;
     
     void lilTestyWesty();

    /**
     * @param attr_body_mgr
     * @param string
     * @param b
     */
    Element createDependency(String attrName, String string, boolean proxy);

}