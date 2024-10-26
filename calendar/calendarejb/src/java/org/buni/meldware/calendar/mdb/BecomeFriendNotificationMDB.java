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

import javax.ejb.MessageDrivenBean;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.buni.meldware.calendar.data.ServerInfo;
import org.buni.meldware.calendar.eventbus.AddressChange;


/**
 * DOCUMENT ME!
 * 
 * @author $Author: andy $
 * @version $Revision: 1.3 $
 * 
 * @ejb.bean name="/ejb/reporting/BecomeFriendNotificationMDB"
 *           acknowledge-mode="Auto-acknowledge"
 *           destination-type="javax.jms.Topic"
 *           subscription-durability="Durable" transaction-type="Container"
 * @ejb.transaction type="Required"
 * @ejb.transaction-type="Container"
 * @ejb.security-identity run-as="calendaruser"
 * @jboss.destination-jndi-name name="topic/addressbook"
 * @jboss:subscriber subscription-id="BecomeFriendNotification"
 *                   client-id="BecomeFriendNotification"
 */
public class BecomeFriendNotificationMDB extends BaseUserNotificationMDB
		implements MessageDrivenBean, MessageListener {
	static final long serialVersionUID = "$Id: BecomeFriendNotificationMDB.java,v 1.3 2007/12/30 01:59:41 andy Exp $".hashCode(); //$NON-NLS-1$

	/**
	 * DOCUMENT ME!
	 * 
	 * @param aMessage
	 *            DOCUMENT ME!
	 */
	public void onMessage(Message aMessage) {
		// check if service is not disabled
		if (!((Boolean) ServerInfo.getInfo(ServerInfo.NOTIFICATION_ENABLED))
				.booleanValue()) {
			this.log.info("Notification is disabled, skipping event.");

			return;
		}

		try {
			AddressChange event = (AddressChange) ((ObjectMessage) aMessage)
					.getObject();

			if (event.getType() == AddressChange.ADDRESS_SHARED) {
                //TODO ACO NOOP
                /*
					String fromUser = event.getActor();
					String toUser = event.getAddress().getOwner().getUserName();
					String toUserAddress = event.getAddress().getEmailAddress();
					// add a check if the toUser already have the address
					// if he/she does just send a memo but no invite.
					HashMap values = new HashMap();
				
                    UserProfile profile = profileService.findProfile(toUser);
                    String fn = profile.getPreference(PreferenceConstants.FRIEND_NOTIFICATION);
                    fn = fn == null ? ""+PreferenceConstants.CALENDAR_NOTIFICATION_NONE : fn;
                    int notification = 0; 
                    try {
                        notification = Integer.valueOf(fn);
                    } catch (Exception e) {}

					values.put("toUser", toUser);
					values.put("fromUser", fromUser);

					if (notification != PreferenceConstants.CALENDAR_NOTIFICATION_NONE) {
						processMessage(new String[] { toUserAddress }, values,
								"BecomeFriend", notification);
					}

*/
			}
		} catch (Throwable exp) {
			this.log.error("Discard potential poison message", exp);
		}
	}


}