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

public interface MutableAddressBookEntry extends AddressBookEntry {
    void setFullName(String val);
    void setDescription(String val);
    void setEmployeeNumber(String val);
    void setGivenName(String val); 
    void setSurname(String val);
    void setHomePhone(String val);
    void setInitials(String val);
    void setLocation(String val);
    void setMail(List<String> val);
    void setMobile(String val);
    void setPager(String val);
    void setPostalAddress(String val); 
    void setPostalCode(String val);
    void setStateCode(String val);
    void setStreet(String val);
    void setTelephoneNumber(String val);
    void setUserID(String val);
    
    void coalesceChanges();
    void setPassword(String password);
}
