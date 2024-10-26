package org.buni.meldware.webmail;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.buni.meldware.address.MutableAddressBookEntry;
import org.buni.meldware.common.preferences.UserProfile;
import org.buni.meldware.common.preferences.UserProfileService;
import org.buni.meldware.mail.abmounts.ABMount;
import org.buni.meldware.mail.abmounts.SystemABMountsService;
import org.buni.meldware.mail.userapi.MailSender;
import org.buni.meldware.webmail.command.BaseCommand;
import org.buni.meldware.webmail.command.GetSystemABMounts;

public class GetSuggestions extends BaseCommand {

    public static final String COMMAND_NAME = "getSuggestions";
    private UserProfileService profileService;
    private SystemABMountsService abMountService;

    public GetSuggestions(MailSender sender, UserProfileService profileService, SystemABMountsService abMountService) {
        super(sender);
        this.profileService = profileService;
        this.abMountService = abMountService;
    }

    @Override
    public String execute(HttpServletRequest request, PrintWriter out) {
        String user = request.getUserPrincipal().getName();
        String typed = request.getParameter("typed");
        UserProfile profile = profileService.findProfile(user);
        Set<String> aliases = profile.getAliases();
        Set<String> domains = GetSystemABMounts.getDomainsFromAliases(aliases);
        Set<ABMount> mounts = abMountService.getABMountsForDomains(domains);
        List<String> results = new ArrayList<String>();
        for (ABMount mount : mounts) {
            List<MutableAddressBookEntry> mabes = mount.getMatchingAddresses(typed, 0, 10);
            for (MutableAddressBookEntry entry : mabes) {
                List<String> emails = entry.getMail();
                for (String email : emails) {
             
                    String fullname = entry.getFullName();
                    String result = fullname+" <"+email+">";
                    results.add(result);
                    if (results.size() > 9) {
                        break;
                    }
                }
                if (results.size() > 9) {
                    break;
                }
            }
            if (results.size() > 9) {
                break;
            }
        }
        for (String result: results) {
            out.println("<suggestion><![CDATA["+result+"]]></suggestion>");
        }
        
        return SUCCESS;
    }


}
