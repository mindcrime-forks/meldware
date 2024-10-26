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
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.net.ssl.SSLSocketFactory;

import org.buni.meldware.common.logging.Log;
import org.buni.meldware.mail.AbstractProtocol;
import org.buni.meldware.mail.AbstractResponse;
import org.buni.meldware.mail.MailException;
import org.buni.meldware.mail.MailListenerChain;
import org.buni.meldware.mail.Protocol;
import org.buni.meldware.mail.Request;
import org.buni.meldware.mail.Response;
import org.buni.meldware.mail.ServerThread;
import org.buni.meldware.mail.domaingroup.DomainGroupMBean;
import org.buni.meldware.mail.message.LoopDetectedException;
import org.buni.meldware.mail.message.MailAddress;
import org.buni.meldware.mail.message.MailBodyManager;
import org.buni.meldware.mail.message.MailCreateAdapter;
import org.buni.meldware.mail.message.MailCreateListener;
import org.buni.meldware.mail.message.MailHeaders;
import org.buni.meldware.mail.message.StandardMailHeaders;
import org.buni.meldware.mail.smtp.handlers.SMTPHandler;
import org.buni.meldware.mail.userrepository.UserRepository;
import org.buni.meldware.mail.util.DateUtil;
import org.buni.meldware.mail.util.io.Copier;
import org.buni.meldware.mail.util.io.DotUnstuffingCopier;

/**
 * Service Proxy created by the MBean which is used by the Server (actually ServerThread) to implement
 * the protocol.
 *
 * @author Andrew C. Oliver
 * @version $Revision: 1.7 $
 */
public class SMTPProtocolInstance extends AbstractProtocol implements Protocol,
        SMTPConstants {

    private static final long serialVersionUID = 3258689927104705587L;
    private static final Map<String,String> EMPTY_MAP = 
        new HashMap<String,String>(0);
    private static final Log log = Log.getLog(SMTPProtocolInstance.class);

    /**
     * returned from SMTPHandlers.instance();
     */
    private Map handlers;

    private MailBodyManager manager = null;

    private SMTPProtocol protocol;

    /**
     * @return Returns the manager.
     */
    public MailBodyManager getManager() {
        return manager;
    }

    /**
     * This property allows for the MailBodyManager to be 
     * injected into the protocol instance.  Useful for testing.
     * 
     * @param manager The manager to set.
     */
    public void setManager(MailBodyManager manager) {
        this.manager = manager;
    }

    /**
     * create an SMTPProtocolInstance with the propert handlers and the already constructed properties map
     * @param handlers from SMTPHandlers.instance();
     * @param properties from SMTPProtocol.setProperties()
     */
    public SMTPProtocolInstance(SMTPProtocol protocol, Map handlers,
            MailBodyManager mailBodyManager) {
        this.protocol = protocol;
        this.handlers = handlers;
        setManager(mailBodyManager);
    }

    /**
     * Send a greeting to the user.
     * 
     * @see org.buni.meldware.mail.Protocol#greet(java.io.OutputStream)
     */
    public void greet(OutputStream stream) throws IOException {
        PrintWriter writer = AbstractResponse.getWrappedWriter(new PrintWriter(
                stream));
        writer.println("220 " + getServername()
                + " SMTP Server (Meldware Mail SMTP Server version 0.8) " + "ready"
                + " " + DateUtil.longInternetDate());
        writer.flush();
    }
    
    /**
     * Parse the incoming SMTP command.
     * 
     * @see org.buni.meldware.mail.Protocol#parseRequest(java.io.InputStream, java.net.Socket)
     */
    public Request parseRequest(InputStream stream, Socket socket)
            throws IOException {
        MutableSMTPRequest request = new MutableSMTPRequest(this, stream);
        String commstring = readCommand(stream);
        String[] command = parseCommand(commstring);

        request.setCommand(command[0].trim());
        request.addArgument(command[1]);
        request.setSocket(socket);

        SMTPProtocolInstance.log.trace(commstring);
        return request;
    }

    /**
     * gets a response string for a request
     * @param request which is being processed
     * @return response string
     * @throws IOException in the event the response cannot be read due to some communication exception
     */
    public String readResponse(SMTPRequest request) throws IOException {
        return readCommand(request.getInputStream());
    }

    /**
     * Handle the parsed request.
     * 
     * @see org.buni.meldware.mail.Protocol#handleRequest(java.io.OutputStream, org.buni.meldware.mail.Request)
     */
    public Response handleRequest(OutputStream stream, Request request) {
            log.debug("HandleRequest called with request = %b", request == null);
            log.debug("HandleRequest called with command = %s", 
                    request == null ? "null" : ((SMTPRequest) request).getCommand());
        SMTPHandler handler = (SMTPHandler) handlers
                .get(((SMTPRequest) request).getCommand());
        SMTPResponse response = null;
        if (handler != null) {
            try {
                response = handler.handleRequest(stream, (SMTPRequest) request,
                        this);
            } catch (IOException e) {
                e.printStackTrace();
                handleIOError(stream);
            }
        } else {
            try {
                response = new SMTPResponse((SMTPRequest) request, stream,
                        this, secure);
                PrintWriter writer = response.getWriter();
                writer.println("500 " + getServername()
                        + " Syntax error, command unrecognized: "
                        + ((SMTPRequest) request).getCommand());
                writer.flush();
            } catch (IOException ioe) {
                response = (SMTPResponse) handleIOError(stream);
            }
        }
        return response;
    }

    //TODO: this smells bad..
    /**
     * this is called back to from various handlers.  It handles the actual 
     * authentication with a positive or negative response
     * @param rcptAddr recepient of the org.jboss.org.buni.meldware.mail
     * @param out output stream
     * @param request SMTPRequest which is being processed
     * @param session should be equal to this instance (or we'll puke)
     * @param writer printwriter for writing the response, if negative
     * @return boolean whether the user is authenticated or not
     */
    public boolean handleAuth(MailAddress rcptAddr, OutputStream out,
            SMTPRequest request, SMTPProtocolInstance session,
            PrintWriter writer) {
        DomainGroupMBean dg = protocol.getDomainGroup();
        log.debug("HANDLE AUTH");
        if (session != this) {
            throw new RuntimeException(
                    "THIS SHOULD NOT HAPPEN:::WRONG PROTOCOL INSTANCE TO HANDLE AUTH...I THINK I'LL CHOKE NOW...BYE");
        }

        if (!protocol.isAuthRequired() && protocol.isAuthAllowed()) {
            log.debug("AUTH NOT REQUIRED");
            return true;
        }

        if (!protocol.isAuthAllowed()) {
            writer.println("503 This server does not allow authentication");
            writer.flush();
            return false;
        }
        
        // Headley 6/26/2005 - But if verify-identity == true then we should check that 
        // before we look for the recipient's address to be in our local domain.
        // If verify_identity == true then always check regardless of what authShouldBeRequired returns
        if (authShouldBeRequired((String) getState(USER), rcptAddr.getDomain())
                && protocol.isVerifyIdentity()) {
            String authUser = ((String) getState(USER));
            authUser = authUser != null ? authUser.toLowerCase(Locale.US)
                    : null;
            MailAddress senderAddress = (MailAddress) getState(SENDER);

            if ((authUser != null && !authUser.equals(senderAddress.getUser()))
                    || (!dg.isInGroup(senderAddress.getDomain()))) {
                writer
                        .println("503 Incorrect Authentication for Specified Email Address");
                writer.flush();
                return false;
            }
        }

        // mail to any domain in our local group should be accepted --- that's the free
        // and open world of smtp, where anybody can email anybody. but mail submitted by an
        // unauthenticated user to an unknown domain should get denied.
        String toDomain = rcptAddr.getDomain();
        if (isMailAddressLocal(rcptAddr)) {
            return true; // normal open and free smtp world
        } else if (getState("USER") == null) {
            return false; // no access for unauthenticated users to unknown domains
        }

        log.debug("USER == %s" + getState("USER"));

        if (getState("USER") == null) {

            if (dg.isInGroup(toDomain)) {
                log.debug("SERVERNAME != domain");
                log.debug("SERVERNAME = %s domain = %s", getServername(), rcptAddr.getDomain());
                writer.println("530 Authentication Required");
                writer.flush();
                return false;
            }
        }

        return true;
    }

    /**
     * Determines if the address supplied is local to this server.
     * I.e. is the domain of the address in the local domain group.
     * 
     * @param address
     * @return
     */
    public boolean isMailAddressLocal(MailAddress address) {
        DomainGroupMBean dg = protocol.getDomainGroup();
        String toDomain = address.getDomain();
        return dg.isInGroup(toDomain);
    }

    /**
     * Tests if mail from the specified host may be relayed
     * according to the set of allowed domains.
     * 
     * @param hostname
     * @return
     */
    public boolean isRelayByDomainAllowed(InetAddress address) {
        if (protocol.isRelayByDomain()) {
            String hostname = address.getCanonicalHostName();
            log.debug("Checking if domain relaying is allowed from: "
                    + hostname);
            DomainGroupMBean relayGroup = protocol.getRelayDomainGroup();
            if (relayGroup != null) {
                return relayGroup.isHostInGroup(hostname);
            }
        }
        return false;
    }

    /**
     * Tests if the address specified is in the set of domain that we are able
     * to relay to.
     * 
     * @param address
     * @return
     */
    public boolean isRelayByDomainAllowed(MailAddress address) {
        if (protocol.isRelayByDomain()) {
            log.debug("Checking if domain relaying is allowed to: " + address);
            DomainGroupMBean relayGroup = protocol.getRelayDomainGroup();
            if (relayGroup != null) {
                return relayGroup.isInGroup(address.getDomain());
            }
        }
        return false;
    }

    /**
     * Determines if the specified ip address is allowed to
     * relay using this server.
     * 
     * TODO: Implement.
     * 
     * @param addr
     * @return
     */
    public boolean isRelayByAddrAllowed(InetAddress address) {
        if (protocol.isRelayByAddr()) {
            log.debug("Relaying by IP Address is not supported");
        }
        return false;
    }

    /**
     * dumb method to simplify the nasty if statement (will be removed when we decrud authentication)
     * @param user that is logged in (or null)
     * @param domain of the to address
     * @return whether auth should have been required for the purpose of verifying identity
     */
    private boolean authShouldBeRequired(String user, String domain) {
        DomainGroupMBean dg = protocol.getDomainGroup();
        // MIKEA: should actually allow unauthenticated email to any address in our domain group, not just server name
        if (dg.isInGroup(domain)) {
            return false;
        }
        if (user == null) {
            return false;
        }
        return true;
    }

    /**
     * in the event an IO Error occurs (generally fatal to the request) this is called
     */
    public Response handleIOError(OutputStream stream) {
        log.error("Handle IO Error");
        return null;
    }

    /* (non-Javadoc)
     * @see org.buni.meldware.mail.Protocol#getName()
     */
    public String getName() {
        return "SMTP";
    }

    public boolean isAuthenticated() {
        return getState(USER) != null;
    }

    /**
     * @see org.buni.meldware.mail.Protocol#handleCleanup()
     */
    public void handleCleanup(OutputStream out) {
        // nothing for smtp
    }
    
    private final static String UNKNOWN = "unknown";
    
    public String getIP(String key) {
        InetAddress addr = (InetAddress) getState(key);
        String ip = addr != null ? addr.getHostAddress() : UNKNOWN;
        return ip;
    }
    
    public String getHost(String key) {
        InetAddress addr = (InetAddress) getState(key);
        String host = addr != null ? addr.getHostName() : UNKNOWN;
        return host;        
    }
    
    public String getServername() {
        return protocol.getServername();
    }
    
    /**
     * Checks to see if we have too many received headers.
     * 
     * @param serverName
     * @param localIP
     * @param maxReceived
     * @param receivedThreshold
     * @param maxOwnReceived
     * @throws LoopDetectedException
     */
    public void checkForLoops(MailHeaders headers)
            throws LoopDetectedException {
        
        //SMTPProtocolMBean p = getSMTPMBean();
        String serverName = getServername();
        String localIP = getIP(ServerThread.STATE_LOCAL_ADDRESS);
        long maxReceived = protocol.getMaxReceivedHdrs();
        long receivedThreshold = protocol.getReceivedHdrsThreshold();
        long maxOwnReceived = protocol.getMaxOwnReceivedHdrs();
        
        // Check for loops in headers
        String[] receivedHeaders = headers.getHeader(StandardMailHeaders.RECEIVED);

        if (receivedHeaders != null) {
            if (receivedHeaders.length > maxReceived) {
                String pattern = "SMTP loop? Too many Received headers. Max allowed is %d";
                String msg = String.format(pattern, maxReceived);
                throw new LoopDetectedException(msg);
            }

            if (receivedHeaders.length > receivedThreshold) {

                // Over the threshold for self inspection, see if we occur more
                // than allowed
                int count = 0;
                for (String hdr : receivedHeaders) {
                    if (hdr.indexOf(serverName) > -1
                            && hdr.indexOf(localIP) > -1) {
                        count++;
                        if (count > maxOwnReceived) {
                            String pattern = "SMTP loop? More than %d Received headers contain the name of our server: %s";
                            String msg = String.format(pattern, maxOwnReceived,
                                    serverName);
                            throw new LoopDetectedException(msg);
                        }
                    }
                }
            }
        }
    }    
    
    /**
     * Gets the information required for the Received trace header.
     * 
     * @return The value portion for a 'Received:' header.
     */
    public String getTraceInfo() {
        
        String serverName = getServername();
        String localIP = getIP(ServerThread.STATE_LOCAL_ADDRESS);
        String remoteIP = getIP(ServerThread.STATE_CLIENT_ADDRESS);
        String remoteHost = getHost(ServerThread.STATE_CLIENT_ADDRESS);
        String heloDomain = (String) getState(SMTPConstants.HELO_DOMAIN);
        String pattern = "from %s (%s %s) by %s/%s (%s)\r\n\twith SMTP id %s; %s";
        // TODO: Get the version from the build.
        String version = "Meldware Mail 1.0M8";
        StringBuffer id = new StringBuffer();
        id.append(System.currentTimeMillis());
        id.append(Math.random() * 1024);
        String date = DateUtil.mailHeaderDate();
        
        return String.format(pattern, heloDomain, remoteHost, remoteIP, serverName, version, localIP, id, date);
    }
    
    /**
     * Creates the SMTP Mail Create Listener.
     * 
     * @param from From address
     * @param to To address
     * @param hdrs Additional headers
     * @return
     */
    public MailCreateListener getMailCreateListener(MailAddress from, 
            MailAddress[] to, Map<String,String> hdrs) {
        
        String serverName = getServername();
        String localIP = getIP(ServerThread.STATE_LOCAL_ADDRESS);
        long maxReceived = protocol.getMaxReceivedHdrs();
        long receivedThreshold = protocol.getReceivedHdrsThreshold();
        long maxOwnReceived = protocol.getMaxOwnReceivedHdrs();
        String trace = getTraceInfo();
        boolean isAuth = getState(SMTPConstants.USER) != null;
        
        MailCreateListener mcl = new SMTPMailCreateAdapter(from, to, hdrs, 
                isAuth, serverName, localIP, trace, maxReceived, 
                receivedThreshold, maxOwnReceived);
        
        return mcl;
    }
    
    /**
     * Creates the SMTP Mail Create Listener.
     * 
     * @param from
     * @param to
     * @return
     */
    public MailCreateListener getMailCreateListener(MailAddress from, 
            MailAddress[] to) {
        return getMailCreateListener(from, to, EMPTY_MAP);    
    }
    
    private static class SMTPMailCreateAdapter extends MailCreateAdapter {
        
        private final MailAddress from;
        private final MailAddress[] to;
        //private final Map<String, String> hdrs;
        private final String hostname;
        private final String ip;
        private final long maxReceived;
        private final long receivedThreshold;
        private final long maxOwnReceived;
        private boolean isAuth;
        private String trace;

        public SMTPMailCreateAdapter(MailAddress from, MailAddress[] to, 
                Map<String,String> hdrs, boolean isAuth, String hostname, 
                String ip, String trace, long maxReceived, 
                long receivedThreshold, long maxOwnReceived) {
            
            this.from = from;
            this.to = to;
            this.hostname = hostname;
            this.ip = ip;
            //this.hdrs = hdrs;
            this.isAuth = isAuth;
            this.maxReceived = maxReceived;
            this.receivedThreshold = receivedThreshold;
            this.maxOwnReceived = maxOwnReceived;
            this.trace = trace;
        }
        
        /**
         * Add trace headers etc.
         * 
         * @see org.buni.meldware.mail.message.MailCreateAdapter#postHeadersParsed()
         */
        @Override
        protected void postHeadersParsed() {
            
            if (isAuth) {
                String fromStr = from.toString();
                getMailHeaders().addIfAbsent(StandardMailHeaders.FROM, fromStr);
                getMailHeaders().addIfAbsent(StandardMailHeaders.RETURN_PATH, fromStr);
                getMailHeaders().addIfAbsent(StandardMailHeaders.DATE, DateUtil.mailHeaderDate());
            }
            
            getMailHeaders().addHeader(StandardMailHeaders.RECEIVED, trace);
        }

        /**
         * Returns a dot unstuffing copier.
         * 
         * @see org.buni.meldware.mail.message.MailCreateAdapter#getCopier(int)
         */
        @Override
        public Copier getCopier() {
            return new DotUnstuffingCopier();
        }

        /**
         * @see org.buni.meldware.mail.message.MailCreateAdapter#getFrom()
         */
        @Override
        public MailAddress getFrom() {
            return from;
        }

        /**
         * @see org.buni.meldware.mail.message.MailCreateAdapter#getTo()
         */
        @Override
        public MailAddress[] getTo() {
            return to;
        }

        /**
         * Checks for loops.
         * 
         * @see org.buni.meldware.mail.message.MailCreateAdapter#verifyHeaders()
         */
        @Override
        public void verifyHeaders() throws MailException {
            checkForLoops(getMailHeaders());
        }
        
        /**
         * Checks to see if we have too many received headers.
         * 
         * @param serverName
         * @param localIP
         * @param maxReceived
         * @param receivedThreshold
         * @param maxOwnReceived
         * @throws LoopDetectedException
         */
        public void checkForLoops(MailHeaders headers)
                throws LoopDetectedException {
                        
            // Check for loops in headers
            String[] receivedHeaders = headers.getHeader(StandardMailHeaders.RECEIVED);

            if (receivedHeaders != null) {
                if (receivedHeaders.length > maxReceived) {
                    String pattern = "SMTP loop? Too many Received headers. Max allowed is %d";
                    String msg = String.format(pattern, maxReceived);
                    throw new LoopDetectedException(msg);
                }

                if (receivedHeaders.length > receivedThreshold) {

                    // Over the threshold for self inspection, see if we occur more
                    // than allowed
                    int count = 0;
                    for (String hdr : receivedHeaders) {
                        if (hdr.indexOf(hostname) > -1 || 
                                hdr.indexOf(ip) > -1) {
                            count++;
                            if (count > maxOwnReceived) {
                                String pattern = "SMTP loop? More than %d Received headers contain the name of our server: %s";
                                String msg = String.format(pattern, 
                                        maxOwnReceived, hostname);
                                throw new LoopDetectedException(msg);
                            }
                        }
                    }
                }
            }
        }    
    }

    public long getMaxMessageSize() {
        return protocol.getMaxMessageSize();
    }

    public boolean isRequireTlsForAuth() {
        return protocol.isRequireTlsForAuth();
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

    public SSLSocketFactory getSslSocketFactory() {
        return protocol.getSslSocketFactory();
    }

    public UserRepository getUserRepository() {
        return protocol.getUserRepository();
    }

    public String getAuthMethods() {
        return protocol.getAuthMethods();
    }

    public MailListenerChain getListenerChain() {
        return protocol.getListenerChain();
    }
}
