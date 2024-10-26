package org.buni.meldware.wcapadapter.form;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionMapping;

public class UserPrefsForm extends WCAPForm {
	private String userid = null;

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}
	@Override
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		userid = null;
		super.reset(mapping, request);
	}
}
