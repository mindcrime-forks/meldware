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

import junit.framework.TestCase;

/**
 * @author Michael Barker
 *
 */
public class TestDotStuffingLineLimitedCopier extends TestCase
{
   private String input;
   private String expected;
   
   public void setUp()
   {
      StringBuffer sbInput = new StringBuffer();
      expected = "Hello\r\n..Hello\r\n..Hello\r\n..Hello\r\n..Hello\r\n..Hello\r\n..Hello\r\n..Hello\r\n..Hello\r\n..Hello\r\n";
      String sInput = "Hello\r\n.";
      for (int i = 0; i < 1000; i++)
      {
         sbInput.append(sInput);
         //sbExpected.append(sExpected);
      }
      input = sbInput.toString();
   }
   
   
   private void doCopy(int blockSize, String input, String expected) throws IOException
   {
      Copier copier = new DotStuffingLineLimitedCopier(10);
      ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      
      copier.copy(in, out, 1024);
      
      String result = new String(out.toByteArray());
      
      assertEquals("The dot stuffing hasn't worked", expected, result);
   }

   
   public void testCopySimple() throws IOException
   {
      doCopy(1024, "Hello\r\n.", "Hello\r\n..");
   }
   
   public void testCopyGarbage1() throws IOException
   {
      doCopy(1024, "Hello\r\n..\r\n.\r\n", "Hello\r\n...\r\n..\r\n");
   }
   
   public void testCopy1() throws IOException
   {
      doCopy(1024, input, expected);
   }

   public void testCopy2() throws IOException
   {
      doCopy(1025, input, expected);
   }
   
   public void testCopy3() throws IOException
   {
      doCopy(1026, input, expected);
   }
   
   public void testCopy4() throws IOException
   {
      doCopy(1027, input, expected);
   }
   
   public void testCopy5() throws IOException
   {
      doCopy(1028, input, expected);
   }
   
   public void testCopy6() throws IOException
   {
      doCopy(1029, input, expected);
   }
   
   public void testCopy7() throws IOException
   {
      doCopy(1030, input, expected);
   }
   
   public void testCopy8() throws IOException
   {
      doCopy(1031, input, expected);
   }
   
   
}
