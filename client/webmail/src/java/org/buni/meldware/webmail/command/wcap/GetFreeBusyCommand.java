package org.buni.meldware.webmail.command.wcap;

import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.buni.meldware.Version;
import org.buni.meldware.calendar.data.Invite;
import org.buni.meldware.calendar.interfaces.PIMServiceLocal;
import org.buni.meldware.calendar.interfaces.PIMServiceUtil;
import org.buni.meldware.common.preferences.UserProfile;
import org.buni.meldware.common.preferences.UserProfileService;
import org.buni.meldware.common.util.GMTTimeFormat;
import org.buni.meldware.mail.userapi.MailSender;
import org.buni.meldware.webmail.UTCDateFormat;
import org.buni.meldware.webmail.command.BaseCommand;
import org.buni.meldware.webmail.command.Command;

public class GetFreeBusyCommand extends BaseCommand {
    
    public static final String COMMAND_NAME = "get_freebusy.wcap";
    private UserProfileService profileService;
    UTCDateFormat dformat = new UTCDateFormat();

    public GetFreeBusyCommand(MailSender sender, UserProfileService profileService) {
        super(sender);
        this.profileService = profileService;
    }

    @Override
    public String execute(HttpServletRequest request, PrintWriter out) {
        try {
            DateFormat format = GMTTimeFormat.getFormat();
          //  FetchComponentsByRangeForm rangeForm = (FetchComponentsByRangeForm) form;
            String username = request.getParameter("calid");
         //   DateFormat format = GMTTimeFormat.getFormat();
            Date startDate = format.parse(request.getParameter("dtstart"));
            Date endDate = format.parse(request.getParameter("dtend"));
            PIMServiceLocal pim = PIMServiceUtil.getLocalHome().create();
            Invite[] invites;
            username = username != null && username.startsWith("mailto:") && username.length() > 7 ? username.substring(7) : username;
            username = attendeeToUser(username);
            invites = pim.listInvites(startDate, endDate, username);
            request.setAttribute("invites", invites);

        //WCAPSession session = this.getSession(request);
        //request.setAttribute("startDate", rangeForm.getDtstart());
        //request.setAttribute("endDate", rangeForm.getDtend());
        //request.setAttribute("calinfo",new CalInfo(session.getUserId(),session.getUserName(),session.getUserId(),null,null));
        //return this.findForward(mapping, "success", request);
        String result = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
               "<iCalendar>\n"+
               "<iCal version=\"2.0\" prodid=\"-//"+Version.CAL_SERVER_PRODID+ "//EN\" METHOD=\"PUBLISH\">\n"+
               "<X-NSCP-CALPROPS-LAST-MODIFIED>20061102T170639Z</X-NSCP-CALPROPS-LAST-MODIFIED>\n"+
               "<X-NSCP-CALPROPS-CREATED>20060814T110002Z</X-NSCP-CALPROPS-CREATED>\n"+
               "<X-NSCP-CALPROPS-READ>999</X-NSCP-CALPROPS-READ>\n"+
               "<X-NSCP-CALPROPS-WRITE>999</X-NSCP-CALPROPS-WRITE>\n"+
               "<X-NSCP-CALPROPS-RELATIVE-CALID>"+username+"</X-NSCP-CALPROPS-RELATIVE-CALID>\n"+
               "<X-NSCP-CALPROPS-NAME>"+username+"</X-NSCP-CALPROPS-NAME>\n"+
               "<X-NSCP-CALPROPS-PRIMARY-OWNER>"+username+"</X-NSCP-CALPROPS-PRIMARY-OWNER>\n"+
               "<X-NSCP-CALPROPS-ACCESS-CONTROL-ENTRY>@@o^c^WDEIC^g</X-NSCP-CALPROPS-ACCESS-CONTROL-ENTRY>\n"+
               "<X-NSCP-CALPROPS-ACCESS-CONTROL-ENTRY>@@o^a^RSF^g</X-NSCP-CALPROPS-ACCESS-CONTROL-ENTRY>\n"+
               "<X-NSCP-CALPROPS-ACCESS-CONTROL-ENTRY>@^a^rsf^g</X-NSCP-CALPROPS-ACCESS-CONTROL-ENTRY>\n"+
               "<X-NSCP-CALPROPS-ACCESS-CONTROL-ENTRY>@^c^^g</X-NSCP-CALPROPS-ACCESS-CONTROL-ENTRY>\n"+
               "<X-NSCP-CALPROPS-ACCESS-CONTROL-ENTRY>@^p^r^g</X-NSCP-CALPROPS-ACCESS-CONTROL-ENTRY>\n"+
               "<X-NSCP-CALPROPS-RESOURCE>0</X-NSCP-CALPROPS-RESOURCE>\n"+
               "<X-S1CS-CALPROPS-ALLOW-DOUBLEBOOKING>1</X-S1CS-CALPROPS-ALLOW-DOUBLEBOOKING>\n"+
               "<FREEBUSY>\n"+
               "<START>"+request.getParameter("dtstart") +"</START>\n"+
               "<END>"+request.getParameter("dtend") +"</END>\n";
        for (Invite invite : invites) {
            result += "<FB FBTYPE=\"BUSY\">"+dformat.format(invite.getEvent().getStartDate())+"/"+dformat.format(invite.getEvent().getEndDate())+"</FB>";
        }
 //              "<logic:present name="invites"><logic:iterate id="invite" name="invites"><FB FBTYPE="BUSY"><wcap:UTCDate name="invite" property="event.startDate"/>/<wcap:UTCDate name="invite" property="event.endDate"/></FB></logic:iterate></logic:present>\n"+
        result += "</FREEBUSY>\n"+
                  "<X-NSCP-WCAP-ERRNO>0</X-NSCP-WCAP-ERRNO>\n"+
                  "</iCal>\n"+
                  "</iCalendar>";
        
        out.print(result);
        return Command.SUCCESS;

        } catch (Exception e) {
            //  return this.findError(mapping, Error.CALENDAR_DOES_NOT_EXIST, request);
              throw new RuntimeException(e);
          }
    }

private String attendeeToUser(String userName) {
    UserProfile profile = this.profileService.findProfile(userName);
    String user = profile == null ? userName : profile.getUsername(); //if this is a local user then grab it
                                                                          //otherwise keep it literally
    String result = user == null ? userName : user;
    return result;
}

    
}
  