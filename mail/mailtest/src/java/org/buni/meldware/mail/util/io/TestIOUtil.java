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
package org.buni.meldware.mail.util.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author Michael Barker <mailto:mike@middlesoft.co.uk>
 *
 */
public class TestIOUtil extends TestCase
{
   public TestIOUtil(String name)
   {
      super(name);
   }
   
   public static TestSuite suite()
   {
      return new TestSuite(TestIOUtil.class);
   }
   
   public void testByteArrayToObject()
   {
      Serializable s = new String("21513452394850239845");
      byte[] b = IOUtil.objectToByteArray(s);
      Object s2 = IOUtil.byteArrayToObject(b);
      assertEquals(s, s2);
   }
   
   
   public byte[] createData(int lineSize, int numLines) {
      
      byte[] bs = new byte[(lineSize * numLines) + 5];
      
      for (int i = 0; i < 10; i++) 
      {
         for (int j = 0; j < lineSize - 2; j++)
         {
            bs[(i * lineSize) + j] = (byte) ((i % 26) + 97);
         }
         bs[(i * lineSize) + (lineSize - 2)] = '\r';
         bs[(i * lineSize) + (lineSize - 1)] = '\n';
      }

      bs[bs.length - 5] = '\r';
      bs[bs.length - 4] = '\n';
      bs[bs.length - 3] = '.';
      bs[bs.length - 2] = '\r';
      bs[bs.length - 1] = '\n';
      
      return bs;
   }
   
   
   public long timeCopier() throws IOException {
      Copier c = new DotUnstuffingCopier();
      
      byte[] bs = createData(1000, 10);
      
      long t0 = System.currentTimeMillis();
      
      for (int i = 0; i < 10000; i++) {
         ByteArrayOutputStream baos = new ByteArrayOutputStream(4096);
         c.copy(new ByteArrayInputStream(bs), baos, 1024);
         baos.toByteArray();
      }
      
      long t1 = System.currentTimeMillis();
      
      return t1 - t0;
   }
   
   public long timeParseBody() throws IOException {
      
      byte[] bs = createData(1000, 10);
      
      long t0 = System.currentTimeMillis();
      
      for (int i = 0; i < 10000; i++) {
         IOUtil.parseBody(new ByteArrayInputStream(bs), 4000000);
      }
      
      long t1 = System.currentTimeMillis();
      
      return t1 - t0;
   }
   
   public void testCopierPerformance() throws IOException {
      long parseBodyTime = timeParseBody();
      long copierTime = timeCopier();
      System.out.println("Parse Body: " + parseBodyTime + "ms Copier: " + copierTime + "ms");
      assertTrue("The Copier implementation should be quicker, parse body = " + parseBodyTime + " copier = " + copierTime, 
              parseBodyTime > copierTime);
   }
   
   public void testCharset() {
       Charset cs = Charset.forName("US-ASCII");
       String s = "This is a new string";
       ByteBuffer bb = cs.encode(s);
       System.out.println(bb.arrayOffset());
       System.out.println(bb.hasArray());
       System.out.write(bb.array(), bb.arrayOffset() + bb.position(), bb.remaining());
       System.out.println();
   }
}
