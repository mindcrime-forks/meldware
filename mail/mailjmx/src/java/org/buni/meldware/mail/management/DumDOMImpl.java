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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.management.Attribute;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanServer;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.buni.meldware.mail.util.MMJMXUtil;
import org.jboss.mx.loading.RepositoryClassLoader;
import org.jboss.mx.util.MBeanProxyExt;
import org.jboss.system.ServiceControllerMBean;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 *
 * @author acoliver
 */
public class DumDOMImpl implements DumDOM {

        private String filename;

        private File file;

        Document doc;

        List<Element> mbeans;

        Element selectedMbean;
        Element selectedAttribute;

        ChangeList changes;

        private MBeanServerConnection server;
        private ServiceControllerMBean serviceController;

        private String url;

        public DumDOMImpl() {
            this.changes = new ChangeList();
        }
        
        //attributes
        /* (non-Javadoc)
         * @see org.buni.meldware.mail.management.DumDOM#setDeploymentURL(java.lang.String)
         */
        public void setDeploymentURL(String url) {
            this.url = url;
        }
        
        /* (non-Javadoc)
         * @see org.buni.meldware.mail.management.DumDOM#getDeploymentURL()
         */
        public String getDeploymentURL() {
            return this.url;
        }
        
        /* (non-Javadoc)
         * @see org.buni.meldware.mail.management.DumDOM#setConfigFile(java.lang.String)
         */
        public void setConfigFile(String configfile) {
             this.filename = configfile;
        }
        
        /* (non-Javadoc)
         * @see org.buni.meldware.mail.management.DumDOM#getConfigFile()
         */
        public String getConfigFile() {
            return this.filename;
        }
        
        //operations
        /* (non-Javadoc)
         * @see org.buni.meldware.mail.management.DumDOM#start()
         */
        /* (non-Javadoc)
         * @see org.buni.meldware.mail.management.DumDOM#start()
         */
        public void start() {
            jbConnect();
            serviceController = (ServiceControllerMBean)
            MBeanProxyExt.create(ServiceControllerMBean.class,
               ServiceControllerMBean.OBJECT_NAME, (MBeanServer) server);
        }
        
        /* (non-Javadoc)
         * @see org.buni.meldware.mail.management.DumDOM#stop()
         */
        /* (non-Javadoc)
         * @see org.buni.meldware.mail.management.DumDOM#stop()
         */
        public void stop() {
            
        }

        /* (non-Javadoc)
         * @see org.buni.meldware.mail.management.DumDOM#read()
         */
        /* (non-Javadoc)
         * @see org.buni.meldware.mail.management.DumDOM#read()
         */
        public void read() throws Exception {
            this.file = new File(filename);
            this.changes = new ChangeList();
            DocumentBuilder bld = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            doc = bld.parse(file);

            NodeList lst = doc.getElementsByTagName("mbean");
            mbeans = new ArrayList<Element>(lst.getLength());

            for (int i = 0; i < lst.getLength(); i++) {
                mbeans.add((Element) lst.item(i));
            }
            System.out.println("DONE");
        }

        /* (non-Javadoc)
         * @see org.buni.meldware.mail.management.DumDOM#write()
         */
        /* (non-Javadoc)
         * @see org.buni.meldware.mail.management.DumDOM#write()
         */
        public void write() throws Exception {
            suspend(this.url);
            FileOutputStream stream = new FileOutputStream(file);
            TransformerFactory tFactory = TransformerFactory.newInstance();
            Transformer transformer = tFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(stream);
            transformer.transform(source, result);
            stream.close();
            resume(this.url);
           // resume("/Users/acoliver/projects/jboss/jboss-mail/head/jboss-4.03SP1-ejb3rc5/server/default/deploy/mail.ear/");
        }

        /* (non-Javadoc)
         * @see org.buni.meldware.mail.management.DumDOM#getMBeanAttributes()
         */
        public List<Element> getMBeanAttributes() {
            Element mbean = this.selectedMbean;
            NodeList list = mbean.getChildNodes();
            List<Element> retval = new ArrayList<Element>(list.getLength());
            for (int i = 0; i < list.getLength(); i++) {
                Node n = list.item(i);
                if (n instanceof Element) {
                    Element e = (Element) n;
                    if (e.getNodeName().equals("attribute")) {
                        retval.add(e);
                    } else if (e.getNodeName().equals("depends")) {
                        String oan = e.getAttribute("optional-attribute-name");
                        if (oan != null && !oan.equals("")) {
                            retval.add(e);
                        }
                    }
                }
            }
            return retval;
        }
        
        /* (non-Javadoc)
         * @see org.buni.meldware.mail.management.DumDOM#getMBeanName(org.w3c.dom.Element)
         */
        public String getMBeanName(Element mbean) {
            return mbean.getAttribute("name");
        }

        /* (non-Javadoc)
         * @see org.buni.meldware.mail.management.DumDOM#getMBeanAttribute(java.lang.String)
         */
        public Element getMBeanAttribute(String name) {
            List<Element> attrs = getMBeanAttributes();
            for (int i = 0; i < attrs.size(); i++) {
                Element e = attrs.get(i);
                if (e.getNodeName().equals("attribute")) {
                    if (e.getAttribute("name").equals(name)) {
                        return e;
                    }
                } else if (e.getNodeName().equals("depends")) {
                    if (e.getAttribute("optional-attribute-name").equals(name)) {
                        return e;
                    }
                }
            }
            return null;
        }
        
        public Element createMBean(String name, String className, String interfaceName, String xmbeanpath, String[] attributeNames, Object[] attributeVals,
                String[] proxyNames, boolean[] depends) { 
            return createMBean(name,className,interfaceName,xmbeanpath,attributeNames,attributeVals,proxyNames,null,null,depends);
        }
        
        public Element createMBean(String name, String className, String xmbeanpath, String[] attributeNames, Object[] attributeVals,
                                   String[] proxyNames, boolean[] depends) { 
            return createMBean(name,className,xmbeanpath,attributeNames,attributeVals,proxyNames,null,null,depends);
        }

        public Element createMBean(String name, String className, String xmbeanpath, String[] attributeNames, Object[] attributeVals,
                String[] proxyNames, String[] constructorArgTypes, String[] constructorArgs, boolean[] depends) {
            return createMBean(name, className, null, xmbeanpath, attributeNames, attributeVals, proxyNames, constructorArgTypes, constructorArgs, depends);
        }

        public Element createMBean(String name, String className, String interfaceName, String xmbeanpath, String[] attributeNames, Object[] attributeVals,
                                   String[] proxyNames, String[] constructorArgTypes, String[] constructorArgs, boolean[] depends) {
            NodeList lst = doc.getElementsByTagName("server");
            Element root = (Element)lst.item(0);
            Element mbean = doc.createElement("mbean");
            mbean.setAttribute("name", name);
            mbean.setAttribute("code", className);
            if(interfaceName != null) {
                mbean.setAttribute("interface", interfaceName);
            }
            if(xmbeanpath != null) {
                mbean.setAttribute("xmbean-attr", xmbeanpath);
            }
            if (constructorArgs != null) {
                String elementName="constructor";
                Element cons = doc.createElement(elementName);
                
                for (int i = 0; i < constructorArgTypes.length; i++) {
                    Element arg = doc.createElement("arg");
                    arg.setAttribute("type",constructorArgTypes[i]);
                    arg.setAttribute("value",constructorArgs[i]);
                    cons.appendChild(arg);
                }
                mbean.appendChild(cons);
            }
            for (int i = 0; i < attributeNames.length; i++) {
                String elementName = "attribute";
                if(depends[i] == true) {
                    elementName = "depends";
                }
                Element attr = doc.createElement(elementName);
                if(depends[i] == true ) {
                    if (attributeNames[i] != null) {
                        attr.setAttribute("optional-attribute-name", attributeNames[i]);
                    }
                    if (proxyNames[i] != null) {
                        attr.setAttribute("proxy-type",proxyNames[i]);
                    }
                } else {
                    attr.setAttribute("name", attributeNames[i]);
                } 
                if (attributeVals[i] instanceof String) {
                    attr.setTextContent((String)attributeVals[i]);
                } else if (attributeVals[i] instanceof Element) {
                    Element v = (Element)attributeVals[i];
                    Node n = this.doc.importNode(v, true);
                    attr.appendChild(n);
                } else {
                    throw new RuntimeException("Illegal type for attribute value");
                }
                mbean.appendChild(attr);
            }
            root.appendChild(mbean);
            mbeans.add(mbean);
            changes.addChange(name, "", ChangeElement.MBEAN_CREATED);
            return mbean;
        }

        /* (non-Javadoc)
         * @see org.buni.meldware.mail.management.DumDOM#createAttribute(java.lang.String, java.lang.String)
         */
        public Element createAttribute(String name, String value) {
            Element mbean = this.selectedMbean;
            Element attribute = doc.createElement("attribute");
            attribute.setAttribute("name", name);
            this.setMBeanAttributeValue(attribute, value);
            mbean.appendChild(attribute);
            changes.addChange(this.getMBeanName(mbean), name, ChangeElement.ATTRIBUTE_CREATED);
            return attribute;
        }

        /* (non-Javadoc)
         * @see org.buni.meldware.mail.management.DumDOM#getMBeanAttributeValue(org.w3c.dom.Element)
         */
        public Object getMBeanAttributeValue(Element attribute) {
            NodeList list = attribute.getChildNodes();
            if (list.getLength() > 0) {
                for (int i= 0; i < list.getLength(); i++) {
                    Node n = list.item(i);
                    if (n.getNodeType() == Node.ELEMENT_NODE) {
                        Element e = (Element) n;
                        return e;
                    }
                }
            }
            return attribute.getFirstChild().getTextContent();
        }

        /* (non-Javadoc)
         * @see org.buni.meldware.mail.management.DumDOM#setMBeanAttributeValue(org.w3c.dom.Element, java.lang.String)
         */
        public void setMBeanAttributeValue(Element attribute, Object value) {
            if (value instanceof Element) {
          //      Node n = this.doc.adoptNode(((Element)value));
                Node n = this.doc.importNode((Element)value, true);
                Node child = attribute.getFirstChild();
                while (child != null) { 
                    Node oldChild = child;
                    child = oldChild.getNextSibling();
                    if (oldChild.getNodeType() == Node.ELEMENT_NODE ) {
                        attribute.replaceChild(n,oldChild);
                        break;
                    }
                }
                if (child == null) {
                    attribute.appendChild(n);   
                }
            } else {
                Node mba = attribute.getFirstChild();
                if (mba == null) {
                    mba = doc.createTextNode(value.toString());
                    attribute.appendChild(mba);
                    return;    
                } else {
                    mba.setTextContent(value.toString());
                }
            }
            String theName = attribute.getAttribute("name");
            theName = (theName == null || theName.equals("")) ? attribute.getAttribute("optional-attribute-name") : theName;
            changes.addChange(getMBeanName(this.selectedMbean), theName, ChangeElement.ATTRIBUTE_CHANGED);
        }

        /* (non-Javadoc)
         * @see org.buni.meldware.mail.management.DumDOM#deleteAttribute(org.w3c.dom.Element)
         */
        public void deleteAttribute(Element attribute) {
            attribute.getParentNode().removeChild(attribute);
            changes.addChange(getMBeanName(this.selectedMbean), attribute.getAttribute("name"), ChangeElement.ATTRIBUTE_REMOVED);
        }

        /* (non-Javadoc)
         * @see org.buni.meldware.mail.management.DumDOM#deleteMBean()
         */
        public void deleteMBean() {
            Element mbean = this.selectedMbean;
            mbean.getParentNode().removeChild(mbean);
            mbeans.remove(mbean);
            changes.addChange(getMBeanName(this.selectedMbean), "", ChangeElement.MBEAN_REMOVED);
        }

        /* (non-Javadoc)
         * @see org.buni.meldware.mail.management.DumDOM#findMBeanByName(java.lang.String)
         */
        public Element findMBeanByName(String name) {
            for (int i = 0; i < mbeans.size(); i++) {
                Element e = mbeans.get(i);
                if (getMBeanName(e).equals(name)) {
                    return e;
                }
            }
            return null;
        }

        /* (non-Javadoc)
         * @see org.buni.meldware.mail.management.DumDOM#selectMBean(org.w3c.dom.Element)
         */
        public void selectMBean(Element mbean) {
            this.selectedMbean = mbean;
        }
        
        /* (non-Javadoc)
         * @see org.buni.meldware.mail.management.DumDOM#selectMBeanAttribute(org.w3c.dom.Element)
         */
        public void selectMBeanAttribute(Element attr) {
            this.selectedAttribute = attr;
        }

        /* (non-Javadoc)
         * @see org.buni.meldware.mail.management.DumDOM#getSelectedMBean()
         */
        public Element getSelectedMBean() {
            return this.selectedMbean;
        }

        /* (non-Javadoc)
         * @see org.buni.meldware.mail.management.DumDOM#getSelectedMBeanAttribute()
         */
        public Element getSelectedMBeanAttribute() {
            return this.selectedAttribute;
        }    
        
        /* (non-Javadoc)
         * @see org.buni.meldware.mail.management.DumDOM#synch()
         */
        /* (non-Javadoc)
         * @see org.buni.meldware.mail.management.DumDOM#synch()
         */
        public void sync() throws Exception {
            ChangeList list = this.changes;
            Set<String> set = list.changes.keySet();
            Iterator<String> keys = set.iterator();
            while (keys.hasNext()) {
                String key = keys.next();
                List<ChangeElement> changeElems = list.changes.get(key);
                Iterator<ChangeElement> changes = changeElems.iterator();
                while (changes.hasNext()) {
                    ChangeElement change = changes.next();
                    if (change.getType() == ChangeElement.ATTRIBUTE_CHANGED || change.getType() == ChangeElement.ATTRIBUTE_CREATED) {
                        String attr = change.getAttribute();
                        String mbean = change.getMBean(); // should be the same as key but its more "authorative"
                        ObjectName on = new ObjectName(mbean);
                        this.selectMBean(this.findMBeanByName(key));
                        Object val = this.getMBeanAttributeValue(this.getMBeanAttribute(attr));
                        if (val instanceof Element) {
                            //Attribute attribute = new Attribute(attr, val);
                            setAttributeWithType(on,attr,((Element)val));
                        } else {
                            //Attribute attribute = new Attribute(attr, val.toString());
                            setAttributeWithType(on, attr, val.toString());
                        }
                        
                        
                     //   server.setAttribute(on, attribute);
                    } else if (change.getType() == ChangeElement.MBEAN_REMOVED) {
                      //  RMIAdaptorExt serverext = (RMIAdaptorExt)server;
                        String mbean = change.getMBean(); // should be the same as key but its more "authorative"
                        ObjectName on = new ObjectName(mbean);
                        this.selectMBean(this.findMBeanByName(key));
                        try {
                         //   server.invoke(on, "stop", new Object[]{}, new String[]{});
                        //    server.invoke(on, "destroy", new Object[]{}, new String[]{});
                            this.serviceController.stop(on);
                            this.serviceController.destroy(on);
                            this.serviceController.remove(on);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        } 
                    } else if (change.getType() == ChangeElement.MBEAN_CREATED) {
                        RepositoryClassLoader ucl = (RepositoryClassLoader)DumDOMImpl.class.getClassLoader(); //Thread.currentThread().getContextClassLoader();//this.getClass().getClassLoader();
                        ObjectName cl = ucl.getObjectName();
                        String mbean = change.getMBean();
                        ObjectName on = new ObjectName(mbean);
                        this.selectMBean(this.findMBeanByName(key));
                        this.serviceController.install(this.getSelectedMBean(), cl);
                        this.serviceController.create(on);
                        this.serviceController.start(on);
                    } else {
                        throw new RuntimeException("UNSUPPORTED CHANGE TYPE");
                    }
                }
            }
        }

        /**
         * @param on
         * @param attr
         */
        private void setAttributeWithType(ObjectName on, String attr, Object val) throws Exception {
           short type = getAttributeType(on, attr);
           Attribute attribute = null;
           switch (type) {
           case 1:
               attribute = new Attribute(attr,val.toString());
               server.setAttribute(on, attribute);
               break;
           case 2: 
               attribute = new Attribute(attr, Long.parseLong(val.toString()));
               server.setAttribute(on, attribute); 
               break;
           case 3:
               attribute = new Attribute(attr, Integer.parseInt(val.toString()));
               server.setAttribute(on, attribute);
               break;
           case 4:
               Element e = null;
               if (val instanceof String) {
                   DocumentBuilder bld = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                   Document adoc = bld.parse(new ByteArrayInputStream(val.toString().getBytes()));
                   e = (Element)adoc.getFirstChild();
               } else {
                   e = (Element)val;
               }
               
               attribute = new Attribute(attr,e);
               server.setAttribute(on,attribute);         
               
               break;
           case 5:
               attribute = new Attribute(attr, Boolean.parseBoolean(val.toString()));
               server.setAttribute(on, attribute);
               break;
           case 6:
               String typename = getTypeName(on, attr);
               Class<?> clazz = Thread.currentThread().getContextClassLoader().loadClass(typename);
               Object o = MMJMXUtil.getMBean(new ObjectName(val.toString()), clazz);
               Object w = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{clazz}, new WrappingInvocationHander(o));
               attribute = new Attribute(attr, w);
               server.setAttribute(on, attribute);
               break;
           case 7:
               ObjectName aon = new ObjectName(val.toString());
               attribute = new Attribute(attr, aon);
               server.setAttribute(on, attribute);
               break;
           default: 
        	   throw new RuntimeException("Unknown attribute type "+type+","+attr+","+val);
           }
            
        }
        
        

        /**
         * @param on
         * @param attr
         * @return
         */
        private short getAttributeType(ObjectName on, String attr) throws Exception {
            MBeanInfo info = server.getMBeanInfo(on);
            MBeanAttributeInfo[] mai = info.getAttributes();
            for (int i = 0; i < mai.length; i++) {
                if (mai[i].getName().equals(attr)) {
                    if(mai[i].getType().equals(String.class.getName())) {
                        return 1;
                    } else if (mai[i].getType().equals(Long.class.getName()) || mai[i].getType().equals(long.class.getName())) {
                        return 2;
                    } else if (mai[i].getType().equals(Integer.class.getName()) || mai[i].getType().equals(int.class.getName())) {
                        return 3;
                    } else if (mai[i].getType().equals(Element.class.getName())) {
                        return 4;
                    } else if (mai[i].getType().equals(Boolean.class.getName()) || mai[i].getType().equals(boolean.class.getName())) {
                        return 5;
                    } else if (mai[i].getType().startsWith("org.buni")) { // kidn of a kluge...we're assuming this is an ONAME based on this
                        return 6;
                    } else if (mai[i].getType().equals(ObjectName.class.getName())) {
                        return 7;
                    } else {
                    	System.out.println("******************UNKNOWN TYPE***************"+mai[i].getType());
                    }
                }
            }
            return 0;
        }
        
        private String getTypeName(ObjectName on, String attr) throws Exception {
            MBeanInfo info = server.getMBeanInfo(on);
            MBeanAttributeInfo[] mai = info.getAttributes();
            for (int i = 0; i < mai.length; i++) {
                if (mai[i].getName().equals(attr)) {
                    return mai[i].getType();
                }
            }
            return null;
        }
        
        private void resume(String string) {
            try {
                ObjectName on = new ObjectName("jboss.deployment:type=DeploymentScanner,flavor=URL");
                server.invoke(on,"resumeDeployment",new Object[] {(new URL("file:"+string)),new Boolean(true)}, new String[] {URL.class.getName(),boolean.class.getName()});
            } catch (Exception e) {
               throw new RuntimeException(e);
            }         
        }

        private void suspend(String string) {
            try {
                ObjectName on = new ObjectName("jboss.deployment:type=DeploymentScanner,flavor=URL");
                server.invoke(on,"suspendDeployment",new Object[] {(new URL("file:"+string))}, new String[] {URL.class.getName()});
            } catch (Exception e) {
               throw new RuntimeException(e);
            } 
        }

        public void jbConnect() {
            try {
                this.server = MMJMXUtil.locateJBoss();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }

        /* (non-Javadoc)
         * @see org.buni.meldware.mail.management.DumDOM#lilTestyWesty()
         */
        public void lilTestyWesty() {
            try {
            DumDOMImpl dd = this;
            dd.read();
       //     dd.jbConnect();

            // Element e = dd.findMBeanByName("meldware.mail:service=JaasSecurityDomain,domain=Mail+SSL");
            Element e = dd.findMBeanByName("meldware.mail:type=MailServices,name=SMTPProtocol");
            System.out.println(dd.getMBeanName(e));
            dd.selectMBean(e);
            Element attr = dd.getMBeanAttribute("Servername");
            String val = dd.getMBeanAttributeValue(attr).toString();
            System.out.println("servername was " + val + " changing to localhost.localdomain");
            dd.setMBeanAttributeValue(attr, "localhost.localdomain");
            // Element attr = dd.getMBeanAttribute("KeyStorePass");
            // String val = dd.getMBeanAttributeValue(attr);
            // System.out.println("KeyStorePass="+val);
            // dd.setMBeanAttributeValue(attr,"foobar");
            // val = dd.getMBeanAttributeValue(attr);
            // System.out.println("KeyStorePass="+val);
            // dd.deleteAttribute(attr);
            dd.sync();
            dd.write();
            System.out.println("done");      
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        /* (non-Javadoc)
         * @see org.buni.meldware.mail.management.DumDOM#createDependency(java.lang.String, java.lang.String, boolean)
         */
        public Element createDependency(String attrName, String value, boolean proxy) {
            Element mbean = this.selectedMbean;
            Element attribute = doc.createElement("depends");
            if (attrName != null) {
                attribute.setAttribute("optional-attribute-name", attrName);
            }
            if (proxy) {
                attribute.setAttribute("proxy-type", "attribute");
            }
            attribute.setTextContent(value);
          //  this.setMBeanAttributeValue(attribute, value);
            mbean.appendChild(attribute);
            changes.addChange(this.getMBeanName(mbean), attrName, ChangeElement.ATTRIBUTE_CREATED);
            return attribute;
            
        }

        public Element parse(String val) {
            try {
                DocumentBuilder bld = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                Document adoc = bld.parse(new ByteArrayInputStream(val.getBytes()));
                return (Element)adoc.getFirstChild();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        
        /*
        public static void main(String args[]) {
            try {
                DumDOMImpl dd = new DumDOMImpl(args[0]);
                dd.read();
                dd.jbConnect();

                // Element e = dd.findMBeanByName("meldware.mail:service=JaasSecurityDomain,domain=Mail+SSL");
                Element e = dd.findMBeanByName("meldware.mail:type=MailServices,name=SMTPProtocol");
                System.out.println(dd.getMBeanName(e));
                dd.selectMBean(e);
                Element attr = dd.getMBeanAttribute("Servername");
                String val = dd.getMBeanAttributeValue(attr);
                System.out.println("servername was " + val + " changing to localhost.localdomain");
                dd.setMBeanAttributeValue(attr, "localhost.localdomain");
                // Element attr = dd.getMBeanAttribute("KeyStorePass");
                // String val = dd.getMBeanAttributeValue(attr);
                // System.out.println("KeyStorePass="+val);
                // dd.setMBeanAttributeValue(attr,"foobar");
                // val = dd.getMBeanAttributeValue(attr);
                // System.out.println("KeyStorePass="+val);
                // dd.deleteAttribute(attr);
                dd.synch();
                dd.write();
                System.out.println("done");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        */
    }

    class ChangeList {
        Map<String, List<ChangeElement>> changes;

        public ChangeList() {
            this.changes = new HashMap<String, List<ChangeElement>>();
        }

        public void addChange(String mbean, String attribute, int type) {
            List<ChangeElement> list = this.changes.get(mbean);
            if (list == null) {
                list = new ArrayList<ChangeElement>();
                changes.put(mbean, list);
            }
            ChangeElement element = new ChangeElement(mbean, attribute, type);
            if (!list.contains(element)) {
                list.add(element);
            }
        }
    }

    class ChangeElement {
        public static final int ATTRIBUTE_CHANGED = 0;

        public static final int ATTRIBUTE_REMOVED = 1;

        public static final int ATTRIBUTE_CREATED = 2;

        public static final int MBEAN_REMOVED = 3;
        
        public static final int MBEAN_CREATED = 4;

        int type = 0;

        String mbean;

        String attribute;

        public ChangeElement(String mbean, String attribute, int type) {
            this.mbean = mbean;
            this.attribute = attribute;
            this.type = type;
        }

        public String getMBean() {
            return mbean;
        }

        public String getAttribute() {
            return attribute;
        }

        public int getType() {
            return type;
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof ChangeElement)) {
                return false;
            }
            ChangeElement ce = (ChangeElement) obj;
            if (ce.getMBean().equals(this.getMBean()) && ce.getAttribute().equals(this.getAttribute())
                    && ce.getType() == this.getType()) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            int code = this.getMBean().hashCode() << 4;
            code += this.getAttribute().hashCode() << 2;
            code += (byte) this.getType();
            return code;
        }
    }

    //This gets around some bug in MBean server that insists on calling toString on these which the remote service doesn't implement as a mgmt func
    //all this does is wrap the proxy with another proxy that has a tenuated toString that just returns a const obj address (set at construction)
    //I realize I'm going to hell for this hack!
   class WrappingInvocationHander implements InvocationHandler {
       Object o;
       Object r;
        WrappingInvocationHander(Object o) {
            this.o = o;
            r = new Object();
        }
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (method.getName().equals("toString")) {
                return r.toString();
            }
            return method.invoke(o,args);
        }

    }

