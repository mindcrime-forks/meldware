package org.buni.meldware.mail.mailbox;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.buni.meldware.mail.MailException;
import org.buni.meldware.mail.api.FolderBody;
import org.buni.meldware.mail.api.FolderMessage;
import org.buni.meldware.mail.message.MailAddress;
import org.buni.meldware.mail.message.MailHeaders;
import org.buni.meldware.mail.message.MailHeadersImpl;
import org.buni.meldware.mail.util.io.Copier;
import org.buni.meldware.mail.util.io.SimpleCopier;
import org.buni.panto.BodyHeader;
import org.buni.panto.ContentType;
import org.buni.panto.HeaderParser;

public class FolderMessageProxy implements FolderMessage {
    
    private final MailboxService service;
    private final FolderEntry folderEntry;
    private final int seqNum;

    public FolderMessageProxy(MailboxService service, FolderEntry message, 
            int seqNum) {
        this.service = service;
        this.folderEntry = message;
        this.seqNum = seqNum;
    }

    public long getFolderId() {
        return folderEntry.getFolder().getId();
    }

    public long getUid() {
        return folderEntry.getUid();
    }
    
    public int getSeqNum() {
        return seqNum;
    }

    public boolean isAnswered() {
        return folderEntry.isAnswered();
    }

    public boolean isDeleted() {
        return folderEntry.isDeleted();
    }

    public boolean isFlagged() {
        return folderEntry.isFlagged();
    }

    public boolean isRecent() {
        return folderEntry.isRecent();
    }

    public boolean isSeen() {
        return folderEntry.isSeen();
    }

    public List<FolderBody> getBody() {
        List<MessageBody> bodies = service.getMessageBody(getMessage());
        List<FolderBody> result = new ArrayList<FolderBody>();
        for (MessageBody body : bodies) {
            result.add(new FolderBodyProxy(service, body));
        }
        return result;
    }

    public String getMessageId() {
        return getMessage().getMessageId();
    }

    public MailAddress getSender() {
        return getMessage().getSender();
    }
    
    public String getFrom() {
        return getMessage().getFrom();
    }

    public long getId() {
        return getMessage().getId();
    }
    
    public long getSize() {
        return getMessage().getSize();
    }

    public String getSubject() {
        return getMessage().getSubject();
    }

    public List<MailAddress> getTo() {
        return getMessage().getTo();
    }

    public boolean isMime() {
        return getMessage().isMime();
    }
    
    public boolean hasContent() {
        return true;
    }
    
    public void save() {
        service.updateFolderEntry(folderEntry);
    }

    public void write(OutputStream out, Copier copier) {
        service.mimePrintMessage(getMessage(), true, out, copier);
    }

    /**
     * @see org.buni.meldware.mail.api.FolderMessage#removeFlags(java.util.List)
     */
    public List<String> removeFlags(List<String> flags) {
        folderEntry.getFlagList().setFlags(false, flags);        
        service.updateFolderEntry(folderEntry);
        return folderEntry.getFlagList().getFlagList();
    }

    /**
     * TODO should this be delegated to the MailboxService.
     * 
     * @see org.buni.meldware.mail.api.FolderMessage#setFlags(boolean, java.util.List)
     */
    public List<String> setFlags(boolean isReplace, List<String> flags) {
        if (isReplace) {
            folderEntry.getFlagList().setFlags(flags);            
        } else {
            folderEntry.getFlagList().setFlags(true, flags);            
        }
        service.updateFolderEntry(folderEntry);
        return folderEntry.getFlagList().getFlagList();
    }
    
    public String getFlagString() {
        return folderEntry.getFlagList().toFlagString();
    }
    
    public String[] getHeaders() {
        return getMessage().getHeaders();
    }
    
    MailHeaders headers = null;
    
    public String getHeader(String name) {
        if (headers == null) {
            headers = MailHeadersImpl.create(getMessage().getHeaders());
        }
        String[] values = headers.getHeader(name);
        String value = null;
        if (values != null && values.length > 0) {
            value = values[0];
        }
        return value;
    }
    
    public String[] getHeaders(String name) {
        if (headers == null) {
            headers = MailHeadersImpl.create(getMessage().getHeaders());
        }
        String[] values = headers.getHeader(name);
        if (values == null) {
            values = new String[0];
        }
        return values;
    }

    public Date getTimestamp() {
        return getMessage().getTimestamp();
    }

    public boolean isSet(String flag) {
        return folderEntry.getFlagList().isSet(flag);
    }
    
    public void print(OutputStream out) {
        service.mimePrintMessage(getMessage(), true, out, new SimpleCopier());
    }
    
    public void print(OutputStream out, Copier copier) {
        service.mimePrintMessage(getMessage(), true, out, copier);
    }
    
    public void printText(OutputStream out) {
        service.mimePrintMessage(getMessage(), false, out, new SimpleCopier());
    }
    
    public void printText(OutputStream out, Copier copier) {
        service.mimePrintMessage(getMessage(), false, out, copier);
    }
    
    public long getHeaderSize() {
        return getMessage().getHeader().length();
    }
    
    public long getBodySize() {
        return getMessage().getSize() - (getMessage().getHeader().length() + 2);
    }
    
    public FolderBody getBodyPart(int i) {
        MessageBody body = service.getMessageBody(getMessage(), i);
        FolderBody result = null;
        if (body != null) {
            result = new FolderBodyProxy(service, body);
        }
        return result;
    }
    
    /**
     * Will iterate alony the supplied address loading message bodies.
     * TODO: Should this be delegated to the MailboxService.
     */
    public FolderBody getBodyPart(int[] address) {
        FolderBody result = null;
        
        if (address.length == 0) {
            throw new IllegalArgumentException("address.length == 0");
        }
        
        MessageBody bodyPart = service.getMessageBody(getMessage(), address[0]);
        for (int i = 1; i < address.length && bodyPart != null; i++) {
            bodyPart = service.getMessageBody(bodyPart, address[i]);
        }
        
        if (bodyPart != null) {
            result = new FolderBodyProxy(service, bodyPart);
        }
        
        return result;
    }
    
    public boolean isMessage() {
        return true;
    }

    private HeaderParser getHeaderParser() {
        return new HeaderParser();
    }
    
    public String getBoundary() {
        return getMessage().getBoundary();
    }
    
    private BodyHeader bodyHeader = null;
    public BodyHeader getBodyHeader() {
        if (bodyHeader == null) {
            try {
                if (getMessage().getHeader() != null) {
                    bodyHeader = getHeaderParser().parseHeader(getMessage().getHeader(), ContentType.DEFAULT);
                } else {
                    bodyHeader = new BodyHeader();
                }
            } catch (IOException e) {
                throw new MailException(e);
            }
        }
        return bodyHeader;
    }    
    
    public String getContentSubType() {
        return getBodyHeader().getContentType().getSubType();
    }

    public String getContentType() {
        return getBodyHeader().getContentType().getType();
    }

    public String getMimeType() {
        return getContentType() + "/" + getContentSubType();
    }

    MailHeaders mailHeaders = null;
    public MailHeaders getMailHeaders() {
        if (mailHeaders == null) {
            mailHeaders = MailHeadersImpl.create(getMessage().getHeaders());
        }
        return mailHeaders;
    }
    
    private MessageData getMessage() {
        // TODO: Handle lazy initialisation exception.
        return folderEntry.getMessage();
    }
    
}
