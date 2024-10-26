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

/**
 * Main entry point for getting a mailbox proxy.
 * 
 * @author Michael Barker
 *
 */
public interface MailboxProxyFactory {

    /**
     * Create a mailbox proxy by primary key.
     * @param id
     * @return
     */
    public Mailbox createProxy(long id, Hints hints);
    
    /**
     * Create a mailbox proxy by the alias for the mailbox.
     * @param id
     * @return
     */
    public Mailbox createProxy(String alias, boolean create, Hints hints);
    
    /**
     * Create an active mailbox proxy by primary key.
     * @param id
     * @return
     */
    public ActiveMailbox createActiveProxy(long id, Hints hints);
    
    /**
     * Create an active mailbox proxy by the alias for the mailbox.
     * @param id
     * @return
     */
    public ActiveMailbox createActiveProxy(String alias, boolean create, Hints hints);
    
}
