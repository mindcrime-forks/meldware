package org.buni.meldware.wcapadapter.action;

import java.text.DateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.buni.meldware.calendar.data.Invite; 
import org.buni.meldware.calendar.interfaces.PIMServiceLocal;
import org.buni.meldware.calendar.interfaces.PIMServiceUtil;
import org.buni.meldware.calendar.session.exception.UserUnknownException;
import org.buni.meldware.common.preferences.UserProfile;
import org.buni.meldware.common.util.GMTTimeFormat;
import org.buni.meldware.wcapadapter.bean.CalInfo;
import org.buni.meldware.wcapadapter.bean.Error;
import org.buni.meldware.wcapadapter.form.FetchComponentsByRangeForm; 
import org.buni.meldware.wcapadapter.session.WCAPSession;
import org.buni.meldware.wcapadapter.util.AddressUtil;

public class GetFreeBusy extends WCAPSessionAction {

    /* (non-Javadoc)
	 * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        loadServices(mapping);
		FetchComponentsByRangeForm rangeForm = (FetchComponentsByRangeForm) form;
		String username = rangeForm.getCalid();
		DateFormat format = GMTTimeFormat.getFormat();
		Date startDate = format.parse(rangeForm.getDtstart());
		Date endDate = format.parse(rangeForm.getDtend());
		PIMServiceLocal pim = PIMServiceUtil.getLocalHome().create();
		Invite[] invite;
        username = username != null && username.toLowerCase().startsWith("mailto:") && username.length() > 7 ? AddressUtil.removeMailTo(username) : username;
        username = username == null && request.getUserPrincipal() != null ? request.getUserPrincipal().getName() : username;
        username = username == null ? "nobody" : username;
        username = attendeeToUser(username);
		try {
			invite = pim.listInvites(startDate, endDate, username);
		} catch (UserUnknownException e) {
			return this.findError(mapping, Error.CALENDAR_DOES_NOT_EXIST, request);
		}
		request.setAttribute("invites", invite);
		WCAPSession session = this.getSession(request);
		request.setAttribute("startDate", rangeForm.getDtstart());
		request.setAttribute("endDate", rangeForm.getDtend());
//        request.setAttribute("calinfo",new CalInfo(username,username,username,null,null));
		request.setAttribute("calinfo",new CalInfo(rangeForm.getCalid(),rangeForm.getCalid(),rangeForm.getCalid(),null,null));
		return this.findForward(mapping, "success", request);
	}
    
    private String attendeeToUser(String userName) {
        UserProfile profile = this.ups.findProfile(userName);
        String user = profile == null ? userName : profile.getUsername(); //if this is a local user then grab it
                                                                              //otherwise keep it literally
        String result = user == null ? userName : user;
        return result;
    }
    
}
