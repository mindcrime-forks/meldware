/*
 * Bunisoft the Open Source Communications Company
 * Copyright 2006, Bunisoft LLC.,
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

import java.io.UnsupportedEncodingException;

/**
 * Interface for basic mail-header functionality.
 * User does not have to worry about RFC822 compliance.
 * @author Michael Krause 
 * @author Andrew C. Oliver <acoliver ot jboss dat org> - Javadoc comments only! :-(
 * @version $Revision: 1.4 $
 */
public interface MailHeaders extends Iterable<String> {
    
    
    /**
     * Common header key for Subject
     */
    public static final String HEADER_SUBJECT = "Subject";
    
    /**
     * Get all headers matching the key.  There could be more than one but 
     * generally the array will have one element.
     * 
     * @param name of header
     * @return values of any matching headers. (the keys are not returned)
     */
    public java.lang.String[] getHeader(java.lang.String name);
    
    /**
     * Add a header key/value pair
     * 
     * @param name of header
     * @param value of header
     */
    public int addHeader(java.lang.String name, java.lang.String value);
    
    /**
     * Add a header key/value pair only if it does not already exist.
     * 
     * @param key
     * @param value
     */
    public boolean addIfAbsent(String key, String value);
    
    /**
     * @return an iterator of all header lines
     */
    
    public java.util.Iterator getAllHeaderLines();
    
	/**
     * Remove the specified header.
     * 
	 * @param string
	 */
	public void removeHeader(String string);
    
    /**
     * Get the total output size of the headers.  Will be different to the
     * number of bytes read when parsing.
     * 
     * @param encoding
     * @return
     * @throws UnsupportedEncodingException
     */
    public int size(String encoding) throws UnsupportedEncodingException;
    
    /**
     * Get the mime boundry header value.
     * 
     * @return
     */
    public String getBoundary();
    
    /**
     * Get the to address, null if not defined.
     * 
     * @return
     */
    public String getTo();
    
    public String getCC();
    
    public String getBCC();
    
    /**
     * Get the from address, null if not defined.
     * @return
     */
    public String getFrom();
    
    /**
     * Get the sender of the email, null if not defined.
     * @return
     */
    public String getSender();
    
    /**
     * Get the subject, null if not defined.
     * 
     * @return
     */
    public String getSubject();
    
    /**
     * Get the message id.
     * @return
     */
    public String getMessageId();
    
    /**
     * Get the in reply to messageid.
     * @return
     */
    public String getInReplyTo();
    
    public String getReplyTo();
    
    /**
     * Gets the date value.
     * 
     * @return
     */
    public String getDate();
}
