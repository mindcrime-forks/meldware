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
import java.util.HashMap;
import java.util.Map;

import org.buni.meldware.mail.smtp.SMTPConstants;
import org.buni.meldware.mail.smtp.SMTPProtocolInstance;
import org.buni.meldware.mail.smtp.SMTPRequest;
import org.buni.meldware.mail.smtp.SMTPResponse;
import org.jboss.logging.Logger;

/**
 * HELP command, recommended by rfc 821 
 * 
 * @author <a href='mailto:mikea@xoba.com'> mike andrews </a>
 * @version $Revision: 1.4 $
 */
public class CmdHELP implements SMTPHandler, SMTPConstants {

    private static final Logger jblog = Logger.getLogger(CmdHELO.class);

    private final static Map<String, String[]> topics = new HashMap<String, String[]>();

    static {
        topics.put("HELO", new String[] { "HELO <hostname>",
                "this command identifies the MTA to this server." });
        topics.put("EHLO", new String[] { "HELO <hostname>",
                "this command identifies the MTA to this server",
                "requesting extended SMTP mode." });
        topics.put("MAIL", new String[] { "MAIL FROM:<sender>",
                "identifies the mail sender." });
        topics.put("RCPT", new String[] { "RCPT TO:<recipient>",
                "identifies a mail recipient." });
        topics.put("DATA", new String[] { "DATA",
                "following text is collected as the message.",
                "end with single dot on line followed by CRLF.",
                "otherwise, escape leading dot using \\." });
        topics.put("RSET", new String[] { "RSET", "resets the system." });
        topics.put("NOOP", new String[] { "NOOP", "does nothing." });
        topics.put("QUIT", new String[] { "QUIT",
                "terminate interaction with server." });
        topics.put("VRFY", new String[] { "VRFY <recipient>",
                "verify an address." });
    }

    public final static String COMMAND = "HELP";

    public SMTPResponse handleRequest(OutputStream out, SMTPRequest request,
            SMTPProtocolInstance protocol) throws IOException {
        jblog.debug("HELP command handler called");
        SMTPResponse response = new SMTPResponse(request, out, protocol);
        PrintWriter writer = response.getWriter();
        String[] args = request.getArguments();
        if (args.length > 0) {
            String topic = args[0].toUpperCase();
            if (topics.containsKey(topic)) {
                Object text = topics.get(topic);
                if (text instanceof Object[]) {
                    Object[] lines = (Object[]) text;
                    // multiple line response, indent trailing lines
                    for (int i = 0; i < lines.length; i++) {
                        writer
                                .println("214 " + (i > 0 ? "   " : "")
                                        + lines[i]);
                    }
                    writer.println("214 [end of help info]");
                } else {
                    // single-line response
                    writer.println("214 " + text);
                }
            } else {
                writer.println("501 HELP topic \"" + topic
                        + "\" not recognized");
            }
        } else {
            writer.println("214 Topics:");
            writer.println("214    HELO EHLO MAIL RCPT DATA");
            writer.println("214    RSET NOOP QUIT HELP VRFY");
            writer.println("214 for more information use \"HELP <topic>\"");
            writer.println("214 to report bugs, go to http://buni.org/bugzilla ");
            writer.println("214 [end of help info]");
        }
        writer.flush();
        return response;
    }
}
