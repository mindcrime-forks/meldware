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
package org.buni.meldware.webmail.command;

import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.buni.meldware.calendar.data.Address;
import org.buni.meldware.calendar.data.CalendarEvent;
import org.buni.meldware.calendar.data.Invite;
import org.buni.meldware.calendar.data.InviteStatus;
import org.buni.meldware.mail.userapi.MailSender;
import org.buni.meldware.webmail.UTCDateFormat;

/**
 * Command di tutti Commandi
 * 
 * @author Aron Sogor
 * @version $Revision: 1.8 $
 */
public abstract class BaseCommand implements Command {

	Log log = LogFactory.getLog(this.getClass());
    protected MailSender sender;
    protected UTCDateFormat dateFormat = new UTCDateFormat();
    
    public BaseCommand(MailSender sender) {
        this.sender = sender;
    }

	public abstract String execute(HttpServletRequest request, PrintWriter out);

	protected void printError(String msg, Throwable e, PrintWriter out) {
		this.log.error(msg, e);
		out.print("<error>");
		out.print("<msg>" + msg + "</msg>");
		out.print("<exception>");
		out.print("<type>" + e.getClass().getName() + "</type>");
		out.print("<msg>" + e.getMessage() + "</msg>");
		out.print("<strace>");
		e.printStackTrace(out);
		out.print("</strace>");
		out.print("</exception>");
		out.print("</error>");
	}

	/**
	 * @param out
	 * @param addresses
	 * @param acnt
	 */
	protected void writeContact(PrintWriter out, String address,
			boolean isOwnAddress) {
	/*	long recordId = address.getRecordId();
		String fname = stripNull(address.getFullName());
		String email = stripNull(address.getEmailAddress());
		String organ = stripNull(address.getOrganization());
		String sname = stripNull(address.getNickName());
		String hphon = stripNull(address.getHomePhone());
		String mphon = stripNull(address.getMobilePhone());
		String ophon = stripNull(address.getOfficePhone());
		String faxno = stripNull(address.getFax());
		String userName = "";

		if (address.isIsUserAddress()) {
			userName = address.getEmailAddress();
		}*/
/*
		out.print("<contact>" + "<id>" + recordId + "</id>" + "<userName>" 
				+ userName + "</userName>" + "<contactName>"
				+ fname + "</contactName>" + "<email>" + email + "</email>"
				+ "<organization>" + organ + "</organization>" + "<shortName>"
				+ sname + "</shortName>" + "<homePhone>" + hphon
				+ "</homePhone>" + "<mobilePhone>" + mphon + "</mobilePhone>"
				+ "<officePhone>" + ophon + "</officePhone>" + "<faxNumber>"
				+ faxno + "</faxNumber>" + "<isUserAddress>"
				+ address.isIsUserAddress() + "</isUserAddress>"
				+ "<isOwnContact>" + isOwnAddress + "</isOwnContact>"
				+ "</contact>");
                */
	}

	/**
	 * @param request
	 */
	protected CalendarEvent deserializeEvent(HttpServletRequest request) {
		CalendarEvent event = new CalendarEvent();
		// fill out the event
		event.setStartDate(new Date(Long.parseLong(request
				.getParameter("startDate"))));
		event.setEndDate(new Date(Long.parseLong(request
				.getParameter("endDate"))));
		event.setTitle(request.getParameter("title"));
		event.setLocation(request.getParameter("location"));
		event.setNote(request.getParameter("note"));
		String eventId = request.getParameter("eventId");
		if(eventId!=null) event.setRecordId(Long.parseLong(eventId));
		return event;
	}

	protected void writeEvents(HttpServletRequest request, PrintWriter out, Invite[] invites) {
		for (int i = 0; i < invites.length; i++) {
		    if (invites[i].getStatus() != InviteStatus.CANCELED) {
		        out.println("<calevent>");
		        out.println("<eventid>"
		                + invites[i].getEvent().getRecordId()
		                + "</eventid>");
		        out.println("<title>" + invites[i].getEvent().getTitle()
		                + "</title>");
		        out.println("<location>"
		                + invites[i].getEvent().getLocation()
		                + "</location>");
		        out.println("<note>" + invites[i].getEvent().getNote()
		                + "</note>");
		        out.println("<startDate>"
		                + invites[i].getEvent().getStartDate().getTime()
		                + "</startDate>");
		        out.println("<endDate>"
		                + invites[i].getEvent().getEndDate().getTime()
		                + "</endDate>");
                out.println("<status>"
                        + invites[i].getEvent().getStatus()
                        + "</status>");
		        Iterator invitesItr = invites[i].getEvent().getInvites()
		                .iterator();
		        while (invitesItr.hasNext()) {
		            Invite invite = (Invite) invitesItr.next();
		            out.println("<invite>");
		            out.println("<inviteId>" + invite.getInviteId()
		                    + "</inviteId>");
		            out.println("<status>" + invite.getStatus()
		                    + "</status>");
		            out.println("<version>" + invite.getVersion()
		                    + "</version>");
		            out.println("<userName>"
		                    + invite.getUserName()
		                    + "</userName>");
		            out.println("<isMine>"
		                    + (invite.getUserName()
		                            .equals(request.getUserPrincipal()
		                                    .getName())) + "</isMine>");
		            out.println("</invite>");
		        }
		        out.println("<editable>" + isEditable(invites[i].getEvent().getOrganizer(),request.getUserPrincipal().getName()) + "</editable>");
		        out.println("</calevent>");
		    }
		}
	}
	
    public boolean isEditable(Set organizers,String userName)
    {
        Iterator owners = organizers.iterator();
        while (owners.hasNext()) {
            String owner = (String) owners.next();
            if (owner.equals(userName)) return true;
        }
        return false;
    }
    
	protected String stripNull(String value) {
		return value != null ? value : "";
	}
}
