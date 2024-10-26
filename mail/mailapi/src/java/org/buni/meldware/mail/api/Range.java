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

/**
 * Creates a 2-valued range.  Will also normalise the range.
 * 
 * @author Michael.Barker
 *
 */
public final class Range {

    public final static long UNBOUND = -1;
    public final static Range[] EMPTY = new Range[0];
    public final static Range[] ALL = { new Range(1, -1) };
    private final long max;
    private final long min;

    /**
     * Specifies the range will accept a&lt;b or a&gt;b.
     * 
     * @param a
     * @param b
     */
    public Range(long a, long b) {
        this.min = Math.min(a, b);
        this.max = Math.max(a, b);
    }

    /**
     * @return the max
     */
    public long getMax() {
        return max;
    }

    /**
     * @return the min
     */
    public long getMin() {
        return min;
    }
    
    public Range normalise(long maxValue) {
        long a = min == -1 ? maxValue : min;
        long b = max == -1 ? maxValue : max;
        return new Range(a, b);
    }
    
    public static Range[] create(long id) {
        return new Range[] { new Range(id, id) };
    }
    
    public static long last(long[] l) {
        return l.length > 0 ? l[l.length - 1] : 0;
    }
    
    public static Range[] normaliseToUid(Range[] ranges, long[] uids) {
        long maxValue = last(uids);
        Range[] uidRanges = new Range[ranges.length];
        for (int i = 0; i < ranges.length; i++) {
            long minUid = getUid(uids, (int) ranges[i].getMin(), maxValue);
            long maxUid = getUid(uids, (int) ranges[i].getMax(), maxValue);
            uidRanges[i] = new Range(minUid, maxUid);
        }
        return uidRanges;
    }
        
    private static long getUid(long[] uids, int seqNum, long maxValue) {
        long uid;
        if (seqNum > uids.length) {
            uid = Long.MAX_VALUE;
        } else if (seqNum == -1) {
            uid = maxValue;
        } else if (seqNum < 1) {
            uid = uids[0];
        } else {
            // Remember that sequence numbers are 1 indexed.
            uid = uids[seqNum - 1];
        }
        return uid;
    }
    
    public static boolean isUnbound(Range[] ranges) {
        if (ranges != null) {
            for (Range r : ranges) {
                if (r.getMax() == UNBOUND || r.getMin() == UNBOUND) {
                    return true;
                }
            }
        }
        return false;
    }
}
