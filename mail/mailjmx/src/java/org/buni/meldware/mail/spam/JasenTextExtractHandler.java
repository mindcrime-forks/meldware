package org.buni.meldware.mail.spam;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.buni.panto.BodyHeader;
import org.buni.panto.ContentHandler;

public class JasenTextExtractHandler implements ContentHandler {
    
    private StringBuilder text = new StringBuilder();
    private StringBuilder html = new StringBuilder();
    private List<String> attachmentNames = new ArrayList<String>();
    
    public String getText() {
        return text.toString();
    }
    
    public String getHtml() {
        return html.toString();
    }

    public void body(BodyHeader bd, InputStream in) throws IOException {
        StringBuilder sb;
        String type = bd.getContentType().getType();
        String subType = bd.getContentType().getSubType();
        if (type == null) {
            sb = text;
        } else if("html".equals(subType)) {
            sb = html;
        } else if("xhtml".equals(subType)) {
            sb = html;
        } else if ("text".equals(type)) {
            sb = text;
        } else {
            sb = null;
        }
        
        if (sb != null) {
            String charset = bd.getContentType().getParameters().get("charset");
            charset = charset != null ? charset : "US-ASCII";
            Charset cs;
            try {
                cs = Charset.forName(charset);
            } catch (Exception e) {
                cs = Charset.defaultCharset();
            }
            Reader r = new InputStreamReader(in, cs);
            char[] data = new char[8192];
            int numChars = 0;
            while((numChars = r.read(data)) != -1) {
                sb.append(data, 0, numChars);
            }                
            sb.append("\r\n");
        } else {
            byte[] b = new byte[8192];
            while (in.read(b) != -1);
        }
        
        addFilename(bd);
    }
    
    private void addFilename(BodyHeader h) {
        String filename = h.getContentDisposition().getParameters().get("filename");
        if (filename == null) {
            filename = h.getContentType().getParameters().get("name");
        }
        if (filename != null) {
            attachmentNames.add(filename);
        }
    }

    public void endBodyPart() {
    }

    public void endHeader() {
    }

    public void endMessage() {
    }

    public void endMultipart() {
    }

    public void epilogue(InputStream in) throws IOException {
        byte[] b = new byte[8192];
        while (in.read(b) != -1);
    }

    public void field(String arg0) {
    }

    public void preamble(InputStream in) throws IOException {
        byte[] b = new byte[8192];
        while (in.read(b) != -1);
    }

    public void raw(InputStream arg0) throws IOException {
    }

    public void startBodyPart() {
    }

    public void startHeader() {
    }

    public void startMessage() {
    }

    public void startMultipart(BodyHeader arg0) {
    }
    
    public String[] getAttachmentNames() {
        return attachmentNames.toArray(new String[0]);
    }

}
