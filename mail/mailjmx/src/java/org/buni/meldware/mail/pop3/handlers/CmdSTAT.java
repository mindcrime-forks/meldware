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
import java.util.Collection;

import org.apache.log4j.Logger;
import org.buni.meldware.mail.MailException;
import org.buni.meldware.mail.api.Folder;
import org.buni.meldware.mail.api.Range;
import org.buni.meldware.mail.pop3.POP3ProtocolInstance;
import org.buni.meldware.mail.pop3.POP3Request;
import org.buni.meldware.mail.pop3.POP3Response;

/**
 * STAT generates a one line summary of
 * the user's mailbox.
 *
 * @author Eric Daugherty
 * @version $Revision: 1.4 $
 */
public class CmdSTAT extends AbstractCommand implements POP3Handler, POP3UserMessages {

    public final static String COMMAND = "STAT";

    /** Log4j logger */
    private static final Logger log = Logger.getLogger( CmdSTAT.class );


    /* (non-Javadoc)
     * @see org.buni.meldware.mail.smtp.handlers.POP3Handler#handleRequest(java.io.OutputStream, org.buni.meldware.mail.smtp.POP3Request, org.buni.meldware.mail.pop3.POP3ProtocolInstance)
     */
    public POP3Response handleRequest(
        OutputStream out,
        POP3Request request,
        POP3ProtocolInstance protocol)
        throws IOException, MailException {

        log.debug( "STAT command handler called" );
        POP3Response response = new POP3Response(request, out, protocol);
        PrintWriter writer = response.getWriter();

        // Verify the protocol is in the right state.
        if( verifyState( protocol, writer, POP3ProtocolInstance.STATE_TRANSACTION ) ) {
            Folder folder = protocol.getInbox();
            Collection<Long> sizes = folder.getMessageSizes(false, Range.ALL);

            // Output a listing of all messages
            long totalSize = 0;
            
            // Calculate the total size.
            for (Long l : sizes) {
                totalSize += l.longValue();
            }
            
            // Output the response
            writer.println( "+OK " + sizes.size() + " " + totalSize );
        }

        writer.flush();
        return response;
    }
}
