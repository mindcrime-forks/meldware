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

import org.jboss.system.ServiceMBeanSupport;

import EDU.oswego.cs.dl.util.concurrent.PooledExecutor;

/**
 * Basic executor thread pool for all servers.
 * 
 * @author Andrew C. Oliver <acoliver ot jboss dat org>
 * @version $Revision: 1.4 $
 */
public class ThreadPool extends ServiceMBeanSupport implements ThreadPoolMBean {
    private int initial;

    private int min;

    private int max;

    private int idleKeepAlive;

    private PooledExecutor executor;

    private Thread reaperThread;

    private Reaper reaper;

    // todo:make configurable
    private long pulse = 1000;

    public ThreadPool() {
        executor = new PooledExecutor();
        reaper = new Reaper(pulse);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jboss.system.ServiceMBeanSupport#startService()
     */
    public void startService() {
  //      System.err.println("***** THREAD POOL **** SERVICENAME = "+this.getServiceName().toString());
        updateParms();
        revise();
        reaper.start();
        reaperThread = new Thread(reaper);
        reaperThread.start();
    }
    
    
    /*
     * (non-Javadoc)
     * 
     * @see org.jboss.system.ServiceMBeanSupport#stopService()
     */
    public void stopService() {
        reaper.stop();
        reaperThread.interrupt();
    }

    /**
     * called whenever the poolsize or keepalive time has to be adjusted
     */
    private void updateParms() {
        if(max > 0) {
        	executor.setMaximumPoolSize(max);
        }
        executor.setMinimumPoolSize(min);
        executor.setKeepAliveTime(idleKeepAlive);
    }

    /**
     * called whenever we need to adjust the number of threads in the upward
     */
    private void revise() {
        if (initial > executor.getPoolSize()) {
            executor.createThreads(initial - executor.getPoolSize());
        }
        if (min > executor.getPoolSize()) {
        	executor.createThreads(min - executor.getPoolSize());
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.buni.meldware.mail.ThreadPoolMBean#handleConnection(java.net.Socket,
     *      java.lang.String, long, long)
     */
    public void handleConnection(Socket socket, ObjectName protocolName,
            long timeout, long life) throws InterruptedException {
        ConnectionHandler ch = new ConnectionHandler(socket, protocolName,
                timeout, life, reaper);
        executor.execute(ch);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.buni.meldware.mail.ThreadPoolMBean#setInitial(int)
     */
    public void setInitial(int initial) {
        this.initial = initial;
        updateParms();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.buni.meldware.mail.ThreadPoolMBean#getInitial()
     */
    public int getInitial() {
        return initial;
    }

    /**
     * @return Returns the max number of threads in the pool
     */
    public int getMax() {
        return max;
    }

    /**
     * sets the max number of thread in the pool
     * 
     * @param max
     *            The max to set.
     */
    public void setMax(int max) {
        this.max = max;
        updateParms();
        this.revise();
    }

    /**
     * @return Returns the min number of threads in the pool
     */
    public int getMin() {
        return min;
    }

    /**
     * sets the min number of threads in the pool
     * 
     * @param min
     *            The min to set.
     */
    public void setMin(int min) {
        this.min = min;
        updateParms();
        revise();
    }

    /**
     * @return Returns the idleKeepAlive.
     */
    public int getIdleKeepAlive() {
        return idleKeepAlive;
    }

    /**
     * @param idleKeepAlive
     *            The idleKeepAlive to set.
     */
    public void setIdleKeepAlive(int idleKeepAlive) {
        this.idleKeepAlive = idleKeepAlive;
        updateParms();
        revise();
    }

    public int getPoolSize() {
        return this.executor.getPoolSize();
    }

    public int getActivePoolSize() {
        return this.reaper.getReapableSize();
    }

}
