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

import static java.lang.String.format;
import static org.buni.meldware.common.util.ArrayUtil.join;
import static org.buni.meldware.mail.util.HibernateUtil.singleResult;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.transaction.Status;
import javax.transaction.Synchronization;
import javax.transaction.TransactionManager;

import org.buni.meldware.common.logging.Log;
import org.buni.meldware.common.util.ArrayUtil;
import org.buni.meldware.mail.JPAService;
import org.buni.meldware.mail.MailException;
import org.buni.meldware.mail.api.FolderExistsException;
import org.buni.meldware.mail.api.FolderFilter;
import org.buni.meldware.mail.api.FolderMessage;
import org.buni.meldware.mail.api.FolderNotExistsException;
import org.buni.meldware.mail.api.Hints;
import org.buni.meldware.mail.api.Range;
import org.buni.meldware.mail.api.SearchKey;
import org.buni.meldware.mail.api.Folder.FlagType;
import org.buni.meldware.mail.mailbox.event.ExpungeEvent;
import org.buni.meldware.mail.mailbox.event.MailboxEvent;
import org.buni.meldware.mail.mailbox.event.MessageEvent;
import org.buni.meldware.mail.mailbox.search.SearchContext;
import org.buni.meldware.mail.mailbox.search.SearchQuery;
import org.buni.meldware.mail.mailbox.search.SearchQueryFactory;
import org.buni.meldware.mail.message.Body;
import org.buni.meldware.mail.message.EnvelopedAddress;
import org.buni.meldware.mail.message.MailBodyManager;
import org.buni.meldware.mail.util.Node;
import org.buni.meldware.mail.util.io.Copier;
import org.jboss.aspects.tx.Tx;
import org.jboss.aspects.tx.TxType;
import org.jboss.system.ServiceMBean;
import org.jboss.system.ServiceMBeanSupport;
import org.jboss.tm.TransactionManagerLocator;

/**
 * Default Mailbox service impelmentation using hibernate.
 * 
 * @author Andrew C. Oliver
 * @author Jason Pugsley
 * @author Michael Barker
 * @version $Revision: 1.57 $
 */
public class MailboxServiceImpl extends ServiceMBeanSupport implements MailboxService, ServiceMBean {

    private Log log = Log.getLog(MailboxServiceImpl.class);
    
    MailBodyManager mgr;

    private String hacks; // db specific hacks
    
    private String postmaster = "postmaster";
    
    private final ExecutorService notificationPool;

	private JPAService jpaService;

    private long defaultHardSizeLimit;

    private long defaultSoftSizeLimit;

    private boolean isQuotaEnabled;
    
    public MailboxServiceImpl(int notificationPoolSize) {
        notificationPool = Executors.newFixedThreadPool(notificationPoolSize);
    }

    public EntityManager emInit() {
    	if (jpaService == null) {
    		throw new MailException("EntityManager is not initialised");
    	}
    	return jpaService.getEntityManager();
    }
    
    public void setJPAService(JPAService jpaService) {
    	this.jpaService = jpaService;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.buni.meldware.mail.mailbox.MailboxService#getMailboxById(long)
     */
    @Tx(TxType.REQUIRED)
    public Mailbox getMailboxById(long id) {
        return (Mailbox) emInit().find(Mailbox.class, id);
    }
    
    public MailboxProxy createProxy(long id, Hints hints) {
        Mailbox m = getMailboxById(id);
        MailboxProxy mp = new MailboxProxy(this, m, hints);
        return mp;
    }
    
    public ActiveMailboxProxy createActiveProxy(long id, Hints hints) {
        Mailbox m = getMailboxById(id);
        ActiveMailboxProxy mp = new ActiveMailboxProxy(this, m, hints);
        return mp;
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.buni.meldware.mail.mailbox.MailboxService#getMailboxByAlias(java.lang.String)
     */
    @Tx(TxType.REQUIRED)
    public Mailbox getMailboxByAlias(String alias) {
        String query = "from Mailbox as mbox join fetch mbox.defaultInFolder where mbox.aliases.name=:alias";
        return singleResult(emInit().createQuery(query).setParameter("alias", alias.toLowerCase()), Mailbox.class);
    }

    public MailboxProxy createProxy(String alias, boolean create, Hints hints) {
        Mailbox m = getMailboxByAlias(alias);
        if (m == null) {
            m = createMailbox(alias);
        }
        MailboxProxy mp = new MailboxProxy(this, m, hints);
        return mp;
    }
    
    public ActiveMailboxProxy createActiveProxy(String alias, boolean create, Hints hints) {
        Mailbox m = getMailboxByAlias(alias);
        if (m == null) {
            m = createMailbox(alias);
        }
        ActiveMailboxProxy mp = new ActiveMailboxProxy(this, m, hints);
        return mp;
    }
    
    
    /*
     * (non-Javadoc)
     * 
     * @see org.buni.meldware.mail.mailbox.MailboxService#getDefaultInFolderByAlias(java.lang.String)
     */
    @Tx(TxType.REQUIRED)
    public Folder getDefaultInFolderByAlias(String alias) {
        return getMailboxByAlias(alias).getDefaultInFolder();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.buni.meldware.mail.mailbox.MailboxService#getDefaultOutFolderByAlias(java.lang.String)
     */
    @Tx(TxType.REQUIRED)
    public Folder getDefaultOutFolderByAlias(String alias) {
        return getMailboxByAlias(alias).getDefaultOutFolder();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.buni.meldware.mail.mailbox.MailboxService#createMailbox(java.lang.String)
     */
    @Tx(TxType.REQUIRED)
    public Mailbox createMailbox(String alias) {
        EntityManager session = emInit();
        Mailbox box = this.getMailboxByAlias(alias);
        if (box != null) {
            throw new RuntimeException("You cannot create a SECOND mailbox by that name");
        }
        box = new Mailbox();
        box.setHardSizeLimit(getDefaultHardSizeLimit());
        box.setSoftSizeLimit(getDefaultSoftSizeLimit());
        Alias aliasObj = new Alias();
        aliasObj.setName(alias.toLowerCase());
        box.addAlias(aliasObj);
        Folder inbox = new Folder();
        inbox.setName("INBOX");
        inbox.setParent(box);
        Folder sent = new Folder();
        sent.setName("Sent");
        sent.setParent(box);
        box.setDefaultInFolder(inbox);
        box.setDefaultOutFolder(sent);
        Folder trash = new Folder();
        trash.setName("Trash");
        trash.setParent(box);
        Folder draft = new Folder();
        draft.setName("Drafts");
        draft.setParent(box);
        box.addFolder(inbox);
        box.addFolder(sent);
        box.addFolder(trash);
        box.addFolder(draft);
        session.persist(box);
        session.persist(inbox);
        session.persist(sent);
        session.persist(trash);
        session.persist(draft);
        session.persist(aliasObj);
        
        session.flush();
        
        return box;
    }

    @Tx(TxType.REQUIRED)
    public void save(Mailbox mailbox) {
        EntityManager em = emInit();
        em.merge(mailbox);
    }

    public long getDefaultHardSizeLimit() {
        return defaultHardSizeLimit;
    }

    public long getDefaultSoftSizeLimit() {
        return defaultSoftSizeLimit;
    }

    public void setDefaultHardSizeLimit(long defaultHardSizeLimit) {
        this.defaultHardSizeLimit = defaultHardSizeLimit;
    }

    public void setDefaultSoftSizeLimit(long defaultSoftSizeLimit) {
        this.defaultSoftSizeLimit = defaultSoftSizeLimit;
    }
    
    public void setQuotaEnabled(boolean isQuotaEnabled) {
        this.isQuotaEnabled = isQuotaEnabled;
    }

    public boolean getQuotaEnabled() {
        return isQuotaEnabled;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.buni.meldware.mail.mailbox.MailboxService#createFolder(org.buni.meldware.mail.mailbox.Folder,
     *      java.lang.String)
     */
    @Tx(TxType.REQUIRED)
    public Folder createFolder(Folder xfolder, String foldername) {
        EntityManager session = emInit();
        Folder folder = session.find(Folder.class, xfolder.getId());
        Folder newFolder = new Folder();
        newFolder.setName(foldername);
        folder.addFolder(newFolder);
        newFolder.setParent(folder);
        session.persist(newFolder);
        return newFolder;
    }
    
    /**
     * Creates a folder given the specified path.  Will throw an exception if
     * the folder already exists.
     * 
     * @param xfolder
     * @param path
     * @return
     */
    @Tx(TxType.REQUIRED)
    public Folder createFolder(Folder xfolder, String[] path) {
        Folder parent = emInit().find(Folder.class, xfolder.getId());
        Folder child = null;
        
        for (int i = 0; i < path.length; i++) {
            child = getSubfolderByName(parent, path[i]);
            if (child == null) {
                child = createFolder(parent, path[i]);
            } else if (i == path.length - 1) {
                String message = format("Folder: %s exists", join(path, "/"));
                throw new FolderExistsException(message);
            }
            parent = child;
        }
        
        return child;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.buni.meldware.mail.mailbox.MailboxService#deleteMailboxById(long)
     */
    @Tx(TxType.REQUIRED)
    public void deleteMailboxById(long id) {
        EntityManager session = emInit();
        session.remove(session.find(Mailbox.class, id));
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.buni.meldware.mail.mailbox.MailboxService#deleteMailboxByAlias(java.lang.String)
     */
    @Tx(TxType.REQUIRED)
    public void deleteMailboxByAlias(String alias) {
        emInit().remove(getMailboxByAlias(alias));
    }
    
    @Tx(TxType.REQUIRED)
    public List<MessageBody> getMessageBody(MessageData message) {
        MessageData md = emInit().merge(message);
        return md.getMessageBodies();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.buni.meldware.mail.mailbox.MailboxService#getMailBody(org.buni.meldware.mail.mailbox.MessageData)
     */
    @Tx(TxType.REQUIRED)
    public List<Body> getMailBody(MessageData message) {
        emInit();
        List<Long> bodies = message.getBodyId();
        List<Body> retval = new ArrayList<Body>(bodies.size());
        for (int i = 0; i < bodies.size(); i++) {
            long bid = bodies.get(i);
            Body body = bid > -1 ? mgr.createMailBody(bid) : null;
            retval.add(body);
        }
        return retval;
    }
    
    /**
     * Deliver a message to a list of users.
     * 
     * @param md The mail to be delivered.
     * @param spamState
     * @param tos
     * @return
     */
    @Tx(TxType.REQUIRED)
    public Map<EnvelopedAddress,String[]> deliver(MessageData md, 
            FolderMessage.SpamState spamState, 
            Map<EnvelopedAddress,String[]> tos) {
        
        Map<EnvelopedAddress,String[]> failed = new HashMap<EnvelopedAddress,String[]>();
        EntityManager em = emInit();
        final List<MessageEvent> notifs = new ArrayList<MessageEvent>();
        
        for (Map.Entry<EnvelopedAddress,String[]> to : tos.entrySet()) {
            EnvelopedAddress ea = to.getKey();
            String[] target = to.getValue();
            String user = ea.getUser();
            Mailbox mailbox;
            try {
                mailbox = getMailboxByAlias(user);
            } catch (EntityNotFoundException e) {
                log.warn("User: %s not found sending to postmaster", ea.toSMTPString());
                mailbox = getPostmasterMailbox();
            }
            if (getQuotaEnabled()) {
                long totalSize = getMailboxSize(mailbox);
                long limit = mailbox.getHardSizeLimit();
                // Make sure the limit is sensible.
                limit = limit > 1 ? limit : getDefaultHardSizeLimit();
                if (totalSize > limit) {
                    failed.put(ea, target);
                    log.warn("Mailbox has exceeded site limit: %d, actual %d", limit, totalSize);
                    continue;
                }
            }
            Folder f;
            try {
                f = getSubfolderByPath(mailbox, target);
            } catch (FolderNotExistsException e) {
                log.warn("Path: %s, not found using INBOX", join(target, ","));
                f = mailbox.getDefaultInFolder();
            }
            // Store the message.
            FolderEntry fe = new FolderEntry(f, getNextUid(f.getId()), md, Flag.EMPTY_FLAG_SET);
            fe.setUserSpamState(spamState);
            f.addMessage(fe);
            em.persist(fe);
            notifs.add(new MessageEvent(f.getId(), fe.getId()));
        }
        
        try {
            TransactionManager txManager = TransactionManagerLocator.getInstance().locate();
            txManager.getTransaction().registerSynchronization(new Synchronization() {

                public void afterCompletion(int status) {
                    if (status == Status.STATUS_COMMITTED) {
                        for (MessageEvent notif : notifs) {
                            eventBus.send(notif);
                        }
                    } else {
                        log.warn("Transaction in unexpected status: %d", status);
                    }
                }
                
                public void beforeCompletion() {}
            });
        } catch (Exception e) {
            // It doesn't matter if we can't register.
            // The transaction is unlikely to complete in that case.
            log.warn("Failed to register with transaction: %s", e.getMessage());
        }
        
        
        return failed;
    }
    

    /**
     * Delivers a message to a folder.
     */
    @Tx(TxType.REQUIRED)
    public FolderEntry createMail(Folder xfolder, MessageData mail, 
            FolderMessage.SpamState spamState) {
        EntityManager session = emInit();
        //Folder folder = session.find(Folder.class, xfolder.getId());
        Folder folder = session.merge(xfolder);
        
        FolderEntry fe = new FolderEntry(folder, getNextUid(folder.getId()), mail, Flag.EMPTY_FLAG_SET);
        fe.setUserSpamState(spamState);
        folder.addMessage(fe);
        
        session.persist(fe);
        sendDeliveryNotification(folder.getId(), fe.getUid());
        return fe;
    }

    final static String MAILBOX_SIZE = "SELECT sum(fe.message.messageSize) FROM FolderEntry fe where fe.folder.mailbox.id = :mailbox";
    private long getMailboxSize(Mailbox mailbox) {
        EntityManager em = emInit();
        Query q = em.createQuery(MAILBOX_SIZE);
        q.setParameter("mailbox", mailbox.getId());
        try {
            Number n = (Number) q.getSingleResult();
            return n.longValue();
        } catch (NoResultException e) {
            return 1;
        }        
    }

    public FolderEntry createMail(Folder xfolder, MessageData mail) {
        return createMail(xfolder, mail, FolderMessage.SpamState.UNKNOWN);
    }    

    @Tx(TxType.REQUIRESNEW)
    public long getNextUid(long folderId) {
        EntityManager session = emInit();

        String queryStr;

        if ("hsqldb".equals(hacks)) {
            queryStr = "SELECT nextuid FROM folder_seq WHERE id = :folderId";
        } else {
            queryStr = "SELECT nextuid FROM folder_seq WHERE id = :folderId FOR UPDATE";
        }

        Query getQ = session.createNativeQuery(queryStr);
        getQ.setParameter("folderId", Long.valueOf(folderId));
        long uid = ((Number) getQ.getSingleResult()).longValue();

        long nextUid = uid + 1;
        String updateStr = "UPDATE folder_seq SET nextuid = :uid WHERE id = :folderId";
        Query updateQ = session.createNativeQuery(updateStr);
        updateQ.setParameter("uid", nextUid);
        updateQ.setParameter("folderId", folderId);
        updateQ.executeUpdate();

        return uid;
    }
    
    @Tx(TxType.REQUIRED)
    public long[] getUids(Folder folder) {
        String query = "SELECT fe.uid FROM FolderEntry fe WHERE fe.folder.id = :folderId AND fe.expungeVersion > :version ORDER BY fe.uid";
        Query q = emInit().createQuery(query);
        q.setParameter("folderId", folder.getId());
        q.setParameter("version", folder.getExpungeVersion());
        List results = q.getResultList();
        long[] uids = new long[results.size()];
        for (int i = 0; i < results.size(); i++) {
            Long l = (Long) results.get(i);
            uids[i] = l.longValue();
        }
        return uids;
    }

    /**
     * @see org.buni.meldware.mail.mailbox.MailboxService#getMessage(org.buni.meldware.mail.mailbox.Folder, long)
     */
    @Tx(TxType.REQUIRED)
    public FolderEntry getMessage(Folder folder, long uid) {
        Query q = emInit().createNamedQuery(FolderEntry.BY_FOLDER_UID);
        q.setParameter("folder", folder);
        q.setParameter("uid", uid);
        return (FolderEntry) q.getSingleResult();
    }

    public Collection<FolderEntry> getMessages(Folder folder, Range[] ranges) {
        long folderId = folder.getId();
        long expungeVersion = folder.getExpungeVersion();
        return getMessages(folderId, expungeVersion, ranges, Hints.NONE);
    }
    
    public Collection<FolderEntry> getMessages(Folder folder, Range[] ranges, Hints hints) {
        long folderId = folder.getId();
        long expungeVersion = folder.getExpungeVersion();
        return getMessages(folderId, expungeVersion, ranges, hints);
    }
    
    /**
     * Get a list of message with the specified range.  Dynamically creates 
     * the query based on the ranges supplied.
     * 
     * @param folder
     * @param ranges
     * @param includeFlags
     * @return
     */
    @Tx(TxType.REQUIRED)
    private Collection<FolderEntry> getMessages(long folderId, long expungeVersion, Range[] ranges, Hints hints) {
        emInit();
        
        log.debug("Getting messages for folder %d", folderId);
        
        StringBuilder queryB = new StringBuilder();
        queryB.append("FROM FolderEntry fe ");
        if (hints.isMessage()) {
            queryB.append("JOIN FETCH fe.message ");
        }
        if (hints.isFlags()) {
            queryB.append("LEFT OUTER JOIN FETCH fe.flags ");            
        }
        queryB.append("WHERE fe.folder.id = ?1 ");
        queryB.append("AND fe.expungeVersion > ?2 ");
        final int RANGE_IDX = 3;
        
        appendRangeClause(queryB, ranges, "fe.uid", RANGE_IDX);
        queryB.append("ORDER BY fe.uid");
        
        Query query = emInit().createQuery(queryB.toString());
        
        query.setParameter(1, folderId);
        query.setParameter(2, expungeVersion);
        setRangeValues(query, ranges, RANGE_IDX);
        
        @SuppressWarnings("unchecked")
        Collection<FolderEntry> result = (List<FolderEntry>) query.getResultList();
        
        if (result.size() > 0 && hints.isFlags()) {
            Collection<FolderEntry> cResult = new LinkedHashSet<FolderEntry>(result);
            result = cResult;
        }
        
        return result;
    }
    

    /**
     * @see org.buni.meldware.mail.mailbox.MailboxService#getMessageSizes(org.buni.meldware.mail.api.Range[])
     */
    @Tx(TxType.REQUIRED)
    public Collection<Long> getMessageSizes(Folder f, Range[] ranges, Hints hints) {
        StringBuilder queryB = new StringBuilder();
        queryB.append("SELECT fe.message.messageSize FROM FolderEntry fe WHERE fe.folder.id = ?1 AND fe.expungeVersion > ?2 ");
        
        final int RANGE_IDX = 3;
        appendRangeClause(queryB, ranges, "fe.uid", RANGE_IDX);
        queryB.append("ORDER BY fe.uid");
        
        Query q = emInit().createQuery(queryB.toString());
        q.setParameter(1, f.getId());
        q.setParameter(2, f.getExpungeVersion());
        setRangeValues(q, ranges, RANGE_IDX);
        
        @SuppressWarnings("unchecked")
        Collection<Long> result = (Collection<Long>) q.getResultList();
        return result;
    }

    /**
     * @see org.buni.meldware.mail.mailbox.MailboxService#setDeleted(org.buni.meldware.mail.api.Range[], boolean)
     */
    @Tx(TxType.REQUIRED)
    public void setDeleted(Folder f, Range[] ranges, boolean deleted) {
        StringBuilder queryB = new StringBuilder();
        queryB.append("UPDATE FolderEntry fe SET fe.deleted = ?1 WHERE fe.folder = ?2 ");
        appendRangeClause(queryB, ranges, "fe.uid", 3);
        Query q = emInit().createQuery(queryB.toString());
        q.setParameter(1, deleted);
        q.setParameter(2, f);
        setRangeValues(q, ranges, 3);
        q.executeUpdate();
    }

    @Tx(TxType.REQUIRED)
    public void setFlag(Folder f, Range[] ranges, FlagType flag, boolean isSet) {
    	String pattern = "UPDATE FolderEntry fe SET fe.{0} = ?1 WHERE fe.folder = ?2 and fe.{0} = ?3 ";
        StringBuilder queryB = new StringBuilder();
        queryB.append(MessageFormat.format(pattern, flag.toString()));
        appendRangeClause(queryB, ranges, "fe.uid", 4);
        Query q = emInit().createQuery(queryB.toString());
        q.setParameter(1, isSet);
        q.setParameter(2, f);
        q.setParameter(3, !isSet);
        setRangeValues(q, ranges, 4);
        q.executeUpdate();
    }
    
    private int appendRangeClause(StringBuilder sb, Range[] ranges, String id, int start) {
        int idx = start;
        if (ranges.length > 0) {
            sb.append("AND (");
            for (int i = 0; i < ranges.length; i++) {
                sb.append("(");
                sb.append(id).append(" >= ?");
                sb.append(idx++);
                sb.append(" AND ");
                sb.append(id).append(" <= ?");
                sb.append(idx++);
                sb.append(")");
                if (i < ranges.length - 1) {
                    sb.append(" OR ");
                }
            }
            sb.append(") ");
        }
        return idx - start;
    }
    
    private void setRangeValues(Query q, Range[] ranges, int start) {
        int idx = start;
        for (int i = 0; i < ranges.length; i++) {
            q.setParameter(idx++, ranges[i].getMin());
            q.setParameter(idx++, ranges[i].getMax());
        }        
    }
    

    /*
     * (non-Javadoc)
     * 
     * @see org.buni.meldware.mail.mailbox.MailboxService#setBodyManager(org.buni.meldware.mail.message.MailBodyManagerMBean)
     */
    public void setBodyManager(MailBodyManager bodymgr) {
        this.mgr = bodymgr;

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.buni.meldware.mail.mailbox.MailboxService#getBodyManager()
     */
    public MailBodyManager getBodyManager() {
        return this.mgr;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.buni.meldware.mail.mailbox.MailboxService#getFolderIdByAlias(java.lang.String)
     */
    @Tx(TxType.REQUIRED)
    public long getMailboxIdByAlias(String alias) {
        String thealias = alias.toLowerCase();
        String query = "select mbox.id from Mailbox mbox where mbox.aliases.name=:alias";
        Long l = singleResult(emInit().createQuery(query).setParameter("alias", thealias), Long.class);
        if (l == null) {
            return -1;
        } else {
            return l;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.buni.meldware.mail.mailbox.MailboxService#createAlias(long,
     *      java.lang.String)
     */
    @Tx(TxType.REQUIRED)
    public boolean createAlias(long id, String aliasName) {
        EntityManager session = emInit();
        Mailbox m = session.find(Mailbox.class, id);
        Alias alias = new Alias();
        alias.setName(aliasName.toLowerCase());
        m.addAlias(alias);
        session.persist(alias);
        session.persist(m);
        return true;
    }

    @Tx(TxType.REQUIRED)
    public Folder getSubfolderByName(Folder folder, String name) {
        String query = "from Folder f where f.parent.id = :folderid and f.name = :name";
        List result = emInit().createQuery(query).setParameter("folderid", folder.getId()).setParameter("name", name)
                .getResultList();
        if (result.size() == 0) {
            return null;
        }
        return (Folder) result.get(0);
    }

    @Tx(TxType.REQUIRED)
    public List<Folder> getSubfolders(Folder reference, FolderFilter filter) {
        List<Folder> result = new ArrayList<Folder>();
        if (!reference.isMailbox() && filter.match(reference.getPath())) {
            result.add(reference);
        }
        List<Folder> subfolders = getAllSubfolders(reference);
        for (Folder f : subfolders) {
            if (filter.match(f.getPath()) 
                    || filter.match(f.getPath() + Folder.FOLDER_SEP)) {
                result.add(f);
            }
        }
        return result;
    }
    
    @Tx(TxType.REQUIRED)
    public List<Folder> getAllSubfolders(Folder reference) {
        EntityManager em = emInit();
        reference = em.merge(reference);
        String s = "SELECT f FROM Folder f WHERE f.mailbox.id = :mailbox AND f.path LIKE :path";
        Query q = em.createQuery(s);
        q.setParameter("mailbox", reference.getMailbox().getId());
        q.setParameter("path", reference.getPath() + Folder.FOLDER_SEP + "%");
        @SuppressWarnings("unchecked")
        List<Folder> result = (List<Folder>) q.getResultList();
        return result;
    }
    
    
    /** 
     * @see org.buni.meldware.mail.mailbox.MailboxService#updateMessageMetadata(org.buni.meldware.mail.mailbox.MessageData)
     */
    @Tx(TxType.REQUIRED)
    public void updateFolderEntry(FolderEntry fe) {
        emInit().merge(fe);
    }
    
    /**
     * @see org.buni.meldware.mail.mailbox.MailboxService#copy(org.buni.meldware.mail.mailbox.Folder, org.buni.meldware.mail.mailbox.Folder, org.buni.meldware.mail.api.Range[])
     */
    @Tx(TxType.REQUIRED)
    public void copy(Folder source, org.buni.meldware.mail.api.Folder target, 
            Range[] ranges) {
        EntityManager em = emInit();
        Folder folder = em.find(Folder.class, target.getId());
        Collection<FolderEntry> toCopy = getMessages(source, ranges, Hints.FLAGS);
        
        for (FolderEntry fe : toCopy) {
            FolderEntry newFe = new FolderEntry(folder, getNextUid(folder.getId()), fe);            
            folder.addMessage(newFe);
            em.persist(newFe);
        }
    }

    @Tx(TxType.REQUIRED)
    public Node<FolderSummary> folderSummary(String alias) {
        Mailbox mbox = getMailboxByAlias(alias);
        //List<Object[]> retval = new ArrayList<Object[]>();
        FolderSummary fs = new FolderSummary();
        fs.setId(mbox.getId());
        fs.setName(mbox.getName());
        fs.setParentId(-1);        
        Node<FolderSummary> retval = new Node<FolderSummary>(fs);
        retSummary(retval);
        return retval;
    }
    
    
    /**
     * Recursively loads all of the folder summaries.
     * 
     * TODO: Look to add DB specific optimisations.
     *  - Inline views.
     *  - Recursive query (i.e. CONNECT BY for Oracle)
     * 
     * @param pid
     * @param path
     * @param parent
     */
    @Tx(TxType.REQUIRED)
    private void retSummary(Node<FolderSummary> parent) {
        
    	EntityManager session = emInit();
        long pid = parent.getValue().getId();
        
        String qTotal = "select f.id, f.name, f.parent.id, count(m.id)"
            + " from Folder as f left join f.messages m"
            + " where f.parent.id = :pid" 
            + " group by f.id, f.name, f.parent.id";
        
        @SuppressWarnings("unchecked")        
        List<Object[]> rTotal = (List<Object[]>) session.createQuery(qTotal).setParameter("pid", pid).getResultList();

        Map<Object,FolderSummary> folders = new HashMap<Object,FolderSummary>();
        for (Object[] row : rTotal) {
            FolderSummary fs = new FolderSummary();
            fs.setId(((Long)row[0]).longValue());
            fs.setName((String) row[1]);
            fs.setParentId(((Long)row[2]).longValue());
            fs.setTotal(((Long)row[3]).longValue());
            folders.put(row[0], fs);
        }
        
        String qUnread = "select f.id, count(m.id)"
            + " from Folder as f left join f.messages m with m.seen = false"
            + " where f.parent.id = :pid"
            + " group by f.id";
        
        @SuppressWarnings("unchecked")
        List<Object[]> rUnread = (List<Object[]>) session.createQuery(qUnread).setParameter("pid", pid).getResultList();
        
        for (Object[] row : rUnread) {
            FolderSummary fs = folders.get(row[0]);
            if (fs != null) {
                Node<FolderSummary> n = new Node<FolderSummary>(fs);
                parent.addChild(n);
                fs.setUnread(((Long)row[1]).longValue());
                retSummary(n);
            } else {
                // XXX Log warning.
            }
        }
    }

    /**
     * Expunges a folder.  Sets the expunge version of the message to current
     * folder version.  This allows individual clients to see a 'snapshot' of
     * the folder based on the specified version number.
     * 
     * @see org.buni.meldware.mail.mailbox.MailboxService#expunge(org.buni.meldware.mail.mailbox.Folder,
     *      boolean)
     */
    @Tx(TxType.REQUIRED)
    public List<Long> expunge(Folder folder, boolean returnMessages) {

        EntityManager em = emInit();
        String queryStr;
        final long folderId = folder.getId();

        if ("hsqldb".equals(hacks)) {
            queryStr = "SELECT expungeVersion FROM folder_seq WHERE id = :folderId";
        } else {
            queryStr = "SELECT expungeVersion FROM folder_seq WHERE id = :folderId FOR UPDATE";
        }
        
        Query getQ = em.createNativeQuery(queryStr);
        getQ.setParameter("folderId", Long.valueOf(folderId));
        long version = ((Number) getQ.getSingleResult()).longValue();
        final long nextVersion = version + 1;
        String updateStr = "UPDATE folder_seq SET expungeVersion = :version WHERE id = :folderId";
        Query updateQ = em.createNativeQuery(updateStr);
        updateQ.setParameter("version", nextVersion);
        updateQ.setParameter("folderId", folderId);
        updateQ.executeUpdate();
        
        Query expQ = em.createQuery("UPDATE FolderEntry fe SET fe.expungeVersion = :nextversion, fe.expungeDate = :eDate WHERE fe.deleted = true AND fe.folder.id = :folder AND fe.expungeVersion > :version");
        expQ.setParameter("eDate", new Date());
        expQ.setParameter("nextversion", nextVersion);
        expQ.setParameter("folder", folderId);
        expQ.setParameter("version", version);
        expQ.executeUpdate();

        // Get the expunged messages.
        List<Long> retval = new ArrayList<Long>();
        if (returnMessages) {
            Query q = em.createQuery("SELECT fe.uid FROM FolderEntry fe WHERE folder = :folder AND fe.deleted = true AND fe.expungeVersion = :version");
            q.setParameter("folder", folder);
            q.setParameter("version", nextVersion);
            List uids = q.getResultList();
            for (Object o : uids) {
                Number n = (Number) o;
                retval.add(n.longValue());
            }
        }
        
        try {
            TransactionManager txManager = TransactionManagerLocator.getInstance().locate();
            txManager.getTransaction().registerSynchronization(new Synchronization() {

                public void afterCompletion(int status) {
                    if (status == Status.STATUS_COMMITTED) {
                        log.warn("Transaction commited sending notification");
                        sendExpungeNotification(folderId, nextVersion);
                    } else {
                        log.warn("Transaction in unexpected status: %d", status);
                    }
                }
                
                public void beforeCompletion() {}
            });
        } catch (Exception e) {
            // It doesn't matter if we can't register.
            // The transaction is unlikely to complete in that case.
            log.warn("Failed to register with transaction: %s", e.getMessage());
        }
        
        em.refresh(folder);
        
        return retval;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.buni.meldware.mail.mailbox.MailboxService#getMail(long)
     */
    @Tx(TxType.REQUIRED)
    public MessageData getMail(long uid) {
        MessageData data = emInit().find(MessageData.class, uid);
        return data;
    }
    
    /**
     * Refreshes the folder, including the expungeVersion and returns a list
     * of the messages have been expunged.
     * 
     * @param f
     * @return
     */
    public long[] getExpunged(Folder f, long newVersion) {
        EntityManager em = emInit();
        String query = "SELECT fe.uid FROM FolderEntry fe WHERE fe.folder.id = :folderId AND fe.expungeVersion > :oldVersion AND fe.expungeVersion <= :version ORDER BY fe.uid";
        Query q = em.createQuery(query);
        q.setParameter("folderId", f.getId());
        q.setParameter("oldVersion", f.getExpungeVersion());
        q.setParameter("version", newVersion);
        List result = q.getResultList();
        long[] expungedUids = new long[result.size()];
        int i = 0;
        for (Object o : result) {
            Number n = (Number) o;
            expungedUids[i++] = n.longValue();
        }
        return expungedUids;
    }

    
    @Tx(TxType.REQUIRED)
    public FolderInfo getFolderInfo(Folder f) {
        
        EntityManager em = emInit();
        String query = "SELECT fe.uid, fe.recent, fe.seen FROM FolderEntry fe WHERE fe.folder.id = :folderId AND fe.expungeVersion > :version ORDER BY fe.uid";
        Query q = em.createQuery(query);
        q.setParameter("folderId", f.getId());
        q.setParameter("version", f.getExpungeVersion());
        
        long recent = 0;
        long unseen = 0;
        long firstUnseen = -1;
        List results = q.getResultList();
        long[] uids = new long[results.size()];
        
        for (int i = 0; i < results.size(); i++) {
            Object[] row = (Object[]) results.get(i);
            uids[i] = ((Number) row[0]).longValue();
            if ((Boolean) row[1]) {
                recent++;
            }
            if (!(Boolean) row[2]) {
                unseen++;
                if (firstUnseen == -1) {
                    firstUnseen = i + 1;
                }
            }
        }
        
        return new FolderInfo(uids, recent, unseen, firstUnseen);
    }

    /**
     * @see org.buni.meldware.mail.mailbox.MailboxService#moveFolder(org.buni.meldware.mail.mailbox.Folder,
     *      org.buni.meldware.mail.mailbox.Folder, java.lang.String)
     */
    @Tx(TxType.REQUIRED)
    public Folder moveFolder(Folder folder, Folder target, String name) {
    	EntityManager session = emInit();
        Folder f = session.find(Folder.class, folder.getId());
        Folder t = session.find(Folder.class, target.getId());
        f.setName(name);
        if (folder.getId() != t.getId()) {
            f.getParent().getFolders().remove(f);
            t.addFolder(f);
            f.setParent(t);
        }
        session.persist(f);
        return f;
    }
    
    /**
     * XXX: Optimise this store the root mailbox with the folder.
     * @param f
     * @return
     */
    private Mailbox getMailbox(Folder f) {
        while (!f.isMailbox()) {
            f = f.getParent();
        }
        return (Mailbox) f;
    }
    
    /**
     * Moves the folder to the specified path.  Throws FolderExistsException
     * if the path points to a folder that alread exists.
     * 
     * @param source
     * @param path
     * @return
     */
    @Tx(TxType.REQUIRED)
    public Folder moveFolder(Folder source, String[] path) {
        source = emInit().merge(source);
        Folder parent = getMailbox(source);
        Folder child = null;
        
        for (int i = 0; i < path.length; i++) {
            child = getSubfolderByName(parent, path[i]);
            if (i < path.length - 1) {
                if (child == null) {
                    child = createFolder(parent, path[i]);
                }
                parent = child;
            } else {
                if (child == null) {
                    source.setParent(parent);
                    source.setName(path[i]);
                    child = source;
                } else {
                    String message = String.format("Folder: %s exists", 
                            ArrayUtil.join(path, "/"));
                    throw new FolderExistsException(message);
                }
            }
        }
        
        return child;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.buni.meldware.mail.mailbox.MailboxService#getPathFor(org.buni.meldware.mail.mailbox.Folder)
     */
    @Tx(TxType.REQUIRED)
    public String getPathFor(Folder folder) {
        
        String result;
        
        if (folder.getParent() == null) {
            result = "/";
        } else {
            List<String> pathArr = new ArrayList<String>();
            Folder current = folder;
            
            while (current.getParent() != null) {
                pathArr.add(0, current.getName());
                current = current.getParent();
            }
            
            result = ArrayUtil.join(pathArr, "/");            
        }
        
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.buni.meldware.mail.mailbox.MailboxService#deleteFolder(org.buni.meldware.mail.mailbox.Folder)
     */
    @Tx(TxType.REQUIRED)
    public void deleteFolder(Folder folder) {
    	EntityManager session = emInit();
        Folder f = session.find(Folder.class, folder.getId());
        f.getParent().getFolders().remove(f);
        session.remove(f);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.buni.meldware.mail.mailbox.MailboxService#getSubfolderByPath(org.buni.meldware.mail.mailbox.Mailbox,
     *      java.lang.String[])
     */
    @Tx(TxType.REQUIRED)
    public Folder getSubfolderByPath(Folder parent, String[] path) {
        if (path.length == 0) {
            return parent;
        }
        String pathStr = parent.getPath() + join(path, Folder.FOLDER_SEP, Folder.FOLDER_SEP, "");
        Query q = emInit().createNamedQuery(Folder.BY_PATH);
        q.setParameter("mailbox", parent.getMailbox());
        q.setParameter("path", pathStr);
        try {
            return (Folder) q.getSingleResult();
        } catch (NoResultException e) {
            throw new FolderNotExistsException(pathStr + " does not exist");
        }
    }

    public void setHacks(String dbName) {
        this.hacks = dbName;
    }

    public String getHacks() {
        return hacks;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.buni.meldware.mail.mailbox.MailboxService#getMessageBody(org.buni.meldware.mail.mailbox.MessageData,
     *      int)
     */
    @Tx(TxType.REQUIRED)
    public MessageBody getMessageBody(MessageData message, int position) {
        message = emInit().merge(message);
        MessageBody body = null;
        if (position < message.getBody().size()) {
            body = message.getBody().get(position);
        }
        return body;
    }
    
    @Tx(TxType.REQUIRED)
    public MessageBody getMessageBody(MessageBody part, int position) {
        part = emInit().merge(part);
        MessageBody body = null;
        if (position < part.getChildren().size()) {
            body = part.getChildren().get(position);
        }
        return body;
    }
    
    /**
     * Prints the whole message to the output stream.
     */
    @Tx(TxType.REQUIRED)
    public void mimePrintMessage(MessageData md, boolean includeHeaders, 
            OutputStream out, Copier copier) {
        MessageData message = emInit().merge(md);
        MessageDataUtil mdu = new MessageDataUtil(getBodyManager());
        try {
            InputStream in = mdu.getInputStream(message, includeHeaders);
            copier.copy(new BufferedInputStream(in), out, 8192);
            //mdu.printMessage(message, includeHeaders, out, copier);
        } catch (IOException e) {
            throw new MailException(e);
        }
    }
    
    /**
     * Writes the specified body to the output stream, excluding its own
     * mime header.
     */
    @Tx(TxType.REQUIRED)
    public void mimePrintBody(long id, boolean includeHeaders, 
            OutputStream out, Copier copier) {
        MessageBody body = emInit().find(MessageBody.class, id);
        MessageDataUtil mdu = new MessageDataUtil(getBodyManager());
        try {
            mdu.printBodyPart(body, includeHeaders, out, copier);
        } catch (IOException e) {
            throw new MailException(e);
        }
    }
    
    
    

    @Tx(TxType.REQUIRED)
    public List<String> getAliases(String user) {
        //there is a nasty bug that prevents us from doing this query in hql given Alias's unidirectional relationship
        String query = "select b.name from Alias a, Alias b where b.folder_id = a.folder_id and a.name=:alias";
        @SuppressWarnings("unchecked")
        List<String> result = (List<String>)emInit().createNativeQuery(query)
                .setParameter("alias", user.toLowerCase()).getResultList();
        return result;
    }

    @Tx(TxType.REQUIRED)
    public void deleteAlias(String username, String alias) {
        long folder = getMailboxIdByAlias(username);
        String deleteQuery = "delete from Alias a where a.name= :alias and a.folder_id=:folder";
        emInit().createNativeQuery(deleteQuery)
                .setParameter("alias", alias.toLowerCase())
                .setParameter("folder", folder).executeUpdate();
    }
    
    /**
     * Get the mailbox for the postmaster.
     * 
     * @return
     */
    @Tx(TxType.REQUIRESNEW)
    public Mailbox getPostmasterMailbox() {
        Mailbox mbox = getMailboxByAlias(postmaster);
        if (mbox == null) {
            log.info("Creating postmaster mailbox: %s", postmaster);
            try {
                mbox = createMailbox(postmaster);
            } catch (RuntimeException e) {
                log.warn("Error creating postmaster mailbox: %s", e.getMessage());
                // Probably already created.
                mbox = getMailboxByAlias(postmaster);
            }
        }
        return mbox;
    }
    
    /**
     * Starts the mailbox service and creates the postmaster mailbox if 
     * required.
     * @throws NamingException 
     *
     */
    public void startService() throws NamingException {
        getPostmasterMailbox();
    }

    /**
     * @return the postmaster
     */
    public String getPostmaster() {
        return postmaster;
    }

    /**
     * @param postmaster the postmaster to set
     */
    public void setPostmaster(String postmaster) {
        this.postmaster = postmaster;
    }
    
    @Tx(TxType.REQUIRED)
    public Collection<Long> search(Folder f, SearchKey searchKey) {
        SearchQueryFactory fact = new SearchQueryFactory();
        SearchQuery q = fact.create(searchKey);
        SearchContext ctx = new SearchContext(f, emInit(), this);
        Set<Long> uids = q.getResults(ctx);
        Set<Long> sorted = new TreeSet<Long>(uids);
        return sorted;
    }

	final static String SPAM_QUERY = "SELECT m FROM MessageData m WHERE m.id IN (SELECT f.message.id FROM FolderEntry f WHERE f.systemSpamState = ?1) AND m.id NOT IN (SELECT f.message.id FROM FolderEntry f WHERE f.systemSpamState = ?2)";
	
    @Tx(TxType.REQUIRED)
	public <T> List<T> getMessages(Transformer<MessageData,T> t, 
			FolderMessage.SpamState include, FolderMessage.SpamState exclude) {
		EntityManager em = emInit();
		Query q = em.createQuery(SPAM_QUERY);
		q.setParameter(1, include);
		q.setParameter(2, exclude);
		@SuppressWarnings("unchecked")
		List<MessageData> mds = (List<MessageData>) q.getResultList();
		List<T> result = new ArrayList<T>();
		for (MessageData md : mds) {
			T value = t.transform(md);
			if (value != null) {
				result.add(value);
			}
		}
		return result;
	}
    
    private final ConcurrentHashMap<Long,Set<EventBusListener>> listeners = 
        new ConcurrentHashMap<Long,Set<EventBusListener>>();
    private EventBus eventBus;
    
    public void addEventBusListener(long folderId, EventBusListener fl) {
        add(listeners, folderId, fl);
    }
    
    public void removeEventBusListener(long folderId, EventBusListener fl) {
        remove(listeners, folderId, fl);
    }
    
    private void sendDeliveryNotification(long folderId, long uid) {
        eventBus.send(new MessageEvent(folderId, uid));
    }
    
    private void sendExpungeNotification(long folderId, long uid) {
        eventBus.send(new ExpungeEvent(folderId, uid));
    }

    
    public void setEventBus(EventBus eventBus) {
        this.eventBus = eventBus;
        eventBus.addEventBusListener(new EventBusListener() {
            public void send(MailboxEvent ev) {
                notifyListeners(ev);
            }
        });
    }
    
    

    /**
     * Send the notification to all of the local listeners.
     * 
     * @param folderId
     */
    private void notifyListeners(final MailboxEvent ev) {
        Set<EventBusListener> fls = listeners.get(ev.getFolderId());
        if (fls != null) {
            log.debug("Sending notification to %d listeners", fls.size());
            for (final EventBusListener fl : fls) {
            	// Execute the notifications aysnchronously.
            	notificationPool.execute(new Runnable() {
					public void run() {
	                    fl.send(ev);
					}
            	});
            }
        }
    }
    
    protected static <K,T> void add(ConcurrentHashMap<K,Set<T>> m, 
            K id, T t) {
        Set<T> st1 = new CopyOnWriteArraySet<T>();
        st1.add(t);
        Set<T> st2 = m.putIfAbsent(id, st1);
        if (st2 != null) {
            st2.add(t);
        }
    }

    protected static <K,T> void remove(ConcurrentHashMap<K,Set<T>> m, 
            K id, T t) {
        Set<T> st1 = m.get(id);
        if (st1 != null) {
            st1.remove(t);
        }
    }
    
}