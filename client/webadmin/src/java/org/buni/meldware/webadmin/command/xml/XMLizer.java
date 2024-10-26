package org.buni.meldware.webadmin.command.xml;

import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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
                    if (method.getParameterTypes().length == 0) {
    					String field = method.getName().substring(3);
    					Object val = method.invoke(result);
    					out.println("  <"+field+">"+val.toString()+"</"+field+">");
                    }
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
