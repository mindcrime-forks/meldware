/**
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
package org.buni.meldware.mail;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import javax.management.ObjectName;

import org.buni.meldware.common.logging.Log;
import org.buni.meldware.mail.util.MMJMXUtil;

/**
 * The connection handler is responsbile for handling the connection with a
 * given user and passing it off to the protocol.
 * 
 * @author Andrew C. Oliver <acoliver ot jboss dat org>
 * @version $Revision: 1.8 $
 */
public class ConnectionHandler implements Runnable {

    final static Log log = Log.getLog(ConnectionHandler.class);

    private Socket socket;

    private ObjectName protocolName;

    private long timeout;

    private long life;

    private Protocol protocol;

    private Reaper reaper;

    public ConnectionHandler(Socket socket, ObjectName protocolName,
            long timeout, long life, Reaper reap) {
        this.socket = socket;
        this.protocolName = protocolName;
        this.timeout = timeout;
        this.life = life;
        this.reaper = reap;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Runnable#run()
     */
    public void run() {
        runSocket();
    }


    /**
     * 
     */
    private void runSocket() {
        InputStream input;
        Thread thisthread = Thread.currentThread();
        
        Closable c = new Closable() {
            boolean isClosed = false;
            public void close() {
                if (!isClosed) {
                    isClosed = true;
                    try {
                        log.debug("Sending protocol BYE command");
                        OutputStream out = socket.getOutputStream();
                        protocol.handleCleanup(out);
                        out.flush();
                        out.close();
                        socket.close();
                    } catch (IOException e) {
                        log.error(e);
                    }                    
                }
            }
        };
        reaper.register(c, thisthread, timeout, life);
        try {

            try {
                ProtocolFactory fact = (ProtocolFactory) MMJMXUtil
                        .getMBean(protocolName, ProtocolFactory.class);
                this.protocol = fact.createInstance();
            } catch (Exception e1) {
                throw new RuntimeException(e1.toString()
                        + " cannot get protocol " + protocolName, e1);
            }
            protocol.setState(ServerThread.STATE_CLIENT_ADDRESS, socket
                    .getInetAddress());
            protocol.setState(ServerThread.STATE_LOCAL_ADDRESS, socket
                    .getLocalAddress());
            protocol.setState(ServerThread.STATE_LIFE, this.life);

            log.info("connected: "
                    + protocol.getState(ServerThread.STATE_CLIENT_ADDRESS)
                    + " to: "
                    + protocol.getState(ServerThread.STATE_LOCAL_ADDRESS)
                    + " until: " + protocol.getState(ServerThread.STATE_LIFE));

            input = new BufferedInputStream(socket.getInputStream());

            OutputStream output = socket.getOutputStream();
            protocol.greet(output);
            // ((AbstractProtocol)protocol).setServerThread(this);
            boolean loop = true;
            input.mark(2);
            while (loop && input.read() != -1) {
                
                input.reset();
                // create Request object and parse
                Request request = protocol.parseRequest(input, socket);
                reaper.lock(thisthread);
                reaper.update(thisthread);
                input.mark(2); // we've got our command...mark our new spot

                // create Response object
                Response response = protocol.handleRequest(output, request);
                if (response.isSocketOverride()) {
                    this.socket = response.getSocketOverride();
                    input = new BufferedInputStream(socket.getInputStream());
                    output = socket.getOutputStream();
                }
                reaper.unlock(thisthread);

                Thread.sleep(50); // so we can be interrupted
                input.mark(2);
                loop = !response.isFinish();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e2) {
            e2.printStackTrace();
        } catch (NullPointerException e3) {
            e3.printStackTrace();
        } finally {
            // Close the socket
            c.close();
            this.reaper.remove(Thread.currentThread());
            log.debug("ConnectionHandler finished");
        }
    }
}
