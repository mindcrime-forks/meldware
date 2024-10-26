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

import java.util.Collection;

public interface Mailbox {

    /**
     * Gets a child folder by name.
     * 
     * @param name
     * @return
     */
    public Folder getFolder(String name);
    
    /**
     * Gets a descendent folder by path.
     * 
     * @param path
     * @return
     * @throws FolderNotExistsException
     */
    public Folder getFolder(String[] path);
    
    /**
     * Create a folder with the specified path.  Will create any necessary
     * parent folders.  Will throw an exception if the folder already exists.
     * 
     * @param path
     * @return
     * @throws FolderExistsException
     */
    public Folder createFolder(String[] path);
    
    /**
     * Delete a folder by path, will throw an exception if the folder already
     * exists.
     * 
     * @param path
     */
    public void deleteFolder(String[] path);
    
    /**
     * Returns the default folder for this mailbox.
     * 
     * @return
     */
    public Folder getDefault();
    
    /**
     * Get all of the aliases for this folder.
     * 
     * @return
     */
    public Collection<String> getAliases();
    
    public Folder getRootFolder();
    
}
