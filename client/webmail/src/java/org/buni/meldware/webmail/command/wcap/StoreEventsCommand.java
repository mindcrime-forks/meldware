/*
 * Bunisoft the Open Source Communications Company
 * Copyright 2006, Bunisoft Inc., and individual contributors as
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
package org.buni.meldware.webmail.command.wcap;

import java.io.PrintWriter;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.buni.meldware.calendar.data.CalendarEvent;
import org.buni.meldware.calendar.data.Invite;
import org.buni.meldware.calendar.data.InviteStatus;
import org.buni.meldware.calendar.interfaces.PIMService;
import org.buni.meldware.calendar.interfaces.PIMServiceUtil;
import org.buni.meldware.common.preferences.UserProfile;
import org.buni.meldware.common.preferences.UserProfileService;
import org.buni.meldware.common.util.ArrayUtil;
import org.buni.meldware.mail.userapi.MailSender;
import org.buni.meldware.webmail.command.BaseCommand;
import org.buni.meldware.webmail.command.Command;

/**
 * Store Events WCAP Command.
 * 
 * @author Aron Sogor
 * @version $Revision: 1.6 $
 */
public class StoreEventsCommand extends BaseCommand {

	public static final String COMMAND_NAME = "storeevents";

	public int METHOD_PUBLISH = 1;

	public int METHOD_REQUEST = 2;

	public int METHOD_REPLY = 4;

	public int METHOD_CANCEL = 8;

    public UserProfileService profileService;

	/**
	 * @param sender
	 */
	public StoreEventsCommand(MailSender sender) {
		super(sender);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.buni.meldware.mail.webmail.command.BaseCommand#execute(javax.servlet.http.HttpServletRequest,
	 *      java.io.PrintWriter)
	 */
	@SuppressWarnings("unchecked")
    @Override
	public String execute(HttpServletRequest request, PrintWriter out) {
		try {
			int method = Integer.parseInt(request.getParameter("method"));
			// update or create
			if ((method == METHOD_PUBLISH) || (method == METHOD_REQUEST)) {
				CalendarEvent event = constructEvent(request);
				String user = request.getParameter("orgUID");
				String modType = request.getParameter("mod");
				PIMService pim = PIMServiceUtil.getHome().create();
				// if modType and uid specified this is an update.
				if ((event.getGUID() != null) && (modType != null)) {
					event = pim.updateEvent(event);
					String[] inviteNames = parseInvites(request
							.getParameter("attendees"), request
							.getUserPrincipal().getName());
                    
                    if (inviteNames != null) {
                        Set<String> newInvites = new HashSet<String>();
                        inviteNames = attendeesToUsers(inviteNames);
                        newInvites.addAll(Arrays.asList(inviteNames));                        
                        
                        Set<Invite> existingInvites = (Set<Invite>)event.getInvites();
                        
                        for (Invite invite : existingInvites) {
                            if (newInvites.contains(invite.getUserName())) {
                                newInvites.remove(invite.getUserName());
                            }
                        }
                        
                        inviteNames = newInvites.toArray(new String[]{});
                    }
                    
					if (inviteNames != null)
						pim.addInvitesToEvent(event.getRecordId(), inviteNames);
					this.writeResponse(event, out);
                    
                    inviteNames = parseInvites(request
                            .getParameter("attendees"), request
                            .getUserPrincipal().getName());
                    if (inviteNames != null) {
                        Set<String> newInvites = new HashSet<String>();
                        inviteNames = attendeesToUsers(inviteNames);
                        newInvites.addAll(Arrays.asList(inviteNames));
                        Set<Invite> existingInvites = (Set<Invite>)event.getInvites();
                        for (Invite invite : existingInvites) {
                            if(!newInvites.contains(invite.getUserName())) {
                                invite.setStatus(InviteStatus.CANCELED);
                                pim.updateInvite(invite);
                            }
                        }
                       
                    }
				}
				// this is a create
				else {
                    String[] inviteNames = parseInvites(request
                            .getParameter("attendees"), request
                            .getUserPrincipal().getName());
                    inviteNames = attendeesToUsers(inviteNames);
					Invite[] invites = pim.scheduleEvent(inviteNames, event);
					this.writeResponse(invites[0].getEvent(), out);
				}
				return Command.SUCCESS;

			} else
			// RSVP
			if (method == METHOD_REPLY) {
				PIMService pim = PIMServiceUtil.getHome().create();
				String guids[] = new String[] { request.getParameter("uid") };
				Invite[] invites = pim.listInvites(guids);
				if (invites.length == 0) {
					this.printError("Event unknown: " + guids[0], null, out);
					return Command.ERROR;
				}
                invites[0].setUserName(this.attendeesToUsers(new String[]{invites[0].getUserName()})[0]);
				invites[0].setStatus(parseStatusRequested(request));
				pim.updateInvite(invites[0]);
				return Command.SUCCESS;
			} else
			// Cancel
			if (method == METHOD_CANCEL) {
				return Command.SUCCESS;
			}
			this.printError("Unknown request " + COMMAND_NAME, null, out);
			return Command.ERROR;
		} catch (Exception e) {
			this.printError("Failed to: " + COMMAND_NAME, e, out);
			return Command.ERROR;
		}
	}

	private CalendarEvent constructEvent(HttpServletRequest request)
			throws ParseException {
		CalendarEvent event = new CalendarEvent();
		event.setStartDate(this.dateFormat.parse(request
				.getParameter("dtstart")));
		event.setEndDate(this.dateFormat.parse(request.getParameter("dtend")));
		event.setLocation(request.getParameter("location"));
		event.setNote(request.getParameter("desc"));
		event.setTitle(request.getParameter("summary"));
		event.setGUID(request.getParameter("uid"));
        event.setStatus(Short.parseShort(request.getParameter("status")));
		return event;
	}

	private String[] parseInvites(String attendesString, String organizer) {
		if (attendesString == null)
			return null;

		String[] inviteString = attendesString.split(";");
		Vector<String> result = new Vector<String>();
		for (String invite : inviteString) {
			invite = invite.split("\\^")[2];
			if (!invite.equals(organizer))
				result.add(invite);
		}
		return (String[]) result.toArray(new String[0]);
	}

	private int parseStatusRequested(HttpServletRequest request)
			throws Exception {
		String[][] attendees = splitAttendeesString(request
				.getParameter("attendees"));
		// search for my attendee
		String currentUser = request.getUserPrincipal().getName();
        Set<String> aliases = profileService.findProfile(currentUser).getAliases();
		for (String[] invite : attendees) {
            String pt2 = invite[2];
			if (pt2.equals(currentUser) || aliases.contains(pt2))
			{
				String status = invite[0];
				if(status.toUpperCase().equals("PARTSTAT=DECLINED"))
				{
					return InviteStatus.DECLINED;
				}
				if(status.toUpperCase().equals("PARTSTAT=ACCEPTED"))
				{
					return InviteStatus.ACCEPTED;
				}
				if(status.toUpperCase().equals("PARTSTAT=NEEDS-ACTION"))
				{
					return InviteStatus.TENATIVE;
				}
				if(status.toUpperCase().equals("PARTSTAT=CANCELED"))
				{
					return InviteStatus.CANCELED;
				}
			}
		}
		throw new Exception("User is not invited for event");
	}
    
    private String[] attendeesToUsers(String[] userNames) {
        Set<String> result = new HashSet<String>();
        if (userNames != null) {
            for (String username : userNames) {
                UserProfile profile = this.profileService.findProfile(username);
                String user = profile == null ? username : profile.getUsername(); //if this is a local user then grab it
                                                                                  //otherwise keep it literally
                result.add(user);
            }
        }
        return result.toArray(new String[result.size()]);
    }


	private String[][] splitAttendeesString(String attendesString) {
		String[] inviteString = attendesString.split(";");
		String[][] result = new String[inviteString.length][3];
		for (int i = 0; i < inviteString.length; i++) {
			result[i] = inviteString[i].split("\\^");
		}
		return result;
	}

	private void writeResponse(CalendarEvent event, PrintWriter out) {
		out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		out.println("<iCalendar>");
		out
				.println("<iCal version=\"2.0\" prodid=\"-//Buni/Meldware Calendar Server//EN\" METHOD=\"PUBLISH\">");
		out.println("<EVENT>");
		out.println("<RSTATUS>2.0;Success.  Store successful.</RSTATUS>");
		out.println("<UID>" + event.getGUID() + "</UID>");
		out.println("</EVENT>");
		out.println("</iCal>");
		out.println("</iCalendar>");
	}
}
