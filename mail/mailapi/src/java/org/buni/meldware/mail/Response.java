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
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Response is a fragment of the command pattern. Protocols have requests and
 * responses. For SMTP, the response is mostly just a transport between
 * handlers. For other protocols this might have more meaning.
 * 
 * @author Andrew C. Oliver
 * @version $Revision: 1.1 $
 */
public interface Response {

    /**
     * retrieve the request object associated with this response
     * 
     * @return request object associated with the response
     */
    public Request getRequest();

    /**
     * retrieve the output stream for writing this response
     * 
     * @return output stream
     * @throws IOException
     *             in the event there is some problem in retreiving the
     *             outputstream
     */
    public OutputStream getOutputStream() throws IOException;

    /**
     * get a printwriter for the outputstream
     * 
     * @return printwriter
     * @throws IOException
     *             in the event a print writer could not be created
     */
    public PrintWriter getWriter() throws IOException;

    /**
     * Get the associated protocol instance
     * 
     * @return protocol instance associates with this response
     */
    public Protocol getProtocol();

    /**
     * Should this response terminate the conversation
     * 
     * @return boolean on whether to quit or not
     */
    public boolean isFinish();

    /**
     * primarily for TLS requests. carries the socket override to the connection
     * handler
     */
    public void setSocketOverride(Socket socket);

    /**
     * did the command request to override the socket?
     */
    public boolean isSocketOverride();

    /**
     * primarily for TLS requests. carries the socket override to the connection
     * handler
     */
    public Socket getSocketOverride();
}
