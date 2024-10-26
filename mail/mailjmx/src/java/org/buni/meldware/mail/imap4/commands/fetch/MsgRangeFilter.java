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
 *
 * 
 * Additionally, portions of this IMAP code are from the epost project at epostmail.org.
 * These sources are derivitive works in which the original is included under these terms:
 * ----------------------------------------------------------------------------------------
 * "Free Pastry" Peer-to-Peer Application Development Substrate
 *
 * Copyright (C) 2002, Rice University
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are 
 * permitted provided that the following conditions are met:
 *
 *    * Redistributions of source code must retain the above copyright notice, this 
 *      list of conditions and the following disclaimer.
 *    * Redistributions in binary form must reproduce the above copyright notice, 
 *      this list of conditions and the following disclaimer in the documentation and/or 
 *      other materials provided with the distribution.
 *    * Neither the name of Rice University (RICE) nor the names of its contributors may be 
 *      used to endorse or promote products derived from this software without specific prior 
 *      written permission.
 *
 *
 * This software is provided by RICE and the contributors on an "as is" basis, without any 
 * representations or warranties of any kind, express or implied including, but not limited 
 * to, representations or warranties of non-infringement, merchantability or fitness for a 
 * particular purpose. In no event shall RICE or contributors be liable for any direct, 
 * indirect, incidental, special, exemplary, or consequential damages (including, but not 
 * limited to, procurement of substitute goods or services; loss of use, data, or profits; 
 * or business interruption) however caused and on any theory of liability, whether in 
 * contract, strict liability, or tort (including negligence or otherwise) arising in 
 * any way out of the use of this software, even if advised of the possibility of such damage.
 */
package org.buni.meldware.mail.imap4.commands.fetch;

import java.util.regex.Pattern;

import org.buni.meldware.mail.api.FolderMessage;

/**
 * @version $Revision: 1.4 $
 */
public class MsgRangeFilter extends MsgFilter {

    static final Pattern TWO_PART = Pattern.compile("(\\d+|\\*):(\\d+|\\*)");

    int _top;

    int _bottom;

    boolean _isUID;

    boolean _unbounded;
    
    public MsgRangeFilter(int a, int b) {
        _bottom = a;
        _top = b;
        _unbounded = (a == -1 || b == Integer.MAX_VALUE);
    }


    public int getTop() {
        return this._top;
    }

    public int getBottom() {
        return this._bottom;
    }
    
    public boolean isUnbounded() {
        return _unbounded;
    }

    public boolean includes(FolderMessage msg) {
        // TODO fix this
        // int msgValue = (_isUID ? msg.getId() : msg.getSequenceNumber());
        long msgValue = msg.getUid();

        return msgValue >= _bottom && msgValue <= _top;
    }

    public boolean includes(FolderMessage msg, int seqNum) {
        return seqNum >= _bottom && seqNum <= _top;
    }
}