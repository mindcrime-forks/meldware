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

import java.util.ArrayList;
import java.util.List;

import org.buni.meldware.mail.message.EnvelopedAddress;
import org.buni.meldware.mail.message.Mail;

/**
 * Remove inbox from the target folders if it exists.  Add the target folder in its stead.
 * 
 * @author Andrew C. Oliver (acoliver ot buni dat org)
 * @version $Revision: 1.1 $
 */
public class MoveAction implements Action {
    public static String NAME = "Move To";

    @SuppressWarnings("unchecked")
    public boolean perform(String[] params, Mail mail, EnvelopedAddress address) {
        List<String> targets = (List<String>)address.getAttribute("targetFolder");
        if (targets == null) {
            targets = new ArrayList<String>();
            address.setAttribute("targetFolder",targets);
        } else if (targets.contains("/INBOX")) {
            targets.remove("/INBOX");
        }
        if (!targets.contains(params[0])) {
            targets.add(params[0]);
        }
        return true;
    }

}
