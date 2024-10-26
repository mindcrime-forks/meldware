/*
 * Bunisoft the Open Source Communications Company
 * Copyright 2006, Bunisoft LLC.,
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

import java.text.DateFormat;
import java.util.Date;

import javax.mail.internet.MailDateFormat;

/**
 * This class is not constructable but contains all of the date functions used throughout
 * @version $Revision: 1.2 $
 */
public class DateUtil {
	
	static MailDateFormat mailDateFormat;
	static{
		 mailDateFormat = new MailDateFormat();
	}
	
	/**
	 * private constructor to prevent construction
	 */
	private DateUtil(){}
	/**
	 * This may have a bug, it needs to be tested outside of W Europe and the US.  I don't know how
	 * it would work abroad.  It is supposed to return a LONG INTERNET TIME whatever that means.
	 * @return date in "LONG INTERNET TIME"
	 */
	public static String longInternetDate() {
		Date date = new Date();
		DateFormat dformat = DateFormat.getDateInstance(DateFormat.LONG);
		DateFormat tformat = DateFormat.getTimeInstance(DateFormat.LONG);
		return dformat.format(date)+" " + tformat.format(date);
	}
	
	/**  
	 * @return Current date in "message header format", specified in RFC 2822
	 */
	public static String mailHeaderDate(){
		return mailDateFormat.format(new Date());
	}
	
}
