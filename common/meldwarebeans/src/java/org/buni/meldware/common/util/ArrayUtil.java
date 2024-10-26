/*
 * Bunisoft the Open Source Communications Company
 * Copyright 2006, Bunisoft Inc.,
 *
 * Portions of this software are Copyright 2006, JBoss Inc., and 
 * individual contributors as indicated by the @authors tag.
 * See the copyright.txt in the distribution for a full listing of
 * individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
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
package org.buni.meldware.common.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Useful utilities for handling arrays (most String arrays).
 * 
 * @author Michael Barker <mailto:mike@middlesoft.co.uk>
 * @author Andrew C. Oliver <acoliver ot buni dat org>
 * @version $Revision: 1.5 $
 */
public class ArrayUtil {

    
    public static String[] toStringArray(List<? extends Object> l) {
        String[] ss = new String[l.size()];
        for (int i = 0; i < l.size(); i++) {
            ss[i] = l.get(i).toString();
        }
        return ss;
    }
    
    public static String join(Object[] values, String delimiter) {
        return join(values, "", delimiter, "");
    }
    
            
    /**
     * Joins an array of Strings together into a single String.
     * User supplies the array, prefix, suffix, and delimiter.
     * An empty String <code>""</code> can be passed if any of the 
     * three should be ignored.
     *  
     * @param values An array of value to join.
     * @param prefix The text to go at the front of the string
     * @param delimiter The text to go between each value.
     * @param suffix The text to go at the end of the strign.
     * @return
     */
    public static String join(Object[] values, String prefix, String delimiter,
            String suffix) {
        StringBuilder sb = new StringBuilder();
        sb.append(prefix);
        for (int i = 0; i < values.length; i++) {
            sb.append(values[i].toString());
            if (i < (values.length - 1)) {
                sb.append(delimiter);
            }
        }
        sb.append(suffix);
  
        return sb.toString();
    }
    
    public static String join(Collection<? extends Object> values, 
            String delimiter) {
        return join(values, "", delimiter, "");
    }
    
    /**
     * Joins an array of Strings together into a single String.
     * User supplies the array, prefix, suffix, and delimiter.
     * An empty String <code>""</code> can be passed if any of the 
     * three should be ignored.
     *  
     * @param values An array of value to join.
     * @param prefix The text to go at the front of the string
     * @param delimiter The text to go between each value.
     * @param suffix The text to go at the end of the strign.
     * @return
     */
    public static String join(Collection<? extends Object> values, String prefix, 
            String delimiter, String suffix) {
        StringBuilder sb = new StringBuilder();
        sb.append(prefix);
        for (Iterator<? extends Object> i = values.iterator(); i.hasNext();) {
            sb.append(i.next().toString());
            if (i.hasNext()) {
                sb.append(delimiter);
            }            
        }
        sb.append(suffix);
  
        return sb.toString();
    }
    
	/**
	 * @param bods
	 * @return
	 */
	public static String longListCommaDel(List<Long> bods) {
		String retval = "";
		for (int i = 0; i < bods.size(); i++) {
			retval += bods.get(i);
			retval += (i != bods.size()-1) ? "," : "";
		}
		return retval;
	}

    /**
     * return a list of values that are in the superset but are not in the subset
     * @param subset additions from here are ignored
     * @param superset anything in here but not in the subset compose the return value
     * @return list of values from superset collection not in subset
     */
    public static List<String> rightHandDisjunction(List<String> subset, List<String> superset) {
        List<String> result = new ArrayList<String>();
        for (String string : superset) {
            if (!subset.contains(string)) {
                result.add(string);
            }
        }
        return result;
    }
    
    public static <T> Set<T> asSet(T... values) {
        Set<T> s = new HashSet<T>((int) (values.length/0.75f  + 1));
        for (T value : values) {
            s.add(value);
        }
        return s;
    }

	/**
     * Take a comma del string and turn it into an String[]
     * @param string to parse
     * @return String[] containing the comma-del'd elements from the string
     */
    public static String[] commaDelToArray(String string) {
        String[] retval = string != null ? string.split(",") : null;
        return retval;
    }

    public static List<String> commaDelToStringList(String string) {
        String[] val = commaDelToArray(string);
        List<String> retval = Arrays.asList(val);
        return retval;
    }
}
