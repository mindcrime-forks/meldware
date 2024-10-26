/**
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
package org.buni.meldware.mail;

import javax.management.ObjectName;

import org.buni.meldware.mail.message.Message;
import org.jboss.system.ServiceMBean;
import org.w3c.dom.Element;

/**
 * MailListenerChain is a grouping of mail listeners. They process mails in the
 * order of the chain.
 * 
 * @author Andrew C. Oliver
 * @version $Revision: 1.2 $
 */
public interface MailListenerChain extends ServiceMBean {

    public abstract void addListener(ObjectName listenerName);

    public abstract void addListener(ObjectName listenerName, int position);

    public abstract void removeListener(ObjectName listener);

    public abstract void removeListener(int position);

    public abstract int getNumberListeners();

    public abstract String[] listListeners();

    public abstract Element getListeners();

    public abstract void setListeners(Element listeners);

    public abstract Message processMail(Message mail) throws MailException;

}