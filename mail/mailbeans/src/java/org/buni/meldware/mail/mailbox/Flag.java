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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * A custom flag (not optimized flags like "deleted" and friends)
 *
 * @author Andrew C. Oliver
 * @version $Revision: 1.4 $
 */
@Entity
public class Flag {
    
    public final static Set<Flag> EMPTY_FLAG_SET = Collections.unmodifiableSet(new HashSet<Flag>());

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Basic
    private String value;

    @ManyToOne
    private FolderEntry message;

    public Flag() {}
    
    /**
     * @param theflag
     */
    public Flag(String theflag) {
        this.value = theflag;
    }

    public long getId() {
        return id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setFolderEntry(FolderEntry message) {
        this.message = message;
    }

    public FolderEntry getMessage() { 
        return this.message;
    }
    
    public boolean equals(Object e) {
        if (e instanceof Flag == false) {
            return false;
        } else if (((Flag)e).getValue().equals(this.getValue())) {
            return true;
        }
        return false;
    }
    
    public int hashCode() {
        return this.getValue().hashCode();
    }
    
    public String toString() {
        return this.getValue();
    }

}
