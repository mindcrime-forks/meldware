package org.buni.meldware.common.util;

public class StatusUtil {
    

    public static final short CONFIRMED = 0;
    public static final short CANCELLED = 1;
    public static final short TENTATIVE = 2;
    public static final short NEEDS_ACTION = 3;
    public static final short COMPLETED = 4;
    public static final short IN_PROCESS = 5;
    public static final short DRAFT = 6;
    public static final short FINAL = 7;
    
    private static String[] codes = new String[]{"Confirmed","Cancelled","Tentative","Needs_Action","Completed",
        "In_Process","Draft","Final"};

    public static short toCode(String status) {
        if (isNumber(status)) {
            return (short)Short.parseShort(status);
        }
        for(int i = 0; i < codes.length; i++) {
            if (codes[i].toUpperCase().equals(status.toUpperCase())) {
                return (short)i;
            }
        }
        return -1;
    }
    
    public static String codeToStatus(short status) {
        return codes[status].toUpperCase();
    }
    
    private static boolean isNumber(String num) {
        try {
            Integer.decode(num);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

}
