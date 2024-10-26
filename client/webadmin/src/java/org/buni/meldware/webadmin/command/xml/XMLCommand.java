package org.buni.meldware.webadmin.command.xml;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;

import org.w3c.dom.Document;

public interface XMLCommand {
    public static final String SUCCESS = "success";
    public static final String ERROR = "error";
    public String execute(HttpServletRequest request,Document doc,PrintWriter out);
}
