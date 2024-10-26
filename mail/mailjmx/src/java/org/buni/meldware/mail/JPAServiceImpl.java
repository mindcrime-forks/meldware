package org.buni.meldware.mail;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;

import org.buni.meldware.common.logging.Log;
import org.jboss.system.ServiceMBeanSupport;

/**
 * Simple service that looks up an entity manager from Jndi when it starts.
 * Can be injected into other services.
 * 
 * @author mike
 *
 */
public class JPAServiceImpl extends ServiceMBeanSupport implements JPAService {

	private final static Log log = Log.getLog(JPAServiceImpl.class);
    private EntityManager em;
    private String entityManagerName;
	
	public void startService() throws NamingException {
		log.info("Connecting to EntityManager: %s", entityManagerName);
    	if (entityManagerName != null) {
            InitialContext ctx = new InitialContext();
            em = ((EntityManager) ctx.lookup(entityManagerName));    	
    	} else {
    		throw new NamingException("EntityManager is not specified");
    	}		
	}


	public EntityManager getEntityManager() {
		return em;
	}


	public String getEntityManagerName() {
		return entityManagerName;
	}

	public void setEntityManagerName(String emJndiName) {
		this.entityManagerName = emJndiName;
	}
}
