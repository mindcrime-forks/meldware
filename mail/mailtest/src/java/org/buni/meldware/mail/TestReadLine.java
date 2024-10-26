/*
 * Bunisoft the Open Source Communications Company
 * Copyright 2006, Bunisoft Inc., and individual contributors as
 * indicated by the @authors tag.  See the copyright.txt in the 
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; version 2.1 of
 * the License.
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import junit.framework.TestCase;

/**
 * @author Michael.Barker
 *
 */
public class TestReadLine extends TestCase {

    protected String readCommand(InputStream stream) throws IOException {
        boolean firstchar = true;
        byte[] buffer = new byte[1];
        StringBuffer commstring = new StringBuffer();
        // XXX: This needs fixing.  Not charset safe.
        while (stream.read(buffer) != -1) {
            char c = (char) buffer[0];
            //System.out.print(HexDump.toHex(buffer[0]));
            commstring.append(c);
            String check = commstring.substring(commstring.length() - 1);
            // System.out.print(check);
            if (check.equals("\n") || check.equals("\r")) {
                if (firstchar == false) {
                    commstring.deleteCharAt(commstring.length() - 1);
                    break;
                } else {
                    commstring.deleteCharAt(commstring.length() - 1);
                }
            } else {
                firstchar = false;
            }
        }
        stream.mark(2);
        return commstring.toString();
    }
    
    public int appendLine(StringBuilder b, InputStream in, String charSet) 
    throws IOException {
        
        int numRead = 0;
        
        if (!in.markSupported()) {
            throw new IOException("mark(int) not support for input stream");
        }
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        
        int c;
        while ((c = in.read()) != -1) {
            numRead++;
            if (c == '\r') {
                break;
            } else if (c == '\n') {
                in.mark(1);
                int nextC = in.read();
                if (nextC != '\r') {
                    in.reset();
                } else {
                    numRead++;
                }
                break;
            } else {
                out.write(c);
            }
        }
        
        if (out.size() > 0) {
            b.append(out.toString(charSet));
        }
        
        return numRead;
    }

    public void testReadLine() throws Exception {
        
        byte[] b = { 'a','a','a','a','a','a','a','\n','\r','a','a','\r','\n' };
        InputStream in = new ByteArrayInputStream(b);
        String s;
        s = readCommand(in);
        System.out.println(s.length() + ": " + s);
        s = readCommand(in);
        System.out.println(s.length() + ": " + s);
        s = readCommand(in);
        System.out.println(s.length() + ": " + s);
        s = readCommand(in);
        System.out.println(s.length() + ": " + s);
        
    }
    
    public void testAppendLine() throws Exception {
        
        byte[] b = { 'a','a','a','a','a','a','a','\n','\r','a','a','\n','\r' };
        InputStream in = new ByteArrayInputStream(b);
        {
            StringBuilder sb = new StringBuilder();
            int i = appendLine(sb, in, "US-ASCII");
            System.out.println(i + ": " + sb);
        }
        {
            StringBuilder sb = new StringBuilder();
            int i = appendLine(sb, in, "US-ASCII");
            System.out.println(i + ": " + sb);
        }
        {
            StringBuilder sb = new StringBuilder();
            int i = appendLine(sb, in, "US-ASCII");
            System.out.println(i + ": " + sb);
        }
        {
            StringBuilder sb = new StringBuilder();
            int i = appendLine(sb, in, "US-ASCII");
            System.out.println(i + ": " + sb);
        }        
    }
    
}
