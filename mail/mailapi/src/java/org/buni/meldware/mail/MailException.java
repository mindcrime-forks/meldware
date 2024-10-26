/**
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
package org.buni.meldware.mail;

/**
 * Top level Meldware Mail Exception. All exceptions defined in MeldwareMail should
 * inherit from this. Preferably from one of its derived classes
 * MailSystemException or MailApplicationException.
 * <p>
 * System exceptions are generally unrecoverable. This include things like
 * SQLExceptions which should be wrapped.
 * <p>
 * Application exception are ones that can be handled. E.g.
 * MailboxNotFoundExcetion.
 * <p>
 * It is recommended when throwing exceptions that you use a narrow definition
 * for the exception as possible. This will allow callers to trap specific
 * exceptions that are throw and handle the errors differently. Even though
 * exceptions are runtime based, application exceptions at least should be
 * declared in the throws clause of the method.
 * 
 * @author Michael Barker <mailto:mike@middlesoft.co.uk>
 * @version $Revision: 1.2 $
 */
public class MailException extends RuntimeException {

    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 3257566213403063605L;

    /**
     * 
     */
    public MailException() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @param arg0
     */
    public MailException(String arg0) {
        super(arg0);
        // TODO Auto-generated constructor stub
    }
    
    public MailException(String msg, Object...args) {
        super(String.format(msg, args));
    }

    /**
     * @param arg0
     * @param arg1
     */
    public MailException(String arg0, Throwable arg1) {
        super(arg0, arg1);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param arg0
     */
    public MailException(Throwable arg0) {
        super(arg0);
        // TODO Auto-generated constructor stub
    }
}
