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

import javax.management.ObjectName;

import org.jboss.system.ServiceMBean;

/**
 * Service interface for the Server MBean. Go look at Server.java in this same
 * package if you really want to know.
 * 
 * @author Andrew C. Oliver
 * @version $Revision: 1.2 $
 */
public interface ServerMBean extends ServiceMBean {

    /**
     * @param port
     *            number to listen on (25 for SMTP)
     */
    void setPort(int port);

    /**
     * @return port number we're listening on
     */
    int getPort();

    /**
     * @param address
     *            (almost always "localhost")
     */
    void setAddress(String address);

    /**
     * @return addres ("localhost")
     */
    String getAddress();

    /**
     * @param life -
     *            like sothsaysers....how long a connection instance will live
     *            for
     */
    void setLife(long life);

    /**
     * @return length which connections are permitted to live for
     */
    long getLife();

    /**
     * @param timeout
     *            between protocol commands... This is the idle time not the
     *            whole connection life. This shouldn't include the time for the
     *            command to execute.
     */
    void setTimeout(long timeout);

    /**
     * @return timeout between protocol commands.
     */
    long getTimeout();

    /**
     * @param backlog
     *            number of requests (max 5 on UNIX) which can be backlogged
     *            while we're maxed and can't assign them... just leave this at
     *            5 and don't mess with it.
     */
    void setBacklog(int backlog);

    /**
     * @return backlog number of requests
     */
    int getBacklog();

    /**
     * @param protocol
     *            name (JMX name usually)
     */
    void setProtocol(ObjectName protocol);

    /**
     * @return protocol name (JMX name usually)
     */
    ObjectName getProtocol();

    /**
     * @param poolsize
     *            threads available to handle connections (once they're all used
     *            in concurrency we run into the backlog, and those clients are
     *            blocked waiting on us...once we hit the backlog max, we refuse
     *            connections)
     */
    // void setPoolSize(int poolsize);
    /**
     * @return number of connections that can be handled.
     */
    // int getPoolSize();
    public void setThreadPool(ThreadPoolMBean tp);

    public ThreadPoolMBean getThreadPool();

    /**
     * Getter for property usesSSL.
     * 
     * @return Value of property usesSSL.
     */
    public boolean isUsesSSL();

    /**
     * Setter for property usesSSL.
     * 
     * @param usesSSL
     *            New value of property usesSSL.
     */
    public void setUsesSSL(boolean usesSSL);

    /**
     * Set the JAAS security domain to be used with server sockets that support
     * SSL
     * 
     * @param domainName
     *            The JNDI name of the security domain used with server sockets
     *            that support SSL
     */
    public void setSslDomain(String domainName);

    /**
     * Get the JAAS security domain to be used with server sockets that support
     * SSL
     * 
     * @return The JNDI name of the security domain used with server sockets
     *         that support SSL
     */
    public String getSslDomain();

}
