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

import java.util.ArrayList;
import java.util.List;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.management.ObjectName;
import javax.naming.InitialContext;

import org.buni.meldware.mail.MailListener;
import org.buni.meldware.mail.maillist.MailListManager;
import org.buni.meldware.mail.message.Mail;
import org.buni.meldware.mail.message.MailAddress;
import org.buni.meldware.mail.message.MailBodyManager;
import org.buni.meldware.mail.message.Message;
import org.buni.meldware.mail.util.MMJMXUtil;
import org.jboss.aspects.tx.Tx;
import org.jboss.aspects.tx.TxType;
import org.jboss.system.ServiceMBeanSupport;

/**
 * MailListJMSListener - sends mails to a special queue for de-listifying mail
 * At current this needs to be early in the chain.  the mails are then put on 
 * a seperate queue to be handled by the mail list delivery listener.
 * 
 * @author Andrew C. Oliver
 * @version $Revision: 1.6 $
 */
public class MailListJMSMailListener extends ServiceMBeanSupport implements
        MailListJMSMailListenerMBean, MailListener {

    private ObjectName mlmgr;

    /**
     * destination queue/topic name
     */
    private String destinationName;

    private String connectionFactoryName;
    
    /**
     * Connection Factory from JNDI
     */
    private ConnectionFactory cf;
    
    private Destination destination;
    
    private MailBodyManager mgr;

    private boolean isJTA;
    
    public MailListJMSMailListener(String connectionFactoryName, boolean isJTA) {
        this.connectionFactoryName = connectionFactoryName;
        this.isJTA = isJTA;        
    }
    
    public String getConnectionFactoryName() {
        return connectionFactoryName;
    }

    /**
     * Start the lister by connecting to JMS.
     */
    public void startService() throws Exception {
        InitialContext ctx = new InitialContext();
        
        cf = (ConnectionFactory) ctx.lookup(connectionFactoryName);
        destination = (Destination) ctx.lookup(destinationName);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jboss.system.ServiceMBeanSupport#stopService()
     */
    public void stopService() throws Exception {
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.buni.meldware.mail.maillistener.MailListJMSMailListenerMBean#setDestination(java.lang.String)
     */
    public void setDestination(String postDest) {
        this.destinationName = postDest;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.buni.meldware.mail.maillistener.MailListJMSMailListenerMBean#getDestination()
     */
    public String getDestination() {
        return destinationName;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.buni.meldware.mail.MailListener#send(org.buni.meldware.mail.message.Message)
     */
    public Message send(Message msg) {
        try {
            MailListManager mgr = (MailListManager) MMJMXUtil.getMBean(
                    mlmgr, MailListManager.class);
            Mail mail = (Mail) msg;

            ArrayList<MailAddress> notHandledAddrs = new ArrayList<MailAddress>();
            boolean isMailList = false;

            for (MailAddress address : mail.getTo()) {
                if (mgr.findList(address) == null) {
                    notHandledAddrs.add(address);
                } else {
                    isMailList = true;
                }
            }

            if (isMailList) {
                sendMessage(msg);

                int size = notHandledAddrs.size();
                if (mail.getTo().size() == (size+1)) {
                    // We handled all the addresses
                    // TODO: return null, or WrappedMail with empty to array?
                    return null;
                } else {
                    // Create new Mail with all addresses not for list
                    WrappedMail wmail = new WrappedMail(mail, notHandledAddrs);
                    return wmail;
                }
            } else {
                // Message was not for the list, return the original message
                return msg;
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * does the real work for sendMessage for any JMS destination type.
     * 
     * @param msg
     *            the message to be sent
     * @throws Exception
     *             if there is any problem (JMS related or otherwise)
     */
    @Tx(TxType.REQUIRED)
    private void sendMessage(Message msg) throws Exception {
        Mail mail = (Mail) msg;
        
        Connection tc = null;
        Session ts = null;
        MessageProducer sender = null;
        
        try {
            tc = cf.createConnection();
            ts = tc.createSession(false, 0);
            sender = ts.createProducer(destination);

            ObjectMessage om = ts.createObjectMessage(mail);
            long reference = mgr.addReference(mail.getMailBody());
            om.setLongProperty(Mail.REFERENCE, reference);
            sender.send(om);
            sender.close();
        } finally {
            if (!isJTA) {
                ts.commit();
            }
            close(sender);
            close(ts);
            close(tc);
        }
    }
    
    private static void close(Connection cn) {
        if (cn != null) {
            try {
                cn.close();
            } catch (Throwable t) {}
        }
    }

    private static void close(Session cn) {
        if (cn != null) {
            try {
                cn.close();
            } catch (Throwable t) {}
        }
    }
    
    private static void close(MessageProducer cn) {
        if (cn != null) {
            try {
                cn.close();
            } catch (Throwable t) {}
        }
    }
    

    /*
     * (non-Javadoc)
     * 
     * @see org.buni.meldware.mail.maillistener.MailListJMSListenerMBean#getMailListManager()
     */
    public ObjectName getMailListManager() {
        return mlmgr;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.buni.meldware.mail.maillistener.MailListJMSListenerMBean#setMailListManager(javax.management.ObjectName)
     */
    public void setMailListManager(ObjectName on) {
        this.mlmgr = on;
    }

    public MailBodyManager getMailBodyManager() {
        return mgr;
    }

    public void setMailBodyManager(MailBodyManager mgr) {
        this.mgr = mgr;
    }

}

/**
 * Contains the original mail, with all addresses that were not for maillist
 * 
 * @author <a href="kabirkhan@bigfoot.com">Kabir Khan</a>
 */
class WrappedMail extends Mail {

    private static final long serialVersionUID = 4049360803218207796L;

    List<MailAddress> to;

    WrappedMail(Mail mail, List<MailAddress> to) {
        super(mail);
        this.to = to;
    }

    public List<MailAddress> getTo() {
        return to;
    }
}
