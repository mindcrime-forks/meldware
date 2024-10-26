package org.buni.meldware.webmail.command.xml;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;

import org.buni.meldware.mail.userapi.MailSender;
import org.w3c.dom.Document;

public interface XMLCommand {
    public static final String SUCCESS = "success";
    public static final String ERROR = "error";
    public void setMailSender(MailSender sender);
    public String execute(HttpServletRequest request,Document doc,PrintWriter out);
}
