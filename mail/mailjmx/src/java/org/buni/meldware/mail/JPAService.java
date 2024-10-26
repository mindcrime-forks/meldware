package org.buni.meldware.mail;

import javax.persistence.EntityManager;

import org.jboss.system.ServiceMBean;

public interface JPAService extends ServiceMBean {

	void setEntityManagerName(String emJndiName);
	
	String getEntityManagerName();
	
	EntityManager getEntityManager();
}
