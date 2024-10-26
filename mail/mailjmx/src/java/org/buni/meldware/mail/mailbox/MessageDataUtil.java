package org.buni.meldware.mail.mailbox;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.mail.internet.MailDateFormat;

import org.buni.meldware.common.logging.Log;
import org.buni.meldware.mail.MailException;
import org.buni.meldware.mail.message.Body;
import org.buni.meldware.mail.message.Mail;
import org.buni.meldware.mail.message.MailBodyManager;
import org.buni.meldware.mail.message.MailHeaders;
import org.buni.meldware.mail.util.io.Copier;
import org.buni.meldware.mail.util.io.ListInputStream;
import org.buni.meldware.mail.util.io.SimpleCopier;
import org.buni.panto.MimeParser;

/**
 * Factory object for create MessasgeData objects.
 * 
 * @author Michael Barker
 */
public class MessageDataUtil {

    private final static Log log = Log.getLog(MessageDataUtil.class);
    
    private final MailBodyManager mgr;

    public MessageDataUtil(MailBodyManager mgr) {
        this.mgr = mgr;
    }
    
    public MessageData create(Mail mail, boolean parseMime) {
        MessageData md;
        try {
            // Makes sure that the mail has a date header.
            MailDateFormat df = new MailDateFormat();
            MailHeaders headers = mail.getMailHeaders();
            String tsraw[] = headers.getHeader("Date");
            if (tsraw == null || tsraw.length == 0) {
                headers.addHeader("Date", df.format(new Date()));// now
            }
            
            if (parseMime && mail.isMime()) {
                InputStream is = mail.getRawStream(mgr);
                md = createMimeMessage(is);
                md.setHeaders(mail.getMailHeaders());
            } else {
                md = new MessageData(mail);
            }
        } catch (MailException e) {
            // Fall back to a standard mail if parse fails.
            log.warn(e, "Mime parse failed, building simple message: %s", e);
            md = new MessageData(mail);
        } catch (IOException e) {
            // Fall back to a standard mail if parse fails.
            log.warn("Mime parse failed, building simple message: %s", e);
            md = new MessageData(mail);
        }
        return md;        
    }
    
    
    public MessageData createMimeMessage(InputStream is) throws IOException {
        Copier c = new SimpleCopier();
        PantoContentHandler mch = new PantoContentHandler(mgr, c);
        MimeParser p = new MimeParser();
        p.setContentHandler(mch);
        p.parse(is);
        MessageData md = mch.getMessage();
        md.setMime(md.getBoundary() != null);
        setSizes(md);
        
        return md;
    }
    
    private final static byte[] ENDL = { '\r', '\n' };
    
    public void printMessage(MessageData message, boolean includeHeaders, 
            OutputStream out, Copier copier) throws IOException {
        
        if (includeHeaders) {
            // Write out the mail headers.
            out.write((message.getHeader()).getBytes());
            out.write(ENDL);
        }

        if (message.isMime()) {
            // Write out the mime body.
            writeln(message.getMimePreamble(), out);
            
            String boundary = message.getBoundary();
            for (MessageBody body : message.getBody()) {
                out.write(("--" + boundary + "\r\n").getBytes(Mail.ENCODING));
                
                out.write(body.getMimeheader().getBytes(Mail.ENCODING));
                out.write(ENDL);
                
                printBodyPart(body, true, out, copier);
                
                if (!body.hasChildren()) {
                    out.write(ENDL);        
                }                    
            }
            out.write(("--" + boundary + "--\r\n").getBytes(Mail.ENCODING));

            writeln(message.getEpilogue(), out);
        } else {
            // Write out a simple text body.
            if (message.getBody().size() > 0) {
                mgr.write(message.getBody().get(0), out, copier);
            } else {
                throw new MailException("Message has no body");
            }
        }
    }
    
    public void printBodyPart(MessageBody body, boolean includeHeaders, OutputStream out, Copier c) 
    throws IOException {
        
        // Write out the mail headers if they exist.
        String header = body.getHeader();
        if (header != null && header.length() > 0 && includeHeaders) {
            out.write(header.getBytes(Mail.ENCODING));
            out.write(ENDL);
        }
        
        switch (body.getType()) {
        case MULTIPART:
            writeln(body.getPreamble(), out);
            
            // MessageBody does not prepend '--' to the boundary.
            for (MessageBody child : body.getChildren()) {
                out.write(("--" + body.getBoundary() + "\r\n").getBytes(Mail.ENCODING));
                
                out.write(child.getMimeheader().getBytes(Mail.ENCODING));
                out.write(ENDL);
                                
                printBodyPart(child, true, out, c);
                
                if (!child.hasChildren()) {
                    out.write(ENDL);        
                }
            }
            // MessageBody does not prepend '--' to the boundary.
            out.write(("--" + body.getBoundary() + "--\r\n").getBytes(Mail.ENCODING));
            
            writeln(body.getEpilogue(), out);
            break;
            
        case MESSAGE:
            // There should only be one body part in this case.
            int size = body.getChildren().size();
            if (size != 1) {
                log.warn("Strange message simple text message with %d parts", size);
            }
            for (MessageBody child : body.getChildren()) {
                printBodyPart(child, true, out, c);
                out.write(ENDL);        
            }
            break;
            
        default:
            Body b = mgr.createMailBody(Long.valueOf(body.getBodyId()));
            mgr.write(b, out, c);
        }
        
    }
    
    public void addStream(List<InputStream> streams, String s) {
        streams.add(new ByteArrayInputStream(encode(s)));
    }
    
    public void addStreamLn(List<InputStream> streams, String s) {
        if (s != null) {
            streams.add(new ByteArrayInputStream(encode(s + "\r\n")));
        } else {
            streams.add(new ByteArrayInputStream(ENDL));
        }
    }
    
    public void addStream(List<InputStream> streams, byte[] b) {
        streams.add(new ByteArrayInputStream(b));
    }
    
    public byte[] encode(String s) {
        try {
            return s.getBytes(Mail.ENCODING);
        } catch (UnsupportedEncodingException e) {
            throw new MailException(e);
        }
    }
    
    public InputStream getInputStream(MessageData message, boolean includeHeaders) {
        
        List<InputStream> streams = new ArrayList<InputStream>();
        
        if (includeHeaders) {
            // Write out the mail headers.
            addStreamLn(streams, message.getHeader());
        }

        if (message.isMime()) {
            // Write out the mime body.
            addStreamLn(streams, message.getMimePreamble());
            
            String boundary = message.getBoundary();
            for (MessageBody body : message.getBody()) {
                addStreamLn(streams, "--" + boundary);
                addStreamLn(streams, body.getMimeheader());
                
                getInputStream(streams, body, true);
                
                if (!body.hasChildren()) {
                    addStream(streams, ENDL);
                    //out.write(ENDL);  
                }                    
            }
            addStreamLn(streams, "--" + boundary + "--");
            //out.write(("--" + boundary + "--\r\n").getBytes(Mail.ENCODING));

            addStreamLn(streams, message.getEpilogue());
            //writeln(message.getEpilogue(), out);
        } else {
            // Write out a simple text body.
            if (message.getBody().size() > 0) {
                streams.add(mgr.getInputStream(message.getBody().get(0)));
            } else {
                throw new MailException("Message has no body");
            }
        }
        
        return new ListInputStream(streams);
    }
    
    private void getInputStream(List<InputStream> streams, MessageBody body, 
            boolean includeHeaders) {
        // Write out the mail headers if they exist.
        String header = body.getHeader();
        if (header != null && header.length() > 0 && includeHeaders) {
            addStreamLn(streams, body.getHeader());
        }
        
        switch (body.getType()) {
        case MULTIPART:
            addStreamLn(streams, body.getPreamble());
            //writeln(body.getPreamble(), out);
            
            // MessageBody does not prepend '--' to the boundary.
            for (MessageBody child : body.getChildren()) {
                addStreamLn(streams, "--" + body.getBoundary());
                //out.write(("--" + body.getBoundary() + "\r\n").getBytes(Mail.ENCODING));
                
                addStreamLn(streams, child.getMimeheader());
                //out.write(child.getMimeheader().getBytes(Mail.ENCODING));
                //out.write(ENDL);
                                
                getInputStream(streams, child, true);
                
                if (!child.hasChildren()) {
                    addStream(streams, ENDL);
                    //out.write(ENDL);
                }
            }
            // MessageBody does not prepend '--' to the boundary.
            addStreamLn(streams, "--" + body.getBoundary() + "--");
            //out.write(("--" + body.getBoundary() + "--\r\n").getBytes(Mail.ENCODING));
            
            addStreamLn(streams, body.getEpilogue());
            //writeln(body.getEpilogue(), out);
            break;
            
        case MESSAGE:
            // There should only be one body part in this case.
            int size = body.getChildren().size();
            if (size != 1) {
                log.warn("Strange message simple text message with %d parts", size);
            }
            for (MessageBody child : body.getChildren()) {
                getInputStream(streams, child, true);
                //printBodyPart(child, true, out, c);
                addStream(streams, ENDL);
                //out.write(ENDL);        
            }
            break;
            
        default:
            //Body b = mgr.createMailBody(Long.valueOf(body.getBodyId()));
            //streams.add(mgr.getInputStream(body));
            streams.add(new MessageBodyInputStream(body, mgr));
        }        
    }
    
    
    private void writeln(String value, OutputStream out) throws IOException {
        if (value != null) {
            out.write(value.getBytes(Mail.ENCODING));                     
        }
        out.write(ENDL);        
    }
    
    
    /**
     * Set the size of the message data object and all of its ancestor body
     * parts based on its mime structure.
     * 
     * @param md
     */
    private void setSizes(MessageData md) {
        
        long size = 0;
        size += md.getHeader().length();
        size += ENDL.length;
        
        if (md.isMime()) {
            size += getLength(md.getMimePreamble());
            size += ENDL.length;
            
            for (MessageBody mb : md.getBody()) {
                size += "--".length();
                size += md.getBoundary().length();
                size += ENDL.length;
                
                String mh = mb.getMimeheader();
                long mimeHeaderSize = (mh != null) ? mh.length() + 2 : 2;
                size += mimeHeaderSize;
                
                setSizes(mb);
                
                if (!mb.hasChildren()) {
                    size += 2;
                }
                
                size += mb.getPartSize();
            }
            
            size += "--".length();
            size += md.getBoundary().length();
            size += "--".length();
            size += ENDL.length;
            
            // XXX: Assuming 7 bit ascii.
            size += getLength(md.getEpilogue());
            size += ENDL.length;
        } else {
            size += md.getBody().get(0).getSize();
        }
        
        md.setSize(size);
    }
    
    /**
     * Recursively set body sizes.  The size of a body excludes its own mime
     * header, but will include a message header if it has one.
     * 
     * @param mb
     */
    private void setSizes(MessageBody mb) {

        long size = 0;
        // Write out the mail headers if they exist.
        String h = mb.getHeader();
        long headerSize = (h != null && h.length() > 0) ? h.length() + 2 : 0;
        size += headerSize;
        
        switch (mb.getType()) {
        case MULTIPART:
            size += getLength(mb.getPreamble());
            size += ENDL.length;
            
            for (MessageBody child : mb.getChildren()) {
                // MessageBody does not prepend '--' to the boundary.
                size += "--".length();
                size += mb.getBoundary().length();
                size += ENDL.length;
                
                String mh = child.getMimeheader();
                long mimeHeaderSize = (mh != null) ? mh.length() + 2 : 2;
                size += mimeHeaderSize;
                
                setSizes(child);
                
                if (!child.hasChildren()) {
                    size += 2;
                }                
                
                size += child.getPartSize();
            }
        
            size += "--".length();
            size += mb.getBoundary().length();
            size += "--".length();
            size += ENDL.length;
            
            // XXX: Assuming US-ASCII.
            size += getLength(mb.getEpilogue());
            size += ENDL.length;
            
            mb.setPartSize(size);
            break;
            
        case MESSAGE:
            // There should only be one simple body part in this case.
            for (MessageBody child : mb.getChildren()) {
                setSizes(child);
                size += child.getPartSize();
                size += ENDL.length;
            }
            mb.setPartSize(size);
            break;
            
        default:
            // Part size already set for simple parts.
        }
    }
    
    private static class MessageBodyInputStream extends InputStream {
        private final MessageBody body;
        private InputStream in = null;
        private MailBodyManager mgr;

        public MessageBodyInputStream(MessageBody body, MailBodyManager mgr) {
            this.body = body;
            this.mgr = mgr;
        }
        
        private InputStream getIn() {
            if (in == null) {
                in = mgr.getInputStream(body);
            }
            return in;
        }

        @Override
        public int read() throws IOException {
            return getIn().read();
        }

        /**
         * @see java.io.InputStream#available()
         */
        @Override
        public int available() throws IOException {
            return getIn().available();
        }

        /**
         * @see java.io.InputStream#close()
         */
        @Override
        public void close() throws IOException {
            getIn().close();
        }

        /**
         * @see java.io.InputStream#mark(int)
         */
        @Override
        public synchronized void mark(int readlimit) {
            getIn().mark(readlimit);
        }

        /**
         * @see java.io.InputStream#markSupported()
         */
        @Override
        public boolean markSupported() {
            return getIn().markSupported();
        }

        /**
         * @see java.io.InputStream#read(byte[], int, int)
         */
        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            return getIn().read(b, off, len);
        }

        /**
         * @see java.io.InputStream#read(byte[])
         */
        @Override
        public int read(byte[] b) throws IOException {
            return getIn().read(b);
        }

        /**
         * @see java.io.InputStream#reset()
         */
        @Override
        public synchronized void reset() throws IOException {
            getIn().reset();
        }

        /**
         * Checks the size of the message body and will skip the
         * entire thing if required.
         */
        @Override
        public long skip(long n) throws IOException {
            long skipped;
            if (in == null && n > body.getPartSize()) {
                skipped = body.getPartSize();
                in = new ByteArrayInputStream(new byte[0]);
            } else {
                skipped = getIn().skip(n);
            }
            return skipped;
        }
    }
    
    private int getLength(String s) {
        if (s == null) {
            return 0;
        }
        return s.length();
    }    
}
