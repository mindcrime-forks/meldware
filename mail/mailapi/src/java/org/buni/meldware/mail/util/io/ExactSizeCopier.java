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
 * This is used for IMAP copies where we know the exact size.
 *
 * @author Andrew C. Oliver
 */
public class ExactSizeCopier implements Copier {

    private long size;
    
    /**
     * @param bufferSize
     * @param size
     */
    public ExactSizeCopier(long size) {
       this.size = size;
    }

    /* (non-Javadoc)
     * @see org.buni.meldware.mail.util.io.Copier#copy(java.io.InputStream, java.io.OutputStream)
     */
    public int copy(InputStream in, OutputStream out, int blockSize) throws IOException {
        byte[] buffer = new byte[(int) Math.min(size, Math.max(blockSize, MIN_BLOCK_SIZE))];
        int bytesread = 0;
        
        for (long bytesleft = size; bytesleft > 0 && bytesread > -1 ; bytesleft -= bytesread) {
            if (bytesleft < buffer.length) {
                buffer = new byte[(int)bytesleft];
            }
            bytesread = in.read(buffer);
            out.write(buffer);
        }
        return (int) size; //this is naughty...we could store crap > 2g via imap...unlikely but we could...
    }

}
