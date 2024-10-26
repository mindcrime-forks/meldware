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
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import org.buni.meldware.mail.MailException;
import org.buni.meldware.mail.MailListenerChain;
import org.buni.meldware.mail.message.LoopDetectedException;
import org.buni.meldware.mail.message.Mail;
import org.buni.meldware.mail.message.MailAddress;
import org.buni.meldware.mail.message.MailBodyManager;
import org.buni.meldware.mail.message.MailCreateListener;
import org.buni.meldware.mail.message.Message;
import org.buni.meldware.mail.smtp.SMTPConstants;
import org.buni.meldware.mail.smtp.SMTPProtocolInstance;
import org.buni.meldware.mail.smtp.SMTPRequest;
import org.buni.meldware.mail.smtp.SMTPResponse;
import org.jboss.aspects.tx.Tx;
import org.jboss.aspects.tx.TxType;
import org.jboss.logging.Logger;

/**
 * DATA tells the server to expect the actual message.  It marks the begin of a message payload.
 *  
 * @author Andrew C. Oliver
 */
public class CmdDATA implements SMTPHandler, SMTPConstants {
    private static final Logger jblog = Logger.getLogger(CmdDATA.class);

    public final static String COMMAND = "DATA";
    private final static String MSG_550 = "550 %s";
    private final static String MSG_250 = "250 message received";
    private final static String MSG_559 = 
        "559 Message not committed due to internal error";

    /*
     *  (non-Javadoc)
     * @see org.buni.meldware.mail.smtp.handlers.SMTPHandler#handleRequest(java.io.OutputStream, org.buni.meldware.mail.smtp.SMTPRequest, org.buni.meldware.mail.Protocol)
     */
    @Tx(TxType.REQUIRED)
    public SMTPResponse handleRequest(OutputStream out, SMTPRequest request,
            SMTPProtocolInstance protocol) throws IOException {
        jblog.debug("DATA command handler called");
        SMTPResponse response = new SMTPResponse(request, out, protocol);
        SMTPProtocolInstance smtpProtocol = (SMTPProtocolInstance) protocol;
        PrintWriter writer = response.getWriter();
        String arg = getArg(request.arguments());
        if (arg != null && !arg.equals("")) {
            writer
                    .println("500 Unexpected argument provided with DATA command");
        }

        if (smtpProtocol.getState(SENDER) == null) {
            writer.println("503 No sender specified");
        } else if (protocol.getState(RCPT_LIST) == null) {
            writer.println("503 No recipients specified");
        } else {
            writer.println("354 Ok Send data ending with <CRLF>.<CRLF>");
            writer.flush();

            InputStream msgIn = request.getInputStream();

            //prepare sender and receiver        
            MailAddress sender = (MailAddress) smtpProtocol.getState(SENDER);
            @SuppressWarnings("unchecked")
            List<MailAddress> rcptsList = (List<MailAddress>) smtpProtocol.getState(RCPT_LIST);
            MailAddress[] rcpts = new MailAddress[rcptsList.size()];
            rcpts = (MailAddress[]) rcptsList.toArray(rcpts);

            try {
                MailCreateListener mcl = 
                    smtpProtocol.getMailCreateListener(sender, rcpts);
                MailBodyManager mgr = smtpProtocol.getManager();
                Mail mail = Mail.create(mgr, msgIn, mcl);
                MailListenerChain chain = protocol.getListenerChain();
                Message msg = mail;
                
                msg = chain.processMail(msg);

                writer.println(MSG_250);
                
                msgIn.mark(2);
            } catch (LoopDetectedException e) {
                //The exception contains the reason
                writer.println(String.format(MSG_550, e.getMessage()));
            } catch (MailException e) {
                jblog.error(MSG_559, e);
                writer.println(MSG_559);
                // FIXME What is internal error message code.
            }
        }

        protocol.resetState();

        writer.flush();
        return response;
    }

    /**
     * gets the actual argument...SMTP has single line arguments this we have to parse them out of one argument
     * @param iter list of arguments (one element)
     * @return String represneting actual argument...
     */
    private String getArg(Iterator iter) {
        String remote = null;
        if (iter != null && iter.hasNext()) {
            remote = (String) iter.next();
        }
        return remote;
    }
}
