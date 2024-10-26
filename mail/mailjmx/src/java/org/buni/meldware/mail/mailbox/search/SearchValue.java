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
package org.buni.meldware.mail.mailbox.search;

import javax.persistence.Query;

import org.buni.meldware.mail.mailbox.search.EJBQLSearchQuery.Operator;

public abstract class SearchValue {

    public abstract void setParameter(int idx, Query q);
    public abstract String getClause(String fieldName, int idx);
    public String getField(String fieldName) {
        return fieldName;
    }
    
    public String getOp(Operator oper) {
        switch (oper) {
        case EQ:
            return "=";
        case LT:
            return "<";
        case LE:
            return "<=";
        case GT:
            return ">";
        case GE:
            return ">=";
        case LIKE:
            return "LIKE";
        default:
            throw new RuntimeException("Unknown Operator: " + oper);
        }
    }
    
}
