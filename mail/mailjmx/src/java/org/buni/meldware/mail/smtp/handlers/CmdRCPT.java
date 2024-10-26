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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

import org.buni.meldware.mail.message.MailAddress;
import org.buni.meldware.mail.smtp.SMTPConstants;
import org.buni.meldware.mail.smtp.SMTPProtocolInstance;
import org.buni.meldware.mail.smtp.SMTPRequest;
import org.buni.meldware.mail.smtp.SMTPResponse;
import org.jboss.logging.Logger;

/**
 * RCPT is for listing the users who will receive the message
 * 
 * @author Andrew C. Oliver
 * @version $Revision: 1.3 $
 */
public class CmdRCPT implements SMTPHandler, SMTPConstants {

    private static final Logger jblog = Logger.getLogger(CmdRCPT.class);

    public final static String COMMAND = "RCPT";

    public final static String SENDER = "SENDER";

    public final static String MESG_SIZE = "MESG_SIZE";

    /*
     *  (non-Javadoc)
     * @see org.buni.meldware.mail.smtp.handlers.SMTPHandler#handleRequest(java.io.OutputStream, org.buni.meldware.mail.smtp.SMTPRequest, org.buni.meldware.mail.Protocol)
     */
    public SMTPResponse handleRequest(OutputStream out, SMTPRequest request,
            SMTPProtocolInstance protocol) throws IOException {
        jblog.debug("RCPT command handler called");
        SMTPProtocolInstance smtpProtocol = (SMTPProtocolInstance) protocol;
        SMTPResponse response = new SMTPResponse(request, out, protocol);
        PrintWriter writer = response.getWriter();
        String arguments[] = parseArguments(getArgline(request.arguments()));
        String recipient = null;
        String argument = null;
        if (arguments.length > 0) {
            recipient = arguments[0];
        }
        if (arguments.length > 1) {
            argument = arguments[1];
        }

        if (protocol.getState(SENDER) == null) {
            writer.println("503 Need MAIL before RCPT");
        } else if (recipient == null
                || !argument.toUpperCase(Locale.US).equals("TO")
                || recipient.equals("")) {
            writer.println("501 Usage: RCPT TO:<recipient>");
        } else {

            List rcpts = (List) protocol.getState(RCPT_LIST);
            if (rcpts == null) {
                rcpts = new ArrayList();
            }
            Map options = parseOptions(recipient);
            MailAddress rcpt = parseRecipient(recipient.trim());

            if (rcpt.isValid()) {
                if (smtpProtocol.handleAuth(rcpt, out, (SMTPRequest) request,
                        (SMTPProtocolInstance) protocol, response.getWriter())) {

                    if (handleRelay(smtpProtocol, rcpt)) {
                        rcpts.add(rcpt);
                        protocol.setState(RCPT_LIST, rcpts);
                        writer.println("250 Recipient " + recipient.trim()
                                + " OK");
                    } else {
                        writer.println("553 Relaying disallowed");
                    }

                } else {
                    // don't we need to write something here?
                    writer.println("550 Not Authorized");
                }
            } else {
                writer.println("501 Syntax error in recipient address");
            }

        }

        writer.flush();
        return response;
    }

    /**
     * Returns true if this address is local or it is an allowed relay request.
     * Relaying is allowed for all authenticated users.
     * 
     * @param protocol
     * @param rcpt
     * @return
     */
    public boolean handleRelay(SMTPProtocolInstance protocol, MailAddress rcpt) {
        //InetAddress address = (InetAddress) protocol.getState(ServerThread.STATE_CLIENT_ADDRESS);
        return protocol.isAuthenticated() || protocol.isMailAddressLocal(rcpt)
                ||
                //protocol.isRelayByAddrAllowed(rcpt) || 
                protocol.isRelayByDomainAllowed(rcpt);
    }

    /**
     * parse the receipient and return a MailAddress object
     * @param String representation of recipient for this mail
     * @return MailAddress representation
     */
    private MailAddress parseRecipient(String recipient) {
        int lastChar = recipient.lastIndexOf('>');
        MailAddress retval = MailAddress.parseSMTPStyle(recipient.substring(0,
                lastChar + 1));
        return retval;
    }

    /**
     * parse the various options into a map.  Theoretically there can be options associated with the recipient
     * @param recipient as a String (raw argument to CmdRCPT)
     * @return Map representing any options
     */
    private Map parseOptions(String recipient) {
        Map options = new HashMap();
        int lastChar = recipient.lastIndexOf('>');
        if ((lastChar > 0) && (recipient.length() > lastChar + 2)
                && (recipient.charAt(lastChar + 1) == ' ')) {
            String rcptOptionString = recipient.substring(lastChar + 2);

            StringTokenizer optionTokenizer = new StringTokenizer(
                    rcptOptionString, " ");

            while (optionTokenizer.hasMoreElements()) {
                String rcptOption = optionTokenizer.nextToken();
                int equalIndex = rcptOptionString.indexOf('=');
                String rcptOptionName = rcptOption;
                String rcptOptionValue = "";

                if (equalIndex > 0) {
                    rcptOptionName = rcptOption.substring(0, equalIndex)
                            .toUpperCase(Locale.US);
                    rcptOptionValue = rcptOption.substring(equalIndex + 1);
                }
                options.put(rcptOptionName, rcptOptionValue);
            }
        }
        return options;
    }

    private String[] parseArguments(String argline) {
        String args[] = new String[2];
        int colIndex = argline.indexOf(":");
        if (colIndex < 1) {
            return args;
        }
        args[0] = argline.substring(colIndex + 1);
        args[1] = argline.substring(0, colIndex);
        return args;
    }

    private String getArgline(Iterator iter) {
        String remote = null;
        if (iter.hasNext() && iter != null) {
            remote = (String) iter.next();
        }
        return remote;
    }
}
