package org.buni.meldware.mail.imap4;

import java.io.IOException;
import java.io.InputStream;

/**
 * Copies at most length bytes from the underlying stream.
 * 
 * TODO: Implement block reading.
 * 
 * @author Michael Barker
 *
 */
public class SizeLimitedInputStream extends InputStream {
    
    private final InputStream in;
    private final long length;
    private long read = 0;

    public SizeLimitedInputStream(InputStream in, long length) {
        this.in = in;
        this.length = length;
    }

    @Override
    public int read() throws IOException {
        if (read >= length) {
            return -1;
        } else {
            read++;
            return in.read();
        }
    }

}
