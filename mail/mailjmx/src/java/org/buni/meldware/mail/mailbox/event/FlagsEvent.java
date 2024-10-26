package org.buni.meldware.mail.mailbox.event;

public class FlagsEvent extends MailboxEvent {

    private static final long serialVersionUID = -4222181202319870307L;
    private final long[] uids;
    private final String[] flags;

    public FlagsEvent(long folderId, long[] uids, String[] flags) {
        super(folderId);
        this.uids = uids;
        this.flags = flags;
    }
    
    public String[] getFlags() {
        return flags;
    }

    public long[] getUids() {
        return uids;
    }

    @Override
    public Type getType() {
        return Type.FLAGS;
    }

    
}
