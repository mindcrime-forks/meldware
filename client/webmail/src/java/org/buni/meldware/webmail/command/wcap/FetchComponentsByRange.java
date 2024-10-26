package org.buni.meldware.webmail.command.wcap;

import java.io.PrintWriter;
import java.util.Date;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;

import org.buni.meldware.calendar.data.Invite;
import org.buni.meldware.calendar.data.InviteStatus;
import org.buni.meldware.calendar.interfaces.PIMService;
import org.buni.meldware.calendar.interfaces.PIMServiceUtil;
import org.buni.meldware.common.util.StatusUtil;
import org.buni.meldware.mail.userapi.MailSender;
import org.buni.meldware.webmail.command.BaseCommand;
import org.buni.meldware.webmail.command.Command;

public class FetchComponentsByRange extends BaseCommand {

	public FetchComponentsByRange(MailSender sender) {
		super(sender);
		// TODO Auto-generated constructor stub
	}

	public static final String COMMAND_NAME = "fetchcomponents_by_range";

	@Override
	public String execute(HttpServletRequest request, PrintWriter out) {
		try {
			Date startDate = this.dateFormat.parse(request
					.getParameter("dtstart"));
			Date endDate = this.dateFormat.parse(request.getParameter("dtend"));
			PIMService pim = PIMServiceUtil.getHome().create();
			writeResponse(pim.listInvites(startDate, endDate), out);
			return Command.SUCCESS;
		} catch (Exception e) {
			this.printError("Failed to: " + COMMAND_NAME, e, out);
			return Command.ERROR;
		}
	}

	private void writeResponse(Invite[] invites, PrintWriter out) {
		out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		out.println("<iCalendar>");
		out
				.println("<iCal version=\"2.0\" prodid=\"-//Buni/Meldware Calendar Server//EN\" METHOD=\"PUBLISH\">");
		for (Invite invite : invites) {
			if ((invite.getStatus() == InviteStatus.ACCEPTED)
					|| (invite.getStatus() == InviteStatus.TENATIVE)) {
				String organizer = (String)invite.getEvent().getOrganizer()
						.iterator().next();
				out.println("<EVENT>");
				out.println("<UID>" + invite.getEvent().getGUID() + "</UID>");
				out.println("<SUMMARY>" + invite.getEvent().getTitle()
						+ "</SUMMARY>");
				out.println("<START>"
						+ dateFormat.format(invite.getEvent().getStartDate())
						+ "</START>");
				out.println("<END>"
						+ dateFormat.format(invite.getEvent().getEndDate())
						+ "</END>");
				out.println("<CREATED>"
						+ dateFormat.format(invite.getEvent().getCreateDate())
						+ "</CREATED>");
				out.println("<PRIORITY>0</PRIORITY>");
				out.println("<SEQUENCE>0</SEQUENCE>");
				out.println("<DESC>" + invite.getEvent().getNote() + "</DESC>");
				out.println("<CLASS>PUBLIC</CLASS>");
				out.println("<LOCATION>" + invite.getEvent().getLocation()
						+ "</LOCATION>");
				out.print("<ORGANIZER>" + organizer
						+ "</ORGANIZER>");
				out.println("<STATUS>" + StatusUtil.codeToStatus(invite.getEvent().getStatus())
						+ "</STATUS>");
				writeAttendees(invite, organizer, out);
				out.println("</EVENT>");
			}
		}
		out.println("</iCal>");
		out.println("</iCalendar>");
	}

	private void writeAttendees(Invite myInvite, String organizer, PrintWriter out) {
		Iterator iterator = myInvite.getEvent().getInvites().iterator();
		while (iterator.hasNext()) {
			Invite invite = (Invite) iterator.next();
			if (myInvite != invite) {
				out
						.print("<ATTENDEE ROLE=\"REQ-PARTICIPANT\" CUTYPE=\"INDIVIDUAL\" PARTSTAT=\"");
				out.print(printPARSTAT(invite.getStatus()));
				out.println("\" CN=\"" + invite.getUserName()
						+ "\" RSVP=\"TRUE\">" + invite.getUserName()
						+ "</ATTENDEE>");
			}
		}
	}

	private String printPARSTAT(int statusCode) {
		switch (statusCode) {
		case InviteStatus.ACCEPTED:
			return "ACCEPTED";
		case InviteStatus.DECLINED:
			return "DECLINED";
		case InviteStatus.TENATIVE:
			return "NEEDS-ACTION";
		default:
			return "UNKNONW" + statusCode;
		}
	}

	private String printSTATUS(int statusCode) {
		switch (statusCode) {
		case InviteStatus.ACCEPTED:
			return "CONFIRMED";
		case InviteStatus.TENATIVE:
			return "NEEDS-ACTION";
		default:
			return "UNKNONW" + statusCode;
		}
	}
}
