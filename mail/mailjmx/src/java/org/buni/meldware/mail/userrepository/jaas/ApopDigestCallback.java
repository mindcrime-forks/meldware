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
package org.buni.meldware.mail.userrepository.jaas;

import java.security.MessageDigest;
import java.util.Map;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.buni.meldware.mail.util.MMJMXUtil;
import org.jboss.crypto.digest.DigestCallback;
import org.jboss.logging.Logger;

/**
 * ApopDigestCallback is used with a JAAS login module.  It retreieves the
 * "apopkey" in the clear from the mbean passed in as 
 * "callback.option.servicename" and prepends the akey to the MessageDigest
 * passed in from the login module.  You must use this with login modules
 * when using JaasUserRepository and supporting APOP.
 * 
 * @version $Revision: 1.2 $
 *
 * @see org.jboss.crypto.digest.DigestyCallback
 *
 * @author Andrew C. Oliver
 */
public class ApopDigestCallback implements DigestCallback {
    private ObjectName mbeanName;

    private MBeanServer server;

    private final static String MBEAN_NAME = "callback.option.serviceName";

    private final static String[] MBEAN_SIGNATURE = new String[] {};

    private final static Object[] MBEAN_ARGS = new Object[] {};

    private static final Logger log = Logger
            .getLogger(ApopDigestCallback.class);

    /** Pass through access to the login module options. When coming from a
     * login module this includes the following keys:
     * callback.option.serviceName - the mbean name to get the AKEY from
     */
    public void init(Map options) {
        try {
            String strMBean = (String) options.get(MBEAN_NAME);
            this.mbeanName = new ObjectName(strMBean);
            this.server = MMJMXUtil.locateJBoss();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void preDigest(MessageDigest digest) {
        try {
            String apopKey = (String) server.invoke(mbeanName, "apopKey",
                    MBEAN_ARGS, MBEAN_SIGNATURE);
            log.debug("preDigest with apopkey=" + apopKey);
            digest.update(apopKey.getBytes());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void postDigest(MessageDigest digest) {
        //nothing done here 
    }
}
