package org.buni.meldware.mail.mailbox;

import org.buni.meldware.common.logging.Log;
import org.buni.meldware.mail.MailException;
import org.buni.meldware.mail.mailbox.event.MailboxEvent;
import org.jgroups.Channel;
import org.jgroups.ChannelClosedException;
import org.jgroups.ChannelException;
import org.jgroups.ChannelNotConnectedException;
import org.jgroups.ExtendedReceiverAdapter;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.View;
import org.w3c.dom.Element;

public class JGroupsEventBus extends AbstractEventBus implements JGroupsEventBusMBean {

    private final static Log log = Log.getLog(JGroupsEventBus.class);
    private final static String GROUP_NAME = "org.jboss.meldware.mail.mailbox.event";
    private Channel group;
    private Element config = null;
    
    public void send(MailboxEvent ev) {
        try {
            log.debug("Sending cluster notification %d", ev.getFolderId());
            group.send(new Message(null, null, ev));
        } catch (ChannelNotConnectedException e) {
            throw new MailException("Unable to send notification", e);
        } catch (ChannelClosedException e) {
            throw new MailException("Unable to send notification", e);
        }
    }

    public void startService() throws ChannelException {
        log.info("Starting JGroups Mailbox Event Bus");
        if (config == null) {
            group = new JChannel();
        } else {
            group = new JChannel(config);
        }
        group.setReceiver(new EventBusReceiver());
        group.connect(GROUP_NAME);
    }
    
    public void stopService() {
        log.info("Stopping JGroups Mailbox Event Bus");
        group.disconnect();
        group = null;
    }
    
    private class EventBusReceiver extends ExtendedReceiverAdapter {
        
        @Override
        public void receive(Message msg) {
            MailboxEvent ev = (MailboxEvent) msg.getObject();
            log.debug("Received cluster notification %d", ev.getFolderId());
            sendNotification(ev);
        }

		@Override
		public void viewAccepted(View view) {
			log.debug("Member joined: %s", view.getVid());
		}
    }
    
    public void setConfig(Element config) {
        this.config = config;
    }

}
