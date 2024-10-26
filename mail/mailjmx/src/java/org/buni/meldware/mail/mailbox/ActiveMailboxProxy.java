package org.buni.meldware.mail.mailbox;

import org.buni.meldware.mail.api.ActiveFolder;
import org.buni.meldware.mail.api.ActiveMailbox;
import org.buni.meldware.mail.api.Hints;

public class ActiveMailboxProxy extends MailboxProxy implements ActiveMailbox {

    public ActiveMailboxProxy(MailboxService service, Mailbox mailbox, Hints hints) {
        super(service, mailbox, hints);
    }

    @Override
    public ActiveFolder createFolder(String[] path) {
        final MailboxService service = getService();
        Folder f = service.createFolder(getMailbox(), path);
        return createFolder(service, f);
    }

    @Override
    public ActiveFolder getFolder(String name) {
        MailboxService service = getService();
        Folder f = service.getSubfolderByName(getMailbox(), name);
        return createFolder(service, f);
    }

    @Override
    public ActiveFolder getFolder(String[] path) {
        ActiveFolderProxy fp = null;
        MailboxService service = getService();
        Folder f = service.getSubfolderByPath(getMailbox(), path);
        if (f != null) {
            fp = createFolder(service, f);
        }
        return fp;
    }

    @Override
    public ActiveFolder getRootFolder() {
        return createFolder(getService(), getMailbox());
    }
    
    @Override
    public ActiveFolder getDefault() {
        return createFolder(getService(), getMailbox().getDefaultInFolder());        
    }
    
    private ActiveFolderProxy createFolder(MailboxService m, Folder f) {
        return new ActiveFolderProxy(m, f, getHints());
    }
    

}
