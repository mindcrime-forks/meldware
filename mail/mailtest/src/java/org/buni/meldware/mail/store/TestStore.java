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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.Arrays;

import javax.naming.NamingException;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.buni.meldware.mail.MailUtil;
import org.buni.meldware.test.JMXTestWrapper;
import org.buni.meldware.mail.util.HexDump;
import org.buni.meldware.mail.util.io.Copier;
import org.buni.meldware.mail.util.io.DotUnstuffingCopier;
import org.buni.meldware.mail.util.io.SimpleCopier;
import org.jboss.logging.Logger;

/**
 * @author Michael Barker <mailto:mike@middlesoft.co.uk>
 * 
 */
public class TestStore extends TestCase {
    Logger log = Logger.getLogger(TestStore.class);
    
    public static TestSuite suite() {
        return JMXTestWrapper.suite(TestStore.class);
    }

    /**
     * @param name
     */
    public TestStore(String name) {
        super(name);
    }

    public Store getStore() throws StoreException {
        return MailUtil.getMailBodyManager().getStore();
    }

    public void testStoreByteArray01() throws Exception {
        int bufSize = 1024;

        Store store = getStore();

        StoreItem item = store.createStoreItem();

        byte[] outBuf = new byte[bufSize];

        for (int i = 0; i < outBuf.length; i++) {
            byte b = (byte) ((i % 26) + 97);
            outBuf[i] = b;
        }

        long t0 = System.currentTimeMillis();
        OutputStream out = item.getOuputStream();
        out.write(outBuf, 0, outBuf.length);
        out.flush();
        out.close();
        long t1 = System.currentTimeMillis();

        System.out.println("Time to write: " + (t1 - t0));

        byte[] inBuf = new byte[bufSize];
        long t2 = System.currentTimeMillis();
        InputStream in = item.getInputStream();
        in.read(inBuf, 0, inBuf.length);
        long t3 = System.currentTimeMillis();

        System.out.println("Time to read: " + (t3 - t2));

        for (int i = 0; i < inBuf.length; i++) {
            assertEquals("Value " + i + " is incorrect", inBuf[i], outBuf[i]);            
        }

        item.delete();
    }

    public void testStoreByteArray02() throws Exception {
        Store store = getStore();

        StoreItem item = store.createStoreItem();

        byte[] outBuf = new byte[256];

        for (int i = 0; i < outBuf.length; i++) {
            outBuf[i] = (byte) (i & 0x000000ff);
        }

        OutputStream out = item.getOuputStream();
        out.write(outBuf, 0, outBuf.length);
        out.flush();
        out.close();

        byte[] inBuf = new byte[outBuf.length];
        InputStream in = item.getInputStream();
        in.read(inBuf, 0, inBuf.length);
        //store.read(id, item.getStartIndex(), inBuf, 0, inBuf.length);

        for (int i = 0; i < outBuf.length; i++) {
            if (outBuf[i] != inBuf[i]) {
                System.out.println("in: " + outBuf[i] + ", out: " + inBuf[i]);
            }
        }

        for (int i = 0; i < inBuf.length; i++) {
            assertEquals("Value " + i + " is incorrect", inBuf[i], outBuf[i]);            
        }
    }

    /**
     * Write a single byte to the output stream and read it back again.
     * 
     * @throws StoreException
     * @throws SQLException
     * @throws NamingException
     * 
     * @throws StoreItemNotFoundException
     * @throws NamingException
     * @throws SQLException
     * @throws IOException
     * @throws IOException
     * @throws Exception
     */
    public void testBlobWriteByte() throws Exception {
        StoreItem item = getStoreItem();
        OutputStream out = item.getOuputStream();

        int value = 'a';
        out.write('a');
        out.flush();
        out.close();

        InputStream in = item.getInputStream();
        int result = in.read();

        assertEquals(value, result);
    }

    /**
     * @return
     * @throws StoreException
     */
    private StoreItem getStoreItem() throws StoreException {
        return getStore().createStoreItem();
    }

    /**
     * Write an array of bytes to the output stream and read them back again.
     * 
     * @throws StoreItemNotFoundException
     * @throws NamingException
     * @throws SQLException
     * @throws IOException
     * @throws Exception
     */
    public void testBlobWriteByteArrayTwiceBufferSize() throws Exception {
        StoreItem item = getStoreItem();
        OutputStream out = item.getOuputStream();

        byte[] bs = new byte[2048];

        for (int i = 0; i < bs.length; i++) {
            bs[i] = (byte) ((i % 26) + 97);
        }

        out.write(bs);
        out.flush();
        out.close();

        InputStream in = item.getInputStream();

        byte retBs[] = new byte[2048];
        int numRead = in.read(retBs);

        for (int i = 0; i < bs.length && i < retBs.length; i++) {
            assertEquals("Index " + i + " is not the same.", bs[i], retBs[i]);
        }

        assertEquals(2048, numRead);
        assertTrue(Arrays.equals(bs, retBs));
    }

    /**
     * Write an array of bytes to the output stream and read them back again.
     * 
     * The size of the input will not be a multiple of the size of the buffer.
     * 
     * @throws Exception
     * 
     * @throws NamingException
     * @throws SQLException
     */
    public void testBlobWriteByteArrayNotMultipleOfBufferSize()
            throws Exception {
        StoreItem item = getStoreItem();
        OutputStream out = item.getOuputStream();

        byte[] bs = new byte[2048];

        for (int i = 0; i < bs.length; i++) {
            bs[i] = (byte) ((i % 26) + 97);
        }

        out.write(bs);
        out.flush();
        out.close();

        InputStream in = item.getInputStream();
        byte retBs[] = new byte[2048];
        int numRead = in.read(retBs);

        for (int i = 0; i < bs.length && i < retBs.length; i++) {
            if (bs[i] != retBs[i]) {
                assertFalse("Index " + i + " is not the same.", true);
                break;
            }
        }

        assertEquals(2048, numRead);
        assertTrue(Arrays.equals(bs, retBs));
    }

    /**
     * Write an array of bytes to the output stream and read them back again.
     * 
     * @throws Exception
     * 
     * @throws NamingException
     */
    public void testBlob65536ByteBuffers() throws Exception {
        int bufSize = 65535;
        int loops = 128;

        System.out.println("Total bytes: " + bufSize);
        StoreItem item = getStoreItem();
        OutputStream out = item.getOuputStream();

        int index = 0;

        byte[] bs = new byte[bufSize];

        for (int i = 0; i < bs.length; i++) {
            bs[i] = (byte) ((i % 26) + 97);
        }

        for (int i = 0; i < loops; i++) {
            out.write(bs);
        }
        out.flush();
        out.close();

        byte retBs[] = new byte[bufSize];
        InputStream in = item.getInputStream();

        for (int i = 0; i < loops; i++) {
            in.read(retBs);
            for (int j = 0; j < retBs.length; j++) {
                if (bs[j] != retBs[j]) {
                    index = ((i * retBs.length) + j);
                    assertEquals("Index: " + index, bs[j], retBs[j]);
                }
            }
            assertTrue(Arrays.equals(bs, retBs));
        }

        System.out.println("Index: " + index);
    }

    public void testUniCode() throws IOException {

        StringBuffer sb = new StringBuffer();

        sb.append(' ');
        sb.append('\u0401');
        sb.append('\u0401');
        sb.append('\u0401');
        sb.append('\u0401');
        sb.append('\u0401');
        sb.append('\u0401');
        sb.append('\u0401');
        sb.append(' ');
        String s = sb.toString();
        byte[] input = s.getBytes("UTF-8");

        StoreItem item = getStoreItem();
        OutputStream out = item.getOuputStream();
        out.write(input);
        out.flush();
        out.close();

        InputStream in = item.getInputStream();
        byte[] output = new byte[1024];
        int numRead = in.read(output);

        String s2 = new String(output, 0, numRead, "UTF-8");

        System.out.println("Input size: " + input.length + " Output size: "
                + numRead);
        System.out.println("Input: " + HexDump.dump(input, input.length, 0));
        System.out.println("Output: " + HexDump.dump(output, numRead, 0));

        assertEquals(s, s2);

    }

    public void testMsgFile() throws Exception {
        InputStream in = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("test5.msg");
        byte[] expected = readAll(in);

        StoreItem item = getStoreItem();
        OutputStream out = item.getOuputStream();
        Copier c = new SimpleCopier();
        c.copy(new ByteArrayInputStream(expected), out, 8192);
        in.close();
        out.flush();
        out.close();

        byte[] retBs = readAll(item.getInputStream());

        for (int j = 0; j < retBs.length; j++) {
            if (expected[j] != retBs[j]) {
                assertEquals("Index: " + j, expected[j], retBs[j]);
            }
        }
    }

    public byte[] readAll(InputStream in) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        new SimpleCopier().copy(in, baos, 8192);
        return baos.toByteArray();
    }

    public void compare(String message, InputStream expected,
            InputStream actual, int size) {

    }

    /**
     * Write an array of bytes to the output stream and read them back again.
     * 
     * @throws Exception
     * 
     * @throws NamingException
     */
    public void testBlob8KBuffers() throws Exception {
        int bufSize = 8192;
        int loops = 10;

        System.out.println("Total bytes: " + bufSize);
        StoreItem item = getStoreItem();
        OutputStream out = item.getOuputStream();

        byte[] bs = new byte[bufSize];

        for (int i = 0; i < bs.length; i++) {
            bs[i] = (byte) ((i % 26) + 97);
        }

        for (int i = 0; i < loops; i++) {
            out.write(bs);
        }
        out.flush();
        out.close();

        byte retBs[] = new byte[bufSize];
        InputStream in = item.getInputStream();

        for (int i = 0; i < loops; i++) {
            in.read(retBs);
            for (int j = 0; j < retBs.length; j++) {
                assertEquals("Loop: " + i + ", idx: " + j, bs[j], retBs[j]);
            }
            assertTrue(Arrays.equals(bs, retBs));
        }
    }

    public void testCopy() throws Exception {
        byte[] data = new byte[128000];
        for (int i = 0; i < data.length - 5; i++) {
            data[i] = (byte) ((i % 26) + 97);
        }

        data[data.length - 5] = (byte) '\r';
        data[data.length - 4] = (byte) '\n';
        data[data.length - 3] = (byte) '.';
        data[data.length - 2] = (byte) '\r';
        data[data.length - 1] = (byte) '\n';

        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        StoreItem item = getStoreItem();
        OutputStream out = item.getOuputStream();

        Copier c = new DotUnstuffingCopier();

        c.copy(bais, out, 1024);
        out.flush();
        out.close();

        Copier c2 = new SimpleCopier();

        InputStream in = item.getInputStream();
        c2.copy(in, baos, 1024);

        byte[] data2 = baos.toByteArray();

        assertEquals("Length is wrong", data.length - 5, data2.length);

        for (int i = 0; i < data.length && i < data2.length; i++) {
            assertEquals("Index i = " + i, data[i], data2[i]);
        }
    }

}
