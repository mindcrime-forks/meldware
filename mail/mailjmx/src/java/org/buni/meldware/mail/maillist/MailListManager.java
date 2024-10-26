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
package org.buni.meldware.mail.maillist;

import java.util.List;

import org.buni.meldware.mail.message.MailAddress;

/** 
 * Defines available operations for managing MailingLists
 *
 * @author <a href="kabirkhan@bigfoot.com">Kabir Khan</a>
 * @version $Revision: 1.3 $
 */
public interface MailListManager {

    /** Creates a MailList with the passed in address as its name
     * 
     * @param listAddress Address of list to create
     * @param properties of list we are creating
     * @return false if a list already exists with that name
     */
    boolean createList(MailAddress listAddress, MailListProperties properties);
    boolean createList(String listAddress);

    /** Deletes the MailList with the passed in address as its name
     * 
     * @param listAddress Address of list to delete
     */
    void deleteList(MailAddress listAddress);
    void deleteList(String listAddress);

    /** Finds the MailList with the passed in address as its name
     * 
     * @param listAddress Address of list to find
     * @return The created list. If the list already existed the existing list is returned
     */
    MailList findList(MailAddress listAddress);
    MailList findList(String listAddress);

    boolean addMember(String listAddress, String member);
    boolean removeMember(String listAddress, String member);
    List<String> searchLists(String pattern);
    List<String> searchMembers(String listAddress, String pattern);
    void editList(MailList list);
    
}
