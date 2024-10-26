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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.net.ssl.SSLSocketFactory;

import org.buni.meldware.mail.AbstractProtocol;
import org.buni.meldware.mail.AbstractResponse;
import org.buni.meldware.mail.MailException;
import org.buni.meldware.mail.Protocol;
import org.buni.meldware.mail.Request;
import org.buni.meldware.mail.Response;
import org.buni.meldware.mail.ServerThread;
import org.buni.meldware.mail.api.Folder;
import org.buni.meldware.mail.api.Hints;
import org.buni.meldware.mail.api.Mailbox;
import org.buni.meldware.mail.mailbox.MailboxService;
import org.buni.meldware.mail.pop3.handlers.POP3Handler;
import org.buni.meldware.mail.userrepository.UserRepository;
import org.jboss.aspects.tx.Tx;
import org.jboss.aspects.tx.TxType;
import org.jboss.logging.Logger;
import org.w3c.dom.Element;

/**
 * Service Proxy created by the MBean which is used by the Server (actually ServerThread) to implement
 * the protocol.
 *
 * @author Eric Daugherty
 * @version $Revision: 1.12 $
 */
public class POP3ProtocolInstance extends AbstractProtocol implements Protocol {

    /**
     * 
     */
    private static final long serialVersionUID = 3832907641444774452L;

    private Logger log = Logger.getLogger(POP3ProtocolInstance.class);

    private String apop = null;

    //
    // The possible connection states
    //

    /**
     * The connection is in AUTHORIZATION state until
     * the user has successfully authenticated with the server
     */
    public static final int STATE_AUTHORIZATION = 0;

    /**
     * The TRANSACTION state allows the user to execute
     * all commands to manipulate the mailbox state.  No
     * changes are saved until it enters the UPDATE state.
     */
    public static final int STATE_TRANSACTION = 1;

    /**
     * The connection enters teh UPDATE state after
     * the QUIT command.  During this state, the mailbox changes
     * are persisted.
     */
    public static final int STATE_UPDATE = 2;

    /**
     * returned from SMTPHandlers.instance();
     */
    private Map handlers;

    /**
     * The current state of the connection.  Values
     * are: AUTHORIZATION, TRANSACTION, and UPDATE
     */
    private int state;

    /**
     * The username for the current session.
     */
    private String username = null;

    private MailboxService mailboxManager = null;

    private POP3Protocol protocol;

    /**
     * create an POP3ProtocolInstance with the proper handlers and the already
     * constructed properties map
     * @param handlers from POP3Handlers.instance();
     * @param properties from POP3Protocol.setProperties()
     */
    public POP3ProtocolInstance(POP3Protocol protocol, Map handlers,
            MailboxService mailboxManager) {
        this.protocol = protocol;
        this.handlers = handlers;
        this.mailboxManager = mailboxManager;
        state = STATE_AUTHORIZATION;
    }

    /*
     *  (non-Javadoc)
     * @see org.buni.meldware.mail.Protocol#greet(java.io.OutputStream)
     */
    public void greet(OutputStream stream) throws IOException {
        String id = generateIdStringForAPOP(protocol.getServername());
        PrintWriter writer = AbstractResponse.getWrappedWriter(new PrintWriter(
                stream));
        String display = "+OK "
                + "POP3 Server (Meldware Mail POP3 Server version 0.8) " + "ready "
                + id;
        log.debug(display);
        writer.println(display);
        writer.flush();
    }

    private String generateIdStringForAPOP(String servername) {
        String threadid = "" + Thread.currentThread().getId();
        String thetime = "" + System.currentTimeMillis();
        String retval = "<" + threadid + "." + thetime + "@" + servername + ">";
        this.apop = retval;
        return retval;
    }

    /*
     *  (non-Javadoc)
     * @see org.buni.meldware.mail.Protocol#parseRequest(java.io.InputStream, java.net.Socket)
     */
    public Request parseRequest(InputStream stream, Socket socket)
            throws IOException {

        MutablePOP3Request request = new MutablePOP3Request(this, stream);
        String commandString = readCommand(stream);
        if (log.isDebugEnabled()) {
            log.debug("Got commandString: " + commandString);
        }

        String[] command = parseCommand(commandString);

        request.setCommand(command[0].trim());
        if (command[1] != null && command[1].trim().length() > 0) {
            //KAB: POP3 commands can have more than one argument (e.g. TOP).
            //Arguments are separated by a single space.
            //request.addArgument(command[1].trim());
            StringTokenizer tok = new StringTokenizer(command[1].trim(), " ");
            while (tok.hasMoreTokens()) {
                String arg = tok.nextToken();
                request.addArgument(arg);
            }
        }

        request.setSocket(socket);
        return request;
    }

    /*
     *  (non-Javadoc)
     * @see org.buni.meldware.mail.Protocol#handleRequest(java.io.OutputStream, org.buni.meldware.mail.Request)
     */
    public Response handleRequest(OutputStream stream, Request request) {
        if (log.isDebugEnabled()) {
            log.debug("HandleRequest called with request = "
                    + (request == null ? "true" : "false"));
            log.debug("HandleRequest called with command = "
                    + (request == null ? "null" : ((POP3Request) request)
                            .getCommand()));
        }
        POP3Handler handler = (POP3Handler) handlers
                .get(((POP3Request) request).getCommand());
        POP3Response response = null;
        if (handler != null) {
            try {
                response = handler.handleRequest(stream, (POP3Request) request,
                        this);
            } catch (IOException e) {
                log.error("Command: " + handler.getClass().getSimpleName(), e);
                handleIOError(stream);
            } catch (MailException e) {
                log.error("Command: " + handler.getClass().getSimpleName(), e);
                handleIOError(stream);
            }
        } else {
            try {
                response = new POP3Response(request, stream, this);
                PrintWriter writer = response.getWriter();
                writer.println("-ERR " + protocol.getServername()
                        + " Syntax error, command unrecognized: "
                        + ((POP3Request) request).getCommand());
                writer.flush();
            } catch (IOException ioe) {
                log.error(ioe);
                response = (POP3Response) handleIOError(stream);
            }
        }
        return response;
    }

    /**
     * gets a response string for a request
     * @param request which is being processed
     * @return response string
     * @throws IOException in the event the response cannot be read due to some communication exception
     */
    public String readResponse(Request request) throws IOException {
        return readCommand(((POP3Request) request).getInputStream());
    }

    /**
     * in the event an IO Error occurs (generally fatal to the request) this is called
     */
    public Response handleIOError(OutputStream stream) {
        log.error("Handle IO Error");
        return null;
    }

    public String getName() {
        return "POP3";
    }

    public void setProperties(List properties) {
        // TODO Auto-generated method stub

    }

    public Element getProperties() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * The current state of the transaction.
     *
     * @return STATE_AUTHORIZATION, STATE_TRANSACTION, or STATE_UPDATE
     */
    public int getState() {
        return state;
    }

    /**
     * The current state of the transaction.
     *
     * @param state STATE_AUTHORIZATION, STATE_TRANSACTION, or STATE_UPDATE
     */
    public void setState(int state) {
        this.state = state;
    }

    /**
     * The username for the current session.
     *
     * @return null if not set.
     */
    public String getUsername() {
        return username;
    }

    /**
     * The username for the current session.
     *
     * @param username valid username, or null to clear.
     */
    public void setUsername(String username) {
        this.username = username;
    }
    
    private Mailbox mailboxProxy = null;
    public Mailbox getMailbox() {
        if (mailboxProxy == null) {
            mailboxProxy = mailboxManager.createProxy(username, true, 
                    new Hints(false, true, true));
        }
        return mailboxProxy;
    }

    
    private Folder inboxProxy = null;
    public Folder getInbox() {
        if (inboxProxy == null) {
            inboxProxy = getMailbox().getDefault();
        }
        return inboxProxy;
    }

    public String getApopId() {
        return apop;
    }

    /**
     * @return
     */
    public MailboxService getMailboxService() {
        return mailboxManager;
    }

    /**
     * @return
     */
//    @Tx(TxType.REQUIRESNEW)
//    public boolean commit() {
//        return getMailboxService().commit(this.getUsername());
//    }

    /**
     * 
     */
    public boolean deleteMarked() {
        if (inboxProxy != null) {
            inboxProxy.expunge(false);
        }
        return true;
    }

    /* (non-Javadoc)
     * @see org.buni.meldware.mail.Protocol#handleCleanup()
     */
    @Tx(TxType.REQUIRESNEW)
    public void handleCleanup(OutputStream out) {
        try {
            deleteMarked();
        } catch (Exception e) {
        }

//        try {
//            getMailbox().release(getUsername());
//            //getMailboxService().release(this.getUsername(), this.getMailbox());
//        } catch (Exception e) {
//        }

    }
    
    public Date getLockExpiration() {
        Object o = getState(ServerThread.STATE_LIFE);
        return new Date((Long) o);
    }

    public UserRepository getAOPUserRepostiory() {
        return protocol.getAPOPUserRepository();
    }

    public String getServername() {
        return protocol.getServername();
    }

    public SSLSocketFactory getSslSocketFactory() {
        return protocol.getSslSocketFactory();
    }

    public boolean isTlsEnabled() {
        return protocol.isTlsEnabled();
    }
    
    public boolean isRequireClientCert() {
        return protocol.isRequireClientCert();
    }

    public boolean isRequireTls() {
        return protocol.isRequireTls();
    }

    public UserRepository getUserRepository() {
        return protocol.getUserRepository();
    }
    
}
