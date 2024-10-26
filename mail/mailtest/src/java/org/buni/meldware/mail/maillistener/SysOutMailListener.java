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

import java.io.IOException;
import java.io.InputStream;

import org.buni.meldware.mail.MailException;
import org.buni.meldware.mail.MailListener;
import org.buni.meldware.mail.message.Mail;
import org.buni.meldware.mail.message.MailBodyManager;
import org.buni.meldware.mail.message.Message;
import org.buni.meldware.mail.util.io.Copier;
import org.buni.meldware.mail.util.io.SimpleCopier;
import org.jboss.aspects.tx.Tx;
import org.jboss.aspects.tx.TxType;
import org.jboss.system.ServiceMBeanSupport;

/**
 * JMSMailListener - proof of concept code which posts mails to queues or topics.  It has both 
 * onServer and offServer settings thus you can divide the processing up such that posts for users
 * in domains handled by this SMTP server are "onServer" and handled by one set of MDBeans and the 
 * others are handled by the offServer queue and processed seperately.  If you don't want them 
 * seperate, just set them both to the same queue.
 *
 * @author Andrew C. Oliver
 */
public class SysOutMailListener
    extends ServiceMBeanSupport
    implements SysOutMailListenerMBean, MailListener {

    

    private MailBodyManager mgr;


    public void startService() throws Exception {
  
    }
    
    public void stopService() throws Exception {
        
    }
 
 
    public void setMailBodyManager(MailBodyManager mgr) {
        this.mgr = mgr;
    }
    

    /* (non-Javadoc)
     * @see org.buni.meldware.mail.MailListener#send(org.buni.meldware.mail.Message)
     */
    
    public Message send(Message msg) throws MailException {
        System.out.println("Send message called");
        Mail mail = (Mail)msg;
        
        try {
			printMail(mail);
		} catch (IOException e) {
			throw new MailException(e);
		}
        
        return msg;
    }
    
    @Tx(TxType.REQUIRED)
    private void printMail(Mail m) throws IOException {
        Copier c = new SimpleCopier();
        InputStream in = m.getRawStream(mgr);
        c.copy(in, System.out, 8192);
    }
    
}
