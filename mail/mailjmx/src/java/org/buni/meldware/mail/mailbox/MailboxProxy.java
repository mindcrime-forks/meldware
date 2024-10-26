package org.buni.meldware.mail.mailbox;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.buni.meldware.common.util.ArrayUtil;
import org.buni.meldware.mail.api.FolderNotExistsException;
import org.buni.meldware.mail.api.Hints;

/**
 * Acts as a smart proxy onto a mailbox.
 * 
 * @author Michael.Barker
 *
 */
public class MailboxProxy implements org.buni.meldware.mail.api.Mailbox {

    private final MailboxService service;
    private final Mailbox mailbox;
    private final Hints hints;

    public MailboxProxy(MailboxService service, Mailbox mailbox, Hints hints) {
        this.service = service;
        this.mailbox = mailbox;
        this.hints = hints;
    }
    
    public Collection<String> getAliases() {
        List<String> aliases = new ArrayList<String>();
        for (Alias alias : mailbox.getAliases()) {
            aliases.add(alias.getName());
        }
        return aliases;
    }

    public org.buni.meldware.mail.api.Folder getDefault() {
        return createFolder(service, mailbox.getDefaultInFolder());
    }

    public org.buni.meldware.mail.api.Folder getFolder(String name) {
        Folder f = service.getSubfolderByName(mailbox, name);
        return createFolder(service, f);
    }

    // TODO How should non-existence be handled.
    public org.buni.meldware.mail.api.Folder getFolder(String[] path) {
        FolderProxy fp = null;
        Folder f = service.getSubfolderByPath(mailbox, path);
        if (f != null) {
            fp = createFolder(service, f);
        }
        return fp;
    }
    
    public org.buni.meldware.mail.api.Folder createFolder(String[] path) {
        Folder f = service.createFolder(mailbox, path);
        return createFolder(service, f);
    }
    
    public void deleteFolder(String[] path) {
        Folder toDelete = service.getSubfolderByPath(mailbox, path);
        if (toDelete == null) {
            String msg = String.format("Folder %s does not exist", 
                    ArrayUtil.join(path, "/"));
            throw new FolderNotExistsException(msg);
        }
        service.deleteFolder(toDelete);
    }
    
    public org.buni.meldware.mail.api.Folder getRootFolder() {
        return createFolder(service, mailbox);
    }
    
    private FolderProxy createFolder(MailboxService m, Folder f) {
        return new PassiveFolderProxy(m, f, hints);
    }
    
    protected Hints getHints() {
        return hints;
    }
    
    protected Mailbox getMailbox() {
        return mailbox;
    }
    
    protected MailboxService getService() {
        return service;
    }

}
