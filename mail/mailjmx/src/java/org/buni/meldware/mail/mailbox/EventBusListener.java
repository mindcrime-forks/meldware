package org.buni.meldware.mail.mailbox;

import org.buni.meldware.mail.mailbox.event.MailboxEvent;

public interface EventBusListener {

    void send(MailboxEvent ev);
}
