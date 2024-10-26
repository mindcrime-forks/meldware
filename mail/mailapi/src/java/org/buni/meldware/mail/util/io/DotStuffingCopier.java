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
 */
package org.buni.meldware.mail.util.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


/**
 * @author Michael Barker
 * @version $Revision: 1.3 $
 */
public class DotStuffingCopier implements Copier
{
   public final static byte CR = (byte) '\r';
   public final static byte LF = (byte) '\n';
   public final static byte DOT = (byte) '.';
   
   /**
    * Reads all of the data from the supplied input stream until it recieves and
    * EOF.  Any time a "\r\n." string is found an extra full stop is written to the
    * output stream.
    * 
    * @see org.buni.meldware.mail.util.io.Copier#copy(java.io.InputStream, java.io.OutputStream)
    */
   public int copy(InputStream in, OutputStream out, int blockSize) 
           throws IOException
   {
      byte[] block = new byte[Math.max(blockSize, MIN_BLOCK_SIZE)];
      
      int total = 0;
      int count = 0;
      int idx = 0;
      byte lastRead = 0;
      
      while ((count = in.read(block)) != -1)
      {
         while (idx < count)
         {
            int lineIdx = count;

            for (int i = idx; i < count; i++)
            {
               if (count - i == 1)
               {
                  lastRead = block[i];
               }
               else if (i == idx && lastRead == CR && block[i] == LF)
               {
                  lineIdx = i + 1;
                  break;
               }
               else if (CR == block[i] && LF == block[i+1])
               {
                  lineIdx = (i + 2);
                  break;
               }
            }
            
            if (block[idx] == DOT)
            {
               out.write(DOT);
               total++;
            }
            
            out.write(block, idx, lineIdx - idx);
            total += (lineIdx - idx);
            idx = lineIdx;
            
         }  // end while(idx < count);
         idx = 0;
      }
      
      return total;
   }
   
}
