package org.buni.meldware.mail.mailbox;

import org.jboss.system.ServiceMBean;
import org.w3c.dom.Element;

public interface JGroupsEventBusMBean extends EventBus, ServiceMBean {

    public void setConfig(Element config);
}
