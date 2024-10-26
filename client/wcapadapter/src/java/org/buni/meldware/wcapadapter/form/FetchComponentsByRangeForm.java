package org.buni.meldware.wcapadapter.form;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionMapping;

public class FetchComponentsByRangeForm extends CalForm {
	private String relativealarm = null;
	private String compressed = null;
	private String recurring = null;
	private String dtstart = null;
	private String dtend = null;
	
	public String getCompressed() {
		return compressed;
	}
	public void setCompressed(String compressed) {
		this.compressed = compressed;
	}
	public String getDtend() {
		return dtend;
	}
	public void setDtend(String dtend) {
		this.dtend = dtend;
	}
	public String getDtstart() {
		return dtstart;
	}
	public void setDtstart(String dtstart) {
		this.dtstart = dtstart;
	}
	public String getRecurring() {
		return recurring;
	}
	public void setRecurring(String recurring) {
		this.recurring = recurring;
	}
	public String getRelativealarm() {
		return relativealarm;
	}
	public void setRelativealarm(String relativealarm) {
		this.relativealarm = relativealarm;
	}
	@Override
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		relativealarm = null;
		compressed = null;
		recurring = null;
		dtstart = null;
		dtend = null;
		super.reset(mapping, request);
	}
}
