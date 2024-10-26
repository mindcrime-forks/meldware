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
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

import junit.framework.TestCase;

/**
 * @author Michael Barker
 *
 */
public class TestDotUnstuffingCopier extends TestCase
{

   private byte[] input;
   private String expected;
   
   public void setUp()
   {
      StringBuffer sbInput = new StringBuffer();
      StringBuffer sbExpected = new StringBuffer();
      String sExpected = "Hello\r\n.";
      String sInput = "Hello\r\n..";
      for (int i = 0; i < 1000; i++)
      {
         sbInput.append(sInput);
         sbExpected.append(sExpected);
      }
      sbInput.append("\r\n.\r\n");
      //sbExpected.append("\r\n");
      input = sbInput.toString().getBytes();
      expected = sbExpected.toString();
   }
   
   
   private void doCopy(int blockSize, byte[] input, String expected) throws IOException
   {
      DotUnstuffingCopier copier = new DotUnstuffingCopier();
      ByteArrayInputStream in = new ByteArrayInputStream(input);
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      
      copier.copy(in, out, 1024);
      
      String result = new String(out.toByteArray());
      
      if (result.length() != expected.length())
      {
         System.out.println("Result Length: " + result.length() + ", Expected length: " + expected.length());
      }
      
      for (int i = 0; i < result.length() && i < expected.length(); i++)
      {
         char res = result.charAt(i);
         char exp = expected.charAt(i);
         if (res != exp)
         {
            System.out.println("Values different at: " + i);
            break;
         }
      }
      
      assertEquals("The dot stuffing hasn't worked", expected, result);
   }

   
   public void testCopySimple() throws IOException
   {
      doCopy(1024, "Hello\r\n..\r\n.\r\n".getBytes(), "Hello\r\n.");
   }
   
   public void testCopyGarbage() throws IOException
   {
       try {
           doCopy(1024, "Hello\r\n..\r\n".getBytes(), "Hello\r\n.");
           assertTrue("Premature EOF Exception should have been thrown", false);
       } catch (IOException e) {
           
       }
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
   
   public void testCopy9() throws IOException
   {
      doCopy(1032, input, expected);
   }

   public void testCopy10() throws IOException
   {
      doCopy(1033, input, expected);
   }
   
   public void testPerf() throws Exception
   {
	   
      Copier c = new DotUnstuffingCopier();
      
      long t0 = System.currentTimeMillis();
      for (int i = 0; i < 1000; i++)
      {
         OutputStream out = new ByteArrayOutputStream();		   
         ByteArrayInputStream in = new ByteArrayInputStream(input);
         c.copy(in, out, 1024);
      }
      long t1 = System.currentTimeMillis();
      
      long t2 = System.currentTimeMillis();
      for (int i = 0; i < 1000; i++)
      {
         OutputStream out = new CountingOutputStream(new ByteArrayOutputStream());		   
         ByteArrayInputStream in = new ByteArrayInputStream(input);
         c.copy(in, out, 1024);
      }
      long t3 = System.currentTimeMillis();
	   
      long t4 = System.currentTimeMillis();
      for (int i = 0; i < 1000; i++)
      {
         OutputStream out = new HashingOutputStream("SHA-256", new CountingOutputStream(new ByteArrayOutputStream()));         
         ByteArrayInputStream in = new ByteArrayInputStream(input);
         c.copy(in, out, 1024);
      }
      long t5 = System.currentTimeMillis();

      long t6 = System.currentTimeMillis();
      for (int i = 0; i < 1000; i++)
      {
         OutputStream out = new HashingOutputStream("MD5", 
                 new CountingOutputStream(
                       new GZIPOutputStream(
                             new ByteArrayOutputStream(), 1024)));         
         ByteArrayInputStream in = new ByteArrayInputStream(input);
         c.copy(in, out, 1024);
      }
      long t7 = System.currentTimeMillis();
      System.out.println("Time Taken: " + (t1-t0));
	  System.out.println("Time Taken (count): " + (t3-t2));
      System.out.println("Time Taken (hash): " + (t5-t4));
      System.out.println("Time Taken (compress): " + (t7-t6));
   }
   
   
}
