/*
 * Bunisoft the Open Source Communications Company
 * Copyright 2006, Bunisoft LLC.,
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
 * 
 * Regarding this class:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 */
package org.buni.meldware.mail.util;



import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.mail.MessagingException;
import javax.mail.internet.MimeUtility;

import org.buni.meldware.mail.MailException;



/* some of this code was ripped from JAMES - thanks :-) */
/* some code was ripped from NUKES - Thanks  [so this is  LGPL] */

/**
 * Simple Base64 string decoding function
 *
 * @version This is $Revision: 1.5 $
 */

public class Base64 {

    public static BufferedReader decode(String b64string) throws Exception {
        return new BufferedReader(new InputStreamReader(MimeUtility.decode(
                new ByteArrayInputStream(b64string.getBytes()), "base64")));
    }
    
    /**
     * Decodes a base64 encoded inputstream to a byte array.  Allows the 
     * user to specify a maximum length to prevent DoS.
     * @param in
     * @param max
     * @return
     * @throws IOException 
     */
    public static byte[] decode(InputStream in, int max) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            InputStream base64 = MimeUtility.decode(in, "base64");
            
            int count = 0;
            byte[] buffer = new byte[8192];
            int numRead;
            while ((numRead = base64.read(buffer)) != -1) {
                baos.write(buffer, 0, numRead);
                count += numRead;
                if (count > max) {
                    throw new MailException("Max number of bytes exceeded");
                }
            }
            return baos.toByteArray();
            
        } catch (MessagingException e) {
            throw new MailException(e);
        }
    }
    

    public static String decodeAsString(String b64string) throws Exception {
        if (b64string == null) {
            return b64string;
        }
        String returnString = decode(b64string).readLine();
        if (returnString == null) {
            return returnString;
        }
        return returnString.trim();
    }

    public static ByteArrayOutputStream encode(String plaintext)
            throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] in = plaintext.getBytes();
        ByteArrayOutputStream inStream = new ByteArrayOutputStream();
        inStream.write(in, 0, in.length);
        // pad
        if ((in.length % 3) == 1) {
            inStream.write(0);
            inStream.write(0);
        } else if ((in.length % 3) == 2) {
            inStream.write(0);
        }
        inStream.writeTo(MimeUtility.encode(out, "base64"));
        return out;
    }

    public static ByteArrayOutputStream encode(byte[] in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayOutputStream inStream = new ByteArrayOutputStream();
        inStream.write(in, 0, in.length);
        // pad
        if ((in.length % 3) == 1) {
            inStream.write(0);
            inStream.write(0);
        } else if ((in.length % 3) == 2) {
            inStream.write(0);
        }
        try {
            inStream.writeTo(MimeUtility.encode(out, "base64"));
        } catch (MessagingException e) {
            throw new MailException(e);
        }
        return out;
    }

    public static String encodeAsString(String plaintext) throws Exception {
        return encode(plaintext).toString();
    }

    /**
     * Computes an md5 hash of a string.
     * @param text the hashed string
     * @return the string hash
     * @exception NullPointerException if text is null
     */
    public static byte[] md5(String text) {
        // arguments check
        if (text == null) {
            throw new NullPointerException("null text");
        }

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(text.getBytes());
            return md.digest();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Cannot find MD5 algorithm", e);
        }
    }

    /**
     * Computes an md5 hash and returns the result as a string in hexadecimal format.
     * @param text the hashed string
     * @return the string hash
     * @exception NullPointerException if text is null
     */
    public static String md5AsHexString(String text) {
        return toHexString(md5(text));
    }

    /**
     * Returns a string in the hexadecimal format.
     * @param bytes the converted bytes
     * @return the hexadecimal string representing the bytes data
     */
    public static String toHexString(byte[] bytes) {
        StringBuffer hex = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            hex.append(Character.forDigit((bytes[i] & 0XF0) >> 4, 16));
            hex.append(Character.forDigit((bytes[i] & 0X0F), 16));
        }
        return hex.toString();
    }

}
