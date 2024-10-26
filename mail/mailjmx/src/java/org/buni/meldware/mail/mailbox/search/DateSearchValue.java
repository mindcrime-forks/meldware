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

import java.util.Calendar;

import javax.persistence.Query;
import javax.persistence.TemporalType;

import org.buni.meldware.mail.mailbox.search.EJBQLSearchQuery.Operator;

public class DateSearchValue extends SearchValue {
    
    private Calendar value;
    private Operator op;

    public DateSearchValue(Operator op, Calendar value) {
        this.op = op;
        this.value = value;
    }

    @Override
    public void setParameter(int idx, Query q) {
        q.setParameter(idx, value, TemporalType.DATE);
        if (op.equals(Operator.EQ)) {
            q.setParameter(idx, value, TemporalType.DATE);
            Calendar nextDay = (Calendar) value.clone();
            nextDay.add(Calendar.DAY_OF_MONTH, 1);
            q.setParameter(idx + 1, nextDay, TemporalType.DATE);
        } else {
            q.setParameter(idx, value, TemporalType.DATE);
        }
    }
    
    @Override
    public String getClause(String fieldName, int idx) {
        if (op.equals(Operator.EQ)) {
            return String.format(fieldName + " between ?%d and ?%d", idx, idx + 1);
        } else {
            String oper = getOp(op);
            return String.format(fieldName + " %s ?%d", oper, idx);            
        }
    }

}
