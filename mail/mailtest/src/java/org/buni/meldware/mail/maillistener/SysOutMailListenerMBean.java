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
package org.buni.meldware.mail.maillistener;

import org.buni.meldware.mail.MailException;
import org.buni.meldware.mail.message.Message;
import org.jboss.system.ServiceMBean;

/**
 * Proof of concept implementation which deals with posting your mails to JMS..  See 
 * JMSMailListener for more info.  
 *
 * @author Andrew C. Oliver
 */
public interface SysOutMailListenerMBean extends ServiceMBean {


    /**
     * actually send the message.  This is used by the JMXMailListenerProxy to ACTUALLY send the mail
     * @param msg mail to send (we'll have to cast it to Mail)
    * @throws MailException 
     */
    Message send(Message msg) throws MailException;
}


