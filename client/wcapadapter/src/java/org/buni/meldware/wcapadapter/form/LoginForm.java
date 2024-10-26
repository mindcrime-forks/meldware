package org.buni.meldware.wcapadapter.form;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionMapping;

/**
 * @author Aron Sogor
 * 
 */
public class LoginForm extends WCAPForm {
	private static final long serialVersionUID = "$Id:".hashCode();
	
	private String user = null;
	private String password = null;
	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}
	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	/**
	 * @return the user
	 */
	public String getUser() {
		return user;
	}
	/**
	 * @param user the user to set
	 */
	public void setUser(String userName) {
		this.user = userName;
	}
	@Override
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		user = null;
		password = null;
		super.reset(mapping, request);
	}
	
	
}
