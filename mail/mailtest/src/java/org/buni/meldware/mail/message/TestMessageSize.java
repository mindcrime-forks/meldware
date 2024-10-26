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

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.buni.meldware.mail.MailException;
import org.buni.meldware.mail.MailUtil;
import org.buni.meldware.test.JMXTestWrapper;
import org.buni.meldware.mail.util.io.Copier;
import org.buni.meldware.mail.util.io.SimpleCopier;

/**
 * @author Michael Barker
 *
 */
public class TestMessageSize extends TestCase {

    public static TestSuite suite() {
        return JMXTestWrapper.suite(TestMessageSize.class);
    }
    
    public TestMessageSize(String name) {
        super(name);
    }
    
    private Mail createMail(MailBodyManager mgr, String filename) throws FileNotFoundException {
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(filename);
        if (in == null) {
            in = new FileInputStream("src/resources/data/" + filename);
        }
        Mail m = Mail.create(mgr, new BufferedInputStream(in), new SimpleCopier());
        return m;
    }
    
    private Mail createMail(MailBodyManager mgr, String from, String to, String subject, String body) {
        
        Mail m = Mail.create(mgr, from, new String[] { to }, new String[0], 
                new String[0], subject, body);
        return m;
    }
    
    private void checkMessageSize(String message, String filename) throws MailException, IOException
    {
        MailBodyManager mgr = MailUtil.getMailBodyManager();
        Mail m = createMail(mgr, filename);
        assertEquals(message, actualSize(mgr, m), m.getMessageSize());
    }
    
    private int actualSize(MailBodyManager mgr, Mail m) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Copier c = new SimpleCopier();
        try {
            c.copy(m.getRawStream(mgr), baos, 8192);
        } catch (Exception e) {
            throw new MailException(e);
        }
        return baos.toByteArray().length;
    }

//    Useful method for find out why sizes are wrong.
//    private int actualSize(Mail m) {
//        try {
//            File dir = File.createTempFile("meldware", "msg");
//            System.out.println(dir.getAbsolutePath());
//            FileOutputStream baos = new FileOutputStream(dir);
//            Copier c = new SimpleCopier(8192);
//            c.copy(m.getRawStream(), baos);
//            return (int) dir.length();
//        } catch (Exception e) {
//            throw new MailException(e);
//        }
//    }
    
    public void testManualMessageSize() throws Exception {
        MailBodyManager mgr = MailUtil.getMailBodyManager();
        Mail m = createMail(mgr, "<tom@localhost>", "<tom@localhost>", "test", "test");
        assertEquals(actualSize(mgr, m), m.getMessageSize());
    }
    
    public void testMimeMailSize() throws Exception {
        checkMessageSize("Mime message size is incorrect", "test5.msg");
    }
    
    public void testTextMailSize() throws Exception {
        checkMessageSize("Text message size is incorrect", "text-mail.msg");
    }
}
