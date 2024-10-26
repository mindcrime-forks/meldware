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
package org.buni.meldware.mail.imap4.commands.fetch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.buni.meldware.common.logging.Log;
import org.buni.meldware.common.util.ArrayUtil;
import org.buni.meldware.mail.MailException;
import org.buni.meldware.mail.StreamWriteException;
import org.buni.meldware.mail.api.FolderBody;
import org.buni.meldware.mail.api.FolderMessage;
import org.buni.meldware.mail.imap4.IMAP4OutputStream;
import org.buni.meldware.mail.message.MailAddress;
import org.buni.meldware.mail.message.MailAddressFactory;
import org.buni.meldware.mail.message.MailHeaders;
import org.buni.meldware.mail.message.StandardMailHeaders;

/**
 * @version $Revision: 1.15 $
 */
public class MessagePropertyPart extends FetchPart {

    private final static Log log = Log.getLog(MessagePropertyPart.class);
    
    List supportedParts = Arrays.asList("BODYSTRUCTURE", "ENVELOPE", 
            "FLAGS", "INTERNALDATE", "UID");

    private String name;

    public MessagePropertyPart(String name) {
        this.name = name;
    }

    public boolean canHandle(Object req) {
        return supportedParts.contains(req);
    }
    
    @Override
    public boolean requiresMessage() {
        return !("FLAGS".equals(name) || "UID".equals(name));
    }
    
    public void fetch(FolderMessage msg, IMAP4OutputStream out) {
        
        try {
            log.debug("Processing command: %s", name);
            out.write(name);
            out.write(" ");
            
            if ("BODY".equals(name)) {
                fetchBodyStructure(msg, out, false);
            } else if ("BODYSTRUCTURE".equals(name)) {
                fetchBodyStructure(msg, out, true);
            } else if ("ENVELOPE".equals(name)) {
                fetchEnvelope(msg.getMailHeaders(), out);
            } else if ("FLAGS".equals(name)) {
                fetchFlags(msg, out);
            } else if ("INTERNALDATE".equals(name)) {
                fetchInternaldate(msg, out);
            } else if ("UID".equals(name)) {
                fetchUID(msg, out);
            } else {
                throw new MailException("Unknown Command: %s", name);
            }
        } catch (IOException e) {
            throw new StreamWriteException(e);
        }
    }

    /**
     * Appends creates the envelope response.
     * 
     * @param msg
     * @param out
     * @return
     */
    private void fetchEnvelope(MailHeaders msg, IMAP4OutputStream out) {
        try {
            StringBuilder sb = new StringBuilder();
            appendEnvelope(sb, msg);
            out.write(sb.toString());
        } catch (IOException e) {
            throw new StreamWriteException(e);
        }
        
    }
    
    private void appendEnvelope(StringBuilder sb, MailHeaders msg) {

        String date = msg.getDate();
        String subject = msg.getSubject();

        // String fromLine = msg.getHeader("From");
        String fromLine = msg.getFrom();
        List<MailAddress> from = MailAddressFactory.parseAddressList(fromLine);

        String senderLine = msg.getSender();
        List<MailAddress> sender;
        if (senderLine != null) {
            sender = MailAddressFactory.parseAddressList(senderLine);
        } else {
            sender = from;
        }

        String replyToLine = msg.getReplyTo();
        List<MailAddress> replyTo;
        if (replyToLine != null) {
            replyTo = MailAddressFactory.parseAddressList(replyToLine);
        } else {
            replyTo = from;
        }
        
        String toLine = msg.getTo();
        List<MailAddress> to = MailAddressFactory.parseAddressList(toLine);

        String ccLine = msg.getCC();
        List<MailAddress> cc = MailAddressFactory.parseAddressList(ccLine);

        String bccLine = msg.getBCC();
        List<MailAddress> bcc = MailAddressFactory.parseAddressList(bccLine);

        String inReplyTo = msg.getInReplyTo();
        String messageId = msg.getMessageId();

        //StringBuilder sb = new StringBuilder();
        sb.append("(");
        appendString(sb, date);
        sb.append(" ");
        appendString(sb, subject);
        sb.append(" ");
        appendAddressList(sb, from);
        sb.append(" ");
        appendAddressList(sb, sender);
        sb.append(" ");
        appendAddressList(sb, replyTo);
        sb.append(" ");
        appendAddressList(sb, to);
        sb.append(" ");
        appendAddressList(sb, cc);
        sb.append(" ");
        appendAddressList(sb, bcc);
        sb.append(" ");
        appendString(sb, inReplyTo);
        sb.append(" ");
        appendString(sb, messageId);
        sb.append(")");
    }

    private void appendString(StringBuilder sb, String val) {
        if (val != null) {
            sb.append("\"").append(val.toUpperCase()).append("\"");
        } else {
            sb.append("NIL");
        }
    }
    
    private void addParam(List<String> l, String name, String val) {
        if (val != null) {
            StringBuilder sb = new StringBuilder();
            appendString(sb, name);
            sb.append(" ");
            appendString(sb, val);
            l.add(sb.toString());
        }
    }    
    
    private void appendAddressList(StringBuilder sb, 
            List<MailAddress> addresses) {

        if (addresses != null && addresses.size() != 0) {
            sb.append("(");
            for (MailAddress address : addresses) {
                sb.append(address.toIMAPString());
            }
            sb.append(")");
        } else {
            sb.append("NIL");
        }
    }
    
    void fetchBodyStructure(FolderMessage data, IMAP4OutputStream out, 
            boolean extension) throws IOException {
        List<FolderBody> parts = data.getBody();
        StringBuilder sb = new StringBuilder();
        if (parts.size() > 1) {
            sb.append("(");
        }
        for (FolderBody part : parts) {
            appendBody(sb, part, extension);
        }
        if (data.isMime()) {
            sb.append(" ");
            appendString(sb, data.getContentSubType());
        }
        if (parts.size() > 1) {
            if (extension) {
                sb.append(" NIL NIL NIL NIL");
            }
            sb.append(")");
        }
        out.write(sb.toString());
    }
    
    private void appendBody(StringBuilder sb, FolderBody body, boolean extension) {
        sb.append("(");
        if (body.isMessage()) {
            writeSimple(sb, body, "message", "rfc822");
            sb.append(" ");
            appendEnvelope(sb, body.getMailHeaders());
            sb.append(" (");
            for (FolderBody child : body.getChildren()) {
                appendBody(sb, child, extension);
            }
            sb.append(" ");
            appendString(sb, body.getContentSubType());
            sb.append(") ");
            sb.append(Math.max(Math.round(body.getBodySize() / 80), 1));
        } else if (body.isMultipart()) {
            for (FolderBody child : body.getChildren()) {
                appendBody(sb, child, extension);
            }
            sb.append(" ");
            appendString(sb, body.getContentSubType());
        } else {
            writeSimple(sb, body);
            if ("TEXT".equalsIgnoreCase(body.getContentType())) {
                sb.append(" ");
                sb.append(Math.max(Math.round(body.getBodySize() / 80), 1));
            }
        }
        if (extension) {
            sb.append(" NIL NIL NIL NIL");
        }
        sb.append(")");
    }
    
    private void writeSimple(StringBuilder sb, FolderBody body) {
        writeSimple(sb, body, body.getContentType(), body.getContentSubType());
    }
    
    private void writeSimple(StringBuilder sb, FolderBody body, String type, String subType) {
        appendString(sb, type);
        sb.append(" ");
        appendString(sb, subType);
        sb.append(" (");
        List<String> params = new ArrayList<String>();
        addParam(params, "CHARSET", body.getCharset());
        addParam(params, "NAME", body.getName());
        sb.append(ArrayUtil.join(params, " "));
        // TODO: Handle content id and content disposition.
        sb.append(") NIL NIL ");
        appendString(sb, body.getContentTransferEncoding());
        sb.append(" ");
        sb.append(body.getBodySize());
    }
    
    void fetchSize(FolderMessage message, IMAP4OutputStream out) {
        try {
            out.write(String.valueOf(message.getSize()));
        } catch (IOException e) {
            throw new StreamWriteException(e);
        }
    }

    void fetchUID(FolderMessage msg, IMAP4OutputStream out) {
        try {
            out.write(String.valueOf(msg.getUid()));
        } catch (IOException e) {
            throw new StreamWriteException(e);
        }
    }

    void fetchFlags(FolderMessage msg, IMAP4OutputStream out) {
        try {
            out.write("(");
            out.write(msg.getFlagString());
            out.write(")");
        } catch (IOException e) {
            throw new StreamWriteException(e);
        }
    }

    void fetchInternaldate(FolderMessage msg, IMAP4OutputStream out) {
        String thedate = msg.getHeader(StandardMailHeaders.DATE);
        thedate = thedate == null ? "" : thedate;
        try {
            out.write("\"");
            out.write(thedate); // TODO null checking.
            out.write("\"");
        } catch (IOException e) {
            throw new StreamWriteException(e);
        }
    }
    
    public String getName() {
        return name;
    }
    
    public String toString() {
        return getName();
    }
    
}
