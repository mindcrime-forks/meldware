package org.buni.meldware.mail.util.io;

import java.io.IOException;
import java.io.OutputStream;

/**
 * This is a sadistically evil mechanism for limiting the number of bytes
 * written to the output stream, will throw an exception back to the caller
 * to long jump out of the calling code.
 * 
 */
public class SizeLimitedOutputStream extends OutputStream {
    
    private final int start;
    private final int end;
    private int pos = 0;
    private final OutputStream out;

    public SizeLimitedOutputStream(OutputStream out, int start, int size) {
        this.out = out;
        this.start = start;
        this.end = start + size;
    }
    
    /**
     * @see java.io.OutputStream#write(byte[], int, int)
     */
    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        
        if (pos >= end) {
            out.flush();
            throw LimitReachedException.instance();
        } else if (pos + len >= start) {
            int toSkip = Math.max(0, start - pos);
            int toTrunc = Math.max(0, (pos + len) - end);
            off += toSkip;
            len -= (toSkip + toTrunc);
            if (off < b.length) {
                out.write(b, off, len);
            }
            pos += (len + toSkip);
        } else {
            pos += len;
        }
    }

    /**
     * @see java.io.OutputStream#write(byte[])
     */
    @Override
    public void write(byte[] b) throws IOException {
        write(b, 0, b.length);
    }

    /**
     * @see java.io.OutputStream#write(int)
     */
    @Override
    public void write(int b) throws IOException {
        if (pos >= end) {
            out.flush();
            throw LimitReachedException.instance();
        } else if (pos >= start && pos < end) {
            out.write(b);
        }
        pos++;
    }

    // Perhaps even extend Throwable.
    public static class LimitReachedException extends RuntimeException {
        private static final long serialVersionUID = 1L;
        private final static LimitReachedException e = new LimitReachedException();
        public Throwable fillInStackTrace() {
            return null;
        }
        
        public static LimitReachedException instance() {
            return e;
        }
    }

}
