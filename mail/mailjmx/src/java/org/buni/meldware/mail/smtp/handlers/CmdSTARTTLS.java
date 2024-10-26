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
import java.net.Socket;
import java.security.cert.Certificate;

import javax.net.ssl.HandshakeCompletedEvent;
import javax.net.ssl.HandshakeCompletedListener;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import org.buni.meldware.mail.ServerThread;
import org.buni.meldware.mail.smtp.MutableSMTPRequest;
import org.buni.meldware.mail.smtp.SMTPConstants;
import org.buni.meldware.mail.smtp.SMTPProtocolInstance;
import org.buni.meldware.mail.smtp.SMTPRequest;
import org.buni.meldware.mail.smtp.SMTPResponse;
import org.jboss.logging.Logger;

/**
 * Implements the STARTTLS-Command described in rfc 2487.
 * 
 * @author Michael Krause
 * @version $Revision: 1.3 $
 */
public class CmdSTARTTLS implements SMTPHandler, SMTPConstants {

    public final static String COMMAND = "STARTTLS";

    private static final Logger jblog = Logger.getLogger(CmdSTARTTLS.class);

    public SMTPResponse handleRequest(OutputStream out, SMTPRequest request,
            SMTPProtocolInstance protocol) throws IOException {
        jblog.debug("STARTTLS command handler called");
        SMTPResponse response = new SMTPResponse(request, out, protocol);
        PrintWriter writer = response.getWriter();
        if (request.arguments().hasNext()) {
            writer.println("501 Syntax error (no parameters allowed)");
        } else {
            writer.println("220 Ready to start TLS");
            java.security.Security
                    .addProvider(new com.sun.net.ssl.internal.ssl.Provider());
            //     System.setProperty("javax.net.debug", "ssl,handshake");
            SSLSocketFactory factory = protocol.getSslSocketFactory();
            Socket oldSocket = ((MutableSMTPRequest) request).getSocket();
            final SSLSocket newSocket = (SSLSocket) factory.createSocket(
                    oldSocket, oldSocket.getLocalAddress().getHostName(),
                    oldSocket.getLocalPort(), true);
            newSocket.setNeedClientAuth(protocol.isRequireClientCert());
            newSocket.setUseClientMode(false);
            final SMTPProtocolInstance tempProtocol = (SMTPProtocolInstance) protocol;
            newSocket
                    .addHandshakeCompletedListener(new HandshakeCompletedListener() {
                        public void handshakeCompleted(
                                HandshakeCompletedEvent event) {
                            jblog.debug("HS completed");
                            tempProtocol.setSecure(true);

                            //Reset the state but carry over the addresses connected as this is needed
                            //by MailHeadersImpl
                            Object localAddr = tempProtocol
                                    .getState(ServerThread.STATE_LOCAL_ADDRESS);
                            Object remoteAddr = tempProtocol
                                    .getState(ServerThread.STATE_CLIENT_ADDRESS);
                            tempProtocol.resetState();
                            tempProtocol
                                    .setState(ServerThread.STATE_LOCAL_ADDRESS,
                                            localAddr);
                            tempProtocol.setState(
                                    ServerThread.STATE_CLIENT_ADDRESS,
                                    remoteAddr);

                            jblog.debug("State reset");
                            Certificate certs[] = null;
                            try {
                                certs = newSocket.getSession()
                                        .getPeerCertificates();
                                int j;
                                for (j = certs.length - 1; j >= 0; j--) {
                                    jblog.debug(certs[j]);
                                }
                                //TODO: Put some info about the Certificate in Mail-Header
                            } catch (SSLPeerUnverifiedException e) {
                                e.printStackTrace();
                            }
                        }
                    });

            response.setSocketOverride(newSocket);
        }
        writer.flush();
        return response;
    }
}
