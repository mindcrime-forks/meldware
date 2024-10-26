/*
 * Bunisoft the Open Source Communications Company
 * Copyright 2006, Bunisoft Inc.,
 *
 * Portions of this software are Copyright 2006, JBoss Inc., and 
 * individual contributors as indicated by the @authors tag.
 * See the copyright.txt in the distribution for a full listing of
 * individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 *
 * 
 * Additionally, portions of this IMAP code are from the epost project at epostmail.org.
 * These sources are derivitive works in which the original is included under these terms:
 * ----------------------------------------------------------------------------------------
 * "Free Pastry" Peer-to-Peer Application Development Substrate
 *
 * Copyright (C) 2002, Rice University
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are 
 * permitted provided that the following conditions are met:
 *
 *    * Redistributions of source code must retain the above copyright notice, this 
 *      list of conditions and the following disclaimer.
 *    * Redistributions in binary form must reproduce the above copyright notice, 
 *      this list of conditions and the following disclaimer in the documentation and/or 
 *      other materials provided with the distribution.
 *    * Neither the name of Rice University (RICE) nor the names of its contributors may be 
 *      used to endorse or promote products derived from this software without specific prior 
 *      written permission.
 *
 *
 * This software is provided by RICE and the contributors on an "as is" basis, without any 
 * representations or warranties of any kind, express or implied including, but not limited 
 * to, representations or warranties of non-infringement, merchantability or fitness for a 
 * particular purpose. In no event shall RICE or contributors be liable for any direct, 
 * indirect, incidental, special, exemplary, or consequential damages (including, but not 
 * limited to, procurement of substitute goods or services; loss of use, data, or profits; 
 * or business interruption) however caused and on any theory of liability, whether in 
 * contract, strict liability, or tort (including negligence or otherwise) arising in 
 * any way out of the use of this software, even if advised of the possibility of such damage.
 */
package org.buni.meldware.mail.imap4.commands;

import org.buni.meldware.mail.api.Folder;
import org.buni.meldware.mail.api.FolderNotExistsException;
import org.buni.meldware.mail.imap4.IMAP4ProtocolInstance;
import org.buni.meldware.mail.imap4.IMAP4Response;
import org.buni.meldware.mail.imap4.IMAP4ProtocolInstance.ImapState;

/**
 * EXAMINE command.
 * <p>
 * <a href="http://asg.web.cmu.edu/rfc/rfc2060.html#sec-6.3.2">
 * http://asg.web.cmu.edu/rfc/rfc2060.html#sec-6.3.2 </a>
 * </p>
 * @author Michael Barker
 * @version $Revision: 1.6 $
 */
public class ExamineCommand extends AbstractImapCommand {

    String _folder;

    public ExamineCommand() {
        super("EXAMINE");
    }

    public boolean isValidForState(ImapState state) {
        return state == ImapState.AUTHENTICATED;
    }

    public ExamineCommand(String name) {
        super(name);
    }

    public IMAP4Response execute() {
        IMAP4Response response = constructResponse();
        IMAP4ProtocolInstance protocol = getProtocolInstance();

        // before attempting the new selection, deselect any currently selected mailbox
        protocol.clearFolder();
        // and return the state back to AUTHENTICATED from SELECTED
        protocol.setState(ImapState.AUTHENTICATED);

        String[] path = getPath(getFolder());
        try {
            Folder f = protocol.selectFolder(path);
           
            if (this.getClass() == ExamineCommand.class /* || user has read-only-rights */) {
                protocol.setFolderReadOnly(true);               
            } else {
                protocol.setFolderReadOnly(false);
            }
           
            long messages = f.getMessageCount();
            long recent = f.getRecentCount();
            long uid = f.getLikelyUID();
            long unseen = f.getUnseenCount();
   
            protocol.setState(ImapState.SELECTED);
            untaggedResponse(messages + " EXISTS");
            untaggedResponse(recent + " RECENT");
            if (unseen > 0) {
                untaggedSuccess("[UNSEEN %d]", f.getFirstUnseen());                
            }
            untaggedSuccess("[UIDVALIDITY " + f.getId() + "] UIDs valid ");
            untaggedSuccess("[UIDNEXT " + uid + "] Predicted next UID ");
            untaggedResponse("FLAGS (\\Answered \\Flagged \\Deleted \\Seen \\Draft)");
            if (this.getClass() == ExamineCommand.class) {
                untaggedSuccess("[PERMANENTFLAGS ()] No permanent flags permitted");
                taggedSuccess("[READ-ONLY] " + getCmdName() + " completed");
            } else { // SelectCommand.class
                // TODO fill this if-clause for read only folders (per-user permissions)
                // if (client-is-permitted-to-modify-mailbox/messages) {
                untaggedSuccess("[PERMANENTFLAGS (\\* \\Answered \\Flagged \\Deleted \\Seen \\Draft)] Permanent flags");
                taggedSuccess("[READ-WRITE] " + getCmdName() + " completed");
            }
        } catch (FolderNotExistsException e) {
            taggedFailure("%s failure: %s", getCmdName(), e.getMessage());
        }

        flush();
        return response;
    }

    protected boolean isWritable() {
        return false;
    }

    public String getFolder() {
        return _folder;
    }

    public void setFolder(String mailbox) {
        _folder = mailbox;
    }
}
