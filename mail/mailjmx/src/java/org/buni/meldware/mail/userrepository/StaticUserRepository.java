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
package org.buni.meldware.mail.userrepository;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.buni.meldware.mail.util.Base64;
import org.jboss.logging.Logger;
import org.jboss.system.ServiceMBean;
import org.jboss.system.ServiceMBeanSupport;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This UserRepository is merely configured via the JBoss XML Mbean config with &lt;users&gt;,
 * &lt;user&gt;, &lt;id&gt;, and &lt;password&gt; elements.  These are statically configured or can be 
 * ammended via the JMX console.
 * @author Andrew C. Oliver 
 * @version $Revision: 1.3 $
 */ 
public class StaticUserRepository extends ServiceMBeanSupport implements
        ServiceMBean, StaticUserRepositoryMBean, Serializable {

    private static final long serialVersionUID = 3258698723180950328L;

    private Map<String,String> users;

    private static final Logger jblog = Logger
            .getLogger(StaticUserRepository.class);

    public StaticUserRepository() {
        users = new HashMap<String,String>();
    }

    /* (non-Javadoc)
     * @see org.buni.meldware.mail.userrepository.StaticUserRepositoryMBean#setUsers(org.w3c.dom.Element)
     */
    public void setUsers(Element users) {
        NodeList nodes = users.getElementsByTagName("user");
        Map<String, String> pusers = new HashMap<String,String>(nodes.getLength());
        for (int k = 0; k < nodes.getLength(); k++) {
            String pname = null;
            String pval = null;
            Node node = nodes.item(k);
            NodeList subnodes = node.getChildNodes();
            for (int x = 0; x < subnodes.getLength(); x++) {
                Node subnode = subnodes.item(x);
                if (subnode.getNodeName().equals("id")) {
                    pname = subnode.getFirstChild().getNodeValue();
                } else if (subnode.getNodeName().equals("password")) {
                    pval = subnode.getFirstChild().getNodeValue();
                } else {
                    throw new RuntimeException(
                            "user id and password pairs are all that are needed/permitted");
                }
            }
            if (pname == null || pval == null) {
                throw new RuntimeException(
                        "users need passwords and passwords need users...");
            }
            pusers.put(pname, pval);
        }
        this.users = pusers;
    }

    /* (non-Javadoc)
     * @see org.buni.meldware.mail.userrepository.StaticUserRepositoryMBean#getUsers()
     */
    public Element getUsers() {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {

            e.printStackTrace();
            return null;
        }
        DOMImplementation impl = builder.getDOMImplementation();

        DocumentType DOCTYPE = impl.createDocumentType("non", "non", "non");
        Document doc = impl.createDocument("non", "non", DOCTYPE);
        Element retval = doc.createElement("users");
        Iterator i = null;
        for (i = users.keySet().iterator(); i.hasNext();) {
            String key = (String) i.next();
            String val = (String) users.get(key);
            Element user = doc.createElement("user");
            Element id = doc.createElement("id");
            Element pw = doc.createElement("password");
            id.appendChild(doc.createTextNode(key));
            pw.appendChild(doc.createTextNode(val));
            user.appendChild(id);
            user.appendChild(pw);
            retval.appendChild(user);
        }

        return retval;
    }

    /* (non-Javadoc)
     * @see org.buni.meldware.mail.userrepository.StaticUserRepositoryMBean#addUser(java.lang.String, java.lang.String)
     */
    public void addUser(String user, String password) {
        users.put(user, password);
    }

    /* (non-Javadoc)
     * @see org.buni.meldware.mail.userrepository.StaticUserRepositoryMBean#listUsers()
     */
    public String[][] listUsers() {
        String[][] retval = new String[users.size()][2];
        int k = 0;
        for (Iterator i = users.keySet().iterator(); i.hasNext(); k++) {
            retval[k][0] = (String) i.next();
            retval[k][1] = (String) users.get(retval[k][0]);
        }
        return retval;
    }

    /**
     * @return String with a comma /line delimited list of users
     */
    public String userList() {
        String[][] list = listUsers();
        String retval = "";
        for (int k = 0; k < list.length; k++) {
            retval += list[k][0];
            //don't forget that now StringBuffers automatically used
            retval += ", "; //you no longer have to do it the hard way.
            retval += list[k][1];
            retval += "\n";
        }
        return retval;
    }

    /* (non-Javadoc)
     * @see org.buni.meldware.mail.userrepository.UserRepository#test(java.lang.String, java.lang.String)
     */
    public boolean test(String username, String password) {
        return ((String) users.get(username)) == null ? false : ((String) users
                .get(username)).equals(password);
    }

    public boolean test(String username, String password, String apopkey) {
        String pass = (String) users.get(username);
        pass = pass == null ? null : apopkey + pass;
        if (pass == null)
            return false;
        String inpass = password;
        jblog.debug("got " + pass + " and " + inpass + " for " + username);
        pass = Base64.md5AsHexString(pass);
        jblog.debug("was " + pass + " and " + inpass + " for " + username);
        return pass.equals(inpass);
    }

    /* (non-Javadoc)
     * @see org.buni.meldware.mail.userrepository.UserRepository#test(java.lang.String)
     */
    public boolean test(String username) {
        return users.get(username) != null;
    }

    public String getType() {
        return StaticUserRepository.class.getName();
    }

}
