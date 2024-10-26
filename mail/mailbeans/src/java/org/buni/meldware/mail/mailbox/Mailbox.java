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

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

/**
 * A users mailbox. It contains a list of folders. Additionally it has a
 * relationship between itself and one of those folders as the default in folder
 * (to which mails are directed by default). There is a relationship to a
 * default out folder but it is presently unused (and is generally the same as
 * teh in folder). Mailboxes have aliases which match a user alias or username.
 * While mailboxes are folders they shouldn't directly contain mails but rather
 * their default infolder should.
 * 
 * @author Andrew C. Oliver
 * @version $Revision: 1.4 $
 */
@Entity
@Inheritance(strategy = javax.persistence.InheritanceType.SINGLE_TABLE)
public class Mailbox extends Folder implements Serializable {

    /**
     * serialization id
     */
    private static final long serialVersionUID = -5752618705978499226L;

    /**
     * default in folder (STMP sticks its mails here)
     */
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Folder defaultInFolder;

    /**
     * default out folder (not presently used)
     */
    @OneToOne(cascade = CascadeType.ALL)
    private Folder defaultOutFolder;

    /**
     * aliases assigned to this folder
     */
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "FOLDER_ID")
    private Collection<Alias> aliases;
    
    @Basic
    private long softSizeLimit;
    
    @Basic
    private long hardSizeLimit;
    
    public Mailbox() {
        this.aliases = new HashSet<Alias>();
        setMailbox(this);
    }

    /**
     * @return collection of aliases assigned to this folder
     */
    public Collection<Alias> getAliases() {
        return aliases;
    }

    public void setAliases(Collection<Alias> aliases) {
        this.aliases = aliases;
    }

    /**
     * add and alias to this folder
     * 
     * @param alias
     */
    public void addAlias(Alias alias) {
        this.aliases.add(alias);
    }

    /**
     * @return default folder in which messages are placed (by smtp)
     */
    public Folder getDefaultInFolder() {
        return defaultInFolder;
    }

    /**
     * set the default folder in which messages are placed (by smtp)
     * 
     * @param defaultFolder
     */
    public void setDefaultInFolder(Folder defaultFolder) {
        this.defaultInFolder = defaultFolder;
    }

    /**
     * @return not presently used "out folder"
     */
    public Folder getDefaultOutFolder() {
        return defaultOutFolder;
    }

    /**
     * set the not presently used "out folder"
     * 
     * @param defaultFolder
     */
    public void setDefaultOutFolder(Folder defaultFolder) {
        this.defaultOutFolder = defaultFolder;
    }

    /**
     * @return true for mailboxes (false for folders that are not mailboxes )
     */
    public boolean isMailbox() {
        return true;
    }
    
    public String getPath() {
        return "";
    }

    /**
     * If a mailbox is in excess of this limit emails will be bounced back
     * to the sender.
     * 
     * @return The hardQuota in bytes.
     */
    public long getHardSizeLimit() {
        return hardSizeLimit;
    }

    public void setHardSizeLimit(long hardSizeLimit) {
        this.hardSizeLimit = hardSizeLimit;
    }

    /**
     * If a mailbox is in excess of this limit then warning will be sent to
     * the user.
     * 
     * @return The softQuota in bytes.
     */
    public long getSoftSizeLimit() {
        return softSizeLimit;
    }

    public void setSoftSizeLimit(long softSizeLimit) {
        this.softSizeLimit = softSizeLimit;
    }
    
}
