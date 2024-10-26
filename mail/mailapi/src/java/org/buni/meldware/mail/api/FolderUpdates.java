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

public class FolderUpdates {

    public static final FolderUpdates NULL = new FolderUpdates();
    private final int[] expunged;
    private final long exists;
    private final long recent;
    private final boolean hasChanged;
    
    public FolderUpdates() {
        this(new int[0], 0L, 0L, false);
    }

    public FolderUpdates(int[] expunged, long exists, long recent) {
        this(expunged, exists, recent, true);
    }
    
    public FolderUpdates(int[] expunged, long exists, long recent, boolean hasChanged) {
        this.expunged = expunged;
        this.exists = exists;
        this.recent = recent;
        this.hasChanged = hasChanged;
    }

    public long getExists() {
        return exists;
    }

    public int[] getExpunged() {
        return expunged;
    }

    public long getRecent() {
        return recent;
    }
    
    public boolean hasChanged() {
        return hasChanged;
    }
}
