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
package org.buni.meldware.mail.api;

import java.io.InputStream;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public interface Folder extends Iterable<Long> {

    public enum FlagType {
    	DELETED("deleted"),
    	ANSWERED("answered"),
    	SEEN("seen"),
    	DRAFT("draft"),
    	RECENT("recent"),
    	FLAGGED("flagged");
    	
    	private final String name;
    	private FlagType(String name) {
    		this.name = name;
    	}
    	
    	public String toString() {
    		return name;
    	}
    }
    
    long getId();
    
    String getName();
    
    String[] getPath();
    
    /**
     * Returns all of the messages in this folder.
     * 
     * @return
     */
    List<FolderMessage> getMessages();
    
    /**
     * Gets an individiual message by id.
     * 
     * @param isUid
     * @param id
     * @return
     */
    FolderMessage getMessage(boolean isUid, long id);
    
    /**
     * Returns all of the message in the specified range of uids.
     * 
     * @param range
     * @return
     */
    List<FolderMessage> getMessages(boolean isUid, Range[] ranges);
    
    /**
     * Gets a list of FolderMessages, but will not load the actual message.
     * Calls to get any of the message information will fail.  Only get uid and
     * sequence numbers and flags from the FolderMessage after calling this.
     * 
     * TODO: Consider returns a different interface.
     * 
     * @param isUid
     * @param ranges
     * @return
     */
    List<FolderMessage> getFlags(boolean isUid, Range[] ranges);
    
    /**
     * Set the deleted flag on the specified messages.
     * 
     * @param isUid
     * @param ranges
     * @param deleted
     */
    void setDeleted(boolean isUid, Range[] ranges, boolean deleted);
    
    /**
     * Set the specified flag on the specified messages.
     * 
     * @param isUid
     * @param ranges
     * @param deleted
     */
    void setFlag(boolean isUid, Range[] ranges, FlagType flag, boolean isSet);
    
    /**
     * Copies the specified messages to the named folder.
     * 
     * @param isUid
     * @param ranges
     */
    void copy(Folder folder, boolean isUid, Range[] ranges);
        
    /**
     * Renames a folder to a specific path.  Throws an exception if the
     * new folder name already exists.
     * 
     * @param path
     * @throws FolderExistsException
     */
    void rename(String[] path);

    long getMessageCount();

    long getRecentCount();
    
    long getUnseenCount();
    
    long getFirstUnseen();

    long getLikelyUID();
    
    long getMaxUid();
    
    List<String[]> getSubFolders(FolderFilter filter);
    
    /**
     * Create a message with a fixed size.
     * 
     * @param in
     * @param size
     */
    void append(InputStream in, int size, List<String> flags, 
            Date timestamp);
    
    void append(String from, String[] to, String[] cc, String[] bcc, 
            String subject, String body);
    
    void append(org.buni.meldware.mail.message.Message message);
    
    /**
     * Expunges all of the deleted messages from the mailbox.
     */
    List<Long> expunge(boolean returnUids);

    Collection<Long> search(SearchKey searchKey, boolean isUid);

    Collection<Long> getMessageSizes(boolean isUid, Range[] ranges);

    void close();

    long getUid(int i);
}
