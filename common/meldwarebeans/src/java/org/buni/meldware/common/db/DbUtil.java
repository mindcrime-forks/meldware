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
package org.buni.meldware.common.db;



import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

import javax.sql.DataSource;

import org.buni.meldware.common.logging.Log;




/**
 * Simple utility class for use with JDBC.  Methods here remove
 * the dependency on commons-dbutil.
 * 
 * @author Michael Barker <mailto:mike@middlesoft.co.uk>
 * @version $Revision: 1.3 $
 */
public class DbUtil {

    public static final Object[] EMPTY_OBJARRAY = new Object[]{};

    /**
     * Closes the connection without propagating any exceptions.  Will
     * not catch Error however.
     * 
     * @param log
     * @param cn
     */
    public static void closeQuietly(Log log, Connection cn) {
        if (cn != null) {
            try {
                cn.close();
            } catch (Exception e) {
                if (log != null) {
                    log.warn("Failed to close connection: ", e);
                }
            }
        }
    }

    /**
     * Closes the statement without propagating any exceptions.  Will
     * not catch Error however.
     * 
     * @param log
     * @param cn
     */
    public static void closeQuietly(Log log, Statement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (Exception e) {
                if (log != null) {
                    log.warn("Failed to close statement: ", e);
                }
            }
        }
    }

    /**
     * Closes the result set without propagating any exceptions.  Will
     * not catch Error however.
     * 
     * @param log
     * @param cn
     */
    public static void closeQuietly(Log log, ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (Exception e) {
                if (log != null) {
                    log.warn("Failed to close result set: ", e);
                }
            }
        }
    }

    public static void executeStatement(DataSource ds, String query, Object[] objects) {
        Connection con = null;
        PreparedStatement stmt = null;
        try {
            con = ds.getConnection();
            stmt = con.prepareStatement(query);
            for (int i = 0; i < objects.length; i++) {
               stmt.setObject(i+1, objects[i]);               
            }          
            stmt.execute();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {stmt.close();} catch (Exception e2) {}
            try {con.close();} catch (Exception e2) {}
        }       
    }
    
    public static String generateUID() {
        UUID uuid = UUID.randomUUID();
        long msb = uuid.getMostSignificantBits();
        long lsb = uuid.getLeastSignificantBits();
        byte[] resultBytes = new byte[16];
        for (int i = 7; i >= 0; i--)
        {
          resultBytes[i+8] = (byte)lsb;
            lsb >>>= 8;
            resultBytes[i] = (byte)lsb;
            msb >>>= 8;
        }
        BigInteger bigint = new BigInteger(1, resultBytes);
        return bigint.toString(32);       
    }
    
    public static ResultSet executeResultStatement(DataSource ds, String query, Object[] parms) throws SQLException {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        con = ds.getConnection();
        stmt = con.prepareStatement(query);
        for (int i = 0; i < parms.length; i++) {
           stmt.setObject(i+1, parms[i]);               
        }          
        stmt.execute();
        rs = stmt.getResultSet();
 
        return rs;
    }
    
    public static void closeQuietly(ResultSet rs) {
        Statement stmt = null;
        Connection conn = null; 
        try {
            stmt = rs.getStatement();
        } catch (Exception e) {
            
        }
        
        try {
            conn = stmt != null ? stmt.getConnection() : null;            
        } catch (Exception e) {
        }
        
        try {
            rs.close();
        } catch (Exception e) {
        }
        
        try {
            stmt.close();
        } catch (Exception e) {
        }

        try {
            conn.close();
        } catch (Exception e) {
        }
        
    }

    public static void closeQuietlyOnlyRS(ResultSet rs) {


        
        try {
            rs.close();
        } catch (Exception e) {
        }
        
      
    }


}
