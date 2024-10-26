/*
 * Bunisoft the Open Source Communications Company
 * Copyright 2006, Bunisoft LLC.,
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

import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Michael Barker <mailto:mike@middlesoft.co.uk>
 * @version $Revision: 1.2 $
 */
public interface StoreItem {
    /**
     * Get a unique idenfifier for this Stream StoreMBean.
     * @return
     */
    public Long getId();

    /**
     * Get a input stream that will the data from the store.
     * @return
     * @throws StoreException 
     */
    public InputStream getInputStream() throws StoreException;

    /**
     * Get an output stream that can be used to write information
     * back to the store.
     * 
     * @return
     * @throws StoreException 
     */
    public OutputStream getOuputStream() throws StoreException;

    /**
     * Delete this store item.
     * 
     * @throws StoreException
     */
    public void delete() throws StoreException;

    /**
     * Get the size of this item.
     * 
     * @return
     * @throws StoreException
     */
    public int getSize() throws StoreException;

    public long getStartIndex();

}
