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
import java.net.Socket;
import java.util.Map;

/**
 * The protocol is an abstraction for attaching bi-directional stateful
 * protocols to the "Server". The two most important artifacts of this contract
 * are the properties and states as well as the difference. Properties are
 * intended to be passed in upon construction of a protocol instance, if the
 * instance is pooled then these are only passed in upon construction or in the
 * event of a server reconfiguration of some kind. State variables are used
 * throughout the lifecycle of a user connection and change or are cleared as
 * needed. These represent the conversational state with the client and are much
 * like session variables.
 * 
 * @author Andrew C. Oliver
 * @version $Revision: 1.4 $
 */
public interface Protocol {

    /**
     * @return get the key name for the protocol
     */
    String getName();

    /**
     * greet the client upon connection
     * 
     * @param stream
     *            output stream of the client
     * @throws IOException
     *             in the event the OutputStream cannot be written to for some
     *             reason
     */
    void greet(OutputStream stream) throws IOException;

    /**
     * parse an incoming request by pulling the "command" off of the input
     * stream and constructing a request object of some kind.
     * 
     * @param stream
     *            the input stream to process the request from
     * @param socket
     *            the socket to retrieve user information from (remote address,
     *            etc)
     * @return Request object representing the incomming request.
     * @throws IOException
     *             denoting a failure to read the request from the input stream
     */
    Request parseRequest(InputStream stream, Socket socket) throws IOException;

    /**
     * handle a request including any subsequent conversation required on the
     * output stream then construct a response object representing any
     * information about the process.
     * 
     * @param stream
     *            OutputStream in which to contact the client.
     * @param request
     *            representing the protocol command from the client.
     * @return Response object providing information about the processing
     */
    Response handleRequest(OutputStream stream, Request request);

    /**
     * In the event an IOError is thrown, the server will delegate teh "what to
     * do" to this method. It cannot assume that the outputstream is valid and
     * cannot throw an exception (it needs to handle the error possibly by
     * relinquishing resources and giving up)
     * 
     * @param stream
     *            in which to contact the client
     * @return Response indicating any information about processing the error.
     */
    Response handleIOError(OutputStream stream);

    /**
     * retrieve a conversational state variable (session variable)
     * 
     * @param name
     *            of the state variable
     * @return value of the variable
     */
    Object getState(String name);

    /**
     * set the value of a conversational state variable (session variable)
     * 
     * @param name
     *            of the variable
     * @param value
     *            of the variable
     */
    void setState(String name, Object value);

    /**
     * reset all state to nothing
     */
    void resetState();

    /**
     * set configruation information for this protocol (should only need to do
     * this on construction or server reconfig)
     * 
     * @param properties
     */
    void setProperties(Map properties);

    /**
     * handle cleanup is called on quit or connection drop. It must not throw
     * exceptions.
     * @param out 
     */
    void handleCleanup(OutputStream out);


}
