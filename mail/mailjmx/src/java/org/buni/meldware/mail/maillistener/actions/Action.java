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
package org.buni.meldware.mail.maillistener.actions;

import org.buni.meldware.mail.message.EnvelopedAddress;
import org.buni.meldware.mail.message.Mail;

/**
 * Action is basically what to do if a serverside filter matches.
 * Generally these just set the target folder on the EnvelopedAddress
 * object associated with a mail.
 *
 * @author Andrew C. Oliver (acoliver ot buni dat org)
 *
 * @see org.buni.meldware.maillistener.actions.CopyAction
 * @see org.buni.meldware.maillistener.actions.MoveAction
 * @see org.buni.meldware.maillistener.actions.DeleteAction
 * 
 * @version $Revision: 1.2 $
 */
public interface Action {
    boolean perform(String[] params, Mail mail, EnvelopedAddress address);
}
