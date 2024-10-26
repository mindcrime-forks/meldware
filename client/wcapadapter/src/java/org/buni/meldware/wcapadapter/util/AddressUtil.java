package org.buni.meldware.wcapadapter.util;

/**
 * Utilities for dealing with email addresses as applies to wcap
 * @author Andrew C. Oliver &lt;acoliver@buni.org&gt;
 */
public class AddressUtil {

    /**
     * remove "mailto" when it proceeds an email address.  normally we want to use
     * these as userids.
     * @param address to remove the mailto: from (case insensitive for lightning .3/.5
     * @return address without mailto:
     */
    public static String removeMailTo(final String address) {
        String result = address;
        String loweraddress = address.toLowerCase();
        int start = loweraddress.indexOf("mailto:");
        int end = start+"mailto:".length();
        String part1="";
        if (start != 0) { 
            part1 = address.substring(0,start); //shouldn't happen but it is only our job to remove
        }                                       //mailto: not judge where it was located
        result = part1+address.substring(end);
        return result;
    }

}
 