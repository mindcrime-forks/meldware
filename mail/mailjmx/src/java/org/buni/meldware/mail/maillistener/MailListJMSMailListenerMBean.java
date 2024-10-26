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

import javax.management.ObjectName;

import org.buni.meldware.mail.message.MailBodyManager;
import org.buni.meldware.mail.message.Message;
import org.jboss.system.ServiceMBean;

/**
 * Mail List service mbean implementation
 *
 * @author Andrew C. Oliver <acoliver ot jboss dat org>
 * @version $Revision: 1.2 $
 */
public interface MailListJMSMailListenerMBean extends ServiceMBean {
    
    String getConnectionFactoryName();

    /**
     * @param string queue or topic to post to (for domains in the onServerDomainsList)
     */
    void setDestination(String string);

    /**
     * @return String name of the queue or topic we're posting to for domains in the onServerDomainsList
     */
    String getDestination();

    ObjectName getMailListManager();

    void setMailListManager(ObjectName on);
    
    void setMailBodyManager(MailBodyManager mgr);
    MailBodyManager getMailBodyManager();

    /**
     * actually send the message.  This is used by the JMXMailListenerProxy to ACTUALLY send the mail
     * This checks to see that the address is indeed a mail list and the puts it on the queue or topic.
     * @param msg mail to send (we'll have to cast it to Mail)
     * @return the message containing what could not be handled by this listener, null if 
     * the whole message was consumed
     */
    Message send(Message msg);

}
