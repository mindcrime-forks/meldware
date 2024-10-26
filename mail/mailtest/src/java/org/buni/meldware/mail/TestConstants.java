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
package org.buni.meldware.mail;

/**
 * @author Michael Barker
 *
 */
public class TestConstants {
    public final static String IMAP_MBEAN = "meldware.mail:type=Protocol,name=IMAP4Protocol";
    public final static String SMTP_MBEAN = "meldware.mail:type=Protocol,name=SMTPProtocol";
    public final static int SMTP_PORT = 9025;
    public final static int SMTP_SSL_PORT = 9465;
    public final static int POP_PORT = 9110;
    public final static int POP_SSL_PORT = 9995;
    public final static String MAILBOX_SERVICE_MBEAN = "meldware.mail:type=MailboxManager,name=MailboxManager";
}
