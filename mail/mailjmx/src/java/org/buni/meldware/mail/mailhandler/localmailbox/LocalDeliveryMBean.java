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
package org.buni.meldware.mail.mailhandler.localmailbox;

import org.buni.meldware.mail.MailListener;
import org.buni.meldware.mail.mailbox.MailboxService;

/**
 *
 * @author Michael Barker
 * @author Andrew C. Oliver
 * @version $Revision: 1.2 $
 */
public interface LocalDeliveryMBean extends MailListener {

    public void setMailboxManager(MailboxService mailboxManager);

    public void setRouter(MailListener router);
    
    void setParseMime(boolean isParseMime);
    
    boolean getParseMime();
}
