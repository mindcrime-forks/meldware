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

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.buni.meldware.common.logging.Log;
import org.buni.meldware.mail.MailException;
import org.buni.meldware.mail.StreamWriteException;
import org.buni.meldware.mail.api.Folder;
import org.buni.meldware.mail.api.FolderMessage;
import org.buni.meldware.mail.api.Range;
import org.buni.meldware.mail.imap4.IMAP4OutputStream;
import org.buni.meldware.mail.imap4.IMAP4ProtocolInstance;
import org.buni.meldware.mail.imap4.IMAP4Response;
import org.buni.meldware.mail.imap4.IMAP4ProtocolInstance.ImapState;
import org.buni.meldware.mail.imap4.commands.fetch.BodyPartRequest;
import org.buni.meldware.mail.imap4.commands.fetch.FetchPart;
import org.buni.meldware.mail.imap4.commands.fetch.MessagePropertyPart;
import org.buni.meldware.mail.imap4.commands.fetch.MsgSetFilter;
import org.buni.meldware.mail.message.Mail;

/**
 * FETCH command.
 *
 * <p>
 * <a  href="http://asg.web.cmu.edu/rfc/rfc2060.html#sec-6.4.5">
 * http://asg.web.cmu.edu/rfc/rfc2060.html#sec-6.4.5 </a>
 * </p>
 *
 * <p>
 * <a  href="http://asg.web.cmu.edu/rfc/rfc2060.html#sec-7.4.2">
 * http://asg.web.cmu.edu/rfc/rfc2060.html#sec-7.4.2 </a>
 * </p>
 * @version $Revision: 1.13 $
 */
public class FetchCommand extends AbstractImapCommand {

    // TODO Make this configurable.
    private static final int BUFFER_SIZE = 8192;
    private final static Log log = Log.getLog(FetchCommand.class);

    public FetchCommand(boolean isUID) {
        super(isUID ? "UID FETCH" : "FETCH");
        this.isUID = isUID;
    }

    public boolean isValidForState(ImapState state) {
        return state == ImapState.SELECTED;
    }

    private boolean isUID;

    private MsgSetFilter range;

    private List<FetchPart> parts = new ArrayList<FetchPart>();

    public IMAP4Response execute() {
        IMAP4Response result = constructResponse();
        IMAP4ProtocolInstance protocol = getProtocolInstance();

        // If this is a UID request and the UID command is not
        // present make it the first portion of the request.
        MessagePropertyPart uidPart = new MessagePropertyPart("UID");
        if ((isUID) && (!parts.contains(uidPart))) {
            parts.add(0, uidPart);
        }
        
        boolean isPeek = true;
        boolean requiresMessage = false;
        for (FetchPart obj : parts) {
           if (obj instanceof BodyPartRequest && ((BodyPartRequest)obj).getPeek() == false) {
              isPeek = false;
           }
           requiresMessage = requiresMessage || obj.requiresMessage();
        }
        
        IMAP4OutputStream out = new IMAP4OutputStream(
                new BufferedOutputStream(getOutputStream(), BUFFER_SIZE), 
                Mail.ENCODING);
        
        try {
            Folder f = protocol.getFolderProxy();
            Range[] ranges = range.getRanges();
            List<FolderMessage> messages;
            if (requiresMessage) {
                messages = f.getMessages(isUID, ranges);
            } else {
                messages = f.getFlags(isUID, ranges);
            }
            for (FolderMessage msg : messages) {
                untaggedResponse(out);
                int seqNum = msg.getSeqNum();
                fetchMessage(msg, seqNum, out);
            }
            if (!isPeek && !protocol.isFolderReadOnly()) {
            	f.setFlag(isUID, ranges, Folder.FlagType.SEEN, true);
            }
            if (!protocol.isFolderReadOnly()) {
            	f.setFlag(isUID, ranges, Folder.FlagType.RECENT, false);
            }
            
            taggedSimpleSuccess(out);
            out.flush();
            flush();
        } catch (IOException e) {
            result.setFinish(true);
            log.error(e);
        } catch (StreamWriteException e) {
            result.setFinish(true);
            log.error(e);
        } catch (MailException e) {
            // XXX: Report unknown error back to the client.
            result.setFinish(true);
            log.error(e);
        }
        
        return result;
    }

    private void fetchMessage(FolderMessage msg, int seq, 
            IMAP4OutputStream out) {
        
        try {
            out.write(String.valueOf(seq));
            out.write(" FETCH (");

            for (Iterator<FetchPart> i = parts.iterator(); i.hasNext();) {
                FetchPart part = i.next();
                part.fetch(msg, out);
                if (i.hasNext()) {
                    out.write(" ");
                }
            }
            
            out.write(")\r\n");
        } catch (IOException e) {
            throw new StreamWriteException(e);
        }
    }

    public void appendPartRequest(FetchPart part) {
        if (parts.contains(part)) {
            return;
        }

        parts.add(part);
    }
    

    public List<FetchPart> getParts() {

        return this.parts;
    }

    public MsgSetFilter getRange() {

        return this.range;
    }

    public void setRange(MsgSetFilter range) {
        this.range = range;
    }
    
    public boolean isUid() {
        return isUID;
    }
}
