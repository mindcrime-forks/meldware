package org.buni.meldware.test;

 
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import javax.management.Attribute;
import javax.management.ObjectName;

import org.jboss.jmx.adaptor.rmi.RMIAdaptor;


/**
 * @author andy
 *
 */
public class ProxyMaker {
    public static Object makeProxy (final RMIAdaptor rmi, final Class iface, final ObjectName oname) {        
        Object proxy = null;
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        Class[] interfaces = new Class[]{iface};
        InvocationHandler hander = new InvocationHandler() {
        
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                String name = method.getName();
                
                if(name.startsWith("set")) {
                    String attributeName = name.substring(3);
                    Attribute a = new Attribute(attributeName, args[0]);
                    rmi.setAttribute(oname, a);
                } else if (name.startsWith("get")) {
                    String attributeName = name.substring(3);
                    return rmi.getAttribute(oname, attributeName);
                } else {
                    String operation = name;
                    Class[] cparms = method.getParameterTypes();
                    String[] sparms = new String[cparms.length];
                    for(int i = 0;  i < cparms.length; i++) {
                        sparms[i] = cparms[i].getName();
                    }
                    return rmi.invoke(oname, operation, args, sparms);
                }
                
                return null;
            }
        
        };
        return Proxy.newProxyInstance(loader, interfaces, hander);
       // return proxy;
    }

}
