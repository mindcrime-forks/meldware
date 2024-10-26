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
package org.buni.meldware.mail.usereditor.db;

import org.buni.meldware.mail.usereditor.UserEditor;
import org.jboss.resource.adapter.jdbc.remote.WrapperDataSourceServiceMBean;


public interface DBUserEditor extends UserEditor {
    public void setDataSource(WrapperDataSourceServiceMBean ds);
    public WrapperDataSourceServiceMBean getDataSource();

    public void setCreateUserTableSQL(String createUserTable);
    public String getCreateUserTableSQL();
    
    public void setCreateRoleTableSQL(String createRoleTable);
    public String getCreateRoleTableSQL();
    
    public void setAddUserSQL(String createUser);
    public String getAddUserSQL();
     
    public void setAddRoleSQL(String addRole);
    public String getAddRoleSQL();
    
    public void setDeleteUserSQL(String deleteUser);
    public String getDeleteUserSQL();
    
    public void setDeleteRoleSQL(String deleteRole);
    public String getDeleteRoleSQL();
    
    public void setUserRolesSQL(String userRoles);
    public String getUserRolesSQL();
    
    public void setUsersSQL(String users);
    public String getUsersSQL();
    
    public void setUpdatePasswordSQL(String pasword);
    public String getUpdatePasswordSQL();
}
