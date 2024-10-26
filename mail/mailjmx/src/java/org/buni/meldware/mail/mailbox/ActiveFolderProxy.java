package org.buni.meldware.mail.mailbox;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.LinkedBlockingQueue;

import org.buni.meldware.common.logging.Log;
import org.buni.meldware.mail.api.ActiveFolder;
import org.buni.meldware.mail.api.FolderListener;
import org.buni.meldware.mail.api.FolderUpdates;
import org.buni.meldware.mail.api.Hints;
import org.buni.meldware.mail.mailbox.event.ExpungeEvent;
import org.buni.meldware.mail.mailbox.event.MailboxEvent;


/**
 * A folder that will automatically update if new mail arrives.
 * 
 * @author Michael Barker
 */
public class ActiveFolderProxy extends FolderProxy implements EventBusListener, ActiveFolder {

    private final static Log log = Log.getLog(ActiveFolderProxy.class);
    private final BlockingQueue<MailboxEvent> events = new LinkedBlockingQueue<MailboxEvent>();
    
    public ActiveFolderProxy(MailboxService service, 
            org.buni.meldware.mail.mailbox.Folder folder, Hints hints) {
        super(service, folder, hints);
        
    }

    public void open() {
        getService().addEventBusListener(getId(), this);
        loadFolderInfo();
    }
    
    public void close() {
        getService().removeEventBusListener(getFolderId(), this);
    }    

    public void send(MailboxEvent ev) {
        log.debug("Received notification for folder: %d", getId());
        events.add(ev);
        Iterator<FolderListener> fl = listeners.iterator();
        if (fl.hasNext()) {
            FolderUpdates updates = refresh();
            while (fl.hasNext()) {
                FolderListener l = fl.next();
                l.folderChanged(updates);
            }
        }
    }
    
    /**
     * We need to synchronize the refreshing of the folder due to all of
     * the evil things it does.
     */
    public synchronized FolderUpdates refresh() {
        
        // Flush the queue.
        Collection<MailboxEvent> cEvents = new ArrayList<MailboxEvent>();
        events.drainTo(cEvents);
        FolderInfo folderInfo = getFolderInfo();
        
        if (cEvents.size() > 0) {
            
            long expungeVersion = -1;
            
            for (MailboxEvent event : cEvents) {
                switch (event.getType()) {
                case MESSAGE:
                    break;
                case EXPUNGE:
                    ExpungeEvent ee = (ExpungeEvent) event;
                    expungeVersion = Math.max(expungeVersion, ee.getVersion());
                }
            }
            
            if (expungeVersion != -1) {
                getFolder().setExpungeVersion(expungeVersion);
            }
            loadFolderInfo();
            FolderInfo newFolderInfo = getFolderInfo();
            
            long[] expunged = folderInfo.getExpunged(newFolderInfo);            
            int[] seqNums = folderInfo.getSeqNums(expunged);
            long recent = newFolderInfo.getRecent();
            long exists = newFolderInfo.getExists();
            
            return new FolderUpdates(seqNums, exists, recent);            
        } else {
            long recent = folderInfo.getRecent();
            long exists = folderInfo.getExists();
            return new FolderUpdates(new int[0], exists, recent, false);
        }
    }

    @Override
    protected FolderProxy newInstance(MailboxService service, Folder f, Hints hints) {
        return new ActiveFolderProxy(service, f, hints);
    }

    private final Set<FolderListener> listeners = new CopyOnWriteArraySet<FolderListener>();
    
    public void addFolderListener(FolderListener listener) {
        listeners.add(listener);
    }

    public boolean removeFolderListener(FolderListener listener) {
        return listeners.remove(listener);
    }

}
