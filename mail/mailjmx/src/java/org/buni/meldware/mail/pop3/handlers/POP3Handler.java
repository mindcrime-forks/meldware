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

import java.io.IOException;
import java.io.OutputStream;

import org.buni.meldware.mail.MailException;
import org.buni.meldware.mail.pop3.POP3ProtocolInstance;
import org.buni.meldware.mail.pop3.POP3Request;
import org.buni.meldware.mail.pop3.POP3Response;

/**
 * POP3Handler interface defines the method to which requests are passed.  It is called by the
 * POP3Protocol in order to actually handle the request.  New handlers can be added (presently
 * statically, though extensibility is feasible) easily to the POP3Handlers class.
 *
 * @author Eric Daugherty
 * @version $Revision: 1.1 $
 */
public interface POP3Handler {

    /**
     * Handle the processing of the request
     * @param out outputstream in which messages and responses are written
     * @param request which is to be processed
     * @param protocol instance which delegated the request
     * @return Response providing information about the processing of the request.
     * @throws java.io.IOException
     */
    POP3Response handleRequest(OutputStream out, POP3Request request,
            POP3ProtocolInstance protocol) throws IOException, MailException;
}
