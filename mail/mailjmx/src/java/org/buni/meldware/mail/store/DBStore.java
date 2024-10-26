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

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 * Simple abstract class that handles database connections for the store.
 * 
 * @author Michael Barker <mailto:mike@middlesoft.co.uk>
 * @version $Revision: 1.1 $
 */
public abstract class DBStore extends AbstractStore implements DBStoreMBean {

    private String dataSource = null;

    private DataSource ds = null;

    private boolean disconnected = false;

    public DataSource getDS() {
        return ds;
    }

    public void setDS(DataSource ds) {
        this.ds = ds;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public String getDataSource() {
        return this.dataSource;
    }

    /**
     * @return Returns the disconnected.
     */
    public boolean getDisconnected() {
        return disconnected;
    }

    /**
     * @param disconnected
     *           The disconnected to set.
     */
    public void setDisconnected(boolean disconnected) {
        this.disconnected = disconnected;
    }

    public void init() throws StoreException {
        if (ds == null) {
            try {
                InitialContext ctx = new InitialContext();
                this.ds = (DataSource) ctx.lookup(dataSource);
            } catch (NamingException e) {
                throw new StoreException("Unable to locate database: "
                        + dataSource);
            }
        }
    }

    public Connection getConnection() throws StoreException {
        try {
            return ds.getConnection();
        } catch (SQLException e) {
            throw new StoreException("Unable to getConnection");
        }
    }

}
