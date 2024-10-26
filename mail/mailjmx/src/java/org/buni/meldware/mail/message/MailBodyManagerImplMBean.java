package org.buni.meldware.mail.message;

import org.buni.meldware.mail.JPAService;
import org.jboss.system.ServiceMBean;

public interface MailBodyManagerImplMBean extends MailBodyManager, ServiceMBean {

    void setJPAService(JPAService jpaService);
    
}
