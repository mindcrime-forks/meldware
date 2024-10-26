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

import org.buni.meldware.calendar.interfaces.PIMService;
import org.buni.meldware.calendar.interfaces.PIMServiceUtil;
import org.buni.meldware.mail.userapi.MailSender;

/**
 * @author aron
 *
 */
public class AddInvitesCommand extends BaseCommand {
	
	public static final String COMMAND_NAME = "addInvites";

	/**
     * @param sender
     */
    public AddInvitesCommand(MailSender sender) {
        super(sender);
    }

    /* (non-Javadoc)
	 * @see org.buni.meldware.mail.webmail.command.BaseCommand#execute(javax.servlet.http.HttpServletRequest, java.io.PrintWriter)
	 */
	@Override
	public String execute(HttpServletRequest request, PrintWriter out) {
		String users[] = request.getParameter("users").split(",");
        long eventId = Long.parseLong(request.getParameter("eventId"));
        try {
            PIMService pim = PIMServiceUtil.getHome().create();
            pim.addInvitesToEvent(eventId,users);
            out.println("<complete/>");
            return Command.SUCCESS;
        } catch (Exception e) {
            this.printError("Failed to add contacts", e, out);
            return Command.ERROR;
        }
	}

}
