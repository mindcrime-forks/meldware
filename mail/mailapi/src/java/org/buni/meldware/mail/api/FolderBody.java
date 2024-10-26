/*
 * Bunisoft the Open Source Communications Company
 * Copyright 2007, Bunisoft LLC.,
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
package org.buni.meldware.mail.api;

import java.util.List;

import org.buni.meldware.mail.message.Body;
import org.buni.meldware.mail.message.MailHeaders;

/**
 * Definition of a body for a message stored in a folder.
 * 
 * @author Michael Barker
 */
public interface FolderBody extends Body, FolderEntity {

    public enum BodyType { SIMPLE, MESSAGE, MULTIPART }
    
    /**
     * Gets the mime header.
     * 
     * @return
     */
    String getMimeheader();
    
    boolean isMime();
        
    /**
     * True if this is a nested message.  Could be a simple message or
     * a multipart message.
     * 
     * @return
     */
    boolean isMessage();
        
    public String getCharset();
    
    public String getFormat();
    
    public String getName();
    
    public String getContentTransferEncoding();
    
    public String getContentDisposition();
    
    public String getFilename();
    
    public BodyType getType();
    
    public List<FolderBody> getChildren();
    
    public MailHeaders getMailHeaders();
    
    public boolean isMultipart();
}


