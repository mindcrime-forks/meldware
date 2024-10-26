package org.buni.meldware.mail.util;

import junit.framework.TestCase;

public class TestTextUtil extends TestCase {

    public void testTrimRight() {
        
        String s1 = "abc   ";
        assertEquals("abc", TextUtil.trimRight(s1));
        
        String s2 = "abc\r\n\t    ";
        assertEquals("abc", TextUtil.trimRight(s2));
    }
}
