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
import java.util.Collection;
import java.util.LinkedHashSet;

/**
 * Wraps up an InputStream from a blob with its associated connection
 * to allow for the connnection to be closed when the stream is.  Also the
 * Transaction in which the blob was create is passed into the Stream and
 * will be committed when the stream is closed.
 * 
 * @author Michael Barker <mailto:mike@middlesoft.co.uk>
 * @version $Revision: 1.1 $
 */
public class WrappedInputStream extends InputStream {
    private Collection<StreamCloseListener> listeners;
    InputStream in;
    public WrappedInputStream(InputStream in) {
        this.in = in;
        this.listeners = new LinkedHashSet<StreamCloseListener>();
    }

    /**
     * @see java.io.InputStream#close()
     */
    public void close() throws IOException {
        try {
            in.close();
        } finally {
            for (StreamCloseListener l : listeners) {
                l.streamClosed(new StreamCloseEvent(this));
            }
        }
    }
    
    

    /**
     * @return
     * @throws IOException
     * @see java.io.InputStream#available()
     */
    public int available() throws IOException {
        return in.available();
    }

    /**
     * @param readlimit
     * @see java.io.InputStream#mark(int)
     */
    public void mark(int readlimit) {
        in.mark(readlimit);
    }

    /**
     * @return
     * @see java.io.InputStream#markSupported()
     */
    public boolean markSupported() {
        return in.markSupported();
    }

    /**
     * @return
     * @throws IOException
     * @see java.io.InputStream#read()
     */
    public int read() throws IOException {
        return in.read();
    }

    /**
     * @param b
     * @param off
     * @param len
     * @return
     * @throws IOException
     * @see java.io.InputStream#read(byte[], int, int)
     */
    public int read(byte[] b, int off, int len) throws IOException {
        return in.read(b, off, len);
    }

    /**
     * @param b
     * @return
     * @throws IOException
     * @see java.io.InputStream#read(byte[])
     */
    public int read(byte[] b) throws IOException {
        return in.read(b);
    }

    /**
     * @throws IOException
     * @see java.io.InputStream#reset()
     */
    public void reset() throws IOException {
        in.reset();
    }

    /**
     * @param n
     * @return
     * @throws IOException
     * @see java.io.InputStream#skip(long)
     */
    public long skip(long n) throws IOException {
        return in.skip(n);
    }

    /**
     * Adds a listeners to this stream.
     * 
     * @param l
     */
    public void addListener(StreamCloseListener l) {
        listeners.add(l);
    }

    /**
     * Removes a listener from this stream.
     * 
     * @param l
     */
    public void removeListener(StreamCloseListener l) {
        listeners.remove(l);
    }

}
