/**
 * 
 */
package org.buni.meldware.mail.store;

import org.buni.meldware.mail.JPAService;
import org.jboss.system.ServiceMBean;

/**
 * @author Michael.Barker
 *
 */
public interface StoreMBean extends Store, ServiceMBean {

    void setBufferSize(int bufferSize);

    void setStartIndex(int startIndex);

    void setCompressBufferSize(int size);
    
    void setPageSize(int pageSize);

    void setCompress(boolean compress);

    void setHashed(boolean hashed);
    
    void setJPAService(JPAService jpaService);

}
