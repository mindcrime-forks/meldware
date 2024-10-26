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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.buni.meldware.common.HashBucketThing;
import org.opends.server.types.Attribute;
import org.opends.server.types.AttributeValue;
import org.opends.server.types.Entry;

public class AddressBookEntryFactory {

    static AddressBookEntryFactory factory = new AddressBookEntryFactory();

    public static AddressBookEntryFactory getInstance() {
        return factory;
    }

    public AddressBookEntry createNewTransientEntry(String fullName, String description, String employeeNumber,
            String givenName, String surName, String homePhone, String initials, String location, List<String> mail,
            String mobile, String pager, String postalAddress, String postalCode, String stateCode, String street,
            String telephoneNumber, String userId) {
        return new AddressBookAddedEntryImpl(fullName, description, employeeNumber, givenName, surName, homePhone,
                initials, location, mail, mobile, pager, postalAddress, postalCode, stateCode, street, telephoneNumber,
                userId);
    }
    
    public MutableAddressBookEntry immutableToMutable(AddressBookEntry abe) {
        return new AddressBookRetrievedEntryImpl(abe);
    }

    public List<MutableAddressBookEntry> dsEntryToAbEntry(List<Entry> searchResults) {
        List<MutableAddressBookEntry> mubs = new ArrayList<MutableAddressBookEntry>(searchResults.size());
        for (Entry entry : searchResults) {
            AddressBookRetrievedEntryImpl abre = new AddressBookRetrievedEntryImpl(entry);
            mubs.add(abre);
        }
        return mubs;
    }

    private static String getFlatAttributeValue(Entry entry, String key) {
        List<Attribute> attrs = entry.getAttribute(key.toLowerCase());
        if (attrs == null || attrs.size() == 0) { 
            return null;
        }
        Attribute attr = attrs.get(0);
        Set<AttributeValue> vals = attr.getValues();
        if (vals == null || vals.size() == 0) {
            return null;
        }
        AttributeValue val = vals.iterator().next();
        String retval = val == null ? null : val.getStringValue();
        return retval;
    }
    
    private static List<String> getSArrayAttributeValue(Entry entry, String key) {
        List<String> retval = new ArrayList<String>();
        List<Attribute> attrs = entry.getAttribute(key.toLowerCase());
        if (attrs == null || attrs.size() == 0) {
            return null;
        }
        Attribute attr = attrs.get(0);
        Set<AttributeValue> vals = attr.getValues();
        if (vals == null || vals.size() == 0) {
            return null;
        }
        for (AttributeValue value : vals) {
            retval.add(value.getStringValue());
        }
        return retval;
    }

    private class AddressBookAddedEntryImpl implements AddressBookEntry {

        HashBucketThing<String, String> fields;

        AddressBookAddedEntryImpl(String fullName, String description, String employeeNumber, String givenName,
                String surName, String homePhone, String initials, String location, List<String> mail, String mobile,
                String pager, String postalAddress, String postalCode, String stateCode, String street,
                String telephoneNumber, String userId) {
            this.fields = new HashBucketThing<String, String>();
            this.fields.put(OBJECT_CLASS, OC_PERSON);
            this.fields.putAnother(OBJECT_CLASS, OC_ORG_PERSON);
            this.fields.putAnother(OBJECT_CLASS, OC_INET_PERSON);
            this.fields.putAnother(OBJECT_CLASS, OC_TOP);
            this.fields.put(KEY_FULL_NAME, fullName);
            this.fields.put(KEY_DESCRIPTION, description);
            this.fields.put(KEY_EMPLOYEE_NO, employeeNumber);
            this.fields.put(KEY_GIVEN_NAME,givenName);
            this.fields.put(KEY_SURNAME,surName);
            this.fields.put(KEY_HOME_PHONE, homePhone);
            this.fields.put(KEY_INITIALS,initials);
            this.fields.put(KEY_LOCATION,location);
            this.fields.putMore(KEY_MAIL,mail);
            this.fields.put(KEY_MOBILE,mobile);
            this.fields.put(KEY_PAGER, pager);
            this.fields.put(KEY_POSTAL_ADDRESS,postalAddress);
            this.fields.put(KEY_POSTAL_CODE, postalCode);
            this.fields.put(KEY_STATE_CODE, stateCode);
            this.fields.put(KEY_STREET, street);
            this.fields.put(KEY_TELEPHONE_NUMBER,telephoneNumber);
            this.fields.put(KEY_USER_ID, userId);
        }

        public String getDescription() {
            return this.fields.get(KEY_DESCRIPTION);
        }

        public String getEmployeeNumber() {
            return this.fields.get(KEY_EMPLOYEE_NO);
        }

        public String getFullName() {
            return this.fields.get(KEY_FULL_NAME);
        }

        public String getGivenName() {
            return this.fields.get(KEY_GIVEN_NAME);
        }

        public String getHomePhone() {
            return this.fields.get(KEY_HOME_PHONE);
        }

        public String getInitials() {
            return this.fields.get(KEY_INITIALS);
        }

        public List<String> getKeys() {
            return this.fields.getKeys();
        }

        public String getLocation() {
            return this.fields.get(KEY_LOCATION);
        }

        public List<String> getMail() {
            return this.fields.getAll(KEY_MAIL);
        }

        public String getMobile() {
            return this.fields.get(KEY_MOBILE);
        }

        public String getPager() {
            return this.fields.get(KEY_PAGER);
        }

        public String getPostalAddress() {
            return this.fields.get(KEY_POSTAL_ADDRESS);
        }

        public String getPostalCode() {
            return this.fields.get(KEY_POSTAL_CODE);
        }

        public String getStateCode() {
            return this.fields.get(KEY_STATE_CODE);
        }

        public String getStreet() {
            return this.fields.get(KEY_STREET);
        }

        public String getSurname() {
            return this.fields.get(KEY_SURNAME);
        }

        public String getTelephoneNumber() {
            return this.fields.get(KEY_TELEPHONE_NUMBER);
        }

        public Map<String, Integer> getUpdateTypes() {
            return this.fields.getChanges();
        }

        public String getUserID() {
            return this.fields.get(KEY_USER_ID);
        }

        public List<String> getValues() { 

            return fields.getValues();
        }
        
        public String toString() {
            String retval = "";
            List<String> keys = this.getKeys();
            List<String> vals = this.getValues();
            
            for (int i =0; i < keys.size(); i++) {
                retval += keys.get(i)+": "+vals.get(i)+"\n";
            }
            return retval;
        }

        public List<String> getUpdatedKeys() {
            return fields.getUpdatedKeys();
        }

        public List<String> getUpdatedValues() {
            return fields.getUpdatedValues();
        }

        public Map<String, List<String>> getEntryMap() {
            return fields.getMap();
        } 
    }

    private class AddressBookRetrievedEntryImpl extends AddressBookAddedEntryImpl implements MutableAddressBookEntry {
        public AddressBookRetrievedEntryImpl(Entry entry) {
            super(getFlatAttributeValue(entry, KEY_FULL_NAME), getFlatAttributeValue(entry, KEY_DESCRIPTION), 
                    getFlatAttributeValue(entry, KEY_EMPLOYEE_NO), getFlatAttributeValue(entry, KEY_GIVEN_NAME),
                    getFlatAttributeValue(entry, KEY_SURNAME), getFlatAttributeValue(entry, KEY_HOME_PHONE), getFlatAttributeValue(entry, KEY_INITIALS),
                    getFlatAttributeValue(entry, KEY_LOCATION), getSArrayAttributeValue(entry, KEY_MAIL),
                    getFlatAttributeValue(entry, KEY_MOBILE), getFlatAttributeValue(entry, KEY_PAGER),
                    getFlatAttributeValue(entry, KEY_POSTAL_ADDRESS), getFlatAttributeValue(entry, KEY_POSTAL_CODE),
                    getFlatAttributeValue(entry, KEY_STATE_CODE), getFlatAttributeValue(entry, KEY_STREET),
                    getFlatAttributeValue(entry, KEY_TELEPHONE_NUMBER),
                    getFlatAttributeValue(entry, KEY_USER_ID));
        }
  
        public AddressBookRetrievedEntryImpl(AddressBookEntry abe) {
            super(abe.getFullName(),abe.getDescription(),
                  abe.getEmployeeNumber(),abe.getGivenName(), abe.getSurname(),
                  abe.getHomePhone(),abe.getInitials(),
                  abe.getLocation(), abe.getMail(),
                  abe.getMobile(), abe.getPager(), 
                  abe.getPostalAddress(), abe.getPostalCode(), 
                  abe.getStateCode(), abe.getStreet(), 
                  abe.getTelephoneNumber(), 
                  abe.getUserID()); 
        }

        public void setDescription(String description) {
            this.fields.put(KEY_DESCRIPTION, description);
        }

        public void setEmployeeNumber(String employeeNumber) {
            this.fields.put(KEY_EMPLOYEE_NO, employeeNumber);
        }

        public void setFullName(String fullName) {            
            this.fields.put(KEY_FULL_NAME, fullName);
        }

        public void setGivenName(String givenName) {
            this.fields.put(KEY_GIVEN_NAME, givenName);
        }

        public void setHomePhone(String homePhone) {
            this.fields.put(KEY_HOME_PHONE,homePhone);
        }

        public void setInitials(String initials) {
            this.fields.put(KEY_INITIALS,initials);
        }

        public void setLocation(String location) {
            this.fields.put(KEY_LOCATION,location);
        }

        public void setMail(List<String> mail) {
            this.fields.put(KEY_MAIL,mail);
        }

        public void setMobile(String mobile) {
            this.fields.put(KEY_MOBILE,mobile);
        }

        public void setPager(String pager) {
            this.fields.put(KEY_PAGER,pager);
        }

        public void setPostalAddress(String postalAddress) {
            this.fields.put(KEY_POSTAL_ADDRESS,postalAddress);
        }

        public void setPostalCode(String postalCode) {
            this.fields.put(KEY_POSTAL_CODE, postalCode);
        }

        public void setStateCode(String stateCode) {
            this.fields.put(KEY_STATE_CODE, stateCode);
        }

        public void setStreet(String street) {
            this.fields.put(KEY_STREET, street);
        }

        public void setSurname(String surname) {
            this.fields.put(KEY_SURNAME, surname);
        }

        public void setTelephoneNumber(String telephoneNumber) {
            this.fields.put(KEY_TELEPHONE_NUMBER,telephoneNumber);
        }

        public void setUserID(String userID) {
            this.fields.put(KEY_USER_ID, userID);
        }

        public Map<String, Integer> getUpdateTypes() {
            return this.fields.getChanges();
        }
        
        public Map<String, List<String>>getValueMap() {
            return this.fields.getMap();
        }

        public void coalesceChanges() {
           this.fields.coalesce();
            
        }

        public void setPassword(String password) {
            this.fields.put(KEY_PASSWORD,password);
        }
    }

}
