/*
 * Bunisoft the Open Source Communications Company
 * Copyright 2006, Bunisoft LLC., and individual contributors as
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
package org.buni.meldware.mail.message;

import java.io.InputStream;
import java.io.OutputStream;

import javax.management.ObjectName;

import org.buni.meldware.mail.MailException;
import org.buni.meldware.mail.store.Store;
import org.buni.meldware.mail.util.io.Copier;

/**
 * MBean interface for the MailBody manager.
 * 
 * @author Michael Barker <mailto:mike@middlesoft.co.uk>
 * @version $Revision: 1.8 $
 */
public interface MailBodyManager {
    Store getStore();

    int getBufferSize();
    void setStore(Store store);

    void setBufferSize(int bufferSize);

    Body createMailBody(Long storeId)
            throws MailException;

    Body createMailBody() throws MailException;
    
    /**
     * Write this whole Mail Body out to the supplied output stream.
     * 
     * @param out
     * @throws MailException
     */
    void write(Body body, OutputStream out) throws MailException;

    void write(Body body, OutputStream out, Copier copier) throws MailException;

    /**
     * Read into this Mail Body from the supplied input stream.  Will
     * read until the stream has ended.
     * 
     * @param in
     * @throws MailException
     */
    int read(Body body, InputStream in) throws MailException;

    int read(Body body, InputStream in, Copier copier) throws MailException;
    
    /**
     * @throws MailException 
     * 
     */
    InputStream getInputStream(Body body) throws MailException;
    
    int getSize(Body body) throws MailException;
    
    public ObjectName getServiceName();
    
    long addReference(Body body);
    
    void removeReference(long reference, Body body);
    
    void purge();
    
}
