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

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

/**
 * In databases such as MySQL which do not support partial blob loads, we break 
 * the mails into "pages".  This represents a "page" of a mail (baby blob) which 
 * can be chunked together to form the real stream.
 * 
 * @author Michael Barker <mailto:mike@middlesoft.co.uk>
 * @version $Revision: 1.1 $
 */
@Entity
@Table(name = "PAGED_MAIL_STORE_PAGES")
public class Page {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Basic
    private Long blobId;

    @Basic
    private int pageNo;

    @Lob
    private byte[] data;

    public Page() {
        data = new byte[0];
    }

    /**
     * @return
     */
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return Returns the pageNo.
     */
    public int getPageNo() {
        return pageNo;
    }

    /**
     * @param pageNo The pageNo to set.
     */
    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    /**
     * 
     * @return Returns the size.
     */
    public int getSize() {
        return getData().length;
    }

    /**
     * @return Returns the data.
     */
    public byte[] getData() {
        return data;
    }

    /**
     * @param data The data to set.
     */
    public void setData(byte[] data) {
        this.data = data;
    }

    /**
     * @return Returns the blobId.
     */
    public Long getBlobId() {
        return blobId;
    }

    /**
     * @param blobId The blobId to set.
     */
    public void setBlobId(Long blobId) {
        this.blobId = blobId;
    }
}
