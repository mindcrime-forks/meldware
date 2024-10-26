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

/**
 * POP3 Constants contains constants for use throughout, most classes will just implement this interface
 * and use them directly.
 * 
 * @author Kabir Khan
 * @version $Revision: 1.1 $
 */
public interface POP3Constants {
    //properties
    /**
     * Server Name for use in Protocol.getProperty
     */
    public final static String SERVER_NAME = "Servername";

    /**
     * returns the user repository for use in authentication/authorization use with Protocol.getProperty
     */
    public final static String USER_REPOSITORY = "UserRepository";

    /**
     * returns the user repository for use in authentication/authorization use with Protocol.getProperty
     */
    public final static String APOP_USER_REPOSITORY = "APOPUserRepository";

    /**
     * returns whether TLS is required for POP3 or not (on authentication)
     */
    public final static String REQUIRE_TLS = "RequireTls";

    /**
     * determines if the use of the STARTTLS-Extension is required
     */
    //public final static String REQUIRE_STARTTLS = "requireSTARTTLS";
    /**
     * determnines id the client needs to pass a certificate during SSL-Handshake after STARTTLS
     */
    public final static String REQUIRE_CLIENT_CERT = "RequireClientCert";

    /**
     * enable or don't enable TLS (different from requireSTARTTLS as it determines whether we'll even allow it)
     */
    public static final String TLS_SUPPORT = "TlsEnabled";

    /** 
     * The socket factory to be used to create sockets used for TLS
     */
    public static final String SSL_SOCKET_FACTORY = "SslSocketFactory";

    /**
     * The &lt;threadid.time@servername&gt; string for APOP
     */
    public static final String APOP_ID = "ApopId";

}
