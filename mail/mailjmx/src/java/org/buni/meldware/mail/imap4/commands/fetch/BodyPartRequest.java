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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.buni.meldware.common.util.ArrayUtil;
import org.buni.meldware.mail.api.FolderMessage;
import org.buni.meldware.mail.imap4.IMAP4OutputStream;

/**
 * Request for a a body part.
 * 
 * @version $Revision: 1.10 $
 * @author Michael Barker
 */
public class BodyPartRequest extends FetchPart {

    String _name;

    private BodyPart.Type _type = BodyPart.Type.ALL;

    boolean _peek = false;

    private final List<String> _parts = new ArrayList<String>();
    
    private final List<Integer> address = new ArrayList<Integer>();

    int _rangeStart = -1;

    int _rangeLength = -1;

    public String getName() {
        return _name;
    }

    @Override
    public boolean requiresMessage() {
        return true;
    }

    public BodyPart.Type getType() {
        return _type;
    }

    public boolean getPeek() {
        return _peek;
    }

    public void setName(String name) {
        _name = name;
    }

    public void setType(BodyPart.Type type) {
        _type = type;
    }

    public void setPeek(boolean peek) {
        _peek = peek;
    }

    public void addPart(String part) {
        _parts.add(part);
    }

    public boolean hasRange() {
        return ((_rangeStart >= 0) && (_rangeLength > 0));
    }

    public int getRangeStart() {
        return _rangeStart;
    }

    public int getRangeLength() {
        return _rangeLength;
    }
    

    public void setRange(String start, String length) {
        try {
            _rangeStart = Integer.parseInt(start);
            _rangeLength = Integer.parseInt(length);
        } catch (NumberFormatException e) {
        }
    }

    public void setRange(int start, int length) {
        _rangeStart = start;
        _rangeLength = length;
    }
    
    public Iterator getPartIterator() {
        return _parts.iterator();
    }
    
    public List<String> getParts() {
        return _parts;
    }
    
    public void appendAddressId(int i) {
        address.add(i);
    }
    
    public void appendAddressId(Integer i) {
        address.add(i);
    }
    
    public List<Integer> getAddress() {
        return address;
    }
    
    /* (non-Javadoc)
     * @see org.buni.meldware.mail.imap4.commands.fetch.FetchPart#fetch(org.buni.meldware.mail.api.FolderMessage, org.buni.meldware.mail.imap4.IMAP4OutputStream)
     */
    @Override
    public void fetch(FolderMessage msg, IMAP4OutputStream out) {
        BodyPart bPart = new BodyPart();
        bPart.fetch(msg, this, out);
    }

    public String toString() {
        StringBuffer result = new StringBuffer();
        result.append(_name);
        result.append("[");

        result.append(ArrayUtil.join(address, "."));
        if (address.size() > 0 && _type != BodyPart.Type.ALL) {
            result.append(".");
        }
        
        if (_type != null) {
            switch (_type) {
            case HEADER_FIELDS:
                result.append("HEADER.FIELDS");
                break;
            case HEADER_FIELDS_NOT:
                result.append("HEADER.FIELDS.NOT");
                break;
            case ALL:
                break;
            default:
                result.append(_type);
            }
        }

        if ((_parts != null) && (_parts.iterator().hasNext())) {
            result.append(" (");

            for (Iterator i = getPartIterator(); i.hasNext();) {
                result.append("\"" + i.next().toString().toUpperCase() + "\"");
                if (i.hasNext())
                    result.append(' ');
            }

            result.append(")");
        }

        result.append("]");

        if (hasRange()) {
            result.append("<");
            result.append(_rangeStart);
            result.append(">");            
        }

        return result.toString();
    }
}
