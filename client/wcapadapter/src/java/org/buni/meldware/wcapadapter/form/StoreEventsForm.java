package org.buni.meldware.wcapadapter.form;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionMapping;

public class StoreEventsForm extends WCAPForm {
	public static final int METHOD_CANCEL = 8;// (organizer only uses this)

	public static final int METHOD_PUBLISH = 1;// (organizer only uses this)

	public static final int METHOD_REPLY = 4;// (attendees only use this)

	public static final int METHOD_REQUEST = 2;// (organizer only uses this)
	
	public static final int METHOD_UPDATE = 256; // invitee update personal copy 

	private String alarmEmails = null;

	private String alarmPopup = null;

	private String alarmStart = null;

	private String attachments = null;

	private String attendees = null;

	private String categories = null;

	private String compressed = null;

	private String contacts = null;

	private String desc = null;

	private String dtend = null;

	private String dtstart = null;

	private String exdates = null;

	private String exrules = null;

	private String fetch = null;

	private String icsClass = null;

	private String icsUrl = null;

	private String location = null;

	private int method = METHOD_PUBLISH;

	private String orgUID = null;

	private String priority = null;

	private String rdates = null;

	private String recurring = null;

	private String relativealarm = null;

	private String replace = null;

	private String rrules = null;

	private String status = null;

	private String storetype = null;

	private String summary = null;

	private String transparent = null;

	private String tzid = null;

	private String uid = null;

	public String getAlarmEmails() {
		return alarmEmails;
	}

	public String getAlarmPopup() {
		return alarmPopup;
	}

	public String getAlarmStart() {
		return alarmStart;
	}

	public String getAttachments() {
		return attachments;
	}

	public String getAttendees() {
		return attendees;
	}

	public String getCategories() {
		return categories;
	}

	public String getCompressed() {
		return compressed;
	}

	public String getContacts() {
		return contacts;
	}

	public String getDesc() {
		return desc;
	}

	public String getDtend() {
		return dtend;
	}

	public String getDtstart() {
		return dtstart;
	}

	public String getExdates() {
		return exdates;
	}

	public String getExrules() {
		return exrules;
	}

	public String getFetch() {
		return fetch;
	}

	public String getIcsClass() {
		return icsClass;
	}

	public String getIcsUrl() {
		return icsUrl;
	}

	public String getLocation() {
		return location;
	}

	public int getMethod() {
		return method;
	}

	public String getOrgUID() {
		return orgUID;
	}

	public String getPriority() {
		return priority;
	}

	public String getRdates() {
		return rdates;
	}

	public String getRecurring() {
		return recurring;
	}

	public String getRelativealarm() {
		return relativealarm;
	}

	public String getReplace() {
		return replace;
	}

	public String getRrules() {
		return rrules;
	}

	public String getStatus() {
		return status;
	}

	public String getStoretype() {
		return storetype;
	}

	public String getSummary() {
		return summary;
	}

	public String getTransparent() {
		return transparent;
	}

	public String getTzid() {
		return tzid;
	}

	public String getUid() {
		return uid;
	}

	public void setAlarmEmails(String alarmEmails) {
		this.alarmEmails = alarmEmails;
	}

	public void setAlarmPopup(String alarmPopup) {
		this.alarmPopup = alarmPopup;
	}

	public void setAlarmStart(String alarmStart) {
		this.alarmStart = alarmStart;
	}

	public void setAttachments(String attachments) {
		this.attachments = attachments;
	}

	public void setAttendees(String attendees) {
		this.attendees = attendees;
	}

	public void setCategories(String categories) {
		this.categories = categories;
	}

	public void setCompressed(String compressed) {
		this.compressed = compressed;
	}

	public void setContacts(String contacts) {
		this.contacts = contacts;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public void setDtend(String dtend) {
		this.dtend = dtend;
	}

	public void setDtstart(String dtstart) {
		this.dtstart = dtstart;
	}

	public void setExdates(String exdates) {
		this.exdates = exdates;
	}

	public void setExrules(String exrules) {
		this.exrules = exrules;
	}

	public void setFetch(String fetch) {
		this.fetch = fetch;
	}

	public void setIcsClass(String icsClass) {
		this.icsClass = icsClass;
	}

	public void setIcsUrl(String icsUrl) {
		this.icsUrl = icsUrl;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public void setMethod(int method) {
		this.method = method;
	}

	public void setOrgUID(String orgUID) {
		this.orgUID = orgUID;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public void setRdates(String rdates) {
		this.rdates = rdates;
	}

	public void setRecurring(String recurring) {
		this.recurring = recurring;
	}

	public void setRelativealarm(String relativealarm) {
		this.relativealarm = relativealarm;
	}

	public void setReplace(String replace) {
		this.replace = replace;
	}

	public void setRrules(String rrules) {
		this.rrules = rrules;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setStoretype(String storetype) {
		this.storetype = storetype;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public void setTransparent(String transparent) {
		this.transparent = transparent;
	}

	public void setTzid(String tzid) {
		this.tzid = tzid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	@Override
	public void reset(ActionMapping arg0, HttpServletRequest arg1) {
		alarmEmails = null;
		alarmPopup = null;
		alarmStart = null;
		attachments = null;
		attendees = null;
		categories = null;
		compressed = null;
		contacts = null;
		desc = null;
		dtend = null;
		dtstart = null;
		exdates = null;
		exrules = null;
		fetch = null;
		icsClass = null;
		icsUrl = null;
		location = null;
		method = METHOD_PUBLISH;
		orgUID = null;
		priority = null;
		rdates = null;
		recurring = null;
		relativealarm = null;
		replace = null;
		rrules = null;
		status = null;
		storetype = null;
		summary = null;
		transparent = null;
		tzid = null;
		uid = null;
	}

}
