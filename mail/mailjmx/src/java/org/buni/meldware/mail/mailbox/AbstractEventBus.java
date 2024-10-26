package org.buni.meldware.mail.mailbox;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.buni.meldware.mail.mailbox.event.MailboxEvent;
import org.jboss.system.ServiceMBeanSupport;

public abstract class AbstractEventBus extends ServiceMBeanSupport implements EventBus {

    private final Set<EventBusListener> listeners = 
        new CopyOnWriteArraySet<EventBusListener>();

    public void addEventBusListener(EventBusListener listener) {
        listeners.add(listener);
    }

    public void removeEventBusListener(EventBusListener listener) {
        listeners.remove(listener);
    }
    
    protected void sendNotification(MailboxEvent ev) {
        for (EventBusListener l : listeners) {
            l.send(ev);
        }        
    }
}
