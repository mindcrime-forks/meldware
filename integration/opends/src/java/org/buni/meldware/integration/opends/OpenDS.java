/*
 * Bunisoft the Open Source Communications Company
 * Copyright 2007, Bunisoft LLC, and individual contributors as
 * indicated by the @authors tag.  See the copyright.txt in the 
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; version 2.1 of
 * the License.
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
package org.buni.meldware.integration.opends;

import java.util.List;
import java.util.Map;

public interface OpenDS {
    // modification type constants
    int DELETE = 2;

    int ADD = 1;

    int UPDATE = 0;
    
    int NONE = -1;

    // search scope constants
    int BASE_OBJECT = 0;

    int SINGLE_LEVEL = 1;

    int SUBORDINATE_SUBTREE = 2;

    int WHOLE_SUBTREE = 3;

    void create();

    void destroy();

    void start();

    void stop();

    void setConfigFile(String filename);

    String getConfigFile();

    void setRootDir(String filename);

    String getRootDir();

    void addEntry(String bindDN, String password, String dn, List<String> attrs, Map<String, List<String>> avals);

  //  void addEntry(String bindDN, String password, String dn, List<String> attTypes, List<String> avals);

    void deleteEntry(String bindDN, String password, String dn);

    void updateEntry(String bindDN, String password, String dn, Map<String,Integer> types, List<String> attTypes,
            List<String> avals);

    Object search(String bindDN, String password, String baseDN, int scope, String filter);

    /**
     * import an ldif file
     * @param bindDN - the user bind dn (ie cn=Directory Manager)
     * @param password - the password
     * @param backend - the name of the backend to import to (i.e. userRoot) 
     * @param file - the ldif file to import
     */
    void importLDIF(String bindDN, String password, String backend, String file);

    void deleteAttribute(String bindDN, String password, String dn, String attribute);

}
