package org.buni.meldware.mail.util.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.buni.meldware.mail.StreamReadException;
import org.buni.meldware.mail.StreamWriteException;

/**
 * Copies a partial chunk of an input stream to the output stream.
 * 
 * @author Michael Barker
 *
 */
public class PartialCopier implements Copier {

    private final int off;
    private int bytesleft;
    private final int len;

    public PartialCopier(int off, int len) {
        this.off = off;
        this.bytesleft = len;
        this.len = len;
    }
    
    public int copy(InputStream in, OutputStream out, int blockSize) {
        
        try {
            in.skip(off);
        } catch (IOException e) {
            throw new StreamReadException(e.getMessage(), e);
        }
        
        byte[] buffer = new byte[Math.max(blockSize, MIN_BLOCK_SIZE)];
        int bytesread = 0;
        
        //int bytesleft = len;
        do {
            int toRead = Math.min(buffer.length, bytesleft);
            try {
                bytesread = in.read(buffer, 0, toRead);
            } catch (IOException e) {
                throw new StreamWriteException(e.getMessage(), e);
            }
            
            if (bytesread > 0) {
                try {
                    out.write(buffer, 0, bytesread);               
                } catch (IOException e) {
                    throw new StreamWriteException(e.getMessage(), e);
                }                
            }
            bytesleft -= bytesread;
        } while (bytesleft > 0 && bytesread > -1);
                
        // this is naughty...we could store crap > 2g via imap
        // ...unlikely but we could...
        return (int) (len - bytesleft); 
    }

}
