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

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.buni.meldware.common.logging.Log;
import org.buni.meldware.mail.StreamWriteException;
import org.buni.meldware.mail.api.ActiveFolder;
import org.buni.meldware.mail.api.FolderUpdates;
import org.buni.meldware.mail.imap4.IMAP4OutputStream;
import org.buni.meldware.mail.imap4.IMAP4Protocol;
import org.buni.meldware.mail.imap4.IMAP4ProtocolInstance;
import org.buni.meldware.mail.imap4.IMAP4Request;
import org.buni.meldware.mail.imap4.IMAP4Response;
import org.buni.meldware.mail.imap4.IMAP4ProtocolInstance.ImapState;

/**
 * @version $Revision: 1.11 $
 */
public abstract class AbstractImapCommand {
    
    private final static Log log = Log.getLog(AbstractImapCommand.class);
    
    String _tag;

    IMAP4ProtocolInstance protocol;

    IMAP4Request request;

    IMAP4Response response;

    PrintWriter writer;

    //   ImapConnection _conn;
    //   ImapState _state;
    final String _cmdName;

    public AbstractImapCommand(String name) {
        _cmdName = name;
    }

    void taggedResponse(String s) {
        try {
            response.getWriter().println(_tag + " " + s);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    void taggedResponse(String format, Object...args) {
        try {
            String s = String.format(format, args);
            response.getWriter().println(_tag + " " + s);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    protected void taggedSuccess(String s) {
        try {
            response.getWriter().println(_tag + " OK " + s);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected void taggedSimpleSuccess() {
        //dono what unsolicited did TODO
        // _state.printUnsolicited(_conn);
        try {
            response.getWriter().println(
                    _tag + " OK " + getCmdName() + " completed");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected void taggedSimpleSuccess(IMAP4OutputStream out) {
        try {
            out.write(_tag);
            out.write(" OK ");
            out.write(getCmdName());
            out.write(" completed\r\n");
        } catch (IOException e) {
            throw new StreamWriteException(e);
        }
    }
    
    protected void sendContinuation(String s) {
       try {
          response.getWriter().println( "+"
                + ((s == null || s.length() < 1) ? ""
                        : (" " + s)));
       } catch (Exception e) {
          throw new RuntimeException(e);
       }
    }


    protected void println(String s) {
        try {
            response.getWriter().println(s);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected void flush() {
        try {
            response.getWriter().flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected void taggedFailure(String s) {
        try {
            response.getWriter().println(_tag + " NO " + s);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected void taggedFailure(String msg, Object...args) {
        try {
            String s = String.format(msg, args);
            response.getWriter().println(_tag + " NO " + s);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    protected void taggedSimpleFailure() {
        try {
            response.getWriter().println(
                    _tag + " NO " + getCmdName() + " failed");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected void taggedExceptionFailure(Throwable exception) {
        try {
            response.getWriter().println(
                    _tag + " BAD " + exception.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected void untaggedResponse(String s) {
        try {
            response.getWriter().println("* " + s);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    protected void untaggedResponse(IMAP4OutputStream out) {
        try {
            out.write("* ");
        } catch (IOException e) {
            throw new StreamWriteException(e);
        }
    } 
    

    protected void untaggedSuccess(String format, Object... args) {
        try {
            String s = String.format(format, args);
            response.getWriter().println("* OK " + s);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    protected void untaggedSuccess(String s) {
        try {
            response.getWriter().println("* OK " + s);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected void untaggedSimpleResponse(String s) {
        try {
            response.getWriter().println("* " + getCmdName() + " " + s);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    protected void untaggedUpdates(FolderUpdates updates) {
        int[] expunged = updates.getExpunged();
        long exists = updates.getExists();
        long recent = updates.getRecent();
     
        log.debug("Exists: %d, Recent: %d", exists, recent);

        try {
            for (int i = expunged.length - 1; i >= 0; i--) {
                response.getWriter().println("* " + expunged[i] + " EXPUNGE");
            }
            response.getWriter().println("* " + exists + " EXISTS");
            response.getWriter().println("* " + recent + " RECENT");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public OutputStream getOutputStream() {
    	OutputStream stream = null;
    	try {
    		stream = response.getOutputStream();
    	} catch (Exception e) {
    		
    	}
    	return stream;
    }

    /**
     * This is to send untagged responses that the client hasn't
     * requested.
     * 
     */
    protected void untaggedMailboxChanges() {
      /* Look for changes in the current mailbox. Eg. a NOOP might have these responses:
       * C: a047 NOOP
       * S: * 22 EXPUNGE 
       * S: * 23 EXISTS 
       * S: * 3 RECENT 
       * S: * 14 FETCH (FLAGS (\Seen \Deleted)) 
       * S: a047 OK NOOP completed
       */

        IMAP4ProtocolInstance pi = getProtocolInstance();
        ActiveFolder f = pi.getFolderProxy();

        if (f != null) {
         /*
          * TODO There probably needs to be a mechanism to choose which responses
          * should be included here.
          * 
          * At some point more knowledge of changes by other clients will need
          * to be added (EXPUNGE, FETCH etc.).
          */
            FolderUpdates updates = f.refresh();
            untaggedUpdates(updates);
        }
    }

    public void setTag(String s) {
        _tag = s;
    }

    public String getTag() {
        return _tag;
    }

    public abstract boolean isValidForState(ImapState state);

    public abstract IMAP4Response execute();

    public void setProtocolInstance(IMAP4ProtocolInstance prot) {
        protocol = prot;
    }

    public IMAP4ProtocolInstance getProtocolInstance() {
        return protocol;
    }

    public void setRequest(IMAP4Request request) {
        this.request = request;
    }

    public IMAP4Request getRequest() {
        return this.request;
    }
        
    public String[] getPath(String folderName) {
        String[] path = folderName.split(IMAP4Protocol.DIR_SEPARATOR);
        List<String> as = new ArrayList<String>();
        for (String pathElement : path) {
            if (pathElement != null && pathElement.trim().length() != 0) {
                as.add(pathElement);
            }
        }
        return as.toArray(new String[0]);
    }

    public IMAP4Response constructResponse() {
        if (this.response != null) {
            throw new RuntimeException(
                    "PROGRAMMING ERROR ATTEMPT TO CONSTRUCT RESPONSE WHEN RESPONSE IS NOT NULL");
        }
        try {
            response = new IMAP4Response(request, 
                    request.getSocket().getOutputStream(), protocol);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return this.response;
    }

    public void setResponse(IMAP4Response response) {
        if (this.response != null) {
            throw new RuntimeException(
                    "PROGRAMMING ERROR ATTEMPT TO SET RESPONSE WHEN RESPONSE IS NOT NULL");
        }
        this.response = response;
    }

    public String getCmdName() {
        return _cmdName;
    }
    
}
