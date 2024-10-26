/*
 * Bunisoft the Open Source Communications Company
 * Copyright 2006, Bunisoft Inc.,
 *
 * Portions of this software are Copyright 2006, JBoss Inc., and 
 * individual contributors as indicated by the @authors tag.
 * See the copyright.txt in the distribution for a full listing of
 * individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.buni.meldware.mail.util;

import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.UserTransaction;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.jboss.ejb3.entity.HibernateSession;
import org.jboss.logging.Logger;

/**
 * Utility class for Hibernate tasks...  This is still used by mail lists (which is why they are 
 * broken)...  Should no longer be used (only the EJB3 config'd session)
 * 
 * @author Dawie Malan
 * @version $Revision: 1.4 $
 */
public final class HibernateUtil {
    private static EntityManager session;
    private static final Logger log = Logger.getLogger(HibernateUtil.class);

    public static synchronized void initEM() {
       if (session == null) {
           try { // this is a big fat kludge to deal with EJB3 issues
               InitialContext ctx = new InitialContext();
               session = ((EntityManager) ctx.lookup("java:/EntityManagers/mail"));
           } catch (Exception e) {
               e.printStackTrace();
           }
       }
    }


    /**
     * Looks up the current UserTransaction
     * 
     * @return
     * @throws Exception
     */
    public static UserTransaction getUserTransaction() throws Exception {
        InitialContext ctx = new InitialContext();
        return (UserTransaction) ctx.lookup("UserTransaction");
    }

    /**
     * Looks up the SessionFactory from JNDI and opens a Session.
     * 
     * @param jndiName
     * @return
     * @throws NamingException
     * @throws HibernateException
     */
    public static Session getSession() throws HibernateException {
        //return HibernateContext.getSession(HIBERNATE_SESSION_FACTORY);
        //return Session;

        //try {
            if (session == null) {
               initEM();
            } 
            return ((HibernateSession)session).getHibernateSession();
        //} catch (NamingException e) {
        //    throw new HibernateException(e);
        //}
        //return fact.openSession();
    }

    public static Transaction getTransaction(Session session, String name)
            throws HibernateException {
        Transaction t = session.beginTransaction();
        if (log.isDebugEnabled()) {
            log.debug("Begin Transaction: " + name + ":"
                    + System.identityHashCode(t));
        }
        return t;
    }

    /**
     * Closes a session without throwing an exception.
     * 
     * @param log
     * @param session
     */
    public static void safeCloseSession(Logger log, Session session) {

        if (session != null) {
            try {
                //session.close();
            } catch (Exception e) {
                if (log != null)
                    log.error(e.toString(), e);
                else
                    e.printStackTrace();
            }
        } else {
            log.warn("Session is null");
        }
    }

	/**
	 * @param query
	 * @param name
	 * @return
	 */
  //  @Tx(TxType.SUPPORTS)
	public static <T> T singleResult(Query query, Class<T> entityClass) {
        @SuppressWarnings("unchecked")
		List<T> resultlist = (List<T>)query.getResultList();
		T result = null;
		if (resultlist.size() > 1) {
			//TODO type this
			throw new RuntimeException("Constraint Violation - singleResult returned "+resultlist.size());
		} else if (resultlist.size() == 1) {
			result = resultlist.get(0);
		}
		return result;
	}

}
