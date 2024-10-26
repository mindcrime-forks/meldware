package org.buni.meldware.mail.abmounts;

import java.util.List;

import org.buni.meldware.address.MutableAddressBookEntry;
public interface ABMount {
	public final static int SEARCH_NAME_OR_MAIL = 0;
	public static final int SEARCH_MAIL = 1;
	public static final int SEARCH_NAME = 2;
	
	public String getName();
    public String getDescription();
    public String getType();
    public List<MutableAddressBookEntry> getMatchingAddresses(String typed, int pattern, int number);
    public MutableAddressBookEntry createAddress(String uid, String givenName, String sn, String cn, List<String> mail);
    public void updateAddress(MutableAddressBookEntry entry);
}
