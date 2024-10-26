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
package org.buni.meldware.mail.smtp.sender;

import java.util.ArrayList;

import org.buni.meldware.mail.message.MailAddress;

/**
 * Implementation of the SMTPResult.  
 * 
 * @author Kab
 * @see org.jboss.smtp.sender.SMTPResult
 * @version $Revision: 1.1 $
 */

public class SMTPResultImpl implements SMTPResult {
    private MailAddress address;

    private int status;

    SMTPResultImpl(MailAddress address, int status) {
        this.address = address;
        this.status = status;
    }

    /**
     * @param address addresses whose domain could not be resolved
     * @return An ArrayList containing SMTPResult entries for each address indicating that the domain could not be resolved
     */
    static ArrayList getResolveDomainErrorForAll(MailAddress[] address) {
        return getSameResultForAll(address, STATUS_DOMAIN_NOT_RESOLVED);
    }

    /**
     * @param address addresses that were sent OK
     * @return An ArrayList containing SMTPResult entries for each address indicating that they were sent successully
     */
    static ArrayList getOkForAll(MailAddress[] address) {
        return getSameResultForAll(address, STATUS_OK);
    }

    /**
     * @param address addresses that could not be sent
     * @return An ArrayList containing SMTPResult entries for each address indicating that they could not be sent
     */
    static ArrayList getErrorForAll(MailAddress[] address) {
        return getSameResultForAll(address, STATUS_ERROR);
    }

    /**
     * @param address addresses 
     * @param status The send status for the adresses
     * @return An ArrayList containing SMTPResult entries for each address with given status
     */
    private static ArrayList getSameResultForAll(MailAddress[] address,
            int status) {
        ArrayList results = new ArrayList(address.length);

        for (int i = 0; i < address.length; i++) {
            results.add(new SMTPResultImpl(address[i], status));
        }

        return results;
    }

    /* (non-Javadoc)
     * @see org.buni.meldware.mail.smtp.sender.SMTPResult#getAddress()
     */
    public MailAddress getAddress() {
        return address;
    }

    /* (non-Javadoc)
     * @see org.buni.meldware.mail.smtp.sender.SMTPResult#getStatus()
     */
    public int getStatus() {
        return status;
    }

    /* (non-Javadoc)
     * @see org.buni.meldware.mail.smtp.sender.SMTPResult#isOk()
     */
    public boolean isOk() {
        return status == 1;
    }

    /* (non-Javadoc)
     * @see org.buni.meldware.mail.smtp.sender.SMTPResult#redeliver()
     */
    public boolean redeliver() {
        return getStatus() != STATUS_OK
                && getStatus() != STATUS_INVALID_ADDRESS;
    }

    public String getStatusStr() {

        switch (getStatus()) {
        case STATUS_OK:
            return "OK";
        case STATUS_DOMAIN_NOT_RESOLVED:
            return "Domain Not Resolved";
        case STATUS_ERROR:
            return "Error";
        case STATUS_INVALID_ADDRESS:
            return "Invalid Address";
        default:
            return "Unknown";
        }

    }

    public String toString() {
        return "Status: " + getStatusStr() + ", Address: "
                + getAddress().toString();
    }

}
