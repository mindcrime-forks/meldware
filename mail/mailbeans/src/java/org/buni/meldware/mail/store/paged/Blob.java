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
import javax.persistence.Table;

import org.buni.meldware.mail.util.ObjectUtil;

/**
 * 
 * @author Michael Barker <mailto:mike@middlesoft.co.uk>
 * @version $Revision: 1.1 $
 */
@Entity
@Table(name = "PAGED_MAIL_STORE")
public class Blob {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Basic
    private int pageSize;

    @Basic
    private int numPages = 0;

    @Basic
    private long length = 0;

    /**
     * @return Returns the id.
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id The id to set.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the list of pages for this blob.
     * 
     * @return
     */
    public long getLength() {
        return length;
    }

    /**
     * Sets the list of pages for this blob.
     * 
     * @param pages
     */
    public void setLength(long length) {
        this.length = length;
    }

    public void updateLength(Page page) {
        if (page.getPageNo() == (getNumPages() - 1)) {
            long len = ((getNumPages() - 1) * getPageSize()) + page.getSize();
            setLength(len);
        }
    }

    /**
     * @return Returns the pageSize.
     */
    public int getPageSize() {
        return pageSize;
    }

    /**
     * @param pageSize The pageSize to set.
     */
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * @return
     */
    public int getNumPages() {
        return this.numPages;
    }

    /**
     * @param numPages The numPages to set.
     */
    public void setNumPages(int numPages) {
        this.numPages = numPages;
    }

    public void addPage(Page page) {
        int np = getNumPages();
        page.setPageNo(np);
        page.setBlobId(getId());
        setNumPages(np + 1);
    }

    public String toString() {
        return ObjectUtil.toString(this);
    }
}
