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

import java.lang.reflect.Field;

/**
 * @author Michael Barker <mailto:mike@middlesoft.co.uk>
 * @version $Revision: 1.2 $
 */
public class ObjectUtil {

    /**
     * Simple utility to dump an object to a string.  Mainly for use
     * int debug logging.
     * 
     * @param o
     * @return
     */
    public static String toString(Object o) {
        try {
            StringBuffer sb = new StringBuffer();

            Class c = o.getClass();
            Field[] fields = c.getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                fields[i].setAccessible(true);
                sb.append(fields[i].getName());
                sb.append(":");
                sb.append(fields[i].get(o));

                if (i < fields.length - 1) {
                    sb.append(", ");
                }
            }

            return sb.toString();
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }
}
