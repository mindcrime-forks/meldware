package org.buni.meldware.wcapadapter.bean;

import java.util.Date;

import org.buni.meldware.common.util.GMTTimeFormat;

public class CalInfo {
	private String calId = null;

	private String createdDate = null;

	private String lastModified = null;

	private String name = null;
	
	private String ownerId = null;
	
	public CalInfo(String calId, String name, String ownerId, String lastModified, String createdDate) {
		super();
		this.calId = calId;
		this.name = name;
		this.ownerId = ownerId;
		this.createdDate = (createdDate==null?now():createdDate);
		this.lastModified = (lastModified==null?this.createdDate:lastModified);
	}

	public String getCalId() {
		return calId;
	}

	public String getCreatedDate() {
		return createdDate;
	}
	
	public String getLastModified() {
		return lastModified;
	}

	public String getName() {
		return name;
	}

	public String getOwnerId() {
		return ownerId;
	}

	private String now() {
		return GMTTimeFormat.getFormat().format(new Date());
	}
}
