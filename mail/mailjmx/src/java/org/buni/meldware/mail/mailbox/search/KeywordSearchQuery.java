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

public class KeywordSearchQuery extends SearchQuery {

    private String value;

    public KeywordSearchQuery(String value) {
        this.value = value;
    }

    @Override
    public Set<Long> getResults(SearchContext context) {
        String queryStr = "select fe.uid from FolderEntry fe join fe.flags as f where fe.folder.id = ?1 and f.value = ?2";
        Query q = context.getEntityManager().createQuery(queryStr);
        q.setParameter(1, context.getFolderId());
        q.setParameter(2, value.toUpperCase());
        List results = q.getResultList();
        Set<Long> values = new HashSet<Long>();
        for (Object o : results) {
            Long l = (Long) o;
            values.add(l);
        }
        return values;
    }

}
