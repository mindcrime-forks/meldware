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
 * 
 * Regarding this class:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 */
package org.buni.meldware.mail.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * dump data in hexadecimal format; derived from a HexDump utility I (Marc)
 * wrote in June 2001. (taken from POI)
 *
 * @author Marc Johnson
 * @author Glen Stampoultzis  (glens at apache.org)
 * @version $Revision: 1.1 $
 */

public class HexDump
{
    public static final String        EOL         =
        System.getProperty("line.separator");
//    private static final StringBuffer _lbuffer    = new StringBuffer(8);
//    private static final StringBuffer _cbuffer    = new StringBuffer(2);
    private static final char         _hexcodes[] =
    {
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D',
        'E', 'F'
    };
    private static final int          _shifts[]   =
    {
        28, 24, 20, 16, 12, 8, 4, 0
    };


    // all static methods, so no need for a public constructor
    private HexDump()
    {
    }

    /**
     * dump an array of bytes to an OutputStream
     *
     * @param data the byte array to be dumped
     * @param offset its offset, whatever that might mean
     * @param stream the OutputStream to which the data is to be
     *               written
     * @param index initial index into the byte array
     * @param length number of characters to output
     *
     * @exception IOException is thrown if anything goes wrong writing
     *            the data to stream
     * @exception ArrayIndexOutOfBoundsException if the index is
     *            outside the data array's bounds
     * @exception IllegalArgumentException if the output stream is
     *            null
     */
    public synchronized static void dump(final byte [] data, final long offset,
                            final OutputStream stream, final int index, final int length)
            throws IOException, ArrayIndexOutOfBoundsException,
                    IllegalArgumentException
    {
        if ((index < 0) || (data.length != 0 && index >= data.length))
        {
            throw new ArrayIndexOutOfBoundsException(
                "illegal index: " + index + " into array of length "
                + data.length);
        }
        if (data.length == 0)
            return; // nothing more to do.
        if (stream == null)
        {
            throw new IllegalArgumentException("cannot write to nullstream");
        }
        long         display_offset = offset + index;
        StringBuffer buffer         = new StringBuffer(74);


        int data_length = Math.min(data.length,index+length);
        for (int j = index; j < data_length; j += 16)
        {
            int chars_read = data_length - j;

            if (chars_read > 16)
            {
                chars_read = 16;
            }
            buffer.append(
                        dump(display_offset)
                         ).append(' ');
            for (int k = 0; k < 16; k++)
            {
                if (k < chars_read)
                {
                    buffer.append(dump(data[ k + j ]));
                }
                else
                {
                    buffer.append("  ");
                }
                buffer.append(' ');
            }
            for (int k = 0; k < chars_read; k++)
            {
                if ((data[ k + j ] >= ' ') && (data[ k + j ] < 127))
                {
                    buffer.append(( char ) data[ k + j ]);
                }
                else
                {
                    buffer.append('.');
                }
            }
            buffer.append(EOL);
            stream.write(buffer.toString().getBytes());
            stream.flush();
            buffer.setLength(0);
            display_offset += chars_read;
        }

    }

    /**
     * dump an array of bytes to an OutputStream
     *
     * @param data the byte array to be dumped
     * @param offset its offset, whatever that might mean
     * @param stream the OutputStream to which the data is to be
     *               written
     * @param index initial index into the byte array
     *
     * @exception IOException is thrown if anything goes wrong writing
     *            the data to stream
     * @exception ArrayIndexOutOfBoundsException if the index is
     *            outside the data array's bounds
     * @exception IllegalArgumentException if the output stream is
     *            null
     */

    public synchronized static void dump(final byte [] data, final long offset,
                            final OutputStream stream, final int index)
        throws IOException, ArrayIndexOutOfBoundsException,
                IllegalArgumentException
    {
        dump(data, offset, stream, index, data.length-index);
    }

    /**
     * dump an array of bytes to a String
     *
     * @param data the byte array to be dumped
     * @param offset its offset, whatever that might mean
     * @param index initial index into the byte array
     *
     * @exception ArrayIndexOutOfBoundsException if the index is
     *            outside the data array's bounds
     * @return output string
     */
    
    public static String dump(final byte [] data, final long offset,
                            final int index) {
        StringBuffer buffer;
        if ((index < 0) || (index >= data.length))
        {
            throw new ArrayIndexOutOfBoundsException(
                "illegal index: " + index + " into array of length "
                + data.length);
        }
        long         display_offset = offset + index;
        buffer         = new StringBuffer(74);

        for (int j = index; j < data.length; j += 16)
        {
            int chars_read = data.length - j;

            if (chars_read > 16)
            {
                chars_read = 16;
            }
            buffer.append(dump(display_offset)).append(' ');
            for (int k = 0; k < 16; k++)
            {
                if (k < chars_read)
                {
                    buffer.append(dump(data[ k + j ]));
                }
                else
                {
                    buffer.append("  ");
                }
                buffer.append(' ');
            }
            for (int k = 0; k < chars_read; k++)
            {
                if ((data[ k + j ] >= ' ') && (data[ k + j ] < 127))
                {
                    buffer.append(( char ) data[ k + j ]);
                }
                else
                {
                    buffer.append('.');
                }
            }
            buffer.append(EOL);
            display_offset += chars_read;
        }                 
        return buffer.toString();
    }
    

    private static String dump(final long value)
    {
        StringBuffer buf = new StringBuffer();
        buf.setLength(0);
        for (int j = 0; j < 8; j++)
        {
            buf.append( _hexcodes[ (( int ) (value >> _shifts[ j ])) & 15 ]);
        }
        return buf.toString();
    }

    private static String dump(final byte value)
    {
        StringBuffer buf = new StringBuffer();
        buf.setLength(0);
        for (int j = 0; j < 2; j++)
        {
            buf.append(_hexcodes[ (value >> _shifts[ j + 6 ]) & 15 ]);
        }
        return buf.toString();
    }

    /**
     * Converts the parameter to a hex value.
     *
     * @param value     The value to convert
     * @return          A String representing the array of bytes
     */
    public static String toHex(final byte[] value)
    {
        StringBuffer retVal = new StringBuffer();
        retVal.append('[');
        for(int x = 0; x < value.length; x++)
        {
            retVal.append(toHex(value[x]));
            retVal.append(", ");
        }
        retVal.append(']');
        return retVal.toString();
    }

    /**
     * Converts the parameter to a hex value.
     *
     * @param value     The value to convert
     * @return          The result right padded with 0
     */
    public static String toHex(final short value)
    {
        return toHex(value, 4);
    }

    /**
     * Converts the parameter to a hex value.
     *
     * @param value     The value to convert
     * @return          The result right padded with 0
     */
    public static String toHex(final byte value)
    {
        return toHex(value, 2);
    }

    /**
     * Converts the parameter to a hex value.
     *
     * @param value     The value to convert
     * @return          The result right padded with 0
     */
    public static String toHex(final int value)
    {
        return toHex(value, 8);
    }


    private static String toHex(final long value, final int digits)
    {
        StringBuffer result = new StringBuffer(digits);
        for (int j = 0; j < digits; j++)
        {
            result.append( _hexcodes[ (int) ((value >> _shifts[ j + (8 - digits) ]) & 15)]);
        }
        return result.toString();
    }

    /**
     * Dumps <code>bytesToDump</code> bytes to an output stream.
     *
     * @param in          The stream to read from
     * @param out         The output stream
     * @param start       The index to use as the starting position for the left hand side label
     * @param bytesToDump The number of bytes to output.  Use -1 to read until the end of file.
     */
    public static void dump( InputStream in, PrintStream out, int start, int bytesToDump ) throws IOException
    {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        if (bytesToDump == -1)
        {
            int c = in.read();
            while (c != -1)
            {
                buf.write(c);
                c = in.read();
            }
        }
        else
        {
            int bytesRemaining = bytesToDump;
            while (bytesRemaining-- > 0)
            {
                int c = in.read();
                if (c == -1)
                    break;
                else
                    buf.write(c);
            }
        }

        byte[] data = buf.toByteArray();
        dump(data, 0, out, start, data.length);
    }

    public static void main(String[] args) throws Exception {
        File file = new File(args[0]);
        InputStream in = new BufferedInputStream(new FileInputStream(file));
        byte[] b = new byte[(int)file.length()];
        in.read(b);
        System.out.println(HexDump.dump(b, 0, 0));
        in.close();
    }
}
