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

import java.util.List;
import java.util.regex.Pattern;

import org.buni.meldware.common.logging.Log;
import org.buni.meldware.common.util.ArrayUtil;
import org.buni.meldware.mail.api.Folder;
import org.buni.meldware.mail.api.FolderFilter;
import org.buni.meldware.mail.api.FolderNotExistsException;
import org.buni.meldware.mail.api.Mailbox;
import org.buni.meldware.mail.imap4.IMAP4Protocol;
import org.buni.meldware.mail.imap4.IMAP4ProtocolInstance;
import org.buni.meldware.mail.imap4.IMAP4Response;
import org.buni.meldware.mail.imap4.IMAP4ProtocolInstance.ImapState;

/**
 * LIST command.
 * 
 * <p>
 * <a  href="http://asg.web.cmu.edu/rfc/rfc2060.html#sec-6.3.8">
 * http://asg.web.cmu.edu/rfc/rfc2060.html#sec-6.3.8 </a>
 * </p>
 * @author Michael Barker
 * @version $Revision: 1.12 $
 */
public class ListCommand extends AbstractImapCommand
{

	private final static Log log = Log.getLog(ListCommand.class);
	
    public ListCommand(String name) {
        super(name);
    }
    
    public ListCommand() {
        this("LIST");
    }

    public boolean isValidForState(ImapState state) {
        return state == ImapState.AUTHENTICATED;
    }

    String folder;

    String reference;
   

    public IMAP4Response execute() {
        IMAP4Response response = constructResponse();
        IMAP4ProtocolInstance pi = getProtocolInstance();
        try {
            if (!getFolder().equals("")) {
                Mailbox mailbox = pi.getMailbox();
                Folder refFolder;
                String path;
                if (getFolder().startsWith("/")) {
                    refFolder = mailbox.getRootFolder();
                    path = getFolder();
                } else if (getReference().trim().length() > 0) {
                    String[] reference = getPath(getReference());
                    refFolder = mailbox.getFolder(reference);
                    path = ArrayUtil.join(reference, ",") + "/"  + getFolder();
                } else {
                    refFolder = mailbox.getRootFolder();
                    path = "/"  + getFolder();
                }
                 
                FolderFilter filter = new IMAPFolderFilter(path);
                List<String[]> folders = refFolder.getSubFolders(filter);
                refFolder.close();
                     
                for (String[] folder : folders) {
                    String folderPath = ArrayUtil.join(folder, IMAP4Protocol.DIR_SEPARATOR);
                    untaggedSimpleResponse("() \"" + IMAP4Protocol.DIR_SEPARATOR + "\" \"" + folderPath + "\"");             
                    // TODO                 ^^
                    // Proper processing for these four name attributes needs to be done:
                    //
                    //   \Noinferiors
                    //    It is not possible for any child levels of hierarchy to exist
                    //    under this name; no child levels exist now and none can be
                    //    created in the future.
                    //
                    //   \Noselect
                    //    It is not possible to use this name as a selectable mailbox.
                    //
                    //   \Marked
                    //    The mailbox has been marked "interesting" by the server; the
                    //    mailbox probably contains messages that have been added since
                    //    the last time the mailbox was selected.
                    //
                    //   \Unmarked
                    //    The mailbox does not contain any additional messages since the
                    //    last time the mailbox was selected.
                }
            } 
            
            if (getReference().equals("") && getFolder().equals("")) { 
                // The mail client is requesting our folder separator
                untaggedSimpleResponse("(\\Noselect) \"" + IMAP4Protocol.DIR_SEPARATOR + "\" \"\"");
            }
        } catch (FolderNotExistsException e) {
        	// This isn't really a problem, it just does not exist.
        	log.warn("Folder %s does not exist", getFolder());
        }
        taggedSimpleSuccess();

        flush();
        return response;
    }
   
   
    public String getFolder() {
        return folder;
    }

    public void setFolder(String mailbox) {
        this.folder = mailbox;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    private static class IMAPFolderFilter implements FolderFilter {

        private final Pattern pattern;
        
        public IMAPFolderFilter(String path) {
            String patternStr = path.replace("*", ".*").replace("%", "[^/]*");
            pattern = Pattern.compile(patternStr);
        }


        public boolean match(String path) {
            return pattern.matcher(path).matches();
        }

        public boolean isRecursive() {
            return false;
        }
    }
}