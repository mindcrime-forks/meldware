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
package org.buni.meldware.calendar.interfaces;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Hashtable;
import java.util.Properties;

import javax.naming.Context;
import javax.security.auth.login.LoginContext;

import junit.framework.TestCase;

/**
 * DOCUMENT ME!
 * 
 * @author $Author: andy $
 * @version $Revision: 1.2 $
 */
public class AbstractTestCase extends TestCase {
	protected LoginContext lc = null;

	/**
	 * Constructor for AbstractTestCase.
	 */
	public AbstractTestCase() {
		super();
	}

	/**
	 * Constructor for AbstractTestCase.
	 * 
	 * @param arg0
	 */
	public AbstractTestCase(String arg0) {
		super(arg0);
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param msg
	 *            DOCUMENT ME!
	 * @param exp
	 *            DOCUMENT ME!
	 */
	public void fail(String msg, Exception exp) {
		StringWriter writer = new StringWriter();
		exp.printStackTrace(new PrintWriter(writer));
		msg = msg + "\n Exception: \n" + writer.getBuffer().toString();
		fail(msg);
	}

	public Hashtable getContext(String userName, String password) {
		String authConf = this.getClass().getResource("/auth.conf").toString();
		System.setProperty("java.security.auth.login.config", authConf);
		System.out.println("Auth config file:" + authConf);
		Properties context = new Properties();
		context.put(Context.INITIAL_CONTEXT_FACTORY,
				"org.jboss.security.jndi.LoginInitialContextFactory");
		context.put(Context.PROVIDER_URL, "jnp://localhost:1099/");
		context.put(Context.SECURITY_CREDENTIALS, userName);
		context.put(Context.SECURITY_PRINCIPAL, password);
		context.put(Context.SECURITY_PROTOCOL, "client-login");
		return context;
	}
}
