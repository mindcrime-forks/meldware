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
package org.buni.meldware.mail.message;

import java.io.Serializable;

import org.buni.meldware.mail.store.Store;
import org.buni.meldware.mail.store.StoreException;
import org.buni.meldware.mail.store.StoreItem;

/**
 * Holds the message body.  Works as proxy to the message store.
 * 
 * @author Michael Barker <mailto:mike@middlesoft.co.uk>
 * @version $Revision: 1.2 $
 */
public class StoredMailBody extends MailBody implements Serializable {
    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 3618700820481324343L;

    private Long id = null;

    private int size;

    protected StoredMailBody(StoreItem item) {
        this.size = item.getSize();
        this.id = item.getId();
    }

    /**
     * Factory method, writes the data to the store and returns
     * an StoredMailBody with a pointer to the store and object within.
     * 
     * @param storeName
     * @param input
     * @return
     * @throws StoreException
     */
    public static StoredMailBody newInstance(Store store)
            throws StoreException {
        StoreItem item = store.createStoreItem();

        StoredMailBody mailBody = new StoredMailBody(item);

        return mailBody;
    }

    /**
     * Given a string handle, restores the stored mail object.
     * 
     * @param store
     * @param handle
     * @return
     * @throws StoreException
     */
    public static StoredMailBody restore(Store store, Long storeId)
            throws StoreException {
        StoreItem item = store.getStoreItem(storeId);
        StoredMailBody mailBody = new StoredMailBody(item);

        return mailBody;
    }

    public long getSize() throws StoreException {
        return size;
    }

    public int getType() {
        return STORED_TYPE;
    }

    /**
     * Serializes to a string, using the stores specific encoding.
     * @throws StoreException 
     */
    public Long getStoreId() throws StoreException {
        return id;
    }

    /**
     * @see org.buni.meldware.mail.message.Body#setSize(int)
     */
    public void setSize(int size) {
        this.size = size;
    }

}
