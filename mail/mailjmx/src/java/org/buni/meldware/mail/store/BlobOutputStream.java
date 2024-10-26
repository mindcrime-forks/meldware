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
package org.buni.meldware.mail.store;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.sql.SQLException;

import javax.naming.NamingException;

import org.jboss.logging.Logger;

/**
 * Input stream for write to underlying data stores.  Responible for managing
 * which store (id) and where in the store (position) the data is to go.
 * 
 * @author Michael Barker <mailto:mike@middlesoft.co.uk>
 * @version $Revision: 1.2 $
 */
public class BlobOutputStream extends OutputStream {
    private Logger log = Logger.getLogger(BlobOutputStream.class);

    private ByteBuffer buffer;

    private AbstractStore store;

    private long position;

    private StoreItemMetaData meta;

    /**
     * Initialises an input stream and will load the first <code>bufferSize</code>
     * bytes into the buffer.  If the <code>create</code> is true, it will try
     * to create the buffer before hand.
     * 
     * @param dsName
     * @param id
     * @param bufferSize
     * @param create
     * @throws StoreItemNotFoundException
     * @throws NamingException
     * @throws SQLException
     */
    public BlobOutputStream(AbstractStore store, StoreItemMetaData meta) {
        this.store = store;
        this.meta = meta;
        this.buffer = ByteBuffer.allocate(meta.getPageSize());
        this.position = meta.getStartIndex();
    }

    /* (non-Javadoc)
     * @see java.io.OutputStream#write(int)
     */
    public void write(int b) throws IOException {
        // Flush the buffer if it is full.
        if (buffer.remaining() <= 0) {
            flush();
        }
        buffer.put((byte) b);
    }

    /**
     * Implement batch writing to the store.  Basically flush
     * whatever is in the current buffer and then write this
     * entire array to the store.
     */
    public void write(byte[] b, int off, int len) throws IOException {
        int toCopy = Math.min(len, buffer.remaining());
        buffer.put(b, off, toCopy);
        if (buffer.remaining() <= 0) {
            // flush current buffer.
            flush();
        }

        // The amount remaining is larger than the buffer
        // so write it directly to the store.
        if (len - toCopy > buffer.capacity()) {
            doWrite(b, off + toCopy, len - toCopy);
        }
        // Otherwise write the remainder to the buffer.
        else if (len > toCopy) {
            buffer.put(b, off + toCopy, len - toCopy);
        }
    }

    private int doWrite(byte[] b, int off, int len) throws IOException {
        try {
            int numBytes = store.write(meta.getPid(), position, b, off, len);
            position += numBytes;
            return numBytes;
        } catch (Exception e) {
            log.error("Failed to write to Store: ", e);
            throw new IOException(e.getClass() + ":" + e.getMessage());
        }

    }

    public void flush() throws IOException {
        if (buffer.position() > 0) {
            buffer.flip();
            doWrite(buffer.array(), buffer.position(), buffer.remaining());
            buffer.clear();
        }
    }

    public void close() throws IOException {
        flush();
    }

}
