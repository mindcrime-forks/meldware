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

import java.util.HashMap;
import java.util.Map;

import javax.mail.Message;
import javax.mail.Message.RecipientType;

/**
 * Enveloped Address lets us set the visibility/type of the address
 * Enveloped addresses have transient "attributes" which are used to route
 * mails around the server (but are not stored).  Currently these are used
 * only for the "ServerActions".
 * 
 * @author Andrew C. Oliver (acoliver ot buni dat org)
 * @version $Revision: 1.4 $
 */
public class EnvelopedAddress extends MailAddress {

	private static final long serialVersionUID = 3258415014871643702L;
	private Message.RecipientType type;
    private boolean local;
    private boolean rcpt;
    //attributes are not serialized, transient routing properties
    //which control how the mail is moved about the server or 
    //persisted
    private Map<String,Object> attributes;


    /**
     * Constructor creates an enveloped address from a regular one. If it is already an EnvelopedAddress 
     * it acts as a copy constructor.
     * @param orig address to envelope 
     */
    public EnvelopedAddress(MailAddress orig) {
        super(orig);
        if (orig instanceof EnvelopedAddress) {
            EnvelopedAddress maorig = (EnvelopedAddress)orig; 
            this.type = maorig.type;
            this.local = maorig.local;
            this.attributes = maorig.attributes;
        } else {
            this.attributes = new HashMap<String,Object>();
        }
    }

    public EnvelopedAddress(MailAddress address, RecipientType type, boolean rcpt) { 
       this(address,type);
       this.rcpt = rcpt;
    }
    
    /**
     * Constructor takes an existing address and assigns a recipient type
     * @param address to envelope
     * @param type of recipient (BCC, CC, TO)
     */
    public EnvelopedAddress(MailAddress address, RecipientType type) {
        this(address);
        this.type = type;
    }
    
    /**
     * Construct using an SMTP address as a string.
     * 
     * @param sAddr
     * @param type
     */
    public EnvelopedAddress(String sAddr, RecipientType type) {
        this(MailAddress.parseSMTPStyle(sAddr), type);
    }

    /**
     * Specify what type of recipient this is (TO, CC, BCC)
     * @param type of recipient
     */
    public void setType(Message.RecipientType type) {
        this.type = type;
    }
    
    /**
     * @return type of recipient (TO, CC, BCC)
     */
    public Message.RecipientType getType() {
        return this.type;
    }
    
    /**
     * specify whether this address is local or remote
     * @param local or remote status
     */
    public void setLocal(boolean local) {
        this.local = local;
    }
    
    /**
     * @return whether this address is local or not
     */
    public boolean getLocal() {
        return local;
    }
    

    /**
     * Is this a recipient of the mail (not just something to print on the header)
     * @return recipient status
     */
    public boolean getRcpt() {
        return rcpt;
    }

    /**
     * Mark this is the recipient of the mail
     * @param rcpt true if we are actually going to deliver it to that account
     */
    public void setRcpt(boolean rcpt) {
        this.rcpt = rcpt;
    }

	/**
     * set an attribute (transient key/value used for internal routing)
     * @param key of the attribute to get
     */
    public Object getAttribute(String key) {
        return this.attributes.get(key);
    }

    /**
     * set an attribute (transient key/value used for internal routing)
     * @param key of the attribute to set
     * @param value to set the attribute to (any object)
     */ 
    public void setAttribute(String key, Object value) {
        this.attributes.put(key, value);
    }
    
    /**
     * Wraps a MailAddress into an EnvelopedAddress.  Will simply return
     * the same object if it is already a EnvelopedAddress.
     * 
     * @param ma
     * @return
     */
    public static EnvelopedAddress wrap(MailAddress ma) {
    	if (ma instanceof EnvelopedAddress) {
            return (EnvelopedAddress) ma;
    	} else {
            return new EnvelopedAddress(ma);
    	}
    }

}
