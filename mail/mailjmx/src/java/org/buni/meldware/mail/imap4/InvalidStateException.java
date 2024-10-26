package org.buni.meldware.mail.imap4;

import org.buni.meldware.mail.MailException;

public class InvalidStateException extends MailException {

    /**
     * 
     */
    private static final long serialVersionUID = -5494981895039580055L;

    public InvalidStateException(String message) {
        super(message);
    }
}
