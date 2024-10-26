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
package org.buni.meldware.mail.message;



import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.buni.meldware.common.logging.Log;
import org.buni.meldware.mail.JPAService;
import org.buni.meldware.mail.MailException;
import org.buni.meldware.mail.store.Store;
import org.buni.meldware.mail.store.StoreException;
import org.buni.meldware.mail.store.StoreItem;
import org.buni.meldware.mail.util.io.Copier;
import org.buni.meldware.mail.util.io.IOUtil;
import org.buni.meldware.mail.util.io.SimpleCopier;
import org.jboss.aspects.tx.Tx;
import org.jboss.aspects.tx.TxType;
import org.jboss.system.ServiceMBeanSupport;

/**
 * Manages the bodies of email messages, handles creation
 * and restoration.  Acts as a proxy to the store if configured
 * to do so.
 * 
 * @author Michael Barker <mailto:mike@middlesoft.co.uk>
 * @version $Revision: 1.6 $
 */
public class MailBodyManagerImpl extends ServiceMBeanSupport implements MailBodyManagerImplMBean {
    
    private final static int DEFAULT_BUFFER_SIZE = 4096;
    private final static Copier COPIER = new SimpleCopier();
    private final static Log log = Log.getLog(MailBodyManagerImpl.class);
    
    private Store store;
    private int bufferSize = DEFAULT_BUFFER_SIZE;
	private JPAService jpaService;

    /**
     * Creates a mail body from an existing set of data.
     * @param storeId
     * 
     * @return
     * @throws StoreException
     */
    @Tx(TxType.REQUIRED)
    public MailBody createMailBody(Long storeId)
            throws StoreException {
        MailBody body;
        Store store = getStore();
        body = StoredMailBody.restore(store, storeId);
        return body;
    }


    @Tx(TxType.REQUIRED)
    public MailBody createMailBody() throws StoreException {

        return StoredMailBody.newInstance(store);

    }

    /**
     * Returns the store.
     * 
     * @return
     * @throws StoreException
     */
    public Store getStore() {
        return this.store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    public int getBufferSize() {
        if (store == null) {
            return this.bufferSize;            
        }
        else {
            return store.getPageSize();
        }
    }

    /**
     * @see org.buni.meldware.mail.message.MailBodyManager#getInputStream()
     */
    @Tx(TxType.REQUIRED)
    public InputStream getInputStream(Body body) throws MailException {
        return getItem(body).getInputStream();
    }

    /**
     * @see org.buni.meldware.mail.message.MailBodyManager#read(org.buni.meldware.mail.message.Body, java.io.InputStream)
     */
    public int read(Body body, InputStream in) throws MailException {
        return read(body, in, COPIER);
    }

    /**
     * @see org.buni.meldware.mail.message.MailBodyManager#read(org.buni.meldware.mail.message.Body, java.io.InputStream, org.buni.meldware.mail.util.io.Copier)
     */
    @Tx(TxType.REQUIRED)
    public int read(Body body, InputStream in, Copier copier) throws MailException {
        try {
            StoreItem item = getItem(body);
            OutputStream out = item.getOuputStream();
            int retval = copier.copy(in, out, getBufferSize());
            out.flush();
            out.close();
            getStore().commit();
            body.setSize(item.getSize());
            return retval;
        } catch (IOException e) {
            throw new MailException(e);
        }
    }

    /**
     * @see org.buni.meldware.mail.message.MailBodyManager#write(org.buni.meldware.mail.message.Body, java.io.OutputStream)
     */
    public void write(Body body, OutputStream out) throws MailException {
        write(body, out, COPIER);
    }

    /**
     * @see org.buni.meldware.mail.message.MailBodyManager#write(org.buni.meldware.mail.message.Body, java.io.OutputStream, org.buni.meldware.mail.util.io.Copier)
     */
    @Tx(TxType.REQUIRED)
    public void write(Body body, OutputStream out, Copier copier) throws MailException {
        InputStream in = getItem(body).getInputStream();
        try {
            copier.copy(in, out, getBufferSize());
        } catch (IOException e) {
            throw new MailException(e);
        } finally {
            IOUtil.quietClose(log, in);
        }
    }
    
    public int getSize(Body body) {
        return getItem(body).getSize();
    }
    
    private StoreItem getItem(Body body) {
        return store.getStoreItem(body.getStoreId());
    }
    
    @Tx(TxType.REQUIRED)
    public long addReference(Body body) {
    	MailBodyReference mr = new MailBodyReference();
    	mr.setStoreId(body.getStoreId());
    	mr = getEM().merge(mr);
    	long reference = mr.getId();
    	log.debug("Added reference %d, for item %d", reference, body.getStoreId());
    	return reference;
    }
    
    @Tx(TxType.REQUIRED)
    public void removeReference(long reference, Body body) {
    	String queryStr = "DELETE FROM MailBodyReference WHERE id = ?1 and storeId = ?2";
    	Query q = getEM().createQuery(queryStr);
    	q.setParameter(1, reference);
    	q.setParameter(2, body.getStoreId());
    	int count = q.executeUpdate();
    	log.debug("Removed reference %d, for item %d, count %d", reference, body.getStoreId(), count);
    }
    
    public void setJPAService(JPAService jpaService) {
    	this.jpaService = jpaService;
    }
     
    public EntityManager getEM() {
    	if (jpaService == null) {
    		throw new MailException("EntityManager is not initialised");
    	}
    	return jpaService.getEntityManager();
    }
        
    public void purge() {
    	getStore().purge();
    }
}
