/*
 * Bunisoft the Open Source Communications Company
 * Copyright 2006, Bunisoft Inc., and individual contributors as
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
package org.buni.meldware.mail.message;

import org.buni.meldware.mail.store.StoreException;
import org.buni.meldware.mail.util.io.Copier;
import org.buni.meldware.mail.util.io.SimpleCopier;

/**
 * @author Michael Barker <mailto:mike@middlesoft.co.uk>
 * @version $Revision: 1.2 $
 */
public abstract class MailBody implements Body {
    public final static int SIMPLE_TYPE = 0;

    public final static int STORED_TYPE = 1;

    protected final static int BUFFER_SIZE = 1024;

    protected final static Copier COPIER = new SimpleCopier();

    long maxLength = 10000000;

    public abstract Long getStoreId() throws StoreException;

    /**
     * Return the type of the Mail Body
     * @return
     */
    public abstract int getType();


    public void setMaxLength(long length) {
        this.maxLength = length;
    }

    public long getMaxLength() {
        return maxLength;
    }

}