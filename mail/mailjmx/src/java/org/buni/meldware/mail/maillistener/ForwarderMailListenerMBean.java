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

import org.buni.meldware.mail.MailException;
import org.buni.meldware.mail.message.Message;
import org.w3c.dom.Element;

/**
 * @author <a href="mailto:tom@jboss.org">Tom Elrod</a>
 * @version $Revision: 1.1 $
 */
public interface ForwarderMailListenerMBean {
    // required to be an mbean service
    void create();

    void start();

    void stop();

    void destroy();

    void setConfiguration(Element xml);

    Message send(Message msg) throws MailException;
}
