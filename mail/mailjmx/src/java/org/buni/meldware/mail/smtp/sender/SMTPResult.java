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

import org.buni.meldware.mail.message.MailAddress;

/**
 * result of the SMTP send operation
 * @author Andrew C. Oliver
 * @version $Revision: 1.1 $
 */
public interface SMTPResult {
	//We should define more of these constants, they can be used to generate more
	//info in the bounce messages
	/** The sending to the mail address went OK*/
	public static final int STATUS_OK = 1;

	/** The domain of the mail address could not be resolved*/
	public static final int STATUS_DOMAIN_NOT_RESOLVED = 2;

	/** Sending to the address caused an error*/
	public static final int STATUS_ERROR = 3;
    
    /**
     * If Javamail reports the address as invalid (550, 501, 503, 551, 553)
     * set the status to this.  We should not resend for results of this status
     * but return the message to the sender.
     */
    public static final int STATUS_INVALID_ADDRESS = 4;
	
	/** address this result is for*/
	MailAddress getAddress();

	/** If the sending went ok or not
	 * @return true if all went well, false otherwise 
	 */
	boolean isOk();

	/** The status of the sending
	 * @return The numeric constant for the status of the sending to the address 
	 */
	int getStatus();
    
    /**
     * It this result requires redelivery.  Messages that are OK or have Invalid
     * addresses should not be redeliered.
     * 
     * @return
     */
    public boolean redeliver();
    
}
