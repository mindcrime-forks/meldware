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
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PushbackInputStream;

import org.buni.meldware.common.logging.Log;



/**
 * @author Michael Barker <mailto:mike@middlesoft.co.uk>
 * @version $Revision: 1.8 $
 */
public class IOUtil
{
   
   /**
    * Takes a byte array that was previously an object and converts
    * it back.
    * 
    * @param data
    * @return
    */
   public static Object byteArrayToObject(byte[] data)
   {
      try
      {
         ByteArrayInputStream bais = new ByteArrayInputStream(data);
         ObjectInputStream oin = new ObjectInputStream(bais);
         return oin.readObject();         
      }
      catch (IOException e)
      {
         throw new RuntimeException("Error restoring object", e);
      }
      catch (ClassNotFoundException e)
      {
         throw new RuntimeException("Error restoring object", e);
      }
   }
   
   /**
    * Converts an object to a byte array.
    * 
    * @param o
    * @return
    */
   public static byte[] objectToByteArray(Object o)
   {
      try
      {
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         ObjectOutputStream out = new ObjectOutputStream(baos);
         out.writeObject(o);
         
         return baos.toByteArray();
      }
      catch (IOException e)
      {
         throw new RuntimeException("Error serializing object", e);
      }            
   }

   /**
    * @param in
    */
   public static void quietClose(Log log, InputStream in)
   {
      if (in != null)
      {
         try
         {
            in.close();
         }
         catch (Throwable t)
         {
            log.warn("Unable to close input stream: " + t.getMessage());
         }
      }
   }
   
   /**
    * Closes an output stream without throwing an exception
    * 
    * @param log
    * @param out
    */
   public static void quietClose(Log log, OutputStream out)
   {
      if (out != null)
      {
         try
         {
            out.close();
         }
         catch (Throwable t)
         {
            log.warn("Unable to close input stream: " + t.getMessage());
         }
      }
   }
   
   
   /**
    * Appends a line from the input stream to the supplied stringbuilder.
    * Supports line termination with '\r', '\n', '\n\r', '\r\n'.
    * 
    * @param b The StringBuilder to append the result to.
    * @param in The input stream to read from (must support mark(int)).
    * @param charSet The charset to encode to.
    * @return The number of bytes read including the terminator.
    * @throws IOException
    */
   public static int appendLine(StringBuilder b, InputStream in, String charSet) 
   throws IOException {
       
       int numRead = 0;
       
       if (!in.markSupported()) {
           throw new IOException("mark(int) not support for input stream");
       }
   
       ByteArrayOutputStream out = new ByteArrayOutputStream(128);
   
       int c;
       while ((c = in.read()) != -1) {
           numRead++;
           if (c == '\r' || c == '\n') {
               char expected = (c == '\r') ? '\n' : '\r';
               in.mark(1);
               int nextC = in.read();
               if (nextC == expected) {
                   numRead++;
               } else {
                   in.reset();
               }
               break;
           } else {
               out.write(c);
           }
       }
       
       if (out.size() > 0) {
           b.append(out.toString(charSet));
       }
       
       return numRead;
   }
   
   public static int appendLine(StringBuilder b, PushbackInputStream in, 
           String charSet) throws IOException {
       
       int numRead = 0;
       
       ByteArrayOutputStream out = new ByteArrayOutputStream(128);
   
       int c;
       while ((c = in.read()) != -1) {
           numRead++;
           if (c == '\r' || c == '\n') {
               char expected = (c == '\r') ? '\n' : '\r';
               int nextC = in.read();
               if (nextC == expected) {
                   numRead++;
               } else {
                   in.unread(nextC);
               }
               break;
           } else {
               out.write(c);
           }
       }
       
       if (out.size() > 0) {
           b.append(out.toString(charSet));
       }
       
       return numRead;
   }
   
   /**
    * Parse the mail body up to the end of the DATA section.
    *
    * This implementation carefully avoids creating in memory
    * copies of the underlying byte array buffer.
    *
    * This takes care of:
    *   dot stuffing
    *   max message size
    */
   public static byte[] parseBody(InputStream inputStream, long maxLength) throws IOException
   {
      final byte[] dotStuffing = "\r\n.".getBytes();
      final byte[] dataEnd = "\r\n.\r\n".getBytes();
      byte[] body = null;
      //boolean traceEnabled = log.isTraceEnabled();

      int curIndex = 0;
      //int crlfIndex = 0;
      boolean dotStuffed = false;
      ExtByteArrayOutputStream buffer = new ExtByteArrayOutputStream(4 * 1024);

      byte[] inChunk = new byte[1024];
      byte[] outChunk = new byte[1024];
      int outChunkIndex = 0;

      inputStream.mark(inChunk.length);
      int count = inputStream.read(inChunk);

      byte bLast = 0;
      byte bRead = 0;
      while (count != -1)
      {
         for (int i = 0; i < count; i++)
         {
            bRead = inChunk[i];
            if (bRead != '\r' && bRead != '\n')
               dotStuffed = false;

            if (bRead == '.')
            {
               // write out chunk before every buffer.endsWith()
               buffer.write(outChunk, 0, outChunkIndex);
               outChunkIndex = 0;

               if (buffer.endsWith(dotStuffing))
               {
                  dotStuffed = true;
                  bLast = bRead;
                  continue;
               }
            }

            if (curIndex > maxLength)
               throw new IllegalStateException("Maximum message length exceeded: " + maxLength);

            if(outChunkIndex == outChunk.length)
            {
               buffer.write(outChunk, 0, outChunkIndex);
               outChunkIndex = 0;
            }

            outChunk[outChunkIndex] = bRead;
            outChunkIndex++;
            curIndex++;

            if (bRead == '\n' && bLast == '\r')
            {
//               if (traceEnabled)
//               {
//                  int length = curIndex - crlfIndex;
//                  String line = new String(buffer.toByteArray(crlfIndex, length));
//                  crlfIndex = curIndex;
//                  log.debug(line);
//               }

               // write out chunk before every buffer.endsWith()
               buffer.write(outChunk, 0, outChunkIndex);
               outChunkIndex = 0;

               // detect end of data
               if (!dotStuffed && buffer.endsWith(dataEnd))
               {
                  // truncate ".\r\n"
                  body = buffer.toByteArray(0, curIndex - 3);

                  // reset and read until the end of the data
                  inputStream.reset();
                  byte[] dummy = new byte[i];
                  inputStream.read(dummy);

                  return body;
               }
            }

            bLast = bRead;
         }

         inputStream.mark(inChunk.length);
         count = inputStream.read(inChunk);
      }
      throw new IOException("Premature EOF");
   }


         /**
    * Extends the ByteArrayOutput stream to perform fast operations
    * on the underlying byte array with creating copies.
    */
   public static class ExtByteArrayOutputStream extends ByteArrayOutputStream
   {

      public ExtByteArrayOutputStream(int size)
      {
         super(size);
      }

      /** Return true if the last bytes written to the underlying byte array
       *  correspond to the given tail byte array
       */
      public synchronized boolean endsWith(byte[] tailBytes)
      {
         int tailLength = tailBytes.length;
         int offset = count - tailLength;

         if (offset < 0)
            return false;

         for (int i = 0; i < tailLength; i++)
         {
            if (buf[offset + i] != tailBytes[i])
               return false;
         }

         return true;
      }

      /** Returns a copy of the underlying byte array.
       */
      public synchronized byte[] toByteArray(int offset, int length)
      {
         byte newbuf[] = new byte[length];
         System.arraycopy(buf, offset, newbuf, 0, length);
         return newbuf;
      }
   } 
   

    public static String toString(InputStream in, String encoding) 
            throws IOException {
        byte[] b = new byte[8192];
        StringBuilder sb = new StringBuilder();
        for (int numRead = 0; (numRead = in.read(b)) != -1;) {
            sb.append(new String(b, 0, numRead, encoding));
        }
        return sb.toString();
    }
    
    public static String toString(InputStream in) throws IOException {
        byte[] b = new byte[8192];
        StringBuilder sb = new StringBuilder();
        for (int numRead = 0; (numRead = in.read(b)) != -1;) {
            sb.append(new String(b, 0, numRead));
        }
        return sb.toString();
    }
    
}
