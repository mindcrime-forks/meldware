package org.buni.meldware.mail.util.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

/**
 * Emulates a list of streams as a single stream.
 * 
 * @author Michael Barker
 */
public class ListInputStream extends InputStream {
    
    private final Iterator<? extends InputStream> itr;
    private InputStream in;
    
    public ListInputStream(Iterable<? extends InputStream> streams) {
        this.itr = streams.iterator();
        in = itr.next();
    }

    @Override
    public int read() throws IOException {
        
        int b = in.read();
        while (b == -1 && itr.hasNext()) {
            in = itr.next();
            b = in.read();
        }
        
        return b;
    }
    
    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        if (b == null) {
            throw new NullPointerException();
        } else if ((off < 0) || (off > b.length) || (len < 0) ||
               ((off + len) > b.length) || ((off + len) < 0)) {
            throw new IndexOutOfBoundsException();
        } else if (len == 0) {
            return 0;
        }
        
        int num = in.read(b, off, len);
        while (num < len && itr.hasNext()) {
            in = itr.next();
            try {
                num = num == -1 ? 0 : num;
                int n = in.read(b, off + num, len - num);                
                if (n != -1) {
                    num += n;
                }
            } catch (IndexOutOfBoundsException e) {
                System.out.printf("length = %d, off = %d, len = %d, num = %d", 
                        b.length, off, len, num);
                throw e;
            }
        }
        
        return num;
    }

    /**
     * @see java.io.InputStream#read(byte[])
     */
    @Override
    public int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    }

    /**
     * @see java.io.InputStream#skip(long)
     */
    @Override
    public long skip(long n) throws IOException {
        long num = in.skip(n);
        while (num < n && itr.hasNext()) {
            in = itr.next();
            num += in.skip(n - num);
        }
        return num;
    }
}
