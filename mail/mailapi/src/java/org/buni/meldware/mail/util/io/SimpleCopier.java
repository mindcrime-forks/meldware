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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


/**
 * Implements a simple buffered copy from an input stream
 * to an output stream.
 * 
 * @author Michael Barker
 * @version $Revision: 1.2 $
 */
public class SimpleCopier implements Copier {

    /**
     * @see org.buni.meldware.mail.util.io.Copier#copy(java.io.InputStream, java.io.OutputStream)
     */
    public int copy(InputStream in, OutputStream out, int blockSize) throws IOException {
        byte[] block = new byte[Math.max(blockSize, MIN_BLOCK_SIZE)];
        
        int numBytes = 0;
        int totalBytes = 0;
        while ((numBytes = in.read(block)) != -1) {
            out.write(block, 0, numBytes);
            totalBytes += numBytes;
        }
        
        return totalBytes;    
    }
}
