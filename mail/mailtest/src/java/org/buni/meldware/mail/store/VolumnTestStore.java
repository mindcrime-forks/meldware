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
package org.buni.meldware.mail.store;

import java.io.InputStream;
import java.io.OutputStream;

import javax.naming.NamingException;

import junit.framework.TestCase;

/**
 * @author Michael Barker <mailto:mike@middlesoft.co.uk>
 *
 */
public abstract class VolumnTestStore extends TestCase
{
   
   public abstract StoreItem getStoreItem();
   
   /**
    * Write an array of bytes to the output stream and read them back again.
    * @throws Exception 
    * 
    * @throws NamingException
    */
   public void testBlob65536ByteBuffers() throws Exception
   {
      int bufSize = (int) Math.pow(2, 16);
      int loops = 1024;
      
      System.out.println("Total bytes: " + bufSize);
      StoreItem item = getStoreItem();
      InputStream in = item.getInputStream();
      OutputStream out = item.getOuputStream();      
      
      
      byte[] bs = new byte[bufSize];
      
      for (int i = 0; i < bs.length; i++)
      {
         bs[i] = (byte) ((i % 26) + 97);
      }
      
      
      long t0 = System.currentTimeMillis();
      for (int i = 0; i < loops; i++)
      {
         out.write(bs);
      }
      out.flush();
      out.close();
      long t1 = System.currentTimeMillis();
      
      byte retBs[] = new byte[bufSize];
      
      long t2 = System.currentTimeMillis();      
      for (int i = 0; i < loops; i++)
      {
         int numRead = in.read(retBs);          
         //assertTrue(Arrays.equals(bs, retBs));
      }
      long t3 = System.currentTimeMillis();
      
      System.out.println("Time to write: " + (t1 - t0));
      System.out.println("Time to read: " + (t3 - t2));
   }   
   
   /**
    * Write an array of bytes to the output stream and read them back again.
    * @throws Exception 
    * 
    * @throws NamingException
    */
   public void testBlob1MB() throws Exception
   {
      int bufSize = 1 * 1024 * 1024; // 10 MB

      StoreItem item = getStoreItem();
      InputStream in = item.getInputStream();
      OutputStream out = item.getOuputStream();      
      
      
      byte[] bs = new byte[bufSize];
      
      for (int i = 0; i < bs.length; i++)
      {
         bs[i] = (byte) ((i % 26) + 97);
      }
      
      
       long t0 = System.currentTimeMillis();
      for (int i = 0; i < 10; i++)
      {
         out.write(bs);
      }
      out.flush();
      out.close();
      long t1 = System.currentTimeMillis();
      
      byte retBs[] = new byte[bufSize];
      long t2 = System.currentTimeMillis();
      for (int i = 0; i < 10; i++)
      {
         int numRead = in.read(retBs);         
         assertFalse(numRead == -1 && i < 9);
      }
      long t3 = System.currentTimeMillis();

      System.out.println("Time to write: " + (t1 - t0));
      System.out.println("Time to read: " + (t3 - t2));
   }      
   
   /**
    * Write an array of bytes to the output stream and read them back again.
    * @throws Exception 
    * 
    * @throws NamingException
    */
   public void testBlob128MB() throws Exception
   {
      int bufSize = 1 * 1024 * 1024; // 1 MB
      int loopSize = 128; // Write buffer this many times.

      StoreItem item = getStoreItem();
      InputStream in = item.getInputStream();
      OutputStream out = item.getOuputStream();      
      
      
      byte[] bs = new byte[bufSize];
      
      for (int i = 0; i < bs.length; i++)
      {
         bs[i] = (byte) ((i % 26) + 97);
      }
      
      
      long t0 = System.currentTimeMillis();
      for (int i = 0; i < loopSize; i++)
      {
         out.write(bs);
      }
      out.flush();
      out.close();
      long t1 = System.currentTimeMillis();
      
      byte retBs[] = new byte[bufSize];
      int numRead = 0;
      long t2 = System.currentTimeMillis();
      for (int i = 0; i < loopSize; i++)
      {
         numRead += in.read(retBs);
         //assertFalse(numRead == -1 && i < (loopSize - 1));
      }
      long t3 = System.currentTimeMillis();

      assertEquals(loopSize * bufSize, numRead);
      
      System.out.println("Time to write: " + (t1 - t0));
      System.out.println("Time to read: " + (t3 - t2));
   }       

}
