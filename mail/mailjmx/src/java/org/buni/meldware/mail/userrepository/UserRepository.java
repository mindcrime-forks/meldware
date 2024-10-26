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

import org.jboss.system.ServiceMBean;

/**
 * Represents some authority on users which can verify a username and password
 * @author Andrew C. Oliver
 * @version $Revision: 1.3 $
 */
public interface UserRepository extends Serializable, ServiceMBean {

    /**
     * can this user get in
     * @param username of the user
     * @param password of the user
     * @return does he exist and is this the correct password
     */
    public boolean test(String username, String password);

    /**
     * can this user get in
     * @param username of the user
     * @param password of the user
     * @param unhashed apop key prepended in the form <threadid.time@servername>
     * @return does he exist and is this the correct <threadid.time@servername>password string
     */
    public boolean test(String username, String password, String apopkey);

    public String getType();
    
    /**
     * Does the user exist
     * @param username of the user
     * @return does he exist
     * 
     * Dawie: JAAS does not support this...
     */
    //  public boolean test(String username);
}
