/*
 * Bunisoft the Open Source Communications Company
 * Copyright 2007, Bunisoft LLC, and individual contributors as
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

import java.io.OutputStream;
import java.util.Date;
import java.util.List;

import org.buni.meldware.mail.message.MailHeaders;
import org.buni.meldware.mail.util.io.Copier;


/**
 * Represents a message in a folder.
 * 
 * @author Michael Barker <mbarker@buni.org>
 * @version $Revision: 1.13 $
 *
 */
public interface FolderMessage extends Message, FolderEntity {
    
    public enum SpamState {
        SPAM,
        NOT_SPAM,
        UNKNOWN
    };

    /**
     * The identifier for the message unique to a folder.
     * 
     * @return
     */
    long getUid();
    
    /**
     * The identifier for the folder the message is contained in.
     * 
     * @return
     */
    long getFolderId();
    
    /**
     * @return whether the message was answered
     */
    public boolean isAnswered();

    /**
     * @return whether the message was deleted
     */
    public boolean isDeleted();

    /**
     * @return
     */
    public boolean isFlagged();

    /**
     * @return whether the message is recent
     */
    boolean isRecent();

    /**
     * @return whether the message was seen
     */
    boolean isSeen();
    
    /**
     * Writes the message to the supplied output stream.
     * 
     * @param out Stream to write to.
     * @param copier The copier to use (can handle dot stuffing etc...)
     */
    public void write(OutputStream out, Copier copier);
    
    /**
     * Sets the flags for a message.
     * 
     * @param isReplace If true replace the current flags with the specified
     * values, otherwise add them.
     * @param flags The flags to set.
     * @return The current set of flags.
     */
    public List<String> setFlags(boolean isReplace, List<String> flags);
    
    /**
     * Remove the specified flags from the message.
     * 
     * @param flags The flags to be removed.
     * @return The new set of flags.
     */
    public List<String> removeFlags(List<String> flags);
    
    /**
     * Gets a formatted string of the flags.
     * 
     * @return
     */
    public String getFlagString();
        
    /**
     * Gets the value for a header.  If this header has multiple values, will
     * only return the first value.
     * 
     * @param name
     * @return
     */
    public String getHeader(String name);
    
    /**
     * Returns an array of the values for the specified header.  Will return an
     * array of 0 length if no headers are available.
     * 
     * @param name
     * @return
     */
    public String[] getHeaders(String name);
    
    /**
     * Gets the bodies for the this message.
     */
    public List<FolderBody> getBody();

    /**
     * Get the message timestamp.
     * 
     * @return Message timestamp
     */
    Date getTimestamp();

    /**
     * Check if the specified flag is set.
     * 
     * @param flag
     * @return
     */
    boolean isSet(String flag);
        
    /**
     * Returns the size of the headers.
     * @return
     */
    long getHeaderSize();
    
    
    /**
     * Get the body specified by the address.  Should be of non-zero length,
     * otherwise an IllegalArgumentException is thrown.  It is zero indexed.
     * If a message is not found null is returned.
     * 
     * @param address
     * @return
     */
    FolderBody getBodyPart(int[] address);
    
    MailHeaders getMailHeaders();

    // TODO Make this a MailAddress object.
    String getFrom();
    
    /**
     * Get messagedata id
     * 
     * @return messagedata id
     */
    long getId();
    
    int getSeqNum();
}
