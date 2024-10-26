package org.buni.meldware.webmail.command;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;

import org.buni.meldware.common.StringUtil;
import org.buni.meldware.common.preferences.UserProfile;
import org.buni.meldware.common.preferences.UserProfileService;
import org.buni.meldware.mail.userapi.MailSender;

public class SaveUserABMounts extends BaseCommand {
    private static final String ABMOUNTS = "ABMOUNTS";
    public static final String COMMAND_NAME = "saveUserABMounts";
    private final UserProfileService profileService;

    public SaveUserABMounts(MailSender sender, UserProfileService profileService) {
        super(sender);
        this.profileService = profileService;
    }

    @Override
    public String execute(HttpServletRequest request, PrintWriter out) {
        UserProfile prof = profileService.findProfile(request.getUserPrincipal().getName());
        String[] sorders = request.getParameterValues("order");
        String[] sids = request.getParameterValues("id");
        String[] snames = request.getParameterValues("name");
        String[] sdescriptions = request.getParameterValues("description");
        String[] slocals = request.getParameterValues("local");
        String[] senableds = request.getParameterValues("enabled");
        String xml = makeXML(sorders,sids,snames,sdescriptions,slocals,senableds);
        prof.putPreference(ABMOUNTS, xml);
        profileService.updateUserProfile(prof);
        return Command.SUCCESS;
    }

    private String makeXML(String[] sorders, String[] sids, String[] snames, String[] sdescriptions, String[] slocals, String[] senableds) {
        String retval = "";
        for (int i = 0; i < sorders.length; i++) { 
            retval += makeXML(sorders[i],sids[i],snames[i],sdescriptions[i],slocals[i], senableds[i]);
        }
        return retval;
    }

    private String makeXML(String order, String sid, String name, String description, String slocal, String senabled) {
        String retval = StringUtil.field("userABMount",
                StringUtil.field("order",order)+
                StringUtil.field("id", ""+Integer.parseInt(sid))+//validation
                StringUtil.field("name", name)+
                StringUtil.field("description", description)+
                StringUtil.field("local", ""+Boolean.parseBoolean(slocal))+
                StringUtil.field("enabled", ""+Boolean.parseBoolean(senabled)));//validation
        return retval;
    }
}
