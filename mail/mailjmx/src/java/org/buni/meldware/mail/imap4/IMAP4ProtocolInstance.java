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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.buni.meldware.Version;
import org.buni.meldware.common.logging.Log;
import org.buni.meldware.mail.AbstractProtocol;
import org.buni.meldware.mail.AbstractResponse;
import org.buni.meldware.mail.MailException;
import org.buni.meldware.mail.Protocol;
import org.buni.meldware.mail.Request;
import org.buni.meldware.mail.Response;
import org.buni.meldware.mail.api.ActiveFolder;
import org.buni.meldware.mail.api.ActiveMailbox;
import org.buni.meldware.mail.api.FolderListener;
import org.buni.meldware.mail.api.Hints;
import org.buni.meldware.mail.imap4.commands.AbstractImapCommand;
import org.buni.meldware.mail.imap4.commands.BadSyntaxCommand;
import org.buni.meldware.mail.imap4.commands.LoginCommand;
import org.buni.meldware.mail.imap4.parser.Continuation;
import org.buni.meldware.mail.imap4.parser.ImapLexer;
import org.buni.meldware.mail.mailbox.MailboxService;
import org.buni.meldware.mail.mailbox.MessageData;
import org.buni.meldware.mail.mailbox.MessageDataUtil;
import org.buni.meldware.mail.message.MailBodyManager;
import org.buni.meldware.mail.message.MailHeaders;
import org.buni.meldware.mail.message.MailHeadersImpl;
import org.buni.meldware.mail.util.io.Copier;
import org.buni.meldware.mail.util.io.ExactSizeCopier;
import org.jboss.aspects.tx.Tx;
import org.jboss.aspects.tx.TxType;
import org.w3c.dom.Element;

import antlr.TokenStreamException;

/**
 * Service Proxy created by the MBean which is used by the Server (actually
 * ServerThread) to implement the protocol.
 * 
 * @author Eric Daugherty
 * @author Thorsten Kunz
 * @author Andrew C. Oliver
 *
 * @version $Revision: 1.33 $
 */
public class IMAP4ProtocolInstance extends AbstractProtocol implements Protocol {

    /**
     * Log4J logger
     */
    private Log log = Log.getLog(IMAP4ProtocolInstance.class);
    private final static byte[] CONT_MSG = "+ Ready for additional text\r\n".getBytes();

    //
    // The possible connection states
    //

    public enum ImapState {
    /**
     * In the NOT_AUTHENTICATED state, the client MUST supply authentication
     * credentials before most commands will be permitted. This state is entered
     * when a connection starts unless the connection has been
     * pre-authenticated.
     */
    	NOT_AUTHENTICATED,

    /**
     * In the AUTHENTICATED state, the client is authenticated and MUST select a
     * mailbox to access before commands that affect messages will be permitted.
     * This state is entered when a pre-authenticated connection starts, when
     * acceptable authentication credentials have been provided, after an error
     * in selecting a mailbox, or after a successful CLOSE command.
     */
    	AUTHENTICATED,

    /**
     * In a SELECTED state, a mailbox has been selected to access. This state is
     * entered when a mailbox has been successfully selected.
     */
    	SELECTED,

    /**
     * In the LOGOUT state, the connection is being terminated. This state can
     * be entered as a result of a client request (via the LOGOUT command) or by
     * unilateral action on the part of either the client or server. If the
     * client requests the logout state, the server MUST send an untagged BYE
     * response and a tagged OK response to the LOGOUT command before the server
     * closes the connection; and the client MUST read the tagged OK response to
     * the LOGOUT command before the client closes the connection. A server MUST
     * NOT unilaterally close the connection without sending an untagged BYE
     * response that contains the reason for having done so. A client SHOULD NOT
     * unilaterally close the connection, and instead SHOULD issue a LOGOUT
     * command. If the server detects that the client has unilaterally closed
     * the connection, the server MAY omit the untagged BYE response and simply
     * close its connection.
     */
    	LOGOUT,
        
        IDLE
    }

    /**
     * The current state of the connection. Values are: NOT_AUTHENTICATED,
     * AUTHENTICATED, SELECTED, and LOGOUT
     */
    private ImapState state;

    /**
     * The username for the current session.
     */
    private String username = null;
    
    private ActiveMailbox mailbox = null;
    
    private ActiveFolder folderProxy = null;

    /**
     * The selected folder is Read-Only or
     * in Read-Only mode (EXAMINE rather than SELECT)
     */
    private boolean folderReadOnly;

    /**
     * the mailbox manager
     */
    private MailboxService mailboxManager = null;
    private FolderListener folderListener;
    private String tag;
    private IMAP4Protocol protocol;

    /**
     * create an IMAP4ProtocolInstance with the proper handlers and the already
     * constructed properties map
     * 
     * @param handlers
     *            from IMAP4Handlers.instance();
     * @param properties
     *            from IMAP4Protocol.setProperties()
     */
    public IMAP4ProtocolInstance(IMAP4Protocol protocol, Map handlers, 
            MailboxService mailboxManager) {
        this.protocol = protocol;
        this.mailboxManager = mailboxManager;
        state = ImapState.NOT_AUTHENTICATED;
    }

    public String getName() {
        return "IMAP4";
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.buni.meldware.mail.Protocol#greet(java.io.OutputStream)
     */
    public void greet(OutputStream stream) throws IOException {
        PrintWriter writer = AbstractResponse.getWrappedWriter(new PrintWriter(
                stream));
        // TODO: Refactor to common code.
        String serverName = protocol.getServername();
        writer.println("* OK " + serverName + " " + Version.IMAP_SERVER_PRODID);
        writer.flush();
    }
    
    public void setIdleNotification(OutputStream out) {
        synchronized (this) {
            if (getState() == ImapState.IDLE) {
                // Send notification to the client.
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.buni.meldware.mail.Protocol#parseRequest(java.io.InputStream,
     *      java.net.Socket)
     */
    public Request parseRequest(InputStream stream, Socket socket)
            throws IOException {

        synchronized (this) {
            MutableIMAP4Request request = new MutableIMAP4Request(this, stream);
            
            boolean commandComplete = false;
            List<String> values = new ArrayList<String>();
            StringBuilder commandB = new StringBuilder();
            MessageData message = null;
            do {
                try {
                    commandB.append(readCommand(stream));
                    log.debug("Command: %s", commandB);
                    
                    // TODO Implement a CharSequenceReader
                    String command = commandB.toString();
                    ImapLexer lex = new ImapLexer(new StringReader(command));
                    lex.setContinuationValues(values);
                    lex.setMessage(message);
                    lex.setState(state);
                    AbstractImapCommand c;
                    try {
                        lex.doLex();
                        c = lex.getCommand();
                    } catch (TokenStreamException e) {
                        log.warn("Bad Command: %s, Reason: %s", commandB, e.getMessage());
                        c = new BadSyntaxCommand(e.getMessage());
                    } catch (InvalidStateException e) {
                        log.warn("Invalid State: %s, Reason: %s", commandB, e.getMessage());
                        c = new BadSyntaxCommand(e.getMessage());
                    }
                    c.setProtocolInstance(this);
                    request.setSocket(socket);
                    c.setRequest(request);
                    request.setCommand(c);
                    request.setCommandString(command);
                    commandComplete = true;
                    
                } catch (Continuation c) {
                    switch (c.getType()) {
                    case MESSAGE:
                        socket.getOutputStream().write(CONT_MSG);
                        message = createMessage(stream, c.getLength());
                        break;
                    case STRING:
                        if (c.getLength() > 0) {
                            socket.getOutputStream().write(CONT_MSG);
                            values.add(readString(stream, c.getLength()));
                        } else {
                            values.add("");
                        }
                        break;
                    }
                }
            } while (!commandComplete);
            
            return request;            
        }
    }
    
    private MessageData createMessage(InputStream in, int size) throws IOException {
        MessageDataUtil mdu = new MessageDataUtil(mailboxManager.getBodyManager());
        MessageData message = mdu.createMimeMessage(new SizeLimitedInputStream(in, size - 2));
        // Clean up the trailing new line.
        in.read();
        in.read();
        MailHeaders headers = MailHeadersImpl.create(message.getHeaders());
        message.setHeaders(headers);
        return message;
    }
    
    private String readString(InputStream in, int len) throws IOException {
        Copier c = new ExactSizeCopier(len);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        c.copy(in, baos, 8192);
        return new String(baos.toByteArray());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.buni.meldware.mail.Protocol#handleRequest(java.io.OutputStream,
     *      org.buni.meldware.mail.Request)
     */
    public Response handleRequest(OutputStream stream, Request request) {

        synchronized (this) {
            IMAP4Response response = null;
            MutableIMAP4Request iRequest = (MutableIMAP4Request) request;
            try {
                AbstractImapCommand cmd = iRequest.getCommand();
                if (cmd != null) {
                    long t0 = System.currentTimeMillis();
                    response = iRequest.getCommand().execute();
                    response.getWriter().flush();
                    long t1 = System.currentTimeMillis();
                    String cmdStr;
                    if (cmd instanceof LoginCommand) {
                        cmdStr = "LOGIN xxx xxx";
                    } else {
                        cmdStr = iRequest.getCommandString();
                    }
                    protocol.addTiming(cmd.getCmdName(), cmdStr, t1 - t0);
                } else {
                    System.err.println("Got null command somehow");
                }
            } catch (IOException e) {
                log.error(e);
                handleIOError(stream);
            }/* catch (MailException me) {
             log.error(me);
             handleIOError(stream);
             }*/
            return response;
        }        
    }

    /**
     * in the event an IO Error occurs (generally fatal to the request) this is
     * called
     */
    public Response handleIOError(OutputStream stream) {
        log.error("Handle IO Error");
        return null;
    }

    /**
     * gets a response string for a request
     * 
     * @param request
     *            which is being processed
     * @return response string
     * @throws IOException
     *             in the event the response cannot be read due to some
     *             communication exception
     */
    public String readResponse(Request request) throws IOException {
        return readCommand(((IMAP4Request) request).getInputStream());
    }
    
    public ActiveMailbox getMailbox() {
        if (mailbox == null) {
            mailbox = mailboxManager.createActiveProxy(username, true, Hints.NONE);
        }
        return mailbox;
    }
    
    public ActiveFolder getFolderProxy() {
        return folderProxy;
    }
    
    /**
     * Select the folder for the specified path.  Unselect the current folder
     * before processing any path.
     * 
     * @param pathName
     * @return
     */
    public org.buni.meldware.mail.api.Folder selectFolder(String[] path) {
        folderProxy = null;
        if (path != null) {
            if ("INBOX".equalsIgnoreCase(path[0])) {
                if (path.length == 1) {
                    folderProxy = getMailbox().getDefault();
                } else {
                    path[0] = "INBOX";
                    folderProxy = getMailbox().getFolder(path);
                }
            } else {
                folderProxy = getMailbox().getFolder(path);
            }
        } else {
            throw new MailException("Path not properly specified");
        }
        return folderProxy;
    }
    
    public void clearFolder() {
        if (folderProxy != null) {
            folderProxy.close();
            folderProxy = null;
        }
    }

    public ImapState getState() {
        return state;
    }

    public void setState(ImapState state) {
        this.state = state;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setProperties(List properties) {
        // TODO Auto-generated method stub

    }

    public Element getProperties() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Cleans up the IMAP protocol.  Should close the folder before issuing
     * an unsolicited BYE command.
     * 
     * @see org.buni.meldware.mail.Protocol#handleCleanup()
     */
    public void handleCleanup(OutputStream out) {
        try {
            org.buni.meldware.mail.api.Folder f = getFolderProxy();
            if (f != null && !isFolderReadOnly()) {
                log.debug("Cleaning up deleted messages");
                //f.expunge(false);
                f.close();
            }
            out.write("* BYE IMAP4rev1 Server logging out\r\n".getBytes());
        } catch (Exception e) {
            log.error(e, "Failure during cleanup");
        }
    }


    @Tx(TxType.REQUIRED)
    public boolean authenticate(String user, String pass) {
        boolean result = protocol.getUserRepository().test(user, pass);
        if(result) {
            this.setState(ImapState.AUTHENTICATED);
        }
        
        return result;
    }
    
    /**
     * @return MailBodyManager which handles mail bodies for this pi
     */
    public MailBodyManager getManager() {
        return this.mailboxManager.getBodyManager();
    }

    /**
     * Can the folder contents be midified?
     * 
     * @return is the selected folder read-only?
     */
    public boolean isFolderReadOnly() {
        return folderReadOnly;
    }

    /**
     * Set folder contents read-only or read-write.
     * 
     * @param folderReadOnly
     *            Set Read-Only = true, Read-Write = false.
     */
    public void setFolderReadOnly(boolean folderReadOnly) {
        this.folderReadOnly = folderReadOnly;
    }

    public void setListener(FolderListener fl) {
        this.folderListener = fl;
    }

    public FolderListener getListener() {
        return folderListener;
    }
    
    public FolderListener clearListener() {
        FolderListener fl = folderListener;
        folderListener = null;
        return fl;
    }

    public void setIdleTag(String tag) {
        this.tag = tag;
    }
    
    public String clearIdleTag() {
        String _tag = tag;
        tag = null;
        return _tag;
    }
    
}
