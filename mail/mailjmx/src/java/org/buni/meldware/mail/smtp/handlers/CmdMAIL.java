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
import java.util.Iterator;
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
 * MAIL signifies that we have a new message to be sent.  It attaches the FROM to the 
 * message.
 * 
 * @author Andrew C. Oliver
 * @author Eric Vidal 
 * @version $Revision: 1.4 $
 */
public class CmdMAIL implements SMTPHandler, SMTPConstants {
    private static final Logger jblog = Logger.getLogger(CmdMAIL.class);

    public final static String COMMAND = "MAIL";

    /*
     *  (non-Javadoc)
     * @see org.buni.meldware.mail.smtp.handlers.SMTPHandler#handleRequest(java.io.OutputStream, org.buni.meldware.mail.smtp.SMTPRequest, org.buni.meldware.mail.Protocol)
     */
    public SMTPResponse handleRequest(OutputStream out, SMTPRequest request,
            SMTPProtocolInstance protocol) throws IOException {
        jblog.debug("MAIL command handler called");
        Map mailOptions = null;
        SMTPResponse response = new SMTPResponse(request, out, protocol);
        PrintWriter writer = response.getWriter();
        String arg = getArgline(request.arguments());
        String sender = null;
        if (jblog.isDebugEnabled()) {
            jblog.debug("arg = " + arg);
        }
        if ((arg != null) && (arg.indexOf(":") > 0)) {
            jblog.debug("arg wasn't null");
            int colonIndex = arg.indexOf(":");
            jblog.debug("colonIndex=" + colonIndex);
            sender = arg.substring(colonIndex + 1);
            jblog.debug("sender=" + sender);
            arg = arg.substring(0, colonIndex);
            jblog.debug("arg=" + arg);
        }
        if (sender != null && arg != null && jblog.isDebugEnabled()) {
            jblog.debug("Sender= " + sender.trim() + " **arg = " + arg.trim());
        }
        if (protocol.getState(SENDER) != null) {
            writer.println("503 Sender already specified");
        } else if (arg == null || !arg.toUpperCase(Locale.US).equals("FROM")
                || sender == null) {
            writer.println("501 Usage: MAIL FROM:<sender>");
        } else {
            sender = sender.trim();
            int lastChar = sender.indexOf('>', sender.indexOf('<'));
            if ((lastChar > 0) && (sender.length() > lastChar + 2)
                    && (sender.charAt(lastChar + 1) == ' ')) {
                String mailOptionString = sender.substring(lastChar + 2);
                sender = sender.substring(0, lastChar + 1);
                mailOptions = parseMailOptions(mailOptionString);
            }

        }
        if (sender == null) {
            writer.println("501 Syntax error in MAIL command");
        } else if (!sender.startsWith("<") || !sender.endsWith(">")) {
            writer.println("501 Syntax error in MAIL command");
        } else {
            boolean isfrom = true;
            MailAddress address = MailAddress.parseSMTPStyle(sender, isfrom);
            if (!address.isValid()) {
                writer.println("501 Syntax error in sender address");
            } else {
                protocol.setState(SENDER, address);
                writer.println("250 Sender " + sender + " OK");
            }
        }

        writer.flush();
        return response;
    }

    /**
     * The message size tells the server the size of message to expect.  Here we need to check 
     * if its withing bounds
     * @param value size of the message (string)
     * @param writer PrintWriter to write the response
     * @param protocol instance containing the configuration and state
     * @return boolean signifying if the size was within bounds.
     */
    private boolean handleMailSize(String value, PrintWriter writer,
            SMTPProtocolInstance protocol) {
        int size = 0;
        try {
            size = Integer.parseInt(value);
        } catch (NumberFormatException pe) {
            // This is a malformed option value.  We return an error     
            writer
                    .println("501 Syntactically incorrect value for SIZE parameter");
            return false;
        }
        long maxMessageSize = protocol.getMaxMessageSize();
        if ((maxMessageSize > 0) && (size > maxMessageSize)) {
            // Let the client know that the size limit has been hit.
            writer
                    .println("552 Message size exceeds fixed maximum message size");
            return false;
        } else {
            // put the message size in the message state so it can be used
            // later to restrict messages for user quotas, etc.
            protocol.setState(SMTPConstants.MSG_SIZE, Integer.valueOf(size));
        }
        return true;

    }

    private Map parseMailOptions(final String mailOptionString) {
        Map options = new HashMap();
        StringTokenizer optionTokenizer = new StringTokenizer(mailOptionString,
                " ");
        while (optionTokenizer.hasMoreElements()) {
            String mailOption = optionTokenizer.nextToken();
            int equalIndex = mailOption.indexOf('=');
            String mailOptionName = mailOption;
            String mailOptionValue = "";
            if (equalIndex > 0) {
                mailOptionName = mailOption.substring(0, equalIndex)
                        .toUpperCase(Locale.US);
                mailOptionValue = mailOption.substring(equalIndex + 1);
            }
            options.put(mailOptionName, mailOptionValue);
        }
        return options;
    }

    private String getArgline(Iterator iter) {
        String remote = null;
        if (iter.hasNext() && iter != null) {
            remote = (String) iter.next();
        }
        return remote;
    }
}
