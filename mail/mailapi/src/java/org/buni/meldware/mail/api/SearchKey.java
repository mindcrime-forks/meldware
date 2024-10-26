/*
 * Bunisoft the Open Source Communications Company
 * Copyright 2007, Bunisoft LLC, and individual contributors as
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
package org.buni.meldware.mail.api;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;


/**
 * Representation of a search part for a mailbox search.
 * 
 * @author Michael Barker
 *
 */
public final class SearchKey {

    public enum KeyName {
        ALL, ANSWERED, BEFORE, BCC, BODY, CC, DELETED, DRAFT, FLAGGED, 
        FROM, KEYWORD, LARGER, NEW, NOT, ON, RECENT, SEEN, SENTBEFORE,
        SENTON, SENTSINCE, SINCE, SMALLER, SUBJECT, TEXT, TO, UID,
        HEADER, OR, AND, SEQ_NUM
    };

    public enum KeyType { STRING, NUMBER, DATE, BOOLEAN, LIST, PART, RANGE_SET }

    private KeyName partName;
    private final KeyType partType;
    private final String name;
    private Object value;
    
    protected SearchKey(KeyName partName, KeyType partType, String name, 
            Object value) {
        this.partName = partName;
        this.partType = partType;
        this.name = name;
        this.value = value;
    }
    
    protected SearchKey(KeyName partName, KeyType partType, Object value) {
        this(partName, partType, partName.toString(), value);
    }
    
    public String getName() {
        return name;
    }
    
    public KeyName getPartName() {
        return partName;
    }
    
    /**
     * Convenience method, the name of the part reduced to lower case.
     * 
     * @return
     */
    public String getFieldName() {
        return partName.toString().toLowerCase();
    }
    
    public Object getValue() {
        return value;
    }
    
    public KeyType getType() {
        return partType;
    }
    
    /**
     * Normalises any sequence number based search keys to there
     * uid values.
     * 
     * @param uids
     */
    @SuppressWarnings("unchecked")
    public void normalise(long uids[]) {
        switch (partName) {
        case SEQ_NUM:
            value = Range.normaliseToUid((Range[]) value, uids);
            partName = KeyName.UID;
            break;
        case OR:
        case AND:
        case NOT:
            normaliseChildren(uids);
            break;
        }
    }
    
    @SuppressWarnings("unchecked")
    private void normaliseChildren(long[] uids) {
        switch (partType) {
        case LIST:
            for (SearchKey sk : (List<SearchKey>) value) {
                sk.normalise(uids);
            }
            break;
        case PART:
            ((SearchKey) value).normalise(uids);
            break;
        }
    }
    
    public static SearchKey create(KeyName partName, boolean value) {
        return new SearchKey(partName, KeyType.BOOLEAN, value);
    }
    
    public static SearchKey create(KeyName partName, long value) {
        return new SearchKey(partName, KeyType.NUMBER, value);
    }
    
    public static SearchKey create(KeyName partName, String value) {
        return new SearchKey(partName, KeyType.STRING, value);
    }
    
    public static SearchKey create(KeyName partName, Calendar value) {
        return new SearchKey(partName, KeyType.DATE, value);
    }
    
    public static SearchKey createHeader(String name, String value) {
        return new SearchKey(KeyName.HEADER, KeyType.STRING, name, value);
    }
    
    public static SearchKey createOr(List<SearchKey> parts) {
        return new SearchKey(KeyName.OR, KeyType.LIST, parts);
    }
    
    public static SearchKey createOr(SearchKey...parts) {
        return new SearchKey(KeyName.OR, KeyType.LIST, Arrays.asList(parts));
    }
    
    public static SearchKey createAnd(List<SearchKey> parts) {
        return new SearchKey(KeyName.AND, KeyType.LIST, parts);
    }
    
    public static SearchKey createNot(SearchKey part) {
        return new SearchKey(KeyName.NOT, KeyType.PART, part);
    }
    
    public static SearchKey createUid(Range[] ranges) {
        return new SearchKey(KeyName.UID, KeyType.RANGE_SET, ranges);
    }
    
    public static SearchKey createSeqNum(Range[] ranges) {
        return new SearchKey(KeyName.SEQ_NUM, KeyType.RANGE_SET, ranges);
    }    
}
