/*
 * Bunisoft the Open Source Communications Company
 * Copyright 2006, Bunisoft Inc.,
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
package org.buni.meldware.wcapadapter.action;

import javax.servlet.http.HttpServletRequest;

import org.buni.meldware.wcapadapter.session.WCAPSession;

/**
 * WCAPAction is to handle common WCAP corkyness.
 * 
 * @author Aron Sogor
 */
public abstract class WCAPSessionAction extends WCAPAction {

	public static final String SESSION_KEY = "wcapsession";
	
	public WCAPSession getSession(HttpServletRequest request) {
		return (WCAPSession)request.getAttribute(SESSION_KEY);
	}
}
