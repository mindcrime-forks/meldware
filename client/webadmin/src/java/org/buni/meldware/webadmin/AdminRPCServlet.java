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
package org.buni.meldware.webadmin;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.management.ObjectName;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.buni.meldware.calendar.data.PreferenceConstants;
import org.buni.meldware.common.preferences.UserProfile;
import org.buni.meldware.common.preferences.UserProfileService;
import org.buni.meldware.common.util.ArrayUtil;
import org.buni.meldware.mail.ServerMBean;
import org.buni.meldware.mail.ThreadPoolMBean;
import org.buni.meldware.mail.abmounts.SystemABMountsService;
import org.buni.meldware.mail.domaingroup.DomainGroupMBean;
import org.buni.meldware.mail.imap4.IMAP4ProtocolMBean;
import org.buni.meldware.mail.mailbox.MailboxService;
import org.buni.meldware.mail.maillist.MailList;
import org.buni.meldware.mail.maillist.MailListManager;
import org.buni.meldware.mail.maillist.MailListProperties;
import org.buni.meldware.mail.maillist.MailListPropertyConstants;
import org.buni.meldware.mail.management.AdminTool;
import org.buni.meldware.mail.pop3.POP3ProtocolMBean;
import org.buni.meldware.mail.protocol.ProtocolSettings;
import org.buni.meldware.mail.smtp.SMTPProtocolMBean;
import org.buni.meldware.mail.userrepository.UserRepository;
import org.buni.meldware.mail.util.MMJMXUtil;
import org.jboss.security.plugins.JaasSecurityDomainMBean;

/**
 * @author andy
 * 
 */
public class AdminRPCServlet extends HttpServlet {

    private static final byte ERROR_BAD_OP = 1;

    private static final byte ERROR_BUG = 2;

    private AdminTool admin;

    private MailboxService mbs;

    private MailListManager mlm;
    
    private UserProfileService ups;

    private SystemABMountsService abMountService;

    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.GenericServlet#init()
     */
    @Override
    public void init() throws ServletException {
        String adminServiceName = getInitParameter("adminServiceName");
        String mailboxServiceName = getInitParameter("mailboxServiceName");
        String mailListManager = getInitParameter("mailListManager");
        String userProfileService = getInitParameter("userProfileService");
        String abMount = getInitParameter("abMountService");
        admin = (AdminTool) MMJMXUtil.getMBean(adminServiceName, AdminTool.class);
        mbs = (MailboxService) MMJMXUtil.getMBean(mailboxServiceName, MailboxService.class);
        mlm = mailListManager == null ? null : (MailListManager) MMJMXUtil.getMBean(mailListManager, MailListManager.class);
        ups = (UserProfileService) MMJMXUtil.getMBean(userProfileService, UserProfileService.class);
        this.abMountService = (SystemABMountsService) MMJMXUtil.getMBean(abMount, SystemABMountsService.class);
    }
 
    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        String contentType = request.getContentType();
        if ((contentType != null) && contentType.equals("application/xml")) {
            AdminXMLRPC wxr = new AdminXMLRPC();
            wxr.setAdminService(admin);
            wxr.setMailboxService(this.mbs);
            wxr.setUserProfileService(this.ups);
            wxr.setSystemABMountsService(this.abMountService);
            try {
              wxr.process(request, response);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("shit something went wrong with the xml stuff");
            }
            return;
        }
        
        String opname = request.getParameter("op");
        PrintWriter writer = response.getWriter();
        if (opname.equals("get") || opname.equals("post") || opname.equals("Get") || opname.equals("Post")) {
            writeError(writer, ERROR_BAD_OP, "You are a bad person and should die a horrible death");
        }
        try {
            Method m = this.getClass().getMethod("do" + opname,
                    new Class[] { PrintWriter.class, HttpServletRequest.class, HttpServletResponse.class });
            m.invoke(this, new Object[] { writer, request, response });
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            writeError(writer, ERROR_BAD_OP, "No such operation");
        } catch (Exception e) {
            e.printStackTrace();
            writeError(writer, ERROR_BUG, "Some bug lives somewhere, hopefully it isn't ours");
        }
        writer.flush();
    }
    
    public void doEditService(PrintWriter out, HttpServletRequest request, HttpServletResponse response) {
        String name = request.getParameter("name");
        try {
            String address = request.getParameter("address");
            Integer port = new Integer(request.getParameter("port"));
            String backlog = request.getParameter("backlog");
            Long life = new Long(request.getParameter("life").trim());
            Long timeout = new Long(request.getParameter("timeout").trim());
            String protocol = request.getParameter("protocol");
            Boolean usesSSL = new Boolean(request.getParameter("usesSSL").trim());
            String sslDomain = usesSSL ? request.getParameter("sslDomain") : null;
            String threadPool = request.getParameter("threadPool");
            admin.readDescriptor(); 
            admin.editService(name, threadPool, protocol, port, address, timeout, life, usesSSL ? sslDomain : null);
            admin.sync();
            admin.writeDescriptor();
        } catch (Exception e) {
            e.printStackTrace();
            out.println("<status><value>-1</value><message>Failed!</message></status>");
            return; 
        }

        out.println("<status><value>0</value><message>" + name + " service successfully edited!</message></status>");
    }

    public void doAddService(PrintWriter out, HttpServletRequest request, HttpServletResponse response) {
        String name = request.getParameter("name");
        try {
            String address = request.getParameter("address");
            Integer port = new Integer(request.getParameter("port"));
            String backlog = request.getParameter("backlog");
            Long life = new Long(request.getParameter("life").trim());
            Long timeout = new Long(request.getParameter("timeout").trim());
            String protocol = request.getParameter("protocol");
            Boolean usesSSL = new Boolean(request.getParameter("usesSSL").trim());
            String sslDomain = usesSSL ? request.getParameter("sslDomain") : null;
            String threadPool = request.getParameter("threadPool");
            admin.readDescriptor();
            admin.createService(name, threadPool, protocol, port, address, timeout, life, usesSSL ? sslDomain : null);
            admin.sync();
            admin.writeDescriptor();
        } catch (Exception e) {
            e.printStackTrace();
            out.println("<status><value>-1</value><message>Failed!</message></status>");
            return;
        }

        out.println("<status><value>0</value><message>" + name + " service successfully added!</message></status>");
    }

    public void doAddProtocol(PrintWriter out, HttpServletRequest request, HttpServletResponse response) {
        String name = request.getParameter("name");
        try {
            String type = request.getParameter("type");
            String serverName = request.getParameter("serverName");
            String domainGroup = request.getParameter("domainGroup");
            String mailboxManager = request.getParameter("mailboxManager");
            String mailbodyManager = request.getParameter("mailbodyManager");
            String listenerChain = request.getParameter("listenerChain");
            String userRepository = request.getParameter("userRepository");
            String sAPOPEnabled = request.getParameter("APOPEnabled");
            String APOPUserRepository = request.getParameter("APOPUserRepository");
            String sslSecurityDomain = request.getParameter("sslSecurityDomain");
            String sauthRequired = request.getParameter("authRequired");
            String sauthAllowed = request.getParameter("authAllowed");
            String sverifyIdentity = request.getParameter("verifyIdentity");
            String smaxMessageSize = request.getParameter("maxMessageSize");
            String sblockSize = request.getParameter("maxMessageSize");
            String senableTls = request.getParameter("enableTls");
            String srequireTls = request.getParameter("requireTls");
            String srequireTlsForAuth = request.getParameter("requireTlsForAuth");
            String srequireClientCert = request.getParameter("requireClientCert");
            String postMasterAddress = request.getParameter("postMasterAddress");
            String smaxReceivedHeaders = request.getParameter("maxReceivedHeaders");
            String sreceivedHeadersThreshold = request.getParameter("receivedHeadersThreshold");
            String smaxOwnReceivedHeaders = request.getParameter("maxOwnReceivedHeaders");
            Boolean APOPEnabled = nullOrEmpty(sAPOPEnabled) ? null : Boolean.parseBoolean(sAPOPEnabled);
            Boolean authRequired = nullOrEmpty(sauthRequired) ? null : Boolean.parseBoolean(sauthRequired);
            Boolean authAllowed = nullOrEmpty(sauthAllowed) ? null : Boolean.parseBoolean(sauthAllowed);
            Boolean verifyIdentity = nullOrEmpty(sverifyIdentity) ? null : Boolean.parseBoolean(sverifyIdentity);
            Long maxMessageSize = nullOrEmpty(smaxMessageSize) ? null : Long.parseLong(smaxMessageSize);
            Integer blockSize = nullOrEmpty(sblockSize) ? null : Integer.parseInt(sblockSize);
            Boolean enableTls = nullOrEmpty(senableTls) ? null : Boolean.parseBoolean(senableTls);
            Boolean requireTls = nullOrEmpty(srequireTls) ? null : Boolean.parseBoolean(srequireTls);
            Boolean requireTlsForAuth = nullOrEmpty(srequireTlsForAuth) ? null : Boolean
                    .parseBoolean(srequireTlsForAuth);
            Boolean requireClientCert = nullOrEmpty(srequireClientCert) ? null : Boolean
                    .parseBoolean(srequireClientCert);
            Integer maxReceivedHeaders = nullOrEmpty(smaxReceivedHeaders) ? null : Integer
                    .parseInt(smaxReceivedHeaders);
            Integer receivedHeadersThreshold = nullOrEmpty(sreceivedHeadersThreshold) ? null : Integer
                    .parseInt(sreceivedHeadersThreshold);
            Integer maxOwnReceivedHeaders = nullOrEmpty(smaxOwnReceivedHeaders) ? null : Integer
                    .parseInt(smaxOwnReceivedHeaders);

            if (type.equals("POP")) {
                admin.readDescriptor();
                admin.createPOPProtocol(null, name, serverName, mailboxManager, userRepository,
                        (APOPEnabled ? APOPUserRepository : null), enableTls, requireTls, requireClientCert, (enableTls
                                .booleanValue() ? sslSecurityDomain : null));
            } else if (type.equals("IMAP")) {
                admin.readDescriptor();
                admin.createIMAPProtocol(null, name, serverName, mailboxManager, userRepository, enableTls, requireTls,
                        requireClientCert, (enableTls.booleanValue() ? sslSecurityDomain : null));

            } else if (type.equals("SMTP")) {
                admin.readDescriptor();
                admin.createSMTPProtocol(null, name, (!nullOrEmpty(domainGroup) ? new ObjectName(domainGroup) : null),
                        (!nullOrEmpty(mailbodyManager) ? new ObjectName(mailbodyManager) : null),
                        (!nullOrEmpty(listenerChain) ? new ObjectName(listenerChain) : null),
                        (!nullOrEmpty(userRepository) ? new ObjectName(userRepository) : null), enableTls
                                .booleanValue() ? sslSecurityDomain : null, serverName, authRequired, authAllowed,
                        verifyIdentity, maxMessageSize, blockSize, enableTls, requireTls, requireTlsForAuth,
                        requireClientCert, postMasterAddress, maxReceivedHeaders, receivedHeadersThreshold,
                        maxOwnReceivedHeaders);

            } else {
                throw new RuntimeException("invalid protocol type " + type);
            }

            admin.sync();
            admin.writeDescriptor();
        } catch (Exception e) {
            e.printStackTrace();
            out.println("<status><value>-1</value><message>Failed!</message></status>");
            return;
        }

        out.println("<status><value>0</value><message>" + name + " protocol successfully added!</message></status>");
    }

    private boolean nullOrEmpty(String val) {

        return val == null || val.trim().equals("");
    }

    public void doGetServices(PrintWriter out, HttpServletRequest request, HttpServletResponse response) {
        Set<ObjectName> services = admin.getServices();

        for (ObjectName objectName : services) {
            ServerMBean s = MMJMXUtil.getMBean(objectName, ServerMBean.class);
            String address = s.getAddress();
            String port = "" + s.getPort();
            String backlog = "" + s.getBacklog();
            String life = "" + s.getLife();
            String timeout = "" + s.getTimeout();
            String protocol = s.getProtocol().toString();
            boolean usesSSL = s.isUsesSSL();
            String sslDomain = s.getSslDomain();

            admin.readDescriptor();

            String threadPool = (String) admin.getAttributeFromConfig(objectName.toString(), "ThreadPool");

            out.println("<service><name>" + objectName.toString() + "</name>" + "<address>" + address + "</address>"
                    + "<port>" + port + "</port>" + "<backlog>" + backlog + "</backlog>" + "<life>" + life + "</life>"
                    + "<timeout>" + timeout + "</timeout>" + "<protocol>" + protocol + "</protocol>" + "<usesSSL>"
                    + usesSSL + "</usesSSL>" + "<sslDomain>" + sslDomain + "</sslDomain>" + "<threadPool>" + threadPool
                    + "</threadPool>" + "</service>");
        }
        System.err.println("exit GetServices");
        out.flush();
    }

    public void doGetPools(PrintWriter out, HttpServletRequest request, HttpServletResponse response) {
        Set<ObjectName> services = admin.getThreadPools();
        Iterator<ObjectName> i = services.iterator();
        while (i.hasNext()) {
            ObjectName serviceName = i.next();
            out.println("<pool>" + serviceName.toString() + "</pool>");
        }
    }

    public void doGetProtocols(PrintWriter out, HttpServletRequest request, HttpServletResponse response) {
        System.out.println("in get protocols");
        Set<ObjectName> services = admin.getProtocols();
        Iterator<ObjectName> i = services.iterator();
        while (i.hasNext()) {
            ObjectName serviceName = i.next();
            ProtocolSettings p = MMJMXUtil.getMBean(serviceName, ProtocolSettings.class);
            Class clazz = p.getProtocolInterfaceClass();
            String popSpecificParts = "";
            String smtpSpecificParts = "";
            String imapSpecificParts = "";
            if (clazz == POP3ProtocolMBean.class) {
                POP3ProtocolMBean p3pm = MMJMXUtil.getMBean(serviceName, POP3ProtocolMBean.class);
                popSpecificParts += field("type", "POP");
                popSpecificParts += "<mailboxManager>" + p3pm.getMailboxManager().getName() + "</mailboxManager>";

                UserRepository ur = p3pm.getAPOPUserRepository();
                popSpecificParts += "<apopUserRepository>" + (ur != null ? ur.getName() : "") + "</apopUserRepository>";
            } else if (clazz == SMTPProtocolMBean.class) {
                SMTPProtocolMBean spm = MMJMXUtil.getMBean(serviceName, SMTPProtocolMBean.class);
                DomainGroupMBean relayDomainGroup = spm.getRelayDomainGroup();
                smtpSpecificParts += field("type", "SMTP");
                smtpSpecificParts += "<domainGroup>" + spm.getDomainGroup().getName() + "</domainGroup>";
                smtpSpecificParts += "<listenerChain>" + spm.getListenerChain().getName() + "</listenerChain>";
                smtpSpecificParts += "<mailBodyManager>" + spm.getMailBodyManager().getServiceName()
                        + "</mailBodyManager>";
                smtpSpecificParts += "<authMethods>" + spm.getAuthMethods() + "</authMethods>";
                smtpSpecificParts += "<authRequired>" + spm.isAuthRequired() + "</authRequired>";
                smtpSpecificParts += "<authAllowed>" + spm.isAuthAllowed() + "</authAllowed>";
                smtpSpecificParts += "<blockSize>" + spm.getBlockSize() + "</blockSize>";
                smtpSpecificParts += "<maxMessageSize>" + spm.getMaxMessageSize() + "</maxMessageSize>";
                smtpSpecificParts += "<maxOwnReceivedHdrs>" + spm.getMaxOwnReceivedHdrs() + "</maxOwnReceivedHdrs>";
                smtpSpecificParts += "<maxReceivedHdrs>" + spm.getMaxReceivedHdrs() + "</maxReceivedHdrs>";
                smtpSpecificParts += "<postmaster>" + spm.getPostmaster() + "</postmaster>";
                smtpSpecificParts += "<receivedHdrsThreshold>" + spm.getReceivedHdrsThreshold()
                        + "</receivedHdrsThreshold>";
                smtpSpecificParts += "<requireTlsForAuth>" + spm.isRequireTlsForAuth() + "</requireTlsForAuth>";
                smtpSpecificParts += "<verifyIdentity>" + spm.isVerifyIdentity() + "</verifyIdentity>";
                smtpSpecificParts += "<relayByAddr>" + spm.isRelayByAddr() + "</relayByAddr>";
                smtpSpecificParts += "<relayByDomain>" + spm.isRelayByDomain() + "</relayByDomain>";
                smtpSpecificParts += relayDomainGroup == null ? null : "<relayDomainGroup>"
                        + relayDomainGroup.getName() + "</relayDomainGroup>";
            } else if (clazz == IMAP4ProtocolMBean.class) {
                IMAP4ProtocolMBean i4m = MMJMXUtil.getMBean(serviceName, IMAP4ProtocolMBean.class);
                imapSpecificParts += field("type", "IMAP");
                imapSpecificParts += "<mailboxManager>" + i4m.getMailboxManager().getName() + "</mailboxManager>";

            }
            out.println("<protocol><name>" + serviceName.toString() + "</name>" + "<sslSecurityDomain>"
                    + p.getSslDomain() + "</sslSecurityDomain>" + popSpecificParts + smtpSpecificParts
                    + imapSpecificParts + "<requireClientCert>" + p.isRequireClientCert() + "</requireClientCert>"
                    + "<requireTls>" + p.isRequireTls() + "</requireTls>" + "<tlsEnabled>" + p.isTlsEnabled()
                    + "</tlsEnabled>" + "<userRepository>" + p.getUserRepository().getName() + "</userRepository>"
                    + "<serverName>" + p.getServername() + "</serverName>" + "</protocol>");
        }
    }

    public void doGetMailboxManagers(PrintWriter out, HttpServletRequest request, HttpServletResponse response) {
        Set<ObjectName> ons = admin.getMailboxManagers();
        Iterator<ObjectName> i = ons.iterator();
        while (i.hasNext()) {
            ObjectName on = i.next();
            out.println(field("mailboxManager", field("name", on.toString())));
        }
    }

    public void doGetMailbodyManagers(PrintWriter out, HttpServletRequest request, HttpServletResponse response) {
        Set<ObjectName> ons = admin.getMailBodyManagers();
        Iterator<ObjectName> i = ons.iterator();
        while (i.hasNext()) {
            ObjectName on = i.next();
            out.println(field("mailbodyManager", field("name", on.toString())));
        }
    }

    public void doGetUserRepositories(PrintWriter out, HttpServletRequest request, HttpServletResponse response) {
        Set<ObjectName> ons = admin.getUserRepositories();
        Iterator<ObjectName> i = ons.iterator();
        while (i.hasNext()) {
            ObjectName on = i.next();
            UserRepository ur = MMJMXUtil.getMBean(on, UserRepository.class);
            out.println(field("userRepository", field("name", on.toString()) + field("type", ur.getType())));
        }
    }

    public void doGetSSLDomains(PrintWriter out, HttpServletRequest request, HttpServletResponse response) {
        Set<ObjectName> ons = admin.getSSLs();
        Iterator<ObjectName> i = ons.iterator();
        while (i.hasNext()) {
            ObjectName on = i.next();
            JaasSecurityDomainMBean jsd = MMJMXUtil.getMBean(on, JaasSecurityDomainMBean.class);
            String name = on.toString();
            String url = jsd.getKeyStoreURL();
            String ksname = jsd.getName();
            ksname = (ksname != null && ksname.indexOf("(") > 0) ? ksname.split("\\(")[1] : ksname;
            ksname = (ksname != null && ksname.indexOf(")") > 0) ? ksname.split("\\)")[0] : ksname;
            out.println(field("sslDomain", field("name", name) + field("keystoreName", ksname) + field("url", url)));
        }
    }

    public void doGetThreadPools(PrintWriter out, HttpServletRequest request, HttpServletResponse response) {
        Set<ObjectName> ons = admin.getThreadPools();
        Iterator<ObjectName> i = ons.iterator();
        while (i.hasNext()) {
            ObjectName on = i.next();
            ThreadPoolMBean tp = MMJMXUtil.getMBean(on, ThreadPoolMBean.class);
            // String name = tp.getName();
            String initial = "" + tp.getInitial();
            String max = "" + tp.getMax();
            String min = "" + tp.getMin();
            String idleKeepAlive = "" + tp.getIdleKeepAlive();
            String poolSize = "" + tp.getPoolSize();
            String activePoolSize = "" + tp.getActivePoolSize();
            out.println("<threadPool>" + field("name", on) + field("initial", initial) + field("max", max)
                    + field("min", min) + field("idleKeepAlive", idleKeepAlive) + field("poolSize", poolSize)
                    + field("activePoolSize", activePoolSize) + "</threadPool>");
        }
    }

    private String field(String name, Object data) {
        return "<" + name + ">" + data.toString() + "</" + name + ">";
    }

    private DomainGroupMBean getLocalDomainGroup() {
        Set<ObjectName> ons = admin.getDomainGroups();
        Iterator<ObjectName> i = ons.iterator();
        DomainGroupMBean local = null;
        System.out.println("ons length " + ons.size());
        while (local == null && i.hasNext()) {
            ObjectName on = i.next();
            DomainGroupMBean d = MMJMXUtil.getMBean(on, DomainGroupMBean.class);
            local = d.getIncludesLocalInterfaces() ? d : null;
        }
        return local;
    }

    public void doGetDomainGroups(PrintWriter out, HttpServletRequest request, HttpServletResponse response) {
        Set<ObjectName> ons = admin.getDomainGroups();
        Iterator<ObjectName> i = ons.iterator();
        System.out.println("ons length " + ons.size());
        while (i.hasNext()) {
            ObjectName on = i.next();
            DomainGroupMBean d = MMJMXUtil.getMBean(on, DomainGroupMBean.class);
            boolean local = d.getIncludesLocalInterfaces();
            String postmaster = d.getPostmaster();
            String[] domains = d.listDomains();
            out.print("<domainGroup>" + field("postmaster", postmaster) + field("local", local) + "<domains>");
            for (int k = 0; k < domains.length; k++) {
                field("domain", domains[k]);
            }
            out.println("</domainGroup>");
        }
    }

    public void doGetLocalDomainGroups(PrintWriter out, HttpServletRequest request, HttpServletResponse response) {
        Set<ObjectName> ons = admin.getDomainGroups();
        Iterator<ObjectName> i = ons.iterator();
        System.out.println("ons length " + ons.size());
        while (i.hasNext()) {
            ObjectName on = i.next();
            DomainGroupMBean d = MMJMXUtil.getMBean(on, DomainGroupMBean.class);
            boolean local = d.getIncludesLocalInterfaces();
            String postmaster = d.getPostmaster();
            String[] domains = d.listDomains();
            if (local == true) {
                out.print("<localOnly>true</localOnly>");
                out
                        .print("<domainGroup>" + field("name", on) + field("postmaster", postmaster)
                                + field("local", local));
                for (int k = 0; k < domains.length; k++) {
                    out.print(field("domain", domains[k]));
                }
                out.println("</domainGroup>");
            }
        }
    }

    public void doGetMailListenerChains(PrintWriter out, HttpServletRequest request, HttpServletResponse response) {
        Set<ObjectName> ons = admin.getListenerChains();
        Iterator<ObjectName> i = ons.iterator();
        while (i.hasNext()) {
            ObjectName on = i.next();
            out.print(field("mailListenerChain", field("name", on.toString())));
        }

    }

    public void doGetLocalDomains(PrintWriter out, HttpServletRequest request, HttpServletResponse response) {
        DomainGroupMBean local = getLocalDomainGroup();
        if (local == null) {
            admin.readDescriptor();
            List<String> domains = new ArrayList<String>(1);
            domains.add("localhost");
            admin.createDomainGroup("LocalDomain", "postmaster@localhost", domains, true);
            admin.sync();
            admin.writeDescriptor();
            // TODO return it.
        }
        String[] domains = local.listDomains();
        String postmaster = local.getPostmaster();
        String domainString = "";
        for (int x = 0; x < domains.length; x++) {
            domainString += field("domain", domains[x]);
        }
        out.println(field("postmaster", postmaster) + domainString);
    }

    public void doSetLocalDomains(PrintWriter out, HttpServletRequest request, HttpServletResponse response) {
        try {
            String dparm = request.getParameter("domain");
            List<String> domainsin = list(dparm);
            admin.readDescriptor();
            admin.editDomainGroup("DomainGroup,group=Local", null, domainsin, true); // TODO
                                                                                        // this
                                                                                        // is
                                                                                        // really
                                                                                        // brittle
                                                                                        // since
                                                                                        // we
                                                                                        // don't
            // look at the name when finding them.
            admin.sync();
            admin.writeDescriptor();
        } catch (Exception e) {
            e.printStackTrace();
            out.println("<status><value>-1</value><message>Failed!</message></status>");
            return;
        }
        out.println("<status><value>0</value><message>Virtual Hosts successfully edited!</message></status>");
    }

    public void doGetUser(PrintWriter out, HttpServletRequest request, HttpServletResponse response) {
        out.println("<user>" + "<contactName>Administrator" + "</contactName>" + "<email>" + "admin@localhost"
                + "</email></user>");
    }

    public void doGetUsers(PrintWriter out, HttpServletRequest request, HttpServletResponse response) {
        String pattern = request.getParameter("pattern");
        pattern = (pattern == null || pattern.trim().equals("")) ? "%" : pattern;
        List<String> users = admin.getUsers(pattern);
        for (int i = 0; i < users.size(); i++) {
            List<String> roles = admin.getRoles(users.get(i));
            List<String> aliases = mbs.getAliases(users.get(i));
            String uroles = commaDel(roles);
            String ualiases = commaDel(aliases);
            UserProfile profile = this.ups.findProfile(users.get(i));
            String defaultAlias = profile != null && profile.getDefaultAlias() != null ? profile.getDefaultAlias() : "";
            out.println("<user><num>" + i + "</num><name>" + users.get(i) + "</name><roles>" + uroles + "</roles>"
                    + "<aliases>" + ualiases + "</aliases><defaultAlias>"+defaultAlias+"</defaultAlias></user>");
        }
    }

    public void doEditProtocol(PrintWriter out, HttpServletRequest request, HttpServletResponse response) {
        String name = request.getParameter("name");

        System.err.println("doEditProtocol " + name);
        try {
            String type = request.getParameter("type");
            String serverName = request.getParameter("serverName");
            String domainGroup = request.getParameter("domainGroup");
            String mailboxManager = request.getParameter("mailboxManager");
            String mailbodyManager = request.getParameter("mailbodyManager");
            String listenerChain = request.getParameter("listenerChain");
            String userRepository = request.getParameter("userRepository");
            String sAPOPEnabled = request.getParameter("APOPEnabled");
            String APOPUserRepository = request.getParameter("APOPUserRepository");
            String sslSecurityDomain = request.getParameter("sslSecurityDomain");
            String sauthRequired = request.getParameter("authRequired");
            String sauthAllowed = request.getParameter("authAllowed");
            String sverifyIdentity = request.getParameter("verifyIdentity");
            String smaxMessageSize = request.getParameter("maxMessageSize");
            String sblockSize = request.getParameter("maxMessageSize");
            String senableTls = request.getParameter("enableTls");
            String srequireTls = request.getParameter("requireTls");
            String srequireTlsForAuth = request.getParameter("requireTlsForAuth");
            String srequireClientCert = request.getParameter("requireClientCert");
            String postMasterAddress = request.getParameter("postMasterAddress");
            String smaxReceivedHeaders = request.getParameter("maxReceivedHeaders");
            String sreceivedHeadersThreshold = request.getParameter("receivedHeadersThreshold");
            String smaxOwnReceivedHeaders = request.getParameter("maxOwnReceivedHeaders");
            Boolean APOPEnabled = nullOrEmpty(sAPOPEnabled) ? null : Boolean.parseBoolean(sAPOPEnabled);
            Boolean authRequired = nullOrEmpty(sauthRequired) ? null : Boolean.parseBoolean(sauthRequired);
            Boolean authAllowed = nullOrEmpty(sauthAllowed) ? null : Boolean.parseBoolean(sauthAllowed);
            Boolean verifyIdentity = nullOrEmpty(sverifyIdentity) ? null : Boolean.parseBoolean(sverifyIdentity);
            Long maxMessageSize = nullOrEmpty(smaxMessageSize) ? null : Long.parseLong(smaxMessageSize);
            Integer blockSize = nullOrEmpty(sblockSize) ? null : Integer.parseInt(sblockSize);
            Boolean enableTls = nullOrEmpty(senableTls) ? null : Boolean.parseBoolean(senableTls);
            Boolean requireTls = nullOrEmpty(srequireTls) ? null : Boolean.parseBoolean(srequireTls);
            Boolean requireTlsForAuth = nullOrEmpty(srequireTlsForAuth) ? null : Boolean
                    .parseBoolean(srequireTlsForAuth);
            Boolean requireClientCert = nullOrEmpty(srequireClientCert) ? null : Boolean
                    .parseBoolean(srequireClientCert);
            Integer maxReceivedHeaders = nullOrEmpty(smaxReceivedHeaders) ? null : Integer
                    .parseInt(smaxReceivedHeaders);
            Integer receivedHeadersThreshold = nullOrEmpty(sreceivedHeadersThreshold) ? null : Integer
                    .parseInt(sreceivedHeadersThreshold);
            Integer maxOwnReceivedHeaders = nullOrEmpty(smaxOwnReceivedHeaders) ? null : Integer
                    .parseInt(smaxOwnReceivedHeaders);

            if (type.equals("POP")) {
                admin.readDescriptor();
                admin.editPOPProtocol(null, name, serverName, mailboxManager, userRepository,
                        (APOPEnabled ? APOPUserRepository : null), enableTls, requireTls, requireClientCert, (enableTls
                                .booleanValue() ? sslSecurityDomain : null));
            } else if (type.equals("IMAP")) {
                admin.readDescriptor();
                admin.editIMAPProtocol(null, name, serverName, mailboxManager, userRepository, enableTls, requireTls,
                        requireClientCert, (enableTls.booleanValue() ? sslSecurityDomain : null));

            } else if (type.equals("SMTP")) {
                admin.readDescriptor();
                admin.editSMTPProtocol(null, name, (!nullOrEmpty(domainGroup) ? new ObjectName(domainGroup) : null),
                        (!nullOrEmpty(mailbodyManager) ? new ObjectName(mailbodyManager) : null),
                        (!nullOrEmpty(listenerChain) ? new ObjectName(listenerChain) : null),
                        (!nullOrEmpty(userRepository) ? new ObjectName(userRepository) : null), enableTls
                                .booleanValue() ? sslSecurityDomain : null, serverName, authRequired, authAllowed,
                        verifyIdentity, maxMessageSize, blockSize, enableTls, requireTls, requireTlsForAuth,
                        requireClientCert, postMasterAddress, maxReceivedHeaders, receivedHeadersThreshold,
                        maxOwnReceivedHeaders);

            } else {
                throw new RuntimeException("invalid protocol type " + type);
            }

            admin.sync();
            admin.writeDescriptor();
        } catch (Exception e) {
            e.printStackTrace();
            out.println("<status><value>-1</value><message>Failed!</message></status>");
            return;
        }

        out.println("<status><value>0</value><message>" + name + " protocol successfully added!</message></status>");

    }

    public void doEditThreadPool(PrintWriter out, HttpServletRequest request, HttpServletResponse response) {
        String name = request.getParameter("name");

        System.err.println("doEditThreadPool " + name);
        try {
            name = name.split("name=")[1];
            String sinitial = request.getParameter("initial");
            String smin = request.getParameter("min");
            String smax = request.getParameter("max");
            String sidleKeepAlive = request.getParameter("idleKeepAlive");
            Integer initial = sinitial == null ? null : Integer.parseInt(sinitial);
            Integer min = smin == null ? null : Integer.parseInt(smin);
            Integer max = smax == null ? null : Integer.parseInt(smax);
            Integer idleKeepAlive = sidleKeepAlive == null ? null : Integer.parseInt(sidleKeepAlive);
            admin.readDescriptor();
            admin.editThreadPool(name, initial, min, max, idleKeepAlive);
            admin.sync();
            admin.writeDescriptor();
        } catch (Exception e) {
            e.printStackTrace();
            out.println("<status><value>-1</value><message>Failed!</message></status>");
            return;
        }
        out.println("<status><value>0</value><message>" + name + " successfully edited!</message></status>");
    }

    public void doEditUser(PrintWriter out, HttpServletRequest request, HttpServletResponse response) {
        String username = null;
        try {
            username = request.getParameter("username").trim();
            String password = request.getParameter("password").trim();
            String defaultAlias = request.getParameter("defaultAlias");
            String uroles = request.getParameter("roles");
            String ualiases = request.getParameter("aliases");
            defaultAlias = defaultAlias != null ? defaultAlias : "";
            List<String> roles = (uroles == null || uroles.trim().equals("")) ? null : list(uroles);
            List<String> aliases = (ualiases == null || ualiases.trim().equals("")) ? null : list(ualiases);

            List<String> ealiases = mbs.getAliases(username);

            List<String> addAliases = aliases != null ? ArrayUtil.rightHandDisjunction(ealiases, aliases)
                    : new ArrayList<String>();
            // List<String> addAliases = aliases != null ? checkAdds(aliases,
            // ealiases) : new ArrayList<String>();

            // List<String> removeAliases = aliases != null ?
            // checkRemoves(aliases, ealiases) : ealiases;
            List<String> removeAliases = aliases != null ? ArrayUtil.rightHandDisjunction(aliases, ealiases) : ealiases;
            UserProfile profile = ups.findProfile(username);
            Set<String> as = new HashSet<String>();
            as.addAll(aliases);
            profile = profile != null ? profile : ups.createUserProfile(username, defaultAlias, as);
            for (String alias : removeAliases) {
                if (!alias.equals(username)) {
                    mbs.deleteAlias(username, alias);
                    profile.getAliases().remove(alias);
                }
            }

            long id = mbs.getMailboxIdByAlias(username);
            if (id == -1) {
                id = mbs.createMailbox(username).getId();
                addAliases.remove(username);
            }
            if(!profile.getAliases().contains(username)) {
                profile.getAliases().add(username);
            }
            for (String alias : addAliases) {
                if (alias != null && !alias.trim().equals("")) {
                    mbs.createAlias(id, alias);
                    profile.getAliases().add(alias);
                }
            }

            admin.editUser(username, password, roles);
            ups.updateUserProfile(profile);
        } catch (Exception e) {
            e.printStackTrace();
            out.println("<status><value>-1</value><message>Failed!</message></status>");
            return;
        }
        out.println("<status><value>0</value><message>" + username + " successfully edited!</message></status>");
    }

    public void doDeleteUser(PrintWriter out, HttpServletRequest request, HttpServletResponse response) {
        String username = null;
        try {
            username = request.getParameter("username").trim();

            admin.deleteUser(username);
            if (mbs.getMailboxByAlias(username) != null) {
                mbs.deleteMailboxByAlias(username);
            }
            ups.deleteUserProfile(username);

        } catch (Exception e) {
            e.printStackTrace();
            out.println("<status><value>-1</value><message>Failed!</message></status>");
            return;
        }
        out.println("<status><value>0</value><message>" + username + " successfully deleted!</message></status>");
    }

    public void doDeleteThreadPool(PrintWriter out, HttpServletRequest request, HttpServletResponse response) {
        String name = null;
        try {
            admin.readDescriptor();
            name = request.getParameter("name").trim();

            admin.removeThreadPool(name);
            admin.sync();
            admin.writeDescriptor();

        } catch (Exception e) {
            e.printStackTrace();
            out.println("<status><value>-1</value><message>Failed!</message></status>");
            return;
        }
        out.println("<status><value>0</value><message>" + name + " successfully deleted!</message></status>");
    }

    public void doDeleteProtocol(PrintWriter out, HttpServletRequest request, HttpServletResponse response) {
        String name = null;
        try {
            admin.readDescriptor();
            name = request.getParameter("name").trim();

            admin.removeProtocol(name);
            admin.sync();
            admin.writeDescriptor();

        } catch (Exception e) {
            e.printStackTrace();
            out.println("<status><value>-1</value><message>Failed!</message></status>");
            return;
        }
        out.println("<status><value>0</value><message>" + name + " successfully deleted!</message></status>");
    }

    public void doAddThreadPool(PrintWriter out, HttpServletRequest request, HttpServletResponse response) {
        String prettyName = request.getParameter("name");
        System.err.println("addThreadPool with " + prettyName);
        try {
            admin.readDescriptor();
            int initial = Integer.parseInt(request.getParameter("initial"));
            int min = Integer.parseInt(request.getParameter("min"));
            int max = Integer.parseInt(request.getParameter("max"));
            int idleKeepAlive = Integer.parseInt(request.getParameter("idleKeepAlive"));
            admin.createThreadPool(null, prettyName, initial, min, max, idleKeepAlive);
            admin.sync();
            admin.writeDescriptor();
        } catch (Exception e) {
            e.printStackTrace();
            out.println("<status><value>-1</value><message>Failed!</message></status>");
            return;
        }
        out.println("<status><value>0</value><message>" + prettyName
                + "ThreadPool successfully added!</message></status>");

    }

    public void doAddUser(PrintWriter out, HttpServletRequest request, HttpServletResponse response) {
        String username = null;
        try {
            username = request.getParameter("username");
            String password = request.getParameter("password");
            String uroles = request.getParameter("roles");
            String ualiases = request.getParameter("aliases");
            String defaultAlias = request.getParameter("defaultAlias");
            defaultAlias = defaultAlias != null ? defaultAlias : "";
            List<String> roles = (uroles == null || uroles.trim().equals("")) ? null : list(uroles);
            List<String> aliases = (ualiases == null || ualiases.trim().equals("")) ? null : list(ualiases);

            admin.createUser(username, password, roles);
            long id = mbs.createMailbox(username).getId();
            if (aliases != null) {
                for (int i = 0; i < aliases.size(); i++) {
                    mbs.createAlias(id, aliases.get(i));
                }
            }
            Set<String> aliasSet = new HashSet<String>();
            aliasSet.addAll(aliases); 
            aliasSet.add(username);
            UserProfile up = this.ups.createUserProfile(username, defaultAlias, aliasSet);
            up.getPreferences().put(PreferenceConstants.PUBLIC_FREEBUSY, Boolean.TRUE.toString());
            up.getPreferences().put(PreferenceConstants.CALENDAR_NOTIFICATION, ""+PreferenceConstants.CALENDAR_NOTIFICATION_EMAIL);
            this.ups.updateUserProfile(up);
            
        } catch (Exception e) {
            e.printStackTrace();
            out.println("<status><value>-1</value><message>Failed!</message></status>");
            return;
        }
        out.println("<status><value>0</value><message>" + username + " successfully added!</message></status>");
    }

    
    public void doGetMailLists(PrintWriter out, HttpServletRequest request, HttpServletResponse response) {
        String pattern = request.getParameter("pattern");
        pattern = (pattern == null || pattern.trim().equals("")) ? "%" : pattern;
        try {
            List<String> lists = mlm.searchLists(pattern);
            for (String address : lists) {
                MailList list = mlm.findList(address);
                MailListProperties props = list.getProperties();
                out.println(field("mailList",field("listAddress",address)+
                                             field(MailListPropertyConstants.ATTACHMENT_ALLOWED,props.getPropertyBool(MailListPropertyConstants.ATTACHMENT_ALLOWED).toString())+
                                             field(MailListPropertyConstants.MEMBERS_ONLY,props.getPropertyBool(MailListPropertyConstants.MEMBERS_ONLY).toString())+
                                             field(MailListPropertyConstants.PREFIX_AUTO_BRACKETED, props.getPropertyBool(MailListPropertyConstants.PREFIX_AUTO_BRACKETED).toString())+
                                             field(MailListPropertyConstants.REPLY_TO_LIST, props.getPropertyBool(MailListPropertyConstants.REPLY_TO_LIST).toString())
                                             
                        ));
            }
        } catch (Exception e) {
            e.printStackTrace();
            out.println("<status><value>-1</value><message>Failed!</message></status>");
            return;
        }
    }
    
    public void doGetMailListMembers(PrintWriter out, HttpServletRequest request, HttpServletResponse response) {
        String listAddress = request.getParameter("listAddress");
        String pattern = request.getParameter("pattern");
        pattern = (pattern == null || pattern.trim().equals("")) ? "%" : pattern;
        try {
            List<String> addresses = mlm.searchMembers(listAddress,pattern);
            for (String address : addresses) {
                out.println(field("member",field("address",address)));
            }
        } catch (Exception e) {
            e.printStackTrace();
            out.println("<status><value>-1</value><message>Failed!</message></status>");
            return;
        }
    }
    
    public void doAddMailListMember(PrintWriter out, HttpServletRequest request, HttpServletResponse response) {
        String listAddress = request.getParameter("listAddress");
        String address = request.getParameter("address");
        try {
            boolean state = mlm.addMember(listAddress, address);
            if (state) {
                out.println("<status><value>o</value><message>Member added</message></status>");
            } else {
                out.println("<status><value>-1</value><message>Failed!</message></status>");
            }
        } catch (Exception e) {
            e.printStackTrace();
            out.println("<status><value>-1</value><message>Failed!</message></status>");
            return;
        }
    }
    
    public void doDeleteMailListMember(PrintWriter out, HttpServletRequest request, HttpServletResponse response) {
        String listAddress = request.getParameter("listAddress");
        String address = request.getParameter("address");
        try {
            boolean state = mlm.removeMember(listAddress, address);
            if (state) {
                out.println("<status><value>o</value><message>Member removed</message></status>");
            } else {
                out.println("<status><value>-1</value><message>Failed!</message></status>");
            }
        } catch (Exception e) {
            e.printStackTrace();
            out.println("<status><value>-1</value><message>Failed!</message></status>");
            return;
        }
    }
    
    public void doDeleteMailList(PrintWriter out, HttpServletRequest request, HttpServletResponse response) {
        String listAddress = request.getParameter("listAddress");
        try {
            mlm.deleteList(listAddress);
            out.println("<status><value>0</value><message>Mail List removed</message></status>");

        } catch (Exception e) {
            e.printStackTrace();
            out.println("<status><value>-1</value><message>Failed!</message></status>");
            return;
        }        
    }
    
    public void doEditMailList(PrintWriter out, HttpServletRequest request, HttpServletResponse response) {
        String listAddress = request.getParameter("listAddress");
        String sAttachmentsAllowed = request.getParameter("attachmentsAllowed");
        String sMembersOnly = request.getParameter("membersOnly");
        String sPrefixAutoBracketed = request.getParameter("prefixAutoBracketed");
        String sReplyToList = request.getParameter("replyToList");
        Boolean attachmentsAllowed = Boolean.valueOf(sAttachmentsAllowed);
        Boolean membersOnly = Boolean.valueOf(sMembersOnly);
        Boolean prefixAutoBracketed = Boolean.valueOf(sPrefixAutoBracketed);
        Boolean replyToList = Boolean.valueOf(sReplyToList);
        try {

            MailList list = mlm.findList(listAddress);
            if (list != null) {
                MailListProperties props = list.getProperties();
                props.setProperty(MailListPropertyConstants.ATTACHMENT_ALLOWED, attachmentsAllowed);
                props.setProperty(MailListPropertyConstants.MEMBERS_ONLY, membersOnly);
                props.setProperty(MailListPropertyConstants.PREFIX_AUTO_BRACKETED, prefixAutoBracketed);
                props.setProperty(MailListPropertyConstants.REPLY_TO_LIST, replyToList);
                list.setProperties(props);
                mlm.editList(list);
            } else {
                out.println("<status><value>-1</value><message>Failed!</message></status>");
            }
        } catch (Exception e) {
            e.printStackTrace();
            out.println("<status><value>-1</value><message>Failed!</message></status>");
            return;
        }
    }
    
    public void doCreateMailList(PrintWriter out, HttpServletRequest request, HttpServletResponse response) {
        String listAddress = request.getParameter("listAddress");
        String sAttachmentsAllowed = request.getParameter("attachmentsAllowed");
        String sMembersOnly = request.getParameter("membersOnly");
        String sPrefixAutoBracketed = request.getParameter("prefixAutoBracketed");
        String sReplyToList = request.getParameter("replyToList");
        Boolean attachmentsAllowed = Boolean.valueOf(sAttachmentsAllowed);
        Boolean membersOnly = Boolean.valueOf(sMembersOnly);
        Boolean prefixAutoBracketed = Boolean.valueOf(sPrefixAutoBracketed);
        Boolean replyToList = Boolean.valueOf(sReplyToList);
        try {
            
            boolean state = mlm.findList(listAddress) == null;
            if (state) {
                state = mlm.createList(listAddress);
                MailList list = mlm.findList(listAddress);
                MailListProperties props = list.getProperties();
                props.setProperty(MailListPropertyConstants.ATTACHMENT_ALLOWED, attachmentsAllowed);
                props.setProperty(MailListPropertyConstants.MEMBERS_ONLY, membersOnly);
                props.setProperty(MailListPropertyConstants.PREFIX_AUTO_BRACKETED, prefixAutoBracketed);
                props.setProperty(MailListPropertyConstants.REPLY_TO_LIST, replyToList);
                list.setProperties(props);
                mlm.editList(list);
            }
            if (state) {
                out.println("<status><value>o</value><message>mail list created</message></status>");
            } else {
                out.println("<status><value>-1</value><message>Failed!</message></status>");
            }
        } catch (Exception e) {
            e.printStackTrace();
            out.println("<status><value>-1</value><message>Failed!</message></status>");
            return;
        }
    }
    
    // wow remember how hard this used to be before java had regexp, collections
    // and generics?
    // now it is one pretty clear line!
    private List<String> list(String uroles) {
        return Arrays.asList(uroles.split(","));
    }

    private String commaDel(List<String> vals) {
        String retval = "";
        for (int i = 0; i < vals.size(); i++) {
            String val = vals.get(i);
            retval += (i != (vals.size() - 1)) ? val + "," : val;
        }
        return retval;
    }


    /**
     * @param writer
     * @param string
     */
    private void writeError(PrintWriter writer, byte code, String string) {
        writer.println("<errorCode>" + code + "</errorCode><message>" + string + "</message>");

    }

    /*
     * private List<String> checkAdds(List<String> delta, List<String>
     * existing) { List<String> result = new ArrayList<String>(); for (String
     * string : delta) { if (!existing.contains(string)) { result.add(string); } }
     * return result; }
     * 
     * 
     * private List<String> checkRemoves(List<String> delta, List<String>
     * existing) { return checkAdds(existing, delta); //removes are just the
     * opposite }
     */

}
