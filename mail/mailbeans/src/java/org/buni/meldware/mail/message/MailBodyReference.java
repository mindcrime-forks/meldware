package org.buni.meldware.mail.message;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class MailBodyReference {

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	@Basic
	private long storeId;
	
	public MailBodyReference() {
	}

	public long getStoreId() {
		return storeId;
	}

	public void setStoreId(long storeId) {
		this.storeId = storeId;
	}

	public long getId() {
		return id;
	}

}
