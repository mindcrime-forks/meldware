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
package org.buni.meldware.mail.mailbox;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Folders/mailboxes have Aliases. The aliases may match a user alias or
 * username.
 * 
 * @author Andrew C. Oliver
 * @version $Revision: 1.2 $
 */
@Entity
public class Alias implements Serializable {
    /**
     * serialization ID of this version
     */
    private static final long serialVersionUID = 7717509459976136413L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    long id;

    @Basic
    private String name;

    public long getId() {
        return id;
    }
    
    /**
     * Set the alias name
     * 
     * @param name
     *            of the alias
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return name of the alias
     */
    public String getName() {
        return this.name;
    }
}
