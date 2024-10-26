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
import java.io.OutputStream;
import java.security.NoSuchAlgorithmException;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import org.buni.meldware.common.logging.Log;
import org.buni.meldware.mail.util.io.CountingOutputStream;
import org.buni.meldware.mail.util.io.HashingOutputStream;
import org.buni.meldware.mail.util.io.IOUtil;
import org.buni.meldware.mail.util.io.StreamCloseEvent;
import org.buni.meldware.mail.util.io.StreamCloseListener;
import org.jboss.aspects.tx.Tx;
import org.jboss.aspects.tx.TxType;

/**
 * Implemenation of a proxy to the store hold an
 * id reference and meta data information for the
 * store.
 * 
 * @author Michael Barker <mailto:mike@middlesoft.co.uk>
 * @version $Revision: 1.2 $
 */
public class StoreItemImpl implements StoreItem, StreamCloseListener {
    private final static Log log = Log.getLog(StoreItemImpl.class);

    private Long id;

    private AbstractStore store;

    private StoreItemMetaData metaData;

    private CountingOutputStream cOut;

    private HashingOutputStream hOut;

    /**
     * Initialises a StoreMBean Item.
     * 
     * @param dsName
     * @param id
     * @throws StoreException
     */
    public StoreItemImpl(Long id, AbstractStore store) {
        this.id = id;
        this.store = store;
    }

    /**
     * @see org.buni.meldware.mail.store.Stream#getId()
     */
    public Long getId() {
        return id;
    }

    /**
     * @see org.buni.meldware.mail.store.Stream#getInputStream()
     */
    @Tx(TxType.REQUIRED)
    public InputStream getInputStream() throws StoreException {
        InputStream in = store.getInputStream(id, getMetaData());
        if (getMetaData().getCompressed()) {
            in = new InflaterInputStream(in, new Inflater(), getMetaData()
                    .getPageSize());
        }
        return in;
    }

    /**
     * @see org.buni.meldware.mail.store.Stream#getOuputStream()
     */
    @Tx(TxType.REQUIRED)
    public OutputStream getOuputStream() throws StoreException {
        OutputStream out = null;
        try {
            out = store.getOutputStream(id, getMetaData());
            if (getMetaData().getCompressed()) {
                out = new DeflaterOutputStream(out, new Deflater(),
                        getMetaData().getPageSize());
            }
            if (getMetaData().getHashed()) {
                hOut = new HashingOutputStream("SHA-256", out);
                cOut = new CountingOutputStream(hOut);
            } else {
                cOut = new CountingOutputStream(out);
            }
            cOut.addListener(this);
            return cOut;
        } catch (NoSuchAlgorithmException e) {
            IOUtil.quietClose(log, out);
            throw new StoreException(e);
        }
    }

    public void delete() throws StoreException {
        store.delete(id);
    }

    public int getSize() throws StoreException {
        return (int) getMetaData().getItemSize();
    }

    @Tx(TxType.REQUIRED)
    public StoreItemMetaData getMetaData() throws StoreException {
        if (metaData == null) {
            setMetaData(store.loadMetaData(id));
        }
        return metaData;
    }

    public void setMetaData(StoreItemMetaData metaData) {
        this.metaData = metaData;
    }

    /**
     * Gets the store object.
     * @return
     */
    public AbstractStore getStore() {
        return this.store;
    }

    /**
     * Closes the store item and saves the associated
     * meta data.
     * 
     * @throws StoreException
     */
    public void close() throws StoreException {
        StoreItemMetaData md = getMetaData();
        md.setItemSize(Math.max(cOut.getCounter(), md.getItemSize()));
        if (md.getHashed()) {
            md.setHash(hOut.getHash());
        }
        getStore().updateMetaData(md);
    }

    /**
     * Lets the StoreItem know when the stream has been closed.
     * 
     * @param event
     */
    @Tx(TxType.REQUIRED)
    public void streamClosed(StreamCloseEvent event) throws IOException {
        try {
            close();
        } catch (StoreException e) {
            throw new IOException(e.getMessage());
        }
    }

    /* (non-Javadoc)
     * @see org.buni.meldware.mail.store.StoreItem#getStartIndex()
     */
    public long getStartIndex() {
        return getMetaData().getStartIndex();
    }

}
