/*
 * Bunisoft the Open Source Communications Company
 * Copyright 2007, Bunisoft LLC., and individual contributors as
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
package org.buni.meldware.address;

import java.util.List;

import org.buni.meldware.integration.opends.OpenDS;

public interface AddressBook {
    void start();
    void stop();
    void create();
    void destroy();
    
    void setOpenDS(OpenDS openDS);
    OpenDS getOpenDS();
    
    List<MutableAddressBookEntry> searchFullName(String fullname);
    List<MutableAddressBookEntry> searchEitherNameOrEmail(String name);
    List<MutableAddressBookEntry> searchEitherName(String name);
    List<MutableAddressBookEntry> searchEmail(String email);
    List<MutableAddressBookEntry> searchGivenName(String name);
    List<MutableAddressBookEntry> searchSurname(String sn);
    MutableAddressBookEntry addAddress(AddressBookEntry entry);
    void modifyAddress(AddressBookEntry entry);
    void deleteAddress(AddressBookEntry entry);
    
    void setDirectoryRoot(String root);
    String getDirectoryRoot();
}
