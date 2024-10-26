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

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.management.ObjectName;

import org.buni.meldware.common.util.ArrayUtil;
import org.buni.meldware.mail.message.EnvelopedAddress;
import org.buni.meldware.mail.message.Mail;
import org.buni.meldware.mail.message.MailAddress;
import org.buni.meldware.mail.message.MailBodyManager;
import org.buni.meldware.mail.util.MMJMXUtil;
import org.columba.ristretto.log.RistrettoLogger;
import org.columba.ristretto.message.Address;
import org.columba.ristretto.parser.ParserException;
import org.columba.ristretto.smtp.SMTPException;
import org.columba.ristretto.smtp.SMTPProtocol;
import org.jboss.system.ServiceMBeanSupport;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xbill.DNS.ARecord;
import org.xbill.DNS.Cache;
import org.xbill.DNS.Credibility;
import org.xbill.DNS.DClass;
import org.xbill.DNS.ExtendedResolver;
import org.xbill.DNS.MXRecord;
import org.xbill.DNS.Message;
import org.xbill.DNS.Name;
import org.xbill.DNS.RRset;
import org.xbill.DNS.Rcode;
import org.xbill.DNS.Record;
import org.xbill.DNS.Resolver;
import org.xbill.DNS.SetResponse;
import org.xbill.DNS.SimpleResolver;
import org.xbill.DNS.TextParseException;
import org.xbill.DNS.Type;

/**
 * @see org.buni.meldware.mail.smtp.sender.SMTPSenderMBean
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @author Andrew C. Oliver <acoliver at jboss.org> (refactored from Nukes code)
 * @version: $Revision: 1.9 $
 */
public class SMTPSender extends ServiceMBeanSupport implements SMTPSenderMBean {

    private static final long serialVersionUID = 3546639923579727921L;

    private Cache cache;

    private ExtendedResolver resolver;

    private boolean useCache;

    private boolean allRouted;

    private SMTPRoute soleRoute;

    Map<String,Resolver> resolvers;

    Map<ObjectName,SMTPRoute> routes;

    Map<String,ObjectName> domainRoutes;

    private boolean allowAddressLookups;
    
    private MailBodyManager mgr;


    public SMTPSender() {
        resolvers = new HashMap<String,Resolver>();
        routes = new HashMap<ObjectName,SMTPRoute>();
        domainRoutes = new HashMap<String,ObjectName>();
    }

    /* (non-Javadoc)
     * @see org.jboss.system.ServiceMBeanSupport#startService()
     */
    protected void startService() throws Exception {
        this.cache = new Cache();
        if (resolvers.values().size() > 0) {
            Resolver[] r = (Resolver[]) resolvers.values().toArray(
                    new Resolver[resolvers.values().size()]);
            this.resolver = new ExtendedResolver(r);
        } else {
            this.resolver = new ExtendedResolver();
        }

    }

    public void addDNSServer(String dns) throws UnknownHostException {
        SimpleResolver sr = new SimpleResolver(dns);
        if (resolver == null)
            resolver = new ExtendedResolver(); // setting attributes need this and they get set very early in the lifecycle
        resolver.addResolver(sr);
        resolvers.put(dns, sr);
    }

    public void addDNSRoute(String nm) {
        try {
            if (this.allRouted) {
                throw new RuntimeException(
                        "you cannot add multiple routes when one is set to route all mail");
            }
            ObjectName name = new ObjectName(nm);
            SMTPRoute route = (SMTPRoute) MMJMXUtil.getMBean(name,
                    SMTPRoute.class);
            routes.put(name, route);
            if (!route.getRouteAll()) {
                String domains[] = route.getDomainGroup().listDomains();
                for (int i = 0; i < domains.length; i++) {
                    domainRoutes.put(domains[i], name);
                }
            } else {
                this.allRouted = true;
                this.soleRoute = route;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /* (non-Javadoc)
     * @see org.buni.meldware.mail.smtp.sender.SMTPSenderMBean#clearDNSCache()
     */
    public void clearDNSCache() {
        cache = new Cache();
    }

    /* (non-Javadoc)
     * @see org.buni.meldware.mail.smtp.sender.SMTPSenderMBean#getCacheEnabled()
     */
    public boolean getCacheEnabled() {
        return useCache;
    }

    /* (non-Javadoc)
     * @see org.buni.meldware.mail.smtp.sender.SMTPSenderMBean#getDNSServers()
     * @todo implement
     */
    public Element getDNSServers() {
        //TODO output in XML
        return null;
    }

    /* (non-Javadoc)
     * @see org.buni.meldware.mail.smtp.sender.SMTPSenderMBean#getDNSRoutes()
     * @todo implement
     */
    public Element getDNSRoutes() {
        //TODO output in XMLS
        return null;
    }

    /* (non-Javadoc)
     * @see org.buni.meldware.mail.smtp.sender.SMTPSenderMBean#mxLookup(java.lang.String)
     */
    public String[] mxLookup(String address) {
        Record[] records = performMXLookup(address);
        String[] results = new String[records.length];
        for (int i = 0; i < records.length; i++) {
            if (records[i] instanceof MXRecord) {
                results[i] = ((MXRecord) records[i]).getTarget().toString();
            } else if (records[i] instanceof ARecord) {
                results[i] = ((ARecord) records[i]).getAddress().getHostAddress();
            }
        }
        return results;
    }

    public String listDNSRoutes() {
        Iterator it = routes.keySet().iterator();

        StringBuffer res = new StringBuffer();
        while (it.hasNext()) {
            res.append((String) it.next());
            if (it.hasNext())
                res.append(", ");
        }

        return res.toString();
    }

    public String listDNSServers() {
        Iterator it = resolvers.keySet().iterator();

        StringBuffer res = new StringBuffer();
        while (it.hasNext()) {
            res.append((String) it.next());
            if (it.hasNext())
                res.append(", ");
        }

        return res.toString();
    }

    /**
     * @param address
     * @return
     */
    private Record[] performMXLookup(String address) {
        // for simplicity the code for cache and no cache is just whether we set to
        //this.cache or whether we make our own local instance.
        Cache cache = null;
        if (useCache == true) {
            cache = this.cache;
        } else {
            cache = new Cache();
        }

        // Do we have a mounted resolver ?
        if (resolver == null) {
            throw new RuntimeException(
                    "No name servers are mounted!  Mount one will you?");
        }

        Name name; // Name _name = Name.fromString(name); does not work, why ?
        try {
            name = new Name(address + ".");
            //trailing dots make it "absolute" so that it is happy
        } catch (TextParseException e) {
            throw new RuntimeException("Invalid lookup ", e);
        }

        // First lookup the MX records.
        SetResponse mxCached = queryDNS(cache, resolver, name, Type.MX);

        List<Record> aRecords = new ArrayList<Record>();
        if (!mxCached.isSuccessful()) {
            // If there isn't an MX record, lookup the A record directly.
            SetResponse aCached = queryDNS(cache, resolver, name, Type.A);
            
            if (aCached.isSuccessful()) {
                for (RRset rrset : aCached.answers()) {
                    for (Iterator i = rrset.rrs(); i.hasNext();) {
                        Record r = (Record) i.next();
                        aRecords.add(r);
                    }
                }                        
            }
        } else {
            // Otherwise look up the A record for the MX record.
            List<Record> mxRecords = new ArrayList<Record>();
            // Get all of the MX records.
            for (RRset rrset : mxCached.answers()) {
                for (Iterator i = rrset.rrs(); i.hasNext();) {
                    Record r = (Record) i.next();
                    mxRecords.add(r);
                }
            }
            
            // Lookup all of the A records for the MX records.
            for (Record record : mxRecords) {
                if (record instanceof MXRecord) {
                    MXRecord mxRecord = (MXRecord) record;
                    Name mxHost = mxRecord.getTarget();
                    SetResponse aCached = queryDNS(cache, resolver, mxHost, Type.A);
                    
                    if (aCached.isSuccessful()) {
                        for (RRset rrset : aCached.answers()) {
                            for (Iterator i = rrset.rrs(); i.hasNext();) {
                                Record r = (Record) i.next();
                                aRecords.add(r);
                            }
                        }          
                    }
                }
            }
        }
        
        return (Record[]) aRecords.toArray(new Record[aRecords.size()]);
    }
    
    /**
     * Queries the resolver and adds the response into the cache.
     * 
     * @param resolver
     * @param query
     * @return
     */
    private SetResponse queryDNS(Cache cache, Resolver resolver, Name name, 
            int type) {
        try {
            SetResponse aCached = cache.lookupRecords(name, type, 
                    Credibility.NONAUTH_ANSWER);
            if (!aCached.isSuccessful()) {
                Record r = Record.newRecord(name, type, DClass.IN);
                Message q = Message.newQuery(r);
                Message response = resolver.send(q);
                short rcode = response.getHeader().getRcode();
                if (rcode == Rcode.NOERROR || rcode == Rcode.NXDOMAIN) {
                    cache.addMessage(response);
                }                
            }
            SetResponse cached = cache.lookupRecords(name, type, 
                    Credibility.NONAUTH_ANSWER);
            return cached;
        } catch (IOException ioe) {
            throw new RuntimeException("Could not resolve DNS lookup", ioe);
        }        
    }

    /* (non-Javadoc)
     * @see org.buni.meldware.mail.smtp.sender.SMTPSenderMBean#removeDNSServer(java.lang.String)
     */
    public void removeDNSServer(String dns) {
        Resolver r = (Resolver) resolvers.get(dns);
        resolver.deleteResolver(r);
        resolvers.remove(dns);
    }

    /* (non-Javadoc)
     * @see org.buni.meldware.mail.smtp.sender.SMTPSenderMBean#removeDNSRoute(java.lang.String)
     */
    public void removeDNSRoute(String nm) {
        try {
            ObjectName name = new ObjectName(nm);
            SMTPRoute route = (SMTPRoute) routes.remove(name);
            if (!route.getRouteAll()) {
                String[] domains = route.getDomainGroup().listDomains();
                for (int i = 0; i < domains.length; i++) {
                    this.domainRoutes.remove(domains[i]);
                }
            } else {
                this.allRouted = false;
                this.soleRoute = null;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /* (non-Javadoc)
     * @see org.buni.meldware.mail.smtp.sender.SMTPSenderMBean#send(org.buni.meldware.mail.smtp.Mail, boolean)
     */
    public SMTPResult[] send(Mail message, MailAddress[] exclude) {
        log.debug("sender - exclude:" + ArrayUtil.join(exclude, ","));
        ArrayList results = new ArrayList(message.getTo().size());

        //Get the addresses by domain, and send for each domain
        MailAddressesByDomain addrByDomain = new MailAddressesByDomain(
                message.getTo(), exclude);

        for (Iterator it = addrByDomain.getDomains(); it.hasNext();) {
            String domain = (String) it.next();
            MailAddress[] addressesForDomain = addrByDomain
                    .getAddresses(domain);
            ArrayList resultsForDomain = sendForDomain(addressesForDomain,
                    message);
            results.addAll(resultsForDomain);
        }

        return (SMTPResult[]) results.toArray(new SMTPResult[results.size()]);
    }

    /* (non-Javadoc)
     * @see org.buni.meldware.mail.smtp.sender.SMTPSenderMBean#send(org.buni.meldware.mail.smtp.MailAddress, org.buni.meldware.mail.smtp.Mail)
     */
    public SMTPResult send(MailAddress to, Mail mail) {
        return (SMTPResult) sendForDomain(new MailAddress[] { to }, mail).get(0);
    }

    /**
     * Determines if the error code represents an invalid address.
     * 
     * @param code
     * @return
     */
    public boolean isInvalidAddress(int code) {
        return code / 500 == 1;
    }

    public void addResults(Set addresses, int status, List results) {
        for (Iterator i = addresses.iterator(); i.hasNext();) {
            MailAddress ma = (MailAddress) i.next();
            SMTPResult result = new SMTPResultImpl(ma, status);
            results.add(result);
        }
    }

    /** Sends a mail to several recipients
     * @param to list of recipients who must all belong to the same domain
     * @param mail mail to be sent
     * @return ArrayList of SMTPResult entries. One for each recipient
     */
    private ArrayList sendForDomain(MailAddress[] to, Mail mail) {
        boolean debug = log.isDebugEnabled();

        if (debug) {
            log.debug("before lookup domain = " + to[0].getDomain());
            log.debug("resolve route yields = "
                    + resolveRoute(to[0].getDomain()));
        }
        String[] hosts = mxLookup(resolveRoute(to[0].getDomain()));
        if (debug) {
            log.debug("hosts(length=" + hosts.length + ") = ");
            for (int k = 0; k < hosts.length; k++) {
                log.debug("host[" + k + "] = " + hosts[k]);
            }
        }
        //String host = null;

        if (hosts.length < 1) {
            if (debug) {
                log.debug("Could not resolve MX lookup for "
                        + to[0].getDomain() + ". Remote mail not sent");
            }
            return SMTPResultImpl.getResolveDomainErrorForAll(to);            
        }

        SMTPProtocol protocol = null;

        // Create a set of the addresses that failed.
        Set invalid = new HashSet();
        Set retry = new HashSet();
        Set success = new HashSet();

        RistrettoLogger.setLogStream(System.out);

        try {
            // fall back to OTHER MX records in the event one host
            // is down...if the last host is down throw IOException
            // and catch below
            protocol = findHost(hosts);
            
            try {
                //per spec we first do ehlo
                protocol.ehlo(InetAddress.getLocalHost());
            } catch (SMTPException e) {
                //per spec then we try helo as a failback
                protocol.helo(InetAddress.getLocalHost());
            }
            protocol.mail(Address.parse(mail.getSender().toString()));
            log.info("Sending mail from: " + mail.getSender());

            // TODO: Make into seperate method.
            for (int i = 0; i < to.length; i++) {
                try {
                    protocol.rcpt(Address.parse(to[i].toString()));
                    success.add(to[i]);
                    log.info("Delivering to: " + to[i]);
                } catch (ParserException e) {
                    invalid.add(to[i]);
                    log.error("Invalid Address: " + to[i]);
                } catch (SMTPException e) {
                    if (isInvalidAddress(e.getCode())) {
                        invalid.add(to[i]);
                        log.error("Invalid Address: " + to[i]);
                    } else {
                        retry.add(to[i]);
                        log.error("Delivery failed, will retry: " + to[i]);
                    }
                }
            }

            if (success.size() > 0) {
                protocol.data(mail.getRawStream(getMailBodyManager()));
                protocol.quit();
            } else {
                log.warn("Skipped data send, no valid addresses");
            }

            ArrayList results = new ArrayList();
            addResults(invalid, SMTPResult.STATUS_INVALID_ADDRESS, results);
            addResults(retry, SMTPResult.STATUS_ERROR, results);
            addResults(success, SMTPResult.STATUS_OK, results);
            return results;
        } catch (IOException e) {
            log.error("Unble to send email.", e);
            return SMTPResultImpl.getErrorForAll(to);
        } catch (SMTPException e) {
            log.error("Unble to send email.", e);
            return SMTPResultImpl.getErrorForAll(to);
        } catch (ParserException e) {
            log.error("Unble to send email.", e);
            return SMTPResultImpl.getErrorForAll(to);
        } finally {
            if (protocol != null) {
                try {
                    protocol.dropConnection();
                } catch (IOException e) {
                    log.warn("Error closing SMTP Connection: ", e);
                }
            }
        }

    }

    /**
     * Connect to an SMTP server for us.
     * @throws IOException 
     */
    private SMTPProtocol findHost(String[] hosts) throws IOException {
        
        for (String host : hosts) {
            SMTPProtocol protocol = null;
            try {
                log.info("Connecting to: " + host);
                protocol = new SMTPProtocol(host); 
                protocol.openPort();
                if (protocol.getState() == SMTPProtocol.PLAIN) {
                    return protocol;
                }
            } catch (Exception e) {
                log.warn("Failed to connect to host, retrying next host; " + e.getMessage());
                if (protocol != null) {
                    try {
                        protocol.dropConnection();
                    } catch (IOException e1) {
                        log.warn("Error closing SMTP Connection: ", e1);
                    }
                }
            }
        }
        throw new IOException("Failed to connect to any SMTP Host: " + ArrayUtil.join(hosts, ","));
    }

    
    /* (non-Javadoc)
     * @see org.buni.meldware.mail.smtp.sender.SMTPSenderMBean#setCacheEnabled(boolean)
     */
    public void setCacheEnabled(boolean enabled) {
        this.useCache = enabled;
    }

    /* (non-Javadoc)
     * @see org.buni.meldware.mail.smtp.sender.SMTPSenderMBean#setDNSRoutes(org.w3c.dom.Element)
     */
    public void setDNSRoutes(Element servers) throws Exception {

        NodeList list = servers.getElementsByTagName("route");
        for (int k = 0; k < list.getLength(); k++) {
            String route = getNodeText((Node) list.item(k)).trim();
            addDNSRoute(route);
        }
    }

    /* (non-Javadoc)
     * @see org.buni.meldware.mail.smtp.sender.SMTPSenderMBean#setDNSServers(org.w3c.dom.Element)
     */
    public void setDNSServers(Element servers) throws Exception {

        NodeList list = servers.getElementsByTagName("server");
        for (int k = 0; k < list.getLength(); k++) {
            String server = getNodeText((Node) list.item(k)).trim();
            addDNSServer(server);
        }
    }

    //todo move to util class
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
     * turn anything that has a route into the proper host or leave it as is
     * if it doesn't This method probably violates the encapsulation provided
     * by domain group and if we ever need more sophisticated domain matching
     * we'll regret this but we did it all in the name of performance :-) so 
     * that we dont' have to curse through the entire collection for every address
     * for every mail.
     */
    private String[] resolveRoutes(String[] domains) {
        if (this.allRouted) {
            return new String[] { this.soleRoute.getHostname() };
        }
        String[] result = new String[domains.length];
        for (int i = 0; i < domains.length; i++) {
            Object route = this.domainRoutes.get(domains[i]);
            route = route == null ? domains[i] : ((SMTPRoute) this.routes
                    .get(route)).getHostname();
            result[i] = (String) route;
        }
        return result;
    }

    private String resolveRoute(String domain) {
        return resolveRoutes(new String[] { domain })[0];
    }

    public boolean getAllowAddressLookups() {
        return this.allowAddressLookups;
    }

    public void setAllowAddressLookups(boolean allowAddressLookups) {
        this.allowAddressLookups = allowAddressLookups;
    }

    /**
     * @see org.buni.meldware.mail.smtp.sender.SMTPSenderMBean#getMailBodyManager()
     */
    public MailBodyManager getMailBodyManager() {
        return mgr;
    }

    /**
     * @see org.buni.meldware.mail.smtp.sender.SMTPSenderMBean#setMailBodyManager(org.buni.meldware.mail.message.MailBodyManager)
     */
    public void setMailBodyManager(MailBodyManager mgr) {
        this.mgr = mgr;
    }

}

class MailAddressesByDomain {
    HashMap addressesByDomain = new HashMap();

    public MailAddressesByDomain(List<MailAddress> addresses, 
            MailAddress[] exclude) {
        for (MailAddress address : addresses) {
            EnvelopedAddress ea = new EnvelopedAddress(address);

            if (!included(ea, exclude) && !ea.getLocal()) {
                String domain = ea.getDomain();
                Set addrs = (Set) addressesByDomain.get(domain);

                if (addrs == null) {
                    addrs = new HashSet();
                    addressesByDomain.put(domain, addrs);
                }

                addrs.add(ea);
            }
        }
    }

    /** Checks to see if the address is in the list of addresses
     * not efficient for large sets, but good for now.
     * @param address to check for
     * @param list to check in
     * @return inclusion state (yes or no)
     */
    private boolean included(MailAddress address, MailAddress[] list) {
        boolean retval = false;
        for (int k = 0; k < list.length; k++) {
            if (address.equals(list[k])) {
                retval = true;
                break;
            }
        }
        return retval;
    }

    public Iterator getDomains() {
        return addressesByDomain.keySet().iterator();
    }

    public MailAddress[] getAddresses(String domain) {
        Set addrs = (Set) addressesByDomain.get(domain);

        if (addrs != null) {
            return (MailAddress[]) addrs.toArray(new MailAddress[addrs.size()]);
        } else {
            return new MailAddress[] {};
        }
    }
}
