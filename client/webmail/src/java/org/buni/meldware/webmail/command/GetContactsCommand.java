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
package org.buni.meldware.webmail.command;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;

import org.buni.meldware.calendar.data.Address;
import org.buni.meldware.calendar.interfaces.PIMService;
import org.buni.meldware.calendar.interfaces.PIMServiceUtil;
import org.buni.meldware.mail.userapi.MailSender;

/**
 * This is a command to retrieve the user's contact list
 * 
 * @author Aron Sogor
 */
public class GetContactsCommand extends BaseCommand {
    
    public static final String COMMAND_NAME = "getContacts";
    
    /**
     * @param sender
     */
    public GetContactsCommand(MailSender sender) {
        super(sender);
    }

    @Override
    public String execute(HttpServletRequest request, PrintWriter out) {
        try {
            PIMService pim = PIMServiceUtil.getHome().create();
            String[] addresses = pim.listAddresses(0, 100, 1);
            for (int acnt = 0; acnt < addresses.length; acnt++) {
           /*     writeContact(out, addresses[acnt], (addresses[acnt].getOwner()
                        .getUserName().equals(request.getUserPrincipal().getName()) && (addresses[acnt]
                        )));
                        */
                //TODO ACO temporarily disabled
            }
            pim.remove();
            return SUCCESS;
        } catch (Exception e) {
            this.printError("Error loading contacts",e,out);
            return ERROR;
        }
    }

}
