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
package org.buni.meldware.mail.smtp.handlers;

import java.util.HashMap;
import java.util.Map;

/**
 * This class forms a handler collection which allows the caller to receive a statically held instance
 * of the handler Map.  Handlers are singleton instances in nature delegating all session information
 * up to the Protocol handler.  Handlers are mapped in such a way that the key value from their "COMMAND"
 * member determines their use. 
 * 
 * @author Andrew C. Oliver
 * @author Kabir Khan 
 * @version $Revision: 1.2 $
 */
public class SMTPHandlers {
    private static Map<String,SMTPHandler> handlers;

    /**
     * return the map of handlers
     * @return Map containing the handlers mapped to their keys
     */
    public static Map instance() {
        if (handlers == null) {
            constructHandlers();
        }
        return handlers;
    }

    /**
     * static internal initializer which is called to construct the map of handlers in the event the
     * mapping is null.
     */
    private static synchronized void constructHandlers() {
        if (handlers == null) {
            handlers = new HashMap<String,SMTPHandler>();

            handlers.put(CmdHELO.COMMAND, new RequireSTARTTLSProxy(new CmdHELO()));

            handlers.put(CmdEHLO.COMMAND, new CmdEHLO());

            handlers.put(CmdQUIT.COMMAND, new CmdQUIT());

            handlers.put(CmdMAIL.COMMAND, new RequireSTARTTLSProxy(new CmdMAIL()));

            handlers.put(CmdRCPT.COMMAND, new RequireSTARTTLSProxy(new CmdRCPT()));

            handlers.put(CmdAUTH.COMMAND, new RequireSTARTTLSProxy(new CmdAUTH()));

            handlers.put(CmdDATA.COMMAND, new RequireSTARTTLSProxy(new CmdDATA()));

            handlers.put(CmdSTARTTLS.COMMAND, new CmdSTARTTLS());
            
            handlers.put(CmdRSET.COMMAND, new CmdRSET());

			handlers.put(CmdNOOP.COMMAND, new CmdNOOP());

 			handlers.put(CmdHELP.COMMAND, new CmdHELP());

 			handlers.put(CmdVRFY.COMMAND, new CmdVRFY());
 			
 			handlers.put(CmdEXPN.COMMAND, new CmdEXPN());
       }
    }
}

