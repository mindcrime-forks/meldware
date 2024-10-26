package org.buni.meldware.wcapadapter.form;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

/**
 * @author Aron Sogor
 * 
 */
public class WCAPForm extends ActionForm {

	private static final long serialVersionUID = "$Id:".hashCode();

	private String id = null;

	private String appid = null;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAppid() {
		return appid;
	}

	public void setAppid(String appid) {
		this.appid = appid;
	}

	@Override
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		id = null;
		appid = null;
		super.reset(mapping, request);
	}
}
