/*
 * Bunisoft the Open Source Communications Company
 * Copyright 2006, Bunisoft LLC.,
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
package org.buni.meldware.mail.message;

/** 
 * This code has been copied from RFC2822Headers in JAMES, and 
 * defines constants for the header names defined in RFC 2822.
 * @author Kabir Khan
 * @version $Revision: 1.2 $
 */
public interface StandardMailHeaders {
    // See Section 3.6.1 of RFC 2822

    /**
     * The name of the RFC 2822 header that stores the mail date.
     */
    String DATE = "Date";

    // See Section 3.6.2 of RFC 2822

    /**
     * The name of the RFC 2822 header that stores the mail author(s).
     */
    String FROM = "From";

    /**
     * The name of the RFC 2822 header that stores the actual mail transmission agent,
     * if this differs from the author of the message.
     */
    String SENDER = "Sender";

    /**
     * The name of the RFC 2822 header that stores the reply-to address.
     */
    String REPLY_TO = "Reply-To";

    // See Section 3.6.3 of RFC 2822

    /**
     * The name of the RFC 2822 header that stores the primary mail recipients.
     */
    String TO = "To";

    /**
     * The name of the RFC 2822 header that stores the carbon copied mail recipients.
     */
    String CC = "Cc";

    /**
     * The name of the RFC 2822 header that stores the blind carbon copied mail recipients.
     */
    String BCC = "Bcc";

    // See Section 3.6.4 of RFC 2822

    /**
     * The name of the RFC 2822 header that stores the message id.
     */
    String MESSAGE_ID = "Message-ID";

    /**
     * A common variation on the name of the RFC 2822 header that 
     * stores the message id.  This is needed for certain filters and
     * processing of incoming mail.
     */
    String MESSAGE_ID_VARIATION = "Message-Id";

    /**
     * The name of the RFC 2822 header that stores the message id of the message
     * that to which this email is a reply.
     */
    String IN_REPLY_TO = "In-Reply-To";

    /**
     * The name of the RFC 2822 header that is used to identify the thread to
     * which this message refers.
     */
    String REFERENCES = "References";

    // See Section 3.6.5 of RFC 2822

    /**
     * The name of the RFC 2822 header that stores the subject.
     */
    String SUBJECT = "Subject";

    /**
     * The name of the RFC 2822 header that stores human-readable comments.
     */
    String COMMENTS = "Comments";

    /**
     * The name of the RFC 2822 header that stores human-readable keywords.
     */
    String KEYWORDS = "Keywords";

    // See Section 3.6.6 of RFC 2822

    /**
     * The name of the RFC 2822 header that stores the date the message was resent.
     */
    String RESENT_DATE = "Resent-Date";

    /**
     * The name of the RFC 2822 header that stores the originator of the resent message.
     */
    String RESENT_FROM = "Resent-From";

    /**
     * The name of the RFC 2822 header that stores the transmission agent
     * of the resent message.
     */
    String RESENT_SENDER = "Resent-Sender";

    /**
     * The name of the RFC 2822 header that stores the recipients
     * of the resent message.
     */
    String RESENT_TO = "Resent-To";

    /**
     * The name of the RFC 2822 header that stores the carbon copied recipients
     * of the resent message.
     */
    String RESENT_CC = "Resent-Cc";

    /**
     * The name of the RFC 2822 header that stores the blind carbon copied recipients
     * of the resent message.
     */
    String RESENT_BCC = "Resent-Bcc";

    /**
     * The name of the RFC 2822 header that stores the message id
     * of the resent message.
     */
    String RESENT_MESSAGE_ID = "Resent-Message-ID";

    // See Section 3.6.7 of RFC 2822

    /**
     * The name of the RFC 2822 headers that store the tracing data for the return path.
     */
    String RETURN_PATH = "Return-Path";

    /**
     * The name of the RFC 2822 headers that store additional tracing data.
     */
    String RECEIVED = "Received";

    // MIME headers

    /**
     * The name of the MIME header that stores the content type.
     */
    String CONTENT_TYPE = "Content-Type";

}
