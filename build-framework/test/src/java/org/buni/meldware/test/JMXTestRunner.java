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
package org.buni.meldware.test;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestFailure;
import junit.framework.TestListener;
import junit.framework.TestResult;
import junit.framework.TestSuite;

import org.buni.meldware.common.logging.Log;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

/**
 * @author Michael Barker
 * 
 */
public class JMXTestRunner implements JMXTestRunnerMBean {
    
    private final static Log log = Log.getLog(JMXTestRunner.class);
    Element properties;

    /**
     * Runs the test returning summary of the results.
     * 
     * @param suiteClassName
     *            The name of the suite to run.
     * @return A summary of the tests run.
     */
    public String runTest(String suiteClassName) {
        try {
            TestResult result = new TestResult();
            TestSuite suite = getTestSuite(suiteClassName);
            ResultFormatter formatter = new ResultFormatter(suite, result);
            result.addListener(formatter);
            long t0 = System.currentTimeMillis();
            suite.run(result);
            long t1 = System.currentTimeMillis();
            formatter.setTime(t1 - t0);
            StringBuffer sb = new StringBuffer();
            sb.append(suiteClassName);
            sb.append(", Errors: " + result.errorCount());
            sb.append(", Failures: " + result.failureCount());
            sb.append(", Total: " + result.runCount());
            sb.append("\n");
            sb.append(formatter.toCSV());
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public static Class loadClass(String className)
            throws ClassNotFoundException {
        Thread t = Thread.currentThread();
        ClassLoader cl = t.getContextClassLoader();
        return cl.loadClass(className);
    }

    /**
     * Runs an individual test in the Application Server.
     * 
     * @param testClass
     *            The name of the test class.
     * @param testMethod
     *            The name of the test method.
     */
    public String runTest(String testClass, String testMethod) {
        String reason = "N/A";
        String status;
        
        log.info("-- Start Test %s.%s --", testClass, testMethod);

        try {
            Class c = loadClass(testClass);
            Constructor cns = c.getConstructor(String.class);
            Object o = cns.newInstance(testMethod);
            if (o instanceof TestCase) {
                TestCase tc = (TestCase) o;
                TestSuite ts = new TestSuite();
                ts.addTest(tc);
                TestResult tr = new TestResult();
                ts.run(tr);
                if (tr.errorCount() > 0) {
                    status = "error";
                    Enumeration e = tr.errors();
                    if (e.hasMoreElements()) {
                        TestFailure tf = (TestFailure) e.nextElement();
                        tf.thrownException().printStackTrace();
                        reason = tf.toString();
                    }
                } else if (tr.failureCount() > 0) {
                    status = "failure";
                    Enumeration e = tr.failures();
                    if (e.hasMoreElements()) {
                        TestFailure tf = (TestFailure) e.nextElement();
                        tf.thrownException().printStackTrace();
                        reason = tf.toString();
                    }
                } else {
                    status = "success";
                }
            } else {
                throw new Exception("Class: " + testClass 
                        + " is not a test case");
            }
        } catch (Exception e) {
            status = "error";
            reason = e.getMessage();
        }

        log.info("-- End Test %s.%s --", testClass, testMethod);
        
        return status + ":" + reason;
    }

    public String runTestXML(String suiteClassName) {
        TestSuite suite = getTestSuite(suiteClassName);
        TestResult result = new TestResult();
        ResultFormatter formatter = new ResultFormatter(suite, result);
        result.addListener(formatter);
        long t0 = System.currentTimeMillis();
        suite.run(result);
        long t1 = System.currentTimeMillis();
        formatter.setTime(t1 - t0);
        return formatter.toString();
    }

    private TestSuite getTestSuite(String suiteClassName) {
        TestSuite suite;
        try {
            Class clazz = Thread.currentThread().getContextClassLoader()
                    .loadClass(suiteClassName);
            try {
                Method m = clazz.getMethod("suite", new Class[0]);
                suite = (TestSuite) m.invoke(null, new Object[0]);
            } catch (NoSuchMethodException e1) {
                suite = new TestSuite(clazz, clazz.getName());
            }
            return suite;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load testSuite "
                    + suiteClassName, e);
        }
    }

    static class ResultFormatter implements TestListener {
        private Map<Test, TestCaseResult> testCaseResults;

        private TestResult result;

        private TestSuite suite;

        private long time = 0;

        public ResultFormatter(TestSuite suite, TestResult result) {
            this.testCaseResults = new HashMap<Test, TestCaseResult>();
            this.result = result;
            this.suite = suite;
        }

        public TestCaseResult getTestCaseResult(Test test) {
            TestCaseResult tcr = testCaseResults.get(test);
            if (tcr == null) {
                String className = test.getClass().getName();
                String testName = JUnitVersionHelper.getTestCaseName(test);
                tcr = new TestCaseResult(className, testName);
                tcr.setStartTime();
                testCaseResults.put(test, tcr);
            }
            return tcr;
        }

        /**
         * @see junit.framework.TestListener#addError(junit.framework.Test,
         *      java.lang.Throwable)
         */
        public void addError(Test test, Throwable t) {
            TestCaseResult tcr = getTestCaseResult(test);
            tcr.setEndTime();
            tcr.setThrowable(t);
        }

        /**
         * @see junit.framework.TestListener#addFailure(junit.framework.Test,
         *      junit.framework.AssertionFailedError)
         */
        public void addFailure(Test test, AssertionFailedError error) {
            TestCaseResult tcr = getTestCaseResult(test);
            tcr.setEndTime();
            tcr.setAssertionFailedError(error);
        }

        /**
         * @see junit.framework.TestListener#endTest(junit.framework.Test)
         */
        public void endTest(Test test) {
            TestCaseResult tcr = getTestCaseResult(test);
            tcr.setEndTime();
        }

        /**
         * @see junit.framework.TestListener#startTest(junit.framework.Test)
         */
        public void startTest(Test test) {
        }

        public void setTime(long time) {
            this.time = time;
        }

        public String toString() {
            try {
                TransformerFactory f = TransformerFactory.newInstance();
                Transformer t = f.newTransformer();
                StringWriter sw = new StringWriter();
                StreamResult tgt = new StreamResult(sw);
                DOMSource src = new DOMSource(getDocument());
                t.transform(src, tgt);
                return sw.toString();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        public String toCSV() {
            StringBuilder sb = new StringBuilder();

            for (Iterator i = testCaseResults.values().iterator(); i.hasNext();) {
                TestCaseResult tcr = (TestCaseResult) i.next();
                sb.append(tcr.getCSV());
                sb.append("\n");
            }

            return sb.toString();
        }

        public Document getDocument() throws ParserConfigurationException {
            DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = f.newDocumentBuilder();
            Document d = builder.newDocument();

            int failures = result.failureCount();
            int errors = result.errorCount();
            int count = result.runCount();
            double elapsed = (double) time / 1000d;

            Element eTestresult = d.createElement("testsuite");
            eTestresult.setAttribute("errors", String.valueOf(errors));
            eTestresult.setAttribute("failures", String.valueOf(failures));
            eTestresult.setAttribute("name", suite.getName());
            eTestresult.setAttribute("tests", String.valueOf(count));
            eTestresult.setAttribute("time", String.valueOf(elapsed));

            d.appendChild(eTestresult);

            Element eProperties = getProperties(d);
            eTestresult.appendChild(eProperties);

            for (Iterator i = testCaseResults.values().iterator(); i.hasNext();) {
                TestCaseResult tcr = (TestCaseResult) i.next();
                Element eTestcase = tcr.getElement(d);
                eTestresult.appendChild(eTestcase);
            }

            Element eSystemOut = d.createElement("system-out");
            CDATASection cSystemOut = d.createCDATASection("");
            eSystemOut.appendChild(cSystemOut);
            eTestresult.appendChild(eSystemOut);

            Element eSystemErr = d.createElement("system-err");
            CDATASection cSystemErr = d.createCDATASection("");
            eSystemOut.appendChild(cSystemErr);
            eTestresult.appendChild(eSystemErr);

            return d;
        }

        /**
         * 
         * 
         * @param d
         * @return
         */
        public Element getProperties(Document d) {
            Element eProperties = d.createElement("properties");

            Properties p = System.getProperties();

            for (Iterator i = p.entrySet().iterator(); i.hasNext();) {
                Map.Entry entry = (Map.Entry) i.next();
                String key = (String) entry.getKey();
                String value = (String) entry.getValue();
                if (!"line.separator".equals(key)) {
                    Element eProperty = d.createElement("property");
                    eProperty.setAttribute("name", key);
                    eProperty.setAttribute("value", value);
                    eProperties.appendChild(eProperty);
                }
            }

            return eProperties;
        }

    }

    /**
     * Holds a test case result.
     * 
     */
    static class TestCaseResult {
        private String className;

        private String testName;

        private Throwable t = null;

        private AssertionFailedError error = null;

        private long startTime = 0;

        private long endTime = 0;

        public TestCaseResult(String className, String testName) {
            this.className = className;
            this.testName = testName;
        }

        public void setThrowable(Throwable t) {
            this.t = t;
        }

        public void setAssertionFailedError(AssertionFailedError error) {
            this.error = error;
        }

        public void setStartTime() {
            startTime = System.currentTimeMillis();
        }

        public double getElapsedTime() {
            return endTime - startTime;
        }

        public void setEndTime() {
            endTime = System.currentTimeMillis();
        }

        public String getCSV() {
            StringBuilder sb = new StringBuilder();
            sb.append(this.testName);
            sb.append(",");
            sb.append(String.valueOf(getElapsedTime() / 1000d));
            sb.append(",");
            if (t != null) {
                sb.append("error");
                sb.append(",");
                sb.append(t.getMessage());
                sb.append("\n");
                sb.append(getStackTrace(t));
            } else if (error != null) {
                sb.append("failure");
                sb.append(",");
                sb.append(error.getMessage());
            } else {
                sb.append("success");
            }

            return sb.toString();
        }

        public Element getElement(Document d) {
            double elapsed = getElapsedTime() / 1000d;

            Element eTestcase = d.createElement("testcase");
            eTestcase.setAttribute("classname", this.className);
            eTestcase.setAttribute("name", this.testName);
            eTestcase.setAttribute("time", String.valueOf(elapsed));

            if (t != null) {
                Element eError = d.createElement("error");
                eError.setAttribute("type", t.getClass().getName());
                eTestcase.appendChild(eError);

                Text tError = d.createTextNode(getStackTrace(t));
                eError.appendChild(tError);
            } else if (error != null) {
                Element eFailure = d.createElement("failure");
                eFailure.setAttribute("message", error.getMessage());
                eFailure.setAttribute("type", error.getClass().getName());
                eTestcase.appendChild(eFailure);

                Text tFailure = d.createTextNode(error.getMessage());
                eFailure.appendChild(tFailure);
            }

            return eTestcase;
        }

        private String getStackTrace(Throwable t) {
            StringWriter sw = new StringWriter();
            t.printStackTrace(new PrintWriter(sw));
            return sw.toString();
        }
    }

}
