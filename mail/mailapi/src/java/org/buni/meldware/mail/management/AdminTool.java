/*
 * Bunisoft the Open Source Communications Company
 * Copyright 2006, Bunisoft LLC.,
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
package org.buni.meldware.mail.management;

import java.util.List;
import java.util.Set;

import javax.management.ObjectName;

import org.buni.meldware.mail.usereditor.UserEditor;

/**
 * Service interface to the admin stuff.
 *
 * @author acoliver
 */
public interface AdminTool {
    public static final short PROTOCOL_SMTP = 1;
    public static final short PROTOCOL_POP  = 2;
    public static final short PROTOCOL_IMAP = 3;
    public static final String ATTR_DOMAIN_GROUP = "DomainGroup";
    public static final String ATTR_MAILBOX_MGR = "MailboxManager";
    public static final String ATTR_BODY_MGR = "MailBodyManager";
    public static final String ATTR_LISTENER_CHAIN = "ListenerChain";
    public static final String ATTR_APOP_USER_REPOSITORY = "APOPUserRepository";
    public static final String ATTR_USER_REPOSITORY = "UserRepository";
    public static final String ATTR_SSL_DOMAIN = "SslDomain";
    public static final String ATTR_SERVERNAME = "Servername";
    public static final String ATTR_AUTH_REQUIRED = "AuthRequired";
    public static final String ATTR_AUTH_ALLOWED = "AuthAllowed";
    public static final String ATTR_AUTH_METHODS = "AuthMethods";
    public static final String VAL_AUTH_METHODS = "AUTH LOGIN PLAIN";
    public static final String ATTR_VERIFY_IDENT = "VerifyIdentity";
    public static final String ATTR_MAX_MSG_SIZE = "MaxMessageSize";
    public static final String ATTR_BLOCK_SIZE = "BlockSize";
    public static final String ATTR_TLS_ENABLED = "TlsEnabled";
    public static final String ATTR_REQUIRE_TLS = "RequireTls";
    public static final String ATTR_REQUIRE_TLS_FOR_AUTH = "RequireTlsForAuth";
    public static final String ATTR_REQUIRE_CLIENT_CERT = "RequireClientCert";
    public static final String ATTR_POSTMASTER = "Postmaster";
    public static final String ATTR_MAX_REC_HDRS = "MaxReceivedHdrs";
    public static final String ATTR_MAX_HDR_THRS = "ReceivedHdrsThreshold";
    public static final String ATTR_MAX_OWN_REC_HDRS = "MaxOwnReceivedHdrs";
    public static final String ATTR_INITIAL = "Initial";
    public static final String ATTR_MIN = "Min";
    public static final String ATTR_MAX = "Max";
    public static final String ATTR_IDLE_KEEP_ALIVE = "IdleKeepAlive";
    public static final String ATTR_LIFE = "Life";
    public static final String ATTR_TIMEOUT = "Timeout";
    public static final String ATTR_USES_SSL = "UsesSSL";
    public static final String ATTR_ADDRESS = "Address";
    public static final String ATTR_PORT = "Port";
    public static final String ATTR_PROTOCOL = "Protocol";
    public static final String ATTR_THREAD_POOL = "ThreadPool";

    public static final String ATTR_DOMAINS = "Domains";
    
    void installSSLDomain(String prettyName, ObjectName on, String domainName, String keystoreURL, String password);
    void editSSLDomain(ObjectName on, String domainName, String keystoreURL, String password);
    void putFile(String url,byte[] bytes);
    
    void installQueue(String name, String prettyName);
    void removeQueue(String name);

    public void createService(String name, String threadPool, String protocol,
			Integer port, String address, Long timeout, Long life, String sslDomain);
	
    public void removeService(String name);
    
    void editService(String name, String threadPool, String protocol,
			Integer port, String address, Long timeout, Long life, String sslDomain);
	
    void createSMTPProtocol(String name, 
                            String prettyName,
                            ObjectName domainGroup,
                            ObjectName bodyMgr,
                            ObjectName listenerChain,
                            ObjectName userRepository,
                            String sslSecurityDomain,
                            String serverName,
                            Boolean authRequired,
                            Boolean authAllowed,
                            Boolean verifyIdentity,
                            Long maxMessageSize,
                            Integer blockSize,
                            Boolean enableTls,
                            Boolean requireTls,
                            Boolean requireTlsForAuth,
                            Boolean requireClientCert,
                            String  postMasterAddress,
                            Integer maxReceivedHeaders,
                            Integer receivedHeadersThreshold,
                            Integer maxOwnReceivedHeaders);
  /*  
    void createPOPProtocol(String name, 
            String prettyName,
            ObjectName listenerChain,
            ObjectName mailboxManager,
            ObjectName userRepository,
            ObjectName APOPUserRepository,
            String sslSecurityDomain,
            String serverName,
            Boolean enableTls,
            Boolean requireTls,
            Boolean requireClientCert);
            */
    
    void editSMTPProtocol(String name, 
            String prettyName,
            ObjectName domainGroup,
            ObjectName bodyMgr,
            ObjectName listenerChain,
            ObjectName userRepository,
            String sslSecurityDomain,
            String serverName,
            Boolean authRequired,
            Boolean authAllowed,
            Boolean verifyIdentity,
            Long maxMessageSize,
            Integer blockSize,
            Boolean enableTls,
            Boolean requireTls,
            Boolean requireTlsForAuth,
            Boolean requireClientCert,
            String  postMasterAddress,
            Integer maxReceivedHeaders,
            Integer receivedHeadersThreshold,
            Integer maxOwnReceivedHeaders);
    
    void removeProtocol(String name);
  
    void createThreadPool(
            String name,
            String prettyName,
            Integer initial,
            Integer min,
            Integer max,
            Integer idleKeepAlive
            );
    
    void editThreadPool(
            String name,
            Integer initial,
            Integer min,
            Integer max,
            Integer idleKeepAlive            
            );
    
    void removeThreadPool(
            String name
            );
    
    void createSSL(String domain, String keystoreUrl, String keystorePass);
    void removeSSL(String domain);
    Set<ObjectName> getSSLs();
    
    
    /*
    void createServiceConfiguration(
            String prettyName,
            ObjectName servicename,
            ObjectName protocolname,
            short type,
            ObjectName threadPool,
            Long port,
            String bindAddress,
            Long timeout,
            Long life,
            ObjectName domainGroup,
            ObjectName listenerChain,
            ObjectName userRepository,
            String sslSecurityDomain,
            String serverName,
            Boolean authRequired,
            Boolean authAllowed,
            Boolean verifyIdentity,
            Long maxMessageSize,
            Integer blockSize,
            Boolean enableTls,
            Boolean requireTls,
            Boolean requireTlsForAuth,
            Boolean requireClientCert,
            String  postMasterAddress,
            Integer maxReceivedHeaders,
            Integer receivedHeadersThreshold,
            Integer maxOwnReceivedHeaders,
            Boolean usesSSL
    );
    */
    //void changeServiceConfiguration();
    
    void createDomainGroup(String name,String postmaster,List<String> domains, 
                           Boolean includesLocal);
        
    void editDomainGroup(String name,String postmaster,List<String> domains, 
                           Boolean includesLocal);
    Set<ObjectName> getDomainGroups();
    void removeDomainGroup(String name);
    
    
    void setDumDOM(DumDOM dd);
    DumDOM getDumDOM();
    
    void setUserEditor(UserEditor editor);
    UserEditor getUserEditor();
    
    
	/**
	 * @return
	 */ 
    Set<ObjectName> getServices();
    Set<ObjectName> getThreadPools();
    Set<ObjectName> getProtocols();
    Set<ObjectName> getQueues();
    Set<ObjectName> getUserRepositories();
    Set<ObjectName> getMailBodyManagers(); 
	
    public void readDescriptor();
    public void writeDescriptor();
    public void sync(); 
    List<String> getUsers(String pattern);
    List<String> getRoles(String string);
    void createUser(String username, String password, List<String> roles);
    void editUser(String username, String password, List<String> roles);
    void deleteUser(String username);
    Set<ObjectName> getListenerChains();
    void createPOPProtocol(String name, String prettyName, String serverName, String mailboxManager, String userRepository, String string, Boolean enableTls, Boolean requireTls, Boolean requireClientCert, String sslSecurityDomain);
    void createIMAPProtocol(String name, String prettyName, String serverName, String mailboxManager, String userRepository, Boolean enableTls, Boolean requireTls, Boolean requireClientCert, String sslSecurityDomain);
    Set<ObjectName> getMailboxManagers();
    void editPOPProtocol(Object object, String name, String serverName, String mailboxManager, String userRepository, String string, Boolean enableTls, Boolean requireTls, Boolean requireClientCert, String string2);
    void editIMAPProtocol(Object object, String name, String serverName, String mailboxManager, String userRepository, Boolean enableTls, Boolean requireTls, Boolean requireClientCert, String string);
    Object getAttributeFromConfig(String mbeanName, String attributeName);
}
