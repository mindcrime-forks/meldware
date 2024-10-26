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
 * Returns true if a header string equals the passed in value exactly.
 * 
 * @author Andrew C. Oliver (acoliver ot buni dat org)
 * @version $Revision: 1.1 $
 */
public class Equals implements Condition {

    private static final String name = "equals";
    private static final String symbol = "==";
    private static final String antiSymbol = "!=";
    private static final String antonym = "does not equal";
    
    private boolean isNegation;

    public Equals(boolean negation) {
        this.isNegation = negation;
    }
    
    public boolean evaluate(String header, String value) {
        if (header == null && value == null) {
            return isNegation ? false : true;
        } if ((header == null && value != null) || (value == null && header != null)) {
            return isNegation ? true : false;
        }
        return isNegation ? !header.equals(value) : header.equals(value);
    }

    public String getName() {
        return name;
    }

    public String getSymbol() {
        // TODO Auto-generated method stub
        return symbol;
    }

    public String getAntiSymbol() {
        return antiSymbol;
    }

    public String getAntonym() {
        return antonym;
    }

}
