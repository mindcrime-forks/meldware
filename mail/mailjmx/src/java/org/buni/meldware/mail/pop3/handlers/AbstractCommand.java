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

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import org.buni.meldware.mail.pop3.POP3ProtocolInstance;

/**
 * Provides a base class for all POP3 CmdXXX classes.
 *
 * @author Eric Daugherty
 * @version $Revision: 1.1 $
 */
public class AbstractCommand implements POP3UserMessages {

    /**
     * Verifies that the number of arguments matches the expected
     * size.  If the size does not match, the proper message is
     * written to the client and false is returned.  Otherwise, true
     * is returned.
     * @param arguments the arguments to verify.
     * @param expectedCount the number of arguments expected
     * @return true if the size of the array matches the expectedCount
     */
    boolean verifyArgumentCount(String[] arguments, PrintWriter writer,
            int expectedCount) {

        if (arguments.length < expectedCount) {
            writer.println(MESSAGE_ARGUMENT_MISSING);
            return false;
        } else if (arguments.length > expectedCount) {
            //KAB: Check used to be: ( arguments.length > 1 )
            //but POP3 commands can have more than one argument
            writer.println(MESSAGE_TOO_MANY_ARGUMENTS);
            return false;
        }

        return true;
    }

    /**
     * Verifies that the number of arguments matches the expected
     * size.  If the size does not match, the proper message is
     * written to the client and false is returned.  Otherwise, true
     * is returned.
     * @param arguments the arguments to verify.
     * @param expectedCount the number of arguments expected
     * @return true if the size of the array matches the expectedCount
     */
    boolean verifyArgumentCount(String[] arguments, OutputStream out,
            int expectedCount) throws IOException {

        if (arguments.length < expectedCount) {
            out.write(MESSAGE_ARGUMENT_MISSING_BYTES);
            out.write(ENDL);
            return false;
        } else if (arguments.length > expectedCount) {
            out.write(MESSAGE_TOO_MANY_ARGUMENTS_BYTES);
            out.write(ENDL);
            return false;
        }

        return true;
    }

    /**
     * Verifies that the current protocol state matches the
     * expected state for this command.  Writes error to users and
     * returns false if states do not match.
     *
     * @param expectedState the state required for this command to be valid.
     * @return true if the expectedState matches the current state.
     */
    boolean verifyState(POP3ProtocolInstance protocol, PrintWriter writer,
            int expectedState) {

        // Verify the protocol is in the right state.
        if (protocol.getState() != expectedState) {
            writer.println(MESSAGE_INVALID_STATE);
            return false;
        }

        return true;
    }

    /**
     * Verifies that the current protocol state matches the
     * expected state for this command.  Writes error to users and
     * returns false if states do not match.
     *
     * @param expectedState the state required for this command to be valid.
     * @return true if the expectedState matches the current state.
     * @throws IOException
     */
    boolean verifyState(POP3ProtocolInstance protocol, OutputStream out,
            int expectedState) throws IOException {

        // Verify the protocol is in the right state.
        if (protocol.getState() != expectedState) {
            out.write(MESSAGE_INVALID_STATE_BYTES);
            out.write(ENDL);
            return false;
        }

        return true;
    }

}
