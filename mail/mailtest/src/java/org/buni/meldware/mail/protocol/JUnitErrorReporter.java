package org.buni.meldware.mail.protocol;

import junit.framework.TestCase;

public class JUnitErrorReporter extends TestCase implements ErrorReporter {

    public void report(String msg) {
        assertTrue(msg, false);
    }

}
