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

import org.buni.meldware.mail.message.MailAddress;

/** Defines the available operations that can be made to an individual mailing list
 *
 * @author <a href="kabirkhan@bigfoot.com">Kabir Khan</a>
 * @version $Revision: 1.1 $
 */
public interface MailList {

    /** Gets the address of the list 
     * @return The address if the list
     */
    MailAddress getListAddress();

    /** Gets the members of the list 
     * @return a list of the members of the list
     */
    MailAddress[] getMembers();

    /** Adds an address to the list
     * @param address Address to be added 
     */
    void addMember(MailAddress address);

    /** Checks if an address exists in the list
     * @param address Address to be added
     * @return true if is in list
     */
    boolean isMember(MailAddress address);

    /** Removes an address from the list 
     * @param address Address to be removed
     */
    void removeMember(MailAddress address);

    /** Gets the properties of the list - a clone should be returned
     * @return a clone of the MailListProperties for the list
     */
    MailListProperties getProperties();

    /** Sets the properties of the list
     * @param properties properties to be set
     */
    void setProperties(MailListProperties properties);
}
