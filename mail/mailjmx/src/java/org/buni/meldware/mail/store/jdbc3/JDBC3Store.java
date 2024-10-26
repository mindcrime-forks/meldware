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
package org.buni.meldware.mail.store.jdbc3;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.persistence.Query;

import org.buni.meldware.common.db.DbUtil;
import org.buni.meldware.common.logging.Log;
import org.buni.meldware.mail.store.AbstractStore;
import org.buni.meldware.mail.store.BlobInputStream;
import org.buni.meldware.mail.store.BlobOutputStream;
import org.buni.meldware.mail.store.StoreClerk;
import org.buni.meldware.mail.store.StoreException;
import org.buni.meldware.mail.store.StoreItemMetaData;
import org.buni.meldware.mail.store.StoreItemNotFoundException;
import org.buni.meldware.mail.util.io.WrappedInputStream;
import org.buni.meldware.mail.util.io.WrappedOutputStream;
import org.hibernate.lob.BlobImpl;
import org.jboss.aspects.tx.Tx;
import org.jboss.aspects.tx.TxType;
import org.jboss.ejb3.entity.HibernateSession;

/**
 * A JDBC3 Implemenation of a store that provides a backing
 * for the stream store component.
 * 
 * @author Michael Barker <mailto:mike@middlesoft.co.uk>
 * @version $Revision: 1.6 $
 */
//@Service /*@Management(JDBC3StoreMBean.class) */
public class JDBC3Store extends AbstractStore implements JDBC3StoreMBean {
    //  @PersistenceContext Session session;
    public static final String TRUE = "T";

    public static final String FALSE = "F";

    private static Log log = Log.getLog(JDBC3Store.class);

    private String readStatement = null;

    private String writeStatement = null;

    private String idColumn = null;

    private String blobColumn = null;

    private String tableName = null;

    private boolean useStreams = true;

    private boolean connected = false;

    //=====================
    // Properties
    //=====================   

    /**
     * @return Returns the blobColumn.
     */
    public String getBlobColumn() {
        return blobColumn;
    }

    /**
     * @param blobColumn The blobColumn to set.
     */
    public void setBlobColumn(String blobColumn) {
        this.blobColumn = blobColumn;
    }

    /**
     * @return Returns the idColumn.
     */
    public String getIdColumn() {
        return idColumn;
    }

    /**
     * @param idColumn The idColumn to set.
     */
    public void setIdColumn(String idColumn) {
        this.idColumn = idColumn;
    }

    /**
     * @return Returns the readStatement.
     */
    public String getReadStatement() {
        return readStatement;
    }

    /**
     * @param readStatement The readStatement to set.
     */
    public void setReadStatement(String selectStatement) {
        this.readStatement = selectStatement;
    }

    /**
     * @return Returns the tableName.
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * @param tableName The tableName to set.
     */
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    /**
     * @return Returns the useStreams.
     */
    public boolean getUseStreams() {
        return useStreams;
    }

    /**
     * @param useStreams The useStreams to set.
     */
    public void setUseStreams(boolean useStreams) {
        this.useStreams = useStreams;
    }

    /**
     * @return Returns the disconnected.
     */
    public boolean getConnected() {
        return connected;
    }

    /**
     * @param disconnected The disconnected to set.
     */
    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    /**
     * @return Returns the writeStatement.
     */
    public String getWriteStatement() {
        return writeStatement;
    }

    /**
     * @param writeStatement The writeStatement to set.
     */
    public void setWriteStatement(String writeStatement) {
        this.writeStatement = writeStatement;
    }

    /**
     * Uses string as Id, no marshalling necessary.
     * 
     * @see org.buni.meldware.mail.store.Store#idToString(java.lang.Object)
     */
    public String idToString(Object id) {
        return (String) id;
    }

    /**
     * Uses string as Id, no marshalling necessary.
     * 
     * @see org.buni.meldware.mail.store.Store#stringToId(java.lang.String)
     */
    public Object stringToId(String s) {
        return s;
    }

    /**
     * Get the size of the blob.
     * 
     */
    @Tx(TxType.REQUIRESNEW)
    public long getSize(Long id) throws StoreException {
        PreparedStatement psGet = null;
        ResultSet rs = null;
        long size;

        log.debug("Getting size for oid: " + id);

        try {
            //      session = this.session;
            psGet = ((HibernateSession) emInit()).getHibernateSession()
                    .connection().prepareStatement(getReadStatement());
            psGet.setObject(1, id);
            rs = psGet.executeQuery();
            if (rs.next()) {
                Blob blob = rs.getBlob(blobColumn);
                size = blob.length();
            } else {
                throw new StoreItemNotFoundException("Stream with id: " + id
                        + " does not exist");
            }
        } catch (SQLException e) {
            log.error("Get size failed for id: " + id + ": " + e.getMessage());
            throw new StoreException("Get size failed for id: " + id, e);
        } finally {
            DbUtil.closeQuietly(log, rs);
            DbUtil.closeQuietly(log, psGet);
        }

        return size;
    }

    /**
     * Reads a block from the jdbc3 blob.
     * 
     */
    @Tx(TxType.REQUIRESNEW)
    public int read(Long id, long position, byte[] b, int off, int len)
            throws StoreException {
        //   Session session = null;
        PreparedStatement psGet = null;
        ResultSet rs = null;
        int bytesRead;

        log.debug("Read oid: " + id + ", position: " + position + ", length: "
                + len);

        try {
            // session = this.session;
            psGet = ((HibernateSession) emInit()).getHibernateSession()
                    .connection().prepareStatement(getReadStatement());
            psGet.setObject(1, id);
            rs = psGet.executeQuery();
            if (rs.next()) {
                Blob blob = rs.getBlob(blobColumn);
                long length = blob.length();

                if (length < position) {
                    return 0;
                }

                if (useStreams) {
                    InputStream in = blob.getBinaryStream();
                    // Need to skip ahead a few bytes to find the appropriate 
                    // offset.
                    in.skip(position - StoreClerk.longToInt(getStartIndex()));
                    bytesRead = in.read(b, off, len);
                } else {
                    bytesRead = 0;
                    while (len > bytesRead && position <= length) {
                        int toPut = Math.min(len - bytesRead, getPageSize());
                        byte[] inBytes = blob.getBytes(position, toPut);
                        int numBytes = inBytes.length;
                        System.arraycopy(inBytes, 0, b, off + bytesRead,
                                numBytes);
                        bytesRead += numBytes;
                        position += numBytes;
                    }
                }
            } else {
                throw new StoreItemNotFoundException("Stream with id: " + id
                        + " does not exist");
            }
        } catch (IOException e) {
            throw new StoreException("Read oid: " + id + ", position: "
                    + position + ", length: " + len, e);
        } catch (SQLException e) {
            throw new StoreException("Read oid: " + id + ", position: "
                    + position + ", length: " + len, e);
        } finally {
            DbUtil.closeQuietly(log, rs);
            DbUtil.closeQuietly(log, psGet);
        }

        return bytesRead;
    }

    /**
     * 
     */
    @Tx(TxType.REQUIRESNEW)
    public ByteBuffer getBuffer(Long id, long position, int len)
            throws StoreException {
        //  Session session = null;
        PreparedStatement psGet = null;
        ResultSet rs = null;
        ByteBuffer buffer;

        log.debug("Get Buffer oid: " + id + ", position: " + position
                + ", length: " + len);

        try {
            //    session = this.session;
            psGet = ((HibernateSession) emInit()).getHibernateSession()
                    .connection().prepareStatement(getReadStatement());
            psGet.setObject(1, id);
            rs = psGet.executeQuery();
            if (rs.next()) {
                Blob blob = rs.getBlob(blobColumn);
                long length = blob.length();

                if (len > getPageSize()) {
                    throw new StoreException("Requested length: " + len
                            + " exceeds the maxBlockSize: " + getPageSize());
                }

                if (useStreams) {
                    InputStream in = blob.getBinaryStream();
                    in.skip(position - 1);

                    int remaining = StoreClerk.longToInt(length - position);
                    byte[] b = new byte[Math.min(remaining + 1, len)];
                    int numRead = in.read(b);
                    buffer = ByteBuffer.wrap(b, 0, numRead);
                } else {
                    byte[] b = blob.getBytes(position, len);
                    if (b != null) {
                        buffer = ByteBuffer.wrap(b);
                    } else {
                        buffer = ByteBuffer.allocate(0);
                    }
                }
            } else {
                throw new StoreItemNotFoundException("Stream with id: " + id
                        + " does not exist");
            }
        } catch (IOException e) {
            throw new StoreException("Get Buffer oid: " + id + ", position: "
                    + position + ", length: " + len, e);
        } catch (SQLException e) {
            throw new StoreException("Get Buffer oid: " + id + ", position: "
                    + position + ", length: " + len, e);
        } finally {
            DbUtil.closeQuietly(log, rs);
            DbUtil.closeQuietly(log, psGet);
        }

        return buffer;
    }

    /**
     * @throws StoreException 
     * @see org.buni.meldware.mail.store.Store#write(java.nio.ByteBuffer)
     * 
     */
    @Tx(TxType.REQUIRESNEW)
    public int write(Long id, long position, byte[] b, int off, int len)
            throws StoreException {
        PreparedStatement psGet = null;
        ResultSet rs = null;
        int bytesWritten;

        log.debug("Write oid: " + id + ", position: " + position + ", length: "
                + len);

        try {
            //  session = this.session;
            psGet = ((HibernateSession) emInit()).getHibernateSession()
                    .connection().prepareStatement(getWriteStatement());
            psGet.setObject(1, id);
            rs = psGet.executeQuery();
            if (rs.next()) {
                Blob blob = rs.getBlob(blobColumn);

                if (useStreams) {
                    OutputStream out = blob.setBinaryStream(position);
                    out.write(b, off, len);
                    bytesWritten = len;
                } else {
                    bytesWritten = 0;
                    while (len > bytesWritten) {
                        int toGet = Math.min(len - bytesWritten, getPageSize());
                        blob.setBytes(position, b, off + bytesWritten, toGet);
                        bytesWritten += toGet;
                        position += toGet;
                    }
                }
            } else {
                throw new StoreItemNotFoundException("Stream with id: " + id
                        + " does not exist");
            }
        } catch (IOException e) {
            throw new StoreException("Write oid: " + id + ", position: "
                    + position + ", length: " + len + ", reason: "
                    + e.getMessage(), e);
        } catch (SQLException e) {
            throw new StoreException("Write oid: " + id + ", position: "
                    + position + ", length: " + len + ", reason: "
                    + e.getMessage(), e);
        } finally {
            DbUtil.closeQuietly(log, rs);
            DbUtil.closeQuietly(log, psGet);
        }

        return bytesWritten;
    }

    /**
     * Creates the appropriate input
     * 
     * @see org.buni.meldware.mail.store.Store#getInputStream(Long)
     */
    public InputStream getInputStream(Long id, StoreItemMetaData meta)
            throws StoreException {
        InputStream in;

        if (connected) {
            in = getWrappedInputStream(id);
        } else {
            in = new BlobInputStream(this, meta);
        }

        return in;
    }

    /**
     * Returns an InputStream from the blob wrapped up with the database connection
     * and the associate transaction such that they can be closed/commited respectively
     * when the stream is closed.
     * 
     * @param id
     * @return
     * @throws StoreException
     * 
     */
    @Tx(TxType.REQUIRESNEW)
    public InputStream getWrappedInputStream(Long id) throws StoreException {
        //   Session session = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        WrappedInputStream in;

        try {
            //       session = this.session;
            ps = ((HibernateSession) emInit()).getHibernateSession()
                    .connection().prepareStatement(getReadStatement());
            ps.setObject(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                Blob b = rs.getBlob(getBlobColumn());
                in = new WrappedInputStream(b.getBinaryStream());
            } else {
                throw new StoreItemNotFoundException("Stream with id: " + id
                        + " does not exist");
            }
        } catch (SQLException e) {
            throw new StoreException(
                    "Failed to get wrapped input stream for StoreItem: " + id,
                    e);
        }
        return in;
    }

    /**
     * Returns an OutputStream from the blob wrapped up with the database connection
     * and the associate transaction such that they can be closed/commited respectively
     * when the stream is closed.
     * 
     * @param id
     * @return
     * @throws StoreException
     * 
     */
    @Tx(TxType.REQUIRESNEW)
    public OutputStream getWrappedOutputStream(Long id) throws StoreException {
        //    Session session = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        WrappedOutputStream out;

        try {
            ps = ((HibernateSession) emInit()).getHibernateSession()
                    .connection().prepareStatement(getReadStatement());
            ps.setObject(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                Blob b = rs.getBlob(getBlobColumn());
                out = new WrappedOutputStream(b
                        .setBinaryStream(getStartIndex()));
            } else {
                throw new StoreItemNotFoundException("Stream with id: " + id
                        + " does not exist");
            }
        } catch (SQLException e) {
            throw new StoreException(
                    "Failed to get wrapped output stream for StoreItem: " + id,
                    e);
        }

        return out;
    }

    /**
     * @throws StoreException 
     * @see org.buni.meldware.mail.store.Store#getOuputStream(Long)
     */
    public OutputStream getOutputStream(Long id, StoreItemMetaData meta)
            throws StoreException {
        OutputStream out;

        if (getConnected()) {
            out = getWrappedOutputStream(id);
        } else {
            out = new BlobOutputStream(this, meta);
        }

        return out;
    }

    /**
     * @see org.buni.meldware.mail.store.Store#createStoreItem(java.lang.Object)
     * 
     */
    @Tx(TxType.REQUIRESNEW)
    protected Long doCreate() throws StoreException {
        log.debug("Creating new store item");
        Item item = new Item();
        item.setData(new BlobImpl(new byte[] { 'x' }));
        emInit().persist(item);

        return item.getId();
    }

    /**
     * Deletes the row from the database by id.
     * 
     * @param id The id of the blob.
     * 
     */
    @Tx(TxType.REQUIRESNEW)
    public void delete(Long id) throws StoreException {
    	log.info("Deleting %d", id);
        final String deleteStmt = "delete org.buni.meldware.mail.store.jdbc3.Item where id = :id";
        Query q = emInit().createQuery(deleteStmt);
        q.setParameter("id", id);
        q.executeUpdate();
    }

    public void validate() throws StoreException {
        super.validate();
        testNull("ReadStatement", getReadStatement());
        testNull("WriteStatement", getWriteStatement());
    }

    /**
     * Initalise the JDBC3 Store.  Create the store
     * table if required.
     * 
     */
    public void init() throws StoreException {
        log.info("Initalising JDBC3 Store");
    }
}
