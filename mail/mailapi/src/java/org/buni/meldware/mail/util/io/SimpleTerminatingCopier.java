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
package org.buni.meldware.mail.util.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

/**
 * Copier implementation that handles SMTP dot stuffing.  The input
 * stream will be dot stuffed.  I.e. any time a "\r\n." string is written
 * it will contain an extra "." just after.  This class removes those dots
 * therefore (unstuffing them) hence the name.  Makes sense?  I hope so.
 * 
 * @author Michael Barker
 * @version $Revision: 1.3 $
 */
public class SimpleTerminatingCopier implements Copier {
    public final static byte CR = (byte) '\r';

    public final static byte LF = (byte) '\n';

    public final static byte DOT = (byte) '.';

    public final static byte[] SMTPTerminator = { '\r', '\n', '.', '\r', '\n' };

    public SimpleTerminatingCopier() {
    }

    /**
     * Copies from the InputStream to the OutputStream terminating with
     * "\r\n.\r\n" and stuffing dots.  Will reset the input stream to the 
     * point just after the input termination.  If an EOF occurs before a 
     * "\r\n.\r\n" then an exception will be thrown.
     * 
     * @param in
     * @param out
     * @throws IOException
     */
    public int copy(InputStream in, OutputStream out, int blockSize) throws IOException {

        byte[] last = new byte[5];

        int datum;
        ByteArrayOutputStream tmpOut = new ByteArrayOutputStream();

        while (true) {
            datum = in.read();
            if (datum != -1) {
                last[0] = last[1];
                last[1] = last[2];
                last[2] = last[3];
                last[3] = last[4];
                last[4] = (byte) datum;

                tmpOut.write(datum);

                if (Arrays.equals(last, SMTPTerminator)) {
                    break;
                }
            } else {
                throw new IOException("Premature EOF");
            }
        }

        byte[] tmpData = tmpOut.toByteArray();
        out.write(tmpData, 0, tmpData.length - 3);

        return tmpData.length - 3;
    }
}
