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
package org.buni.meldware.calendar.mdb;

import javax.ejb.EJBException;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;

import org.buni.meldware.calendar.data.Invite;
import org.buni.meldware.calendar.eventbus.Event;
import org.buni.meldware.calendar.eventbus.mail.StatusChange;
import org.buni.meldware.calendar.interfaces.AddressManagerLocal;
import org.buni.meldware.calendar.interfaces.AddressManagerUtil;
import org.buni.meldware.calendar.interfaces.ScheduleManagerLocal;
import org.buni.meldware.calendar.interfaces.ScheduleManagerUtil;
import org.buni.meldware.common.preferences.UserProfile;

/**
 * Mail event response Handler takes
 * 
 * @author Aron Sogor
 * @version $Revision: 1.4 $
 * 
 * @ejb.bean name="/ejb/reporting/MailResposeHandlerMDB"
 *           acknowledge-mode="Auto-acknowledge"
 *           destination-type="javax.jms.Queue"
 *           subscription-durability="Durable" transaction-type="Container"
 * @ejb.transaction type="Required"
 * @ejb.transaction-type="Container"
 * @ejb.security-identity run-as="calendaruser"
 * @jboss.destination-jndi-name name="queue/calendarResponse"
 * @jboss:subscriber subscription-id="MailResposeHandlerMDB"
 *                   client-id="MailResposeHandlerMDB"
 * 
 */
public class MailResposeHandlerMDB extends BaseUserNotificationMDB {

    public void onMessage(Message aMessage) {
        Event response = null;
        try {
            response = (Event) ((ObjectMessage) aMessage).getObject();
            log.info("Received user response + " + response);
            if (response instanceof StatusChange)
                processStatusChange((StatusChange) response);
            
        } catch (JMSException ex) {
            log.error("Problem getting message in : "
                    + MailResposeHandlerMDB.class.getName(), ex);
            return;
        }
    }

    public void ejbRemove() throws EJBException {

    }

    public void processStatusChange(StatusChange event) {
        try {
            AddressManagerLocal addLocal = AddressManagerUtil.getLocalHome()
                    .create();
            ScheduleManagerLocal schLocal = ScheduleManagerUtil.getLocalHome()
                    .create();
            UserProfile user = profileService.findProfile(event.getActor());
            Invite[] invites = schLocal.listInvites(new String[] { event
                    .getEventGUID() }, user.getUsername());
            for (int i = 0; i < invites.length; i++) {
                invites[i].setStatus(event.getAction());
                schLocal.updateInvite(invites[i], user.getUsername());
            }
        } catch (Exception e) {
            log.error("Failed to update event:" + event.getEventGUID()
                    + " status:" + " for user:" + event.getActor(), e);
        }
    }
}
