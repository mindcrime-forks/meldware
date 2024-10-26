package org.buni.meldware.wcapadapter.action;

import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.buni.meldware.calendar.data.CalendarEvent;
import org.buni.meldware.calendar.data.Invite;
import org.buni.meldware.calendar.interfaces.PIMService;
import org.buni.meldware.calendar.interfaces.PIMServiceUtil;
import org.buni.meldware.calendar.session.exception.DuplicateInviteException;
import org.buni.meldware.calendar.session.exception.InvalidCalendarEventException;
import org.buni.meldware.calendar.session.exception.RecordNotFoundException;
import org.buni.meldware.calendar.session.exception.UserNotAuthorizedException;
import org.buni.meldware.calendar.session.exception.UserUnknownException;
import org.buni.meldware.common.preferences.UserProfile;
import org.buni.meldware.common.util.GMTTimeFormat;
import org.buni.meldware.common.util.StatusUtil;
import org.buni.meldware.wcapadapter.bean.Attendee;
import org.buni.meldware.wcapadapter.bean.CalInfo;
import org.buni.meldware.wcapadapter.bean.Status;
import org.buni.meldware.wcapadapter.form.StoreEventsForm;
import org.buni.meldware.wcapadapter.session.WCAPSession;

public class StoreEvents extends WCAPSessionAction {

    /**
     * note that the WCAP doco writers are cracked.  STORE_TYPE_NONE is really "create or update"
     * or at least that is what the JESCS evolution plugin peeps think
     */
	public final static int WCAP_STORE_TYPE_NONE = 0;

    /**
     * create or throw an error if it exists
     */
	public final static int WCAP_STORE_TYPE_CREATE = 1;

    /**
     * modify or throw an error if it doesn't exist
     */
	public final static int WCAP_STORE_TYPE_MODIFY = 2;

    private String userProfileService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping,
	 *      org.apache.struts.action.ActionForm,
	 *      javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
        loadServices(mapping);
		StoreEventsForm eventForm = (StoreEventsForm) form;
		PIMService pim = PIMServiceUtil.getHome().create();
        if (eventForm.getStoretype() == null) {
            eventForm.setStoretype(""+WCAP_STORE_TYPE_NONE);
        }
		int storeType = Integer.parseInt(eventForm.getStoretype());
		switch (storeType) {
		case WCAP_STORE_TYPE_CREATE:
			return createEvent(request, eventForm, pim, mapping);
		case WCAP_STORE_TYPE_MODIFY:
			return changeEvent(request, eventForm, pim, mapping);
		case WCAP_STORE_TYPE_NONE:
			return createOrUpdateEvent(request, eventForm, pim, mapping);
		}
		return this.findForward(mapping, "failed", request);
	}

    private ActionForward createOrUpdateEvent(HttpServletRequest request, StoreEventsForm eventForm, PIMService pim, ActionMapping mapping) throws UserUnknownException, RemoteException, InvalidCalendarEventException, DuplicateInviteException, ParseException, RecordNotFoundException, UserNotAuthorizedException {
        CalendarEvent event =  pim.loadEventByGUID(eventForm.getUid());
        if (event == null) {
            return createEvent(request, eventForm, pim, mapping);
        } else {
            return changeEvent(request, eventForm, pim, mapping);
        }
    }

    private ActionForward changeEvent(HttpServletRequest request,
			StoreEventsForm eventForm, PIMService pim, ActionMapping mapping)
			throws DuplicateInviteException, UserUnknownException,
			RemoteException, ParseException, RecordNotFoundException,
			InvalidCalendarEventException, UserNotAuthorizedException {
		Map attendees = null;
		CalendarEvent event =  pim.loadEventByGUID(eventForm.getUid());
		WCAPSession session = this.getSession(request);
		request.setAttribute("calinfo", new CalInfo(session.getUserId(),
				session.getUserName(), session.getUserId(), null, null));
		switch (eventForm.getMethod()) {
		// this means update the event
		case StoreEventsForm.METHOD_PUBLISH:
			return updateEvent(request, eventForm, pim, event, session, mapping);
		case StoreEventsForm.METHOD_REPLY:
			attendees = parseAttendees(eventForm.getAttendees());
            //TODO ACO make this use ups to resolve
			Attendee attendee = (Attendee)attendees.get(session.getUserId());
			if(attendee != null)
			{
				Invite invite = pim.listInvites(new String[]{eventForm.getUid()})[0];
				invite.setStatus(attendee.getStatusCodeParsed());
				pim.updateInvite(invite);
				Status status = new Status(invite, Status.SUCCESS, Status.OPERTATION_STORE);
				request.setAttribute("status", status);
				return this.findForward(mapping, "success_reply", request);
			}
			return this.findForward(mapping, "failed", request);
			// this means invites might changed too.
			// but same than update
		case StoreEventsForm.METHOD_REQUEST:
			attendees = parseAttendees(eventForm.getAttendees());
			String[] userNames = extractNewAttendees(event, attendees);
            userNames = attendeesToUsers(userNames);
			if (userNames.length > 0) { 
				pim.addInvitesToEvent(event.getRecordId().longValue(),
						userNames);
			}
			return updateEvent(request, eventForm, pim, event, session, mapping);
		// This is not really an update method
		// Other servers allow to overwrite some values of the original invite in a personal copy
		// we do not do that yet, but to make the thing work we fake a response with unchanged values
		case StoreEventsForm.METHOD_UPDATE:
			return createUpdateResponse(request, event, session, mapping);
		default:
			break;
		}
		return null;

	}

	private ActionForward updateEvent(HttpServletRequest request,
			StoreEventsForm eventForm, PIMService pim, CalendarEvent event,
			WCAPSession session, ActionMapping mapping) throws ParseException,
			RecordNotFoundException, InvalidCalendarEventException,
			UserNotAuthorizedException, RemoteException {
		decorateEvent(eventForm, event);
		event = pim.updateEvent(event);

		return createUpdateResponse(request, event, session, mapping);
	}

	private ActionForward createUpdateResponse(HttpServletRequest request, CalendarEvent event, WCAPSession session, ActionMapping mapping) {
		Status status;
		Invite myInvite = findMyInvite(event, session);
		status = new Status(myInvite, Status.SUCCESS, Status.OPERTATION_STORE);
		request.setAttribute("status", status);
		return this.findForward(mapping, "success", request);
	}

	private Invite findMyInvite(CalendarEvent event, WCAPSession session) {
		Iterator iterator = event.getInvites().iterator();
		while (iterator.hasNext()) {
			Invite myInvite = (Invite) iterator.next();
			if (myInvite.getUserName().equals(session.getUserId())) {
				return myInvite;
			}
		}
		throw new RuntimeException("Invite not found for user: " + session.getUserId());
	}

	private String[] extractNewAttendees(CalendarEvent event, Map attendees) {
		Iterator invites = event.getInvites().iterator();
		while (invites.hasNext()) {
			attendees.remove(((Invite) invites.next()).getUserName());
		}
		return ((String[]) attendees.keySet().toArray(new String[0]));
	}

	private ActionForward createEvent(HttpServletRequest request,
			StoreEventsForm eventForm, PIMService pim, ActionMapping mapping) throws ParseException,
			UserUnknownException, InvalidCalendarEventException,
			DuplicateInviteException, RemoteException {
		CalendarEvent event = constructEvent(eventForm);
        Map attendees = parseAttendees(eventForm.getAttendees());
        String[] userNames = extractNewAttendees(event, attendees);
        userNames = attendeesToUsers(userNames);
	//	Invite[] invites = pim.scheduleEvent(new String[] { this.getSession(
	//			request).getUserId() }, event);
        Invite[] invites = pim.scheduleEvent(userNames, event);
		Status status = new Status(invites[0], Status.SUCCESS,
				Status.OPERTATION_STORE);
		request.setAttribute("currentTime", GMTTimeFormat.getFormat().format(
				new Date()));		
		request.setAttribute("status", status);
		WCAPSession session = this.getSession(request);
		request.setAttribute("calinfo", new CalInfo(session.getUserId(),
				session.getUserName(), session.getUserId(), null, null));
		return this.findForward(mapping, "success", request);
	}

	private String[] attendeesToUsers(String[] userNames) {
        Set<String> result = new HashSet<String>();
        for (String username : userNames) {
            UserProfile profile = this.ups.findProfile(username);
            String user = profile == null ? username : profile.getUsername(); //if this is a local user then grab it
                                                                              //otherwise keep it literally
            result.add(user);
        }
        return result.toArray(new String[result.size()]);
    }

    private CalendarEvent constructEvent(StoreEventsForm eventForm)
			throws ParseException {
		CalendarEvent event = new CalendarEvent();
		decorateEvent(eventForm, event);
		return event;
	}

	private void decorateEvent(StoreEventsForm eventForm, CalendarEvent event)
			throws ParseException {
		DateFormat format = GMTTimeFormat.getFormat();
		Date startDate = format.parse(eventForm.getDtstart());
		Date endDate = format.parse(eventForm.getDtend());
        event.setGUID(eventForm.getUid());
		event.setStartDate(startDate);
		event.setEndDate(endDate);
		event.setTitle(eventForm.getSummary());
		event.setNote(eventForm.getDesc());
		event.setLocation(eventForm.getLocation());
        if (eventForm.getStatus() == null) {
            eventForm.setStatus(StatusUtil.codeToStatus(StatusUtil.DRAFT));
        }
        event.setStatus(StatusUtil.toCode(eventForm.getStatus()));
	}

	private Map parseAttendees(String input) {
        if (input == null || input.trim().length() == 0) {
            return new HashMap<String, Attendee>(); //empty map for empty attendees
        }
		String[] inviteString = input.split(";");
		Map<String, Attendee> attendeeMap = new HashMap<String, Attendee>();
		for (int i = 0; i < inviteString.length; i++) {
			Attendee attendee = Attendee.parseAttendee(inviteString[i], this.ups);
			if(attendee != null)
				attendeeMap.put(attendee.getUserId(), attendee);
		}
		return attendeeMap;
	}
    
    public void setUserProfileService(String userProfileService) {
        this.userProfileService = userProfileService;
    }
    
    public String getUserProfileService() {
        return this.userProfileService;
    }
}
