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
 */
package org.buni.meldware.mail.pop3.handlers;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import org.buni.meldware.mail.api.Mailbox;
import org.buni.meldware.mail.pop3.POP3ProtocolInstance;
import org.buni.meldware.mail.pop3.POP3Request;
import org.buni.meldware.mail.pop3.POP3Response;
import org.jboss.logging.Logger;

/**
 * PASS accepts a password as part
 * of the authentication process.
 *
 * @author Eric Daugherty
 * @version $Revision: 1.8 $
 */
public class CmdPASS extends AbstractCommand implements POP3Handler {

    public final static String COMMAND = "PASS";

    /** Log4j logger */
    private static final Logger log = Logger.getLogger(CmdPASS.class);

    /* (non-Javadoc)
     * @see org.buni.meldware.mail.smtp.handlers.POP3Handler#handleRequest(java.io.OutputStream, org.buni.meldware.mail.smtp.POP3Request, org.buni.meldware.mail.pop3.POP3ProtocolInstance)
     */
    public POP3Response handleRequest(OutputStream out, POP3Request request,
            POP3ProtocolInstance protocol) throws IOException {

        log.debug("PASS command handler called");
        POP3Response response = new POP3Response(request, out, protocol);
        PrintWriter writer = response.getWriter();

        // Get the arguments and verify the size.
        String[] arguments = request.getArguments();

        // Verify the current state and the arguement size.
        if (verifyState(protocol, writer,
                POP3ProtocolInstance.STATE_AUTHORIZATION)
                && verifyArgumentCount(arguments, writer, 1)) {

            String username = protocol.getUsername();
            String password = arguments[0].trim();
            //password = password.trim();

            // Verify the USER command has been accepted.
            if (username == null) {
                writer.println(MESSAGE_USER_REQUIRED_BEFORE_PASS);
            }
            // Verify password exists
            else if (password.length() == 0) {
                writer.println(MESSAGE_PASS_INVALID);
            }
            // Verify the password.
            else if (password.length() > 0) {

                if (!protocol.getUserRepository().test(username, password)) {
                    writer.println("-ERR Authentication failed");
                    protocol.setState(USER, null);
                } else {
                    
                    // If the password is valid, load the mailbox.
                    try {
                        protocol.setUsername(username);
                        Mailbox mailbox = protocol.getMailbox();
                        if (mailbox == null) {
                            writer.println(MESSAGE_SERVICE_UNAVAILABLE);
                        } else {
                            protocol.setState(POP3ProtocolInstance.STATE_TRANSACTION);
                            writer.println(MESSAGE_OK);
                        }
                        
                    } catch (Exception e) {
                        log.error("Error getting MailboxMBean: " + e, e);
                        writer.println(MESSAGE_SERVICE_UNAVAILABLE);
                    }
                }
            }

        }

        writer.flush();
        return response;
    }
}
