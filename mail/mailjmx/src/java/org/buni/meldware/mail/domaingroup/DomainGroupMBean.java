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
package org.buni.meldware.mail.domaingroup;

import org.jboss.system.ServiceMBean;
import org.w3c.dom.Element;

/**
 * represents a list of domains
 * @author Andrew C. Oliver
 * @version $Revision: 1.1 $
 */
public interface DomainGroupMBean extends ServiceMBean {

    /**
     * Adds a domain or domains (';' seperated) to the group
     *
     * @param string the domain to add to domain group
     */
    public void add(String domainList);

    /**
     * Removes a domain or domains (';' seperated) from
     * this domain group.
     * 
     * @param domainList
     */
    public void remove(String domainList);

    /**
     * lists the domains explicitly added to the group; 
     * this method should not be used for testing membership since other domains
     * not in the array may test positive for being in group too.
     *
     * @return array of all domains (character-case irrelevant)
     */
    public String[] listDomains();

    /**
     * tests whether or not a domain is part of the group, including all local
     * internet address domains too as required by rfc 1123-5.2.17. 
     * (example: the domain string "[127.0.0.1]" will always be in group, etc...)
     *
     * @param the domain name
     * @return whether or not the domain is in the group. domains are case insensitive
     */
    public boolean isInGroup(String string);

    /**
     * @return whether or not the group implicitly includes local network interface literals; i.e.,
     * domains like '[127.0.0.1]' or '[64.32.179.43]' etc
     */
    public boolean getIncludesLocalInterfaces();

    /**
     * clears and initializes the group to have the domains in the xml element. top-level element
     * should be 'domains' with an optional attribute 'includes-local-interfaces' whose value
     * is either 'true' or 'false'. if true, then 'getIncludesLocalInterfaces()' method will
     * return true. child elements of 'domains' elements are 'domain' elements, each of which
     * whose text content is the name of a domain part of the group.
     *
     * @param element the xml element containing domain configuration information
     */
    void setDomains(Element element);

    /**
     * Determines if a host is part of one of the domains 
     * specified in this domain group.  E.g. mail.jboss.org
     * would be in the domain jboss.org.
     * 
     * @param hostname The name of the host to test.
     */
    boolean isHostInGroup(String hostname);

    /**
     * Gets the mail address for the post master for this domain
     * group.
     * 
     * @return
     */
    String getPostmaster();

    /**
     * Sets the mail address for the post master for the domain group.
     * @param postmasterAddr
     */
    public void setPostmaster(String postmasterAddr);
}
