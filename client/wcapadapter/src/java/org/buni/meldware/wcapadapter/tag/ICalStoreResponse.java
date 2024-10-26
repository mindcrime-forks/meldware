package org.buni.meldware.wcapadapter.tag;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.Iterator;
 
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;
 
import org.buni.meldware.calendar.data.Invite;
import org.buni.meldware.calendar.data.InviteStatus;
import org.buni.meldware.common.util.GMTTimeFormat;
import org.buni.meldware.wcapadapter.bean.Status;
 
public class ICalStoreResponse extends ICalVEvent {

	private static final long serialVersionUID="$Id:".hashCode();
	
	private String status = null;

	private Status statusObj = null;

	public ICalStoreResponse() {
		super();
	}

	@Override
	public int doStartTag() throws JspException {
		this.statusObj = (Status) this.pageContext.findAttribute(status);
		JspWriter writer = this.pageContext.getOut();

		try {
			writeVEvent(statusObj.getInvite(), writer);
		} catch (IOException e) {
			throw new JspException(e);
		}
		return SKIP_BODY;
	}

	protected void writeResponseStatus(JspWriter writer) throws IOException {
		writer.println("REQUEST-STATUS:" + statusObj.getICalStatus());
	}

	public int doEndTag() throws JspException {
		return EVAL_PAGE;
	}

	public void release() {
		super.release();
		status = null;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
