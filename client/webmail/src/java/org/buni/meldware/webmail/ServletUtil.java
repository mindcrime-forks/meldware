package org.buni.meldware.webmail;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

public class ServletUtil {

    public static String getParameter(HttpServletRequest request, String name) 
            throws ServletException {
        String value = request.getParameter(name);
        if (value == null) {
            throw new ServletException("Parameter: " + name + " not specified");
        }
        return value;
    }
    
    public static long getLongParameter(HttpServletRequest request, String name) 
            throws ServletException {
        String value = request.getParameter(name);
        if (value == null) {
            throw new ServletException("Parameter: " + name + " not specified");
        }
        return Long.parseLong(value);
    }
    
}
