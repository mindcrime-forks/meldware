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
package org.buni.meldware.mail.maillistener.actions;

/**
 * A condition is the part of a serverside filter to decide to run the action or not.
 * @author Andrew C. Oliver (acoliver ot buni dat org)
 * 
 * @see org.buni.meldware.mail.maillistener.actions.EndsWith
 * @see org.buni.meldware.mail.maillistener.actions.Equals
 * @see org.buni.meldware.mail.maillistener.actions.StartsWith
 * @see org.buni.meldware.mail.maillistener.actions.Contains
 * 
 * @version $Revision: 1.1 $
 */
public interface Condition {
    /**
     * @return name of this condition i.e. "equals"
     */
    String getName();
    /**
     * @return symbol of this condition i.e. "=="
     */
    String getSymbol();
    /**
     * @return name of the opposite of this condition i.e. "not equals"
     */
    String getAntonym();
    /**
     * @return symbol of the opposite of this action i.e. "!="
     */
    String getAntiSymbol();
    
    /**
     * evaluate whether the header evaluates to the value via this condition
     * @param header to match
     * @param value to match the header to
     * @return matches or not
     */
    boolean evaluate(String header, String value);
}
 