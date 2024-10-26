package org.buni.meldware.wcapadapter.bean;

import java.util.HashMap;
import java.util.Map;

import org.buni.meldware.calendar.data.InviteStatus;
import org.buni.meldware.common.preferences.UserProfile;
import org.buni.meldware.common.preferences.UserProfileService;
import org.buni.meldware.wcapadapter.util.AddressUtil;

public class Attendee {
	public static final String KEY_PARTSTAT = "PARTSTAT";//The only required parameter. This shows the attendees participation status. 
	public static final String KEY_CUTYPE = "CUTYPE";//	Calendar user type. 
	public static final String KEY_MEMBER = "MEMBER";//	List of groups the attendee is part of. WCAP has no understanding of these groups. 
	public static final String KEY_ROLE = "ROLE";//	Role of the attendee in this meeting. 
	public static final String KEY_RSVP = "RSVP";//	Attendee response required or not. 
	public static final String KEY_DELEGATED_TO = "DELEGATED-TO";//	To whom the attendee delegates attendance. 
	public static final String KEY_DELEGATED_FROM = "DELEGATED-FROM";//	Attendee is a delegate for this person. 
	public static final String KEY_SENT_BY = "SENT-BY";// The calendar user acting on behalf of the specified user. 
	public static final String KEY_CN = "CN";// Display name of attendee. 
	public static final String KEY_DIR = "DIR";// Directory entry reference. 
	public static final String KEY_LANG = "LANG";// Language of the entry.
	
	private Map<String,String> values = new HashMap<String, String>();
	private String userId = null;
	private String mailTo = null;
	
	public Attendee() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public void setParam(String key,String value)
	{
		this.values.put(key, value);
	}
	
	public String getParam(String key)
	{
		return values.get(key);
	}

	public String getMailTo() {
		return mailTo;
	}

	public void setMailTo(String mailTo) {
		this.mailTo = mailTo;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	public static Attendee parseAttendee(String input, UserProfileService ups)
	{
		String[] attr = input.split("\\^");
		Attendee result = new Attendee();
		String userid = attr[attr.length-1];
        String loweruserid = userid.toLowerCase();
		if(loweruserid.toLowerCase().contains("mailto"))
		{
		    userid = AddressUtil.removeMailTo(userid);
			// try to look up te e-mail address
			result.setMailTo(userid);
            UserProfile up = ups.findProfile(result.getMailTo());
            String uid = up == null ? result.getMailTo() : up.getUsername();
           
			result.setUserId(uid);
		}
		else
			result.setUserId(userid);
			for (int i = 0; i < attr.length-1; i++) {
			if(attr[i].contains("="))
			{
				String[] pair = attr[i].split("=",2);
				if(pair.length==2)
					result.setParam(pair[0],pair[1]);
				else
					result.setParam(pair[0], null);
			}
		}
		return result;
	}
	
	public int getStatusCodeParsed(){
		String status = this.getParam(Attendee.KEY_PARTSTAT);
		if("ACCEPTED".equals(status))
			return InviteStatus.ACCEPTED;
		if("DECLINED".equals(status))
			return InviteStatus.DECLINED;
		if("CANCELED".equals(status))
			return InviteStatus.CANCELED;
		if("TENATIVE".equals(status))
			return InviteStatus.TENATIVE;
		throw new RuntimeException("Unknown Status code: " + status);
	}
}
