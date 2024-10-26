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
package org.buni.meldware.mail.store;

import javax.management.ObjectName;

import org.buni.meldware.mail.util.MMJMXUtil;

/**
 * Set of utility methods for the store.
 * @todo change cutesy name to something more descriptive 
 * @author Michael Barker <mailto:mike@middlesoft.co.uk>
 * @version $Revision: 1.1 $
 */
public class StoreClerk {
    public static int longToInt(long l) {
        if (l >= Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        } else if (l <= Integer.MIN_VALUE) {
            return Integer.MIN_VALUE;
        } else {
            return (int) l;
        }
    }

    /**
     * Gets a proxy to the store, given the name.
     * 
     * @param name Name of the store MBean
     * @return
     * @throws StoreException
     */
    public static Store getStore(String name) throws StoreException {
        try {
            return (Store) MMJMXUtil.getMBean(name, Store.class);
        } catch (Exception e) {
            throw new StoreException(e);
        }
    }

    /**
     * Gets a proxy to the store, given the name.
     * 
     * @param name Name of the store MBean
     * @return
     * @throws StoreException
     */
    public static Store getStore(ObjectName name) throws StoreException {
        try {
            return (Store) MMJMXUtil.getMBean(name, Store.class);
        } catch (Exception e) {
            throw new StoreException(e);
        }
    }

}
