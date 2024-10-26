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
package org.buni.meldware.mail.mailbox;

/**
 * Contains summary information for a folder.
 * 
 * @author Michael Barker
 *
 */
public class FolderSummary {

    private long id;
    private String name;
    private long parentId;
    private long unread = 0;
    private long total = 0;
    /**
     * @return the id
     */
    public long getId() {
        return id;
    }
    /**
     * @param id the id to set
     */
    public void setId(long id) {
        this.id = id;
    }
    /**
     * @return the name
     */
    public String getName() {
        return name;
    }
    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }
    /**
     * @return the parentId
     */
    public long getParentId() {
        return parentId;
    }
    /**
     * @param parentId the parentId to set
     */
    public void setParentId(long parentId) {
        this.parentId = parentId;
    }
    /**
     * @return the total
     */
    public long getTotal() {
        return total;
    }
    /**
     * @param total the total to set
     */
    public void setTotal(long total) {
        this.total = total;
    }
    /**
     * @return the unread
     */
    public long getUnread() {
        return unread;
    }
    /**
     * @param unread the unread to set
     */
    public void setUnread(long unread) {
        this.unread = unread;
    }
    
    public String toString() {
        return String.format("%d, %s, %d, %d, %d", id, name, parentId, total, unread);
    }
}
