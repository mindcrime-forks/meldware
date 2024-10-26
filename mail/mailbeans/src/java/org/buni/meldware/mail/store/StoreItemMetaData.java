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

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;

import org.buni.meldware.mail.util.HexDump;

/**
 * MetaData for the item kept in the store.
 * 
 * @hibernate.class table="STORE_ITEM_METADATA"
 * 
 * @author Michael Barker <mailto:mike@middlesoft.co.uk>
 * @version $Revision: 1.1 $
 */
@Entity
public class StoreItemMetaData {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Basic
    private long pid;

    @Basic
    private boolean isCompressed;

    @Basic
    private long itemSize;

    @Basic
    private long startIndex;

    @Basic
    private int pageSize;

    @Lob
    private byte[] hash = null;

    @Basic
    private boolean hashed = false;

    public StoreItemMetaData() {
    }

    public StoreItemMetaData(Long pid, long size, boolean isCompressed,
            long startIndex, int pageSize) {
        this.pid = pid;
        this.itemSize = size;
        this.isCompressed = isCompressed;
        this.startIndex = startIndex;
        this.pageSize = pageSize;
    }

    /**
     * @hibernate.id column = "ID" generator-class = "assigned"
     * 
     * @return Returns the id.
     */
    public Long getId() {
        return id;
    }

    public long getPid() {
        return pid;
    }

    public void setPid(long pid) {
        this.pid = pid;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @hibernate.property column = "IS_COMPRESSED"
     * 
     * @return Returns the compressed.
     */
    public boolean getCompressed() {
        return isCompressed;
    }

    public void setCompressed(boolean isCompressed) {
        this.isCompressed = isCompressed;
    }

    /**
     * @hibernate.property column = "MAIL_SIZE"
     * 
     * @return Returns the size.
     */
    public long getItemSize() {
        return itemSize;
    }

    public void setItemSize(long size) {
        this.itemSize = size;
    }

    /**
     * @hibernate.property column = "START_INDEX"
     * 
     * @return
     */
    public long getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(long startIndex) {
        this.startIndex = startIndex;
    }

    /**
     * @hibernate.property column = "PAGE_SIZE"
     * @return
     */
    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * @hibernate.property column = "HASH" type = "binary"
     * 
     * @return
     */
    public byte[] getHash() {
        return this.hash;
    }

    public void setHash(byte[] hash) {
        this.hash = hash;
    }

    public String getHashString() {
        if (this.hash != null) {
            return new String(HexDump.toHex(getHash()));
        } else {
            return "[]";
        }
    }

    /**
     * @hibernate.property column = "ISHASHED"
     * 
     * @return
     */
    public boolean getHashed() {
        return this.hashed;
    }

    public void setHashed(boolean hashed) {
        this.hashed = hashed;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Id: ").append(getId()).append("Page: ").append(getPid())
                .append(", Size: ").append(getItemSize()).append(", Page Size: ")
                .append(getPageSize()).append(", Compressed: ").append(
                        getCompressed()).append(", Start Index: ").append(
                        getStartIndex());

        if (getHashed()) {
            sb.append(", Hash: ").append(getHashString());
        }

        return sb.toString();
    }

}
