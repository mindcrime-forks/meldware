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

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.buni.meldware.common.db.DbUtil;

/**
 * @author Michael Barker <mailto:mike@middlesoft.co.uk>
 *
 */
public class StoreTestUtil {

	public static DataSource getDataSource(String className, String url,
			String user, String pass) {
		return new MyDataSource(className, url, user, pass);
	}

	public static void printMemInfo() {
		long tm = Runtime.getRuntime().totalMemory();
		long mm = Runtime.getRuntime().maxMemory();
		long fm = Runtime.getRuntime().freeMemory();
		long afm = (mm - tm) + fm;
		System.out.println("Memory: " + afm / 1000 + "K of " + mm / 1000 + "K");
	}

	public static void execute(DataSource ds, String[] statements)
			throws SQLException {
		Connection con = null;
		Statement stmt = null;
		try {
			con = ds.getConnection();

			for (int i = 0; i < statements.length; i++) {
				stmt = con.createStatement();
				System.out.println("Executing: " + statements[i]);
				stmt.execute(statements[i]);
				stmt.close();
			}

		} finally {
			DbUtil.closeQuietly(null, stmt);
			DbUtil.closeQuietly(null, con);
		}
	}

	static class MyDataSource implements DataSource {
		private String className;

		private String url;

		private String user;

		private String pass;

		public MyDataSource(String className, String url, String user,
				String pass) {
			this.className = className;

			try {
				// Check to see if driver exists in the classpath.
				Class.forName(className);
			} catch (Exception e) {
				throw new RuntimeException("Unable to location driver: "
						+ className);
			}

			this.url = url;
			this.user = user;
			this.pass = pass;
		}

		/* (non-Javadoc)
		 * @see javax.sql.DataSource#getConnection()
		 */
		public Connection getConnection() throws SQLException {
			try {
				Class c = Class.forName(className);
				DriverManager.registerDriver((Driver) c.newInstance());
				return DriverManager.getConnection(url, user, pass);
			} catch (SQLException e) {
				throw e;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		/* (non-Javadoc)
		 * @see javax.sql.DataSource#getConnection(java.lang.String, java.lang.String)
		 */
		public Connection getConnection(String username, String password)
				throws SQLException {
			try {
				Class c = Class.forName(className);
				DriverManager.registerDriver((Driver) c.newInstance());
				return DriverManager.getConnection(url, username, password);
			} catch (SQLException e) {
				throw e;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		/* (non-Javadoc)
		 * @see javax.sql.DataSource#getLogWriter()
		 */
		public PrintWriter getLogWriter() throws SQLException {
			// TODO Auto-generated method stub
			return null;
		}

		/* (non-Javadoc)
		 * @see javax.sql.DataSource#setLogWriter(java.io.PrintWriter)
		 */
		public void setLogWriter(PrintWriter arg0) throws SQLException {
			// TODO Auto-generated method stub

		}

		/* (non-Javadoc)
		 * @see javax.sql.DataSource#setLoginTimeout(int)
		 */
		public void setLoginTimeout(int arg0) throws SQLException {
			// TODO Auto-generated method stub

		}

		/* (non-Javadoc)
		 * @see javax.sql.DataSource#getLoginTimeout()
		 */
		public int getLoginTimeout() throws SQLException {
			// TODO Auto-generated method stub
			return 0;
		}

		public boolean isWrapperFor(Class<?> arg0) throws SQLException {
			// TODO Auto-generated method stub
			return false;
		}

		public <T> T unwrap(Class<T> arg0) throws SQLException {
			// TODO Auto-generated method stub
			return null;
		}

	}

}
