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
package org.buni.meldware.mail.maillist;

import java.util.HashMap;

/** Used to get/set properties from a mailing list. It is initialised with 
 * default values that hopefully make sense!  
 *
 * @author <a href="kabirkhan@bigfoot.com">Kabir Khan</a>
 * @version $Revision: 1.1 $
 */
public class MailListProperties implements MailListPropertyConstants, Cloneable {

    private static HashMap<String,Object> defaultProperties = new HashMap<String,Object>();
    private HashMap<String,Object> properties;
    
    static {
        defaultProperties.put(REPLY_TO_LIST, new Boolean(true));
        defaultProperties.put(PREFIX_AUTO_BRACKETED, new Boolean(true));
        defaultProperties.put(ATTACHMENT_ALLOWED, new Boolean(false));
        defaultProperties.put(MEMBERS_ONLY, new Boolean(true));
    }

    public MailListProperties() {
        properties = (HashMap<String, Object>) defaultProperties.clone();
    }

    public void setProperty(String name, String value) {
        properties.put(name, value);
    }

    public void setProperty(String name, Long value) {
        properties.put(name, value);
    }

    public void setProperty(String name, Boolean value) {
        properties.put(name, value);

    }

    public String getProperty(String name) {
        String val = (String) properties.get(name);

        if (val == null) {
            val = (String) defaultProperties.get(name);
        }

        return val;
    }

    public Boolean getPropertyBool(String name) {
        Boolean val = (Boolean) properties.get(name);

        if (val == null) {
            val = (Boolean) defaultProperties.get(name);
        }

        return val;
    }

    public Long getPropertyLong(String name) {
        Long val = (Long) properties.get(name);

        if (val == null) {
            val = (Long) defaultProperties.get(name);
        }

        return val;
    }

    public Object clone() {
        MailListProperties mlprops = new MailListProperties();
        mlprops.properties = (HashMap) properties.clone();
        return mlprops;
    }
}
