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

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;


/**
 * The reaper attempts to enforce a timeout.
 * 
 * @author Andrew C. Oliver <acoliver ot jboss dat org>
 * @version $Revision: 1.2 $
 */
public class Reaper implements Runnable {
    long pulse;

    private Map<Thread,TimeAndLife> reapables;

    private boolean stop = false;

    private static final Logger logger = Logger.getLogger(Reaper.class);

    public Reaper(long pulse) {
        logger.debug("Pulse value is " + pulse);
        this.pulse = pulse;
        this.reapables = new ConcurrentHashMap<Thread,TimeAndLife>(50);
    }

    public void stop() {
        this.stop = true;
        run();
    }

    public void start() {
        this.stop = false;
    }

    public void run() {
        try {
            while (stop == false) {
                Thread.sleep(pulse);
                long thetime = System.currentTimeMillis();
                synchronized (reapables) {
                    Iterator reapme = reapables.entrySet().iterator();

                    while (reapme.hasNext()) {
                        TimeAndLife tal = (TimeAndLife) ((Map.Entry) reapme
                                .next()).getValue();
                        if (tal.check(thetime)) {
                            reapme.remove();
                        }
                    }
                }
            }
            if (stop == true) {
                synchronized (reapables) {
                    Iterator reapme = reapables.entrySet().iterator();
                    while (reapme.hasNext()) {
                        TimeAndLife tal = (TimeAndLife) ((Map.Entry) reapme
                                .next()).getValue();
                        tal.kill();
                        reapme.remove();
                    }
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param thread
     * @param timeout
     * @param life
     */
    public void register(Closable closable, Thread thread, long timeout, long life) {
        TimeAndLife tal = new TimeAndLife(closable, thread, timeout, life,
                reapables);
        tal.update(System.currentTimeMillis());
        reapables.put(thread, tal);
    }

    public void remove(Thread thread) {
        reapables.remove(thread);
    }

    public int getReapableSize() {
        return reapables.size();
    }

    public void lock(Thread thread) {
        TimeAndLife tal = (TimeAndLife) reapables.get(thread);
        tal.lock();
    }

    public void unlock(Thread thread) {
    	try {
    		TimeAndLife tal = (TimeAndLife) reapables.get(thread);
    		tal.unlock();
    	} catch (Exception e) {
    		
    	}
    }

    public void update(Thread thread) {
        TimeAndLife tal = (TimeAndLife) reapables.get(thread);
        tal.update(System.currentTimeMillis());
    }

    /**
     * @param ssc
     * @param thisthread
     * @param timeout
     * @param life
     */
//    public void register(ServerSocketChannel ssc, Thread thread, long timeout,
//            long life) {
//        TimeAndLife tal = new TimeAndLife(ssc, thread, timeout, life, reapables);
//        tal.update(System.currentTimeMillis());
//        reapables.put(thread, tal);
//    }

}

class TimeAndLife {
    
    private final static Logger log = Logger.getLogger(TimeAndLife.class);
    
    private long thistime;

    private long time;

    private long life;

    private Thread thread;

    //private Socket socket;

    private boolean locked; // if we're processing a command we cannot timeout

    //private ServerSocketChannel ssc;

    private Map reapables;

    private Closable closable;

    public TimeAndLife(Closable closable, Thread thread, long time, long life,
            Map reapables) {
        //this.socket = socket;
        this.closable = closable;
        this.time = time;
        this.life = life;
        this.thread = thread;
        this.reapables = reapables;
    }

    public void kill() {
        try {
            log.warn("Reaping Thread: " + thread.getId());
            closable.close();
        } catch (Exception e) {
        }
        thread.interrupt();
    }

    /**
     * @param ssc
     * @param thread2
     * @param timeout
     * @param life2
     */
//    public TimeAndLife(ServerSocketChannel ssc, Thread thread, long time,
//            long life, Map reapables) {
//        this((Socket) null, thread, time, life, reapables);
//        this.ssc = ssc;
//    }

    public synchronized boolean check(long thetime) {
        boolean retval = false;
        // if we have timed out
        if (this.locked == false && ((thistime + time) < thetime)) {
            log.warn("Reaping Thread: " + thread.getId());
            closable.close();
//            try {
//                if (socket == null) {
//                    ssc.close();
//                } else {
//                    socket.close();
//                }
//            } catch (IOException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
            thread.interrupt();
            retval = true;
            reapables.remove(thread);
        }

        // if our life is over
        if ((life < thetime)) {
            log.warn("Reaping Thread: " + thread.getId());
            closable.close();
//            try {
//                if (socket == null) {
//                    ssc.close();
//                } else {
//                    socket.close();
//                }
//            } catch (IOException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
            thread.interrupt();
            retval = true;
        }
        return retval;
    }

    // we just executed a command so update
    public synchronized void update(long time) {
        thistime = time;
    }

    synchronized void lock() {
        this.locked = true;
    }

    synchronized void unlock() {
        this.locked = false;
    }

}
