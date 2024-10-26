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
package org.buni.meldware.mail.maillist;

/**
 * Constants defining the properties that can be set in MailListProperties
 * @author <a href="kabirkhan@bigfoot.com">Kabir Khan</a>
 * @version $Revision: 1.1 $
 */
public interface MailListPropertyConstants {
    //These have been kind of adapted from James

    /** Boolean property: Whether the reply-to address for all messages sent to this 
     * list is set to the list address. Defaults to true*/
    public static final String REPLY_TO_LIST = "replyToList";

    /** String property: If set, this value is prepended to the subject line of all 
     * messages sent to the list*/
    public static final String SUBJECT_PREFIX = "subjectPrefix";

    /** Boolean property: If a subjectprefix is set, this value determines whether the 
     * prefix is bracketed before being prepended to the subject line. Defaults to true*/
    public static final String PREFIX_AUTO_BRACKETED = "prefixAutoBracketed";

    /** Boolean property: whether attachments are allowed in messages sent to this list. 
     * Defaults to false*/
    public static final String ATTACHMENT_ALLOWED = "attachmentAllowed";

    /** Boolean property: Whether only members of the list can send messages to this list. 
     * Defaults to true */
    public static final String MEMBERS_ONLY = "membersOnly";
}
