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

import org.buni.meldware.common.db.DbUtil;
import org.buni.meldware.integration.opends.OpenDS;
import org.opends.server.types.Entry;

public class AddressBookService implements AddressBook {

    private OpenDS openDS;
    private String dirBase;

    public MutableAddressBookEntry addAddress(AddressBookEntry entry) {
        String userID = entry.getUserID();
        if (entry.getUserID() == null) {
            userID = DbUtil.generateUID();
        }
        List<String> attrs = entry.getKeys();
        String dn = "uid="+userID+","+this.dirBase;
        openDS.addEntry(null, null, dn, attrs, entry.getEntryMap());
        return search(OpenDS.SUBORDINATE_SUBTREE, "(&(objectClass=*)(uid="+userID+"))").get(0);
    }

    public void create() {
        
    }

    public void destroy() {
        
    }

    public OpenDS getOpenDS() {
        return this.openDS;
    }

    public void modifyAddress(AddressBookEntry entry) { 
        String dn = "uid="+entry.getUserID()+","+this.dirBase;
        this.openDS.updateEntry(null, null, dn, entry.getUpdateTypes(), entry.getUpdatedKeys(), entry.getUpdatedValues());
    }

    public List<MutableAddressBookEntry> searchEitherName(String name) {
        String search = "(&(objectClass=*)(|(sn="+name+"*)(givenName="+name+"*)))";
        return search(OpenDS.SUBORDINATE_SUBTREE, search);
    }

    private List<MutableAddressBookEntry> search(int scope, String search) {
        List<Entry> searchResults = (List<Entry>)this.openDS.search(null, null, this.dirBase, OpenDS.SUBORDINATE_SUBTREE, search);
        AddressBookEntryFactory factory = AddressBookEntryFactory.getInstance();
        List<MutableAddressBookEntry> results = factory.dsEntryToAbEntry(searchResults);
        return results;
    }   

    public List<MutableAddressBookEntry> searchEitherNameOrEmail(String name) {
        String search = "(&(objectClass=*)(|(sn="+name+"*)(givenName="+name+"*)(mail="+name+"*)))";
        return search(OpenDS.SUBORDINATE_SUBTREE, search);
    } 

    public List<MutableAddressBookEntry> searchEmail(String email) {
        String search = "(&(objectClass=*)(mail="+email+"*))";
        return search(OpenDS.SUBORDINATE_SUBTREE,search);
    }

    public List<MutableAddressBookEntry> searchGivenName(String name) {
        String search = "(&(objectClass=*)(givenName="+name+"*))";
        return search(OpenDS.SUBORDINATE_SUBTREE, search);
    }

    public List<MutableAddressBookEntry> searchSurname(String sn) {
        String search = "(&(objectClass=*)(sn="+sn+"*))";
        return search(OpenDS.SUBORDINATE_SUBTREE, search);
    }

    public void setOpenDS(OpenDS openDS) {
        this.openDS = openDS; 
    }

    public void start() {
    }

    public void stop() {
    }

    public void deleteAddress(AddressBookEntry entry) {
        String dn = "uid="+entry.getUserID()+","+this.dirBase;
        this.openDS.deleteEntry(null, null, dn);
    }

    public String getDirectoryRoot() {
        return this.dirBase;
    }

    public void setDirectoryRoot(String root) {
        this.dirBase = root;
    }

    public List<MutableAddressBookEntry> searchFullName(String cn) {
        String search = "(&(objectClass=*)(cn="+cn+"*))";
        return search(OpenDS.SUBORDINATE_SUBTREE, search);
    }

}
