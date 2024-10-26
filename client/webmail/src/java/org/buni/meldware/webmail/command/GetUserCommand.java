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
import java.rmi.RemoteException;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.buni.meldware.calendar.data.Address;
import org.buni.meldware.calendar.interfaces.PIMService;
import org.buni.meldware.calendar.interfaces.PIMServiceUtil;
import org.buni.meldware.calendar.session.exception.DuplicateUserAddressException;
import org.buni.meldware.calendar.session.exception.UserAddressUnknownException;
import org.buni.meldware.calendar.session.exception.UserUnknownException;
import org.buni.meldware.common.preferences.ProfileConstants;
import org.buni.meldware.common.preferences.UserProfile;
import org.buni.meldware.common.preferences.UserProfileService;
import org.buni.meldware.mail.userapi.MailSender;

/**
 * This is a command to retrieve the user's contact list
 * 
 * @author Aron Sogor
 */
public class GetUserCommand extends BaseCommand {
    
    public UserProfileService profileService;
    /**
     * @param sender
     */
    public GetUserCommand(MailSender sender) {
        super(sender);
    }

    public static final String COMMAND_NAME = "getUser";

@Override
    public String execute(HttpServletRequest request, PrintWriter out) {
        try {
                PIMService pim = null;
                String address = null;
                UserProfile profile = null;
                
                try {
                    pim = PIMServiceUtil.getHome().create();
                    address = pim.getUserAddress();
                    profile = profileService.findProfile(request.getUserPrincipal().getName());
                 
                } catch (UserUnknownException e) {
                    // auto provison the user
                    address = needUserContact(pim, out, request);
                } catch (UserAddressUnknownException e) {
                    address = needUserContact(pim, out, request);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                out.println("<user>");
                writeContact(out, address,true);
                Set<String> aliases = profile.getAliases();
                if (aliases != null) {
                    for (String alias : aliases) {
                        if(alias.indexOf("@") > -1) {
                            out.print("<alias>" + alias + "</alias>");
                        }
                    }
                }
                String timezone = profile.getPreference(ProfileConstants.TIME_ZONE);
                timezone = timezone == null ? "" : timezone;
                out.println("<preferences>");
                out.println("<timeZone>" + timezone
                        + "</timeZone>");
                out.println("</preferences>");
                out.println("<defaultAlias>"+profile.getDefaultAlias()+"</defaultAlias>");
                out.println("<username>"+profile.getUsername()+"</username>");
                
                out.println("</user>");
            return SUCCESS;
        } catch (Exception e) {
            this.printError("Error loading contacts",e,out);
            return ERROR;
        }
    }    /**
             * @param out
             */
    private String needUserContact(PIMService pim, PrintWriter out,
            HttpServletRequest request) throws UserUnknownException,RemoteException,DuplicateUserAddressException{
        String userAddress = new String();
        String defaultAlias = "nodefaultAlias";
        
        List<String> aliases = sender.getAliases(request.getUserPrincipal().getName());
        if ((aliases != null)&&(aliases.size()>0)) {
            defaultAlias = aliases.get(0);
        }
        
        pim.addUserAddress(userAddress);
        return userAddress;        
    }
}
