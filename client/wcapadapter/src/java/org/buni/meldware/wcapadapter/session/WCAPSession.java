package org.buni.meldware.wcapadapter.session;

import java.util.Date;

public class WCAPSession {
	String userId = null;
	String userName = null;
	String password = null;
	String sessionId = null;
	Date lastTouched = null;
	WCAPSessionStore sessionStore = null;
	
	public WCAPSession(String userId, String userName, String password, WCAPSessionStore sessionStore) {
		super();
		this.userId = userId;
		this.password = password;
		this.userName = userName;
		this.lastTouched = new Date();
		this.sessionId = String.valueOf(this.hashCode());
		this.sessionStore = sessionStore;
	}

	public String getPassword() {
		return password;
	}

	public String getSessionId() {
		return sessionId;
	}

	public String getUserId() {
		return userId;
	}

	public String getUserName() {
		return userName;
	}
	
	public Date getLastTouched() {
		return lastTouched;
	}
	
	public void touch() {
		this.lastTouched = new Date();
	}

	public void expire() {
		this.sessionStore.remove(this);
	}
}
