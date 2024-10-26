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
import java.util.Map;

 

public interface AddressBookEntry {
    static final String KEY_FULL_NAME = "cn";
    static final String KEY_DESCRIPTION = "description";
    static final String KEY_EMPLOYEE_NO = "employeeNumber";
    static final String KEY_GIVEN_NAME = "givenName";
    static final String KEY_HOME_PHONE = "homePhone";
    static final String KEY_INITIALS = "initials";
    static final String KEY_LOCATION = "l";
    static final String KEY_MAIL = "mail";
    static final String KEY_MOBILE ="mobile";
    static final String KEY_PAGER = "pager";
    static final String KEY_POSTAL_ADDRESS = "postalAddress";
    static final String KEY_POSTAL_CODE = "postalCode";
    static final String KEY_SURNAME = "sn";
    static final String KEY_STATE_CODE = "st";
    static final String KEY_STREET = "street";
    static final String KEY_TELEPHONE_NUMBER = "telephoneNumber";
    static final String KEY_PASSWORD = "userPassword";
    static final String KEY_USER_ID = "uid";
    
    static final String OBJECT_CLASS = "objectClass";
    static final String OC_PERSON = "person";
    static final String OC_ORG_PERSON = "organizationalperson";
    static final String OC_INET_PERSON = "inetorgperson";
    static final String OC_TOP = "top";
    
    
    //constants for getUpdateTypes these should be the same as OpenDS service but we don't want to create a compile time binding here.
    int DELETE = 2;
    int ADD = 1;
    int UPDATE = 0;
    
    @XMLMapped
    String getFullName();
    @XMLMapped
    String getDescription();
    @XMLMapped
    String getEmployeeNumber();
    @XMLMapped
    String getGivenName(); 
    @XMLMapped
    String getSurname();
    @XMLMapped
    String getHomePhone();
    @XMLMapped
    String getInitials();
    @XMLMapped
    String getLocation();
    @XMLMapped
    List<String> getMail();
    @XMLMapped
    String getMobile();
    @XMLMapped
    String getPager();
    @XMLMapped
    String getPostalAddress();
    @XMLMapped
    String getPostalCode();
    @XMLMapped
    String getStateCode();
    @XMLMapped
    String getStreet();
    @XMLMapped
    String getTelephoneNumber();
    @XMLMapped
    String getUserID();
    
    List<String> getKeys();
    List<String> getValues();
    
    Map<String, Integer> getUpdateTypes();
    List<String> getUpdatedKeys();
    List<String> getUpdatedValues();
    Map<String,List<String>> getEntryMap();
}
