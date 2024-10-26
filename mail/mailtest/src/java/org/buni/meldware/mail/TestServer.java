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
package org.buni.meldware.mail;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.buni.meldware.test.JMXTestWrapper;
import org.buni.meldware.mail.util.MMJMXUtil;

/** 
 * Tests for the server class
 * @author Andrew C. Oliver
 */
public class TestServer extends TestCase {

    ServerMBean server = null;
    private long[] concurrentresults;
    MBeanServerUtil mbsu;
    int port;

    public static TestSuite suite() {
        return JMXTestWrapper.suite(TestServer.class);
    }

    
   public TestServer(String name)
   {
      super (name);
   }
   
   
    public static void main(String[] args) {
        junit.textui.TestRunner.run(TestServer.class);
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        
        server = MMJMXUtil.getMBean("meldware.mail:name=SMTP,type=Service", 
                ServerMBean.class);
//        
//        server.getThreadPool().stop();
//        server.getThreadPool().setMax(100);
//        server.getThreadPool().start();
        
        port = server.getPort();
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
//        server.getThreadPool().stop();
//        server.getThreadPool().setMax(100);
//        server.getThreadPool().start();
    }

    /**
     * Creates a new instance of the server and tests that it can be started and stopped.
     * @throws Exception
     */
    public void testServer() throws Exception {
        // create a server on port 9000 with a 5 wait on the localhost using the SMTP
        // protocol
        Thread.sleep(5000);
    }

    public void testTwoConnections() throws Exception {
        Socket socket1 = new Socket("localhost", port);
        InputStream in1 = socket1.getInputStream();
        BufferedReader reader1 = new BufferedReader(new InputStreamReader(in1));

        String line = reader1.readLine();

        reader1.close();
        in1.close();
        socket1.close();

        Socket socket2 = new Socket("localhost", port);
        InputStream in2 = socket2.getInputStream();
        BufferedReader reader2 = new BufferedReader(new InputStreamReader(in2));

        line = reader2.readLine();
        System.out.println("line= " + line);

        System.out.println("line= " + line);

        reader2.close();
        in2.close();
        socket2.close();

    }

    public void testOneHundredConnections() throws Exception {
        long[] results = multiSubsequentConnectionTest(100);
        for (int k = 0; k < 100; k++) {
            System.out.println("msc result=" + results[k]);
            assertTrue(
                "multi-subsequent - request "
                    + k
                    + " took more than 5 seconds (thats really bad) "
                    + results[k],
                results[k] < 5000);
            assertTrue(
                "multi-subsequent request number " + k + " failed (dang!)",
                results[k] > -1);
        }
        /*     tearDown();
             setUp();*/
        results = multiReverseSubsequentConnectionTest(100);
        for (int k = 0; k < 100; k++) {
            System.out.println("mrsc result=" + results[k]);
            assertTrue(
                "multi-reverse-subsequent request "
                    + k
                    + " took more than 5 seconds (thats really bad) "
                    + results[k],
                results[k] < 5000);
            assertTrue(
                "multi-reverse-subsequent request number "
                    + k
                    + " failed (dang!)",
                results[k] > -1);
        }
        multiConcurrentConnectionTest(100);
        results = this.concurrentresults;
        for (int k = 0; k < 100; k++) {
            System.out.println("mcsc result=" + results[k]);
            assertTrue(
                "multi-concurrent request "
                    + k
                    + " took more than 5 seconds (thats pretty bad) "
                    + results[k],
                results[k] < 5000);
            assertTrue(
                "multi-concurrent request number " + k + " failed (dang!)",
                results[k] > -1);
        }

    }

    private long[] multiSubsequentConnectionTest(int num) {
        Socket[] s = new Socket[num];
        InputStream[] in = new InputStream[num];
        BufferedReader[] reader = new BufferedReader[num];
        long[] results = new long[num];
        for (int k = 0; k < num; k++) {
            try {
                long time = System.currentTimeMillis();
                s[k] = new Socket("localhost", port);
                in[k] = s[k].getInputStream();
                reader[k] = new BufferedReader(new InputStreamReader(in[k]));
                results[k] = System.currentTimeMillis() - time;
            } catch (Exception e) {
                results[k] = -1;
                e.printStackTrace();
            }
        }
        for (int k = 0; k < num; k++) {
            try {
                long time = System.currentTimeMillis();
                reader[k].readLine();
                reader[k].close();
                in[k].close();
                s[k].close();
                results[k] = results[k] + (System.currentTimeMillis() - time);
            } catch (Exception e) {
                results[k] = -1;
                e.printStackTrace();
            }
        }
        return results;
    }

    private long[] multiReverseSubsequentConnectionTest(int num)
        throws Exception {
        Socket[] s = new Socket[num];
        InputStream[] in = new InputStream[num];
        BufferedReader[] reader = new BufferedReader[num];
        long[] results = new long[num];
        for (int k = 0; k < num; k++) {
            long time = System.currentTimeMillis();
            s[k] = new Socket("localhost", port);
            in[k] = s[k].getInputStream();
            reader[k] = new BufferedReader(new InputStreamReader(in[k]));
            results[k] = System.currentTimeMillis() - time;
        }
        for (int k = num - 1; k > -1; k--) {
            long time = System.currentTimeMillis();
            reader[k].readLine();
            reader[k].close();
            in[k].close();
            s[k].close();
            results[k] = results[k] + (System.currentTimeMillis() - time);
        }
        return results;
    }

    public void multiConcurrentConnectionTest(int num) throws Exception {
        RequestThread[] requestThreads = new RequestThread[num];
        Thread[] threads = new Thread[num];
        this.concurrentresults = new long[num];
        boolean complete = false;
        for (int k = 0; k < requestThreads.length; k++) {
            requestThreads[k] = new RequestThread(k, this, port);
            threads[k] = new Thread(requestThreads[k]);
            this.concurrentresults[k] = -2;
        }
        for (int k = 0; k < requestThreads.length; k++) {
            threads[k].start();
            Thread.sleep(100);
        }
        Thread.sleep(5000);
        while (!complete) {
            for (int k = 0; k < requestThreads.length; k++) {
                complete = true;
                Thread.sleep(1);
                if (threads[k].isAlive() || this.concurrentresults[k] == -2) {
                    //          System.out.println("stuff isn't done yet");
                    complete = false;
                    break;
                }
            }
        }
        System.out.println("Stuff must be done now....sleeping");
        //Thread.sleep(120000);
    }

    public synchronized void addResult(int threadnum, long result) {
        concurrentresults[threadnum] = result;
    }

}

class RequestThread implements Runnable {
    private int threadnum;
    private TestServer test;
    private int port;
    RequestThread(int num, TestServer test, int port) {
        this.threadnum = num;
        this.test = test;
        this.port = port;
    }

    public void run() {
        try {
            long time = System.currentTimeMillis();
            Socket socket = new Socket("localhost", port);
            InputStream in = socket.getInputStream();
            BufferedReader reader =
                new BufferedReader(new InputStreamReader(in));
            long result = System.currentTimeMillis() - time;
            time = System.currentTimeMillis();
            reader.readLine();
            reader.close();
            in.close();
            socket.close();
            result = result + (System.currentTimeMillis() - time);
            test.addResult(threadnum, result);
        } catch (Exception e) {
            e.printStackTrace();
            test.addResult(threadnum, -1);
        }
    }

}
