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

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;

/**
 * @author Michael Barker <mailto:mike@middlesoft.co.uk>
 * @version $Revision: 1.1 $
 */
public class WrappedOutputStream extends FilterOutputStream {
    private Collection<StreamCloseListener> listeners;

    protected OutputStream out;

    public WrappedOutputStream(OutputStream out) {
        super(out);
        this.out = out;
        this.listeners = new LinkedHashSet<StreamCloseListener>();
    }

    /**
     * @see java.io.InputStream#close()
     */
    public void close() throws IOException {
        try {
            super.close();
        } finally {
            for (Iterator i = listeners.iterator(); i.hasNext();) {
                StreamCloseListener l = (StreamCloseListener) i.next();
                l.streamClosed(new StreamCloseEvent(this));
            }
        }
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
