/*
 * Bunisoft the Open Source Communications Company
 * Copyright 2007, Bunisoft LLC.,
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
package org.buni.meldware.calendar.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.buni.meldware.calendar.data.ServerInfo;
import org.buni.meldware.calendar.data.Task;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * DOCUMENT ME!
 * 
 * @author aron To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public class HibernateLookUp {
	private static SessionFactory factory = null;

	/**
	 * 
	 */
	private HibernateLookUp() {
		super();
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 * 
	 * @throws HibernateException
	 *             DOCUMENT ME!
	 */
	public static final SessionFactory getSessionFactory()
			throws HibernateException {
		initializeFactory();

		return factory;
	}

	/**
	 * This is a hack to ping hibernate
	 * to load the schema if needed.
	 */
	public static final void checkSchema()
	{
		Log log = LogFactory.getLog(HibernateLookUp.class);
		log.info("Check schema");
		Session session = HibernateLookUp.getSessionFactory().openSession();
		Query query = session.createQuery("select count(TaskId) from " + Task.class.getName()
				+ " as user");
        
		log.info("Calendar starts with task count:" + query.list().get(0));
		session.close();
	}
	
	private static void initializeFactory() throws HibernateException {
		if (factory == null) {
			loadFactory();
		}
	}

	private static synchronized void loadFactory() throws HibernateException {
		if (factory == null) {
			Log log = LogFactory.getLog(HibernateLookUp.class);
			log.debug("Start loading Hibernate Configuration");
			factory = new Configuration().configure("calenderServer." + ServerInfo.getInfo(ServerInfo.RDBMS_DIALECT)+".cfg.xml").buildSessionFactory();
			log.debug("Loading complete");
		}
	}
}
