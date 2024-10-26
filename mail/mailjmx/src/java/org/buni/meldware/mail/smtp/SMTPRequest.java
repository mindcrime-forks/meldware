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

import java.io.InputStream;
import java.util.Iterator;

import org.buni.meldware.mail.Protocol;
import org.buni.meldware.mail.Request;

/**
 * The specific SMTPRequest type extends the basic request with various SMTP-specific request properties
 * 
 * @author Andrew C. Oliver
 * @author Eric Daugherty
 * @version $Revision: 1.1 $
 */
public interface SMTPRequest extends Request {

    /**
     * SMTP Request command such as HELO, EHLO, QUIT, MAIL, etc.
     * @return command to be processed
     */
    public String getCommand();

    /**
     * arguments to the command
     * @see #getCommand()
     * @return arguments iterator
     */
    public Iterator arguments();

    /**
     * Returns an array of Strings that contains
     * the arguments for the current command.
     *
     * @return array of 0 or more strings.  Never null.
     */
    public String[] getArguments();

    /**
     * @return protocol instance which this request is attached to
     */
    public Protocol getProtocol();

    /**
     * @return input stream from which this request was parsed.
     */
    public InputStream getInputStream();
}
