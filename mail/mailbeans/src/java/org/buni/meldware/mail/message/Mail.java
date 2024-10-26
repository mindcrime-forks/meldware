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
package org.buni.meldware.mail.message;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.buni.meldware.common.util.ArrayUtil;
import org.buni.meldware.mail.MailException;
import org.buni.meldware.mail.StreamReadException;
import org.buni.meldware.mail.api.FolderMessage;
import org.buni.meldware.mail.util.io.Copier;
import org.buni.meldware.mail.util.io.SimpleCopier;
import org.jboss.logging.Logger;

/**
 * SMTP uses a Mail message. This is him.
 * 
 * @author Andrew C. Oliver
 * @author Eric Daugherty
 * @author Michael Barker
 * @version $Revision: 1.13 $
 */
public class Mail implements Message, Serializable {
    
   public final static String ENCODING = "US-ASCII";
   public final static String REFERENCE = "meldwareReference";

    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 3257005445343492403L;

    private final static Logger log = Logger.getLogger(Mail.class);

    MailHeaders ih;

    /**
     * envelope to addresses
     */
    List<EnvelopedAddress> to;

    int retries = 0;

    Body body;

    private MailAddress returnPath;
    //attributes are transient key/values used for routing.  They are not
    //persisted and are only used for internal routing.
    private Map<String, Object> attributes = new HashMap<String, Object>();
    
    private FolderMessage.SpamState spamState = FolderMessage.SpamState.UNKNOWN;
    
    public Mail(Mail mail) {
        this.body = mail.body;
        this.ih = mail.ih;
        this.retries = mail.retries;
        this.to = mail.to;
    }
    
    public Mail(List<EnvelopedAddress> to, MailAddress returnPath, 
            MailHeaders headers, Body body, int retries) {
        this.to = to;
        this.returnPath = returnPath;
        this.ih = headers;
        this.body = body;
        this.retries = retries;
        if (log.isDebugEnabled()) {
            debugPrintHeaders(ih);
        }
    }  
    
	public void addTo(MailAddress addy) {
        to.add(new EnvelopedAddress(addy, javax.mail.Message.RecipientType.TO));
    }

    public void addBCC(MailAddress addy) {
        to.add(new EnvelopedAddress(addy, javax.mail.Message.RecipientType.BCC, true));
    }
    
    public void removeTo(MailAddress addy) {
       to.remove(addy);
    }

    /**
     * determines the enveloping based on the headers.
     */
    private static List<EnvelopedAddress> envelopeTos(MailAddress[] rcptTos,
            MailHeaders ih) {
        List<EnvelopedAddress> result = new ArrayList<EnvelopedAddress>();
        String[] headerTos = ih.getHeader(StandardMailHeaders.TO);
        String[] headerCcs = ih.getHeader(StandardMailHeaders.CC);
        
        if (headerTos == null) {
            headerTos = new String[0];
        }
        if (headerCcs == null) {
            headerCcs = new String[0];
        }
        
        for (int k = 0; k < rcptTos.length; k++) {
            MailAddress to = rcptTos[k];
            javax.mail.Message.RecipientType type;
            if (isIn(headerTos, to)) {
                type = javax.mail.Message.RecipientType.TO;
            } else if (isIn(headerCcs, to)) {
                type = javax.mail.Message.RecipientType.CC;
            } else {
                type = javax.mail.Message.RecipientType.BCC;
            }
            EnvelopedAddress ea = new EnvelopedAddress(to, type);
            ea.setRcpt(true);
            result.add(ea);
        }
        return result;
    }

    /**
     * determine if the passed in address is in the array of addresses.
     * 
     * @param tos
     *            array of addresses
     * @param address
     *            to check for
     * @return present or not
     */
    public static boolean isIn(String[] tos, MailAddress address) {
        for (int k = 0; k < tos.length; k++) {
            if (tos[k] != null && tos[k].indexOf(address.getRawAddress()) != -1) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean isIn(MailAddress[] tos, MailAddress address) {
        for (int k = 0; k < tos.length; k++) {
            if (tos[k] != null && tos[k].equals(address)) {
                return true;
            }
        }
        return false;
    }

    /**
     * should print the org.jboss.org.buni.meldware.mail headers.
     * 
     * @param ih
     *            internet headers object parsed from the input
     */
    private void debugPrintHeaders(MailHeaders ih) {
        java.util.Iterator e = ih.getAllHeaderLines();
        while (e.hasNext()) {
            log.debug((String) e.next());
        }
    }

    /**
     * WARNING: sender is returned by copy, thus modifications do not affect the
     * original object
     * 
     * @return sends or the mail
     */
    public MailAddress getSender() {
        MailAddress sender = null;
        String senderStr = ih.getSender();
        if (senderStr == null) {
        	List<MailAddress> froms = this.getFrom();
        	if(froms != null && froms.size() > 0)
        		sender = froms.get(0);
        }
        else
        {
        	sender = MailAddress.parseSMTPStyle(senderStr);
        }
        return sender;
    }
    
    /**
     * Gets the list of from addresses.
     * @return
     */
    public List<MailAddress> getFrom() {
        return MailAddressFactory.parseAddressList(ih.getFrom());
    }
    
    public MailAddress getReturnPath() {
        return returnPath;
    }

    /*
     * WARNING: receipients are returned by copy, thus modifications do not
     * affect the original object. @return copy of the receipients
     */
    public List<MailAddress> getTo() {
        List<MailAddress> tempTos = new ArrayList<MailAddress>(to);
        return Collections.unmodifiableList(tempTos);
        //return (MailAddress[]) to.toArray(new MailAddress[to.size()]);
    }

    /**
     * Returns the list of recipients as a shallow copy, changes to this list
     * will not affect the original recipient list.
     * 
     * @return
     */
    public List<EnvelopedAddress> getRecipients() {
        return to;
    }
    
    /**
     * Flag to determine if the message is a mime message.
     */
    public boolean isMime() {
        return getBoundary() != null;
    }
    
    /**
     * A Mail instance is not persistent, so return 0.  Perhaps
     * later we store Mails before queuing.
     */
    public String getMessageId() {
        return ih.getMessageId();
    }
    
    /**
     * Gets the subject of the message.
     */
    public String getSubject() {
        return ih.getSubject();
    }

    /**
     * Returns the MailHeaders that contain all the individual header entries.
     * 
     * @return the original MailHeaders instance.
     */
    public MailHeaders getMailHeaders() {
        // TODO: Should this be cloned?
        return ih;
    }

    /**
     * increase the number of retries by one
     */
    public void increaseRetries() {
        retries++;
    }

    /**
     * @return the number of retries this mail has undergone
     */
    public int getRetryNumber() {
        return retries;
    }

    /**
     * @return body of this mail
     */
    //TODO temp
    public Body getMailBody() {
        return body;
    }
    
    public List<Body> getBody() {
        return Arrays.asList(getMailBody());
    }
    
    public String[] getHeader(String name) {
        return ih.getHeader(name);
    }

    public void addHeader(String name, String value) {
        ih.addHeader(name,value);
    }

    /**
     * Returns the whole message as a stream of bytes.
     *  
     * @return
     * @throws MailException
     */
    public InputStream getRawStream(MailBodyManager mgr) throws MailException {
        List<InputStream> streams = new ArrayList<InputStream>();

        try {
            MailHeaders headers = getMailHeaders();
            for (Iterator it = headers.getAllHeaderLines(); it.hasNext();) {
                Object header = it.next();

                streams.add(new ByteArrayInputStream(header.toString()
                        .getBytes(ENCODING)));
                streams.add(new ByteArrayInputStream("\r\n".getBytes()));
            }
            streams.add(new ByteArrayInputStream("\r\n".getBytes()));
            streams.add(mgr.getInputStream(getMailBody()));
        } catch (IOException e) {
            throw new MailException(e);
        }

        return new SequenceInputStream(Collections.enumeration(streams));
    }
    
    public long getSize() {
        return getMessageSize();
    }
    
    public long getMessageSize() {
        
        return getHeaderSize() + getBodySize() + 2;
    }
    
    public long getHeaderSize() {
        try {
            return ih.size(ENCODING);
        } catch (UnsupportedEncodingException e) {
            throw new MailException("Unsupported encoding: " + ENCODING);
        }
    }
    
    
    public long getBodySize() {
        return body.getSize();
    }
    
    /**
     * @return
     */
    public String getBoundary() {
        // TODO Auto-generated method stub
        return ih.getBoundary();
    }
    
    /**
     * @return the spamState
     */
    public FolderMessage.SpamState getSpamState() {
        return spamState;
    }

    /**
     * @param spamState the spamState to set
     */
    public void setSpamState(FolderMessage.SpamState spamState) {
        this.spamState = spamState;
    }

    /**
     * Static constructor for org.jboss.org.jboss.mail objects. Later we may
     * pool these, for now it just calls the private constructor.
     * 
     * @param in input stream containing the org.jboss.org.jboss.mail, post
     *            DATA command
     * @return Mail object
     */
    //@Tx(TxType.REQUIRED)
    public static Mail create(MailBodyManager mgr, InputStream in, 
            MailCreateListener callback) 
        throws MailException {
        
        MailHeadersImpl ih = new MailHeadersImpl();
        try {
            int bytesRead = ih.load(in, Mail.ENCODING);
            callback.onHeadersParsed(bytesRead, ih);
        } catch (IOException e) {
            throw new StreamReadException(e);
        }
        
        callback.verifyHeaders();
        
        Copier copier = callback.getCopier();
        MailAddress to[] = callback.getTo();
        //MailAddress from = callback.getFrom();
        
        Body body = constructBody(mgr, in, copier);
        List<EnvelopedAddress> tos = envelopeTos(to, ih);
        
        return new Mail(tos, callback.getFrom(), ih, body, 0);
    }
    

    /**
     * Create a mail from a set of prespecified values.
     * 
     * @param mgr
     * @param from
     * @param to
     * @param cc
     * @param bcc
     * @param subject
     * @param body
     * @return
     */
    //@Tx(TxType.REQUIRED)
    public static Mail create(MailBodyManager mgr, String from,
            String to[], String[] cc, String[] bcc, String subject, String text) {
        MailHeaders ih = new MailHeadersImpl();
        List<EnvelopedAddress> rcpts = new ArrayList<EnvelopedAddress>();
        
        MailAddress fromAddress = MailAddress.parseSMTPStyle(from);
        ih.addHeader(StandardMailHeaders.FROM, from);
        ih.addHeader(StandardMailHeaders.RETURN_PATH, fromAddress.toSMTPString());
        
        ih.addHeader("To", ArrayUtil.join(to, ","));
        for (String addrStr : to) {
            MailAddress addr = MailAddress.parseSMTPStyle(addrStr);
            if (!addr.isEmpty()) {
                rcpts.add(new EnvelopedAddress(addr, 
                        javax.mail.Message.RecipientType.TO));                    
            }
        }
        
        ih.addHeader("Cc", ArrayUtil.join(cc, ","));
        for (String addrStr : cc) {
            MailAddress addr = MailAddress.parseSMTPStyle(addrStr);
            if (!addr.isEmpty()) {
                rcpts.add(new EnvelopedAddress(addr, 
                        javax.mail.Message.RecipientType.CC));                    
            }
        }
        
        ih.addHeader("Bcc", ArrayUtil.join(bcc, ","));
        for (String addrStr : bcc) {
            MailAddress addr = MailAddress.parseSMTPStyle(addrStr);
            if (!addr.isEmpty()) {
                rcpts.add(new EnvelopedAddress(addr, 
                        javax.mail.Message.RecipientType.BCC));                    
            }
        }
        
        ih.addHeader(MailHeaders.HEADER_SUBJECT, subject);
        
        // XXX: What encoding?
        byte[] msgbytes = text.getBytes();
        ByteArrayInputStream msgIn = new ByteArrayInputStream(msgbytes);

        log.info("all headers after loading: " + ih);
        Body body = constructBody(mgr, new BufferedInputStream(msgIn));
        return new Mail(rcpts, fromAddress, ih, body, 0);
    }
    
    
    /**
     * Creates a simple mail from an input stream.  Uses the mail's headers 
     * for envelope information.
     * 
     * @param mgr
     * @param in
     * @param copier
     * @return
     */
    //@Tx(TxType.REQUIRED)
    public static Mail create(MailBodyManager mgr, InputStream in, Copier copier) {
        try {
            MailHeaders ih = MailHeadersImpl.create(in);
            Body body = constructBody(mgr, in, copier);
            
            MailAddress from;
            if (ih.getFrom() == null) {
                throw new MailException("Mail must have a from address");
            } else {
                from = MailAddress.parseSMTPStyle(ih.getFrom());
            }
            
            List<EnvelopedAddress> lto = new ArrayList<EnvelopedAddress>();
            String[] ato = ih.getHeader("To");
            if (ato != null) {
               for (int i = 0; i < ato.length; i++) {
                   lto.add(new EnvelopedAddress(ato[i], javax.mail.Message.RecipientType.TO));
               }
            }
            return new Mail(lto, from, ih, body, 0);
        }
        catch (IOException e) {
            throw new MailException(e);
        }
    }

    //@Tx(TxType.REQUIRED)
    private static Body constructBody(MailBodyManager manager, 
            InputStream msgIn)
        throws MailException {
        
        return constructBody(manager, msgIn, new SimpleCopier());
    }
    
    /**
     * Create a list of mail bodies from the supplied input stream with 
     * the specific boundry
     * 
     * @param manager
     * @param msgIn
     * @param copier
     * @param boundary
     * @return
     * @throws MailException
     */
    //@Tx(TxType.REQUIRED)
    private static Body constructBody(MailBodyManager manager, 
            InputStream msgIn, Copier copier)
        throws MailException {
        //List<Body> bodies = new ArrayList<Body>(1);
        int bodySize = 0;
        Body body = manager.createMailBody();
        bodySize += manager.read(body, msgIn, copier);
        //bodies.add(body);
        return body;
    }

    /** 
     * get a header as one continuous long strong.
     * @param name of the header to get
     * @return header
     */
    public String getHeaderAsString(String name) {
        String retval = "";
        String[] headers = ih.getHeader(name);
        if (headers == null) {
            return null;
        }
        for (int i = 0; i < headers.length; i++) {
            retval += headers[i];
        }
        return retval;
    }
    
    /**
     * set an attribute, a transient object used for internal routing
     * attributes on a Mail are not user specific.  
     * @param key of the attribute to set
     * @param val ue to set it to (object of any type)
     */
    public void setAttribute(String key, Object val) {
        this.attributes.put(key, val);
    }   
   
    /**
     * get an attribute, a transient object used for internal routing
     * attributes on a Mail are not user specific. 
     * @param key of the attribute to get
     * @return value of the attribute
     */ 
    public Object getAttribute(String key) {
        return this.attributes.get(key);
    }   
}