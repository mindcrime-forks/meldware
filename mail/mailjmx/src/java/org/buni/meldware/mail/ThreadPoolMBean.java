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

import java.net.Socket;

import javax.management.ObjectName;

import org.jboss.system.ServiceMBean;

/**
 * Thread Pool is used by instances of ServerMBean to handle the actual
 * connections. This implements a basic thread-per-connection model.
 * 
 * @author Andrew C. Oliver <acoliver ot jboss dat org>
 * @version $Revision: 1.4 $
 */
public interface ThreadPoolMBean extends ServiceMBean {

    /**
     * set the initial number of threads in the pool
     * 
     * @param initial
     *            number of threads
     */
    void setInitial(int initial);

    /**
     * @return initial number of threads in the pool
     */
    int getInitial();

    /**
     * @return Returns the max number of threads in the pool.
     */
    int getMax();

    /**
     * set the maximum number of threads in the pool
     * 
     * @param max
     *            The max to set.
     */
    void setMax(int max);

    /**
     * @return Returns the min nubmer of thread in the pool
     */
    int getMin();

    /**
     * sets the minimum numbe rof threads in the pool
     * 
     * @param min
     *            The min to set.
     */
    void setMin(int min);

    /**
     * @return Returns the idleKeepAlive.
     */
    int getIdleKeepAlive();

    /**
     * actual number of threads in the pool
     */
    int getPoolSize();

    /**
     * number of threads in use
     */
    int getActivePoolSize();

    /**
     * set the number of ms to keep a connection open when its just sitting
     * there.
     * 
     * @param idleKeepAlive
     *            The idleKeepAlive to set.
     */
    void setIdleKeepAlive(int idleKeepAlive);

    /**
     * handle a normal socket connection
     * 
     * @param socket
     * @param protocolName
     * @param timeout
     * @param life
     * @throws InterruptedException
     */
    void handleConnection(Socket socket, ObjectName protocolName, long timeout,
            long life) throws InterruptedException;

}
