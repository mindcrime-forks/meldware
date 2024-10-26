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

import java.util.Date;

import javax.ejb.EJBException;
import javax.jms.Message;

import org.buni.meldware.calendar.data.Invite;
import org.buni.meldware.calendar.data.InviteStatus;
import org.buni.meldware.calendar.interfaces.ScheduleManagerLocal;
import org.buni.meldware.calendar.interfaces.ScheduleManagerUtil;
/**
 * DOCUMENT ME!
 * 
 * @author $Author: andy $
 * @version $Revision: 1.5 $
 * 
 * @ejb.bean name="/ejb/reporting/ResourceAutoAcceptInviteMDB"
 *           acknowledge-mode="Auto-acknowledge"
 *           destination-type="javax.jms.Topic"
 *           subscription-durability="Durable" transaction-type="Container"
 * @ejb.transaction type="Required"
 * @ejb.transaction-type="Container"
 * @ejb.security-identity run-as="calendaruser"
 * @jboss.destination-jndi-name name="topic/calendar"
 * @jboss:subscriber subscription-id="ResourceAutoAcceptInvite" client-id="ResourceAutoAcceptInvite"
 */
public class ResourceAutoAcceptInviteMDB extends BaseCalendarMDB {

	/**
	 * @ejb.create-method
	 */
	public void ejbCreate() {
		
	}
	
	public void ejbRemove() throws EJBException {
		// TODO Auto-generated method stub
	}

	public void onMessage(Message aMessage) {
		ScheduleManagerLocal scheduleManager;
		try {
			scheduleManager = ScheduleManagerUtil.getLocalHome().create();
		} catch (Exception e) {
			log.error("ScheduleManage not found!", e);
			return;
		}
/*		try {
			CalendarChange event = (CalendarChange) ((ObjectMessage) aMessage)
					.getObject();

			if (event.getType() == CalendarChange.INVITE_CREATED) {
			Invite invite = scheduleManager.getInvite(event.getInviteId()); 
				if(invite.getUserName().getPreference().isIsResource())
					acceptInvite(scheduleManager,invite)
			} 
                //TODO ACO temporarily disable

			if (event.getType() == CalendarChange.EVENT_RESCHEDULED) {
				acceptInvitesForEvent(scheduleManager,event.getEventId());
			}
            
			
		} catch (Throwable exp) {
			this.log.error("Discard potential poison message", exp);
		}*/

	}
	
	private void acceptInvite(ScheduleManagerLocal scheduleManager,Invite invite){
		Date startdate = invite.getEvent().getStartDate();
		Date enddate = invite.getEvent().getEndDate();
		try {
			// default we accept
			invite.setStatus(InviteStatus.ACCEPTED);
			Invite[] currentSchedule = scheduleManager.listInvites(startdate, enddate, invite.getUserName());
			for (Invite existingInvite : currentSchedule) {
				// if there is an event already accpeted decline this invite
				if((existingInvite.getStatus() == InviteStatus.ACCEPTED)&&(existingInvite.getInviteId().longValue() != invite.getInviteId().longValue()))
					invite.setStatus(InviteStatus.DECLINED);
			}
			scheduleManager.updateInvite(invite, invite.getUserName());
		} catch (Exception e) {
			log.error("Faild to ack resource invite", e);
		}
	}

	private void acceptInvitesForEvent(ScheduleManagerLocal scheduleManager, long eventId){
		for (Invite invite : scheduleManager.listResourceInvitesForEvent(eventId)) {
			acceptInvite(scheduleManager, invite);
		}
	}
}
