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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.buni.meldware.mail.api.FolderBody.BodyType;
import org.buni.meldware.mail.message.Body;
import org.buni.meldware.mail.message.Mail;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 * MessageBody reprensents the mime part in the case of mime messages
 */
@Entity
public class MessageBody implements Body {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    long id;

    @ManyToOne
    @JoinColumn(name="message_id")
    MessageData message;

    @ManyToOne
    @JoinColumn(name="parent_id")
    MessageBody parent;

    @OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER, mappedBy = "parent")
    @OnDelete(action=OnDeleteAction.CASCADE)
    List<MessageBody> children;

    @Basic
    long bodyId;

    @Basic
    @Column(name = "bodyPos")
    int bodyPos;

    @Basic
    @Column(length = 8192)
    private String mimeheader;

    @Basic
    @Column(length = 32768)
    private String header;

    @Basic
    private boolean bodyless;

    @Basic
    private String epilogue;

    @Basic
    private String preamble;

    @Basic
    private long partSize;
    
    @Basic
    private String mimeType = "text/plain";
    
    @Basic 
    private String boundary;
    
    public MessageBody() {
        children = new ArrayList<MessageBody>();
    }

    /**
     * Construct a messageBody from a message data and mail object. The part
     * describes WHICH message body this is in the list of parts that make up
     * the message. In turn any parts contained by this messageBody will be
     * created recursively with the other constructor.
     * 
     * @param message
     *            which contains this messageBody
     * @param mail
     *            which constructed the MessageData and contains the MailBody
     *            object which will be used to construct this persistent
     *            version.
     * @param part
     *            number of this messageBody's place in the MessageData's list
     *            of bodies.
     */
    public MessageBody(MessageData message, Mail mail, int part) {
        this.message = message;
        Body b = mail.getMailBody();
        this.bodyId = b.getStoreId();
        this.bodyPos = 0;
        this.partSize = b.getSize();
    }

    
    /**
     * Copy constructor.  Recursive copies child bodies, but not store items
     * will retain references.
     * 
     * @param loaded
     */
    public MessageBody(MessageBody loaded) {
        bodyId = loaded.bodyId;
        bodyPos = loaded.bodyPos;
        message = null;
        if (loaded.children != null) {
            children = new ArrayList<MessageBody>();
            for (MessageBody body : loaded.children) {
                addMessageBody(new MessageBody(body));
            }
        }        
        header = loaded.header;
        mimeheader = loaded.mimeheader;
        bodyless = loaded.bodyless;
        preamble = loaded.preamble;
        epilogue = loaded.epilogue;
        partSize = loaded.partSize;
        boundary = loaded.boundary;
        mimeType = loaded.mimeType;
    }

    
    public void addMessageBody(MessageBody child) {
        child.setParent(this);
        child.setBodyPos(children.size());
        children.add(child);
    }

    /**
     * get ID of this messageBody
     * 
     * @return primary key
     */
    public long getId() {
        return id;
    }

    /**
     * MessageData object this is associated with.
     * 
     * @return
     */
    public MessageData getMessageData() {
        return message;
    }

    /**
     * set MessageData object this is associated with
     * 
     * @param message
     */
    public void setMessage(MessageData message) {
        this.message = message;
    }

    /**
     * @return MessageBody that this is a child of
     */
    public MessageBody getParent() {
        return parent;
    }
    
    public void setParent(MessageBody parent) {
        this.parent = parent;
    }

    /**
     * @return children that this messageBody object contains.
     */
    public List<MessageBody> getChildren() {
        return children;
    }

    /**
     * @return id of LOB object for the store service
     */
    public long getBodyId() {
        if (bodyless) {
            throw new RuntimeException("Illegal attempt to get the body id on a bodyless MessageBody");
        }
        return bodyId;
    }
    
    public void setBodyId(long id) {
        bodyId = id;
    }

    /**
     * @return the position in the containing message of messageBody
     */
    public int getBodyPos() {
        return bodyPos;
    }

    /**
     * @param position
     *            the position to set
     */
    public void setBodyPos(int position) {
        this.bodyPos = position;
    }

    /**
     * @return header associated with this if it is a message
     */
    public String getHeader() {
        return header;
    }

    /**
     * @param header
     *            associated with this if it is a message (submessage)
     */
    public void setHeader(String header) {
        this.header = header;
    }

    /**
     * @return mimeheader of this body part
     */
    public String getMimeheader() {
        return mimeheader;
    }

    /**
     * @param mimeheader
     *            of this body part
     */
    public void setMimeheader(String mimeheader) {
        this.mimeheader = mimeheader;
    }

    /**
     * set that this is a bodyless messagebody. Bodyless message bodies
     * represent message objects. They have no body id of their own but have
     * children (the embedded message pieces)
     * 
     * @param bodyless
     *            if this is is a bodyless messagebody
     */
    public void setBodyless(boolean bodyless) {
        this.bodyless = bodyless;
    }

    /**
     * see setter for more info
     * 
     * @return bodyless or not
     */
    public boolean getBodyless() {
        return this.bodyless;
    }

    /**
     * @return mime epilogue (after the actual embedded mime stuff - not common)
     */
    public String getEpilogue() {
        return epilogue;
    }

    /**
     * @param epilogue
     */
    public void setEpilogue(String epilogue) {
        this.epilogue = epilogue;
    }

    /**
     * @return mime preamble (prefixes actual mime stuff generally just says
     *         that this is a mime message)
     */
    public String getPreamble() {
        return preamble;
    }

    /**
     * set the mime preamble "This is a message in mime format"...
     * 
     * @param preamble
     */
    public void setPreamble(String preamble) {
        this.preamble = preamble;
    }

    /**
     * @return size of this single part.
     */
    public long getPartSize() {
        // TODO Auto-generated method stub
        return partSize;
    }
    
    void setPartSize(long partSize) {
        this.partSize = partSize;
    }
    
    public long getSize() {
        return partSize;
    }
    
    public void setSize(int size) {
        this.partSize = size;
    }
    
    public Long getStoreId() {
        return bodyId;
    }

    /**
     * @return the boundary
     */
    public String getBoundary() {
        return boundary;
    }

    /**
     * @param boundary the boundary to set
     */
    public void setBoundary(String boundary) {
        this.boundary = boundary;
    }

    /**
     * @return the mimeType
     */
    public String getMimeType() {
        return mimeType;
    }

    /**
     * @param mimeType the mimeType to set
     */
    void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }    
    
    
    public boolean hasChildren() {
        return children != null && children.size() > 0;
    }
    
    public BodyType getType() {
        if (boundary != null && boundary.length() > 0) {
            return BodyType.MULTIPART;
        } else if (hasChildren()) {
            return BodyType.MESSAGE;
        } else {
            return BodyType.SIMPLE;
        }
    }

    /**
     * TODO Store the charset of the incoming text.
     * 
     * @return
     */
    public String getCharset() {
        return "UTF-8";
    }
}
