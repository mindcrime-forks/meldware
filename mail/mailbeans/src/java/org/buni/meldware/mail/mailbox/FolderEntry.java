/*
 * Bunisoft the Open Source Communications Company
 * Copyright 2006, Bunisoft Inc.,
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

import static org.buni.meldware.mail.mailbox.FolderEntry.BY_FOLDER;
import static org.buni.meldware.mail.mailbox.FolderEntry.BY_FOLDER_UID;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.buni.meldware.mail.api.FolderMessage;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 * Maintains a link between the folder and the messages.
 * 
 * @author Michael Barker
 *
 */
@Entity
@NamedQueries({ 
    @NamedQuery(name = BY_FOLDER_UID, query = "SELECT fe FROM FolderEntry fe JOIN FETCH fe.message WHERE fe.folder = :folder AND fe.uid = :uid"),
    @NamedQuery(name = BY_FOLDER, query = "SELECT fe FROM FolderEntry fe JOIN FETCH fe.message WHERE fe.folder.id = ?1")
})
public class FolderEntry {
    
    public final static String BY_FOLDER_UID = "FolderEntryByFolderUid";
    public final static String BY_FOLDER = "FolderEntryByFolder";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="folder_id")
    @Index(name="folderentry_fk1")
    public Folder folder;
    
    @Basic
    public long uid;
    
    @ManyToOne(fetch = FetchType.LAZY, cascade = 
    { CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH })
    @JoinColumn(name="messagedata_id")
    public MessageData message;

    /**
     * flag representing whether the message is in a deleted state
     */
    @Basic
    private boolean deleted;

    @Basic
    private boolean seen;

    @Basic
    private boolean recent;

    @Basic
    private boolean answered;

    @Basic
    private boolean flagged;
    
    @Basic
    private boolean draft;
    
    @Basic
    private long expungeVersion;

    @Temporal(TemporalType.TIMESTAMP)
    private Date expungeDate;
    
    /**
     * userSpamState is an indicator to the user that the current mail is
     * spam.
     */
    @Basic
    private FolderMessage.SpamState userSpamState = FolderMessage.SpamState.UNKNOWN;
    
    /**
     * systemSpamState is an internal indicator that the message is spam.  This
     * is normally only set if a user reclassifies an email as spam or not.
     * This field is used for extracting emails for retraining.  We want to
     * avoid using an email for retraining unless it is reclassified.  I.e. we
     * only train the engine if it makes mistakes.
     */
    @Basic
    private FolderMessage.SpamState systemSpamState = FolderMessage.SpamState.UNKNOWN;
    
    @OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.ALL }, mappedBy = "message")
    @Cascade( { org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
    @Fetch(FetchMode.SUBSELECT)
    @OnDelete(action=OnDeleteAction.CASCADE)    
    Set<Flag> flags;
    
    @Transient
    private FlagList flagList;
    
    FolderEntry() {
        deleted = false;
        seen = false;
        recent = true;
        answered = false;
        flagged = false;
        draft = false;
        flags = new HashSet<Flag>();
        flagList = new FlagList(this);
        expungeVersion = Long.MAX_VALUE;
    }
    
    public FolderEntry(Folder folder, long uid, MessageData message, Set<Flag> flags) {
        this();
        this.folder = folder;
        this.uid = uid;
        this.message = message;
        this.flags = new HashSet<Flag>();
        for (Flag flag : flags) {
            this.flags.add(new Flag(flag.getValue()));
        }
        flagList = new FlagList(this);
    }

    public FolderEntry(Folder folder, long uid, FolderEntry fe) {
        this(folder, uid, fe.getMessage(), fe.getFlags());
        deleted = fe.deleted;
        seen = fe.seen;
        recent = fe.recent;
        answered = fe.answered;
        flagged = fe.flagged;
        draft = fe.draft;
    }
    
    /**
     * @return the answered
     */
    public boolean isAnswered() {
        return answered;
    }

    /**
     * @param answered the answered to set
     */
    public void setAnswered(boolean answered) {
        this.answered = answered;
    }

    /**
     * @return the deleted
     */
    public boolean isDeleted() {
        return deleted;
    }

    /**
     * @param deleted the deleted to set
     */
    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    /**
     * @return the draft
     */
    public boolean isDraft() {
        return draft;
    }

    /**
     * @param draft the draft to set
     */
    public void setDraft(boolean draft) {
        this.draft = draft;
    }

    /**
     * @return the flagged
     */
    public boolean isFlagged() {
        return flagged;
    }

    /**
     * @param flagged the flagged to set
     */
    public void setFlagged(boolean flagged) {
        this.flagged = flagged;
    }

    /**
     * @return the recent
     */
    public boolean isRecent() {
        return recent;
    }

    /**
     * @param recent the recent to set
     */
    public void setRecent(boolean recent) {
        this.recent = recent;
    }

    /**
     * @return the seen
     */
    public boolean isSeen() {
        return seen;
    }

    /**
     * @param seen the seen to set
     */
    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    /**
     * @return the uid
     */
    public long getUid() {
        return uid;
    }

    /**
     * @return the folder
     */
    public Folder getFolder() {
        return folder;
    }
    
    /**
     * @return the id
     */
    long getId() {
        return id;
    }
    
    /**
     * @return the systemSpamState
     */
    public FolderMessage.SpamState getSystemSpamState() {
        return systemSpamState;
    }

    /**
     * @param systemSpamState the systemSpamState to set
     */
    public void setSystemSpamState(FolderMessage.SpamState systemSpamState) {
        this.systemSpamState = systemSpamState;
    }

    /**
     * @return the userSpamState
     */
    public FolderMessage.SpamState getUserSpamState() {
        return userSpamState;
    }

    /**
     * @param userSpamState the userSpamState to set
     */
    public void setUserSpamState(FolderMessage.SpamState userSpamState) {
        this.userSpamState = userSpamState;
    }

    /**
     * @return the messageData
     */
    public MessageData getMessage() {
        return message;
    }
    
    public Set<Flag> getFlags() {
        return flags;
    }
    
    public FlagList getFlagList() {
        return flagList;
    }

    public long getExpungeVersion() {
        return expungeVersion;
    }

    public void setExpungeVersion(long expungeVersion) {
        this.expungeVersion = expungeVersion;
    }
    
    public Date getExpungeDate() {
        return expungeDate;
    }
}
