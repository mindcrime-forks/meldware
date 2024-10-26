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
 * an SMTPRoute associates a DomainGroup with an SMTPHost meaning that any
 * mail that qualifies for the DomainGroup should be routed to the host
 * defined by this route.
 *
 * @author Andrew C. Oliver
 * @version $Revision: 1.1 $
 */
interface SMTPRoute {
    /**
     * host to send mail through
     */
    void setHostname(String hostname);

    /**
     * @return host to send mail through
     */
    String getHostname();

    /**
     * set whether to route for all external mail rather than list of domains
     */
    void setRouteAll(boolean routeall);

    /**
     * @return route for all external mail rather than list of domains (aka gateway)
     */
    boolean getRouteAll();

    /**
     * domains to be sent through this hostname (above)
     */
    void setDomainGroup(DomainGroupMBean domainGroup);

    /**
     * @return domainGroup holding the domains that define this route
     */
    DomainGroupMBean getDomainGroup();
}
