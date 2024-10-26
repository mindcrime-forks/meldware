package org.buni.meldware.test;


import java.lang.reflect.Method;
import java.util.Hashtable;

import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestResult;
import junit.framework.TestSuite;


/**
 * Wraps a JUnit test such that the actual test is delegated to the
 * JBoss server using the TestRunnerMBean.
 * 
 * @author Michael.Barker
 *
 */
public class JMXTestWrapper implements Test {

    private static String url = "jnp://localhost:1099";
    private static String invoker = "jmx/rmi/RMIAdaptor";
    private String mbeanName = "meldware.test:type=MailServices,name=TestRunner";
    private String testClass;
    private String testMethod;
    
    public JMXTestWrapper(String testClass, String testMethod) {
        this.testClass = testClass;
        this.testMethod = testMethod;
    }
    
    /* (non-Javadoc)
     * @see junit.framework.Test#countTestCases()
     */
    public int countTestCases() {
        return 1;
    }
    
    public String getName() {
        return toString();
    }
    
    public String toString() {
        return testClass + "." + testMethod;
    }

    /* (non-Javadoc)
     * @see junit.framework.Test#run(junit.framework.TestResult)
     */
    public void run(TestResult result) {
        
        try {
            MBeanServerConnection server = getRemoteJBoss();
            JMXTestRunnerMBean testRunner = (JMXTestRunnerMBean) 
                MBeanServerInvocationHandler.newProxyInstance
                    (server, new ObjectName(mbeanName), JMXTestRunnerMBean.class, false);
            
            result.startTest(this);
            String rsp = testRunner.runTest(testClass, testMethod);
            result.endTest(this);
            // TODO: This is a horrible hack
            // Come up with a better way to handle returned results.
            if (!rsp.startsWith("success")) {
                String msg;
                int idx = rsp.indexOf(":");
                if (idx != -1) {
                    msg = rsp.substring(idx + 1);
                } else {
                    msg = "N/A";
                }
                
                if (rsp.startsWith("failure")) {
                    result.addFailure(this, new AssertionFailedError(msg));
                } else {
                    result.addError(this, new Exception(msg));
                }
            }
            
        } catch (NamingException e) {
            result.addError(this, e);
        } catch (MalformedObjectNameException e) {
            result.addError(this, e);
        } catch (NullPointerException e) {
            result.addError(this, e);
        } catch (Exception e) {
            result.addError(this, e);            
        }
        
    }
    
    public static <T> T getRemoteMBean(String jmxName, Class<T> clazz) {
        try {
            MBeanServerConnection server = getRemoteJBoss();
            ObjectName objectName;
            objectName = new ObjectName(jmxName);
            @SuppressWarnings("unchecked")
            T result = (T) MBeanServerInvocationHandler.newProxyInstance(server,
                    objectName, clazz, false);
            return result; 
        } catch (MalformedObjectNameException e) {
            throw new RuntimeException("Unable to get MBean: " + jmxName, e);
        } catch (NullPointerException e) {
            throw new RuntimeException("Unable to get MBean: " + jmxName, e);
        } catch (NamingException e) {
            throw new RuntimeException("Unable to get MBean: " + jmxName, e);
        }
    }
    
    public static MBeanServerConnection getRemoteJBoss() throws NamingException {
        Hashtable<String,String> h = new Hashtable<String,String>();
        h.put("java.naming.factory.initial", "org.jnp.interfaces.NamingContextFactory");
        h.put("java.naming.factory.url.pkgs", "org.jboss.naming:org.jnp.interfaces");
        h.put("java.naming.provider.url", url);
        InitialContext ic = new InitialContext(h);
        return (MBeanServerConnection) ic.lookup(invoker);
    }
    
    public static TestSuite suite(Class c) {
        TestSuite ts = new TestSuite(c.getName());
        
        Method[] ms = c.getMethods();
        
        for (Method m : ms) {
            if (m.getName().startsWith("test") 
                    && m.getParameterTypes().length == 0
                    && m.getReturnType().equals(Void.TYPE)) {
                
                Test t = new JMXTestWrapper(c.getName(), m.getName());
                ts.addTest(t);
            }
        }
        
        return ts;
    }
    
    public static TestSuite suite(Class c, String method) {
        TestSuite ts = new TestSuite(c.getName());
        Test t = new JMXTestWrapper(c.getName(), method);
        ts.addTest(t);
        return ts;
    }

}
