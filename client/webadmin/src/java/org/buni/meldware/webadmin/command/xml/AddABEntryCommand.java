package org.buni.meldware.webadmin.command.xml;

import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.buni.meldware.address.MutableAddressBookEntry;
import org.buni.meldware.mail.abmounts.ABMount;
import org.buni.meldware.mail.abmounts.SystemABMountsService;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class AddABEntryCommand extends AbstractXMLCommand implements XMLCommand {
    SystemABMountsService sabs;
    public String execute(HttpServletRequest request, Document doc, PrintWriter out) {
        boolean update = false;
        Element operation = this.getOperation(doc);
        String mountName = this.getString(operation, "mountName");
        Element abentry = this.getElement(operation, "addressBookEntry");
        String sn = this.getString(abentry, "sn");  
        String givenName = this.getString(abentry,"givenName");
        String cn = this.getString(abentry, "cn");
        String postalAddress = this.getString(abentry, "postalAddress");
        String mobile = this.getString(abentry, "mobile");
        String telephoneNumber = this.getString(abentry, "telephoneNumber");
        String pager = this.getString(abentry, "pager");
        String uid = this.getString(abentry,"username");
  //      String password = this.getString(abentry,"password"); 
        List<String> mail = this.getStringList(abentry, "mail");
        System.out.println("sn="+sn);
        ABMount mount = sabs.getMount(mountName);  
        MutableAddressBookEntry entry = null;
        List<MutableAddressBookEntry> addresses = mount.getMatchingAddresses(mail.get(0), 1, 10);
        for (MutableAddressBookEntry mab : addresses) { //TODO: this rematch is a bit hairy should probably be a search on userid
            if (mab.getUserID().equals(uid)) { 
                update = true;
                entry = mab;
              //  entry.setUserID(uid);
                entry.setGivenName(givenName);
                entry.setSurname(sn);
                entry.setMail(mail);
            } 
        }
        if (update == false) {
            entry = mount.createAddress(uid, givenName, sn, cn, mail);
            entry.coalesceChanges();
        }
        entry.setPostalAddress(postalAddress.replaceAll("\n", "$"));
        entry.setPager(pager);
        entry.setTelephoneNumber(telephoneNumber);
        entry.setMobile(mobile);
    //    entry.setPassword(password);
        mount.updateAddress(entry);
        
        return SUCCESS;
    }
    
    public void setSystemABMounts(SystemABMountsService sabs) {
        this.sabs = sabs;
    }

}
