package org.buni.meldware.mail.mailbox;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.buni.meldware.mail.MailException;
import org.buni.meldware.mail.api.FolderBody;
import org.buni.meldware.mail.message.MailHeaders;
import org.buni.meldware.mail.message.MailHeadersImpl;
import org.buni.meldware.mail.store.StoreException;
import org.buni.meldware.mail.util.io.Copier;
import org.buni.meldware.mail.util.io.SimpleCopier;
import org.buni.panto.BodyHeader;
import org.buni.panto.ContentType;
import org.buni.panto.HeaderParser;

public class FolderBodyProxy implements FolderBody {

    private MailboxService service;
    private MessageBody body;

    public FolderBodyProxy(MailboxService service, MessageBody body) {
        this.service = service;
        this.body = body;
    }

    public long getSize() throws StoreException {
        return body.getSize();
    }

    public Long getStoreId() throws StoreException {
        return body.getStoreId();
    }

    public void setSize(int size) {
        // TODO This should go away.
    }

    public String getMimeheader() {
        return body.getMimeheader();
    }
    
    public String[] getHeaders() {
        String[] headers = null;
        String header = body.getHeader();
        if (header != null) {
            headers = header.split("\r\n");
        }
        return headers;
    }
    
    public boolean isMessage() {
        return body.getHeader() != null && body.getHeader().length() > 0;
    }
    
    public boolean isMime() {
        return body.getMimeheader() != null;
    }
    
    public boolean hasContent() {
        return !body.getBodyless();
    }
    
    public void print(OutputStream out, Copier copier) {
        service.mimePrintBody(body.getId(), true, out, copier);
    }
    
    public void print(OutputStream out) {
        printText(out, new SimpleCopier());
    }
    
    public void printText(OutputStream out, Copier copier) {
        service.mimePrintBody(body.getId(), false, out, copier);
    }
    
    public void printText(OutputStream out) {
        printText(out, new SimpleCopier());
    }
    
    public FolderBody getBodyPart(int i) {
        FolderBody result = null;
        MessageBody bodyPart = service.getMessageBody(body, i);
        if (bodyPart != null) {
            result = new FolderBodyProxy(service, bodyPart);
        }
        return result;
    }
    
    public long getBodySize() {
        return (body.getPartSize() - getHeaderSize());
    }
    
    public int getHeaderSize() {
        String header = body.getHeader();
        if (header == null) {
            return 0;
        } else {
            return header.length();
        }
    }

    public String getContentSubType() {
        return getMimeType().split("/")[1];
    }

    public String getContentType() {
        return getMimeType().split("/")[0];
    }

    public String getMimeType() {
        return body.getMimeType();
    }

    public String getBoundary() {
        return body.getBoundary();
    }
    
    public String getCharset() {
        return getBodyHeader().getContentType().getParameters().get("charset");
    }
    
    public String getFormat() {
        return getBodyHeader().getContentType().getParameters().get("flowed");
    }
    
    public String getName() {
        return getBodyHeader().getContentType().getParameters().get("name");
    }
    
    public String getContentTransferEncoding() {
        return getBodyHeader().getContentTransferEncoding().getType();
    }
    
    public String getContentDisposition() {
        return getBodyHeader().getContentDisposition().getType();
    }
    
    public String getFilename() {
        return getBodyHeader().getContentDisposition().getParameters().get("filename");
    }
    
    private BodyHeader bodyHeader = null;
    private BodyHeader getBodyHeader() {
        if (bodyHeader == null) {
            try {
                if (body.getMimeheader() != null) {
                    bodyHeader = getHeaderParser().parseHeader(body.getMimeheader(), ContentType.DEFAULT);
                } else {
                    bodyHeader = new BodyHeader();
                }
            } catch (IOException e) {
                throw new MailException(e);
            }
        }
        return bodyHeader;
    }
    
    private HeaderParser getHeaderParser() {
        return new HeaderParser();
    }
    

    public BodyType getType() {
        return body.getType();
    }

    public List<FolderBody> getChildren() {
        List<FolderBody> children = new ArrayList<FolderBody>(body.getChildren().size());
        for (MessageBody child : body.getChildren()) {
            children.add(new FolderBodyProxy(service, child));
        }
        return children;
    }
    
    MailHeaders mailHeaders = null;
    public MailHeaders getMailHeaders() {
        if (mailHeaders == null && body.getHeader() != null) {
            mailHeaders = MailHeadersImpl.create(body.getHeader().split("\r\n"));
        }
        return mailHeaders;
    }    
    
    public boolean isMultipart() {
        return "multipart".equals(getContentType().toLowerCase());
    }
}
