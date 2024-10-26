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
 * Returns true if a header string ENDS with the passed in value
 * @author Andrew C. Oliver (acoliver ot buni dat org)
 * @version $Revision: 1.1 $
 */
public class EndsWith implements Condition {

    private static final String name = "ends with";
    private static final String antonym = "does not end with";
    
    private boolean isNegation;
    
    public EndsWith(boolean negation) {
        this.isNegation = negation;
    }

    public boolean evaluate(String header, String value) {
        return isNegation ? !header.endsWith(value) : header.endsWith(value);
    }

    public String getAntiSymbol() {
        return antonym;
    }

    public String getAntonym() {
        return antonym;
    }

    public String getName() {
        return name;
    }

    public String getSymbol() {
        return  name;
    }

}
