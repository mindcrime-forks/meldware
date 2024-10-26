package org.buni.meldware.integration.opends;

import junit.framework.Test;
import junit.framework.TestSuite;

public class MainSuite {

    public static Test suite() {
        TestSuite ts = new TestSuite("OpenDS Main Test Suite");
        //$JUnit-BEGIN$
        ts.addTest(TestOpenDS.suite());
        //$JUnit-END$
        return ts;
    }

    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(MainSuite.suite());
    }
}
