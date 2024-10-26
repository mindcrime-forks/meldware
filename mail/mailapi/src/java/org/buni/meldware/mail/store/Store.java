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

import java.io.IOException;

/**
 * General interface for underlying storage.
 * 
 * @author Michael Barker <mailto:mike@middlesoft.co.uk>
 * @version $Revision: 1.6 $
 */
public interface Store {


    /**
     * Creates a store and will autogenerate and return the id.
     * @return
     * @throws StoreException
     */
    StoreItem createStoreItem() throws StoreException;

    StoreItem getStoreItem(Long id) throws StoreException;

    void delete(Long id) throws StoreException;

    //
    // Generic Properties
    //
    boolean getCompress();

    int getPageSize();

    int getCompressBufferSize();

    int getBufferSize();

    int getStartIndex();

    boolean getHashed();

    String listMetaData();

    /**
     * commit the transaction for this store item
     */
    void commit();
    
    /**
     * Method for debugging.  Should not be used generally.
     * 
     * @param id
     * @return
     * @throws IOException 
     */
    String toString(Long id) throws IOException;
    
    public int purge();
}
