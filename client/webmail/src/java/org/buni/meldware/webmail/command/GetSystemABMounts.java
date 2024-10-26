package org.buni.meldware.webmail.command;

import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.buni.meldware.common.StringUtil;
import org.buni.meldware.common.preferences.UserProfile;
import org.buni.meldware.common.preferences.UserProfileService;
import org.buni.meldware.mail.abmounts.ABMount;
import org.buni.meldware.mail.abmounts.SystemABMountsService;
import org.buni.meldware.mail.userapi.MailSender;

public class GetSystemABMounts extends BaseCommand {

    public static final String COMMAND_NAME = "getSystemABMounts";
    private UserProfileService profileService;
    private SystemABMountsService abMountsService;

    public GetSystemABMounts(MailSender sender, UserProfileService profileService, SystemABMountsService abMountService) {
        super(sender);
        this.profileService = profileService;
        this.abMountsService = abMountService;
    } 

    @Override
    public String execute(HttpServletRequest request, PrintWriter out) {
        UserProfile profile = this.profileService.findProfile(request.getUserPrincipal().getName());
        Set<String> aliases = profile.getAliases();
        Set<ABMount> mounts = abMountsService.getABMountsForDomains(getDomainsFromAliases(aliases));
        for (ABMount mount : mounts) {
            out.println(StringUtil.field("systemABMount",
                           StringUtil.field("id", ""+mount.getName().hashCode())+
                           StringUtil.field("name", mount.getName())+
                           StringUtil.field("description", mount.getDescription())+
                           StringUtil.field("type", mount.getType())
                        )
            );
        }
        return SUCCESS;
    }

    public static Set<String> getDomainsFromAliases(Set<String> aliases) {
        Set<String> domains = new HashSet<String>();
        for (String alias : aliases) {
            String[] domain = alias.split("@");
            if (domain.length == 1) {
            } else if (domain.length == 2) {
                domains.add(domain[1]);
            } //anything else and we ignore it.
        }       
        return domains;
    }

}
