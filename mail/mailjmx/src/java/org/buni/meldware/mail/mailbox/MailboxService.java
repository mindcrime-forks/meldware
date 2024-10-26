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

import java.io.OutputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.buni.meldware.mail.JPAService;
import org.buni.meldware.mail.api.FolderFilter;
import org.buni.meldware.mail.api.FolderMessage;
import org.buni.meldware.mail.api.Hints;
import org.buni.meldware.mail.api.MailboxProxyFactory;
import org.buni.meldware.mail.api.Range;
import org.buni.meldware.mail.api.SearchKey;
import org.buni.meldware.mail.api.Folder.FlagType;
import org.buni.meldware.mail.message.Body;
import org.buni.meldware.mail.message.EnvelopedAddress;
import org.buni.meldware.mail.message.MailBodyManager;
import org.buni.meldware.mail.util.Node;
import org.buni.meldware.mail.util.io.Copier;

/**
 * The MailboxService handles interaction between the protocol instance and the mailbox subsystem. It is responsable for
 * retreiving mails, creating them, deleting them, etc. It also provides a management interface for
 * mailboxes/folders/etc. These must be Standard or XMBeans and not service beans because service beans cannot handle
 * proxy dependencies well,etc. The MBS is assigned a BodyManager in which the actual mail bodies and/or mime parts are
 * stored.
 * 
 * @author acoliver
 * @author Michael Barker
 * @version $Revision: 1.35 $
 */
public interface MailboxService extends MailboxProxyFactory {
    
    public final static String[] DEFAULT_FOLDER  = { "INBOX" };
    
    /**
     * set the mail body manager which handles actually storing the blobs this is done through injection
     * 
     * @param bodymgr
     */
    void setBodyManager(MailBodyManager bodymgr);

    /**
     * @return the proxy to mail body manager which handles actually storing the blobs
     */
    MailBodyManager getBodyManager();

    /**
     * @param id
     * @return mailbox by surrogate id
     */
    Mailbox getMailboxById(long id);

    /**
     * @param alias
     * @return mailbox identified by that alias
     */
    Mailbox getMailboxByAlias(String alias);

    /**
     * @param alias
     * @return the default in folder identified by provided alias
     */
    Folder getDefaultInFolderByAlias(String alias);

    /**
     * @param alias
     * @return the unused default out folder identified by provided alias
     */
    Folder getDefaultOutFolderByAlias(String alias);

    /**
     * create a new mailbox identified by the given alias
     * 
     * @param alias
     * @return Mailbox
     */
    Mailbox createMailbox(String alias);

    /**
     * get mailbox ID by alias
     * 
     * @param alias
     * @return folder id
     */
    long getMailboxIdByAlias(String alias);

    /**
     * create Alias for a mailbox
     * 
     * @param id
     * @param alias
     * @return success
     */
    boolean createAlias(long id, String alias);

    /**
     * create a subfolder for a folder or mailbox
     * 
     * @param folder
     * @param foldername
     * @return folder identified by foldername
     */
    Folder createFolder(Folder folder, String foldername);

    /**
     * Creates a folder given the specified path.  Will throw an exception if
     * the folder already exists.
     * 
     * @param xfolder
     * @param path
     * @return
     */
    Folder createFolder(Folder folder, String[] path);
    
    /**
     * delete the mailbox identified by the surrogate id
     * 
     * @param id
     */
    void deleteMailboxById(long id);

    /**
     * delete the mailbox identified by the provided alias
     * 
     * @param alias
     */
    void deleteMailboxByAlias(String alias);

    List<MessageBody> getMessageBody(MessageData message);    
    
    /**
     * @param message
     * @return mail body for the given message
     */
    List<Body> getMailBody(MessageData message);

    /**
     * add a new mail to the given folder
     * 
     * @param folder
     * @param mail
     */
    FolderEntry createMail(Folder folder, MessageData mail, 
            FolderMessage.SpamState spamState);

    FolderEntry createMail(Folder folder, MessageData mail);
    
    public Map<EnvelopedAddress,String[]> deliver(MessageData md, 
            FolderMessage.SpamState spamState, 
            Map<EnvelopedAddress,String[]> tos);
    /**
     * Get message by id.
     * 
     * @param folder
     * @param uid
     */
    FolderEntry getMessage(Folder folder, long uid);

    /**
     * Get a list of messages where id of the message is within the specified
     * range.
     * 
     * @param ranges
     * @param includeFlags
     * @return
     */
    Collection<FolderEntry> getMessages(Folder folder, Range[] ranges, Hints hints);
    
    /**
     * Get a list of messages where id of the message is within the specified
     * range.
     * 
     * @param ranges
     * @param includeFlags
     * @return
     */
    Collection<FolderEntry> getMessages(Folder folder, Range[] ranges);
    
    /**
     * Gets the sizes of the selected messages
     * 
     * @param f
     * @param ranges
     * @param opts
     * @return
     */
    Collection<Long> getMessageSizes(Folder f, Range[] ranges, Hints hints);
    
    /**
     * Set the sepecifed messages' deleted flag
     * @param f
     * @param ranges
     * @param deleted
     */
    void setDeleted(Folder f, Range[] ranges, boolean deleted);

    /**
     * Set the sepecifed messages' flag
     * @param f
     * @param ranges
     * @param deleted
     */
	void setFlag(Folder folder, Range[] normalised, FlagType flag, boolean isSet);
    
    long getNextUid(long folderId);
    
    /**
     * Gets all of the uids for the messages in the folder.
     * 
     * @return
     */
    long[] getUids(Folder folder);
    
    Folder getSubfolderByName(Folder f, String name);

    List<Folder> getSubfolders(Folder reference, FolderFilter filter);

    /**
     * @param md
     */
    void updateFolderEntry(FolderEntry fe);

    void copy(Folder source, org.buni.meldware.mail.api.Folder target, Range[] ranges);
    
    /**
     * removed "deleted" mails from a folder for real
     * 
     * @param folder
     *            to delete the deleted mails from
     * @param returnMessages
     *            that were deleted or "fastExpunge"
     * @return list of deleted mail seq nums
     */
    List<Long> expunge(Folder folder, boolean returnMessages);
    
    /**
     * Refreshes the folder, including the expungeVersion and returns a list
     * of the messages have been expunged.
     * 
     * @param f
     * @return
     */
    public long[] getExpunged(Folder f, long newVersion);


    /**
     * All of the summary information for a folder.
     * 
     * @param f
     * @return
     */
    FolderInfo getFolderInfo(Folder f);
    
    /**
     * returns a summary of folder information in the manner expected by the WebMailRPC servlet
     * 
     * @param alias
     *            of the mailbox
     * @return List of Object[]
     */
    Node<FolderSummary> folderSummary(String alias);

    /**
     * get a mail given its uid
     * 
     * @param uid
     *            of mail
     * @return MessageData
     */
    MessageData getMail(long uid);

    /**
     * move and rename a folder
     * 
     * @param source
     *            to move
     * @param target
     *            to be its new momma
     * @param name
     *            to rename the folder to
     * @return updated folder
     */
    Folder moveFolder(Folder source, Folder target, String name);

    /**
     * Moves the folder to the specified path.  Throws FolderExistsException
     * if the path points to a folder that alread exists.
     * 
     * @param source
     * @param path
     * @return
     */
    Folder moveFolder(Folder source, String[] path);
    
    /**
     * get a path (string version) for a folder
     * 
     * @param folder
     *            to get the path for
     * @return String representing the path
     */
    String getPathFor(Folder folder);

    /**
     * Delete a folder
     * 
     * @param folder
     *            to delete
     */
    void deleteFolder(Folder folder);

    /**
     * @param box
     * @param apath
     * @return
     */
    Folder getSubfolderByPath(Folder folder, String[] path);

    void setHacks(String databaseName); 

    String getHacks();

	/**
	 * @param part
	 * @param i 
	 * @return
	 */
	MessageBody getMessageBody(MessageData part, int i);
    
    MessageBody getMessageBody(MessageBody part, int i);
    
	void mimePrintMessage(MessageData md, boolean includeHeaders, 
            OutputStream stream, Copier copier);
    
    void mimePrintBody(long id, boolean includeHeaders, OutputStream out, 
            Copier copier);

    List<String> getAliases(String user);

    void deleteAlias(String username, String alias);
    
    public Collection<Long> search(Folder f, SearchKey searchKey);
    
    Mailbox getPostmasterMailbox();

    String getName();
    
	<T> List<T> getMessages(Transformer<MessageData,T> t,
			FolderMessage.SpamState include, FolderMessage.SpamState exclude);
    
    /**
     * Add a listener for the specified folder.  Will receive clustered
     * notifications.
     * 
     * @param folderId
     * @param fl
     */
    void addEventBusListener(long folderId, EventBusListener fl);
    
    /**
     * Removes the listener for the specified folder.
     * 
     * @param folderId
     * @param fl
     */
    void removeEventBusListener(long folderId, EventBusListener fl);
    
    void setEventBus(EventBus eventBus);
        
    void setJPAService(JPAService jpaService);
    
    void save(Mailbox mailbox);
    
    long getDefaultHardSizeLimit();
    void setDefaultHardSizeLimit(long defaultHardSizeLimit);
    
    long getDefaultSoftSizeLimit();
    void setDefaultSoftSizeLimit(long defaultSoftSizeLimit);
    
    boolean getQuotaEnabled();
    void setQuotaEnabled(boolean isQuotaEnabled);
    
}