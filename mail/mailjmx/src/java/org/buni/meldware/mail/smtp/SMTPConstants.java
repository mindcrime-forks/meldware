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

/**
 * SMTP Constants contains constants for use throughout, most classes will just implement this interface
 * and use them directly.
 * 
 * @author Andrew C. Oliver
 * @copyright SuperLink Software, Inc. 2003 All rights reserved unless otherwise granted.
 * @version $Revision: 1.1 $
 */
public interface SMTPConstants {
    //properties
    /**
     * Server Name for use in Protocol.getProperty
     */
    public final static String SERVER_NAME = "Servername";

    /**
     * Is authentication requred... for use in Protocol.getProperty
     */
    public final static String AUTH_REQUIRED = "AuthRequired";

    /**
     * Is authentication allowed... for use in Protocol.getProperty
     */
    public final static String AUTH_ALLOWED = "AuthAllowed";

    /**
     * Server Name for use in Protocol.getProperty
     */
    public final static String STATUS = "STATUS";

    /**
     * allowed methods for authentication in Protocol.getProperty
     */
    public final static String AUTH_METHODS = "AuthMethods";

    /**
     * returns the user repository for use in authentication/authorization use with Protocol.getProperty
     */
    public final static String USER_REPOSITORY = "UserRepository";

    /**
     * returns whether or not we will verify identity, use with Protocol.getProperty
     */
    public final static String VERIFY_IDENTITY = "VerifyIdentity";

    /**
     * returns the set of Mail listeners for sending messages, use with Protocol.getProperty
     */
    public final static String MAIL_LISTENERS = "mail-listeners";

    /**
     * returns the maximum allowed message size when passed to Protocol.getProperty
     */
    public final static String MAX_MSG_SIZE = "MaxMessageSize";

    /**
     * determines if the use of the STARTTLS-Extension is required
     */
    public final static String REQUIRE_STARTTLS = "RequireTls";

    /**
     * determines if the use of the STARTTLE-Extension is required
     * when the user is authenticated.
     */
    public static final String REQUIRE_STARTTLS_FOR_AUTH = "RequireTlsForAuth";

    /**
     * determnines id the client needs to pass a certificate during SSL-Handshake after STARTTLS
     */
    public final static String REQUIRE_CLIENT_CERT = "RequireClientCert";

    /**
     * enable or don't enable TLS (different from requireSTARTTLS as it determines whether we'll even allow it)
     */
    public static final String TLS_SUPPORT = "TlsEnabled";

    /**
     * block size for messages 
     */
    public final static String BLOCK_SIZE = "BlockSize";

    /** 
     * The socket factory to be used to create sockets used for TLS
     */
    public static final String SSL_SOCKET_FACTORY = "SslSocketFactory";

    /**
     * The account used for the postmaster (also gets set in System properties)
     */
    public static final String POSTMASTER = "PostMaster";

    /**
     * The maximum number of received headers allowed (to avoid looping). 
     * If greater than this the message is not accepted. 
     */
    public static final String MAX_RECEIVED_HEADERS = "MaxReceivedHdrs";

    /**
     * The number of received headers before we start inspecting the content looking for ourselves (to avoid looping)
     */
    public static final String RECEIVED_HEADERS_THRESHOLD = "ReceivedHdrsThreshold";

    /**
     * The number of times we can find ourselves in the received headers (to avoid looping)
     * If greater than this the message is not accepted. 
     */
    public static final String MAX_OWN_RECEIVED_HEADERS = "MaxOwnReceivedHdrs";

    //states

    /**
     * get the domain passed in the HELO or EHLO command from the Protocol.getState
     */
    public final static String HELO_DOMAIN = "h";

    /**
     * get the user from the Protocol.getState
     */
    public final static String USER = "USER";

    /**
     * get the sender from the Protocol.getState
     */
    public final static String SENDER = "SENDER";

    /**
     * get the receipient list from the Protocol.getState
     */
    public final static String RCPT_LIST = "RCPT_LIST";

    //authentication types
    /**
     * for use in determining the authentication type (PLAIN digest) 
     */
    public final static String AUTH_TYPE_PLAIN = "PLAIN";

    /**
     * for use in determining the authentication type (LOGIN method)
     */
    public final static String AUTH_TYPE_LOGIN = "LOGIN";

    /**
     * stores the message's size (CmdMAIL)
     */
    public static final String MSG_SIZE = null;

    //other
    /**
     * list of SMTP Terminators for the filtering IO stream.
     */
    public final static char[] SMTPTerminator = { '\r', '\n', '.', '\r', '\n' };

    public static final String MAIL_LISTENER = "mail-listener";

    /**
     * if STARTTLS is used, this is the name of the header in which the certificate
     * of the sender is put
     */
    public static final String MAIL_HEADER_CERT = "X-Sender-Certificate";

    public static final String DOMAIN_GROUP = "DomainGroup";

    public static final String MAIL_BODY_MANAGER_NAME = "MailBodyManagerName";

    public static final String MAIL_LISTENER_CHAIN = "ListenerChain";

    public static final String RELAY_BY_ADDR = "RelayByAddr";

    public static final String RELAY_BY_DOMAIN = "RelayByDomain";

}
