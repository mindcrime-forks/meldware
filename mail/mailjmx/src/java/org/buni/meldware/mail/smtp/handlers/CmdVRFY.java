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
 * VRFY command, recommended by <a href='http://www.faqs.org/ftp/rfc/pdf/rfc2821.txt.pdf'>rfc 821</a>
 * 
 * @author <a href='mailto:mikea@xoba.com'>mike andrews</a>
 * @version $Revision: 1.3 $
 */
public class CmdVRFY implements SMTPHandler, SMTPConstants {

    private static final Logger jblog = Logger.getLogger(CmdVRFY.class);

    public final static String COMMAND = "VRFY";

    public SMTPResponse handleRequest(OutputStream out, SMTPRequest request,
            SMTPProtocolInstance protocol) throws IOException {
        jblog.debug("VRFY command handler called");
        SMTPResponse response = new SMTPResponse(request, out, protocol);
        PrintWriter writer = response.getWriter();
        writer.println("502 " + protocol.getServername()
                + " VRFY not implemented");
        writer.flush();
        return response;
    }
}
