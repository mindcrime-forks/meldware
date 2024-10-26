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
package org.buni.meldware.mail.imap4;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import org.buni.meldware.mail.util.io.WrappedOutputStream;

/**
 * @author Michael Barker
 *
 */
public class IMAP4OutputStream extends WrappedOutputStream {

//    private final static Log log = Log.getLog(IMAP4OutputStream.class);
//    private StringBuilder sb = null;
    private Charset cs;

    public IMAP4OutputStream(OutputStream d, String csn) {
        super(d);
        this.cs = Charset.forName(csn);
    }
    
    /**
     * Converts a string using the supplied charset.
     * 
     * @param s
     * @throws IOException
     */
    public void write(String s) throws IOException {
        ByteBuffer bb = cs.encode(s);
        int pos = bb.arrayOffset() + bb.position();
        write(bb.array(), pos, bb.remaining());
//        if (sb == null) {
//            sb = new StringBuilder();
//        }
//        sb.append(s);
//        if (sb.indexOf("\r\n") != -1) {
//            log.info("Response: %s", sb.toString().trim());
//            sb = null;
//        }
    }
    
    public void flush() throws IOException {
        super.flush();
    }
}
