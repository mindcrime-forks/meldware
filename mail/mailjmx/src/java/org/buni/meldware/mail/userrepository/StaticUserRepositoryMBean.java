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

import org.jboss.system.ServiceMBean;
import org.w3c.dom.Element;

/**
 * This UserRepository is merely configured via the JBoss XML Mbean config with &lt;users&gt;,
 * &lt;user&gt;, &lt;id&gt;, and &lt;password&gt; elements.  These are statically configured or can be 
 * ammended via the JMX console.
 * @author Andrew C. Oliver
 * @version $Revision: 1.2 $
 */
public interface StaticUserRepositoryMBean extends ServiceMBean,
        java.io.Serializable, UserRepository {

    /** 
     * set the users via a DOM element
     * @param users element
     */
    public void setUsers(Element users);

    /**
     * @return the list of users via a DOM element
     */
    public Element getUsers();

    /**
     * @return {{user,password}{user,password}}
     */
    public String[][] listUsers();

    /**
     * @return big fat string containing user,password\n till we run out
     */
    public String userList();

    /**
     * add a user
     * @param user name of user
     * @param password of user
     */
    public void addUser(String user, String password);
    
    public String getType();

}
