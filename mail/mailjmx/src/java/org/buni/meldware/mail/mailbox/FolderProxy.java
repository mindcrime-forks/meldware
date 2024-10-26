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
package org.buni.meldware.mail.mailbox;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.buni.meldware.mail.api.Folder;
import org.buni.meldware.mail.api.FolderFilter;
import org.buni.meldware.mail.api.FolderMessage;
import org.buni.meldware.mail.api.Hints;
import org.buni.meldware.mail.api.Range;
import org.buni.meldware.mail.api.SearchKey;
import org.buni.meldware.mail.message.Mail;
import org.buni.meldware.mail.message.MailAddress;
import org.buni.meldware.mail.message.MailCreateAdapter;
import org.buni.meldware.mail.message.MailCreateListener;
import org.buni.meldware.mail.message.Message;
import org.buni.meldware.mail.util.io.Copier;
import org.buni.meldware.mail.util.io.ExactSizeCopier;
import org.jboss.aspects.tx.Tx;
import org.jboss.aspects.tx.TxType;

public abstract class FolderProxy implements Folder {

    private final org.buni.meldware.mail.mailbox.Folder folder;
    private final MailboxService service;
    private final Hints hints;
    protected volatile FolderInfo folderInfo;

    public FolderProxy(MailboxService service,
            org.buni.meldware.mail.mailbox.Folder folder, Hints hints) {
        this.service = service;
        this.folder = folder;
        this.hints = hints;
        open();
    }
    
    protected abstract void open();
    
    public long getId() {
        return folder.getId();
    }
    
    protected abstract FolderProxy newInstance(MailboxService service, 
            org.buni.meldware.mail.mailbox.Folder f, Hints hint);
    
    public List<FolderMessage> getMessages() {
        // TODO: Could be lazily created.
        Collection<FolderEntry> fes = service.getMessages(folder, Range.EMPTY, hints);
        List<FolderMessage> messages = new ArrayList<FolderMessage>(fes.size());
        int seqNum = 1;
        for (FolderEntry fe : fes) {
            messages.add(new FolderMessageProxy(service, fe, seqNum));
            seqNum++;
        }
        return messages;
    }
    
    
    public FolderMessage getMessage(boolean isUid, long id) {
        // Always make a defensive copy of the reference.
        FolderInfo fi = getFolderInfo();
        long uid = fi.normalise(isUid, id);
        FolderEntry fe = service.getMessage(folder, uid);
        int seqNum = fi.getSeqNum(uid);
        return new FolderMessageProxy(service, fe, seqNum);
    }
    
    public List<FolderMessage> getMessages(boolean isUid, Range[] ranges) {
        Hints newHints = new Hints(hints, true, true, false);
        return getEntries(isUid, ranges, newHints);
    }
    
    public List<FolderMessage> getFlags(boolean isUid, Range[] ranges) {
        Hints newHints = new Hints(true, false, false);
        return getEntries(isUid, ranges, newHints);
    }
    
    private List<FolderMessage> getEntries(boolean isUid, Range[] ranges, 
            Hints newHints) {
        // Always make a defensive copy of the reference.
        FolderInfo fi = getFolderInfo();
        Range[] normalised = fi.normalise(isUid, ranges);
        
        // XXX: We could exclude some ranges for performance.
        Collection<FolderEntry> fes = service.getMessages(folder, normalised,
                newHints);
        List<FolderMessage> messages = new ArrayList<FolderMessage>(fes
                .size());
        for (FolderEntry fe : fes) {
            int seqNum = fi.getSeqNum(fe.getUid());
            if (seqNum > 0) {
                messages.add(new FolderMessageProxy(service, fe, seqNum));
            }
        }
        return messages;
    }

    public void copy(Folder target, boolean isUid, Range[] ranges) {
        FolderInfo fi = getFolderInfo();
        Range[] normalised = fi.normalise(isUid, ranges);
        service.copy(folder, target, normalised);
    }
    
    
    
    public void setFlag(boolean isUid, Range[] ranges, FlagType flag, boolean isSet) {
        FolderInfo fi = getFolderInfo();
        Range[] normalised = fi.normalise(isUid, ranges);
        service.setFlag(folder, normalised, flag, isSet);
	}

	public void setDeleted(boolean isUid, Range[] ranges, boolean deleted) {
        FolderInfo fi = getFolderInfo();
        Range[] normalised = fi.normalise(isUid, ranges);
        service.setDeleted(folder, normalised, deleted);
    }    
    
    public String getName() {
        return folder.getName();
    }

    public String[] getPath() {
        return service.getPathFor(folder).split("/");
    }
    
    public void rename(String[] path) {
        service.moveFolder(folder, path);
    }
    
    public long getMessageCount() {
        return folderInfo.getExists();
    }
    
    /**
     * Magic method that resets the uid list if the max uid is greater than
     * the last received value.
     */
    public long getMaxUid() {
        return getFolderInfo().getMaxUid();
    }
    
    public long getRecentCount() {
        return getFolderInfo().getRecent();
    }
    
    public long getUnseenCount() {
        return getFolderInfo().getUnseen();
    }
    
    public long getFirstUnseen() {
        return getFolderInfo().getFirstUnseen();
    }
    
    public long getLikelyUID() {
        return getFolderInfo().getLikelyUid();
    }

    public List<String[]> getSubFolders(FolderFilter filter) {
        List<String[]> subfolders = new ArrayList<String[]>();
        for (org.buni.meldware.mail.mailbox.Folder f : 
            service.getSubfolders(folder, filter)) {
            if (f.getPath() != null) {
                String[] path = f.getPath().split("/");
                if (path.length == 0 || path.length == 1) {
                    path = new String[] { "/" };
                } else {
                    String[] newPath = new String[path.length - 1];
                    System.arraycopy(path, 1, newPath, 0, newPath.length);
                    path = newPath;
                }
                subfolders.add(path);
            }
        }
        return subfolders;
//        List<Folder> subfolders = new ArrayList<Folder>();
//        for (org.buni.meldware.mail.mailbox.Folder f : 
//            service.getSubfolders(folder, filter)) {
//            subfolders.add(newInstance(service, f, hints));
//        }
//        return subfolders;
    }
    
    public void append(InputStream in, int size, List<String> flags, 
            Date timestamp) {
        MailCreateListener mcl = new IMAP4MailCreateAdapter(size);
        Mail m = Mail.create(service.getBodyManager(), in, mcl);
        
        MessageDataUtil mdu = new MessageDataUtil(service.getBodyManager());
        MessageData md = mdu.create(m, true);
        service.createMail(folder, md);        
    }
    
    /**
     * @see org.buni.meldware.mail.api.Folder#append(java.lang.String, java.lang.String[], java.lang.String[], java.lang.String[], java.lang.String, java.lang.String)
     */
    @Tx(TxType.REQUIRED)
    public void append(String from, String[] to, String[] cc, String[] bcc, 
            String subject, String body) {
        MessageDataUtil mdu = new MessageDataUtil(service.getBodyManager());
        Mail m = Mail.create(service.getBodyManager(), from, to, cc, bcc, subject, body);
        MessageData md = mdu.create(m, false);
        service.createMail(folder, md);
    }

    public void append(Message message) {
        // TODO Fix this (its nasty).
        MessageData md = (MessageData) message;
        service.createMail(folder, md);
    }
    
    /**
     * Returns a list of sequence numbers on an expunge.  The sequnce numbers
     * will be in reverse order to handle the IMAP auto-decrement rule.
     */
    public List<Long> expunge(boolean returnUids) {
        if (returnUids) {
            //long[] _uids = getUids();
            FolderInfo fi = getFolderInfo();
            List<Long> deletedUids = service.expunge(folder, returnUids);
            List<Long> seqNums = new ArrayList<Long>(deletedUids.size());
            for (int i = deletedUids.size() - 1; i >= 0; i--) {
                long l = deletedUids.get(i);
                int seqNum = fi.getSeqNum(l);
                if (fi.isValidSeqNum(seqNum, l)) {
                    seqNums.add((long) seqNum);
                }
            }
            loadFolderInfo();
            //clearUids();
            return seqNums;
        } else {
            loadFolderInfo();
            //clearUids();
            return service.expunge(folder, returnUids);
        }
    }
    
    protected void loadFolderInfo() {
        folderInfo = service.getFolderInfo(folder);
    }
    
    
    public FolderInfo getFolderInfo() {
        return folderInfo;
    }
    
    protected void setFolderInfo(FolderInfo folderInfo) {
        this.folderInfo = folderInfo;
    }
    
    
    public Collection<Long> getMessageSizes(boolean isUid, Range[] ranges) {
        FolderInfo fi = getFolderInfo();
        Range[] normalised = fi.normalise(isUid, ranges);
        return service.getMessageSizes(folder, normalised, hints);
    }
    
    public Collection<Long> search(SearchKey searchKey, boolean isUid) {
        FolderInfo fi = getFolderInfo();
        searchKey = fi.normalise(searchKey);
        Collection<Long> uids = service.search(folder, searchKey);
        if (isUid) {
            return uids;
        } else {
            Collection<Long> seqNums = new ArrayList<Long>(uids.size());
            for (Long uid : uids) {
                //seqNums.add((long) getSeqNum(currentUids, uid));
                seqNums.add((long) fi.getSeqNum(uid));
            }
            return seqNums;
        }
    }
    
    public long getUid(int seqNum) {
        return getFolderInfo().getUid(seqNum);
    }
    
    public Iterator<Long> iterator() {
        return getFolderInfo().iterator();
    }
    
    private static class IMAP4MailCreateAdapter extends MailCreateAdapter {

        private long size;

        public IMAP4MailCreateAdapter(long size) {
            this.size = size;
        }
        
        /**
         * Creates an exact size copier based on the size handed to
         * the append command minus the size of the headers.
         * 
         * @see org.buni.meldware.mail.message.MailCreateAdapter#getCopier(int)
         */
        @Override
        public Copier getCopier() {
            long bodySize = size - getBytesRead();
            return new ExactSizeCopier(bodySize);                
        }

        /**
         * Extracts the from address from the headers.
         * 
         * @see org.buni.meldware.mail.message.MailCreateAdapter#getFrom()
         */
        @Override
        public MailAddress getFrom() {
            MailAddress from;
            String[] fromArr = getMailHeaders().getHeader("From");
            if (fromArr != null && fromArr.length > 0) {
                String sfrom = getMailHeaders().getHeader("From")[0];
                from = MailAddress.parseSMTPStyle(sfrom);
            } else {
                from = MailAddress.parseSMTPStyle("");
            }
            return from;
        }

        /**
         * Extracts the to address from the headers.
         * 
         * @see org.buni.meldware.mail.message.MailCreateAdapter#getTo()
         */
        @Override
        public MailAddress[] getTo() {
            String[] ato = getMailHeaders().getHeader("To");
            MailAddress[] to = new MailAddress[ato.length];
               for (int i = 0; i < ato.length; i++) {
                   to[i] = MailAddress.parseSMTPStyle(ato[i]);
               }
            return to;
        }
    }
       
    protected MailboxService getService() {
        return service;
    }
    
    protected long getFolderId() {
        return folder.getId();
    }
    
    protected org.buni.meldware.mail.mailbox.Folder getFolder() {
        return folder;
    }
}
