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
package org.buni.meldware.mail.message;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Iterator;

import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author Michael Barker
 *
 */
public class TestMailHeaders extends TestCase {

    public static TestSuite suite() {
        return new TestSuite(TestMailHeaders.class);
    }
    
    public void testMailHeadersSimple() {
        MailHeaders headers = new MailHeadersImpl();
        headers.addHeader("Received", "Hello");
        assertEquals("Hello", headers.getHeader("Received")[0]);
        assertEquals("Received: Hello", headers.getAllHeaderLines().next());
    }
    
    private MailHeaders create() throws Exception {
        
        StringBuilder sb = new StringBuilder();
        sb.append("\r\n");
        sb.append("\r\n");
        sb.append("\r\n");
        sb.append("Received: by 10.67.93.5 with SMTP id v5cs125777ugl; Sat, 3 Jun 2006 09:41:58 -0700 (PDT)\r\n");
        sb.append("Received: by 10.49.66.2 with SMTP id t2mr2894028nfk; Sat, 03 Jun 2006 09:41:58 -0700 (PDT)\r\n");
        sb.append("Message-Id: <29772F32-0F2F-4154-886A-CA50F581F199@gnu.org>\r\n");
        sb.append("Received: from unknown (HELO developer.classpath.org) (216.218.240.216) by 192.168.147.25 with SMTP; 3 Jun 2006 01:14:24 -0000\r\n");
        sb.append("Return-Path: <classpath-bounces@gnu.org>\r\n");
        sb.append("Mime-Version: 1.0 (Apple Message framework v750)\r\n");
        sb.append("Content-Transfer-Encoding: 7bit\r\n");
        sb.append("To: Classpath <classpath@gnu.org>\r\n");
        sb.append("Subject: FYI: ssl-nio-branch made; jessie-nio abandoned\r\n");
        sb.append("From: Casey Marshall <csm@gnu.org>\r\n");
        sb.append("Sender: classpath-bounces@gnu.org\r\n");
        sb.append("\r\n");
        
        ByteArrayInputStream in = new ByteArrayInputStream(sb.toString().getBytes());
        
        MailHeaders mh = MailHeadersImpl.create(in);
        
        return mh;
    }
    
    public void testRemoveExisting() throws Exception {
        MailHeaders mhs = create();
        mhs.removeHeader("To");
        assertEquals(0, mhs.getHeader("To").length);
    }
    
    public void testRemoveNonExisting() throws Exception {
        MailHeaders mhs = create();
        mhs.removeHeader("To:");
    }
    
//    public void testFolding() {
//        MailHeaders mh = new MailHeadersImpl();
//        String longHeader = "This is a header that is longer than 76 characters so that we can test the folding of long lines";
//        String expected = "X-Test: This is a header that is longer than 76 characters so that we can test\r\n  the folding of long lines";
//        mh.addHeader("X-Test", longHeader);
//        String s = (String) mh.getAllHeaderLines().next();
//        assertEquals(s, expected);
//    }
    
    public void testOrder() {
        
        MailHeaders mh = new MailHeadersImpl();
        mh.addHeader("X-Test", "Nothing");
        mh.addHeader("Received", "Localhost");
        mh.addHeader("To", "Me");
        mh.addHeader("From", "Somebody else");
        mh.addHeader("Return-Path", "Back Home");
        mh.addHeader("Resent-To", "Them");
        
        Iterator<String> i = mh.iterator();
        assertTrue(i.next().indexOf("Received") != -1);
        assertTrue(i.next().indexOf("Return-Path") != -1);
        assertTrue(i.next().indexOf("Resent-To") != -1);
        assertTrue(i.next().indexOf("X-Test") != -1);
        assertTrue(i.next().indexOf("To") != -1);
        assertTrue(i.next().indexOf("From") != -1);
    }
    
    public void testCase() throws Exception {
        
        MailHeaders mh = create();
        assertNotNull(mh.getHeader("message-id"));
        
    }
    
    public void testSize() throws Exception {
        
        MailHeaders mh = create();
        
        int size = mh.size("UTF-8");
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        for (String header : mh) {
            baos.write(header.getBytes("UTF-8"));
            baos.write("\r\n".getBytes("UTF-8"));
        }
        
        assertEquals(size, baos.toByteArray().length);
    }
}
