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
 * ConditionConfig is the persistent face of the condition.  Where a condition is the "matcher" of something
 * the conditionconfig is when and where the condition should be performed inside a UserActionSet
 * 
 * @author Andrew C. Oliver (acoliver ot buni dat org)
 * @version $Revision: 1.1 $
 */
public class ConditionConfig {
    private String id;
    private boolean ignoreCase;
    private boolean negation;
    private String name;
    private String header;
    private String value;

    public ConditionConfig(String id, boolean ignoreCase, boolean negation, 
                           String name, String header, String value) {
        this.id = id;
        this.ignoreCase = ignoreCase;
        this.negation = negation;
        this.name = name;
        this.header = header;
        this.value = value;
    }

    public ConditionConfig(ResultSet rs) throws SQLException {
        this.id = rs.getString("C_ID");
        this.ignoreCase = rs.getBoolean("C_IGNORE_CASE");
        this.negation = rs.getBoolean("C_NOT");
        this.name = rs.getString("C_NAME");
        this.header = rs.getString("C_HEADER");
        this.value = rs.getString("C_VALUE");
    }

    public String getId() {
        return id;
    }
    
    public String getHeader() {
        return header;
    }

    
    public boolean getIgnoreCase() {
        return ignoreCase;
    }

    
    public String getName() {
        return name;
    }

    
    public boolean getNegation() {
        return negation;
    }

    
    public String getValue() {
        return value;
    }

    public void setId(String cid) {
        this.id=cid;
    }
}
