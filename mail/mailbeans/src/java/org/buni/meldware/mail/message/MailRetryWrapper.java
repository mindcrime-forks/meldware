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
package org.buni.meldware.mail.message;

import java.util.List;

/**
 * Thin wrapper around the Mail class to handle things like SMTP retries etc.
 * The idea is that the first time a mail fails the mailhandlers create an instance of 
 * this class and send the message bak to the maillistener that initiated the request.
 * The message is then retried with the wrapper. The same wrapper instance is then used on
 * following attempts. 
 * The to address of the wrapper contains the recipients we were not yet able to send to.
 * @author Kabir Khan
 * @version $Revision: 1.1 $
 */
public class MailRetryWrapper extends Mail {

    private static final long serialVersionUID = 3832618491294398002L;

    /** The number of retries attmpted so far */
    int retries;

    /** Whether the local or remote envelope tos should be retried (since Remote- and 
     * LocalDeliveryMDB know nothing about each other)*/
    int redeliveryDestination;

    public static int REDELIVERY_LOCAL = 1;

    public static int REDELIVERY_REMOTE = 2;

    public MailRetryWrapper(Mail mail, int dest)
            throws IllegalArgumentException {
        super(mail);

        if (dest != REDELIVERY_LOCAL && dest != REDELIVERY_REMOTE) {
            throw new IllegalArgumentException(
                    "dest must be REDELIVERY_LOCAL (1) or REDELIVERY_REMOTE (2)");
        }

        this.redeliveryDestination = dest;
    }

    public void setTos(List<EnvelopedAddress> tos) {
        super.to = tos;
    }

    public void increaseRetries() {
        retries++;
    }

    public int getRetryNumber() {
        return retries;
    }

    public int getRedeliveryDestination() {
        return redeliveryDestination;
    }

}
