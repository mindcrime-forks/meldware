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

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.buni.meldware.mail.MailListener;
import org.buni.meldware.mail.message.Message;
import org.buni.meldware.mail.util.MMJMXUtil;

/**
 * Sends a message via a passed into the constructor JMX service with a "send" function.
 *
 * @author Andrew C. Oliver
 */
public class SysOutMailListenerProxy implements MailListener {
    /**
	 * 
	 */
	private static final long serialVersionUID = 3977015137234596914L;
	ObjectName on;
    MBeanServer srv;

    /**
     * constructor used by the MailListener mbean to pass in its ObjectNAme instance 
     * @param objectname of the calling mail listener (for a callback)
     */    
    public SysOutMailListenerProxy(ObjectName objectname) {
           srv = MMJMXUtil.locateJBoss();
           on = objectname;                    
    }

    /* (non-Javadoc)
     * @see org.buni.meldware.mail.MailListener#send(org.buni.meldware.mail.Message)
     */
    public Message send(Message msg) {
        try {
            srv.invoke(on,"send",new Object[] {msg},new String[]{"org.buni.meldware.mail.Message"});
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        //return true;
        return msg;
    }

}
