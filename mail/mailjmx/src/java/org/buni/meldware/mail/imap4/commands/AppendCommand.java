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

import org.buni.meldware.mail.api.Folder;
import org.buni.meldware.mail.api.Mailbox;
import org.buni.meldware.mail.imap4.IMAP4Protocol;
import org.buni.meldware.mail.imap4.IMAP4ProtocolInstance;
import org.buni.meldware.mail.imap4.IMAP4Response;
import org.buni.meldware.mail.imap4.IMAP4ProtocolInstance.ImapState;
import org.buni.meldware.mail.message.Message;
import org.jboss.aspects.tx.Tx;
import org.jboss.aspects.tx.TxType;

/**
 * APPEND command.
 * 
 * <p>
 * <a  href="http://asg.web.cmu.edu/rfc/rfc2060.html#sec-6.3.11">
 * http://asg.web.cmu.edu/rfc/rfc2060.html#sec-6.3.11 </a>
 * </p>
 * @author Rice University epost contributors
 * @author Andrew C. Oliver
 * @author Michael Barker
 * @version $Revision: 1.7 $
 */
public class AppendCommand extends AbstractImapCommand {

    public AppendCommand() {
        super("APPEND");
    }

    public boolean isValidForState(ImapState state) {
        return state == ImapState.AUTHENTICATED;
    }

    List<String> _flags;

    String _date;

    String _folder;

    int _len = -1;
    
    private Message message;

    //IOException _ioe;

    @Tx(TxType.REQUIRED)
    public IMAP4Response execute() {
        IMAP4ProtocolInstance pi = this.getProtocolInstance();
        String[] path = getFolder().split(IMAP4Protocol.DIR_SEPARATOR);
        Mailbox mbox = pi.getMailbox();
        Folder f = mbox.getFolder(path);
        IMAP4Response response = constructResponse();
        f.append(message);
        // TODO Date/flag handling.
        
        taggedSimpleSuccess();
        flush();
        return response;
    }

    public String getDate() {

        return _date;
    }

    public List<String> getFlags() {

        return _flags;
    }

    public void setContentLength(int len) {
        _len = len;
    }

    public void setDate(String date) {
        _date = date;
    }

    public void setFlags(List<String> flags) {
        _flags = flags;
    }

    public String getFolder() {

        return _folder;
    }

    public void setFolder(String mailbox) {
        _folder = mailbox;
    }
    
    public void setMessage(Message message) {
        this.message = message;
    }
    
}
