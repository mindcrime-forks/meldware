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
import java.io.InputStream;
import java.nio.ByteBuffer;

import org.jboss.aspects.tx.Tx;
import org.jboss.aspects.tx.TxType;

/**
 * A stream that reads data from a Blob in a disconnected manner.    Responible for managing
 * which store (id) and where in the store (position) the data is to come from.
 * 
 * @author Michael Barker <mailto:mike@middlesoft.co.uk>
 *
 */
public class BlobInputStream extends InputStream {
    protected final static int DEFAULT_BUFFER_SIZE = 1024;

    private ByteBuffer buffer;

    private int pageSize;

    private long position;

    private AbstractStore store;

    private StoreItemMetaData meta;

    /**
     * Initialises the input stream.
     * 
     * @param store
     * @param meta
     * @throws StoreException 
     */
    @Tx(TxType.REQUIRED)
    public BlobInputStream(AbstractStore store, StoreItemMetaData meta)
            throws StoreException {
        this.buffer = null;
        this.store = store;
        this.meta = meta;
        this.pageSize = meta.getPageSize();
        this.position = meta.getStartIndex();
    }

    /**
     * Read a byte at a time from the buffer for the blob.
     * 
     * @see java.io.InputStream#read()
     */
    @Tx(TxType.REQUIRED)
    public int read() throws IOException {
        boolean isRead = false;

        // If the buffer has not been initialised.
        // If all of the bytes have been read from the buffer.
        if (buffer == null || !buffer.hasRemaining()) {
            int i = loadBuffer();
            isRead = (i == 0);
        }

        // If the entire blob has be read exit.
        if (isRead) {
            return -1;
        } else {
            return buffer.get() & 0x000000ff;
        }
    }

    @Tx(TxType.REQUIRED)
    public int read(byte[] b, int off, int len) throws IOException {
        int fromBuffer = 0;
        int fromStore = 0;

        if (buffer == null || !buffer.hasRemaining()) {
            loadBuffer();
        }

        // Copy what is currently in the buffer to the output.
        if (buffer.hasRemaining()) {
            fromBuffer = Math.min(buffer.remaining(), len);
            buffer.get(b, off, fromBuffer);
            // If there is still more bytes to be read, read direct
            // from the store.
            if (fromBuffer < len) {
                fromStore = doRead(b, off + fromBuffer, len - fromBuffer);
            }
            return fromBuffer + fromStore;
        } else {
            return -1;
        }
    }

    /**
     * Does the actual read from the store.  Loads the internal
     * buffer.
     * 
     * @param b
     * @param off
     * @param len
     * @return
     * @throws IOException
     */
    @Tx(TxType.REQUIRED)
    private int doRead(byte[] b, int off, int len) throws IOException {
        try {
            int numBytes = store.read(meta.getPid(), position, b, off, len);
            if (numBytes > 0) {
                position += numBytes;
            }
            return numBytes;
        } catch (StoreException e) {
            throw new IOException(e.getMessage());
        }
    }
    
    /* (non-Javadoc)
     * @see java.io.InputStream#skip(long)
     */
    @Override
    public long skip(long n) throws IOException {
        long remaining = meta.getItemSize() - position;
        long toSkip = Math.min(n, remaining);
        position += toSkip;
        return toSkip;
    }

    /**
     * Loads and wraps bytes directly from the store.
     * Does less copying.
     * 
     * @return
     * @throws IOException
     */
    @Tx(TxType.REQUIRED)
    private int loadBuffer() throws IOException {

        try {
            this.buffer = store.getBuffer(meta.getPid(), position, pageSize);
            position += buffer.limit();
            return buffer.limit();
        } catch (StoreException e) {
            throw new IOException(e.getMessage());
        }
    }
}
