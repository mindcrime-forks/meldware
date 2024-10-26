/*
 * Bunisoft the Open Source Communications Company
 * Copyright 2006, Bunisoft LLC., and individual contributors as
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

/**
 * Utilities for maniplulating text.
 * 
 * @author Michael Barker
 *
 */
public class TextUtil {

    private final static String NEWL = "\r\n";
    
    private static int indexOf(CharSequence s, char c, int fromIndex) {
        for (int i = fromIndex; i < s.length(); i++) {
            if (s.charAt(i) == c) {
                return i;
            }
        }
        return -1;
    }
    
    /**
     * Handles line folding.
     * 
     * @param line
     * @param maxLength
     * @param newLinePrefix
     * @return
     */
    public static CharSequence foldLine(CharSequence line, int maxLength, char newLinePrefix) {
        
        // If line must not be broken or key was too long return line as is
        if (line.length() <= maxLength || maxLength <= 0) {
            return line;
        }

        // Try to find possible fold pos else return complete line
        int foldPos = indexOf(line, ' ', maxLength);

        if (foldPos == -1) {
            return line;
        }
        StringBuilder result = new StringBuilder(line.length() + 3);

        result.append(line.subSequence(0, foldPos));       
        result.append(NEWL);
        result.append(newLinePrefix);

        int lastFoldPos = foldPos;

        foldPos += maxLength;

        if (foldPos < line.length()) {
            foldPos = indexOf(line, ' ', foldPos);
        }

        while (foldPos != -1 && foldPos < line.length()) {
            result.append( line.subSequence(0, foldPos));       
            result.append(NEWL);
            result.append(newLinePrefix);
            lastFoldPos = foldPos;
            foldPos += maxLength;
            if( foldPos < line.length() ) {
                foldPos = indexOf(line, ' ', foldPos);
            }
        }
        
        result.append(line.subSequence(lastFoldPos, line.length()));
        return result;
    }
    
    /**
     * Removes any white space from the right hand side of the string.
     * 
     * @param s
     * @return
     */
    public static String trimRight(String s) {
        
        int len = s.length();
        while (s.charAt(len - 1) <= ' ') {
            len--;
        }
        
        if (len == s.length()) {
            return s;
        } else {
            return s.substring(0, len);
        }
    }
}
