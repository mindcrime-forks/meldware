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

import org.buni.meldware.mail.store.StoreMBean;

/**
 * @author Michael Barker <mailto:mike@middlesoft.co.uk>
 * @version $Revision: 1.1 $
 */
public interface JDBC3StoreMBean extends StoreMBean {
    void setReadStatement(String statement);

    String getReadStatement();

    void setWriteStatement(String statement);

    String getWriteStatement();

    void setIdColumn(String columnName);

    String getIdColumn();

    void setBlobColumn(String columnName);

    String getBlobColumn();

    void setTableName(String tableName);

    String getTableName();

    void setConnected(boolean connected);

    boolean getConnected();

    void setUseStreams(boolean useStreams);

    boolean getUseStreams();
}
