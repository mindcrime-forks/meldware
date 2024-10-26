/*
 * JBoss, Home of Professional Open Source
 * Copyright 2006, JBoss Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
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
package org.buni.meldware.mail.delivery;

import static javax.management.MBeanServerInvocationHandler.newProxyInstance;
import static org.jboss.mx.util.MBeanServerLocator.locateJBoss;

import javax.ejb.EJBException;
import javax.ejb.MessageDrivenBean;
import javax.ejb.MessageDrivenContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.buni.meldware.common.logging.Log;
import org.buni.meldware.mail.MailListenerChain;
import org.buni.meldware.mail.message.Mail;
import org.buni.meldware.mail.message.MailBodyManager;

/**
 * Do not think this is still used, if so should be converted to message driven
 * pojo
 * 
 * @author Andrew C. Oliver <acoliver ot jboss dat org>
 * @version $Revision: 1.7 $
 */
public class DeliveryMDB implements MessageDrivenBean, MessageListener {

    private static final long serialVersionUID = -7671292897388892674L;

    private MailListenerChain chain;
    
    private MailBodyManager mgr;

    private static final Log log = Log.getLog(DeliveryMDB.class);
    
    public void ejbCreate() {

        try {
            InitialContext ctx = new InitialContext();
            MBeanServer server = locateJBoss();
            ObjectName listenerName = new ObjectName((String) ctx
                    .lookup("java:comp/env/MailListenerChain"));
            chain = (MailListenerChain) newProxyInstance(server, listenerName, 
                    MailListenerChain.class, false);
            ObjectName mgrName = new ObjectName((String) ctx
                    .lookup("java:comp/env/MailBodyManager"));
            mgr = (MailBodyManager) newProxyInstance(server, mgrName, 
            		MailBodyManager.class, false);
        } catch (NamingException e) {
            log.error(e, "Failed to lookup name of chain MBean from the Inital Context");
            throw new RuntimeException("Failed to lookup name of chain MBean from the Inital Context", e);
        } catch (MalformedObjectNameException e) {
            log.error(e, "The name of the mail listener chain is not correct");
            throw new RuntimeException(
                    "The name of the mail listener chain is not correct", e);
        }
    }

    public void setMessageDrivenContext(MessageDrivenContext arg0)
            throws EJBException {
        // TODO Auto-generated method stub

    }

    public void ejbRemove() throws EJBException {
        // TODO Auto-generated method stub

    }

    public void onMessage(Message jmsMessage) {

        try {
            // Convert the incoming message to a Mail message.
            ObjectMessage jmsObjectMessage = (ObjectMessage) jmsMessage;
            log.debug("Received message: %s", jmsMessage);
            Object messageObject = jmsObjectMessage.getObject();
            if (!(messageObject instanceof Mail)) {
                String msg = "Dropped invalid Object from Local Delivery queue.  Expected: %s, Actual: %s";
                log.error(msg, Mail.class, messageObject.getClass());
                return;
            }

            Mail smtpMessage = (Mail) jmsObjectMessage.getObject();
            chain.processMail(smtpMessage);
            try {
                long reference = jmsObjectMessage.getLongProperty(Mail.REFERENCE);
                mgr.removeReference(reference, smtpMessage.getMailBody());
            } catch (Exception e) {
                log.warn("Unable to find reference, body can not be automatically deleted");
            }
        } catch (JMSException e) {
            log.error(e, "Failed to process message, message lost.");
        }

    }

}
