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
package org.buni.meldware.mail.userapi;

import java.io.InputStream;
import java.util.List;

import org.buni.meldware.mail.MailListenerChain;
import org.buni.meldware.mail.mailbox.Folder;
import org.buni.meldware.mail.mailbox.FolderSummary;
import org.buni.meldware.mail.mailbox.MailboxService;
import org.buni.meldware.mail.message.MailBodyManager;
import org.buni.meldware.mail.util.Node;

/**
 * This is the beginning of our USER API for MM. It needs a name change. However I will do that LATER.
 * 
 * @author Andrew C. Oliver
 */
public interface MailSender {
    void setListenerChain(MailListenerChain chain);

    MailListenerChain getListenerChain();

    void setBodyManager(MailBodyManager mgr);

    MailBodyManager getBodyManager();

    /**
     * attribute containing mailbox manager
     * @param MailboxService for this instance of the api
     */
    void setMailboxManager(MailboxService mbm);

    /**
     * attribute containing mailbox manager
     * @return MailboxService for this instance of the api
     */
    MailboxService getMailboxManager();

    /**
     * Get List of object arrays representing a summary of the mails in the folders in a mailbox.
     * INAME = name of folder, IID = numeric id of folder (unique), IPATH = path of folder, IMESSAGES = num messages in folder,
     * IUNREAD = number of unread messages.
     * 
     * @param allias of mailbox (userid)
     * @return List containing Object[INAME,IID,IPID,IPATH,IMESSAGES,IUNREAD]
     */
    Node<FolderSummary> folderSummary(String alias);

    /**
     * send a mail and potentially save it in the user's sent folder
     * @param alias of user to put in sent folder of
     * @param from header
     * @param to header
     * @param cc header
     * @param bcc header
     * @param subject header
     * @param body of mail minus the headers
     * @return error code (presently always 0)
     */
    int send(String alias, String from, String[] to, String[] cc, String[] bcc, String subject, String body);

    /**
     * @param uid
     *            of mail to get
     * @param firstbyte
     *            of mail to get or -1 for all
     * @param lastbyte
     *            of mail to get or -1 for all
     * @return String containing the mail part asked for
     */
    String getMailBody(long uid, long firstbyte, long lastbyte);

    /**
     * get the length of a mail
     * 
     * @param uid
     *            of the mail
     * @return length in bytes
     */
    long getMailLength(long uid);

    /**
     * create a folder
     * 
     * @param alias
     *            of Mailbox
     * @param path
     *            of folder
     * @return Folder that was created
     */
    Folder createFolder(String alias, String path);

    /**
     * delete a folder by path
     * 
     * @param alias
     * @param foldername
     *            fully qualified folder path
     * @return Folder that has been deleted (disconnected state)
     */
    void deleteFolder(String alias, String foldername);

    /**
     * move a folder by path
     * 
     * @param alias
     *            of the mailbox (user id usually)
     * @param foldername
     *            fully qualified path of folder to move
     * @param targetname
     *            fully qualified path (including new name) to move it to
     * @return Folder object representing the change
     */
    Folder moveFolder(String alias, String foldername, String targetname);

    /**
     * @param alias
     *            of the user mailbox we want more aliases for
     * @return List of Strings representing that user's aliases
     */
    List<String> getAliases(String alias);

    /**
     * if the mailbox exists then return true, if not then create it and return false
     * @param alias
     * @return whether the mailbox originally existed
     */
    boolean provision(String alias);

    InputStream getMailAttachment(long id, String file);
}