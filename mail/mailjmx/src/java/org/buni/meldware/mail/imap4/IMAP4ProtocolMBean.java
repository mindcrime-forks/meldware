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
package org.buni.meldware.mail.imap4;

import org.buni.meldware.mail.ProtocolFactory;
import org.buni.meldware.mail.mailbox.MailboxService;
import org.buni.meldware.mail.protocol.ProtocolSettings;
import org.buni.meldware.mail.userrepository.UserRepository;

/**
 * IMAP4Protocol is used by clients to retrieve mail.
 * 
 * @author Eric Daugherty
 * @author Thorsten Kunz
 * @version $Revision: 1.3 $
 */
public interface IMAP4ProtocolMBean extends ProtocolFactory, ProtocolSettings {
    /**
     * used by the XMBean stuff to set up our properties (see sample)
     * 
     * @param properties Element from JBoss
     */
    // void setProperties(Element properties);
    /**
     * return the proeprties as a DOM object
     * 
     * @return Element containing our properties...
     */
    // Element getProperties();

    /**
     * Sets mailbox for this POP protocol to use.
     * 
     * @param mailboxManager
     */
    void setMailboxManager(MailboxService mailboxManager);

    MailboxService getMailboxManager();



    //methods also in protocol settings generic interface that must be repeated due to mbeans
    Class getProtocolInterfaceClass();
     
     /**
      * @return Returns the sslDomain.
      */
     public abstract String getSslDomain();

     /**
      * @param sslDomain The sslDomain to set.
      */
     public abstract void setSslDomain(String sslDomain);
     
     /**
      * @return Returns the requireClientCert.
      */
     public abstract boolean isRequireClientCert();

     /**
      * @param requireClientCert The requireClientCert to set.
      */
     public abstract void setRequireClientCert(boolean requireClientCert);

     /**
      * @return Returns the requireTls.
      */
     public abstract boolean isRequireTls();

     /**
      * @param requireTls The requireTls to set.
      */
     public abstract void setRequireTls(boolean requireTls);
     
     /**
      * @return Returns the tlsEnabled.
      */
     public boolean isTlsEnabled();

     /**
      * @param tlsEnabled The tlsEnabled to set.
      */
     public void setTlsEnabled(boolean tlsEnabled);
     
     /**
      * @return Returns the userRepository.
      */
     public abstract UserRepository getUserRepository();

     /**
      * @param userRepository The userRepository to set.
      */
     public abstract void setUserRepository(UserRepository userRepository);
     
     /**
      * @return Returns the servername.
      */
     public abstract String getServername();

     /**
      * @param servername The servername to set.
      */
     public abstract void setServername(String servername);

     String printPerformanceStats();
     
     public boolean isTimingEnabled();

     public void setTimingEnabled(boolean timingEnabled);

}
