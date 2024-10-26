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
import java.io.Writer;
import java.net.Socket;

import org.jboss.logging.Logger;

/**
 * AbstractResponse provides a base implementation of the Response interface.
 * <p>
 * This class should be extended to provide concrete implementations for each
 * protocol.
 * 
 * @author Eric Daugherty
 * @author Andrew C. Oliver (acoliver@jboss.org)
 * @version $Revision: 1.2 $
 */
public class AbstractResponse implements Response {

    protected Request request;

    protected OutputStream out;

    protected Protocol protocol;

    protected boolean finish;

    private Socket socketOverride;

    private PrintWriter writer;

    public Request getRequest() {
        return request;
    }

    public OutputStream getOutputStream() throws IOException {
        return out;
    }

    public PrintWriter getWriter() throws IOException {
        if (this.writer == null) {
            this.writer = new WrappedWriter(new PrintWriter(out));
        }
        return this.writer;
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public boolean isFinish() {
        return finish;
    }

    /**
     * used to get a writer if we can't (yet) get the log. Basically used only
     * by greet() should stay that way.
     * 
     * @param print
     *            writer to wrap
     * @return WrappedWriter
     */
    public static PrintWriter getWrappedWriter(PrintWriter p) {
        return new WrappedWriter(p);
    }

    public void setSocketOverride(Socket socket) {
        this.socketOverride = socket;
    }

    public boolean isSocketOverride() {
        boolean retval = this.socketOverride == null ? false : true;
        return retval;
    }

    public Socket getSocketOverride() {
        return this.socketOverride;
    }

}

/**
 * used to log the server responses to the conversational log (not log4j
 * presently)
 * 
 * @author Andrew C. Oliver <acoliver et jboss det org>
 */
class WrappedWriter extends PrintWriter {

    /**
     * log we're writing to, should be the serverLog
     */
    private Logger log = Logger.getLogger(WrappedWriter.class);

    /**
     * SMTP dictates this EOL sequence
     */
    protected String lineSeparator = "\r\n";

    /**
     * constructor for wrapped writer which sets up the printwriter to use and
     * the serverLog to use
     * 
     * @param writer
     *            to wrap
     * @param log
     *            to log the responses to
     */
    public WrappedWriter(Writer writer) {
        super(writer, false);
    }

    public void println(String arg0) {
        super.write(arg0); // some clients barf if this isn't the line sep
        super.write(lineSeparator);
        if (log.isTraceEnabled()) {
            log.trace(arg0);
        }
    }

    public void flush() {
        super.flush();
    }

}
