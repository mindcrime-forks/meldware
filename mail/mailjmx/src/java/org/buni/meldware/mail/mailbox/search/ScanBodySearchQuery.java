/*
 * Bunisoft the Open Source Communications Company
 * Copyright 2006, Bunisoft Inc., and individual contributors as
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
package org.buni.meldware.mail.mailbox.search;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.buni.meldware.mail.mailbox.FolderEntry;
import org.buni.meldware.mail.mailbox.MessageBody;
import org.buni.meldware.mail.message.MailBodyManager;

/**
 * Scans the body of an email looking for the specified substring
 * 
 * @author Michael.Barker
 *
 */
public class ScanBodySearchQuery extends SearchQuery {
    
    private final String value;
    private final boolean scanHeaders;

    public ScanBodySearchQuery(String value, boolean scanHeaders) {
        this.value = value.toUpperCase();
        this.scanHeaders = scanHeaders;
    }
    
    public ScanBodySearchQuery(String value) {
        this(value, false);
    }

    @Override
    public Set<Long> getResults(SearchContext context) {
        EntityManager em = context.getEntityManager();
        Query q = em.createNamedQuery(FolderEntry.BY_FOLDER);
        q.setParameter(1, context.getFolderId());
        @SuppressWarnings("unchecked")
        List<FolderEntry> fes = (List<FolderEntry>) q.getResultList();
        Set<Long> ids = new HashSet<Long>();
        
        for (FolderEntry fe : fes) {
            if (scanHeaders && fe.getMessage().getHeader() != null 
                    && fe.getMessage().getHeader().toUpperCase().indexOf(value) != -1) {
                ids.add(fe.getUid());
                continue;
            }
            List<MessageBody> bodies = fe.getMessage().getMessageBodies();
            if (scan(bodies, context.getMailBodyManager())) {
                ids.add(fe.getUid());
            }
        }
        
        return ids;
    }
    
    /**
     * Recursively scan all of the bodies of the message.  Return instantly 
     * if a message is found.
     * 
     * @param bodies
     * @param mgr
     * @return
     */
    private boolean scan(List<MessageBody> bodies, MailBodyManager mgr) {
        for (MessageBody body : bodies) {
            if (scanHeaders && body.getHeader() != null 
                    && body.getHeader().indexOf(value) != -1) {
                return true;
            }
            if (!body.getBodyless() && isText(body.getMimeType())) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                mgr.write(body, out);
                Charset cs;
                try {
                    cs = Charset.forName(body.getCharset());                    
                } catch (UnsupportedCharsetException e) {
                    // Fallback to platform default.
                    // Centralise this.
                    cs = Charset.defaultCharset();
                }
                CharBuffer cb = cs.decode(ByteBuffer.wrap(out.toByteArray()));
                if (cb.toString().toUpperCase().indexOf(value) != -1) {
                    return true;
                }
            }
            if (body.getChildren().size() > 0 && scan(body.getChildren(), mgr)) {
                return true;
            }
        }
        return false;
    }

    private boolean isText(String mimeType) {
        return mimeType.startsWith("text");
    }

}
