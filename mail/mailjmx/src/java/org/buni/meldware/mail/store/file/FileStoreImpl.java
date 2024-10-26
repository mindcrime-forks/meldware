/*
 * Bunisoft the Open Source Communications Company
 * Copyright 2006, Bunisoft Inc., and individual contributors as
 * indicated by the @authors tag.  See the copyright.txt in the 
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; version 2.1 of
 * the License.
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
package org.buni.meldware.mail.store.file;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Properties;

import org.buni.meldware.mail.store.AbstractStore;
import org.buni.meldware.mail.store.StoreException;
import org.buni.meldware.mail.store.StoreItemMetaData;
import org.jboss.aspects.tx.Tx;
import org.jboss.aspects.tx.TxType;

/**
 * Uses our new handy dandy filestore stores body parts in a series of
 * subdirectories.
 * 
 * @author Andrew C. Oliver
 * 
 */
public class FileStoreImpl extends AbstractStore implements FileStoreService {

    org.buni.filestore.FileStore fileStore;

    private String path;

    private int tries;

    public void init() {
        Properties props = new Properties();
        props.put("base.dir", path);
        props.put("tries", tries);
        this.fileStore = new org.buni.filestore.FileStore(props);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.buni.meldware.mail.store.AbstractStore#doCreate()
     */
    @Override
    protected Long doCreate() throws StoreException {
        return fileStore.allocate();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.buni.meldware.mail.store.StoreMBean#cleanUp(java.util.List)
     */
    public void cleanUp(List<Long> bods) {
        for (int i = 0; i < bods.size(); i++) {
            this.delete(bods.get(i));
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.buni.meldware.mail.store.StoreMBean#delete(java.lang.Long)
     */
    public void delete(Long id) throws StoreException {
        // TODO Auto-generated method stub
        fileStore.setToZeroLength(id);
    }

    /**
     * 
     * @see org.buni.meldware.mail.store.Store#getBuffer(java.lang.Object,
     *      long, int)
     * 
     */
    @Tx(TxType.REQUIRED)
    public ByteBuffer getBuffer(Long id, long position, int len) throws StoreException {
        ByteBuffer buffer;

        // log.debug("Get Buffer oid: " + id + ", position: " + position
        // + ", length: " + len);

        try {
            FileInputStream stream = (FileInputStream) fileStore.getInputStream(id, false);
            stream.getChannel().position(position);
            byte[] b = new byte[len];
            int numRead = stream.read(b, 0, len);
            buffer = ByteBuffer.wrap(b, 0, numRead);
            stream.close();
        } catch (Exception e) {
            throw new StoreException(e);
        }

        return buffer;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.buni.meldware.mail.store.StoreMBean#getInputStream(java.lang.Long,
     *      org.buni.meldware.mail.store.StoreItemMetaData)
     */
    public InputStream getInputStream(Long id, StoreItemMetaData meta) throws StoreException {
        // TODO Auto-generated method stub
        return fileStore.getInputStream(id, true);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.buni.meldware.mail.store.StoreMBean#getOutputStream(java.lang.Long,
     *      org.buni.meldware.mail.store.StoreItemMetaData)
     */
    public OutputStream getOutputStream(Long id, StoreItemMetaData meta) throws StoreException {
        // TODO Auto-generated method stub
        return fileStore.getOutputStream(id, true);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.buni.meldware.mail.store.StoreMBean#idToString(java.lang.Object)
     */
    public String idToString(Object id) {
        // TODO Auto-generated method stub
        return id.toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.buni.meldware.mail.store.StoreMBean#read(java.lang.Long, long,
     *      byte[], int, int)
     */
    public int read(Long id, long position, byte[] b, int off, int len) throws StoreException {
        int read = 0;
        try {
            InputStream stream = fileStore.getInputStream(id, true);
            stream.skip(position);
            read = stream.read(b, off, len);
            stream.close();
        } catch (Exception e) {
            throw new StoreException(e);
        }
        return read;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.buni.meldware.mail.store.StoreMBean#stringToId(java.lang.String)
     */
    public Object stringToId(String s) {
        // TODO Auto-generated method stub
        return Long.parseLong(s);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.buni.meldware.mail.store.StoreMBean#write(java.lang.Long, long,
     *      byte[], int, int)
     */
    public int write(Long id, long position, byte[] b, int off, int len) throws StoreException {
        int written = 0;
        try {
            FileOutputStream stream = (FileOutputStream) fileStore.getOutputStream(id, true);
            stream.getChannel().position(position);
            stream.write(b, off, len);
            written = len;
            stream.close();
        } catch (Exception e) {
            throw new StoreException(e);
        }
        return written;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.buni.meldware.mail.store.file.FileStore#getAttemptsPerAllocate()
     */
    public int getAttemptsPerAllocate() {
        return this.tries;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.buni.meldware.mail.store.file.FileStore#getPath()
     */
    public String getPath() {
        return this.path;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.buni.meldware.mail.store.file.FileStore#setAttemptsPerAllocate(int)
     */
    public void setAttemptsPerAllocate(int tries) {
        this.tries = tries;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.buni.meldware.mail.store.file.FileStore#setPath(java.lang.String)
     */
    public void setPath(String path) {
        this.path = path;
    }

}
