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
import java.net.Socket;
import java.security.Security;
import java.security.cert.Certificate;

import javax.net.ssl.HandshakeCompletedEvent;
import javax.net.ssl.HandshakeCompletedListener;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import org.apache.log4j.Logger;
import org.buni.meldware.mail.ServerThread;
import org.buni.meldware.mail.pop3.MutablePOP3Request;
import org.buni.meldware.mail.pop3.POP3Constants;
import org.buni.meldware.mail.pop3.POP3ProtocolInstance;
import org.buni.meldware.mail.pop3.POP3Request;
import org.buni.meldware.mail.pop3.POP3Response;

/**
 * Implements the STARTTLS-Command for POP3
 * 
 * @author Kabir Khan
 * @version $Revision: 1.3 $
 */
public class CmdSTLS extends AbstractCommand implements POP3Handler,
        POP3UserMessages, POP3Constants {

    public final static String COMMAND = "STLS";

    /** Log4j logger */
    private static final Logger log = Logger.getLogger(CmdSTLS.class);

    /* (non-Javadoc)
     * @see org.buni.meldware.mail.pop3.handlers.POP3Handler#handleRequest(java.io.OutputStream, org.buni.meldware.mail.pop3.POP3Request, org.buni.meldware.mail.pop3.POP3ProtocolInstance)
     */
    public POP3Response handleRequest(OutputStream out, POP3Request request,
            final POP3ProtocolInstance protocol) throws IOException {

        log.debug("STLS command handler called");
        POP3Response response = new POP3Response(request, out, protocol);
        PrintWriter writer = response.getWriter();

        if (protocol.getState() != POP3ProtocolInstance.STATE_AUTHORIZATION) {
            writer.println(MESSAGE_INVALID_STATE);
        } else if (verifyArgumentCount(request.getArguments(), writer, 0)) {
            writer.println(MESSAGE_OK);
            Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
            SSLSocketFactory factory = protocol.getSslSocketFactory();
            Socket oldSocket = ((MutablePOP3Request) request).getSocket();
            final SSLSocket newSocket = (SSLSocket) factory.createSocket(
                    oldSocket, oldSocket.getLocalAddress().getHostName(),
                    oldSocket.getLocalPort(), true);
            newSocket.setNeedClientAuth(protocol.isRequireClientCert());
            newSocket.setUseClientMode(false);
            newSocket.addHandshakeCompletedListener(new HandshakeCompletedListener() {
                public void handshakeCompleted(HandshakeCompletedEvent event) {
                    log.debug("HS completed");
                    protocol.setSecure(true);

                    //Reset the state but carry over the addresses connected as this is needed
                    //by MailHeadersImpl
                    Object localAddr = protocol.getState(ServerThread.STATE_LOCAL_ADDRESS);
                    Object remoteAddr = protocol.getState(ServerThread.STATE_CLIENT_ADDRESS);
                    Object life = protocol.getState(ServerThread.STATE_LIFE);
                    protocol.resetState();
                    protocol.setState(ServerThread.STATE_LOCAL_ADDRESS, localAddr);
                    protocol.setState(ServerThread.STATE_CLIENT_ADDRESS, remoteAddr);
                    protocol.setState(ServerThread.STATE_LIFE, life);

                    log.debug("State reset");
                    Certificate certs[] = null;
                    try {
                        certs = newSocket.getSession().getPeerCertificates();
                        int j;
                        for (j = certs.length - 1; j >= 0; j--) {
                            log.debug(certs[j]);
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
