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
package org.buni.meldware.mail.pop3.handlers;

import java.util.HashMap;
import java.util.Map;

/**
 * This class forms a handler collection which allows the caller to receive a statically held instance
 * of the handler Map.  Handlers are singleton instances in nature delegating all session information
 * up to the Protocol handler.  Handlers are mapped in such a way that the key value from their "COMMAND"
 * member determines their use.
 *
 * @author Eric Daugherty
 */
public class POP3Handlers {

    private static Map<String,POP3Handler> handlers;

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
            handlers = new HashMap<String,POP3Handler>();

            handlers.put(CmdSTAT.COMMAND, new CmdSTAT());

            handlers.put(CmdLIST.COMMAND, new CmdLIST());

            handlers.put(CmdRETR.COMMAND, new CmdRETR());

            handlers.put(CmdDELE.COMMAND, new CmdDELE());

            handlers.put(CmdNOOP.COMMAND, new CmdNOOP());

            handlers.put(CmdRSET.COMMAND, new CmdRSET());

            handlers.put(CmdQUIT.COMMAND, new CmdQUIT());

            handlers.put(CmdTOP.COMMAND, new CmdTOP());

            handlers.put(CmdUIDL.COMMAND, new CmdUIDL());

            handlers.put(CmdUSER.COMMAND, new CmdUSER());

            handlers.put(CmdPASS.COMMAND, new CmdPASS());

            handlers.put(CmdAPOP.COMMAND, new CmdAPOP());

            handlers.put(CmdSTLS.COMMAND, new CmdSTLS());

            handlers.put(CmdCAPA.COMMAND, new CmdCAPA());
        }
    }
}
