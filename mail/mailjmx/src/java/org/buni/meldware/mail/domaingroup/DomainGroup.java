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

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.jboss.system.ServiceMBeanSupport;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * represents a group of domains (such as for using as a required "to" domain in
 * verify identity, authentication required, etc). mikea: domains should be case
 * insensitive, and should actually be a set (not a list)
 * 
 * @author Andrew C. Oliver
 * @author <a href='mailto:mikea@xoba.com'> mike andrews </a>
 * @version $Revision: 1.4 $
 */
public class DomainGroup extends ServiceMBeanSupport implements
        DomainGroupMBean {

    private static final long serialVersionUID = 3760843471744938808L;

    private static final Logger logger = Logger.getLogger(DomainGroup.class);

    private static final Pattern ipAddrPattern = Pattern
            .compile("^\\[(\\d+\\.\\d+\\.\\d+\\.\\d+)\\]$");

    private boolean includesLocalInterfaces;

    public String postmaster;

    public DomainGroup(boolean includesLocalInterfaces) {
        this.includesLocalInterfaces = includesLocalInterfaces;
    }

    public DomainGroup() {
    }

    /**
     * a comparator for case-insensitive string representations
     * 
     * @author <a href='mikea@xoba.com'>mike andrews</a>
     */
    static public class CaseInsensitiveStringComparator implements Comparator<String> {
        public int compare(String a, String b) {
            return a.toString().toLowerCase().compareTo(
                    b.toString().toLowerCase());
        }

        public boolean equals(Object c) {
            if (c instanceof CaseInsensitiveStringComparator) {
                return true;
            } else {
                return false;
            }
        }
        
        public int hashCode() {
            return this.getClass().hashCode();
        }
    }

    private Set<String> domains = new TreeSet<String>(new CaseInsensitiveStringComparator());

    /*
     * (non-Javadoc)
     * 
     * @see org.buni.meldware.mail.domaingroup.DomainGroupMBean#add(java.lang.String)
     */
    public void add(String domain) {
        String[] domainList = domain.trim().split(";");
        for (int i = 0; i < domainList.length; i++) {
            domains.add(domainList[i]);
        }
    }

    public void remove(String domain) {
        String[] domainList = domain.trim().split(";");
        for (int i = 0; i < domainList.length; i++) {
            domains.remove(domainList[i]);
        }
    }

    /* (non-Javadoc)
     * @see org.buni.meldware.mail.domaingroup.DomainGroupMBean#listDomains()
     */
    public String[] listDomains() {
        String[] retval = new String[domains.size()];
        retval = (String[]) domains.toArray(retval);
        return retval;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.buni.meldware.mail.domaingroup.DomainGroupMBean#isInGroup(java.lang.String)
     */
    public boolean isInGroup(String domain) {
        boolean isInGroup = domains.contains(domain);
        if (isInGroup) {
            return true;
        } else if (includesLocalInterfaces) {
            this.includesLocalInterfaces = true;
            Matcher m = ipAddrPattern.matcher(domain);
            if (m.matches()) {
                try {
                    InetAddress addr = InetAddress.getByName(m.group(1));
                    // returns true if a network interface is bound to address
                    return NetworkInterface.getByInetAddress(addr) != null;
                } catch (Exception e) {
                    // a problem processing inet address not worth failing
                    // over...
                    logger.warn("problem processing address domain: " + domain,
                            e);
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /* (non-Javadoc)
     * @see org.buni.meldware.mail.domaingroup.DomainGroupMBean#getIncludesLocalInterfaces()
     */
    public boolean getIncludesLocalInterfaces() {
        return includesLocalInterfaces;
    }

    /**
     * Gets the post master address for this domain group.
     * @return
     */
    public String getPostmaster() {
        return postmaster;
    }

    public void setPostmaster(String postmaster) {
        this.postmaster = postmaster;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.buni.meldware.mail.domaingroup.DomainGroupMBean#setDomains(org.w3c.dom.Element)
     */
    public void setDomains(Element element) {
        String attr = element.getAttribute("includes-local-interfaces");
        if (attr.equals("true")) {
            includesLocalInterfaces = true;
        } else {
            includesLocalInterfaces = false;
        }

        NodeList list = element.getElementsByTagName("domain");
        domains.clear();
        for (int k = 0; k < list.getLength(); k++) {
            Node n = list.item(k);
            add(getNodeText(n).trim());
        }
    }

    private static String getNodeText(Node e) {
        StringBuffer buf = new StringBuffer();
        int type = e.getNodeType();
        if (type == Node.CDATA_SECTION_NODE || type == Node.TEXT_NODE) {
            buf.append(e.getNodeValue());
        } else if (type == Node.ELEMENT_NODE) {
            NodeList list = e.getChildNodes();
            for (int i = 0; i < list.getLength(); i++) {
                Node child = list.item(i);
                buf.append(getNodeText(child));
            }
        }
        return buf.toString();
    }

    /**
     * Determines if a host is part of one of the domains 
     * specified in this domain group.  E.g. mail.jboss.org
     * would be in the domain jboss.org.
     * 
     * @param hostname The name of the host to test.
     */
    public boolean isHostInGroup(String hostname) {

        hostname = hostname.trim().toLowerCase();
        boolean result = false;

        for (Iterator i = domains.iterator(); i.hasNext();) {
            String domain = (String) i.next();
            domain = domain.trim().toLowerCase();
            if (hostname.endsWith(domain)) {
                result = true;
            }
        }

        if (log.isDebugEnabled()) {
            log.debug("Checked host: " + hostname + " in domains: " + domains
                    + " result: " + result);
        }

        return result;
    }
}
