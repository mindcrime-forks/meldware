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
package org.buni.meldware.mail.dev.prot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;

import junit.framework.TestCase;

/**
 * @author Michael Barker
 *
 */
public class ProtocolHandler {

    private String host;
    private int port;
    private boolean isConnected;
    private Socket socket;
    private BufferedReader in;
    private OutputStream out;
    private final static byte[] ENDL = { '\r', '\n' };
    
    public ProtocolHandler(String host, int port) {
        this.host = host;
        this.port = port;
    }
    
    public void connect() {
        if (!isConnected) {
            try {
                socket = new Socket(host, port);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = socket.getOutputStream();                
                isConnected = true;
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    
    public void write(String s) {
        connect();
        try {
            out.write(s.getBytes("US-ASCII"));
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public void writeln(String s) {
        connect();
        try {
            out.write(s.getBytes("US-ASCII"));
            out.write(ENDL);
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Will block until a line is read.
     * 
     * @param pattern
     */
    public void checkInput(String pattern) {
        connect();
        try {
            String s = in.readLine();
            TestCase.assertNotNull("Response was null", s);
            TestCase.assertTrue("Input \n{"+s+"}\n did not match pattern: \n{" + pattern+"}\n", s.matches(pattern));
        } 
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
   
}
