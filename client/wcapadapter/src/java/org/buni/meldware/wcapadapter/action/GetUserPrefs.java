package org.buni.meldware.wcapadapter.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.buni.meldware.calendar.data.Address;
import org.buni.meldware.calendar.interfaces.PIMServiceLocal;
import org.buni.meldware.calendar.interfaces.PIMServiceUtil;

public class GetUserPrefs extends WCAPSessionAction {

	/* (non-Javadoc)
	 * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		PIMServiceLocal pim = PIMServiceUtil.getLocalHome().create();
		String address = pim.getUserAddress();
		request.setAttribute("address", address);
		return this.findForward(mapping, "success", request);
	}
}
