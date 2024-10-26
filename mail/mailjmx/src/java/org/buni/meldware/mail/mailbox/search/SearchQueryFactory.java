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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.buni.meldware.mail.MailException;
import org.buni.meldware.mail.api.Range;
import org.buni.meldware.mail.api.SearchKey;
import org.buni.meldware.mail.api.SearchKey.KeyName;

/**
 * Builds a search query that can be run against a particular folder.
 * 
 * @author Michael Barker
 *
 */
public class SearchQueryFactory {
    
    public SearchQuery create(SearchKey key) {
        switch (key.getPartName()) {
        case ALL:
            return createAll();
        case ANSWERED:
        case DELETED:
        case SEEN:
        case RECENT:
        case FLAGGED:
        case DRAFT:
            return createEJBQL("fe." + key.getFieldName(), key);
        case SUBJECT:
            return createEJBQL("m.subject", key);
        case TO:
            return createEJBQL("m.toAddress", key);
        case FROM:
            return createEJBQL("m.fromAddress", key);
        case SMALLER:
            return createEJBQL("m.messageSize", key, EJBQLSearchQuery.Operator.LT);
        case LARGER:
            return createEJBQL("m.messageSize", key, EJBQLSearchQuery.Operator.GT);
        case SENTON:
        case ON:
            return createEJBQL("m.timestamp", key, EJBQLSearchQuery.Operator.EQ);
        case SENTSINCE:
        case SINCE:
            return createEJBQL("m.timestamp", key, EJBQLSearchQuery.Operator.GT);
        case SENTBEFORE:
        case BEFORE:
            return createEJBQL("m.timestamp", key, EJBQLSearchQuery.Operator.LT);
        case CC:
            return createHeader("Cc", key);
        case BCC:
            return createHeader("Bcc", key);
        case TEXT:
        case BODY:
            return createScanBody(key);
        case KEYWORD:
            return createKeyword(key);
        case UID:
            return createUid(key);
        case HEADER:
            return createHeader(key);
        case AND:
            return createAnd(key);
        case OR:
            return createOr(key);
        case NOT:
            return createNot(key);
        default:
            throw new MailException("Unknown Query type: %s", key.getPartName());
        }
    }
 
    private SearchQuery createAll() {
        return new AllSearchQuery();
    }

    private SearchQuery createNot(SearchKey key) {
        SearchKey childKey = (SearchKey) key.getValue();
        SearchQuery childQ = create(childKey);
        return new NotSearchQuery(childQ);
    }

    private SearchQuery createUid(SearchKey key) {
        return new UIDSearchQuery((Range[]) key.getValue());
    }

    private SearchQuery createKeyword(SearchKey key) {
        return new KeywordSearchQuery((String)key.getValue());
    }

    private SearchQuery createScanBody(SearchKey key) {
        boolean scanHeaders = key.getPartName() == KeyName.TEXT;
        return new ScanBodySearchQuery((String) key.getValue(), scanHeaders);
    }

    private SearchQuery createHeader(SearchKey key) {
        return new HeaderSearchQuery(key.getName(), (String)key.getValue());
    }

    private SearchQuery createHeader(String name, SearchKey key) {
        return new HeaderSearchQuery(name, (String)key.getValue());
    }
    
    private SearchQuery createOr(SearchKey key) {
        @SuppressWarnings("unchecked")
        List<SearchKey> keys = (List<SearchKey>)key.getValue();
        List<SearchQuery> queries = new ArrayList<SearchQuery>();
        for (SearchKey child : keys) {
            queries.add(create(child));
        }
        return new OrSearchQuery(queries);
    }

    private SearchQuery createAnd(SearchKey key) {
        @SuppressWarnings("unchecked")
        List<SearchKey> keys = (List<SearchKey>)key.getValue();
        List<SearchQuery> queries = new ArrayList<SearchQuery>();
        for (SearchKey child : keys) {
            queries.add(create(child));
        }
        return new AndSearchQuery(queries);
    }

    /**
     * Maps the key to a particular ejb3 field in the pojo.
     * 
     * @param field
     * @param key
     * @return
     */
    public SearchQuery createEJBQL(String field, SearchKey key) {
        EJBQLSearchQuery.Operator op = createOperator(key);
        return createEJBQL(field, key, op);
    }
    
    /**
     * Maps the key to a particular ejb3 field in the pojo.
     * 
     * @param field
     * @param key
     * @return
     */
    public SearchQuery createEJBQL(String field, SearchKey key, 
            EJBQLSearchQuery.Operator op) {
        SearchValue v = createValue(op, key);
        return new EJBQLSearchQuery(field, v);
    }
    
    /**
     * Creates a value for the specified key.
     * 
     * @param key
     * @return
     */
    public SearchValue createValue(EJBQLSearchQuery.Operator op, SearchKey key) {
        switch (key.getType()) {
        case BOOLEAN:
            return new BooleanSearchValue(op, (Boolean)key.getValue());
        case STRING:
            return new StringSearchValue(op, (String)key.getValue());
        case NUMBER:
            return new NumberSearchValue(op, (Long)key.getValue());
        case DATE:
            return new DateSearchValue(op, (Calendar)key.getValue());
        default:
            throw new RuntimeException("Unhandled type: " + key.getType());
        }
    }
    
    public EJBQLSearchQuery.Operator createOperator(SearchKey key) {
        switch (key.getType()) {
        case BOOLEAN:
            return EJBQLSearchQuery.Operator.EQ;
        case STRING:
            return EJBQLSearchQuery.Operator.LIKE;
        default:
            throw new RuntimeException("Unhandled type: " + key.getType());
        }
    }
}
