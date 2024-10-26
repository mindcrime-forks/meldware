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

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import javax.management.ObjectName;
import javax.naming.InitialContext;
import javax.net.ssl.SSLServerSocketFactory;

import org.jboss.security.SecurityDomain;
import org.jboss.system.ServiceMBeanSupport;

/**
 * Server constitutes a process or thread which listens on a given port,
 * delegates the connection to a thread using a pre-specified protocol. We can
 * handle one of any number of pre-configured protocols provided there is an
 * assoicated class which implements the protocol interface. You need a seperate
 * instance of Server for each protocol and/or port you wish to use/listen on.
 * Meaning I can have two SMTP Servers with one instance of the associated
 * protocol, two instances of Server with different port settings. I could not
 * make one Server listen on two ports...
 * 
 * @author Andrew C. Oliver
 * @version $Revision: 1.5 $
 */
public class Server extends ServiceMBeanSupport implements ServerMBean,
        Runnable {
    private ServerSocket serverSocket;

    protected ThreadPoolMBean pool;

    private String sslDomain;

    private SSLServerSocketFactory sslServerSocketFactory;

    /**
     * what port we are listening on (usually 25 for SMTP)
     */
    protected int port;

    /**
     * how many requests can sit in the queue (native dependent)
     */
    private int backlog;

    /**
     * life is how long the whole request thread should last
     */
    protected long life;

    /**
     * timeout is how long we will wait in between requests -- Not yet
     * implmeneted...sorry
     */
    protected long timeout;

    /**
     * address is generally going to be "localhost" -- its a "ServerSocket"
     * thing (or actually goes down to the C library but that is another story
     * for another caffiene burst)
     */
    protected String address;

    /**
     * flag used to tell us if we're supposed to be shutting down...
     */
    private boolean shutdown = false;

    /**
     * this is our thread for Server
     */
    private Thread serverThread;

    /**
     * This is the JMX name of the protocol we want to use...later this should
     * possibly be a class name as well but i haven't figured that out yet
     */

    protected ObjectName protocolName;

    private boolean usesSSL;

    /**
     * @return class/jmx name of the protocol in use.
     */
    public ObjectName getProtocol() {
        return protocolName;
    }

    /**
     * @param protocol
     *            name of the protocol for this service to use.
     */
    public void setProtocol(ObjectName protocol) {
        if (protocol == null || protocolName == null
                || !protocol.equals(protocolName)) {
            this.protocolName = protocol;
        }
    }

    /**
     * @return number of milliseconds in between requests before we kill the
     *         connection
     */
    public long getTimeout() {
        return timeout;
    }

    /**
     * @param timeout
     *            in milliseconds (max time allowed between requests)
     */
    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    /**
     * @return address from java.net.ServerSocket ("localhost" is normal)
     */
    public String getAddress() {
        return address;
    }

    /**
     * @param address
     *            from java.net.ServerSocket ("localhost" is sensible)
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * @return number of milliseconds this connection can live before we kill it
     *         (regardless)
     */
    public long getLife() {
        return life;
    }

    /**
     * @param life -
     *            number of milliseconds this connection can live before we kill
     *            it (regardless)
     */
    public void setLife(long life) {
        this.life = life;
    }

    /**
     * Create an instance of server
     * 
     * @param port
     *            TCP port to listen on (25 for SMTP)
     * @param backlog
     *            number of requests to backlog before refusing them (usually a
     *            max of 5 on most systems)
     * @param address
     *            of the local system (127.0.0.1 for testing purposes)
     * @param protocol
     *            key name for the Protocol (generally JMX name)
     * @param timeout
     *            is the maximum amount of time between requests (not yet
     *            implemented)
     * @param life
     *            is the maximum amount of time the connection can live before
     *            we kill it regardless
     */
    public Server(int port, int backlog, String address, ObjectName protocol,
            long timeout, long life) {
        this.port = port;
        this.backlog = backlog;
        this.address = address;
    }

    public Server() {
    }

    public void setThreadPool(ThreadPoolMBean tp) {
        this.pool = tp;
    }

    public ThreadPoolMBean getThreadPool() {
        return this.pool;
    }

    public void startService() throws Exception {
        log.info("starting Server Service instance");
        setupSSLServerSocketFactory();
        log.info("starting serverThread...");
        serverThread = new Thread(this);
        serverThread.start();
        log.info("serverThread started");
        log.info("started Server Service instance");
    }

    public void stopService() {
        log.info("**** stopping Server Service instance");
        try {
            serverSocket.close();
            serverThread.interrupt();
            // poolthread.interrupt();

        } catch (Exception e) {

        }
        log.info("**** stopped Server Service instance");
    }

    /**
     * TCPIP port to run on (presently we use 9000 for testing most of the time)
     * 
     * @param port
     *            25 is normal for SMTP
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * @return port the service is running on
     */
    public int getPort() {
        return this.port;
    }

    /**
     * set the socket backlog
     * 
     * @param backlog -
     *            number of requests to queue at the low level. Operating system
     *            dependent on how much this matters. See ServerSocket for
     *            details, but 5 is a good number. Changing this after the
     *            service has started doesn't accomplish much (restart it)
     */
    public void setBacklog(int backlog) {
        this.backlog = backlog;
    }

    /**
     * @return backlog - number of requests to queue at the low level. Operating
     *         system dependent on how much this matters. See ServerSocket for
     *         details, but 5 is a good number.
     */
    public int getBacklog() {
        return backlog;
    }

    /**
     * wait for a connection then delegate it to its handler.
     */
    public void run() {
        try {
            if (isUsesSSL()) {
                serverSocket = sslServerSocketFactory.createServerSocket(port,
                        backlog, InetAddress.getByName(address));
            } else {
                serverSocket = new ServerSocket(port, backlog, InetAddress
                        .getByName(address));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Loop waiting for a request
        while (!shutdown) {
            Socket socket = null;
            try {
                log.debug("waiting for request");
                socket = serverSocket.accept();
                log.debug("Got request");
                /*
                 * Thread thread = pool.assign( socket, protocolName, timeout,
                 * System.currentTimeMillis() + life);
                 */
                pool.handleConnection(socket, protocolName, timeout, System
                        .currentTimeMillis()
                        + life);
                // thread.start();
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
    }

    public boolean isUsesSSL() {
        return usesSSL;
    }

    public void setUsesSSL(boolean usesSSL) {
        this.usesSSL = usesSSL;
    }

    /**
     * Set up the SSLServerSocketFactory to be used to create the wrapper SSL
     * Socket when SSL/TLS is to be used
     */
    private void setupSSLServerSocketFactory() throws RuntimeException {
        SSLServerSocketFactory sslServerSocketFactory = null;
        if (sslDomain != null) {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();

            try {
                // /////////////////////////////////////////////////////////
                // Here we want to use an SSLSocketFactory that understands
                // security domains. org.jboss.security.ssl.DomainSocket
                // does but doesn't exist in 3.2.3. Another implementation
                // has been provided for 3.2.3 hence this "clutter"!
                //
                // Once 3.2.4 has taken over the alternative implementation
                // should be scrapped.

                try {
                    // This only exists in 3.2.4 and not in 3.2.3
                    Class sfClass = loader
                            .loadClass("org.jboss.security.ssl.DomainServerSocketFactory");
                    sslServerSocketFactory = (SSLServerSocketFactory) sfClass
                            .newInstance();
                } catch (Exception e) {
                    throw new RuntimeException(
                            "Could not create SSLSocketFactory", e);
                }

                Class<? extends SSLServerSocketFactory> sfClass = sslServerSocketFactory.getClass();
                InitialContext ctx = new InitialContext();
                SecurityDomain domain = (SecurityDomain) ctx.lookup(sslDomain);
                Class[] parameterTypes = { SecurityDomain.class };
                Method m = sfClass.getMethod("setSecurityDomain",
                        parameterTypes);
                Object[] args = { domain };
                m.invoke(sslServerSocketFactory, args);

            } catch (NoSuchMethodException e) {
                log
                        .error("Socket factory does not support setSecurityDomain(SecurityDomain)");
                throw new RuntimeException("Could not set up security for TLS",
                        e);
            } catch (Exception e) {
                log.error("Failed to setSecurityDomain=" + sslDomain
                        + " on socket factory", e);
                throw new RuntimeException("Could not set up security for TLS",
                        e);
            }
        } else {
            // If no security domain has been created, it makes no sense to
            // have enabled SSL
            if (isUsesSSL()) {
                log
                        .error("usesSSL was set to true, but no SecurityDomain was specified");
                throw new RuntimeException("No SecurityDomain was specified");
            }

        }

        this.sslServerSocketFactory = sslServerSocketFactory;
    }

    /**
     * Set the JAAS security domain to be used with server sockets that support
     * SSL
     * 
     * @param domainName
     *            The JNDI name of the security domain used with server sockets
     *            that support SSL
     */
    public void setSslDomain(String domainName) {
        try {
            log.debug("setSecurityDomain()");
            this.sslDomain = domainName;
        } catch (RuntimeException e) {
            log.error("Error", e);
            throw e;
        }
    }

    /**
     * Get the JAAS security domain to be used with server sockets that support
     * SSL
     * 
     * @return The JNDI name of the security domain used with server sockets
     *         that support SSL
     */
    public String getSslDomain() {
        return sslDomain;
    }

}
