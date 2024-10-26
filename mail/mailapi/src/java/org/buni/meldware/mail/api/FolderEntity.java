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

import java.io.OutputStream;

import org.buni.meldware.mail.util.io.Copier;


public interface FolderEntity {

    /**
     * Determines if the particular message has direct content
     * that can be written to the output stream.
     * 
     * @return
     */
    boolean hasContent();
        
    /**
     * Get the folder body at index i, this is 0 indexed.  Will return null
     * if the body does not exist.
     * 
     * @param i
     * @return
     */
    FolderBody getBodyPart(int i);
    
    /**
     * Prints a message to the output stream using a default copier.
     * @param out
     */
    void print(OutputStream out);
    
    /**
     * Prints a message to the output stream using a specified copier.
     * @param out
     */
    void print(OutputStream out, Copier copier);

    /**
     * Prints the body of the message.
     * 
     * @param out
     */
    void printText(OutputStream out);
    
    /**
     * Prints the body of the message using the specified copier.
     * 
     * @param out
     */
    void printText(OutputStream out, Copier copier);

    /**
     * Returns an array of all of the headers.
     * 
     * @return
     */
    public String[] getHeaders();
    
    public boolean isMessage();

    /**
     * Returns the size of the body.
     * @return
     */
    long getBodySize();
    
    long getSize();
    
    String getBoundary();
    
    String getContentType();
    
    String getContentSubType();
    
    String getMimeType();    
}
