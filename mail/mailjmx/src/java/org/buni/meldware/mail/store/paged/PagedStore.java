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
package org.buni.meldware.mail.store.paged;



import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.List;

import javax.naming.NamingException;
import javax.persistence.EntityManager;

import org.buni.meldware.common.logging.Log;
import org.buni.meldware.mail.store.AbstractStore;
import org.buni.meldware.mail.store.BlobInputStream;
import org.buni.meldware.mail.store.BlobOutputStream;
import org.buni.meldware.mail.store.StoreException;
import org.buni.meldware.mail.store.StoreItemMetaData;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.jboss.aspects.tx.Tx;
import org.jboss.aspects.tx.TxType;
import org.jboss.ejb3.entity.HibernateSession;

/**
 * @author Michael Barker <mailto:mike@middlesoft.co.uk>
 * @version $Revision: 1.4 $
 */
public class PagedStore extends AbstractStore implements PagedStoreMBean {
    final static Log log = Log.getLog(PagedStore.class);

    /* (non-Javadoc)
     * @see org.buni.meldware.mail.store.AbstractStore#init()
     */
    public void init() throws StoreException {
        //  super.init();
    }

    /**
     * Uses Long as Id.
     * 
     * @see org.buni.meldware.mail.store.Store#idToString(java.lang.Object)
     */
    public String idToString(Object id) {

        return id.toString();
    }

    /**
     * Uses Long as Id.
     * 
     * @see org.buni.meldware.mail.store.Store#stringToId(java.lang.String)
     */
    public Object stringToId(String s) {
        return new Long(s);
    }

    /**
     * Reads <code>len</code> bytes from the paged store from <code>position</position>
     * within the store.
     * 
     */
    @Tx(TxType.REQUIRED)
    public int read(Long id, long position, byte[] b, int off, int len)
            throws StoreException {
        EntityManager session = emInit();
        
        log.debug("Read, Id: %d, Position: %d, Offset: %d, Length: %d", id, 
                position, off, len);

        try {
            Blob blob = (Blob) session.find(Blob.class, (Serializable) id);

            int pageSize = blob.getPageSize();
            int firstPage = (int) (position / pageSize);
            int pageOffset = (int) (position % pageSize);
            int numPages = (int) Math.ceil((double) (pageOffset + len)
                    / (double) pageSize);
            int totalBytes = 0;

            if (position < blob.getLength()) {
                int inputOffset = pageOffset;
                int outputOffset = off;

                for (int i = firstPage; i < (firstPage + numPages)
                        && i < blob.getNumPages(); i++) {
                    Page page = getPage(session, id, i);
                    int outputLength = Math.min(page.getSize() - inputOffset,
                            len - totalBytes);
                    System.arraycopy(page.getData(), inputOffset, b,
                            outputOffset, outputLength);
                    inputOffset = 0;
                    outputOffset += outputLength;
                    totalBytes += outputLength;
                }
            } else {
                totalBytes = 0;
            }

            session.flush();
            ((HibernateSession) session).getHibernateSession().clear();
            //  session.close();
            return totalBytes;
        } catch (StoreException e) {
            String errorMsg = "Failed to read. "
                    + formatArgs(id, position, off, len) + ". Reason:"
                    + e.getMessage();
            log.error(errorMsg);
            throw e;
        } catch (Throwable t) {
            String errorMsg = "Failed to read. "
                    + formatArgs(id, position, off, len) + ". Reason:"
                    + t.getMessage();
            log.error(errorMsg);
            throw new StoreException(t);
        } finally {

        }
    }

    /**
     * Queries for a specified page.
     * 
     * @param session
     * @param blobId
     * @param pageNo
     * @return
     * @throws HibernateException
     * @throws StoreException
     * 
     */
    @Tx(TxType.REQUIRED)
    private Page getPage(EntityManager session, Long id, int pageNo)
            throws HibernateException, StoreException {
        emInit();
        //todo change to named query
        String sql = "from Page where blobId = :bid and pageNo = :pid";
        javax.persistence.Query q = session.createQuery(sql).setParameter(
                "bid", id).setParameter("pid", pageNo).setMaxResults(1);
        @SuppressWarnings("unchecked")
        List<Page> pages = (List<Page>) q.getResultList();

        if (pages.size() == 0) {
            throw new StoreException("Page not found.  Id: " + id
                    + " Page number: " + pageNo);
        }

        return (Page) pages.get(0);
    }

    /**
     * Write the data in <code>b</code> to the db as a page or series of
     * pages.
     * 
     * @see org.buni.meldware.mail.store.Store#write(Long, long, byte[], int, int)
     * 
     */
    @Tx(TxType.REQUIRED)
    public int write(Long id, long position, byte[] b, int off, int len)
            throws StoreException {
        EntityManager session = emInit();
        log.debug("Write, Id: %d, Position: %d, Offset: %d, Length: %d", id, 
                position, off, len);

        try {
            Blob blob = (Blob) session.find(Blob.class, id);

            // First we need a little math.
            int pageSize = blob.getPageSize();
            int firstPage = (int) (position / pageSize);
            int pageOffset = (int) (position % pageSize);
            int numPages = (int) Math.ceil((double) (pageOffset + len)
                    / (double) pageSize);

            // Create any placeholder empty pages.
            for (int i = blob.getNumPages(); i < firstPage; i++) {
                Page page = new Page();
                blob.addPage(page);
                session.persist(page);
            }

            if (firstPage > blob.getNumPages()) {
                throw new RuntimeException("First page: " + firstPage
                        + " is well after: " + blob);
            }

            // Fast path.  If the byte array matches the page size
            // exactly.  This is what should happen if we are using
            // the store properly.
            if (off == 0
                    && b.length == len
                    && (len == pageSize || (len < pageSize && firstPage == blob
                            .getNumPages())) && (position % pageSize) == 0) {
                Page page;
                if (firstPage < blob.getNumPages()) {
                    page = getPage(session, id, firstPage);
                } else {
                    page = new Page();
                    blob.addPage(page);
                }
                page.setData(b);

                blob.updateLength(page);
                session.persist(page);
            }
            // If the request does not match the page structure exactly.
            // This is slower as it will have copy the data from the supplied
            // byte array.
            else {
                int inputOffset = off;
                int outputOffset = pageOffset;

                for (int i = firstPage; i < (firstPage + numPages); i++) {
                    Page page;
                    if (i < blob.getNumPages()) {
                        page = getPage(session, id, firstPage);
                    } else {
                        page = new Page();
                        blob.addPage(page);
                    }

                    int outputLength = Math.min(pageSize - outputOffset, len
                            - (inputOffset - off));
                    byte[] data = page.getData();
                    // If the byte array is not large enough to accomidate
                    // the current write.
                    if (data.length < outputLength + outputOffset) {
                        byte[] newData = new byte[outputLength + outputOffset];
                        System.arraycopy(data, 0, newData, 0, data.length);
                        data = newData;
                        page.setData(data);
                    }

                    System.arraycopy(b, inputOffset, data, outputOffset,
                            outputLength);

                    blob.updateLength(page);
                    session.persist(page);

                    outputOffset = 0;
                    inputOffset += outputLength;
                }
            }

            session.persist(blob);
            session.flush();
            ((HibernateSession) session).getHibernateSession().clear();
            return len;
        } catch (StoreException e) {
            String errorMsg = "Failed to write. "
                    + formatArgs(id, position, off, len) + ". Reason:"
                    + e.getMessage();
            log.error(errorMsg);
            throw e;
        } catch (Throwable t) {
            String errorMsg = "Failed to write. "
                    + formatArgs(id, position, off, len) + ". Reason:"
                    + t.getMessage();
            log.error(errorMsg);
            throw new StoreException(t);
        } finally {
            // HibernateUtil.safeCloseSession(log, session);         
        }
    }

    /**
     * Loads an NIO Byte Buffer with the data from the store.  Used by the blobinputstream
     * to reduce copying.
     * 
     * @see org.buni.meldware.mail.store.Store#getBuffer(Long, long, int)
     * 
     */
    @Tx(TxType.REQUIRED)
    public ByteBuffer getBuffer(Long id, long position, int len)
            throws StoreException {
        EntityManager session = emInit();
        log.debug("Get Buffer, Id: %d, Position: %d, Length: %d", id, position, 
                len);

        try {
            ByteBuffer buffer;

            Blob blob = (Blob) session.find(Blob.class, id);

            int pageSize = blob.getPageSize();
            int firstPage = (int) (position / pageSize);
            int pageOffset = (int) (position % pageSize);
            int numPages = (int) Math.ceil((double) (pageOffset + len)
                    / (double) pageSize);

            //if (firstPage >= blob.getNumPages())
            if (position < blob.getLength()) {
                // Fast Path
                if (len == pageSize && (position % pageSize) == 0) {
                    Page page = getPage(session, id, firstPage);
                    buffer = ByteBuffer.wrap(page.getData());
                }
                // Slow Path
                else {
                    byte[] data = new byte[(int) Math.min(len, blob.getLength() - position)];
                    int inputOffset = pageOffset;
                    int outputOffset = 0;

                    for (int i = firstPage; i < (firstPage + numPages)
                            && i < blob.getNumPages(); i++) {
                        Page page = getPage(session, id, i);
                        int outputLength = Math.min(page.getSize()
                                - inputOffset, len - outputOffset);
                        System.arraycopy(page.getData(), inputOffset, data,
                                outputOffset, outputLength);
                        inputOffset = 0;
                        outputOffset += outputLength;
                    }
                    buffer = ByteBuffer.wrap(data);
                }
            } else {
                // XXX Should this be a buffer underrun exception.
                buffer = ByteBuffer.allocate(0);
            }

            session.flush();
            ((HibernateSession) session).getHibernateSession().clear();
            return buffer;
        } catch (StoreException e) {
            String errorMsg = "Failed to get buffer. "
                    + formatArgs(id, position, 0, len) + ". Reason:"
                    + e.getMessage();
            log.error(errorMsg);
            throw e;
        } catch (Throwable t) {
            String errorMsg = "Failed to get buffer. "
                    + formatArgs(id, position, 0, len) + ". Reason:"
                    + t.getMessage();
            log.error(errorMsg);
            throw new StoreException(errorMsg, t);
        } finally {
            //    ((HibernateSession)session).getHibernateSession().clear();
            //  session.close();
            //HibernateUtil.safeCloseSession(log, session);
        }
    }

    /**
     * Deletes the blob and the associated pages.  Uses SQL
     * as we do not want to lead each object before deleting.
     * 
     * @param id The id of the blob to delete.
     * 
     */
    @Tx(TxType.REQUIRED)
    public void delete(Long id) throws StoreException {
        EntityManager session = emInit();
        try {
            String deleteBlob = "delete from Blob WHERE ID = :pms";
            String deletePage = "delete from Page WHERE blobId = :bid";
            session.createQuery(deletePage).setParameter("bid", id)
                    .executeUpdate();

            session.createQuery(deleteBlob).setParameter("pms", id)
                    .executeUpdate();

        } catch (Throwable t) {
            throw new StoreException("Unable to delete blob with id = " + id, t);
        } finally {
        }
    }

    /**
     * Creates a item in the store.
     * 
     * @see org.buni.meldware.mail.store.Store#createStoreItem()
     * 
     */
    @Tx(TxType.REQUIRED)
    public Long doCreate() throws StoreException {
        EntityManager session = emInit();
        log.debug("Creating Store Item");
        try {
            Blob blob = new Blob();
            blob.setPageSize(getPageSize());
            Long id = (Long) ((HibernateSession) session).getHibernateSession()
                    .save(blob);
            session.flush();

            return id;
        } catch (HibernateException e) {
            String errorMsg = "Failed to create store item. " + e.getMessage();
            log.error(errorMsg);
            throw new StoreException(e);
        } finally {

            //    HibernateUtil.safeCloseSession(log, session);
        }
    }

    /**
     * @see org.buni.meldware.mail.store.Store#getInputStream(Long)
     */
    @Tx(TxType.REQUIRED)
    public InputStream getInputStream(Long id, StoreItemMetaData meta)
            throws StoreException {
        return new BlobInputStream(this, meta);
    }

    /**
     * @see org.buni.meldware.mail.store.Store#getOuputStream(Long)
     */
    @Tx(TxType.REQUIRED)
    public OutputStream getOutputStream(Long id, StoreItemMetaData meta)
            throws StoreException {
        return new BlobOutputStream(this, meta);
    }

    /**
     * Simple utility to format the arguments passed in a store method.
     * 
     * @param id
     * @param position
     * @param off
     * @param len
     */
    private static String formatArgs(Object id, long position, int off, int len) {
        StringBuffer sb = new StringBuffer();
        sb.append("Id: ");
        sb.append(id);
        sb.append(" position: ");
        sb.append(position);
        sb.append(" offset ");
        sb.append(off);
        sb.append(" length: ");
        sb.append(len);
        return sb.toString();
    }

    /**
     * Method to display the blobs that have been allocated.
     * 
     * @return
     * @throws HibernateException
     * @throws NamingException
     * 
     */
    @Tx(TxType.REQUIRED)
    public String listBlobs() throws HibernateException, NamingException {
        EntityManager session = emInit();
        try {
            StringBuffer sb = new StringBuffer();

            Query q = (Query) session
                    .createQuery("from org.buni.meldware.mail.store.paged.Blob");
            Iterator i = q.iterate();

            while (i.hasNext()) {
                Blob b = (Blob) i.next();
                sb.append("Id = " + b.getId());
                sb.append(", pages: " + b.getNumPages());
                sb.append(", size: " + b.getLength());
                sb.append("\n");
            }

            ((HibernateSession) session).getHibernateSession().clear();
            return sb.toString();
        } finally {
            // HibernateUtil.safeCloseSession(log, session);
        }
    }
}
