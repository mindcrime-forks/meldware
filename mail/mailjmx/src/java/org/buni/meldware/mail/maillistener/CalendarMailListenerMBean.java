/*
 * Bunisoft the Open Source Communications Company
 * Copyright 2006, Bunisoft Inc., and individual contributors as
 * indicated by the @authors tag.  See the copyright.txt in the 
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; version 2.1 of
 * the License.
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
package org.buni.meldware.mail.maillistener;

import org.buni.meldware.mail.MailException;
import org.buni.meldware.mail.message.MailBodyManager;
import org.buni.meldware.mail.message.Message;
import org.jboss.system.ServiceMBean;

/**
 * See CalendarMailListener for more info.
 * 
 * @author Aron Sogor
 * 
 */
public interface CalendarMailListenerMBean extends ServiceMBean {

	public String getCalendarUser();

	public void setCalendarUser(String calendarUser);

	public String getConnectionFactoryName();

	public void setConnectionFactoryName(String connectionFactoryName);

	public String getDestination();

	public void setDestination(String destination);

	public String getDestinationType();

	public void setDestinationType(String destType);

	Message send(Message msg) throws MailException;
    
    void setMailBodyManager(MailBodyManager mgr);
    
    MailBodyManager getMailBodyManager();

}
