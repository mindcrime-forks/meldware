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
package org.buni.meldware.mail.fetchmail;

import org.buni.meldware.mail.smtp.SMTPProtocolMBean;
import org.jboss.system.ServiceMBean;

/**
 * @author hwr@pilhuhn.de
 * @version $Revision: 1.1 $
 */
public interface PopperMBean extends ServiceMBean {

    String getPopUser();

    void setPopUser(String user);

    String getPassword();

    void setPassword(String password);

    String getServername();

    void setServername(String name);

    int getPopPort();

    void setPopPort(int port);

    void pop();

    String getLocalUser();

    void setLocalUser(String user);

    boolean isDeleteAfterPop();

    void setDeleteAfterPop(boolean doIt);

    void setSMTPProtocol(SMTPProtocolMBean bean);
}
