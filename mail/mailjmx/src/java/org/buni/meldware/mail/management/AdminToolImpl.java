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
package org.buni.meldware.mail.management;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.buni.meldware.common.util.ArrayUtil;
import org.buni.meldware.mail.usereditor.UserEditor;
import org.buni.meldware.mail.util.MMJMXUtil;
import org.jboss.aspects.tx.Tx;
import org.jboss.aspects.tx.TxType;
import org.w3c.dom.Element;

/**
 * @author acoliver
 */ 
public class AdminToolImpl implements AdminTool {

	private DumDOM dd;
    private UserEditor editor;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.buni.meldware.mail.management.AdminTool#editSSLDomain(javax.management.ObjectName,
	 *      java.lang.String, java.lang.String, java.lang.String)
	 */
	public void editSSLDomain(ObjectName on, String domainName,
			String keystoreURL, String password) {
		//Element e = dd.findMBeanByName(on.toString());
		if (keystoreURL != null) {
			Element a = dd.getMBeanAttribute("KeyStoreURL");
			dd.setMBeanAttributeValue(a, keystoreURL);
		}
		if (password != null) {
			Element a = dd.getMBeanAttribute("KeyStorePass");
			dd.setMBeanAttributeValue(a, password);
		}

		// TODO change domain
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.buni.meldware.mail.management.AdminTool#installQueue(java.lang.String,
	 *      java.lang.String)
	 */
	public void installQueue(String name, String prettyName) {
		dd.createMBean("jboss.mq.destination:service=Queue,name=" + name,
				"org.jboss.mq.server.jmx.Queue", null,
				new String[] { "DestinationManager" },
				new String[] { "jboss.mq:service=DestinationManager" },
				new String[] { null }, new boolean[] { true });
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.buni.meldware.mail.management.AdminTool#installSSLDomain(java.lang.String,
	 *      javax.management.ObjectName, java.lang.String, java.lang.String,
	 *      java.lang.String)
	 */
	public void installSSLDomain(String prettyName, ObjectName on,
			String domainName, String keystoreURL, String password) {

		dd.createMBean(on.toString(),
				"org.jboss.security.plugins.JaasSecurityDomain", null,
				new String[] { "KeyStoreURL", "KeyStorePass" }, new String[] {
						keystoreURL, password }, new String[] { null, null },
				new boolean[] { false, false });
		// TODO constructor
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.buni.meldware.mail.management.AdminTool#putFile(java.lang.String,
	 *      byte[])
	 */
	public void putFile(String url, byte[] bytes) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.buni.meldware.mail.management.AdminTool#removeQueue(java.lang.String)
	 */
	public void removeQueue(String name) {
            try{
                ObjectName qn = new ObjectName("jboss.mq.destination:service=Queue,name="+name);
		Element mbean = dd.findMBeanByName(qn.toString());
		dd.selectMBean(mbean);
		dd.deleteMBean();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.buni.meldware.mail.management.AdminTool#getDumDOM()
	 */
	public DumDOM getDumDOM() {
		return this.dd;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.buni.meldware.mail.management.AdminTool#setDumDOM(org.buni.meldware.mail.management.DumDOM)
	 */
	public void setDumDOM(DumDOM dd) {
		this.dd = dd;
	}	
	
	public void removeService(String name) {
		dd.selectMBean(dd.findMBeanByName("meldware.mail:type=Service,name="
				+ name));
		dd.deleteMBean();
	}

	public void createService(String name, String threadPool, String protocol,
			Integer port, String address, Long timeout, Long life, String sslDomain) {
		try {
			ObjectName on = new ObjectName("meldware.mail:type=Service,name="
					+ name);
			ObjectName tp = new ObjectName(threadPool);
			ObjectName p = new ObjectName(protocol);

			List<String[]> attrs = new ArrayList<String[]>();
			List<Object[]> deps = new ArrayList<Object[]>();
			if (threadPool != null) {
				deps.add(new Object[] { AdminTool.ATTR_THREAD_POOL,
						tp.toString(), true });
			}
			if (protocol != null) {
				deps.add(new Object[] { AdminTool.ATTR_PROTOCOL,
						p.toString(), false });
			}

			if (port != null) {
				attrs
						.add(new String[] { AdminTool.ATTR_PORT,
								port.toString() });
			}
			if (address != null) {
				attrs.add(new String[] { AdminTool.ATTR_ADDRESS,
						address });
			}
			if (timeout != null) {
				attrs.add(new String[] { AdminTool.ATTR_TIMEOUT,
						timeout.toString() });
			}
			if (life != null) {
				attrs.add(new String[] { AdminTool.ATTR_LIFE,
						life.toString() });
			}
			if (sslDomain != null) {
				String sslname = "meldware.mail:service=JaasSecurityDomain,name="
					+ sslDomain;
				String ssljndi = "java:/jaas/" + sslDomain;
				deps.add(new Object[] { null, sslname, false });
				attrs.add(new String[] { AdminTool.ATTR_SSL_DOMAIN, ssljndi });
				attrs.add(new String[] { AdminTool.ATTR_USES_SSL, "true" });
			} else {
			//	attrs.add(new String[] { AdminTool.ATTR_USES_SSL, "false" });				
			}

			String[] attributeNames = new String[deps.size() + attrs.size()];
			String[] attributeVals = new String[attributeNames.length];
			String[] proxyNames = new String[attributeNames.length];
			boolean[] depends = new boolean[attributeNames.length];

			for (int i = 0; i < deps.size(); i++) {
				depends[i] = true;
				Object[] obj = deps.get(i);
				attributeNames[i] = (String) obj[0];
				attributeVals[i] = (String) obj[1];
				proxyNames[i] = ((Boolean) obj[2]).booleanValue() ? "attribute"
						: null;
			}
			for (int i = deps.size(); i < attrs.size() + deps.size(); i++) {
				int x = i - deps.size();
				depends[i] = false;
				String[] str = attrs.get(x);
				attributeNames[i] = str[0];
				attributeVals[i] = str[1];
			}
			String xmbeanpath = null;
			String className = "org.buni.meldware.mail.Server";

			dd.createMBean(on.toString(), className, xmbeanpath,
					attributeNames, attributeVals, proxyNames, depends);

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	public void editService(String name, String threadPool, String protocol,
			Integer port, String address, Long timeout, Long life, String sslDomain) {
		try {
			ObjectName on = new ObjectName(name);

			Element e = dd.findMBeanByName(on.toString());
			dd.selectMBean(e);

			if (threadPool != null) {
				ObjectName tp = new ObjectName(threadPool);
				setOrCreateDependency(AdminTool.ATTR_THREAD_POOL, tp.toString()
						.toString(), true);
			}
			
			if (protocol != null) {
				ObjectName p = new ObjectName(protocol);
				setOrCreateDependency(AdminTool.ATTR_PROTOCOL,
						p.toString(), true );
			}

			if (port != null) {
				setOrCreateAttribute(AdminTool.ATTR_PORT,
								port.toString() );
			}
			if (address != null) {
				setOrCreateAttribute( AdminTool.ATTR_ADDRESS,
						address );
			}
			if (timeout != null) {
			    setOrCreateAttribute( AdminTool.ATTR_TIMEOUT,
						timeout.toString() );
			}
			if (life != null) {
				setOrCreateAttribute( AdminTool.ATTR_LIFE,
						life.toString() );
			}
			if (sslDomain != null) {
				String sslname = "meldware.mail:service=JaasSecurityDomain,name="
					+ sslDomain;
				String ssljndi = "java:/jaas/" + sslDomain;
				setOrCreateDependency( null, sslname, false );
				setOrCreateAttribute( AdminTool.ATTR_SSL_DOMAIN, ssljndi );
				setOrCreateAttribute( AdminTool.ATTR_USES_SSL, "true" );
			} else {
				setOrCreateAttribute( AdminTool.ATTR_USES_SSL, "false" );				
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}


	public void createSMTPProtocol(String name, String prettyName,
			ObjectName domainGroup, ObjectName bodyMgr,
			ObjectName listenerChain, ObjectName userRepository,
			String sslSecurityDomain, String serverName, Boolean authRequired,
			Boolean authAllowed, Boolean verifyIdentity, Long maxMessageSize,
			Integer blockSize, Boolean enableTls, Boolean requireTls,
			Boolean requireTlsForAuth, Boolean requireClientCert,
			String postMasterAddress, Integer maxReceivedHeaders,
			Integer receivedHeadersThreshold, Integer maxOwnReceivedHeaders) {
		try {
            ObjectName on = name == null ? new ObjectName("meldware.mail:type=Protocol,name="
					+ prettyName) : new ObjectName(name);
			List<String[]> attrs = new ArrayList<String[]>();
			List<Object[]> deps = new ArrayList<Object[]>();

			if (domainGroup != null) {
				deps.add(new Object[] { AdminTool.ATTR_DOMAIN_GROUP,
						domainGroup.toString(), true });
			}
			if (bodyMgr != null) {
				deps.add(new Object[] { AdminTool.ATTR_BODY_MGR,
						bodyMgr.toString(), true });
			}
			if (listenerChain != null) {
				deps.add(new Object[] { AdminTool.ATTR_LISTENER_CHAIN,
						listenerChain.toString(), true });
			}
			if (userRepository != null) {
				deps.add(new Object[] { AdminTool.ATTR_USER_REPOSITORY,
						userRepository.toString(), true });
			}
			if (sslSecurityDomain != null) {
				String sslname = "meldware.mail:service=JaasSecurityDomain,name="
						+ sslSecurityDomain;
				String ssljndi = "java:/jaas/" + sslSecurityDomain;
				deps.add(new Object[] { null, sslname, false });
				attrs.add(new String[] { AdminTool.ATTR_SSL_DOMAIN, ssljndi });
			}
			if (serverName != null) {
				attrs
						.add(new String[] { AdminTool.ATTR_SERVERNAME,
								serverName });
			}
			if (authRequired != null) {
				attrs.add(new String[] { AdminTool.ATTR_AUTH_REQUIRED,
						authRequired.booleanValue() ? "true" : "false" });
			}
			if (authAllowed != null) {
				attrs.add(new String[] { AdminTool.ATTR_AUTH_ALLOWED,
						authAllowed.booleanValue() ? "true" : "false" });
			}
			attrs.add(new String[] { AdminTool.ATTR_AUTH_METHODS,
					AdminTool.VAL_AUTH_METHODS });
			if (verifyIdentity != null) {
				attrs.add(new String[] { AdminTool.ATTR_VERIFY_IDENT,
						verifyIdentity.booleanValue() ? "true" : "false" });
			}
			if (maxMessageSize != null) {
				attrs.add(new String[] { AdminTool.ATTR_MAX_MSG_SIZE,
						"" + maxMessageSize.longValue() });
			}
			if (blockSize != null) {
				attrs.add(new String[] { AdminTool.ATTR_BLOCK_SIZE,
						"" + blockSize.intValue() });
			}
			if (enableTls != null) {
				attrs.add(new String[] { AdminTool.ATTR_TLS_ENABLED,
						enableTls.booleanValue() ? "true" : "false" });
			}
			if (requireTls != null) {
				attrs.add(new String[] { AdminTool.ATTR_REQUIRE_TLS,
						requireTls.booleanValue() ? "true" : "false" });
			}
			if (requireTlsForAuth != null) {
				attrs.add(new String[] { AdminTool.ATTR_REQUIRE_TLS_FOR_AUTH,
						requireTlsForAuth.booleanValue() ? "true" : "false" });
			}
			if (requireClientCert != null) {
				attrs.add(new String[] { AdminTool.ATTR_REQUIRE_CLIENT_CERT,
						requireClientCert.booleanValue() ? "true" : "false" });
			}
			if (postMasterAddress != null) {
				attrs.add(new String[] { AdminTool.ATTR_POSTMASTER,
						postMasterAddress });
			}
			if (maxReceivedHeaders != null) {
				attrs.add(new String[] { AdminTool.ATTR_MAX_REC_HDRS,
						maxReceivedHeaders.toString() });
			}
			if (receivedHeadersThreshold != null) {
				attrs.add(new String[] { AdminTool.ATTR_MAX_HDR_THRS,
						receivedHeadersThreshold.toString() });
			}
			if (maxOwnReceivedHeaders != null) {
				attrs.add(new String[] { AdminTool.ATTR_MAX_OWN_REC_HDRS,
						maxOwnReceivedHeaders.toString() });
			}
			String[] attributeNames = new String[deps.size() + attrs.size()];
			String[] attributeVals = new String[attributeNames.length];
			String[] proxyNames = new String[attributeNames.length];
			boolean[] depends = new boolean[attributeNames.length];

			for (int i = 0; i < deps.size(); i++) {
				depends[i] = true;
				Object[] obj = deps.get(i);
				attributeNames[i] = (String) obj[0];
				attributeVals[i] = (String) obj[1];
				proxyNames[i] = ((Boolean) obj[2]).booleanValue() ? "attribute"
						: null;
			}
			for (int i = deps.size(); i < attrs.size() + deps.size(); i++) {
				int x = i - deps.size();
				depends[i] = false;
				String[] str = attrs.get(x);
				attributeNames[i] = str[0];
				attributeVals[i] = str[1];
			}
			String xmbeanpath = null;
			String className = "org.buni.meldware.mail.smtp.SMTPProtocol";

			dd.createMBean(on.toString(), className, xmbeanpath,
					attributeNames, attributeVals, proxyNames, depends);

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.buni.meldware.mail.management.AdminTool#editSMTPProtocol(java.lang.String,
	 *      java.lang.String, javax.management.ObjectName,
	 *      javax.management.ObjectName, javax.management.ObjectName,
	 *      javax.management.ObjectName, java.lang.String, java.lang.String,
	 *      java.lang.Boolean, java.lang.Boolean, java.lang.Boolean,
	 *      java.lang.Long, java.lang.Integer, java.lang.Boolean,
	 *      java.lang.Boolean, java.lang.Boolean, java.lang.Boolean,
	 *      java.lang.String, java.lang.Integer, java.lang.Integer,
	 *      java.lang.Integer)
	 */
	public void editSMTPProtocol(String name, String prettyName,
			ObjectName domainGroup, ObjectName bodyMgr,
			ObjectName listenerChain, ObjectName userRepository,
			String sslSecurityDomain, String serverName, Boolean authRequired,
			Boolean authAllowed, Boolean verifyIdentity, Long maxMessageSize,
			Integer blockSize, Boolean enableTls, Boolean requireTls,
			Boolean requireTlsForAuth, Boolean requireClientCert,
			String postMasterAddress, Integer maxReceivedHeaders,
			Integer receivedHeadersThreshold, Integer maxOwnReceivedHeaders) {
		try {

			ObjectName on = name == null ? new ObjectName("meldware.mail:type=Protocol,name="
					+ prettyName) : new ObjectName(name);
			Element e = dd.findMBeanByName(on.toString());
			dd.selectMBean(e);

			if (domainGroup != null) {
				setOrCreateDependency(AdminTool.ATTR_DOMAIN_GROUP, domainGroup
						.toString(), true);
			}
			if (bodyMgr != null) {
				setOrCreateDependency(AdminTool.ATTR_BODY_MGR, bodyMgr
						.toString(), true);
			}
			if (listenerChain != null) {
				setOrCreateDependency(AdminTool.ATTR_LISTENER_CHAIN,
						listenerChain.toString(), true);
			}
			if (userRepository != null) {
				setOrCreateDependency(AdminTool.ATTR_USER_REPOSITORY,
						userRepository.toString(), true);
			}
			if (sslSecurityDomain != null) {
				String sslname = "meldware.mail:service=JaasSecurityDomain,name="
						+ sslSecurityDomain;
				String ssljndi = "java:/jaas/" + sslSecurityDomain;
				setOrCreateDependency(null, sslname, false);
				setOrCreateAttribute(AdminTool.ATTR_SSL_DOMAIN, ssljndi);
			}
			if (serverName != null) {

				setOrCreateAttribute(AdminTool.ATTR_SERVERNAME, serverName);
			}
			if (authRequired != null) {
				setOrCreateAttribute(AdminTool.ATTR_AUTH_REQUIRED, authRequired
						.booleanValue() ? "true" : "false");
			}
			if (authAllowed != null) {
				setOrCreateAttribute(AdminTool.ATTR_AUTH_ALLOWED, authAllowed
						.booleanValue() ? "true" : "false");
			}
			if (verifyIdentity != null) {
				setOrCreateAttribute(AdminTool.ATTR_VERIFY_IDENT,
						verifyIdentity.booleanValue() ? "true" : "false");
			}
			if (maxMessageSize != null) {
				setOrCreateAttribute(AdminTool.ATTR_MAX_MSG_SIZE, ""
						+ maxMessageSize.longValue());
			}
			if (blockSize != null) {
				setOrCreateAttribute(AdminTool.ATTR_BLOCK_SIZE, ""
						+ blockSize.intValue());
			}
			if (enableTls != null) {
				setOrCreateAttribute(AdminTool.ATTR_TLS_ENABLED, enableTls
						.booleanValue() ? "true" : "false");
			}
			if (requireTls != null) {
				setOrCreateAttribute(AdminTool.ATTR_REQUIRE_TLS, requireTls
						.booleanValue() ? "true" : "false");
			}
			if (requireTlsForAuth != null) {
				setOrCreateAttribute(AdminTool.ATTR_REQUIRE_TLS_FOR_AUTH,
						requireTlsForAuth.booleanValue() ? "true" : "false");
			}
			if (requireClientCert != null) {
				setOrCreateAttribute(AdminTool.ATTR_REQUIRE_CLIENT_CERT,
						requireClientCert.booleanValue() ? "true" : "false");
			}
			if (postMasterAddress != null) {
				setOrCreateAttribute(AdminTool.ATTR_POSTMASTER,
						postMasterAddress);
			}
			if (maxReceivedHeaders != null) {
				setOrCreateAttribute(AdminTool.ATTR_MAX_REC_HDRS,
						maxReceivedHeaders.toString());
			}
			if (receivedHeadersThreshold != null) {
				setOrCreateAttribute(AdminTool.ATTR_MAX_HDR_THRS,
						receivedHeadersThreshold.toString());
			}
			if (maxOwnReceivedHeaders != null) {
				setOrCreateAttribute(AdminTool.ATTR_MAX_OWN_REC_HDRS,
						maxOwnReceivedHeaders.toString());
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	/**
	 * @param attr_ssl_domain
	 * @param ssljndi
	 */
	private void setOrCreateAttribute(String name, Object value) {
		Element e = dd.getMBeanAttribute(name);
		if (e != null) {
			dd.setMBeanAttributeValue(e, value);
		} else {
			dd.createAttribute(name, value.toString()); //todo make this take an object and check for element
		}
	}

	private void setOrCreateDependency(String name, String value, boolean proxy) {
		Element e = name == null ? null : dd.getMBeanAttribute(name);
		if (e != null) {
			dd.setMBeanAttributeValue(e, value);
		} else {
			dd.createDependency(name, value, proxy);
		}
	}
	
	public void removeProtocol(String name) {
	//	dd.selectMBean(dd.findMBeanByName("meldware.mail:type=Protocol,name="
	//			+ name));
        dd.selectMBean(dd.findMBeanByName(name));
		dd.deleteMBean();
	}
    
    public Object getAttributeFromConfig(String mbeanName, String attributeName) {
        dd.selectMBean(dd.findMBeanByName(mbeanName));
        return dd.getMBeanAttributeValue(dd.getMBeanAttribute(attributeName));
    }

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.buni.meldware.mail.management.AdminTool#createThreadPool(java.lang.String,
	 *      java.lang.String, java.lang.Integer, java.lang.Integer,
	 *      java.lang.Integer, java.lang.Long)
	 */
	public void createThreadPool(String name, String prettyName,
			Integer initial, Integer min, Integer max, Integer idleKeepAlive) {
		try {
			ObjectName on = name == null ? new ObjectName("meldware.mail:type=ThreadPool,name="
					+ prettyName) : new ObjectName(name);
			List<String[]> attrs = new ArrayList<String[]>();
			List<Object[]> deps = new ArrayList<Object[]>();

			if (initial != null) {
				attrs.add(new String[] { AdminTool.ATTR_INITIAL,
						initial.toString() });
			}
			if (min != null) {
				attrs.add(new String[] { AdminTool.ATTR_MIN, min.toString() });
			}
			if (max != null) {
				attrs.add(new String[] { AdminTool.ATTR_MAX, max.toString() });
			}
			if (idleKeepAlive != null) {
				attrs.add(new String[] { AdminTool.ATTR_IDLE_KEEP_ALIVE,
						idleKeepAlive.toString() });
			}
			String[] attributeNames = new String[deps.size() + attrs.size()];
			String[] attributeVals = new String[attributeNames.length];
			String[] proxyNames = new String[attributeNames.length];
			boolean[] depends = new boolean[attributeNames.length];

			for (int i = 0; i < deps.size(); i++) {
				depends[i] = true;
				Object[] obj = deps.get(i);
				attributeNames[i] = (String) obj[0];
				attributeVals[i] = (String) obj[1];
				proxyNames[i] = ((Boolean) obj[2]).booleanValue() ? "attribute"
						: null;
			}
			for (int i = deps.size(); i < attrs.size() + deps.size(); i++) {
				int x = i - deps.size();
				depends[i] = false;
				String[] str = attrs.get(x);
				attributeNames[i] = str[0];
				attributeVals[i] = str[1];
			}
			String xmbeanpath = null;
			String className = "org.buni.meldware.mail.ThreadPool";
            String interfaceName = "org.buni.meldware.mail.ThreadPoolMBean";

			dd.createMBean(on.toString(), className, interfaceName, xmbeanpath,
					attributeNames, attributeVals, proxyNames, depends);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.buni.meldware.mail.management.AdminTool#editThreadPool(java.lang.String,
	 *      java.lang.Integer, java.lang.Integer, java.lang.Integer,
	 *      java.lang.Long)
	 */
	public void editThreadPool(String name, Integer initial, Integer min,
			Integer max, Integer idleKeepAlive) {
		try {
			ObjectName on = new ObjectName("meldware.mail:type=ThreadPool,name="
					+ name);
			Element e = dd.findMBeanByName(on.toString());
			dd.selectMBean(e);

			if (initial != null) {
				setOrCreateAttribute(AdminTool.ATTR_INITIAL, initial.toString());
			}
			if (min != null) {
				setOrCreateAttribute(AdminTool.ATTR_MIN, min.toString());
			}
			if (max != null) {
				setOrCreateAttribute(AdminTool.ATTR_MAX, max.toString());
			}
			if (idleKeepAlive != null) {
				System.out.println("IDLE KEEP ALIVE WAS NOT NULL!!");
				setOrCreateAttribute(AdminTool.ATTR_IDLE_KEEP_ALIVE,
						idleKeepAlive.toString());
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.buni.meldware.mail.management.AdminTool#removeThreadPool(java.lang.String)
	 */
	public void removeThreadPool(String name) {
            dd.selectMBean(dd.findMBeanByName(
                                               name));
            dd.deleteMBean();

	}
        
        public void createSSL(String domain, String keystoreUrl, String keystorePass) {
            try {
                ObjectName name=new ObjectName("meldware.mail:service=JaasSecurityDomain,name="+domain);
                String className="org.jboss.security.plugins.JaasSecurityDomain";
                String xmbeanpath=null;
                String[] attributeNames=new String[]{"KeyStoreURL","KeyStorePass"};
                String[] attributeVals= new String[] {keystoreUrl, keystorePass};
                String[] proxyNames= null;
                String[] constructorArgTypes= new String[] {"java.lang.String"};
                String[] constructorArgs = new String[] {"Mail+SSL"};
                boolean[] depends = new boolean[]{false,false};
                dd.createMBean(   
                        name.toString(), className, xmbeanpath, attributeNames, attributeVals,
                        proxyNames, constructorArgTypes, constructorArgs, depends); 
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        
        public void removeSSL(String domain) {
            try {
                ObjectName name=new ObjectName("meldware.mail:service=JaasSecurityDomain,name="+domain);
                dd.selectMBean(dd.findMBeanByName(name.toString()));
                dd.deleteMBean();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }            
        }
        
        public Set<ObjectName> getSSLs() {
            String filter = "meldware.mail:service=JaasSecurityDomain";
            return getStuff(filter);
        }
        
        public void createDomainGroup(String name,String postmaster,List<String> domains,Boolean includesLocal) {
            try {
                ObjectName on=new ObjectName("meldware.mail:type=DomainGroup,name="+name);
                String className = "org.buni.meldware.mail.domaingroup.DomainGroup";
                String xmbeanpath = null;
                String[] attributeNames = new String[]{AdminTool.ATTR_POSTMASTER,AdminTool.ATTR_DOMAINS};
                if(postmaster == null) {
                    attributeNames = new String[]{AdminTool.ATTR_DOMAINS};
                }
                Object[] attributeVals = null;
                String ds = "<domains includes-local-interfaces=\"" +
                                         includesLocal.toString()+
                                         "\">";
                for (int i = 0; i < domains.size(); i++) {
                    ds+="<domain>"+domains.get(i)+"</domain>";
                }
                ds += "</domains>";
       
                Element e = (Element)dd.parse(ds);
                if (postmaster == null) {
                    attributeVals = new Object[]{e};
                } else {
                    attributeVals = new Object[]{postmaster,e};
                }
                dd.createMBean(on.toString(), className, xmbeanpath, attributeNames, attributeVals, null, 
                        new boolean[]{false,false});
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        

        public void editDomainGroup(String name,String postmaster,List<String> domains,Boolean includesLocal) {
            try {
                ObjectName on=new ObjectName("meldware.mail:type=DomainGroup,name="+name);
                Element e = dd.findMBeanByName(on.toString());
                dd.selectMBean(e);
                if (postmaster != null) {
                    setOrCreateAttribute(AdminTool.ATTR_POSTMASTER, postmaster.toString());
                }
                if (domains != null) {
                    //Element att = dd.getMBeanAttribute(AdminTool.ATTR_DOMAINS);
                  //  dd.deleteAttribute(att);
                    String ds = "<domains includes-local-interfaces=\"" +
                                             includesLocal.toString()+
                                             "\">";
                    for (int i = 0; i < domains.size(); i++) {
                        ds+="<domain>"+domains.get(i)+"</domain>";
                    }
                    ds += "</domains>";
                    Element a = (Element)dd.parse(ds);
                    setOrCreateAttribute(AdminTool.ATTR_DOMAINS, a);
               }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        
        public void removeDomainGroup(String domain) {
            try {
                ObjectName name=new ObjectName("meldware.mail:type=DomainGroup,name="+domain);
                dd.selectMBean(dd.findMBeanByName(name.toString()));
                dd.deleteMBean();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }                        
        }
        
        public Set<ObjectName> getDomainGroups() {
            String filter="meldware.mail:type=DomainGroup";
            return getStuff(filter);
        }

	/* (non-Javadoc)
	 * @see org.buni.meldware.mail.management.AdminTool#getServices()
	 */
	public Set<ObjectName> getServices() {

	    String filter = "meldware.mail:type=Service";
		
		return getStuff(filter);
	}
	
	public Set<ObjectName> getThreadPools() {
		String filter = "meldware.mail:type=ThreadPool";
		return getStuff(filter);
	}
    
    public Set<ObjectName> getUserRepositories() {
        String filter = "meldware.mail:type=UserRepository";
        return getStuff(filter);
    }
	
	public Set<ObjectName> getProtocols() {
		String filter = "meldware.mail:type=Protocol";
		return getStuff(filter);
	}
    
    public Set<ObjectName> getListenerChains() {
        String filter = "meldware.mail:type=MailListenerChain";
        return getStuff(filter);
    }
    
    public Set<ObjectName> getMailboxManagers() {
        String filter = "meldware.mail:type=MailboxManager";
        return getStuff(filter);
    }
    
    public Set<ObjectName> getMailBodyManagers() {
        String filter = "meldware.mail:type=MailBodyManager"; 
        return getStuff(filter);
    }
	
	private Set<ObjectName> getStuff(String stuffName) {
		MBeanServer server = MMJMXUtil.locateJBoss();
		ObjectName filter;
		try {
			filter = new ObjectName(stuffName+",*");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		Set<ObjectName> mbeans = (Set<ObjectName>)server.queryNames(filter, null);
		return mbeans;		
	}

        public Set<ObjectName> getQueues() {
            String filter= "jboss.mq.destination:service=Queue";
            return getStuff(filter);
        }
        
	/* (non-Javadoc)
	 * @see org.buni.meldware.mail.management.AdminTool#readDescriptor()
	 */
	public void readDescriptor() {
		try {
			dd.read();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/* (non-Javadoc)
	 * @see org.buni.meldware.mail.management.AdminTool#sync()
	 */
	public void sync() {
		try {
			dd.sync();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/* (non-Javadoc)
	 * @see org.buni.meldware.mail.management.AdminTool#writeDescriptor()
	 */
	public void writeDescriptor() {
		try {
			dd.write();
		} catch (Exception e) {
            e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

    public List<String> getUsers(String pattern) {
        @SuppressWarnings("unchecked")
        List<String> users = (List<String>)editor.getUsers(pattern);
        
        return users;
    }

    public UserEditor getUserEditor() {
        return editor;
    }

    public void setUserEditor(UserEditor editor) {
        this.editor = editor;
    }

    public List<String> getRoles(String username) {
        @SuppressWarnings("unchecked")
        List<String>roles = (List<String>)editor.getRoles(username);
        return roles;
    }

    @Tx(TxType.REQUIRED)
    public void createUser(String username, String password, List<String> roles) {
        this.editor.addUser(username, password);
        if (roles != null) {
            for (int i = 0; i < roles.size(); i++) {
                String role = roles.get(i);
                this.editor.addRole(username, role);
            }
        }
    }

    @Tx(TxType.REQUIRED)
    public void editUser(String username, String password, List<String> roles) {
        if(password != null && !password.trim().equals("")) {
            this.editor.changePassword(username, password);
        }
        @SuppressWarnings("unchecked")
        List<String> eroles = (List<String>)editor.getRoles(username);
        List<String> removes = ArrayUtil.rightHandDisjunction(roles, eroles);
        List<String> adds = ArrayUtil.rightHandDisjunction(eroles, roles);
        
        for (String role : removes) {
            this.editor.deleteRole(username, role);
        }
        
        for (String role : adds) {
            this.editor.addRole(username, role);
        }
    }
    
    @Tx(TxType.REQUIRED)
    public void deleteUser(String username) {
        this.editUser(username, null, new ArrayList<String>()); // remove all roles
        this.editor.deleteUser(username);
    }

    public void createPOPProtocol(String name, String prettyName, String serverName, String mailboxManager, String userRepository, String apopUserRepository, Boolean enableTls, Boolean requireTls, Boolean requireClientCert, String sslSecurityDomain) {

        try {
        System.err.println("create pop protocol called ("+name+","+prettyName+","+serverName+","+userRepository+","+apopUserRepository+","+enableTls+","+requireTls+","+requireClientCert+","+sslSecurityDomain);
        ObjectName on = name != null ? new ObjectName(name) : new ObjectName("meldware.mail:type=Protocol,name="+prettyName);
        String className = "org.buni.meldware.mail.pop3.POP3Protocol";

        List<String[]> attrs = new ArrayList<String[]>();
        List<Object[]> deps = new ArrayList<Object[]>();

        if (apopUserRepository != null) {
            deps.add(new Object[] { AdminTool.ATTR_APOP_USER_REPOSITORY,
                    apopUserRepository.toString(), true });
        }
        if (mailboxManager != null) {
            deps.add(new Object[] { AdminTool.ATTR_MAILBOX_MGR,
                    mailboxManager.toString(), true });
        }
        if (userRepository != null) {
            deps.add(new Object[] { AdminTool.ATTR_USER_REPOSITORY,
                    userRepository.toString(), true });
        }
        if (sslSecurityDomain != null) {
            String sslname = "meldware.mail:service=JaasSecurityDomain,name="
                    + sslSecurityDomain;
            String ssljndi = "java:/jaas/" + sslSecurityDomain;
            deps.add(new Object[] { null, sslname, false });
            attrs.add(new String[] { AdminTool.ATTR_SSL_DOMAIN, ssljndi });
        }
        if (serverName != null) {
            attrs
                    .add(new String[] { AdminTool.ATTR_SERVERNAME,
                            serverName });
        }
        if (enableTls != null) {
            attrs.add(new String[] { AdminTool.ATTR_TLS_ENABLED,
                    enableTls.booleanValue() ? "true" : "false" });
        }
        if (requireTls != null) {
            attrs.add(new String[] { AdminTool.ATTR_REQUIRE_TLS,
                    requireTls.booleanValue() ? "true" : "false" });
        }
        if (requireClientCert != null) {
            attrs.add(new String[] { AdminTool.ATTR_REQUIRE_CLIENT_CERT,
                    requireClientCert.booleanValue() ? "true" : "false" });
        }
        String[] attributeNames = new String[deps.size() + attrs.size()];
        String[] attributeVals = new String[attributeNames.length];
        String[] proxyNames = new String[attributeNames.length];
        boolean[] depends = new boolean[attributeNames.length];

        for (int i = 0; i < deps.size(); i++) {
            depends[i] = true;
            Object[] obj = deps.get(i);
            attributeNames[i] = (String) obj[0];
            attributeVals[i] = (String) obj[1];
            proxyNames[i] = ((Boolean) obj[2]).booleanValue() ? "attribute"
                    : null;
        }
        for (int i = deps.size(); i < attrs.size() + deps.size(); i++) {
            int x = i - deps.size();
            depends[i] = false;
            String[] str = attrs.get(x);
            attributeNames[i] = str[0];
            attributeVals[i] = str[1];
        }
        String xmbeanpath = null;

        dd.createMBean(on.toString(), className, xmbeanpath,
                attributeNames, attributeVals, proxyNames, depends);

    } catch (Exception e) {
        e.printStackTrace();
        throw new RuntimeException(e);
    }
        
    /*    dd.createMBean(name, className, xmbeanpath, attributeNames, attributeVals, proxyNames, depends)*/
    }

    public void createIMAPProtocol(String name, String prettyName, String serverName, String mailboxManager, String userRepository, Boolean enableTls, Boolean requireTls, Boolean requireClientCert, String sslSecurityDomain) {
        System.err.println("create imap protocol called ("+name+","+prettyName+","+serverName+","+userRepository+","+enableTls+","+requireTls+","+requireClientCert+","+sslSecurityDomain);
            try {
            ObjectName on = name != null ? new ObjectName(name) : new ObjectName("meldware.mail:type=Protocol,name="+prettyName);
            String className = "org.buni.meldware.mail.imap4.IMAP4Protocol";

            List<String[]> attrs = new ArrayList<String[]>();
            List<Object[]> deps = new ArrayList<Object[]>();

            if (mailboxManager != null) {
                deps.add(new Object[] { AdminTool.ATTR_MAILBOX_MGR,
                        mailboxManager.toString(), true });
            }
            if (userRepository != null) {
                deps.add(new Object[] { AdminTool.ATTR_USER_REPOSITORY,
                        userRepository.toString(), true });
            }
            if (sslSecurityDomain != null) {
                String sslname = "meldware.mail:service=JaasSecurityDomain,name="
                        + sslSecurityDomain;
                String ssljndi = "java:/jaas/" + sslSecurityDomain;
                deps.add(new Object[] { null, sslname, false });
                attrs.add(new String[] { AdminTool.ATTR_SSL_DOMAIN, ssljndi });
            }
            if (serverName != null) {
                attrs
                        .add(new String[] { AdminTool.ATTR_SERVERNAME,
                                serverName });
            }
            if (enableTls != null) {
                attrs.add(new String[] { AdminTool.ATTR_TLS_ENABLED,
                        enableTls.booleanValue() ? "true" : "false" });
            }
            if (requireTls != null) {
                attrs.add(new String[] { AdminTool.ATTR_REQUIRE_TLS,
                        requireTls.booleanValue() ? "true" : "false" });
            }
            if (requireClientCert != null) {
                attrs.add(new String[] { AdminTool.ATTR_REQUIRE_CLIENT_CERT,
                        requireClientCert.booleanValue() ? "true" : "false" });
            }
            String[] attributeNames = new String[deps.size() + attrs.size()];
            String[] attributeVals = new String[attributeNames.length];
            String[] proxyNames = new String[attributeNames.length];
            boolean[] depends = new boolean[attributeNames.length];

            for (int i = 0; i < deps.size(); i++) {
                depends[i] = true;
                Object[] obj = deps.get(i);
                attributeNames[i] = (String) obj[0];
                attributeVals[i] = (String) obj[1];
                proxyNames[i] = ((Boolean) obj[2]).booleanValue() ? "attribute"
                        : null;
            }
            for (int i = deps.size(); i < attrs.size() + deps.size(); i++) {
                int x = i - deps.size();
                depends[i] = false;
                String[] str = attrs.get(x);
                attributeNames[i] = str[0];
                attributeVals[i] = str[1];
            }
            String xmbeanpath = null;

            dd.createMBean(on.toString(), className, xmbeanpath,
                    attributeNames, attributeVals, proxyNames, depends);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        
    }

    public void editIMAPProtocol(Object prettyName, String name, String serverName, String mailboxManager, String userRepository, Boolean enableTls, Boolean requireTls, Boolean requireClientCert, String sslSecurityDomain) {
        try {

            ObjectName on = name == null ? new ObjectName("meldware.mail:type=Protocol,name="
                    + prettyName) : new ObjectName(name);
            Element e = dd.findMBeanByName(on.toString());
            dd.selectMBean(e);

            if (serverName != null) {

                setOrCreateAttribute(AdminTool.ATTR_SERVERNAME, serverName);
            }
            if (mailboxManager != null) {
                setOrCreateDependency(AdminTool.ATTR_MAILBOX_MGR, new ObjectName(mailboxManager)
                        .toString(), true);
            }
            if (userRepository != null) {
                setOrCreateDependency(AdminTool.ATTR_USER_REPOSITORY,
                        userRepository.toString(), true);
            }
            if (enableTls != null) {
                setOrCreateAttribute(AdminTool.ATTR_TLS_ENABLED, enableTls
                        .booleanValue() ? "true" : "false");
            }
            if (requireTls != null) {
                setOrCreateAttribute(AdminTool.ATTR_REQUIRE_TLS, requireTls
                        .booleanValue() ? "true" : "false");
            }
            if (requireClientCert != null) {
                setOrCreateAttribute(AdminTool.ATTR_REQUIRE_CLIENT_CERT,
                        requireClientCert.booleanValue() ? "true" : "false");
            }
            if (sslSecurityDomain != null) {
                String sslname = "meldware.mail:service=JaasSecurityDomain,name="
                        + sslSecurityDomain;
                String ssljndi = "java:/jaas/" + sslSecurityDomain;
                setOrCreateDependency(null, sslname, false);
                setOrCreateAttribute(AdminTool.ATTR_SSL_DOMAIN, ssljndi);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
 
    }

    public void editPOPProtocol(Object prettyName, String name, String serverName, String mailboxManager, String userRepository, String apopUserRepository, Boolean enableTls, Boolean requireTls, Boolean requireClientCert, String sslSecurityDomain) {
        try {

            ObjectName on = name == null ? new ObjectName("meldware.mail:type=Protocol,name="
                    + prettyName) : new ObjectName(name);
            Element e = dd.findMBeanByName(on.toString());
            dd.selectMBean(e);

            if (serverName != null) {

                setOrCreateAttribute(AdminTool.ATTR_SERVERNAME, serverName);
            }
            if (mailboxManager != null) {
                setOrCreateDependency(AdminTool.ATTR_MAILBOX_MGR, new ObjectName(mailboxManager)
                        .toString(), true);
            }
            if (userRepository != null) {
                setOrCreateDependency(AdminTool.ATTR_USER_REPOSITORY,
                        userRepository.toString(), true);
            }
            if (apopUserRepository != null) {
                setOrCreateDependency(AdminTool.ATTR_APOP_USER_REPOSITORY,
                        apopUserRepository.toString(), true);
            }
            if (enableTls != null) {
                setOrCreateAttribute(AdminTool.ATTR_TLS_ENABLED, enableTls
                        .booleanValue() ? "true" : "false");
            }
            if (requireTls != null) {
                setOrCreateAttribute(AdminTool.ATTR_REQUIRE_TLS, requireTls
                        .booleanValue() ? "true" : "false");
            }
            if (requireClientCert != null) {
                setOrCreateAttribute(AdminTool.ATTR_REQUIRE_CLIENT_CERT,
                        requireClientCert.booleanValue() ? "true" : "false");
            }
            if (sslSecurityDomain != null) {
                String sslname = "meldware.mail:service=JaasSecurityDomain,name="
                        + sslSecurityDomain;
                String ssljndi = "java:/jaas/" + sslSecurityDomain;
                setOrCreateDependency(null, sslname, false);
                setOrCreateAttribute(AdminTool.ATTR_SSL_DOMAIN, ssljndi);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        
    }

}
