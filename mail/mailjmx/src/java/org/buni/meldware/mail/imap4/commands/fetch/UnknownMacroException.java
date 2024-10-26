package org.buni.meldware.mail.imap4.commands.fetch;

import org.buni.meldware.mail.MailException;

public class UnknownMacroException extends MailException {

    private static final long serialVersionUID = 4820330867726351582L;

    /**
     * @param msg
     * @param args
     */
    public UnknownMacroException(String msg, Object... args) {
        super(msg, args);
        // TODO Auto-generated constructor stub
    }

    
}
