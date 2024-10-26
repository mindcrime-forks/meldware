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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SecondaryTable;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 * Mailboxes and folder contain other folders. Folders may contain messages.
 * 
 * @author Andrew C. Oliver
 * @author Michael Barker
 * @version $Revision: 1.11 $
 */
@Entity
@Inheritance(strategy = javax.persistence.InheritanceType.SINGLE_TABLE)
@SecondaryTable(name="folder_seq")
@NamedQueries({
        @NamedQuery(name = Folder.BY_PATH, 
                query = "SELECT f FROM Folder f WHERE f.mailbox = :mailbox AND f.path = :path")})
public class Folder implements Serializable {

    private static final long serialVersionUID = -1138506404055937256L;
    public final static String FOLDER_SEP = "/";
    public final static String BY_PATH = "FolderByPath";

    /**
     * surrogate key
     */

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    /**
     * Folder name, mainly informational
     */

    @Basic
    private String name;

    /**
     * Messages in the folder
     */
    @OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY, mappedBy = "folder")
    private List<FolderEntry> messages;

    /**
     * parent folder that contains this folder
     */
    @ManyToOne(fetch = FetchType.LAZY)
    private Folder parent;

    /**
     * subfolders
     */
    @OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY, mappedBy = "parent")
    private Collection<Folder> folders;
    
    /**
     * The full path to this folder
     */
    private String path;
    
    /**
     * The root mailbox for this folder.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    private Mailbox mailbox;

    /**
     * folders may have locks assigned to them...mailboxes may also have locks.
     * How those are handled are protocol independent but generally locks expire
     * or are deleted on quit or connection drop
     */
    @OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY, mappedBy = "folder")
    @OnDelete(action=OnDeleteAction.CASCADE)    
    private Collection<Lock> locks;
    
    @Column(table="folder_seq")
    private long expungeVersion = 1;
    
    /**
     * The next unique id for this folder.
     */
    @Column(table="folder_seq")
    private long nextUid = 1;
    

    public Folder() {
        this.folders = new ArrayList<Folder>();
        this.locks = new HashSet<Lock>();
        this.messages = new ArrayList<FolderEntry>();
    }

    /**
     * @return surrogate key
     */
    public long getId() {
        return id;
    }

    /**
     * @return name of the folder
     */
    public String getName() {
        return name;
    }

    /**
     * set the folder name
     * 
     * @param name
     *            of the folder
     */
    public void setName(String name) {
        this.name = name;
        if (parent != null) {
            path = parent.getPath() + FOLDER_SEP + name;
        }
    }

    /**
     * @return parent folder or mailbox that contains this folder
     */
    public Folder getParent() {
        return parent;
    }

    /**
     * @param MessageData
     *            object that contains the pieces of the mail (presently one big
     *            unparsed blob)
     */
    public void addMessage(FolderEntry data) {
        messages.add(data);
    }

    /**
     * @return messages in the folder
     */
    public List<FolderEntry> getMessages() {
        return messages;
    }

    /**
     * 
     */
    public void setMessages(List<FolderEntry> messages) {
        this.messages = messages;
    }

    /**
     * @return folders contained by this folder
     */
    public Collection<Folder> getFolders() {
        return folders;
    }

    /**
     * add a folder to this folder's collection of children
     * 
     * @param folder
     *            contained by this folder
     */
    public void addFolder(Folder folder) {
        this.folders.add(folder);
    }

    /**
     * is this folder also a mailbox?
     * 
     * @return false for folders (overrides mailbox which will return true)
     */
    public boolean isMailbox() {
        return false;
    }

    /**
     * set this folder's parent and add self to that folder's collection of
     * children
     * 
     * @param folder
     *            to set as the parent
     */
    public void setParent(Folder folder) {
        parent = folder;
        if (parent != null) {
            setMailbox(parent.getMailbox());
            path = parent.getPath() + FOLDER_SEP + name;
        } else {
            path = "";
        }
    }
    
    public String getPath() {
        return path;
    }
    
    public Mailbox getMailbox() {
        return mailbox;
    }
    
    

    /**
     * add a lock to this folder (generally for a particular user). Usually a
     * folder has only ONE unexpired lock but this is protocol dependent.
     * 
     * @param lock to add
     */
    public void addLock(Lock lock) {
        this.locks.add(lock);
        lock.setFolder(this);
    }

    public void setLocks(Collection<Lock> locks) {
        this.locks = locks;
    }

    public Collection<Lock> getLocks() {
        return this.locks;
    }

    protected void setFolders(Collection<Folder> folders) {
        this.folders = folders;
    }
    
    protected void setMailbox(Mailbox mailbox) {
        this.mailbox = mailbox;
    }
    
    /**
     * @return the nextUid
     */
    public long getNextUid() {
        return nextUid;
    }
    
    public long getExpungeVersion() {
        return expungeVersion;
    }
    
    public void setExpungeVersion(long version) {
        this.expungeVersion = version;
    }
}
