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
import java.io.InputStream;
import java.io.OutputStream;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.TextInputCallback;
import javax.security.auth.callback.TextOutputCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import org.buni.meldware.common.logging.Log;
import org.buni.meldware.mail.api.Constants;
import org.buni.meldware.mail.imap4.IMAP4ProtocolInstance;
import org.buni.meldware.mail.imap4.IMAP4Response;
import org.buni.meldware.mail.imap4.IMAP4ProtocolInstance.ImapState;
import org.buni.meldware.mail.util.io.IOUtil;

/**
 * AUTHENTICATE command.
 * 
 * <p>
 * <a href="http://asg.web.cmu.edu/rfc/rfc2060.html#sec-6.2.2">
 * http://asg.web.cmu.edu/rfc/rfc2060.html#sec-6.2.2 </a>
 * </p>
 * @version $Revision: 1.6 $
 */
public class AuthenticateCommand extends AbstractImapCommand {

    private final static Log log = Log.getLog(AuthenticateCommand.class);
    private String type;
    private InputStream in;
    private OutputStream out;

    public AuthenticateCommand() {
        super("AUTHENTICATE");
    }

    public boolean isValidForState(ImapState state) {
        return state == ImapState.NOT_AUTHENTICATED;
    }

    public IMAP4Response execute() {
        IMAP4ProtocolInstance protocol = getProtocolInstance();
        IMAP4Response result = constructResponse();
        IMAP4CallbackHandler cbh = new IMAP4CallbackHandler();
        
        try {
            in = getRequest().getInputStream();
            out = result.getOutputStream();
            LoginContext lc = new LoginContext("meldware-sasl", cbh);
            lc.login();
            protocol.setUsername(cbh.getUsername());
            taggedSimpleSuccess();
        } catch (LoginException e) {
            e.printStackTrace();
            taggedFailure("Authentication Failed");
        } catch (IOException e) {
            e.printStackTrace();
            taggedFailure("Authentication Failed");
        }
        
        return result;
    }
    
    private void send(String challenge) throws IOException {
        if (challenge != null) {
            if (challenge.length() > 0) {
                String s = "+ " + challenge;
                out.write(s.getBytes(Constants.ENCODING));
                out.write(Constants.ENDL);
            } else {
                out.write('+');
                out.write(Constants.ENDL);
            }
        }
    }
    
    public String recv() throws IOException {
        StringBuilder sb = new StringBuilder();
        IOUtil.appendLine(sb, in, Constants.ENCODING);
        return sb.toString().trim();
    }
    
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    
    private class IMAP4CallbackHandler implements CallbackHandler {
        private String username;
        public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
            for (Callback cb : callbacks) {
                if (cb instanceof TextInputCallback) {
                    TextInputCallback tic = (TextInputCallback) cb;
                    tic.setText(recv());
                } else if (cb instanceof TextOutputCallback) {
                    TextOutputCallback toc = (TextOutputCallback) cb;
                    send(toc.getMessage());
                } else if (cb instanceof NameCallback) {
                    NameCallback nc = (NameCallback) cb;
                    username = nc.getName();
                } else {
                    log.error("Unknown Callback: %s", cb.getClass().getName());
                    throw new UnsupportedCallbackException(cb);
                }
            }
        }
        
        public String getUsername() {
            return username;
        }
    }
}
