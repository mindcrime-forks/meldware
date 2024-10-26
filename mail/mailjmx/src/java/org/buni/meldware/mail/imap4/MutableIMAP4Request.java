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
package org.buni.meldware.mail.imap4;

import java.io.InputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.buni.meldware.mail.Protocol;
import org.buni.meldware.mail.Request;
import org.buni.meldware.mail.imap4.commands.AbstractImapCommand;

/**
 * This represents a mutable version of IMAP4Request and Request.
 * 
 * @author Eric Daugherty
 * @author Thorsten Kunz
 * @author Andrew C. Oliver <acoliver ot jboss dat org>
 * @version $Revision: 1.3 $
 */
public class MutableIMAP4Request implements IMAP4Request, Request {

    private IMAP4ProtocolInstance protocol;

    private List<String> arguments;

    private String prefix;

    private InputStream stream;

    private AbstractImapCommand command;

    private Socket socket;

    private String commandString;

    /**
     * Constructs a new MutableIMAP4Request for the passed in protocol and input
     * stream
     * 
     * @param instance IMAP4Protocol instance
     * @param stream instance
     */
    public MutableIMAP4Request(IMAP4ProtocolInstance instance,
            InputStream stream) {
        this.stream = stream;
        this.protocol = instance;
        this.arguments = new ArrayList<String>(2);
    }

    /**
     * get the name of the command to be processed. This will be used to match a
     * handler. (i.e. LOGIN, LIST, LSUB)
     */
    public AbstractImapCommand getCommand() {
        return this.command;
    }

    /**
     * @return iterator for all arguments
     */
    public Iterator arguments() {
        return arguments.iterator();
    }

    /**
     * Returns an array of Strings that contains the arguments for the current
     * command.
     * 
     * @return array of 0 or more strings. Never null.
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
     * @return input stream of the request.
     */
    public InputStream getInputStream() {
        return stream;
    }

    /**
     * @return address of remote host
     */
    public String getRemoteAddr() {
        return socket.getInetAddress().getHostName() + " ("
                + socket.getInetAddress().getHostAddress() + ")";
    }

    /**
     * set the command to be processed
     * 
     * @param strCommand IMAP4 command such as LOGIN or LIST
     */
    public void setCommand(AbstractImapCommand command) {
        this.command = command;
    }

    /**
     * set the socket
     * 
     * @param socket which originates this request (should be used to get
     *           information such as address)
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
     * For SMTP this will generally just be one String value which will be parsed
     * into multiple arguments.
     * 
     * @param argument modifying the command (for instance HELO mymailserver,
     *           mymailserver would be the argument)
     */
    public void addArgument(String argument) {
        arguments.add(argument);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.buni.meldware.mail.imap4.handlers.IMAP4Request#getTag()
     */
    public String getTag() {
        return prefix;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.buni.meldware.mail.imap4.handlers.IMAP4Request#setTag(java.lang.String)
     */
    public void setTag(String prefix) {
        this.prefix = prefix;
    }

    public void setCommandString(String commandString) {
        this.commandString = commandString;
    }
    
    public String getCommandString() {
        return commandString;
    }

}
