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
package org.buni.meldware.mail.mailhandler.remote;

import org.buni.meldware.mail.MailListener;
import org.buni.meldware.mail.domaingroup.DomainGroupMBean;
import org.buni.meldware.mail.smtp.sender.SMTPSenderMBean;

/**
 * 
 * @author Andrew C. Oliver
 * @version $Revision: 1.2 $
 */
public interface RemoteDeliveryMBean extends MailListener {

    /**
     * @return Returns the domainGroup.
     */
    DomainGroupMBean getDomainGroup();

    /**
     * @param domainGroup The domainGroup to set.
     */
    void setDomainGroup(DomainGroupMBean domainGroup);

    /**
     * @return Returns the router.
     */
    MailListener getRouter();

    /**
     * @param router The router to set.
     */
    void setRouter(MailListener router);

    /**
     * @return Returns the sender.
     */
    SMTPSenderMBean getSender();

    /**
     * @param sender The sender to set.
     */
    void setSender(SMTPSenderMBean sender);

}
