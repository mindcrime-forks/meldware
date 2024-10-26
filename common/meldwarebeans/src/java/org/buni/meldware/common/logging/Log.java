/*
 * Bunisoft the Open Source Communications Company
 * Copyright 2007, Bunisoft LLC., and individual contributors as
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
package org.buni.meldware.common.logging;


  
import org.apache.log4j.Level;
import org.apache.log4j.Logger;


/**
 * Thin wrapper around log4j that adds support for formatted logging.
 * 
 * @author Michael Barker
 *
 */
public class Log {

    private Logger dLog;
    
    protected Log(Logger dLog) {
        this.dLog = dLog;
    }
    
    public void trace(Object o) {
        dLog.debug(o);
    }

    public void trace(Throwable t, Object o) {
        dLog.debug(o, t);
    }
    
    public void trace(String format, Object...params) {
        if (dLog.isDebugEnabled()) {
            dLog.debug(String.format(format, params));            
        }
    }
    
    public void trace(Throwable t, String format, Object...params) {
        if (dLog.isDebugEnabled()) {
            dLog.debug(String.format(format, params), t);
        }        
    }
    
    public void debug(Object o) {
        dLog.debug(o);
    }

    public void debug(Throwable t, Object o) {
        dLog.debug(o, t);
    }
    
    public void debug(String format, Object...params) {
        if (dLog.isDebugEnabled()) {
            dLog.debug(String.format(format, params));            
        }
    }
    
    public void debug(Throwable t, String format, Object...params) {
        if (dLog.isDebugEnabled()) {
            dLog.debug(String.format(format, params), t);
        }        
    }
    
    public void info(Object o) {
        dLog.info(o);
    }

    public void info(Throwable t, Object o) {
        dLog.info(o, t);
    }
    
    public void info(String format, Object...params) {
        if (dLog.isInfoEnabled()) {
            dLog.info(String.format(format, params));            
        }
    }
    
    public void info(Throwable t, String format, Object...params) {
        if (dLog.isInfoEnabled()) {
            dLog.log(Level.INFO, String.format(format, params), t);
        }        
    }
    
    public void warn(Object o) {
        dLog.warn(o);
    }

    public void warn(Throwable t, Object o) {
        dLog.warn(o, t);
    }
    
    public void warn(String format, Object...params) {
        dLog.warn(String.format(format, params));            
    }
    
    public void warn(Throwable t, String format, Object...params) {
        dLog.warn(String.format(format, params), t);
    }
    
    public void error(Object o) {
        dLog.error(o);
    }

    public void error(Throwable t, Object o) {
        dLog.error(o, t);
    }
    
    public void error(String format, Object...params) {
        dLog.error(String.format(format, params));            
    }
    
    public void error(Throwable t, String format, Object...params) {
        dLog.error(String.format(format, params), t);
    }
    
    public void fatal(Object o) {
        dLog.fatal(o);
    }

    public void fatal(Throwable t, Object o) {
        dLog.fatal(o, t);
    }
    
    public void fatal(String format, Object...params) {
        dLog.fatal(String.format(format, params));            
    }
    
    public void fatal(Throwable t, String format, Object...params) {
        dLog.fatal(String.format(format, params), t);
    }
    
    public static Log getLog(String name) {
        return new Log(Logger.getLogger(name));
    }
    
    public static Log getLog(Class c) {
        return new Log(Logger.getLogger(c));
    }

}
