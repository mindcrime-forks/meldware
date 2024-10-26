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
package org.buni.meldware.mail.pop3.handlers;

/**
 * Defines all messages written to the client
 * during a POP3 session.
 *
 * //TODO: Refactor this into some common message store.
 */
public interface POP3UserMessages {

    /** Generic affirmative respone */
    String MESSAGE_OK = "+OK";

    byte[] MESSAGE_OK_BYTES = MESSAGE_OK.getBytes();

    String MESSAGE_ENCRYPTION_REQUIRED = "-ERR Encryption required for requested authentication mechanism";

    /** Generic message for a system error */
    String MESSAGE_SERVICE_UNAVAILABLE = "-ERR Service Unavailable";

    /** Message written to the user when the current state prohibits the requested command */
    String MESSAGE_INVALID_STATE = "-ERR Command not allowed at this time";

    byte[] MESSAGE_INVALID_STATE_BYTES = MESSAGE_INVALID_STATE.getBytes();

    /** Message written to the user when a parameter is required but missing */
    String MESSAGE_ARGUMENT_MISSING = "-ERR This command requires a parameter.";

    byte[] MESSAGE_ARGUMENT_MISSING_BYTES = MESSAGE_ARGUMENT_MISSING.getBytes();

    /** Message written to the user when a parameter is invalid */
    String MESSAGE_ARGUMENT_INVALID = "-ERR The parameter is invalid.";

    byte[] MESSAGE_ARGUMENT_INVALID_BYTES = MESSAGE_ARGUMENT_INVALID.getBytes();

    /** Message written to the user when they passed in more arguments than expected */
    String MESSAGE_TOO_MANY_ARGUMENTS = "-ERR Too many parameters specified.";

    byte[] MESSAGE_TOO_MANY_ARGUMENTS_BYTES = MESSAGE_TOO_MANY_ARGUMENTS
            .getBytes();

    /** Message written to the user when the USER command is specified twice before the PASS */
    String MESSAGE_USER_ALREADY_SPECIFIED = "-ERR User already specified.";

    /** Message written to the user when the specified username is invalid */
    String MESSAGE_USER_INVALID = "-ERR Invalid user.";

    /** Message written to the user if they specifiy the PASS command before the USER command. */
    String MESSAGE_USER_REQUIRED_BEFORE_PASS = "-ERR USER command must be issued before PASS.";

    /** Message written to the user when the specified username is invalid */
    String MESSAGE_PASS_INVALID = "-ERR Invalid password.";

    /** Message written to the user when their login information failed */
    String MESSAGE_AUTH_FAILED = "-ERR Invalid username and/or password.";

    /** The user's mailbox is already locked by another client */
    String MESSAGE_MAILBOX_LOCKED = "-ERR Mailbox locked.";

    /** The messaged id specified is invalid */
    String MESSAGE_INVALID_MESSAGE_ID = "-ERR Unknown message.";

    byte[] MESSAGE_INVALID_MESSAGE_ID_BYTES = MESSAGE_INVALID_MESSAGE_ID
            .getBytes();

    /** The commit (mailbox update) failed.  Changes may not have been persisted */
    String MESSAGE_COMMIT_FAILED = "-ERR Mailbox update failed.";

    public final static byte[] ENDL = { (byte) '\r', (byte) '\n' };

    public final static byte[] TERM = { (byte) '\r', (byte) '\n', (byte) '.', (byte) '\r', (byte) '\n' };

    //TODO: refactor - its a constant but not a user message
    //public final static String USER_REPOSITORY = "user-repository";
    public final static String USER = "USER";

    //TODO: refactor - make configurable
    public final static int BLOCK_SIZE = 4096;
}
