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
package org.buni.meldware.mail.userapi;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.buni.meldware.mail.MailListenerChain;
import org.buni.meldware.mail.mailbox.Alias;
import org.buni.meldware.mail.mailbox.Folder;
import org.buni.meldware.mail.mailbox.FolderSummary;
import org.buni.meldware.mail.mailbox.Mailbox;
import org.buni.meldware.mail.mailbox.MailboxService;
import org.buni.meldware.mail.mailbox.MessageBody;
import org.buni.meldware.mail.mailbox.MessageData;
import org.buni.meldware.mail.message.Body;
import org.buni.meldware.mail.message.Mail;
import org.buni.meldware.mail.message.MailBodyManager;
import org.buni.meldware.mail.util.Node;
import org.buni.meldware.mail.util.io.Copier;
import org.buni.meldware.mail.util.io.SimpleCopier;
import org.jboss.aspects.tx.Tx;
import org.jboss.aspects.tx.TxType;

public class MailSenderImpl implements MailSender {
    MailListenerChain chain;

    MailBodyManager bm;

    MailboxService mailboxService;

    public void setListenerChain(MailListenerChain chain) {
        this.chain = chain;
    }

    public MailListenerChain getListenerChain() {
        return this.chain;
    }

    public void setBodyManager(MailBodyManager mgr) {
        this.bm = mgr;
    }

    public MailBodyManager getBodyManager() {
        return bm;
    }

    public void setMailboxManager(MailboxService mbm) {
        this.mailboxService = mbm;
    }

    public MailboxService getMailboxManager() {
        return this.mailboxService;
    }

    public Node<FolderSummary> folderSummary(String user) {
        return this.mailboxService.folderSummary(user);
    }

    @Tx(TxType.REQUIRED)
    public int send(String alias, String from, String[] to, String[] cc, String[] bcc, String subject, String body) {
        Mail mail = Mail.create(bm, from, to, cc, bcc, subject, body);
        chain.processMail(mail);
        if (alias != null) {  //TODO remove this when we do it automatically with listener
            Mailbox box = this.mailboxService.getMailboxByAlias(alias);
            Folder f = box.getDefaultOutFolder();
            //String path = this.mailboxService.getPathFor(f); 
            MessageData data = new MessageData(mail);
            mailboxService.createMail(f, data);
            //this.save(alias,path,from,to,cc,bcc,subject,body);
        }
        return 0;
    }


    //TODO make this deal with mime correctly
    /*
     * (non-Javadoc)
     * 
     * @see org.buni.meldware.mail.userapi.MailSender#getMailBody(long, long, long)
     */
    @Tx(TxType.REQUIRED)
    public String getMailBody(long uid, long firstbyte, long lastbyte) {
        String retval = null;
        try {
            MessageData data = this.mailboxService.getMail(uid);
            List<Body> bodies = this.mailboxService.getMailBody(data);
            Body body = bodies.get(0);
            InputStream stream = new BufferedInputStream(bm.getInputStream(body));
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            if (firstbyte > 0) {
                stream.skip(firstbyte);
            }
            Copier c = new SimpleCopier();
            // TODO right now we ignore the given bounds...
            c.copy(stream, out, 4096);
            stream.close();
            retval = new String(out.toByteArray(), "US-ASCII"); // TODO really
            // naughty
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return retval;
    }

    /* 
     * (non-Javadoc)
     * 
     * @see org.buni.meldware.mail.userapi.MailSender#getMailAttachmentFilename(long, int)
     */
    @Tx(TxType.REQUIRED)
    public List<String> getMailAttachmentFilename(long uid) {
    	List<String> retval = new ArrayList<String>();
        try {
            MessageData data = this.mailboxService.getMail(uid);
            List<Body> bodies = this.mailboxService.getMailBody(data);
            if (bodies.size() < 2) {
            	return retval;
            }
            Pattern p = Pattern.compile("filename=\"(.+)\"");
            for (int i = 1; i < bodies.size(); i++) {
                MessageBody messageBody =
                	this.mailboxService.getMessageBody(data, i);
                String mimeheader = messageBody.getMimeheader();
                Matcher m = p.matcher(mimeheader);
                if (m.find()) {
                    retval.add(m.group(1));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return retval;
    }

    /* 
     * (non-Javadoc)
     * 
     * @see org.buni.meldware.mail.userapi.MailSender#getMailAttachmentFilename(long, int)
     */
    @Tx(TxType.REQUIRED)
    public InputStream getMailAttachment(long uid, String filename) {
        try {
            MessageData data = this.mailboxService.getMail(uid);
            List<Body> bodies = this.mailboxService.getMailBody(data);
            if (bodies.size() < 2) {
                return null;
            }
            Pattern p = Pattern.compile("filename=\""+filename+"\"");
            for (int i = 1; i < bodies.size(); i++) {
                MessageBody messageBody =
                    this.mailboxService.getMessageBody(data, i);
                String mimeheader = messageBody.getMimeheader();
                Matcher m = p.matcher(mimeheader);
                if (m.find()) {
                    return this.bm.getStore().getStoreItem(messageBody.getBodyId()).getInputStream();
                   // return messageBody.getBodyId();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return null;
    }    
    
    /*
     * (non-Javadoc)
     * 
     * @see org.buni.meldware.mail.userapi.MailSender#getMailLength(long)
     */
    @Tx(TxType.REQUIRED)
    public long getMailLength(long uid) {
        return this.mailboxService.getMail(uid).getSize();
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.buni.meldware.mail.userapi.MailSender#createFolder(java.lang.String, java.lang.String)
     */
    @Tx(TxType.REQUIRED)
    public Folder createFolder(String alias, String path) {
        Mailbox box = this.mailboxService.getMailboxByAlias(alias);
        String[] apath = path.split("/");
        Folder parent = box;
        Folder retval = null;
        if (apath.length > 1) {
            String[] tpath = new String[apath.length - 1];
            System.arraycopy(apath, 0, tpath, 0, tpath.length);
            parent = this.mailboxService.getSubfolderByPath(box, tpath);
            retval = this.mailboxService.createFolder(parent, apath[apath.length - 1]);
        } else {
            retval = this.mailboxService.createFolder(parent, apath[0]);
        }
        return retval;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.buni.meldware.mail.userapi.MailSender#deleteFolder(java.lang.String, java.lang.String)
     */
    @Tx(TxType.REQUIRED)
    public void deleteFolder(String alias, String path) {
        Mailbox box = this.mailboxService.getMailboxByAlias(alias);
        String[] apath = path.split("/");
        Folder folder = null;
        folder = this.mailboxService.getSubfolderByPath(box, apath);
        this.mailboxService.deleteFolder(folder);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.buni.meldware.mail.userapi.MailSender#moveFolder(java.lang.String, java.lang.String, java.lang.String)
     */
    @Tx(TxType.REQUIRED)
    public Folder moveFolder(String alias, String foldername, String targetname) {
        Mailbox box = this.mailboxService.getMailboxByAlias(alias);
        String[] spath = foldername.split("/");
        // String oldname = spath[spath.length-1];
        String[] tpath = targetname.split("/");
        String newname = tpath[tpath.length - 1];
        Folder source = this.mailboxService.getSubfolderByPath(box, spath);
        Folder target = tpath.length > 1 ? this.mailboxService.getSubfolderByPath(box, parent(tpath)) : box;
        Folder retval = this.mailboxService.moveFolder(source, target, newname);
        return retval;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.buni.meldware.mail.userapi.MailSender#getAliases(java.lang.String)
     */
    @Tx(TxType.REQUIRED)
    public List<String> getAliases(String user) {
        Mailbox box = this.mailboxService.getMailboxByAlias(user);
        if(box == null) {
       	    return new ArrayList<String>(); //No Aliases exist for this user.
        }
        Collection<Alias> a = box.getAliases();
        List<String> l = new ArrayList<String>(a.size());
        Iterator<Alias> i = a.iterator();
        while (i.hasNext()) {
            Alias alias = i.next();
            l.add(alias.getName());
        }
        return l;
    }

    private static final String[] parent(String[] path) {
        if (path.length < 2) {
            return new String[] { "" };
        } else {
            String[] retval = new String[path.length - 1];
            System.arraycopy(path, 0, retval, 0, retval.length);
            return retval;
        }
    }

    /* (non-Javadoc)
     * @see org.buni.meldware.mail.userapi.MailSender#provision(java.lang.String)
     */
    public boolean provision(String alias) {
        Mailbox box = this.mailboxService.getMailboxByAlias(alias);
        if (box == null) {
            box = this.mailboxService.createMailbox(alias);
            return false;
        }
        return true;
    }

}