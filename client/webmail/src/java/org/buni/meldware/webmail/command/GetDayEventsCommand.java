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
import java.util.Calendar;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.buni.meldware.calendar.data.Invite;
import org.buni.meldware.calendar.interfaces.PIMService;
import org.buni.meldware.calendar.interfaces.PIMServiceUtil;
import org.buni.meldware.mail.userapi.MailSender;

/**
 * @author aron
 * 
 */
public class GetDayEventsCommand extends BaseCommand {

    public static final String COMMAND_NAME = "getDayEvents";

    /**
     * @param sender
     */
    public GetDayEventsCommand(MailSender sender) {
        super(sender);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.buni.meldware.mail.webmail.command.BaseCommand#execute(javax.servlet.http.HttpServletRequest,
     *      java.io.PrintWriter)
     */
    @Override
    public String execute(HttpServletRequest request, PrintWriter out) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(request.getParameter("day")));
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 5);
        Date startDate = calendar.getTime();
        
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 55);
        Date endDate = calendar.getTime();
        
        log.debug("Get Day events from: " + startDate + "until: " + endDate);
        try {
            PIMService pim = PIMServiceUtil.getHome().create();
            Invite[] invites = pim.listInvites(startDate, endDate);

            writeEvents(request, out, invites);
            out.println("<complete/>");
            return Command.SUCCESS;
        } catch (Exception e) {
            this.printError("Failed to add contacts", e, out);
            return Command.ERROR;
        }
    }
}
