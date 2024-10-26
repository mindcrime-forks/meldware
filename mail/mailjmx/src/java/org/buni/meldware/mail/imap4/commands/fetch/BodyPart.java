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

import static org.buni.meldware.common.util.ArrayUtil.join;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.buni.meldware.common.logging.Log;
import org.buni.meldware.mail.MailException;
import org.buni.meldware.mail.StreamWriteException;
import org.buni.meldware.mail.api.FolderBody;
import org.buni.meldware.mail.api.FolderEntity;
import org.buni.meldware.mail.api.FolderMessage;
import org.buni.meldware.mail.imap4.IMAP4OutputStream;
import org.buni.meldware.mail.util.io.PartialCopier;
import org.buni.meldware.mail.util.io.SizeLimitedOutputStream;
import org.buni.meldware.mail.util.io.SizeLimitedOutputStream.LimitReachedException;

/**
 * Handles the formatting of a BodyPartRequest.
 * 
 * @version $Revision: 1.19 $
 * @author Michael Barker
 */
public class BodyPart {

    public enum Type {
        ALL,
        SIZE,
        HEADER,
        HEADER_FIELDS,
        HEADER_FIELDS_NOT,
        TEXT,
        MIME,
        STRUCTURE
    };
    
    public final static String ENDL = "\r\n";
    private final static Log log = Log.getLog(BodyPart.class);
    private final static String SIZE = "SIZE";
    private final static String HEADER = "HEADER";
    private final static String TEXT = "TEXT";
    private final static List<String> EMPTY = new ArrayList<String>(0);

	public boolean canHandle(Object req) {
        return ((req instanceof BodyPartRequest) || (req instanceof RFC822PartRequest));
    }

    public void fetch(FolderMessage msg, BodyPartRequest breq, 
            IMAP4OutputStream out) {
        try {
            out.write(breq.toString());
            out.write(" ");
            
            List<Integer> address = breq.getAddress();
            
            FolderEntity entity;
            if (address.size() == 0) {
                entity = msg;
            } else {
                int[] addr = new int[address.size()];
                for (int i = 0; i < addr.length; i++) {
                    addr[i] = address.get(i) - 1;
                }
                entity = msg.getBodyPart(addr);                
            }
            
            Partial p = null;
            if (breq.hasRange()) {
                p = new Partial(breq.getRangeStart(), breq.getRangeLength());
            }
            
            if (entity != null) {
                Type type = breq.getType();
                log.debug("Fetch type: %s, address: %s, partial: %s", type, 
                        join(address, "[", ".", "]"), p);
                
                switch (type) {
                case HEADER:
                    fetchHeader(entity.getHeaders(), out, EMPTY, false, p);
                    break;
                case HEADER_FIELDS:
                    fetchHeader(entity.getHeaders(), out, breq.getParts(), true, p);
                    break;
                case HEADER_FIELDS_NOT:
                    fetchHeader(entity.getHeaders(), out, breq.getParts(), false, p);
                    break;
                case TEXT:
                    if (entity.isMessage()) {
                        fetchText(entity, out, p);
                    } else {
                        log.warn("BODY type TEXT is only supported for messages");
                        writeNil(out);
                    }
                    break;
                case STRUCTURE:
                    MessagePropertyPart mpp = new MessagePropertyPart("BODY");
                    mpp.fetch(msg, out);
                case ALL:
                    fetchAll(entity, out, p);
                    break;
                case MIME:
                    if (entity instanceof FolderBody) {
                        fetchMime((FolderBody) entity, out, p);               
                    } else {
                        log.warn("BODY type MIME is not supported for messages");
                        writeNil(out);
                    }
                    break;
                default:
                    log.warn("BODY type %s is not supported", type);
                    writeEmpty(out);
                }
                
            } else {
                writeNil(out);
            }
        } catch (IOException e) {
            throw new StreamWriteException(e);
        }
    }
    
    public void fetch(FolderMessage msg, RFC822PartRequest part, 
            IMAP4OutputStream out) {
        try {
            out.write(part.toString());
            out.write(" ");
            
            // TODO: Should this be more extensive.
            if (SIZE.equals(part.getType())) {
                out.write(fetchSize(msg));
            } else if (HEADER.equals(part.getType())) {
                throw new MailException("Implement RFC822.HEADER");
            } else if (TEXT.equals(part.getType())) {
                throw new MailException("Implement RFC822.TEXT");
            } else {
                log.warn("RFC822.%d is not supported", part.getType());
                try {
                    out.write("\"\"");
                } catch (IOException e) {
                    throw new StreamWriteException(e);
                }            
            }
        } catch (IOException e) {
            throw new StreamWriteException(e);
        }
    }

    protected String fetchSize(FolderEntity message) {
        return String.valueOf(message.getSize());
    }

    
    private final void writeNil(IMAP4OutputStream out) {
        try {
            out.write("NIL");
        } catch (IOException e) {
            throw new StreamWriteException(e);
        }            
    }
    
    private final void writeEmpty(IMAP4OutputStream out) {
        try {
            out.write("\"\"");
        } catch (IOException e) {
            throw new StreamWriteException(e);
        }            
    }
    
    protected void fetchText(FolderEntity part, IMAP4OutputStream out, 
            Partial partial) {
        try {
            if (partial != null) {
                int off = partial.getOff();
                int len = partial.getLen();
                if (off < part.getBodySize()) {
                    long partSize = Math.min(len, Math.max(0, part.getBodySize() - off));
                    out.write("{");
                    out.write(String.valueOf(partSize));
                    out.write("}");
                    out.write(ENDL);
                    try {
                        OutputStream outNew = new SizeLimitedOutputStream(out, off, len);
                        part.printText(outNew);
                    } catch (LimitReachedException e) {}
                } else {
                    out.write("\"\"");
                }                
            } else {
                out.write("{");
                out.write(String.valueOf(part.getBodySize()));
                out.write("}");
                out.write(ENDL);
                part.printText(out);
            }
        } catch (IOException e) {
            throw new StreamWriteException(e);
        }
    }
    
    /**
     * Fetch the mime header for the body part.
     * 
     * @param body
     * @param out
     * @param partial
     */
    protected void fetchMime(FolderBody body, IMAP4OutputStream out, 
            Partial partial) {
        String mime = body.getMimeheader();
        if (mime != null) {
            mime = mime.replaceAll("[\\u0080-\\uffff]", "?");
            if (partial != null) {
                mime = partial.subString(mime).toString();
            }
        }
        String result = format(mime);
        try {
            out.write(result);
        } catch (IOException e) {
            throw new StreamWriteException(e);
        }
    }
    
    /**
     * Determine if a header is in the list.
     * 
     * @param parts
     * @param test
     * @return
     */
    private boolean match(List<String> parts, String test) {
        
        for (String part : parts) {
            if (test.startsWith(part.toUpperCase())) {
                return true; 
            }
        }
        
        return false; 
    }

    /**
     * Fetch the specified headers.
     * 
     * @param headers
     * @param out
     * @param parts
     * @param include
     * @param partial
     */
    protected void fetchHeader(String[] headers, IMAP4OutputStream out, 
            List<String> parts, boolean include, Partial partial) {
        
        String result;
        if (headers == null) {
            result = "NIL";
        } else if (headers.length == 0) {
            result = "\"\"";
        } else {
            StringBuilder resultB = new StringBuilder();
            for (String header : headers) {
                if ((include && match(parts, header.toUpperCase())) 
                    || (!include && !match(parts, header.toUpperCase()))) {
                    resultB.append(header);
                    resultB.append("\r\n");
                }
            }
            resultB.append("\r\n");
            
            if (partial != null) {
                result = partial.subString(resultB).toString();
            } else {
                result = resultB.toString();
            }
            // XXX: This is a bit bogus.
            result = format(result.replaceAll("[\\u0080-\\uffff]", "?"));            
        }
        
        try {
            out.write(result);
        } catch (IOException e) {
            throw new StreamWriteException(e);
        }
    }

    /**
     * Write a partial message to the output stream.
     * 
     * @param breq
     * @param message
     * @param out
     * @param off
     * @param len
     */
    public void fetchAll(FolderEntity message, IMAP4OutputStream out, 
            Partial partial) {
        try {
            if (partial != null) {
                int off = partial.getOff();
                int len = partial.getLen();
                if (off < message.getSize()) {
                    long partSize = Math.min(len, Math.max(0, message.getSize() - off));
                    out.write("{");
                    out.write(String.valueOf(partSize));
                    out.write("}");
                    out.write(ENDL);
                    try {
                        message.print(out, new PartialCopier(off, len));
                    } catch (LimitReachedException e) {}
                } else {
                    out.write("\"\"");
                }                
            } else {
                out.write("{");
                out.write(String.valueOf(message.getSize()));
                out.write("}");
                out.write(ENDL);
                message.print(out);
            }
        } catch (IOException e) {
            throw new StreamWriteException(e.getMessage(), e);
        }
    }

    /**
     * Format some content, will handle empty strings, nulls, etc...
     * 
     * @param content
     * @return
     */
    private String format(CharSequence content) {
    	if (content == null) {
    		return "NIL";
    	} else if (content.equals("")) {
            return "\"\"";
        } else {
            return "{" + content.length() + "}\r\n" + content;
        }
    }
    
    private static class Partial {
        private final int off;
        private final int len;

        public Partial(int off, int len) {
            this.off = off;
            this.len = len;
        }
        
        public int getOff() {
            return off;
        }
        
        public int getLen() {
            return len;
        }
        
        public CharSequence subString(CharSequence s) {
            if (s.length() < off) {
                return "";
            } else if (off + len > s.length()) {
                return s.subSequence(off, s.length());
            } else {
                return s.subSequence(off, off + len);
            }
        }
        
        @Override
        public String toString() {
            return "<" + off + "." + len + ">";
        }
    }    
}
