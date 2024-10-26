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
package org.buni.meldware.mail.smtp.handlers;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import org.buni.meldware.mail.smtp.SMTPConstants;
import org.buni.meldware.mail.smtp.SMTPProtocolInstance;
import org.buni.meldware.mail.smtp.SMTPRequest;
import org.buni.meldware.mail.smtp.SMTPResponse;
import org.jboss.logging.Logger;

/**
 * The RSET command clears out the state of the transmission.  It should make things as if we were just
 * after HELO.
 * 
 * @author Kabir Khan
 * @author Andrew C. Oliver <acoliver ot jboss dat org> - javadoc only
 * @version $Revision: 1.3 $
 */
public class CmdRSET implements SMTPHandler, SMTPConstants {
    public final static String COMMAND = "RSET";

    private static final Logger jblog = Logger.getLogger(CmdRSET.class);

    /* (non-Javadoc)
     * @see org.buni.meldware.mail.smtp.handlers.SMTPHandler#handleRequest(java.io.OutputStream, org.buni.meldware.mail.smtp.SMTPRequest, org.buni.meldware.mail.Protocol, org.buni.meldware.mail.log.MailLog)
     */
    public SMTPResponse handleRequest(OutputStream out, SMTPRequest request,
            SMTPProtocolInstance protocol) throws IOException {
        jblog.debug("RSET command handler called");

        SMTPResponse response = new SMTPResponse(request, out, protocol);
        PrintWriter writer = response.getWriter();

        //Moan about arguments (should be none)
        String[] arguments = request.getArguments();
        if (arguments != null && arguments.length > 0) {
            writer.println("501 Usage: RSET");
        } else {
            //should authentication be undone?  JAMES doesn't
            protocol.setState(SENDER, null);
            protocol.setState(RCPT_LIST, null);
            writer.println("250 state reset OK");
        }

        writer.flush();
        return response;
    }

}
