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

import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.naming.InitialContext;

import org.apache.log4j.Logger;
import org.buni.meldware.calendar.eventbus.Event;
import org.buni.meldware.calendar.eventbus.mail.StatusChange;
import org.buni.meldware.mail.MailException;
import org.buni.meldware.mail.MailListener;
import org.buni.meldware.mail.message.Mail;
import org.buni.meldware.mail.message.MailAddress;
import org.buni.meldware.mail.message.MailBodyManager;
import org.buni.meldware.mail.message.Message;
import org.jboss.aspects.tx.Tx;
import org.jboss.aspects.tx.TxType;
import org.jboss.system.ServiceMBeanSupport;

/**
 * This is the calendar mail listener. It accepts incomming mail responses for
 * invitations.
 * 
 * @author Aron Sogor
 */
public class CalendarMailListener extends ServiceMBeanSupport implements
		CalendarMailListenerMBean, MailListener {

	private static final Logger log = Logger
			.getLogger(CalendarMailListener.class);

	private String calendarUser = "calendar";

	private MailBodyManager mgr;

	/**
	 * connection factory name
	 */
	private String connectionFactoryName;

	/**
	 * destination queue/topic name
	 */
	private String destination;

	/**
	 * queue or topic
	 */
	private String destinationType;

	public String getCalendarUser() {
		return calendarUser;
	}

	public String getConnectionFactoryName() {
		return connectionFactoryName;
	}

	public String getDestination() {
		return destination;
	}

	public String getDestinationType() {
		return destinationType;
	}

	public void handleResponse(Event response) {
		try {
			if (destinationType.toLowerCase().equals("topic"))
				this.sendMessageTopic(response);
			if (destinationType.toLowerCase().equals("queue"))
				this.sendMessageQueue(response);
		} catch (Exception ex) {
			log.error("Failed to deliver response to Calendar Server", ex);
		}
	}

	public Message send(Message msg) throws MailException {
		if (msg != null) {
			Mail mail = (Mail) msg;
			if(log.isInfoEnabled())
				log.debug("Process Calendar mail:" + msg);
			for (MailAddress toAddress : mail.getTo()) {
				if (toAddress.getUser().endsWith(calendarUser)) {
					String[] params = toAddress.getUser().replaceAll(
							"." + calendarUser, "").split("\\.");
					log.info("Do: " + params[0]);
					if (params[0].toUpperCase().equals("ACCEPT")) {
						handleResponse(new StatusChange(mail.getSender()
								.getRawAddress(), StatusChange.ACCEPTED,
								params[1]));
						mail.removeTo(toAddress);
					}
					if (params[0].toUpperCase().equals("DECLINE")) {
						handleResponse(new StatusChange(mail.getSender()
								.getRawAddress(), StatusChange.DECLINED,
								params[1]));
						mail.removeTo(toAddress);
					}
				}
			}
		}
		return msg;
	}

	/**
	 * does the real work for sendMessage in the event of a "queue" destType
	 * 
	 * @param msg
	 *            the message to be sent
	 * @throws Exception
	 *             if there is any problem (JMS related or otherwise)
	 */
	@Tx(TxType.REQUIRED)
	private void sendMessageQueue(Event msg) throws Exception {
		InitialContext ctx = new InitialContext();
		QueueConnectionFactory cf = (QueueConnectionFactory) ctx
				.lookup(connectionFactoryName);
		Queue queue = (Queue) ctx.lookup("queue/" + destination);
		QueueConnection qc = cf.createQueueConnection();
		QueueSession qs = qc.createQueueSession(true, 0);

		try {
			QueueSender sender = qs.createSender(queue);
			ObjectMessage om = qs.createObjectMessage(msg);
			sender.send(om);
		} finally {
			qc.close();
		}

	}

	/**
	 * does the real work for sendMessage in the event of a "topic" destType
	 * 
	 * @param msg
	 *            the message to be sent
	 * @throws Exception
	 *             if there is any problem (JMS related or otherwise)
	 */
	@Tx(TxType.REQUIRED)
	private void sendMessageTopic(Event msg) throws Exception {
		InitialContext ctx = new InitialContext();
		TopicConnectionFactory cf = (TopicConnectionFactory) ctx
				.lookup(connectionFactoryName);
		TopicConnection tc = cf.createTopicConnection();
		TopicSession ts = tc.createTopicSession(true, 0);
		Topic topic = (Topic) ctx.lookup("topic/" + destination);
		try {
			TopicPublisher pub = ts.createPublisher(topic);
			ObjectMessage om = ts.createObjectMessage(msg);
			pub.publish(om);
		} finally {
			tc.close();
		}
	}

	public void setCalendarUser(String calendarUser) {
		this.calendarUser = calendarUser;
	}

	public void setConnectionFactoryName(String connectionFactoryName) {
		this.connectionFactoryName = connectionFactoryName;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public void setDestinationType(String destType) {
		this.destinationType = destType;
	}

	/**
	 * @see org.buni.meldware.mail.maillistener.CalendarMailListenerMBean#getMailBodyManager()
	 */
	public MailBodyManager getMailBodyManager() {
		return mgr;
	}

	/**
	 * @see org.buni.meldware.mail.maillistener.CalendarMailListenerMBean#setMailBodyManager(org.buni.meldware.mail.message.MailBodyManager)
	 */
	public void setMailBodyManager(MailBodyManager mgr) {
		this.mgr = mgr;
	}
}
