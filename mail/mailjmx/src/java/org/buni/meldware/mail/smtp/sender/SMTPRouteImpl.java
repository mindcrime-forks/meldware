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
package org.buni.meldware.mail.smtp.sender;

import org.buni.meldware.mail.domaingroup.DomainGroupMBean;

/**
 * SMTP Route
 * @author acoliver
 * @version $Revision: 1.1 $
 * @see org.buni.meldware.mail.smtp.sender.SMTPRoute
 */
public class SMTPRouteImpl implements SMTPRoute {
    private String hostname;

    private DomainGroupMBean domainGroup;

    private boolean routeAll;

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getHostname() {
        return this.hostname;
    }

    public void setDomainGroup(DomainGroupMBean domainGroup) {
        this.domainGroup = domainGroup;
    }

    public DomainGroupMBean getDomainGroup() {
        return this.domainGroup;
    }

    public void setRouteAll(boolean routeAll) {
        this.routeAll = routeAll;
    }

    public boolean getRouteAll() {
        return this.routeAll;
    }
}
