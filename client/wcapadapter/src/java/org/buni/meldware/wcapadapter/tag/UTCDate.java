package org.buni.meldware.wcapadapter.tag;

import java.text.DateFormat;
import java.util.Date;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.beanutils.PropertyUtils;
import org.buni.meldware.common.util.GMTTimeFormat;

public class UTCDate extends TagSupport {

	private static final long serialVersionUID="$Id:".hashCode();
	private String name = null;
	private String property = null;
	private DateFormat format = GMTTimeFormat.getFormat();
	
	public UTCDate() {
		super();
	}

	@Override
	public int doStartTag() throws JspException {
		Object obj = this.pageContext.getAttribute(name);
		JspWriter writer = this.pageContext.getOut();
		try {
			Date date = ((property == null)?(Date)obj:(Date)PropertyUtils.getProperty(obj, property));
			writer.print(format.format(date));
		} catch (Exception e) {
			throw new JspException("OOPS:" + e.getMessage());
		}
		return SKIP_BODY;
	}

	public int doEndTag() throws JspException {
		return EVAL_PAGE;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}
	
	public void release() {
		super.release();
		this.property = null;
		this.name = null;
	}
}
