/*
 * Bunisoft the Open Source Communications Company
 * Copyright 2006, Bunisoft LLC., and individual contributors as
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

/**
 * A callback interface to allow processing to occur after 
 * the creation of the header items in an email.  Mainly
 * used for things like loop detection.
 * 
 * @author Michael Barker
 *
 */
public interface MailCreateListener {

    /**
     * Sets the headers once they are parsed.  This will always be
     * called before any other method in this class.
     * 
     * @param bytesRead
     * @param mh
     */
    public void onHeadersParsed(int bytesRead, MailHeaders mh);
    
    /**
     * Verifies that headers are created correctly.
     * 
     * @throws MailException
     */
    public void verifyHeaders() throws MailException;
    
    /**
     * Get the from address from the headers.
     * 
     * @param mh
     * @return
     */
    public MailAddress getFrom();
    
    /**
     * Get the list of to addresses from the header
     * @param mh
     * @return
     */
    public MailAddress[] getTo();
    
    /**
     * Get the copier that should be used to copy message to the appropriate
     * output.
     * 
     * @param mh
     * @param bufferSize
     * @return
     */
    public Copier getCopier();
    
    /**
     * Determine if the mail factory should attempt to do a mime parse.  
     * 
     * @return
     */
    public boolean isMime();

}
