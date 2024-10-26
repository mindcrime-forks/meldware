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

import static org.buni.meldware.mail.util.HibernateUtil.singleResult;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.buni.meldware.common.logging.Log;
import org.buni.meldware.mail.JPAService;
import org.buni.meldware.mail.MailException;
import org.buni.meldware.mail.util.io.IOUtil;
import org.jboss.aspects.tx.Tx;
import org.jboss.aspects.tx.TxType;
import org.jboss.system.ServiceMBean;
import org.jboss.system.ServiceMBeanSupport;
import org.jboss.util.id.GUID;

/**
 * Abstact StoreMBean MBean contains all common functionality for the 
 * actual StoreMBean items.  StoreMBean's should inherit from this object.
 * 
 * @author Michael Barker <mailto:mike@middlesoft.co.uk>
 * @version $Revision: 1.7 $
 */
public abstract class AbstractStore extends ServiceMBeanSupport implements
        ServiceMBean, Store {

    public final static int DEFAULT_PAGE_SIZE = 65536;

    public final static int DEFAULT_BUFFER_SIZE = 65536;

    private final static Log log = Log.getLog(AbstractStore.class);

    private boolean compress = false;

    private int compressBufferSize = 8192;

    private int pageSize = DEFAULT_PAGE_SIZE;

    private int bufferSize = DEFAULT_BUFFER_SIZE;

    private int startIndex = 0;

    private boolean hashed = false;
    
    private JPAService jpaService;
    private EntityManager em;

    public AbstractStore() {

    }

    /**
     * this is a horrible kludge used to init the entity manager (the initEM method itself is a bit of a kludge)
     * which should be removed when JBAS supports injecting dependencies via external descriptors.
     */
    protected EntityManager emInit() {
    	if (em == null) {
    		throw new MailException("EntityManager is not initialised");
    	}
    	return em;
    }
    
    public void setJPAService(JPAService jpaService) {
    	this.jpaService = jpaService;
    }


    /**
     * Perform a partial write of the data to the store.
     * 
     * @param id The id of the store item to write to.
     * @param position The offset with in the store to start writing at.
     * @param b Data to write.
     * @param off Offset in the data to write.
     * @param len Number of bytes to write.
     * @return The number of bytes written.
     * @throws StoreException
     */
    public abstract int read(Long id, long position, byte[] b, int off, int len)
            throws StoreException;

    public abstract int write(Long id, long position, byte[] b, int off, int len)
            throws StoreException;

    /**
     * Reads a ByteBuffer from the blob.  This is primarly use only for the blob Input/Output
     * Streams.
     * 
     * @param id
     * @param position
     * @param len
     * @return
     * @throws StoreException
     */
    public abstract ByteBuffer getBuffer(Long id, long position, int len) throws StoreException;
    
    /**
     * Gets an input stream for the store
     * 
     * @throws StoreException 
     */
    public abstract InputStream getInputStream(Long id, StoreItemMetaData meta)
            throws StoreException;

    /**
     * Get an output stream for the store.
     * 
     * @param id
     * @return
     * @throws StoreException
     */
    public abstract OutputStream getOutputStream(Long id, StoreItemMetaData meta)
            throws StoreException;
    

    protected Object createId() {
        return GUID.asString();
    }

    /**
     * Gets the store item.
     * 
     * TODO: Check that this store item exists.
     * @throws StoreException
     */
    @Tx(TxType.REQUIRED)
    public StoreItem getStoreItem(Long id) throws StoreException {
        StoreItemMetaData meta = loadMetaData(id);
        StoreItemImpl item = new StoreItemImpl(id, this);
        item.setMetaData(meta);
        return item;
    }

    /**
     * Creates a new item in the store.
     * 
     */
    @Tx(TxType.REQUIRED)
    public StoreItem createStoreItem() throws StoreException {
        emInit();
        long id = doCreate();
        StoreItemImpl item = new StoreItemImpl(id, this);
        StoreItemMetaData meta = createMetaData(id);
        item.setMetaData(meta);

        return item;
    }

    public void commit() {
        //     try {
        //    getUserTransaction().commit();
        //   } catch (Exception e) {
        //     throw new RuntimeException("could not commit store transaction", e);
        //}
    }

    /**
     * This is smelly but we have no way to inject this into a superclass
     * @return
     */
    //abstract protected UserTransaction getUserTransaction();
    abstract protected Long doCreate() throws StoreException;

    /**
     * 
     * @param id
     * @return
     * @throws StoreException
     * 
     */
    @Tx(TxType.REQUIRED)
    public StoreItemMetaData createMetaData(Long id) throws StoreException {
        EntityManager session = emInit();
        StoreItemMetaData meta;
        try {
            meta = new StoreItemMetaData();
            meta.setPid(id);
            meta.setCompressed(getCompress());
            meta.setPageSize(getPageSize());
            meta.setItemSize(0);
            meta.setStartIndex(getStartIndex());
            meta.setHashed(getHashed());
            //sess = getSession();
            session.persist(meta);
            session.flush();
        } catch (PersistenceException e) {
            throw new StoreException(e);
        } finally {
            //  HibernateUtil.safeCloseSession(log, sess);
        }

        return meta;
    }

    /**
     * Gets the supplied meta data for the given object.
     * 
     * @param id
     * @return
     * @throws StoreException
     * 
     */
    @Tx(TxType.REQUIRED)
    public StoreItemMetaData loadMetaData(Long id) throws StoreException {
        StoreItemMetaData meta;

        try {

            String sql = "from StoreItemMetaData md where md.pid=:pid";
            meta = singleResult(emInit().createQuery(sql).setParameter(
                    "pid", id),StoreItemMetaData.class);
            log.debug("Loaded MetaData: " + meta + " for Pid: " + id);
        } catch (PersistenceException e) {
            throw new StoreException(e);
        } finally {
            //    HibernateUtil.safeCloseSession(log, sess);
        }

        return meta;
    }

    /**
     * Saves the supplied meta data.
     * 
     */
    @Tx(TxType.REQUIRED)
    public void updateMetaData(StoreItemMetaData meta) throws StoreException {
    	EntityManager session = emInit();
        log.debug("Saving Meta Data for Id: " + meta.getPid());
        try {
            StoreItemMetaData nm = session.merge(meta);
            session.persist(nm);
            session.flush();
        } catch (PersistenceException e) {
            throw new StoreException(e);
        } finally {
            //    HibernateUtil.safeCloseSession(log, sess);
        }
    }

    /**
     * Lists all of the meta data in the store.
     * 
     */
    @Tx(TxType.REQUIRED)
    public String listMetaData() {
        EntityManager em = emInit();
        StringBuffer sb = new StringBuffer();

        Query query = em.createQuery("from org.buni.meldware.mail.store.StoreItemMetaData");
        List l = query.getResultList();

        for (Iterator i = l.iterator(); i.hasNext();) {
            sb.append(i.next().toString());
            sb.append("\n");
        }

        return sb.toString();
    }
    
	final static String SELECT_ORPHANED = "SELECT s.pid FROM StoreItemMetaData s WHERE s.pid NOT IN (SELECT storeId FROM MailBodyReference) AND s.pid NOT IN (SELECT bodyId FROM MessageBody)";
	final static String DELETE_BULK = "DELETE FROM StoreItemMetaData s WHERE s.pid IN (?1, ?2, ?3, ?4, ?5, ?6, ?7, ?8, ?9, ?10)";
	final static String DELETE = "DELETE FROM StoreItemMetaData s WHERE s.pid = (?1)";
    final static int BLOCK_SIZE = 10;
	
    /**
     * TODO: This breaks encapsulation.
     */
    @Tx(TxType.REQUIRED)
    public int purge() {
    	EntityManager em = emInit();
    	Query q = em.createQuery(SELECT_ORPHANED);
    	@SuppressWarnings("unchecked")
    	List<Long> pids = q.getResultList();
    	delete(pids);
    	return pids.size();
    }
    
    @Tx(TxType.REQUIRED)
    public void delete(List<Long> pids) {
    	log.info("Deleting %d store items", pids.size());
    	EntityManager em = emInit();
    	for (int i = 0; i < pids.size(); i+=BLOCK_SIZE) {
    		int block = i/BLOCK_SIZE;
    		int remaining = pids.size() - (block*BLOCK_SIZE);
    		if (remaining < BLOCK_SIZE) {
        		for (int j = 0; j < remaining; j++) {
        	    	Query q = em.createQuery(DELETE);
        	    	q.setParameter(1, pids.get(j + (block*BLOCK_SIZE)));
        	    	q.executeUpdate();
        		}
    		} else {
    			Query q = em.createQuery(DELETE_BULK);
        		for (int j = 0; j < BLOCK_SIZE; j++) {
        			q.setParameter(j+1, pids.get(j + (block*BLOCK_SIZE)));
        		}
        		q.executeUpdate();
    		}
    	}
    	for (Long pid : pids) {
    		delete(pid);
    	}
    }


    //=====================
    // Properties
    //=====================

    public void setCompress(boolean b) {
        this.compress = b;
    }

    public boolean getCompress() {
        return this.compress;
    }

    /**
     * @return Returns the bufferSize.
     */
    public int getPageSize() {
        return pageSize;
    }

    /**
     * @param pageSize The bufferSize to set.
     */
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * @return Returns the compressBufferSize.
     */
    public int getCompressBufferSize() {
        return compressBufferSize;
    }

    /**
     * @param compressBufferSize The compressBufferSize to set.
     */
    public void setCompressBufferSize(int compressBufferSize) {
        this.compressBufferSize = compressBufferSize;
    }

    /**
     * @return Returns the bufferSize.
     */
    public int getBufferSize() {
        return bufferSize;
    }

    /**
     * @param bufferSize The bufferSize to set.
     */
    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public void setHashed(boolean hashed) {
        this.hashed = hashed;
    }

    public boolean getHashed() {
        return this.hashed;
    }

    //===================
    // MBean Methods.
    //===================

    /* (non-Javadoc)
     * @see org.jboss.system.Service#start()
     */
    public void startService() throws Exception {
        em = jpaService.getEntityManager();
        validate();
        init();
    }

    /**
     * Validates the generic configuration properties.
     * Subclass should override this method and call
     * <code>super.validate()</code>.
     * 
     * @throws StoreException
     */
    public void validate() throws StoreException {

    }

    /**
     * Should be overidden by subclasses to perform any necessary initalisation
     * upon startup.
     * 
     * @throws StoreException
     */
    public void init() throws StoreException {
        //  initEM();
    }

    /* (non-Javadoc)
     * @see org.jboss.system.Service#stop()
     */
    public void stopService() {
    }

    public void testNull(String name, Object o) throws StoreException {
        if (o == null) {
            throw new StoreException("Property: " + name + " has not been set");
        }
    }
    
    public String toString(Long id) throws IOException {
        StoreItem item = getStoreItem(id);
        InputStream in = item.getInputStream();
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append(IOUtil.toString(in, "US-ASCII"));
        sb.append("}");
        return sb.toString();
    }

}
