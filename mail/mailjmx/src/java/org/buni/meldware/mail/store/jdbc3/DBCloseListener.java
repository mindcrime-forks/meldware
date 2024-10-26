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
package org.buni.meldware.mail.store.jdbc3;



import java.sql.Connection;

import org.buni.meldware.common.db.DbUtil;
import org.buni.meldware.common.logging.Log;
import org.buni.meldware.mail.util.io.StreamCloseEvent;
import org.buni.meldware.mail.util.io.StreamCloseListener;

/**
 * Lister class that closes the database connection.
 * 
 * @author Michael Barker
 * @version $Revision: 1.3 $
 */
public class DBCloseListener implements StreamCloseListener {
    private final static Log log = Log.getLog(DBCloseListener.class);

    Connection cn;

    public DBCloseListener(Connection cn) {
        this.cn = cn;
    }

    public void streamClosed(StreamCloseEvent e) {
        DbUtil.closeQuietly(log, cn);
    }
}
