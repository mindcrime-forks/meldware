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
package org.buni.meldware.mail.fetchmail;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;

import javax.mail.Header;

/**
 * Provide an input stream that contains the whole message
 * @author hwr@pilhuhn.de
 * @version $Revision: 1.2 $
 */
public class MailConcatInputStream extends InputStream {

    private InputStream bodyStream;

    private String headerString;

    private int curChar;

    private int headerLen;

    private int state;

    private final static int STATE_HEADER = 1;

    private final static int STATE_BODY = 2;

    private final static int STATE_END = 3;

    /**
     * 
     */
    public MailConcatInputStream(InputStream body, Enumeration headers) {
        bodyStream = body;
        state = STATE_HEADER;
        StringBuffer hdrs = new StringBuffer();
        while (headers.hasMoreElements()) {
            Header h = (Header) headers.nextElement();
            hdrs.append(h.getName());
            hdrs.append(": ");
            hdrs.append(h.getValue());
            hdrs.append("\r\n");
        }
        hdrs.append("\r\n"); // header body separator
        hdrs.append("\r\n"); // header body separator
        headerString = hdrs.toString();
        curChar = 0;
        headerLen = headerString.length();
    }

    /* (non-Javadoc)
     * @see java.io.InputStream#read()
     */
    public int read() throws IOException {
        int ret = -1;

        if (state == STATE_HEADER) {
            ret = headerString.charAt(curChar);
            curChar++;
            if (curChar >= headerLen)
                state = STATE_BODY;
        } else if (state == STATE_BODY) {
            ret = bodyStream.read();
            if (ret == -1) { // end of mail message		
                state = STATE_END;
                curChar = 0;
            }
        } else if (state == STATE_END) { // add \r\n at the end
            if (curChar == 0) {
                ret = '\r';
                curChar++;
            } else if (curChar == 1) {
                ret = '\n';
                curChar++;
            } else if (curChar == 2) {
                ret = '.';
                curChar++;
            } else if (curChar == 3) {
                ret = '\r';
                curChar++;
            } else if (curChar == 4) {
                ret = '\n';
                curChar++;
            } else {
                ret = -1;
            }
        } else {
            throw new RuntimeException("unknown state, bailing out");
        }
        return ret;
    }
}
