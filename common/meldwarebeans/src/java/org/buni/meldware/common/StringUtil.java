package org.buni.meldware.common;

import java.util.Random;

public class StringUtil {
    private static final Random rnd = new Random();
    public static String randomString(char[] chars, int len) {
        String val = "";
        for (int i = 0; i < len; i++) {
            val += chars[rnd.nextInt(chars.length)];
        }
        return val;
    }
    
    public static String randomAlphabeticalString(int len) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        return randomString(chars.toCharArray(),len);
    }

	/**
     * The field fuction is used to generate an XML-style
     * tag/value.
     */
    public static String field(String tag, String val) {
        return "<"+tag+">"+val+"</"+tag+">";
    }
}
