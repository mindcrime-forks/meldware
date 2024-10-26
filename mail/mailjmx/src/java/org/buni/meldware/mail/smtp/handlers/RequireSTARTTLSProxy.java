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

import org.buni.meldware.mail.smtp.SMTPProtocolInstance;
import org.buni.meldware.mail.smtp.SMTPRequest;
import org.buni.meldware.mail.smtp.SMTPResponse;
import org.jboss.logging.Logger;

/**
 * Proxy for {@link org.buni.meldware.mail.smtp.handlers.SMTPHandler}. 
 * Sends "530 Must issue a STARTTLS command first" if REQUIRE_STARTTLS
 * is configured and the connection is not secure.<br>
 * Otherwise it delegates to its {@link org.buni.meldware.mail.smtp.handlers.SMTPHandler}
 * @author Michael Krause
 * @version $Revision: 1.3 $
 */
public class RequireSTARTTLSProxy implements SMTPHandler {

    private SMTPHandler handler;

    private static final Logger jblog = Logger
            .getLogger(RequireSTARTTLSProxy.class);

    public RequireSTARTTLSProxy(SMTPHandler handler) {
        this.handler = handler;
    }

    public SMTPResponse handleRequest(OutputStream out, SMTPRequest request,
            SMTPProtocolInstance protocol) throws IOException {

        if (isTlsRequired(protocol)) {
            SMTPResponse response = new SMTPResponse(request, out, protocol);
            PrintWriter writer = response.getWriter();
            jblog.warn("Sending 530 Must issue a STARTTLS command first");
            writer.println("530 Must issue a STARTTLS command first");
            writer.flush();
            return response;
        } else {
            return handler.handleRequest(out, request, protocol);
        }
    }

    private boolean isTlsRequired(SMTPProtocolInstance protocol) {
        boolean requireTls = protocol.isRequireTls();
        boolean isSecure = protocol.isSecure();

        return requireTls && !isSecure;
    }
}
