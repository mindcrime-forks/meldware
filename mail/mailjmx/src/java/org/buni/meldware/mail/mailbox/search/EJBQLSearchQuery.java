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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Query;

public class EJBQLSearchQuery extends SearchQuery {

    public enum Operator { EQ, LT, LE, GT, GE, LIKE };
    private SearchValue value;
    private String field;

    public EJBQLSearchQuery(String field, SearchValue value) {
        this.field = field;
        this.value = value;
    }
    
    public String getClause(int idx) {
        return value.getClause(field, idx);
    }
    
    public String getNegateClause(int idx) {
        return "NOT " + value.getClause(field, idx);
    }    
    
    public Set<Long> getResults(SearchContext context) {
        return getResults(context, true);
    }
    
    public Set<Long> getResults(SearchContext context, boolean isPositive) {
        String queryStr = "select fe.uid from FolderEntry fe JOIN fe.message m WHERE fe.folder.id = ?1 and (%s)";
        String clause = isPositive ? getClause(2) : getNegateClause(2);
        Query q = context.getEntityManager().createQuery(String.format(queryStr, clause));
        q.setParameter(1, context.getFolderId());
        value.setParameter(2, q);
        List results = q.getResultList();
        Set<Long> values = new HashSet<Long>();
        for (Object o : results) {
            Long l = (Long) o;
            values.add(l);
        }
        return values;
    }
    
}
