package org.buni.meldware.mail.imap4.commands;

import org.buni.meldware.common.logging.Log;
import org.buni.meldware.mail.api.ActiveFolder;
import org.buni.meldware.mail.api.FolderListener;
import org.buni.meldware.mail.api.FolderUpdates;
import org.buni.meldware.mail.imap4.IMAP4ProtocolInstance;
import org.buni.meldware.mail.imap4.IMAP4Response;
import org.buni.meldware.mail.imap4.IMAP4ProtocolInstance.ImapState;

public class IdleCommand extends AbstractImapCommand {

    private final Log log = Log.getLog(IdleCommand.class);
    
    public IdleCommand() {
        super("IDLE");
    }

    @Override
    public IMAP4Response execute() {
        IMAP4Response response = constructResponse();
        final IMAP4ProtocolInstance pi = getProtocolInstance();
        
        final ActiveFolder folder = pi.getFolderProxy();
        FolderListener fl = new FolderListener() {
            public void folderChanged(FolderUpdates updates) {
                synchronized (pi) {
                    if (pi.getState() == ImapState.IDLE) {
                        untaggedUpdates(updates);
                        flush();
                    }
                }
            }
        };
        pi.setListener(fl);
        pi.setState(ImapState.IDLE);
        pi.setIdleTag(getTag());
        sendContinuation("idling");
        folder.addFolderListener(fl);
        
        // First check for any pending updates.
        FolderUpdates updates = folder.refresh();
        if (updates.hasChanged()) {
            untaggedUpdates(updates);
        }
        
        flush();
        log.debug("Connection idling");
        return response;
    }

    @Override
    public boolean isValidForState(ImapState state) {
        return state == ImapState.SELECTED;
    }

}
