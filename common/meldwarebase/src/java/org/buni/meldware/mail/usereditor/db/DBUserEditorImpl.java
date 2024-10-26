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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.buni.meldware.common.logging.Log;
import org.jboss.resource.adapter.jdbc.remote.WrapperDataSourceServiceMBean;


public class DBUserEditorImpl implements DBUserEditor {

    private String users;
    private String userRoles; 
    private String password;
    private String deleteUser;
    private String deleteRole ;
    private WrapperDataSourceServiceMBean wds;
    private DataSource ds;
    private String addUser;
    private String createUserTable;
    private String createRoleTable;
    private String addRole;
    private final static Log log = Log.getLog(DBUserEditorImpl.class);

    public String getAddRoleSQL() {
        return this.addRole;
    }

    public String getCreateUserTableSQL() {
        return this.createUserTable;
    }
    
    public String getCreateRoleTableSQL() {
        return this.createRoleTable;
    }

    public String getAddUserSQL() {
        return this.addUser;
    }

    public WrapperDataSourceServiceMBean getDataSource() {
        return this.wds;
    }

    public String getDeleteRoleSQL() {
        return this.deleteRole;
    }

    public String getDeleteUserSQL() {
        return this.deleteUser;
    }

    public String getUpdatePasswordSQL() {
        return this.password;
    }

    public String getUserRolesSQL() {
        return this.userRoles;
    }

    public String getUsersSQL() {
        return this.users;
    }

    public void setAddRoleSQL(String addRole) {
        this.addRole = addRole;
    }

    public void setCreateUserTableSQL(String createUserTable) {
        this.createUserTable = createUserTable;
    }
    
    public void setCreateRoleTableSQL(String createRoleTable) {
        this.createRoleTable = createRoleTable; 
    }

    public void setAddUserSQL(String addUser) {
        this.addUser = addUser;
    }

    public void setDataSource(WrapperDataSourceServiceMBean wds) {
        this.wds = wds; 
    }

    public void setDeleteRoleSQL(String deleteRole) {
        this.deleteRole = deleteRole; 
    }

    public void setDeleteUserSQL(String deleteUser) {
        this.deleteUser = deleteUser;
    }

    public void setUpdatePasswordSQL(String password) {
        this.password = password;
    }

    public void setUserRolesSQL(String userRoles) {
        this.userRoles = userRoles;
    }

    public void setUsersSQL(String users) {
        this.users = users;
    }

    public void addRole(String login, String role) {
        if (!roleExists(login, role)) {
            executeStatement(addRole,new String[]{login, role});
        } else {
            log.warn("User: %s already has role: %s", login, role);
        }
    }

    public void addUser(String login, String password) {
        if (!userExists(login)) {
            executeStatement(this.addUser, new String[]{login, password});
        } else {
            log.warn("User: %s already exists", login);
        }
    }

    public void changePassword(String login, String password) {
        executeStatement(this.password, new String[]{password, login});
    }

    public void deleteRole(String login, String role) {
        executeStatement(this.deleteRole, new String[]{login,role});    
    }

    public void deleteUser(String login) {
        executeStatement(this.deleteUser, new String[]{login});
    }

    public List getRoles(String login) {
        return executeResultStatement(this.userRoles, new String[]{login});
    }

    public List getUsers(String pattern) {
        return executeResultStatement(this.users, new String[]{pattern});
    }
    
    public void start() {
        try {
            String jndiName = this.wds.getBindName();
            Context ctx;
            ctx = new InitialContext();
            this.ds=(DataSource) ctx.lookup(jndiName);
            
            this.executeStatement(this.createUserTable, new String[]{});
            this.executeStatement(this.createRoleTable, new String[]{});
        } catch (Exception e) {
            log.debug("could not create user or role table ",e);
            e.printStackTrace();
        }
    }

    public void stop() {
        // TODO Auto-generated method stub
        
    }

    public boolean userExists(String login) {
        return getUsers(login).size() == 1;
    }
    
    public boolean roleExists(String login, String role) {
        List<String> roles = getRoles(login);
        return roles.contains(role);
    }
    
    private void executeStatement(String query, String[] parms) {
        Connection con = null;
        PreparedStatement stmt = null;
        try {
            con = this.ds.getConnection();
            stmt = con.prepareStatement(query);
            for (int i = 0; i < parms.length; i++) {
               stmt.setString(i+1, parms[i]);               
            }          
            stmt.execute();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {stmt.close();} catch (Exception e2) {}
            try {con.close();} catch (Exception e2) {}
        }       
    }
    
    private List<String> executeResultStatement(String query, String[] parms) {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<String> results = new ArrayList<String>();
        try {
            con = this.ds.getConnection();
            stmt = con.prepareStatement(query);
            for (int i = 0; i < parms.length; i++) {
               stmt.setString(i+1, parms[i]);               
            }          
            stmt.execute();
            rs = stmt.getResultSet();
            while(rs.next()) {
                String val = rs.getString(1);
                results.add(val);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {rs.close();} catch (Exception e2) {}
            try {stmt.close();} catch (Exception e2) {}
            try {con.close();} catch (Exception e2) {}
        }   
        return results;
    }
}
