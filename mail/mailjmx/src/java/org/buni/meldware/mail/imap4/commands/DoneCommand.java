package org.buni.meldware.mail.imap4.commands;

import org.buni.meldware.common.logging.Log;
import org.buni.meldware.mail.api.ActiveFolder;
import org.buni.meldware.mail.api.FolderListener;
import org.buni.meldware.mail.imap4.IMAP4ProtocolInstance;
import org.buni.meldware.mail.imap4.IMAP4Response;
import org.buni.meldware.mail.imap4.IMAP4ProtocolInstance.ImapState;

public class DoneCommand extends AbstractImapCommand {

    private final Log log = Log.getLog(DoneCommand.class);

    public DoneCommand() {
        super("DONE");
    }

    @Override
    public IMAP4Response execute() {
        IMAP4Response response = constructResponse();
        IMAP4ProtocolInstance pi = getProtocolInstance();
        
        final ActiveFolder folder = pi.getFolderProxy();
        synchronized (pi) {
            FolderListener fl = pi.clearListener();
            folder.removeFolderListener(fl);
            setTag(pi.clearIdleTag());
            pi.setState(ImapState.SELECTED);
            log.debug("IDLE terminated");
            taggedSuccess("IDLE terminated");
            flush();
        }
        return response;
    }

    @Override
    public boolean isValidForState(ImapState state) {
        return state == ImapState.IDLE;
    }

}
