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
package org.buni.meldware.mail.test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Michael.Barker
 *
 */
public class DataSetFactory {

    private final static Map<String,DataSet> dataSets = new HashMap<String,DataSet>();
    
    static {
        dataSets.put("tom", new SimpleDataSet("tom", "tom", "tom@localhost"));
        dataSets.put("jerry", new SimpleDataSet("jerry", "jerry", "jerry@localhost"));
        dataSets.put("tweety", new SimpleDataSet("tweety", "tweety", "tweety@localhost"));
        dataSets.put("test", new SimpleDataSet("test", "testpwd", "tom@localhost"));
    }
    
    public static DataSet get(String name) {
        if (dataSets.containsKey(name)) {
            return dataSets.get(name);
        } else {
            throw new RuntimeException("DataSet " + name + " not found");
        }
    }
    
}
