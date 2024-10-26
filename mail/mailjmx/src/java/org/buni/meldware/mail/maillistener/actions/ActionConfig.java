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
package org.buni.meldware.mail.maillistener.actions;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * ActionConfig is a persistent representation of Action.  Where 
 * an Action handles the actual behavior an ActionConfig is a member
 * of a UserActionSet that represents which, when and where to execute an 
 * action.
 *
 * @author Andrew C. Oliver
 * 
 * @version $Revision: 1.1 $
 */
public class ActionConfig {
    
    private String params;
    private String name;
    private String id;

    /**
     * Construct an action config with a particular id, name, params
     * @param id of this action
     * @param name of this action
     * @param params comma del string 
     */
    public ActionConfig(String id, String name, String params) {
        this.id = id;
        this.name = name;
        this.params = params;
    }

    /**
     * Construct an action config from a result set, this handles the getting 
     * @param rs resultset to get the id,name,params from
     * @throws SQLException if something bad happens like a disconnect
     */
    public ActionConfig(ResultSet rs) throws SQLException {
        this.id = rs.getString("A_ID");
        this.name = rs.getString("A_NAME");
        this.params = rs.getString("A_PARAMS");
    }

    /**
     * @return id PK in the DB
     */
    public String getId() {
        return id;
    }

    /**
     * @return name of the action to execute
     */
    public String getName() {
        return name;
    }

    /**
     * @return params to pass
     */
    public String getParams() {
        return params;
    }

    /**
     * set the id
     * @param id in the DB 
     */
    public void setId(String id) {
        this.id = id;
    }

}
