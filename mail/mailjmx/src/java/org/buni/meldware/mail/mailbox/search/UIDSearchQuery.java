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

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.buni.meldware.mail.api.Range;

public class UIDSearchQuery extends SearchQuery {

    private Range[] ranges;

    public UIDSearchQuery(Range[] ranges) {
        this.ranges = ranges;
    }

    @Override
    public Set<Long> getResults(SearchContext context) {
        
        StringBuilder queryB = new StringBuilder();
        queryB.append("SELECT DISTINCT fe.uid ");
        queryB.append("FROM FolderEntry fe ");
        queryB.append("WHERE fe.folder.id = ?1 ");
        
        if (ranges.length > 0) {
            queryB.append("AND (");
            int paramNum = 2;
            for (int i = 0; i < ranges.length; i++) {
                queryB.append("(fe.uid >= ?");
                queryB.append(paramNum++);
                queryB.append(" AND ");
                queryB.append("fe.uid <= ?");
                queryB.append(paramNum++);
                queryB.append(")");
                if (i < ranges.length - 1) {
                    queryB.append(" OR ");
                }
            }
            queryB.append(") ");
        }
        
        EntityManager em = context.getEntityManager();
        Query query = em.createQuery(queryB.toString());

        query.setParameter(1, context.getFolderId());
        int paramNum = 2;
        for (int i = 0; i < ranges.length; i++) {
            query.setParameter(paramNum++, ranges[i].getMin());
            query.setParameter(paramNum++, ranges[i].getMax());
        }
        
        @SuppressWarnings("unchecked")
        List<Long> result = (List<Long>) query.getResultList();
        
        return new HashSet<Long>(result);
    }

}
