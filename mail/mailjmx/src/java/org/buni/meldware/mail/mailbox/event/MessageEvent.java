package org.buni.meldware.mail.mailbox.event;

/**
 * Event to send when a new message arrives in a mailbox.
 * 
 * @author Michael Barker
 *
 */
public class MessageEvent extends MailboxEvent {

    private static final long serialVersionUID = -3034118622276867511L;
    private final long uid;

    public MessageEvent(long folderId, long uid) {
        super(folderId);
        this.uid = uid;
    }
    
    public long getUid() {
        return uid;
    }
    
    public Type getType() {
        return Type.MESSAGE;
    }

}
