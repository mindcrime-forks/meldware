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
package org.buni.meldware.mail.smtp;

import java.io.OutputStream;

import org.buni.meldware.mail.AbstractResponse;
import org.buni.meldware.mail.Protocol;

/**
 * SMTPResponse is the SMTP-specific extension to the basic Response which adds SMTP properties and 
 * constructors.
 * 
 * @author Andrew C. Oliver
 */
public class SMTPResponse extends AbstractResponse {

    /**
     * SMTPResponse constructor which creates an instance which is not terminal to the conversation
     * @param request which generated this response
     * @param out stream which the response will be serialized to
     * @param log the serverLog to log to (conversational logging)
     * @param protocol instance which is to be used
     */
    public SMTPResponse(SMTPRequest request, OutputStream out, Protocol protocol) {
        this(request, out, protocol, false);
    }

    /**
     * SMTPResponse constructor which creates an instance which may or may not be terminal
     * @param request which generated this response 
     * @param out stream which the response will be serialized to
     * @param log the serverLog to log to (conversational logging)
     * @param protocol instance which is to be used
     * @param finish whether or not this response terminates the conversation loop
     */
    public SMTPResponse(SMTPRequest request, OutputStream out,
            Protocol protocol, boolean finish) {
        this.request = request;
        this.out = out;
        this.protocol = protocol;
        this.finish = finish;
    }

}
