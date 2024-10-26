/*
 * Bunisoft the Open Source Communications Company
 * Copyright 2006, Bunisoft Inc., and individual contributors as
 * indicated by the @authors tag.  See the copyright.txt in the 
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; version 2.1 of
 * the License.
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

import org.buni.meldware.mail.MailException;
import org.buni.meldware.mail.util.io.Copier;
import org.buni.meldware.mail.util.io.SimpleCopier;

/**
 * Provides a set of default actions for the mail creation listener.
 * 
 * @author Michael.Barker
 *
 */
public class MailCreateAdapter implements MailCreateListener {

    private int bytesRead = -1;
    private MailHeaders mailHeaders = null;

    /**
     * @see org.buni.meldware.mail.message.MailCreateListener#onHeadersParsed(int, org.buni.meldware.mail.message.MailHeaders)
     */
    public void onHeadersParsed(int bytesRead, MailHeaders mh) {
        this.bytesRead = bytesRead;
        this.mailHeaders = mh;
        postHeadersParsed();
    }
    
    /**
     * Override this method to do things like adding extra headers.
     *
     */
    protected void postHeadersParsed() {
    }
    
    
    /**
     * Default implemenation uses a SimpleCopier.
     * 
     * @see org.buni.meldware.mail.message.MailCreateListener#getCopier()
     */
    public Copier getCopier() {
        assert bytesRead > -1;
        assert mailHeaders != null;
        
        return new SimpleCopier();
    }

    /**
     * Default implementation to get the from address for a mail.  Extracts
     * the address from the "From" header.
     * 
     * @see org.buni.meldware.mail.message.MailCreateListener#getFrom()
     */
    public MailAddress getFrom() {
        assert bytesRead > -1;
        assert mailHeaders != null;
        
        MailAddress from;
        String[] sFromArr = mailHeaders.getHeader(StandardMailHeaders.FROM);
        if (sFromArr != null && sFromArr.length > 0) {
            String sfrom = sFromArr[0];
            from = MailAddress.parseSMTPStyle(sfrom);
        } else {
            from = MailAddress.parseSMTPStyle("");
        }
        return from;
    }

    /**
     * Default implementation to get the list of to addresses, extract from
     * the "To" header.
     * 
     * @see org.buni.meldware.mail.message.MailCreateListener#getTo()
     */
    public MailAddress[] getTo() {
        assert bytesRead > -1;
        assert mailHeaders != null;
        
        String[] ato = mailHeaders.getHeader(StandardMailHeaders.TO);
        MailAddress[] to = new MailAddress[ato.length];
        if (ato != null) {
           for (int i = 0; i < ato.length; i++) {
               to[i] = MailAddress.parseSMTPStyle(ato[i]);
           }
        }
        return to;
    }

    /**
     * Default value is false.
     * 
     * @see org.buni.meldware.mail.message.MailCreateListener#isMime()
     */
    public boolean isMime() {
        return false;
    }


    /**
     * @see org.buni.meldware.mail.message.MailCreateListener#verifyHeaders()
     */
    public void verifyHeaders() throws MailException {
    }


    /**
     * @return the bytesRead
     */
    public int getBytesRead() {
        return bytesRead;
    }


    /**
     * @return the mailHeaders
     */
    public MailHeaders getMailHeaders() {
        return mailHeaders;
    }

}
