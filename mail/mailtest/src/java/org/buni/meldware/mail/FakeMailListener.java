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
package org.buni.meldware.mail;

import org.buni.meldware.mail.maillistener.SysOutMailListener;
import org.buni.meldware.mail.message.Message;

/**
 * This class is used in the TestMailListenerChain.  It 
 * basically just puts the message on the TestMailListenerChain
 * as a callback.
 * 
 * @author acoliver
 *
 */
public class FakeMailListener extends SysOutMailListener {
    private TestMailListenerChain tmlc;
    
    /**
     * construct a fake mail listener which will call back 
     * and set the mail on hte TestMailListenerChain
     * @param tmlc instance of TestMailListenerChain
     */
    public FakeMailListener(TestMailListenerChain tmlc) {
        this.tmlc = tmlc;
    }
    
    
    /* (non-Javadoc)
     * @see org.buni.meldware.mail.MailListener#send(org.buni.meldware.mail.message.Message)
     */
    public Message send(Message msg) throws MailException {
            tmlc.setReceived(msg);
        return msg;
    }
    
}