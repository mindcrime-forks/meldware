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

import org.buni.meldware.mail.domaingroup.DomainGroupMBean;
import org.buni.meldware.mail.message.MailAddress;
import org.buni.meldware.mail.message.MailBodyManager;
import org.buni.meldware.mail.message.Message;
import org.jboss.system.ServiceMBean;
import org.w3c.dom.Element;

/**
 * See JMSMailListener for more info.
 *
 * @author Andrew C. Oliver
 * @version $Revision: 1.2 $
 */
public interface JMSMailListenerMBean extends ServiceMBean {

    /**
     * @param string queue or topic to post to (for domains in the onServerDomainsList)
     */
    void setOnServerPostDestination(String string);

    /**
     * @param string queue or topic to post to for domains not in the onServerDomainsList
     */
    void setOffServerPostDestination(String string);

    /**
     * @return String name of the queue or topic we're posting to for domains in the onServerDomainsList
     */
    String getOnServerPostDestination();

    /**
     * @return STring name of the queue or topic we're posting to for domains NOT in the onServerDomainsList
     */
    String getOffServerPostDestination();

    /**
     * @param domainGroup domain group reference
     */
    void setDomainGroup(DomainGroupMBean domainGroup);
    
    void setMailBodyManager(MailBodyManager mgr);

    /**
     * @return domainGroup domain group reference
     */
    DomainGroupMBean getDomainGroup();

    /**
     * @param ma the address we will send to
     * @return either the onServer or offServer queue/topic name depending on whether the mail is local or not
     */
    String getDestinationForMailAddress(MailAddress ma);

    /** Sets the times between retries for local delivery
     * @param Element representing xml of form: <BR>
     * &lt;localRetryTimes&gt;&lt;retryTime&gt;30&lt;/retryTime&gt;&lt;retryTime&gt;120&lt;/retryTime&gt;&lt;/localRetryTimes&gt;<BR>
     * Means maximum two retries will happen, the first will wait 30 seconds, and the second 120 seconds
     * 
     */
    void setLocalRetryTimes(Element localRetryTimes);

    /** Gets the times between retries for local delivery
     * @param Element representing xml of form: <BR>
     */
    Element getLocalRetryTimes();

    /** Sets the times between retries for remote delivery
     * @param Element representing xml of form: <BR>
     * &lt;remoteRetryTimes&gt;&lt;retryTime&gt;30&lt;/retryTime&gt;&lt;retryTime&gt;120&lt;/retryTime&gt;&lt;/remoteRetryTimes&gt;<BR>
     * Means maximum two retries will happen, the first will wait 30 seconds, and the second 120 seconds
     * 
     */
    void setRemoteRetryTimes(Element remoteRetryTimes);

    /** Gets the times between retries for remote delivery
     * @param Element representing xml of form: <BR>
     */
    Element getRemoteRetryTimes();

    /**
     * actually send the message.  This is used by the JMXMailListenerProxy to ACTUALLY send the mail
     * @param msg mail to send (we'll have to cast it to Mail)
     * @return the message containing what could not be handled by this listener, null if 
     * the whole message was consumed
     */
    Message send(Message msg);

}
