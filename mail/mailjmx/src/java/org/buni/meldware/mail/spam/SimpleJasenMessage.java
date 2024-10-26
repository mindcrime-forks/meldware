package org.buni.meldware.mail.spam;

import javax.mail.internet.InternetAddress;

import org.jasen.error.JasenException;
import org.jasen.interfaces.JasenMessage;

public class SimpleJasenMessage implements JasenMessage {
    
    private static final long serialVersionUID = 1L;
    private String text;
    private String html;
    private String sender;
    private InternetAddress from;
    private String subject;

    public SimpleJasenMessage(String text, String html, String sender, 
            String subject, String[] attachmentNames, InternetAddress from) {
        this.text = text;
        this.html = html;
        this.sender = sender;
        this.from = from;
        this.subject = subject;
    }
    
    public String[] getAttachmentNames() throws JasenException {
        return null;
    }

    public String getEnvelopeSender() throws JasenException {
        return sender;
    }

    public InternetAddress getFrom() throws JasenException {
        return from;
    }

    public String getHtmlPart() throws JasenException {
        return html;
    }

    public String getTextPart() throws JasenException {
        return text;
    }
    
    public String getSubject() {
        return subject;
    }

}
