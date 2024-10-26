package org.buni.meldware.wcapadapter.tag;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.Iterator;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.buni.meldware.calendar.data.CalendarEvent;
import org.buni.meldware.calendar.data.Invite;
import org.buni.meldware.calendar.data.InviteStatus;
import org.buni.meldware.common.util.GMTTimeFormat;
import org.buni.meldware.common.util.StatusUtil;

public class ICalVEvent extends TagSupport {

	private String invite = null;
    private boolean deleted = false;

	private static final long serialVersionUID = "$Id:".hashCode();

	public String getInvite() {
		return invite;
	}

	public void setInvite(String invite) {
		this.invite = invite;
	}
    
    public String getDeleted() {
        return new Boolean(deleted).toString();
    }

    public void setDeleted(String deleted) {
        this.deleted = Boolean.parseBoolean(deleted);
    }

	public ICalVEvent() {
		super();
	}

	@Override
	public int doStartTag() throws JspException {
		Invite inviteObj = (Invite) this.pageContext.findAttribute(invite);
		JspWriter writer = this.pageContext.getOut();

		try {
			writeVEvent(inviteObj, writer);
		} catch (IOException e) {
			throw new JspException(e);
		}
		return SKIP_BODY;
	}

	protected void writeVEvent(Invite inviteObj, JspWriter writer)
			throws IOException {
		DateFormat df = GMTTimeFormat.getFormat();
		writer.println("BEGIN:VEVENT");
		writer.println("UID:" + inviteObj.getEvent().getGUID());
		writer.println("DTSTAMP:" + df.format(new Date()));
        if (!deleted) {
		writer.println("SUMMARY:" + inviteObj.getEvent().getTitle());
        }
		writer.println("DTSTART:"
				+ df.format(inviteObj.getEvent().getStartDate()));
		writer.println("DTEND:" + df.format(inviteObj.getEvent().getEndDate()));
        if (!deleted) {
		writer.println("CREATED:"
				+ df.format(inviteObj.getEvent().getCreateDate()));
        }
		writer.println("LAST-MODIFIED:"
				+ df.format(inviteObj.getEvent().getLastModified()));
        if (!deleted) {
		writer.println("PRIORITY:3");// We should store this too
		writer.println("SEQUENCE:"
				+ inviteObj.getEvent().getVersion().intValue());// 3
		writer.println("CLASS:PUBLIC");
        writer.println("DESCRIPTION:"+(inviteObj.getEvent().getNote() != null ? inviteObj.getEvent().getNote() : ""));
        writer.println("LOCATION:"+(inviteObj.getEvent().getLocation() != null ? inviteObj.getEvent().getLocation():""));
		String organizer = (String) inviteObj.getEvent().getOrganizer().iterator()
				.next();
		// first element should be CN="Full Name"
		writer.println("ORGANIZER;X-NSCP-ORGANIZER-UID="
				+ organizer + ";X-S1CS-EMAIL=none:"
				+ organizer);
		writer.println("STATUS:" + StatusUtil.codeToStatus(inviteObj.getEvent().getStatus()));
		writeAttendees(inviteObj.getEvent(), organizer, writer);
		writer.println("TRANSP:OPAQUE");
		writer
				.println("X-NSCP-LANGUAGE:en\n"
						+ "X-NSCP-TOMBSTONE:0\n"
						+ "X-NSCP-ONGOING:0\n"
						+ "X-NSCP-ORGANIZER-EMAIL:none\n"
						+ "X-NSCP-GSE-COMPONENT-STATE;X-NSCP-GSE-COMMENT=\"PUBLISH-COMPLETED\":65538\n");
        }
        if (deleted) {
            writer.println("X-NSCP-TRIGGERED_BY:"+inviteObj.getEvent().getOrganizer()); //TODO store real modifier
        }
		this.writeResponseStatus(writer);
		writer.println("END:VEVENT");
	}

	protected void writeResponseStatus(JspWriter writer) throws IOException {
		// do nothing used in subclasses
	}

	public int doEndTag() throws JspException {
		return EVAL_PAGE;
	}

	public void release() {
		super.release();
		invite = null;
	}

	private void writeAttendees(CalendarEvent event, String organizer,
			JspWriter out) throws IOException {
		Iterator iterator = event.getInvites().iterator();
		while (iterator.hasNext()) { 
			Invite invite = (Invite) iterator.next();
			if (!organizer.equals(invite.getUserName())) {
				out 
						.print("ATTENDEE;ROLE=REQ-PARTICIPANT;CUTYPE=INDIVIDUAL;PARTSTAT=");
				out.print(printPARSTAT(invite.getStatus()));
				out.println(";CN=" + invite.getUserName()
						+ ";RSVP=TRUE:" + invite.getUserName());
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
