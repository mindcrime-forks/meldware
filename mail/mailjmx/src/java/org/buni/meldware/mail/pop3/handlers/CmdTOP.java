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

import org.buni.meldware.mail.MailException;
import org.buni.meldware.mail.api.Folder;
import org.buni.meldware.mail.api.FolderMessage;
import org.buni.meldware.mail.pop3.POP3ProtocolInstance;
import org.buni.meldware.mail.pop3.POP3Request;
import org.buni.meldware.mail.pop3.POP3Response;
import org.buni.meldware.mail.util.io.DotStuffingLineLimitedCopier;
import org.jboss.logging.Logger;

/**
 * TOP Returns the first x lines of the
 * requested message.
 * <p>
 * This is an optional POP3 command.
 *
 * @author Eric Daugherty
 * @version $Revision: 1.4 $
 */
public class CmdTOP extends AbstractCommand implements POP3Handler,
        POP3UserMessages {

    public final static String COMMAND = "TOP";

    /** Log4j logger */
    private static final Logger log = Logger.getLogger(CmdTOP.class);

    /* (non-Javadoc)
     * @see org.buni.meldware.mail.smtp.handlers.POP3Handler#handleRequest(java.io.OutputStream, org.buni.meldware.mail.smtp.POP3Request, org.buni.meldware.mail.pop3.POP3ProtocolInstance)
     */
    public POP3Response handleRequest(OutputStream out, POP3Request request,
            POP3ProtocolInstance protocol) throws IOException, MailException {

        log.debug("TOP command handler called");
        POP3Response response = new POP3Response(request, out, protocol);

        // Get the arguments.
        String[] arguments = request.getArguments();

        // Verify the protocol is in the right state.
        if (verifyState(protocol, out, POP3ProtocolInstance.STATE_TRANSACTION)
                && verifyArgumentCount(arguments, out, 2)) {

            try {
                int id = Integer.parseInt(arguments[0]);
                int numLines = Integer.parseInt(arguments[1]);
                
                Folder f = protocol.getInbox();
                FolderMessage message = f.getMessage(false, id);
                if (message == null) {
                    out.write(MESSAGE_INVALID_MESSAGE_ID_BYTES);
                    out.write(ENDL);
                } else {
                    // Write the message back to the client.
                    out.write(MESSAGE_OK_BYTES);
                    out.write(ENDL);
                    message.write(out, new DotStuffingLineLimitedCopier(numLines));
                    out.write(TERM);
                }
                
            } catch (NumberFormatException numberFormatException) {
                out.write(MESSAGE_ARGUMENT_INVALID_BYTES);
            }
        }

        out.flush();
        return response;
    }
}
