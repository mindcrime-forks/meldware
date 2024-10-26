package org.buni.meldware.wcapadapter.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.buni.meldware.calendar.data.Invite;
import org.buni.meldware.calendar.data.InviteStatus;
import org.buni.meldware.calendar.interfaces.PIMService;
import org.buni.meldware.calendar.interfaces.PIMServiceUtil;
import org.buni.meldware.wcapadapter.bean.Status;
import org.buni.meldware.wcapadapter.form.DeleteEventsByIdForm;

public class DeleteEventsById extends WCAPSessionAction {

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
		DeleteEventsByIdForm deleteForm = (DeleteEventsByIdForm)form;
		PIMService pim = PIMServiceUtil.getHome().create();
		Invite myInvite = pim.listInvites(new String[]{deleteForm.getUid()})[0];
		myInvite.setStatus(InviteStatus.CANCELED);
		pim.updateInvite(myInvite);
		Status status = new Status(myInvite, Status.SUCCESS,
				Status.OPERTATION_DELETE);
		request.setAttribute("status", status);
		return this.findForward(mapping, "success", request);
	}
}
