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
package org.buni.meldware.mail.mailbox;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Lock for a folder.  For POP this is assigned at the mailbox and means that no other pop user can get at the 
 * folder.  MM does not stop SMTP from delivering to the folder however.
 *
 * @author Andrew C. Oliver
 * @version $Revision: 1.1 $
 */
@Entity
@Table(name = "LOCK_TABLE")
public class Lock {
    /**
     * surrogate key 
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    /**
     * timestamp at which this lock is considered expired.  Previous to that time the lock is valid.
     * TODO - reap these
     */
    @Temporal(TemporalType.TIMESTAMP)
    private Date expiration;

    /**
     * user whom has locked the folder
     */
    @Basic
    private String username;

    /**
     * thread id associated with the lock (not really used yet, eventually we can check validity for users 
     * logged into this server as to whether the user is actually still here...if not we can aggressively release)
     */
    @Basic
    private String threadId;

    /**
     * we can force expire a lock (such as quit or logout) but generally we just delete them. (unused ATM)
     */
    @Basic
    private boolean expired;

    /**
     * Folder or mailbox that this lock is assigned to.
     */
    @ManyToOne
    private Folder folder;

    /**
     * @return timestamp designating when this folder expires
     */
    public Date getExpiration() {
        return expiration;
    }

    /**
     * set the expiration timestamp of the folder
     * @param expiration
     */
    public void setExpiration(Date expiration) {
        this.expiration = expiration;
    }

    /**
     * @return thread in which this folder was locked
     */
    public String getThreadId() {
        return threadId;
    }

    /**
     * set the thread for which we should lock this folder
     * @param threadId
     */
    public void setThreadId(String threadId) {
        this.threadId = threadId;
    }

    /**
     * @return user for which this folder was locked
     */
    public String getUsername() {
        return username;
    }

    /**
     * set the user who locked the folder
     * @param username 
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return surrogate key for the folder
     */
    public long getId() {
        return id;
    }

    /**
     * @return was the lock FORCE expired (must also check the date)
     */
    public boolean isExpired() {
        return expired;
    }

    /**
     * set force expiration of this lock
     * @param expired
     */
    public void setExpired(boolean expired) {
        this.expired = expired;
    }

    /**
     * @return folder to which the lock applies
     */
    public Folder getFolder() {
        return folder;
    }

    /**
     * set the folder to lock
     * @param folder
     */
    public void setFolder(Folder folder) {
        this.folder = folder;
        //    folder.addLock(this);
    }

}
