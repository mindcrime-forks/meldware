/**
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
package org.buni.meldware.mail;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.buni.meldware.mail.util.io.IOUtil;

/**
 * Abstract Protocol provides a configurable stateful abstraction for protocols.
 * State is the state of an instance. Properties are configurationation
 * parameters.
 * 
 * @author Andrew C. Oliver
 * @author Michael.Barker
 * @version $Revision: 1.3 $
 */
public abstract class AbstractProtocol implements Protocol {
    //private static final Logger log = Logger.getLogger(AbstractProtocol.class);

    private static final String ENCODING = "US-ASCII";

    /**
     * the serverThread we're assigned to (we may need to release it)
     */
    private ServerThread serverThread;

    /**
     * State is the temporary status of this instance.
     */
    private Map<String,Object> state;

    /**
     * properties are setup parameters...things that don't ususally change very
     * often...like the server name..
     */
    protected Map properties;

    /**
     * determines if the connection is secure, that is, the SSL handshake has
     * completed. This happens after the client says STARTTLS
     */
    protected boolean secure = false;

    /**
     * Constructs an abstract protocol with empty properties and state.
     */
    public AbstractProtocol() {
        this.state = new HashMap<String,Object>();
        this.properties = new HashMap();
    }

    /**
     * retrieve a state value for this protocol instance (generally specific
     * conversational state)
     * 
     * @param name
     *            of state value
     * @return state value
     */
    public Object getState(String name) {
        return state.get(name);
    }

    /**
     * set a state variable.
     * 
     * @param name
     *            of state variable
     * @param value
     *            of state variable
     */
    public void setState(String name, Object value) {
        state.put(name, value);
    }

    /**
     * reset the conversational state (ditch all state variables)
     */
    public void resetState() {
        state = new HashMap<String,Object>();
    }

    /**
     * set the configuration (should only be done upon construction)
     * 
     * @param properties
     *            map containing key value pairs
     */
    public void setProperties(Map properties) {
        this.properties = properties;
    }

    /**
     * Reads the input stream until it encounters endline charecters, or the
     * input stream closes.
     * 
     * @param stream
     *            InputStream to read
     * @return input command without endline characters.
     * @throws IOException
     */
    protected String readCommand(InputStream stream) throws IOException {
        StringBuilder sb = new StringBuilder();
        IOUtil.appendLine(sb, stream, ENCODING);
        return sb.toString();        
    }

    /**
     * seperates the argument from the command
     * 
     * @param commandstring
     *            argument + command
     * @return string array with the first element being the command, subsequent
     *         (always only 1) being the arguments
     */
    protected String[] parseCommand(String commandstring) {
        String command = commandstring;
        String argument = null;
        int spaceIndex = command.indexOf(" ");
        if (spaceIndex > 0) {
            argument = command.substring(spaceIndex + 1);
            command = command.substring(0, spaceIndex);
        }
        command = command.toUpperCase(Locale.US);
        return new String[] { command, argument };
    }

    /**
     * @return ServerThread associated with this instance (we'll need to release
     *         it in the pool)
     */
    public ServerThread getServerThread() {
        return serverThread;
    }

    /**
     * used by the pool to associate our serverThread instance
     * 
     * @param serverThread
     *            associated with this instance
     */
    public void setServerThread(ServerThread serverThread) {
        this.serverThread = serverThread;
    }

    /**
     * @return whether or not we're using TLS
     */
    public boolean isSecure() {
        return secure;
    }

    /**
     * whether to use TLS
     * 
     * @param secure
     *            use TLS or not
     */
    public void setSecure(boolean secure) {
        this.secure = secure;
    }

    /**
     * Default implementation does nothing.
     */
    public void sendForceClose(OutputStream out) throws IOException {
        // NO-OP
    }

}
