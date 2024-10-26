package org.buni.meldware.webmail.command.xml;

import java.io.PrintWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.buni.meldware.address.AddressBookEntry;
import org.buni.meldware.address.MutableAddressBookEntry;
import org.buni.meldware.address.XMLizer;
import org.buni.meldware.common.preferences.UserProfile;
import org.buni.meldware.common.preferences.UserProfileService;
import org.buni.meldware.mail.abmounts.ABMount;
import org.buni.meldware.mail.abmounts.SystemABMountsService;
import org.buni.meldware.webmail.command.GetSystemABMounts;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class SearchAddressBookCommand extends AbstractXMLCommand implements
		XMLCommand {

	private UserProfileService ups;
	private SystemABMountsService sabms;

	public String execute(HttpServletRequest request, Document doc,
			PrintWriter out) {
		Element operation = this.getOperation(doc);
		String name = this.getString(operation, "name");
		String email = this.getString(operation, "email");
		
		searchAB(out, request.getUserPrincipal().getName(), name, email);
		return XMLCommand.SUCCESS;
	}	
	
	private void searchAB(PrintWriter out, String user, String name, String email) {
        email = email.equals("") ? null : email;
        name = name.equals("") ? null : name;
        int match = email == null && name != null ? 2 : -1;
        match = email != null && name == null && match == -1 ? 1 : match;
        match = email != null && name != null && match == -1 ? 0 : match;
        if (match == -1) {
        	 throw new RuntimeException("invalid search criteria (null)");
        }
        UserProfile profile = ups.findProfile(user);
        Set<String> aliases = profile.getAliases();
        Set<String> domains = GetSystemABMounts.getDomainsFromAliases(aliases);
        Set<ABMount> mounts = this.sabms.getABMountsForDomains(domains);
        Set<AddressBookEntry> results = new HashSet<AddressBookEntry>();
        for (ABMount mount : mounts) {
        	List<MutableAddressBookEntry>mabes = null;
        	switch(match) {
        	case 0:
        		mabes = mount.getMatchingAddresses(name, ABMount.SEARCH_NAME, 20);
        		mabes.addAll(mount.getMatchingAddresses(email, ABMount.SEARCH_MAIL, 20));
        		break;
        	case 1:
        		mabes = mount.getMatchingAddresses(email, ABMount.SEARCH_MAIL, 20);
        		break;
        	case 2:
        		mabes = mount.getMatchingAddresses(name, ABMount.SEARCH_NAME, 20);
        		break;
        	}
            for (MutableAddressBookEntry entry : mabes) {
            		results.add(entry);
            }
            if (results.size() > 20) {
                break;
            }
        }
        	XMLizer.toXML("address", results, out);

	}
	
	public void setUserProfile(UserProfileService profileService) {
		this.ups = profileService;
	}
	
	public void setSystemABMounts(SystemABMountsService mountsService) {
		this.sabms = mountsService;
	}

}
