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
package org.buni.meldware.mail.pop3;

import java.io.InputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.buni.meldware.mail.Protocol;
import org.buni.meldware.mail.Request;

/**
 * This represents a mutable version of POP3Request and Request.
 *
 * @author Eric Daugherty
 * @version $Revision: 1.1 $
 */
public class MutablePOP3Request implements POP3Request, Request {

    private POP3ProtocolInstance protocol;

    private List<Object> arguments;

    private InputStream stream;

    private String strCommand;

    private Socket socket;

    /**
     * Constructs a new MutableSMTPRequest for the passed in protocol and input stream
     * @param instance POP3Protocol instance
     * @param stream instance
     */
    public MutablePOP3Request(POP3ProtocolInstance instance, InputStream stream) {
        this.stream = stream;
        this.protocol = instance;
        this.arguments = new ArrayList<Object>(2);
    }

    /**
     * set the command to be processed
     * @param strCommand SMTP command such as HELO or EHLO
     */
    public void setCommand(String strCommand) {
        this.strCommand = strCommand;
    }

    /**
     * get the name of the command to be processed.  This will be used to match a handler.
     * (i.e. HELO, EHLO, QUIT)
     */
    public String getCommand() {
        return this.strCommand;
    }

    /**
     * For SMTP this will generally just be one String value which will be parsed into multiple arguments.
     * @param argument modifying the command (for instance HELO mymailserver, mymailserver would be the
     * argument)
     */
    public void addArgument(Object argument) {
        arguments.add(argument);
    }

    /**
     * @return iterator for all arguments (SMTP will be none or one)
     */
    public Iterator arguments() {
        return arguments.iterator();
    }

    /**
     * Returns an array of Strings that contains
     * the arguments for the current command.
     *
     * @return array of 0 or more strings.  Never null.
     */
    public String[] getArguments() {

        // Determine the number of arguments
        int count = 0;
        if (arguments != null) {
            count = arguments.size();
        }

        // Copy the list to a string array, if necessary.
        String[] argumentsArray = new String[count];
        if (count > 0) {
            arguments.toArray(argumentsArray);
        }
        return argumentsArray;
    }

    /**
     * @return the assoicated protocol
     */
    public Protocol getProtocol() {
        return protocol;
    }

    /**
     * set the socket
     * @param socket which originates this request (should be used to get information such as address)
     */
    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    /**
     * we have to do this for TLS
     */
    public Socket getSocket() {
        return this.socket;
    }

    /**
     * @return address of remote host
     */
    public String getRemoteAddr() {
        return socket.getInetAddress().getHostName() + " ("
                + socket.getInetAddress().getHostAddress() + ")";
    }

    /**
     * @return input stream of the request.
     */
    public InputStream getInputStream() {
        return stream;
    }
}
