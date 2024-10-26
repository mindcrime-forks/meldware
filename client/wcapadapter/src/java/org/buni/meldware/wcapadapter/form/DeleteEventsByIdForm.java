package org.buni.meldware.wcapadapter.form;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionMapping;

public class DeleteEventsByIdForm extends CalForm {
	private String uid = null;

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}
	
	@Override
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		uid = null;
		super.reset(mapping, request);
	}
}
