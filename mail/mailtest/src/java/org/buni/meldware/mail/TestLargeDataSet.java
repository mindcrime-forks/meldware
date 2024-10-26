package org.buni.meldware.mail;

import org.buni.meldware.test.JMXTestWrapper;

import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TestLargeDataSet extends TestCase {

    public static TestSuite suite() {
        return JMXTestWrapper.suite(TestLargeDataSet.class);
    }
    
    public TestLargeDataSet(String name) {
        super(name);
    }
    
    public void testSetup() {
        DataSetup ds = new DataSetup();
        for (int i = 1; i <= 1000; i++) {
            ds.createMailbox("user_" + i);
        }
    }
}
