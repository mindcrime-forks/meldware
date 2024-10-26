/*
 * JBoss, Home of Professional Open Source
 * Copyright 2006, JBoss LLC., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
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
package org.buni.meldware.mail.message;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Address;


/**
 * Represents a org.jboss.org.buni.meldware.mail address, be it from, to, cc or
 * otherwise
 * 
 * @author Andrew C. Oliver
 * @version $Revision: 1.5 $
 */
public class MailAddress implements Serializable {

    private static final long serialVersionUID = 3258689918480954416L;

    private String prettyName;

    /**
     * User (that which preceeds the at symbol)
     */
    private String user;

    /**
     * domain (after the at symbol)
     */
    private String domain;

    /**
     * is this user actually valid or is this address bogus (generally syntax
     * related)
     */
    private boolean isvalid;

    /**
     * Whether this address is empty or not
     */
    private boolean isempty;

    /**
     * Construct a new org.jboss.org.buni.meldware.mail address for the user and domain,
     * not to be called by the client class, but rather the result of parsing an
     * address
     * 
     * @param user
     *            from standard org.jboss.org.buni.meldware.mail string preceeding the
     *            at symbol
     * @param domain
     *            from standard org.jboss.org.buni.meldware.mail string following the at
     *            symbol
     */
    private MailAddress(String user, String domain, boolean isfrom) {
        this.user = user;
        this.domain = domain;
        if (user != null && user.length() > 0 && domain != null
                && domain.length() > 0) {
            isvalid = true;
        }

        if ((user == null || user.length() == 0)
                && (domain == null || domain.length() == 0)) {
            isempty = true;
            isvalid = isfrom;
        }
    }

    /**
     * Construct a new org.jboss.org.buni.meldware.mail address for the user and domain,
     * not to be called by the client class, but rather the result of parsing an
     * address
     * 
     * @param user
     *            from standard org.jboss.org.buni.meldware.mail string preceeding the
     *            at symbol
     * @param domain
     *            from standard org.jboss.org.buni.meldware.mail string following the at
     *            symbol
     * @param prettyName
     *            the name before the &gt; &lt;
     */
    private MailAddress(String user, String domain, String prettyName,
            boolean isfrom) {
        this(user, domain, isfrom);
        this.prettyName = prettyName;
    }

    /**
     * copy constructor. Will create a new copy of Mail Address including
     * validity.
     * 
     * @param orig
     *            source MailAddress object
     */
    public MailAddress(MailAddress orig) {
        this.user = new String(orig.getUser());
        this.domain = new String(orig.getDomain());
        this.prettyName = orig.getPrettyName() != null ? new String(orig
                .getPrettyName()) : null;
        this.isvalid = orig.isValid();
        this.isempty = orig.isempty;
    }

    /**
     * @return the name before &lt;bla@bla.bla&gt;
     */
    public String getPrettyName() {
        return prettyName;
    }

    /**
     * If a valid address is used to construct the instance, it should be valid.
     * If the parsing fails for some reason (say no at symbol) then this will be
     * false.
     * 
     * @return whether this instance is valid
     */
    public boolean isValid() {
        return isvalid;
    }

    /**
     * If a valid address is used to construct the instance, it should be valid.
     * If the parsing fails for some reason (say no at symbol) then this will be
     * false.
     * 
     * @return whether this instance is valid
     */
    public boolean isEmpty() {
        return isempty;
    }

    /**
     * @return username (preceeding the at sign)
     */
    public String getUser() {
        return user;
    }
    
    /**
     * @return domain name (following the at sign)
     */
    public String getDomain() {
        return domain;
    }

    /**
     * parse a standard org.jboss.org.buni.meldware.mail string from at domain.com and
     * return a org.jboss.org.buni.meldware.mail address.
     * 
     * @param addr
     *            a string representing a org.jboss.org.buni.meldware.mail address
     * @return MailAddress object representing the passed in string.
     */
    public final static String ADDRESS = "([^@> ]+)(?:@([^@> ]+))?";

    public final static String SMTP_ADDRESS = "([^<]*)<((?:@[^@>]+,)*@[^@>]+:)?(?:([^@>]+)(?:@(([^@>]+)|()))?)?>";

    public final static Pattern ADDRESS_PATTERN = Pattern.compile(ADDRESS);

    public final static Pattern SMTP_ADDRESS_PATTERN = Pattern
            .compile(SMTP_ADDRESS);

    public final static int PRETTY_GROUP = 1;

    public final static int USER_GROUP = 3;

    public final static int DOMAIN_GROUP = 4;


    public static MailAddress parseSMTPStyle(String address) {
        return parseSMTPStyle(address, false);
    }

    /**
     * Parses an smtp style mail address string.  Handles source routing
     * information (basically discards it - as per RFC).
     * 
     * @param address
     * @param isFrom
     * @return
     */
    public static MailAddress parseSMTPStyle(String address, boolean isFrom) {

        MailAddress mailAddress;
        Matcher smtpAddressMatcher = SMTP_ADDRESS_PATTERN.matcher(address);
        Matcher addressMatcher = ADDRESS_PATTERN.matcher(address);

        if (smtpAddressMatcher.matches()) {

            String prettyName = getPrettyName(smtpAddressMatcher);
            String user = getUser(smtpAddressMatcher);
            String domain = getDomain(smtpAddressMatcher);

            if (user.length() > 0 && domain.length() == 0) {
                domain = "localhost";
            }

            mailAddress = new MailAddress(user, domain, prettyName, isFrom);
        } else if (addressMatcher.matches()) {

            String prettyName = "";
            String user = addressMatcher.group(1);
            String domain = addressMatcher.group(2);

            if (user.length() > 0 && (domain == null || domain.length() == 0)) {
                domain = "localhost";
            }
            mailAddress = new MailAddress(user, domain, prettyName, isFrom);
        } else {
            mailAddress = new MailAddress("", "", isFrom);
        }

        return mailAddress;
    }

    /**
     * Gets the user from the address matcher.
     * 
     * @param m
     * @return
     */
    private static String getUser(Matcher m) {
        return m.group(USER_GROUP) != null ? m.group(USER_GROUP).trim() : "";
    }

    /**
     * Get the pretty name from the matcher.
     * 
     * @param m
     * @return
     */
    private static String getPrettyName(Matcher m) {
        return m.group(PRETTY_GROUP) != null ? m.group(PRETTY_GROUP).trim()
                : "";
    }

    /**
     * Get the domain from the matcher.
     * 
     * @param m
     * @return
     */
    private static String getDomain(Matcher m) {
        return m.group(DOMAIN_GROUP) != null ? m.group(DOMAIN_GROUP).trim()
                : "";
    }

    /**
     * determines if this mail address represents the same as another address.
     * This is a simple textual compare, we have no idea if first@jboss.org is
     * the same as First.Last@jboss.org or if first@jboss.org is the same as
     * 
     * first@mail.jboss.org.
     * 
     * @param address
     *            to test
     * @return true if its equal or false if it isnt
     */
    public boolean equals(MailAddress address) {
        if (!(address instanceof MailAddress)) {
            return false;
        } 
        if (this.getUser().toLowerCase().equals(address.getUser().toLowerCase())
                && this.getDomain().toLowerCase().equals(address.getDomain().toLowerCase())) {
            return true;
        }
        return false;
    }

    // TODO: make support prettyNames
    public String toString() {
        if (isempty) {
            return "<>";
        } else {
            return "<" + getUser() + "@" + getDomain() + ">";
        }
    }
    
    /**
     * Return the string in the smtp mail format.
     * I.e. <user@domain>
     * 
     * @return
     */
    public String toSMTPString() {
        return toString();
    }
    
    /**
     * Returns an email address in the imap format.
     * I.e. (prettyName source user domain)
     * @return
     */
    public String toIMAPString() {
        String name = imapFormat(getPrettyName());
        String user = imapFormat(getUser());
        String domain = imapFormat(getDomain());
        
        return String.format("(%s NIL %s %s)", name, user, domain);
    }
    
    private String imapFormat(String val) {
        if (isempty || val == null || val.trim().length() == 0) {
            return "NIL";
        } else {
            return "\"" + val + "\"";
        }
    }
    

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        // Added to be able to use Set functionality
        return this.toString().toLowerCase().hashCode();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        // Added to be able to use Set functionality
        if (obj instanceof MailAddress)
            return equals((MailAddress) obj);
        else
            return false;
    }

    public static MailAddress[] parseAddressArray(String[] in) {
        MailAddress[] out = new MailAddress[in.length];
        for (int i = 0; i < in.length; i++) {
            out[i] = parseSMTPStyle(in[i]);
        }
        return out;
    }

    /**
     * Convert an javax.mail.Address[] into a MailAddress[]
     * 
     * @param in
     *            Array of addresses as e.g. obtained from JavaMail getFrom()
     * @return Array of MailAddress[]
     */
    public static MailAddress[] parseAddressArray(Address[] in) {
        MailAddress[] out = new MailAddress[in.length];
        for (int i = 0; i < in.length; i++) {
            out[i] = parseAddress(in[i]);
        }
        return out;
    }

    /**
     * Convert a javax.mail.Address into MailAddress
     * 
     * @param in
     *            Address to convert
     * @return new MailAddress object
     */
    public static MailAddress parseAddress(Address in) {
        return MailAddress.parseSMTPStyle(in.toString());
    }

    /**
     * Returns the basic SMTP String representation of an
     * email address.
     * 
     * @return
     */
    public String getRawAddress() {
        return getUser() + "@" + getDomain();
    }
}
