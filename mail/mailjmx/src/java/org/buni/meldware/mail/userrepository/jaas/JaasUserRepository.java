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

import java.io.Serializable;

import javax.security.auth.login.LoginContext;

import org.jboss.logging.Logger;
import org.jboss.security.auth.callback.UsernamePasswordHandler;
import org.jboss.system.ServiceMBean;
import org.jboss.system.ServiceMBeanSupport;

/**
 * 
 * @todo IDENTIFY AUTHOR AND JAVADOC
 *
 */
public class JaasUserRepository extends ServiceMBeanSupport implements
        ServiceMBean, JaasUserRepositoryMBean, Serializable {

    private static final long serialVersionUID = 3257562914868377396L;

    private static final Logger log = Logger
            .getLogger(JaasUserRepository.class);

    private String securityDomain;

    private String postmasterRole = "postmaster";

    private static ThreadLocal<String> apopKey;

    /**
     * 
     * @return the JAAS security domain used to authenticate users
     */
    public String getSecurityDomain() {
        return securityDomain;
    }

    /**
     * Sets the JAAS security domain used to authenticate users
     * @param securityDomain
     */
    public void setSecurityDomain(String securityDomain) {
        this.securityDomain = securityDomain;
    }

    /**
     * Role to identify a user a a postmaster
     * @return
     */
    public String getPostmasterRole() {
        return postmasterRole;
    }

    /**
     * Role to identify a user a a postmaster
     */
    public void setPostmasterRole(String role) {
        this.postmasterRole = role;
    }

    /**
     * Used for testing from jmx console
     */
    public boolean authenticateUser(String username, String password) {
        return test(username, password);
    }

    public boolean test(String username, String password, String apopkey) {
        log.debug("apop test called with username=" + username + ",pw="
                + password + ",apopkey=" + apopkey);
        apopKey = new ThreadLocal<String>();
        apopKey.set(apopkey);
        return login(username, password); //the rest is up to config
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.buni.meldware.mail.userrepository.UserRepository#test(java.lang.String,
     *      java.lang.String)
     */
    public boolean test(String username, String password) {
        return login(username, password);
    }

    /**
     * Tries to log in using JAAS
     * 
     * @param username
     * @param password
     * @return true if successful
     */
    private boolean login(String username, String password) {

        try {
            LoginContext lc = new LoginContext(getSecurityDomain(),
                    new UsernamePasswordHandler(username, password));
            lc.login();

            try {
                // release resources
                lc.logout();
            } catch (Exception e) {
                  log.trace(e);
            }

            log.debug("Login by user " + username);

            return true;
        } catch (Exception e) {
            log.error("Login failed for user " + username);
            log.trace(e);
            return false;
        }
    }


    public String apopKey() {
        return (String) apopKey.get();
    }

    public String getType() {
        return JaasUserRepository.class.getName();
    }

}
