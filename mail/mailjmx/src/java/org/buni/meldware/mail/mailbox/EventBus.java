package org.buni.meldware.mail.mailbox;

import org.buni.meldware.mail.mailbox.event.MailboxEvent;


public interface EventBus {

    void send(MailboxEvent ev);
    
    void addEventBusListener(EventBusListener listener);

    void removeEventBusListener(EventBusListener listener);
}
