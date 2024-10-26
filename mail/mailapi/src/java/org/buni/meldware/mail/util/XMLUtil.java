/*
 * Bunisoft the Open Source Communications Company
 * Copyright 2007, Bunisoft LLC, and individual contributors as
 * indicated by the @authors tag.  See the copyright.txt in the 
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; version 2.1 of
 * the License.
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

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class XMLUtil {

    public static String toString(Element e) throws 
            ParserConfigurationException, TransformerException {
        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document d = db.newDocument();
        e = (Element) d.importNode(e, true);
        d.appendChild(e);
        
        DOMSource domSource = new DOMSource(d);
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.transform(domSource, result);
        return writer.toString();
    }
    
    /**
     * Converts an XML structure of the form:
     * <pre>
     * &lt;ClassName&gt;
     *   &lt;fieldName&gt;VALUE&lt;/FieldName&gt;
     * &lt;/ClassName&gt;
     * </pre>
     * 
     * to Map object of the form ClassName.fieldName=VALUE
     * 
     * @param e
     * @return
     * @throws ParserConfigurationException
     * @throws TransformerException
     */
    public static Map<String,String> toProperties(Element e) {
        Map<String,String> config = new HashMap<String,String>();
        NodeList eClasses = e.getChildNodes();
        
        for (int i = 0; i < eClasses.getLength(); i++) {
            org.w3c.dom.Node n = eClasses.item(i);
            if (n.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                Element eClass = (Element) n;
                NodeList eFields = eClass.getChildNodes();
                for (int j = 0; j < eFields.getLength(); j++) {
                    org.w3c.dom.Node m = eFields.item(j);
                    if (m.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                        Element eField = (Element) m;
                        String name = eClass.getNodeName() + "." + eField.getNodeName();
                        if (eField.getFirstChild() != null) {
                            String value = eField.getFirstChild().getTextContent();
                            config.put(name, value);
                        }
                    }
                }
            }
        }
        
        return config;
    }
    
}
