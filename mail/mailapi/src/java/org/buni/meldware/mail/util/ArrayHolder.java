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
package org.buni.meldware.mail.util;

import static java.lang.System.arraycopy;
import static java.util.Arrays.binarySearch;

/**
 * Holds a sorted array of longs.
 * 
 * @author mike
 *
 */
public class ArrayHolder {

    final long[] values;
    
    public ArrayHolder(long[] values) {
        // TODO: Should we create a copy for safety.
        this.values = values;
    }
    
    public long last() {
        return values.length > 0 ? values[values.length - 1] : 0;
    }
    
    /**
     * Will return the index of a specific value.  Will return negative
     * value if not found.
     * 
     * @param value
     * @return
     */
    public int indexOf(long value) {
        return binarySearch(values, value);
    }
    
    /**
     * Return a new array with the toExclude values removed.
     * 
     * @param toExclude
     * @return
     */
    public long[] exclude(long[] toExclude) {
        long[] temp = new long[values.length];
        int count = 0;
        for (int i = 0; i < values.length; i++) {
            if (binarySearch(toExclude, values[i]) < 0) {
                temp[count] = values[i];
                count++;
            }
        }
        long[] result = new long[count];
        arraycopy(temp, 0, result, 0, count);
        return result;
    }
    
    /**
     * Return a new array with the toExclude values removed.
     * 
     * @param toExclude
     * @return
     */
    public long[] exclude(ArrayHolder toExclude) {
        return exclude(toExclude.values);
    }
    
    /**
     * Return an array of indexes for the supplied array of values.
     * @param toIndex
     * @return
     */
    public int[] indexOf(long[] toIndex, int toAdd) {
        int[] temp = new int[toIndex.length];
        int count = 0;
        for (int i = 0; i < toIndex.length; i++) {
            int idx = binarySearch(values, toIndex[i]);
            if (idx >= 0) {
                temp[count] = (idx + toAdd);
                count++;
            }
        }
        int[] result;
        if (temp.length < toIndex.length) {
            result = new int[count];
            arraycopy(temp, 0, result, 0, count);
        } else {
            result = temp;
        }
        return result;
    }
    
    public int size() {
        return values.length;
    }
    
    public long[] getValues() {
        return values;
    }
    
    public long get(int i) {
        return values[i];
    }

    public boolean isValidIndex(int idx, long uid) {
        return idx > 0 && idx <= values.length && uid == values[idx-1];
    }
}
