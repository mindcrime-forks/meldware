/*
 * Bunisoft the Open Source Communications Company
 * Copyright 2007, Bunisoft LLC., and individual contributors as
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
package org.buni.meldware.address;

import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class XMLizer {

	private static final Object EMPTY_OBJARRAY = new Object[]{};

	@SuppressWarnings("unchecked")
	public static void toXML(String tag, Collection results,
			PrintWriter out) {
		try {
            Class clazz = results.iterator().next().getClass();
			Set<Method> ms = classDemangler(clazz);
            Class ifaces[] = clazz.getInterfaces();
     //       for (Class iface : ifaces) {
     //           ms.addAll(classDemangler(iface));
     //       }
			Map<String,Method> methods = getMethodsStartingWith("get",ms); 
			for (Object result : results) {
				out.println("<"+tag+">");
				for (Method method : methods.values()) {
					String field = method.getName().substring(3);
					Object val = method.invoke(result);
					out.println("  <"+field+">"+val.toString()+"</"+field+">");
				}
				out.println("</"+tag+">");
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static Map<String,Method> getMethodsStartingWith(String string,
			Set<Method> methodArray) {
		Map<String, Method> results = new HashMap<String, Method>();
		for (Method method : methodArray) {
			if (method.getName().startsWith("get")) {
				results.put(method.getName(),method);
			}
		}
		return results;
	}
    
    public static Set<Method> classDemangler(Class clazz) {
        Set<Method> methods = new HashSet<Method>();
        Method[] ms = clazz.getMethods();
        for (Method method : ms) {
            method.setAccessible(true);
            methods.add(method);
        }
        Class[] ifaces = clazz.getInterfaces();
        for (Class iface : ifaces) {
            methods.addAll(classDemangler(iface));
        }
        if (clazz.getName() != Object.class.getName() && clazz.getSuperclass() != null) {
            methods.addAll(classDemangler(clazz.getSuperclass()));
        }
        return methods;
    }

}
