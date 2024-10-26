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

import org.apache.log4j.Logger;

/**
 * ServerThread is a pooled handler which actually does the work for a
 * connection. (NO LONGER USED EXCEPT FOR CONSTANTS THAT SHOULD BE MOVED SOON)
 * 
 * @author Andrew C. Oliver
 * @version $Revision: 1.4 $
 */
public class ServerThread /*implements Runnable*/ {

    Logger logger = Logger.getLogger(ServerThread.class);

    // state attached to a protocol when a client connects
    public static final String STATE_CLIENT_ADDRESS = "client address";

    public static final String STATE_LOCAL_ADDRESS = "local address";

    public static final String STATE_LIFE = "life";

    // instance and invocation statistics:
    private static Long instances = Long.valueOf(0);

    private long id, clientsHandled;

    private Socket socket;

    private InputStream input;

    private OutputStream output;

    private Protocol protocol;

    private long timeout;

    private long life;

    private Thread myThread;

    public long getId() {
        return id;
    }

    public long getClientsHandled() {
        return clientsHandled;
    }



    /**
     * @return java.lang.Thread instance presently assigned to this ServerThread
     */
    protected Thread getMyThread() {
        return myThread;
    }

    /**
     * This sets the underlying Java.lang.thread for this serverThread (done by
     * the ServerThreadPool)
     * 
     * @param myThread
     *            java.lang.Thread instance
     */
    protected void setMyThread(Thread myThread) {
        this.myThread = myThread;
    }

    /**
     * @return input stream assigned to this ServerThread
     */
    protected InputStream getInput() {
        return input;
    }

    /**
     * sets the InputStream for the thread (ServerThreadPool)
     * 
     * @param input
     *            stream for this thread
     */
    protected void setInput(InputStream input) {
        this.input = input;
    }

    /**
     * @return life for this ServerThread - we'll kill it and return it to the
     *         pool when its life is up
     */
    protected long getLife() {
        return life;
    }

    /**
     * set how long we want this thread to live before we kill it and return it
     * to the library.
     * 
     * @param life
     *            milliseconds to live regardless of operation
     */
    protected void setLife(long life) {
        this.life = life;
    }

    /**
     * @return out put stream for this ServerThread
     */
    protected OutputStream getOutput() {
        return output;
    }

    /**
     * @param output
     *            stream for this ServerThread
     */
    protected void setOutput(OutputStream output) {
        this.output = output;
    }

    /**
     * @return Protocol instance presently assigned for this ServerThread
     */
    protected Protocol getProtocol() {
        return protocol;
    }

    /**
     * @return Socket assigned to this ServerThread
     */
    public Socket getSocket() {
        return socket;
    }

    /**
     * set the socket to assign to this ServerThread
     * 
     * @param socket
     *            to assing to thsi ServerThread
     */
    public void setSocket(Socket socket) throws IOException {
        this.socket = socket;
        input = new BufferedInputStream(socket.getInputStream());
        input.mark(2);
        output = socket.getOutputStream();

    }

    /**
     * @return timeout (milliseconds) between operations
     */
    public long getTimeout() {
        return timeout;
    }

    /**
     * set the time in ms between operations. This is the time between when an
     * operation has completed and another one is initiated. Meaning if I say
     * HELO localhost and the command takes 10 minutes to return, provided that
     * "life" is 11 minutes...This takes no effect.. If it returns and I sit
     * around for 10 minutes and the timeout is set to 5...then after 5 minutes
     * of me being idle...the connection is killed and the ServerThread is
     * returned to the pool...and thanks for playing.
     * 
     * @param timeout
     *            between operations (in milliseconds)
     */
    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    /**
     * construct a ServerThread (done by the ServerThreadPool at startup)
     * 
     * @param socket
     *            to handle the connection...
     * @param protocolName
     *            is the JMX name (or classname) to look up and use as the
     *            Protocol...
     * @param timeout
     *            time between operations (ms)
     * @param life
     *            time to live (ms)
     * @param pool
     *            callback for the ServerThreadPool which contains this...
     */
    public ServerThread(Socket socket, String protocolName, long timeout,
            long life) {

        this();

        this.socket = socket;
        this.timeout = timeout;
        this.life = life;
    }

    /**
     * an inner class that expresses the identity of a particular invocation of
     * a server thread
     */
    static public class NDCIdentity {

        private long threadId, clientsHandled;

        private Socket socket;

        public NDCIdentity(ServerThread thread) {
            this.threadId = thread.getId();
            this.clientsHandled = thread.getClientsHandled();
            this.socket = thread.getSocket();
        }

        public String toString() {
            return "addr=" + socket.getInetAddress().getHostAddress()
                    + ",thread=" + threadId + ",clients=" + clientsHandled;
        }

    }

    /**
     * default constructor...use the setters before starting the thread up..
     */
    public ServerThread() {
        synchronized (instances) {
            id = instances.longValue();
            instances = new Long(instances.longValue() + 1);
            clientsHandled = 0;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Runnable#run()
     */
//    public void run() {
//        synchronized (instances) {
//            clientsHandled++;
//        }
//
//        try {
//
//            // use NDC's for conversational logging; *MUST* call NDC.remove()
//            // sometime later
//            // to reclaim resources... [log4j warns about this, although
//            // NDC.remove()'s need
//            // not be called in a 1-1 correspondence with NDC.push()'s]
//            NDC.push(new NDCIdentity(this).toString());
//
//            this.protocol = (Protocol) MMJMXUtil.getMBean(protocolName,
//                    Protocol.class);
//            // set state of socket
//            protocol.setState(STATE_CLIENT_ADDRESS, socket.getInetAddress());
//            protocol.setState(STATE_LOCAL_ADDRESS, socket.getLocalAddress());
//
//            input = new BufferedInputStream(socket.getInputStream());
//            output = socket.getOutputStream();
//            protocol.greet(output);
//            ((AbstractProtocol) protocol).setServerThread(this);
//            boolean loop = true;
//            input.mark(2);
//            while (loop && input.read() != -1
//                    && life > System.currentTimeMillis()) {
//                input.reset();
//                // create Request object and parse
//                Request request = protocol.parseRequest(input, socket);
//                input.mark(2); // we've got our command...mark our new spot
//
//                // create Response object
//                Response response = protocol.handleRequest(output, request);
//                input.mark(2);
//                loop = !response.isFinish();
//                if (loop) {
//                    // MIKEA: avoid delay on SMTP QUIT, etc. (not sure why we
//                    // need delay anyway?)
//                    Thread.sleep(1000);
//                }
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                protocol.handleCleanup();
//                pool.free(this);
//                this.socket.close();
//            } catch (IOException ioe) {
//                ioe.printStackTrace();
//            } finally {
//                NDC.remove();
//            }
//        }
//
//    }

}
