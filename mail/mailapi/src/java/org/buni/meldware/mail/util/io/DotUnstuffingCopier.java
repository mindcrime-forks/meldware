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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Copier implementation that handles SMTP dot stuffing. The input stream will
 * be dot stuffed. I.e. any time a "\r\n." string is written it will contain an
 * extra "." just after. This class removes those dots therefore (unstuffing
 * them) hence the name. Makes sense? I hope so.
 * 
 * @author Michael Barker
 * @version $Revision: 1.3 $
 */
public class DotUnstuffingCopier implements Copier
{
   public final static byte CR = (byte) '\r';
   public final static byte LF = (byte) '\n';
   public final static byte DOT = (byte) '.';
   public final static int MIN_BLOCK_SIZE = 1024;
   private static final int TERMINAL_LENGTH = 5;

   private boolean eofsok;
   private boolean hitterm;
   
   public DotUnstuffingCopier()
   {
      this(false);
   }
   
   public DotUnstuffingCopier(boolean eofsok) {
	   this.eofsok = eofsok;
   }

   /**
    * Copies from the InputStream to the OutputStream terminating with
    * "\r\n.\r\n" and stuffing dots. Will reset the input stream to the point
    * just after the input termination. If an EOF occurs before a "\r\n.\r\n"
    * then an exception will be thrown.
    * 
    * Dot stuffing really bites.  This is the best way that I could get it
    * reasonably efffiecient.  Reads the input, removing the stuffed dots
    * and terminating on '\r\n.\r\n'.
    * When the block has a partial terminating sequences at its end 
    * these bytes are not written to the output, but pushed to the 
    * front of the block and handled in the next iteration.  The following
    * iteration reads the data offset slightly from the front of the block
    * in this case.
    * 
    * @param in
    * @param out
    * @throws IOException
    */
   public int copy(InputStream in, OutputStream out, int blockSize) throws IOException
   {
      if (!in.markSupported())
      {
//         throw new IllegalArgumentException(  TEMP ACO
//               "InputStream must support mark/reset");
      }

      byte[] block = new byte[Math.max(blockSize, MIN_BLOCK_SIZE)];
      int count = 0;
      int total = 0;
      int startOffset = 0;

      
      in.mark(blockSize);
      while ((count = in.read(block, startOffset, block.length - startOffset) + startOffset) != -1)
      {
         int idx = 0;
         startOffset = 0;
         total += count;
         for (int i = 0; i < count; i++)
         {
            int remaining = count - i;
            switch (remaining)
            {
            case 4:
               if (CR == block[i] && LF == block[i + 1] && DOT == block[i + 2] && DOT == block[i + 3])
               {
                  // TODO: Do we need to check if (i - idx > 0).
                  out.write(block, idx, (i - idx) + 3);
                  startOffset = 0;
                  i += remaining;
               }
               else if (CR == block[i] && LF == block[i + 1] && DOT == block[i + 2] && CR == block[i + 3])
               {
                  // Write all but the last 4.
                  out.write(block, idx, (i - idx));
                  // push this to front of the buffer.
                  System.arraycopy(block, i, block, 0, 4);
                  startOffset = remaining;
                  i += remaining;
               }
               break;

            case 3:
               if (CR == block[i] && LF == block[i + 1] && DOT == block[i + 2])
               {
                  // Don't write the last 3.
                  out.write(block, idx, (i - idx));
                  // Push the remaining 3 up
                  System.arraycopy(block, i, block, 0, remaining);
                  startOffset = remaining;
                  i += remaining;
               }
               break;

            case 2:
               if (CR == block[i] && LF == block[i + 1])
               {
                  // Don't write the last 2.
                  out.write(block, idx, (i - idx));
                  // Push the remaining 2 up
                  System.arraycopy(block, i, block, 0, remaining);
                  startOffset = remaining;
                  i += remaining;
               }
               break;

            case 1:
               if (CR == block[i])
               {
                  // Don't write the last 1.
                  out.write(block, idx, (i - idx));
                  // Push the remaining 1 up
                  System.arraycopy(block, i, block, 0, remaining);
                  startOffset = remaining;
               }
               else
               {
                  // Write all of the remaining data
                  out.write(block, idx, (i - idx) + 1);
                  startOffset = 0;
               }
               i += remaining;
               break;

            case 0:
               throw new RuntimeException("Unreachable");

            default:
               if (CR == block[i] && LF == block[i + 1] && DOT == block[i + 2])
               {
                  if (DOT == block[i + 3])
                  {
                     out.write(block, idx, (i - idx) + 3);
                     i += 3;
                     idx = i + 1;
                     startOffset = 0;
                  }
                  else if (CR == block[i + 3] && LF == block[i + 4])
                  {
                     if (i > idx) 
                     {
                        out.write(block, idx, (i - idx));
                     }
                     in.reset();
                     in.skip(i + TERMINAL_LENGTH);
                     this.hitterm = true;
                     return 0;
                  }
               }
               break;
            }
         } // end for.
         in.mark(blockSize);
      } // end while.

      if (eofsok) {
    	  return 0;
      }
      // We should probably throw a premature EOF exception here.
      throw new IOException("Premature EOF, exepecting '\\r\\n.\\r\\n' to end input stream.");
   }
   
   /**
    * did 
    * @return
    */
   public boolean hitTermination() {
	   return this.hitterm;
   }
}