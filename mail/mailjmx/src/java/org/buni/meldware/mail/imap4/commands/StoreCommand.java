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

import java.util.ArrayList;
import java.util.List;

import org.buni.meldware.mail.api.Folder;
import org.buni.meldware.mail.api.FolderMessage;
import org.buni.meldware.mail.api.Range;
import org.buni.meldware.mail.imap4.IMAP4ProtocolInstance;
import org.buni.meldware.mail.imap4.IMAP4Response;
import org.buni.meldware.mail.imap4.IMAP4ProtocolInstance.ImapState;
import org.buni.meldware.mail.imap4.commands.fetch.MsgSetFilter;

/**
 * STORE command.
 * 
 * <p>
 * <a  href="http://asg.web.cmu.edu/rfc/rfc2060.html#sec-6.4.6">
 * http://asg.web.cmu.edu/rfc/rfc2060.html#sec-6.4.6 </a>
 * </p>
 * @version $Revision: 1.7 $
 * @author epost developers
 * @author Andrew C. Oliver
 * @author Michael Barker
 */
public class StoreCommand extends AbstractImapCommand {

    public StoreCommand(boolean isUID) {
        super(isUID ? "UID STORE" : "STORE");
        this.isUID = isUID;
    }

    public boolean isValidForState(ImapState state) {
        return state == ImapState.SELECTED;
    }
    
    public enum Action { ADD, REMOVE, REPLACE };
    
    boolean isUID;
    
    MsgSetFilter range;

    List<String> flags = new ArrayList<String>();

    String type;
    
    private Action action;
    private boolean silent;

    public IMAP4Response execute() {
        IMAP4Response response = constructResponse();
        IMAP4ProtocolInstance pi = getProtocolInstance();
        if (pi.isFolderReadOnly() == true) {
           taggedFailure("STORE failure: can't store: this folder is opened Read-Only.");
           return response;
        }
        
        Folder f = pi.getFolderProxy();
        Range[] ranges = range.getRanges();
        List<FolderMessage> messages = f.getMessages(isUID, ranges);
        
        for (int i = 0; i < messages.size(); i++) {
            FolderMessage message = messages.get(i);
            switch (action) {
            case ADD:
                message.setFlags(false, getFlags());
                break;
            case REPLACE:
                message.setFlags(true, getFlags());
                break;
            case REMOVE:
                message.removeFlags(getFlags());
                break;
            }            
            if (!isSilent()) {
                int seqNum = message.getSeqNum();
                StringBuffer result = new StringBuffer();
                result.append(seqNum
                      ).append(" FETCH (FLAGS ("
                      ).append(message.getFlagString()
                      ).append(")");
                if (isUID) {
                   result.append(" UID "
                         ).append(message.getUid());
                }
                result.append(")");
                untaggedResponse(result.toString());                    
            }
        }
        
        taggedSimpleSuccess();
        flush();
        return response;
    }
    
    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public boolean isSilent() {
        return silent;
    }

    public void setSilent(boolean silent) {
        this.silent = silent;
    }

    public void setFlags(List<String> flags) {
        this.flags = flags;
    }

    public List<String> getFlags() {
        return flags;
    }

    public MsgSetFilter getRange() {
        return range;
    }

    public void setRange(MsgSetFilter range) {
        this.range = range;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }
    
    public boolean isUid() {
        return isUID;
    }    
}
