package org.buni.meldware.mail.mailbox.event;


public class ExpungeEvent extends MailboxEvent {

    private static final long serialVersionUID = 6139364167262569857L;
    private final long version;
    
    public ExpungeEvent(long folderId, long version) {
        super(folderId);
        this.version = version;
    }
    
    public long getVersion() {
        return version;
    }
    
    @Override
    public Type getType() {
        return Type.EXPUNGE;
    }

}
