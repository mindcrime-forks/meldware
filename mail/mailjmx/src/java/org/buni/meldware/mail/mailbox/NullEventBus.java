package org.buni.meldware.mail.mailbox;

import org.buni.meldware.mail.mailbox.event.MailboxEvent;

public class NullEventBus extends AbstractEventBus implements NullEventBusMBean {

    public void send(MailboxEvent ev) {
        sendNotification(ev);
    }
    
}
