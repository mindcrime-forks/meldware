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
package org.buni.meldware.mail.smtp.handlers;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Locale;
import java.util.StringTokenizer;

import org.buni.meldware.mail.smtp.SMTPConstants;
import org.buni.meldware.mail.smtp.SMTPProtocolInstance;
import org.buni.meldware.mail.smtp.SMTPRequest;
import org.buni.meldware.mail.smtp.SMTPResponse;
import org.buni.meldware.mail.util.Base64;
import org.jboss.logging.Logger;

/**
 * The AUTH command is passed often along with the authentication tokens.  This not only 
 * verifies the users identity but applies it to the state of the protocol
 * 
 * @author Andrew C. Oliver
 */
public class CmdAUTH implements SMTPHandler, SMTPConstants {

    private static final Logger jblog = Logger.getLogger(CmdAUTH.class);

    public final static String COMMAND = "AUTH";

    public final static String USERNAME = "Username:";

    public final static String PASSWORD = "Password:";

    //statically to safe cycles  (BTW this base 64 encoding Username: and Password: is stupid
    //but standard ;-)
    private final static String USERNAME_BASE64 = unsafeEncode(USERNAME);

    private final static String PASSWORD_BASE64 = unsafeEncode(PASSWORD);

    public SMTPResponse handleRequest(OutputStream out, SMTPRequest request,
            SMTPProtocolInstance protocol) throws IOException {
        jblog.debug("AUTH command handler called");
        String authType = getAuthType(request.arguments());
        jblog.debug("AuthType = " + authType);
        SMTPProtocolInstance smtpProtocol = (SMTPProtocolInstance) protocol;
        SMTPResponse response = new SMTPResponse(request, out, protocol);
        PrintWriter writer = response.getWriter();
        boolean requireTlsForAuth = protocol.isRequireTlsForAuth();

        if (smtpProtocol.getState(USER) != null) {
            writer
                    .println("503 User has previously authenticated.  Further authentication is not required!");
        } else if (authType == null) {
            writer.println("501 Usage: AUTH (authentication type) <challenge>");
        } else if (requireTlsForAuth && !smtpProtocol.isSecure()) {
            writer
                    .println("538 Encryption required for requested authentication mechanism");
        } else {
            String initialResponse = null;
            if (authType.trim().indexOf(" ") > 0) {
                initialResponse = authType.trim().substring(
                        authType.trim().indexOf(" ") + 1);
            }

            if (!authType.trim().equals(AUTH_TYPE_LOGIN)) {
                if (authType.indexOf(" ") != -1) {
                    authType = authType.substring(0, authType.indexOf(" "));
                }
            }

            authType = authType.trim().toUpperCase(Locale.US);

            if (authType.equals(AUTH_TYPE_PLAIN)) {
                doPlainAuth(initialResponse, smtpProtocol, request, writer);
            } else if (authType.equals(AUTH_TYPE_LOGIN)) {
                doLoginAuth(initialResponse, smtpProtocol, request, writer);
            } else {
                doUnknownAuth(authType, initialResponse, writer);
            }
        }
        //moved to readCommand
        //request.getInputStream().mark(2);  //hack this should be moved...maybe to the readCommand?
        // if you remove this then it will reset() and hit the wrong thing.
        writer.flush();
        return response;
    }

    /**
     * Plain authentication is the most common.  It authenticates along a Base64 hash.
     * @param initialResponse contains the actual password
     * @param protocol the instance of SMTPProtocol which we're applying to this
     * @param request the SMTPRequest which contained the AUTH request
     * @param writer the writer used to respond
     * @throws IOException
     */
    private void doPlainAuth(String initialResponse,
            SMTPProtocolInstance protocol, SMTPRequest request,
            PrintWriter writer) throws IOException {
        String userpass = null;
        String user = null;
        String pass = null;

        if (initialResponse == null) {
            writer.println("334 OK. Continue authentication");
            writer.flush();
            userpass = protocol.readResponse(request);
        } else {
            userpass = initialResponse.trim();
        }

        try {
            if (userpass != null) {
                userpass = Base64.decodeAsString(userpass);
                StringTokenizer authTokenizer = new StringTokenizer(userpass,
                        "\0");
                user = authTokenizer.nextToken();
                pass = authTokenizer.nextToken();
                authTokenizer = null;
            }
        } catch (Exception e) {
            // this means user and pass will be null
            jblog.warn("**BASE 64 caused exception in doPlainAuth");
        }

        if (jblog.isDebugEnabled()) {
            jblog.debug("user=" + user + " pass=" + pass);
        }
        if ((user == null) || (pass == null)) {
            writer
                    .println("501 Could not decode user and password for AUTH PLAIN");
        } else if (protocol.getUserRepository().test(user, pass)) {
            protocol.setState(USER, user);
            writer.println("235 Authentication Successful");
            jblog.debug("AUTH method PLAIN succeeded");
        } else {
            writer.println("535 Authentication Failed");
            jblog.debug("AUTH method PLAIN failed");
        }
    }

    /**
     * Not yet implemented.  This is a challenge and response method.  
     * @param initialResponse
     * @param writer
     */
    private void doLoginAuth(String initialResponse,
            SMTPProtocolInstance smtpProtocol, SMTPRequest request,
            PrintWriter writer) throws IOException {
        String user = null;
        String password = null;
        if (initialResponse == null) {
            writer.println("334 " + USERNAME_BASE64);
            writer.flush();
            user = smtpProtocol.readResponse(request);
        } else {
            user = initialResponse.trim();
        }

        try {
            user = Base64.decodeAsString(user);
        } catch (Exception e) {
            user = null; //ignore authentication will fail - Exchange, JAMES and POSTFIX all do this
        }

        writer.println("334 " + PASSWORD_BASE64);
        writer.flush();
        password = smtpProtocol.readResponse(request);

        try {
            password = Base64.decodeAsString(password);
        } catch (Exception e) {
            password = null; //ignore authentication will fail - Exchange, JAMES and POSTFIX all do this
        }

        if (user == null || password == null) {
            writer.println("501 Could not decode user and password");
        } else if (smtpProtocol.getUserRepository().test(user, password)) {
            smtpProtocol.setState(USER, user);
            writer.println("235 Authentication Successful");
            jblog.debug("AUTH method Login succeeded");
        } else {
            writer.println("535 Authentication Failed");
            jblog.warn("AUTH method LOGIN failed");
            jblog.warn("user was " + user);
        }
        writer.flush();

    }

    /**
     * TODO: Not yet implemented error handler
     * @param authType
     * @param initialResponse
     * @param writer
     */
    private void doUnknownAuth(String authType, String initialResponse,
            PrintWriter writer) {

    }

    private String getAuthType(Iterator iter) {
        String remote = null;
        if (iter.hasNext() && iter != null) {
            remote = (String) iter.next();
        }
        return remote;
    }

    /**
     * Use this only for init (KNOWN GOOD HARDCODED VALUES).  It swallows the exception and throws a 
     * runtime exception
     * @param src string to encode
     * @return base64 encoded string
     */
    private static String unsafeEncode(String src) {
        String retval = null;
        try {
            retval = Base64.encodeAsString(src);
        } catch (Exception e) {
            throw new RuntimeException("CODE ERROR, BASE64 failed");
        }
        return retval;
    }

}
