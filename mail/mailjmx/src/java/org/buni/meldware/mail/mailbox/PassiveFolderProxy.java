package org.buni.meldware.mail.mailbox;

import org.buni.meldware.mail.api.Hints;

public class PassiveFolderProxy extends FolderProxy {

    public PassiveFolderProxy(MailboxService service, Folder folder, Hints hints) {
        super(service, folder, hints);
    }
    
    public void open() {
        loadFolderInfo();
    }

    @Override
    protected FolderProxy newInstance(MailboxService service, Folder f, Hints hint) {
        return new PassiveFolderProxy(service, f, hint);
    }

    public void close() {
        // TODO: Remove locks.
        // No-op
    }

}
