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
import java.util.List;

import org.buni.meldware.mail.message.Mail;

/**
 * A UserActionSet represents a named set of conditions and actions to perform if they are met.  
 * @author Andrew C. Oliver (acoliver ot buni dat org)
 * @version $Revision: 1.2 $
 */
public class UserActionSet {
    private String id;
    private boolean all;
    private List<ConditionConfig> conditions;
    private List<ActionConfig> actions;
    private String name;
    private ServerActionsMailListener conditionFactory;
    public UserActionSet(String id, String name, boolean all) {
        this.name=name;
        this.id = id;
        this.all = all;
    }

    
    public UserActionSet(ResultSet rs) throws SQLException {
        this.name = rs.getString("UA_NAME");
        this.id = rs.getString("UA_ID");
        this.all = rs.getBoolean("UA_ALL");
    }


    public List<ActionConfig> getActions() {
        return actions;
    }

    
    public void setActions(List<ActionConfig> actions) {
        this.actions = actions;
    }

    
    public List<ConditionConfig> getConditions() {
        return conditions;
    }

    
    public void setConditions(List<ConditionConfig> conditions) {
        this.conditions = conditions;
    }


    
    public boolean getAll() {
        return all;
    }


    
    public void setAll(boolean all) {
        this.all = all;
    }


    
    public String getId() {
        return id;
    }


    
    public void setId(String aid) {
        this.id = aid;
    }


    public boolean evaluate(Mail mail) {
        List<ConditionConfig>ccs = this.conditions;
        for (ConditionConfig config : ccs) {
            Condition c = conditionFactory.getCondition(config);
            String mailheader = mail.getHeaderAsString(config.getHeader());
            mailheader = mailheader == null ? null : mailheader.toLowerCase();
            boolean eval = c.evaluate(mailheader,config.getValue().toLowerCase()); //TODO case flags
            if (this.all && !eval) {
                return false;
            } else if (!this.all && eval) {
                return true;
            }
        }
        return false;
    }


    public String getName() {
        return name;
    }


    
    public void setName(String name) {
        this.name = name;
    }

    // YAGNI ConditionFactory Interface
    public void setConditionFactory(ServerActionsMailListener impl) {
        this.conditionFactory = impl;
    }

    //YAGNI ActionFactory interface
    public void setActionFactory(ServerActionsMailListener impl) {       
    }
    
    
}
