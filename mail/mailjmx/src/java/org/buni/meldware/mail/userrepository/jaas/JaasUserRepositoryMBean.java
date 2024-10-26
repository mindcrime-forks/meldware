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
package org.buni.meldware.mail.userrepository.jaas;

import org.buni.meldware.mail.userrepository.UserRepository;
import org.jboss.system.ServiceMBean;

/*
 * JBoss, the OpenSource J2EE webOS
 * 
 * Distributable under LGPL license. See terms of license at gnu.org.
 * @author Dawie Malan
 */

public interface JaasUserRepositoryMBean extends ServiceMBean,
        java.io.Serializable, UserRepository {

    /**
     * 
     * @return the JAAS security domain used to authenticate users
     */
    public String getSecurityDomain();

    /**
     * Sets the JAAS security domain used to authenticate users
     * @param securityDomain
     */
    public void setSecurityDomain(String securityDomain);

    /**
     * Role to identify a user a a postmaster
     * @return
     */
    public String getPostmasterRole();

    /**
     * Role to identify a user a a postmaster
     */
    public void setPostmasterRole(String role);

    public boolean authenticateUser(String username, String password);

    /**
     * @return apopkey for this thread
     */
    public String apopKey();
    
    public String getType();
}
