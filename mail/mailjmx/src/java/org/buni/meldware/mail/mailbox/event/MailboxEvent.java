package org.buni.meldware.mail.mailbox.event;

import java.io.Serializable;

public abstract class MailboxEvent implements Serializable {

    public enum Type {
        MESSAGE,
        FLAGS,
        EXPUNGE
    }
    
    private final long folderId;
    
    public MailboxEvent(long folderId) {
        this.folderId = folderId;
    }
    
    public long getFolderId() {
        return folderId;
    }
    
    public abstract Type getType();
}
