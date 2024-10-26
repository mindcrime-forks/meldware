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
package org.buni.meldware.mail.api;

/**
 * Hints on fetch options for retrieving messages.
 * 
 * @author Michael Barker.
 *
 */
public class Hints {

    public final static Hints FLAGS = new Hints(true, false, false);
    public final static Hints NONE = new Hints(false, false, false);
    private final boolean flags;
    private final boolean message;
    private final boolean excludeDeleted;

    public Hints(boolean flags, boolean message, boolean deleted) {
        this.flags = flags;
        this.message = message;
        this.excludeDeleted = deleted;
    }
    
    /**
     * Or an existing set of hints with some new options.
     * 
     * @param other
     * @param flags
     * @param message
     * @param deleted
     */
    public Hints(Hints other, boolean flags, boolean message, boolean deleted) {
        this.flags = other.isFlags() || flags;
        this.message = other.isMessage() || message;
        this.excludeDeleted = other.isExcludeDeleted() || deleted;
    }
    

    public boolean isExcludeDeleted() {
        return excludeDeleted;
    }

    public boolean isFlags() {
        return flags;
    }

    public boolean isMessage() {
        return message;
    }
}
