/*
 * Bunisoft the Open Source Communications Company
 * Copyright 2007, Bunisoft LLC, and individual contributors as
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
package org.buni.meldware.mail.api;

import java.util.List;

import org.buni.meldware.mail.message.Body;
import org.buni.meldware.mail.message.MailAddress;

/**
 * Represents an actual message to be sent.  This isn't a Mail 
 * (which does use this interface) but its grandpappy.  
 * 
 * @author Andrew C. Oliver
 * @author Michael Barker <mbarker@buni.org>
 * @version $Revision: 1.2 $
 */
public interface Message {

    /**
     * Unique identifier for the message.
     * 
     * @return
     */
    public String getMessageId();
    
    public enum RecipientType { TO, CC, BCC };
    
    public List<MailAddress> getTo();
    
    //public List<MailAddress> getCC();
    
    public MailAddress getSender();
    
    /**
     * Gets the subject of the mail.
     * 
     * @return
     */
    public String getSubject();
    
    public List<? extends Body> getBody();
    
    public long getSize();
    
    public boolean isMime();
    
}
