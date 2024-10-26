/*
 * Bunisoft the Open Source Communications Company
 * Copyright 2006, Bunisoft Inc., and individual contributors as
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
package org.buni.meldware.mail.usereditor;

import java.util.List;

/**
 * A user editor works with a particular underlying JaasUserRepository type.  
 * 
 * @author Andrew C. Oliver <acoliver ot buni dat org>
 */
public interface UserEditor {

    public void start();
    public void stop();
    
    public void addUser(String login, String password);
    public void addRole(String login, String role);
    public void deleteUser(String login);
    public void deleteRole(String login, String role);
    public void changePassword(String login, String password);
    
    public boolean userExists(String login);
    public List getRoles(String login);
    public List getUsers(String pattern);

}
