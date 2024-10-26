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
import java.util.ArrayList;
import java.util.List;

/**
 * A UserActionSets represents all of the UserActionSet objects for a given user.
 * basically the SET of user action set(s).
 * @author Andrew C. Oliver (acoliver ot buni dat org)
 * @version $Revision: 1.1 $
 */
public class UserActionSets {
    private String id;
    private String user;
    private List<UserActionSet> uas;

    public UserActionSets(String id, String user) {
        this.id = id;
        this.user = user;
    }
    
    public UserActionSets(ResultSet rs) throws SQLException {
        this.id = rs.getString("UAS_ID");
        this.user = rs.getString("UAS_USER");
        uas = new ArrayList<UserActionSet>();
        if (rs.getString("UA_ID") != null) {
            do {
                UserActionSet u = new UserActionSet(rs);
                uas.add(u);
            } while (rs.next());
        } 
    }

    public String getId() {
        return id;
    }
    
    public String getUser() {
        return user;
    }
    
    public void setUserActionSets(List<UserActionSet> uas) {
        this.uas = uas;
    }
    
    public List<UserActionSet> getUserActionSets() {
        return this.uas;
    }

    public void setId(String uasid) {
        this.id = uasid;
    }
}
