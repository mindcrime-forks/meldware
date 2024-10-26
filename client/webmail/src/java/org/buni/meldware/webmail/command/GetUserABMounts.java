package org.buni.meldware.webmail.command;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;

import org.buni.meldware.common.preferences.UserProfile;
import org.buni.meldware.common.preferences.UserProfileService;
import org.buni.meldware.mail.userapi.MailSender;

public class GetUserABMounts extends BaseCommand {
    private static final String ABMOUNTS = "ABMOUNTS";
    public static final String COMMAND_NAME = "getUserABMounts";
    private final UserProfileService profileService;

    public GetUserABMounts(MailSender sender, UserProfileService profileService) {
        super(sender);
        this.profileService = profileService;
    }

    @Override
    public String execute(HttpServletRequest request, PrintWriter out) {
        UserProfile prof = this.profileService.findProfile(request.getUserPrincipal().getName());
        String mounts = prof.getPreference(ABMOUNTS);
        mounts = mounts == null ? "" : mounts;
        out.println(mounts);
        return Command.SUCCESS;
    }

    
}
