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
package org.buni.meldware.mail.mailbox;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.mail.internet.MailDateFormat;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.buni.meldware.mail.message.Mail;
import org.buni.meldware.mail.message.MailAddress;
import org.buni.meldware.mail.message.MailAddressFactory;
import org.buni.meldware.mail.message.MailHeaders;
import org.buni.meldware.mail.message.MailHeadersImpl;
import org.buni.meldware.mail.message.Message;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 * MessageData contains the relationship between a mail and its folder and body
 * parts.  these represent the persistent face of o.b.mw.m.message.Mail objects.
 * 
 * @author Andrew C. Oliver
 * @version $Revision: 1.19 $
 */
@Entity
public class MessageData implements Message, Serializable {

    /**
     * serialization version
     */
    private static final long serialVersionUID = 7619084499549195180L;

    /**
     * used to parse date headers
     */
    @Transient
    private transient MailDateFormat df = new MailDateFormat();

    /**
     * surrogate key
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    /**
     * subject of the message (useful for searching)
     */
    @Basic
    private String subject;

    /**
     * to address of the message (useful for searching)
     */
    @Basic
    @Column(length = 8192)
    private String toAddress;

    /**
     * from address of the message (useful for searching)
     */
    @Basic
    private String fromAddress;
    
    @Basic
    private String sender;
    
    @Basic
    private String messageId;

    /**
     * timestamp represents the Date header unless we could not parse it or
     * determined it to be invalid in which case it represents the time that the
     * server created the message (when it was received)
     */
    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp;

    /**
     * the headers de-parsed
     */
    @Basic
    @Column(length = 32768)
    private String header;

    @Transient
    private MailHeaders mailHeaders;

    @OneToMany(targetEntity= MessageBody.class, fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "message")
    @IndexColumn(name = "bodyPos", base = 0)
    @Fetch(FetchMode.SUBSELECT)
    @OnDelete(action=OnDeleteAction.CASCADE)    
    List<MessageBody> bodies;

    /**
     * size of the message
     */
    @Basic
    private long messageSize;

    @Basic
    private boolean mime;

    @Basic
    private String epilogue;

    @Basic
    private String preamble;
    
    @Basic
    private String boundary;

    /**
     * Package visible header, create mails using MessageDataFactory.
     *
     */
    MessageData() {
        bodies = new ArrayList<MessageBody>();
    }

    /**
     * create a message from a mail. (the Mail is used in processing, the
     * MessageData when persisted)
     * 
     * @param mail
     */
    public MessageData(Mail mail) {
        this.preamble = "";
        this.epilogue = "";
        
        MailHeaders headers = mail.getMailHeaders();
        // TODO: This should be done by a mail listener.
        
        setHeaders(headers);
        
//        this.timestamp = findTS(headers);
//        this.header = headers.toString();
//        this.fromAddress = headers.getFrom();
//        this.sender = headers.getSender();
//        this.toAddress = headers.getTo();
//        this.subject = headers.getSubject();
        
        this.bodies = new ArrayList<MessageBody>(1);
        bodies.add(new MessageBody(this, mail, 0));
        this.mime = false;

        // Always set this last.
        this.messageSize = mail.getMessageSize();
    }
    
    public void setHeaders(MailHeaders headers) {
        
        this.timestamp = findTS(headers);
        this.header = headers.toString();
        this.fromAddress = headers.getFrom();
        this.sender = headers.getSender();
        this.toAddress = headers.getTo();
        this.subject = headers.getSubject();
        this.messageId = headers.getMessageId();
    }

    /**
     * Copy constructor.  Does a recursive copy of the message including the
     * message body (but not the store item).
     * 
     * @param loaded
     * @return
     */
    public MessageData (MessageData loaded) {
        
        bodies = new ArrayList<MessageBody>();
        if (loaded.getMessageBodies() != null) {
            for (MessageBody body : loaded.getMessageBodies()) {
                MessageBody newBody = new MessageBody(body);
                addMessageBody(newBody);
            }
        }
        setEpilogue(loaded.getEpilogue());
        setMimePreamble(loaded.getMimePreamble());
        setMime(loaded.isMime());
        setHeader(loaded.getHeader());
        setMessageSize(loaded.getSize());
        fromAddress = loaded.fromAddress;
        toAddress = loaded.toAddress;
        timestamp = loaded.timestamp;
        subject = loaded.subject;
        sender = loaded.sender;
        messageId = loaded.messageId;
        boundary = loaded.boundary;
    }
   
    public String getMessageId() {
        return messageId;
    }

    /**
     * get the timestamp from the date header when provided a mail header
     * collection
     * 
     * @param headers
     * @return date object representing the Date header or "NOW" if it can't be
     *         parsed
     */
    private Date findTS(MailHeaders headers) {
        Date date;
        try {
            String d = headers.getDate();
            date = df.parse(d);
            if (date == null) {
                date = new Date();
            }
        } catch (Exception e) {
            date = new Date();
        }
        return date;
    }

    /**
     * @return id of the store object containing the main body of the message
     */
    public List<Long> getBodyId() {
        List<Long> retval = new ArrayList<Long>(bodies.size());
        for (int i = 0; i < bodies.size(); i++) {
            MessageBody bod = bodies.get(i);
            retval.add(bod.getBodyless() ? -1 : bod.getBodyId());
        }
        return retval;
    }
    
    public List<MessageBody> getBody() {
        return bodies;
    }

    public String getBoundary() {
//        String ct = getHeader("Content-Type");
//        String[] header = (ct == null) ? null : getHeader("Content-Type").split("\\r\\n");
//        String retval = null;
//        try {
//            for (int i = 0; header != null && i < header.length; i++) {
//                if (header[i].indexOf("boundary=\"") > -1) {
//                    String[] temp = header[i].split("boundary\\=\\\"");
//                    String boundary = temp[temp.length - 1];
//                    boundary = "--" + boundary.split("\\\"")[0];
//                    retval = boundary;
//                    break;
//                }
//            }
//        } catch (Exception e) {
//            // don't do anything we'll just NOT parse the mime.
//        }
//        return retval;
        return boundary;
    }
    
    public void setBoundary(String boundary) {
        this.boundary = boundary;
    }
    

    /**
     * @return from header value
     */
    public String getFrom() {
        return fromAddress;
    }
    
    public MailAddress getSender() {
        if (sender != null) {
            return MailAddress.parseSMTPStyle(sender);            
        } else {
            return null;
        }
    }

    /**
     * @return message headers as one long \n delimited string
     */
    public String getHeader() {
        return header;
    }

    /**
     * Get a header value, returns null if no header found. Returns the first
     * value that matches.
     * 
     * @param key
     *            to find the value for
     * @return header value
     */
    public String getHeader(String key) {

        String[] headers = getHeaders(key);
        if (headers != null && headers.length > 0) {
            return headers[0];
        } else {
            return null;
        }

    }

    /**
     * Gets the headers for a particular value.
     * 
     * @param key
     * @return An array of headers, null if no header found.
     */
    public String[] getHeaders(String key) {
        return getMailHeaders().getHeader(key);
    }

    private MailHeaders getMailHeaders() {
        if (mailHeaders == null) {
            String[] headers = getHeaders();
            mailHeaders = MailHeadersImpl.create(headers);
        }
        return mailHeaders;
    }

    /**
     * @return headers parsed into a string array
     */
    public String[] getHeaders() {
        return this.header.split("\\r\\n");
    }

    /**
     * @return surrogate id of the message
     */
    public long getId() {
        return id;
    }

    /**
     * @return size of the message
     */
    public long getSize() {
        return this.messageSize;
    }

    /**
     * @return subject of the message
     */
    public String getSubject() {
        return subject;
    }

    /**
     * @return when the message was received
     */
    public Date getTimestamp() {
        return timestamp;
    }

    /**
     * @return ";" delimited list saying who it is to
     */
    public List<MailAddress> getTo() {
        return MailAddressFactory.parseAddressList(toAddress);
    }

    /**
     * @return the isMime
     */
    public boolean isMime() {
        return mime;
    }


    /**
     * set the message headres (as one long \n delimited string)
     * 
     * @param header
     */
    public void setHeader(String header) {
        this.header = header;
    }

    /**
     * @param messageSize2
     */
    private void setMessageSize(long messageSize) {
        this.messageSize = messageSize;
    }

    /**
     * @param isMime
     *            the isMime to set
     */
    public void setMime(boolean isMime) {
        this.mime = isMime;
    }

    /**
     * @return the bodies
     */
    public List<MessageBody> getMessageBodies() {
        return bodies;
    }
    
    /**
     * Adds a body to this message.
     * @param body
     */
    public void addMessageBody(MessageBody body) {
        body.setMessage(this);
        body.setBodyPos(bodies.size());
        bodies.add(body);
    }

    public String getMimePreamble() {
        return preamble;
    }

    public void setMimePreamble(String preamble) {
        this.preamble = preamble;
    }

    public void setEpilogue(String epilogue) {
        this.epilogue = epilogue;
    }

    public String getEpilogue() {
        return epilogue;
    }
    
    public void setSize(long size) {
        this.messageSize = size;
    }    
}
