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
import java.util.Random;
import java.util.UUID;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.buni.meldware.common.StringUtil;
import org.buni.meldware.mail.util.MMJMXUtil;
import org.buni.meldware.test.JMXTestWrapper;

public class TestAddressBook extends TestCase {

    private static final String MBEAN_ADDRESS_BOOK = "buni.meldware:service=AddressBook";
    private AddressBook abook;
    
    private static String addedFull = null;
    public TestAddressBook(String name){
        super(name);
    }
    
    public static TestSuite suite() {
        return JMXTestWrapper.suite(TestAddressBook.class);
    }    

    protected void setUp() throws Exception {
        if (abook == null) {
            abook = MMJMXUtil.getMBean(MBEAN_ADDRESS_BOOK,AddressBook.class);
        }
    } 
    
    public void testSearchGivenName() {
        String givenName = "Aaccf";
        List<MutableAddressBookEntry> mabes = abook.searchGivenName(givenName);
        assertEquals("mabes.size should be 1 for searchGivenName="+givenName, 1, mabes.size());
        MutableAddressBookEntry mabe = mabes.get(0);
        checkAmar(mabe);
    }
    
    public void testAddEntryAndSearchEmail() {
        UUID uid = UUID.randomUUID();
        String givenName = randomName();
        String surName = randomName(); 
        String fullName = givenName +" " + surName;
        String description = "This is the description for "+fullName+".";
        String employeeNumber = ""+Math.abs(uid.getMostSignificantBits())+""+Math.abs(uid.getLeastSignificantBits());
        String homePhone = "225-216-5900";
        String initials = "ASA";
        String location = "Panama City";
        String userId = "user."+employeeNumber;   
        String mail = userId+"@maildomain.net";
        String mobile = "010-154-3228";
        String pager = "779-041-6341";
        String postalAddress = fullName+"$01251 Chestnut Street$Panama City, DE  50369";
        String postalCode = "50369";
        String stateCode = "DE";
        String street = "01251 Chestnut Street";
        String telephoneNumber = "685-622-6202";    
        List<String> emailAddresses = new ArrayList<String>();
        emailAddresses.add(mail);
        
        AddressBookEntry entry = AddressBookEntryFactory.getInstance().createNewTransientEntry(fullName, description, employeeNumber, givenName, surName, homePhone, initials, location, emailAddresses, mobile, pager, postalAddress, postalCode, stateCode, street, telephoneNumber, userId);
        abook.addAddress(entry);
        List<MutableAddressBookEntry> mabes = abook.searchEmail(mail);
        assertEquals("after add, email search should return 1 entry", 1,mabes.size());
        MutableAddressBookEntry mabe = mabes.get(0);
        assertEquals(fullName+" givenName didn't match",givenName, mabe.getGivenName());
        assertEquals(fullName+" description didn't match",description, mabe.getDescription());
        assertEquals(fullName+" surName didn't match",surName, mabe.getSurname());
        assertEquals(fullName+" employeeNumber didn't match",employeeNumber, mabe.getEmployeeNumber());
        assertEquals(fullName+" fullName didn't match",fullName, mabe.getFullName());
        assertEquals(fullName+" homePhone didn't match",homePhone, mabe.getHomePhone());
        assertEquals(fullName+" initials didn't match",initials, mabe.getInitials());
        assertEquals(fullName+" location didn't match",location, mabe.getLocation());
        assertEquals(fullName+" mail didn't match",mail, mabe.getMail());
        assertEquals(fullName+" mobile didn't match",mobile, mabe.getMobile());
        assertEquals(fullName+" pager didn't match",pager, mabe.getPager());
        assertEquals(fullName+" postalAddress didn't match",postalAddress, mabe.getPostalAddress());
        assertEquals(fullName+" postalCode didn't match",postalCode, mabe.getPostalCode());
        assertEquals(fullName+" state didn't match",stateCode, mabe.getStateCode());
        assertEquals(fullName+" street didn't match",street, mabe.getStreet());
        assertEquals(fullName+" telephoneNumber didn't match",telephoneNumber,mabe.getTelephoneNumber());
        assertEquals(fullName+" userId didn't match",userId, mabe.getUserID());       
        addedFull = fullName;
    }
    
    public void testDeleteAndSearchFull() {
        List<MutableAddressBookEntry> mabes = abook.searchFullName(addedFull);
        assertEquals("searchFullName in testDelete for "+addedFull+" should have 1 entry",1, mabes.size());
        MutableAddressBookEntry mabe = mabes.get(0);
        assertEquals(addedFull+" fullName didn't match",addedFull, mabe.getFullName());
        abook.deleteAddress(mabe);
        mabes = abook.searchFullName(addedFull);
        assertEquals("searchFullName in testDelete after delete for "+addedFull+" should have 0 entries",0, mabes.size());
    }
    
    public void testModifyEntry() {
        String expected = "modified entry";
        testAddEntryAndSearchEmail();
        List<MutableAddressBookEntry> mabes = abook.searchFullName(addedFull);
        assertEquals("searchFullName in testDelete for "+addedFull+" should have 1 entry",1, mabes.size());
        MutableAddressBookEntry mabe = mabes.get(0);
        assertEquals(addedFull+" fullName didn't match",addedFull, mabe.getFullName());
        mabe.setDescription(expected);
        abook.modifyAddress(mabe);
        mabes = abook.searchFullName(addedFull);
        assertEquals("searchFullName in testDelete for "+addedFull+" should have 1 entry",1, mabes.size());
        mabe = mabes.get(0);
        assertEquals(addedFull+" fullName didn't match",addedFull, mabe.getFullName());       
        String desc = mabe.getDescription();
        assertEquals("description after modify should be "+expected,expected, desc);
        mabe.setDescription(null);
        abook.modifyAddress(mabe);
        mabes = abook.searchFullName(addedFull);
        assertEquals("searchFullName in testDelete for "+addedFull+" should have 1 entry",1, mabes.size());
        mabe = mabes.get(0);
        assertEquals(addedFull+" fullName didn't match",addedFull, mabe.getFullName()); 
        desc = mabe.getDescription();
        assertEquals("description after nullify should be null", null, desc);
        testDeleteAndSearchFull();
    }
    
    

    private String randomName() {
        String name = StringUtil.randomAlphabeticalString(8);
        name = (""+name.charAt(0)).toUpperCase()+name.substring(1).toLowerCase();
        return name;
    }

    private void checkAmar(MutableAddressBookEntry mabe) {
        String givenName = "Aaccf";
        String surName = "Amar";
        String description = "This is the description for Aaccf Amar.";
        String employeeNumber = "0";
        String fullName = "Aaccf Amar";
        String homePhone = "225-216-5900";
        String initials = "ASA";
        String location = "Panama City";
        String mail = "user.0@maildomain.net";
        String mobile = "010-154-3228";
        String pager = "779-041-6341";
        String postalAddress = "Aaccf Amar$01251 Chestnut Street$Panama City, DE  50369";
        String postalCode = "50369";
        String state = "DE";
        String street = "01251 Chestnut Street";
        String telephoneNumber = "685-622-6202";
        String userId = "user.0";
        
        assertEquals(fullName+" givenName didn't match",givenName, mabe.getGivenName());
        assertEquals(fullName+" description didn't match",description, mabe.getDescription());
        assertEquals(fullName+" surName didn't match",surName, mabe.getSurname());
        assertEquals(fullName+" employeeNumber didn't match",employeeNumber, mabe.getEmployeeNumber());
        assertEquals(fullName+" fullName didn't match",fullName, mabe.getFullName());
        assertEquals(fullName+" homePhone didn't match",homePhone, mabe.getHomePhone());
        assertEquals(fullName+" initials didn't match",initials, mabe.getInitials());
        assertEquals(fullName+" location didn't match",location, mabe.getLocation());
        assertEquals(fullName+" mail didn't match",mail, mabe.getMail());
        assertEquals(fullName+" mobile didn't match",mobile, mabe.getMobile());
        assertEquals(fullName+" pager didn't match",pager, mabe.getPager());
        assertEquals(fullName+" postalAddress didn't match",postalAddress, mabe.getPostalAddress());
        assertEquals(fullName+" postalCode didn't match",postalCode, mabe.getPostalCode());
        assertEquals(fullName+" state didn't match",state, mabe.getStateCode());
        assertEquals(fullName+" street didn't match",street, mabe.getStreet());
        assertEquals(fullName+" telephoneNumber didn't match",telephoneNumber,mabe.getTelephoneNumber());
        assertEquals(fullName+" userId didn't match",userId, mabe.getUserID());
    }
}
