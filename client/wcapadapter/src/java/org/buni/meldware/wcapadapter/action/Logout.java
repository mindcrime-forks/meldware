package org.buni.meldware.wcapadapter.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.buni.meldware.wcapadapter.bean.Error;

public class Logout extends WCAPSessionAction {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		this.getSession(request).expire();
		return this.findError(mapping, Error.LOGOUT, request);
	}

}
