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

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.buni.meldware.mail.Protocol;
import org.buni.meldware.mail.Request;
import org.jboss.logging.Logger;

/**
 * This represents a mutable version of SMTPRequest and Request. 
 * @author Andrew C. Oliver
 * @author Eric Daugherty
 * @version $Revision: 1.3 $
 */
public class MutableSMTPRequest implements Request, SMTPRequest {

    private SMTPProtocolInstance protocol;

    private List<Object> arguments;

    private InputStream stream;

    private String strCommand;

    private Socket socket;

    /**
     * Constructs a new MutableSMTPRequest for the passed in protocol and input stream
     * @param instance SMTPProtocol instance
     * @param stream instance
     */
    public MutableSMTPRequest(SMTPProtocolInstance instance, InputStream stream) {
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
     *
     */
    public void addArgument(Object argument) {
        if (argument != null) {
            arguments.add(argument);
        }
    }

    /**
     * MIKEA: string arguments should not contain whitespace or linefeeds etc...
     */
    public void addArgument(String argument) {
        if (argument != null) {
            arguments.add(argument.replaceAll("[\\r\\n\\t]", "").trim());
        }
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
     * @return input stream of the request. (wrapped in LoggingInputStream)
     */
    public InputStream getInputStream() {
        //return new LoggingInputStream(stream);
        return stream;
    }
}

/**
 * Uses the MailLog (if enabled) to wrap the socket input stream and log the clients input.  This is 
 * primarily useful for non-command based communication (like the actual mail in response to CmdDATA).
 * @todo make the output nicer.
 * @author Andrew C. Oliver <acoliver ot jboss dat org>
 */
class LoggingInputStream extends InputStream {
    private InputStream stream;

    boolean enabled;

    StringBuffer buffer;

    Logger log = Logger.getLogger(LoggingInputStream.class);

    /**
     * THE constructor, stupid
     * @param stream to wrap (socketInputStream ususally)
     * @param log should be the clientLog
     * @param enabled if we want logging or not, generally is clientLog.getEnabled()
     */
    public LoggingInputStream(InputStream stream) {
        this.stream = stream;
        this.enabled = (log != null);
        if (enabled) {
            buffer = new StringBuffer();
        }
    }

    public void close() throws IOException {
        stream.close();
        if (log.isTraceEnabled()) {
            log.trace(escape(buffer.toString()) + "\r\nDONE\r\n");
            buffer = null;
        }
    }

    public synchronized void mark(int arg0) {
        super.mark(arg0);
        if (log.isTraceEnabled() && buffer.length() > 0) {
            log.trace(escape(buffer.toString()));
            buffer = new StringBuffer();
        }
    }

    public int read(byte[] arg0, int arg1, int arg2) throws IOException {
        int result = stream.read(arg0, arg1, arg2);
        if (enabled) {
            buffer.append(new String(arg0, arg1, arg2, "US-ASCII"));
        }
        return result;
    }

    public int read(byte[] arg0) throws IOException {
        int retval = stream.read(arg0);
        if (enabled) {
            buffer.append(new String(arg0, 0, retval, "US-ASCII"));
        }
        return retval;
    }

    public int read() throws IOException {
        int retval = stream.read();
        if (enabled) {
            buffer.append((char) retval);
            if (((char) retval) == '\n') {
                log.info(escape(buffer.toString()));
                buffer = new StringBuffer();
            }
        }
        return retval;
    }

    private String escape(Object o) {
        return o.toString().replaceAll("\\r", "\\\\r").replaceAll("\\n",
                "\\\\n");
    }

}
