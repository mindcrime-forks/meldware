package org.buni.meldware.wcapadapter.form;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionMapping;

public class CalForm extends WCAPForm {
	private String calid = null;

	public String getCalid() {
		return calid;
	}

	public void setCalid(String calid) {
		this.calid = calid;
	}

	@Override
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		this.calid = null;
		super.reset(mapping, request);
	}
	
}
