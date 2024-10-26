package org.buni.meldware.mail.mailbox;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.buni.meldware.mail.message.Body;
import org.buni.meldware.mail.message.Mail;
import org.buni.meldware.mail.message.MailBodyManager;
import org.buni.meldware.mail.util.TextUtil;
import org.buni.meldware.mail.util.io.Copier;
import org.buni.meldware.mail.util.io.IOUtil;
import org.buni.panto.BodyHeader;
import org.buni.panto.ContentHandler;
import org.buni.panto.ContentType;

/**
 * Content handler for Meldware Mime Parser.  Builds MessageData/MesssageBody
 * objects directly. Creates a MessageData as the root object.  Any nested 
 * Message Objects are created as MessageBody objects.
 * 
 * @author Michael Barker
 *
 */
public class PantoContentHandler implements ContentHandler {
    
    MessageData root = null;
    MyStack<MessageBody> bodyStack = new MyStack<MessageBody>();
    StringBuilder currentHeader = new StringBuilder();
    boolean isMessage = false;
    private MailBodyManager mgr;
    private Copier copier;
    
    public PantoContentHandler(MailBodyManager mgr, Copier copier) {
        this.mgr = mgr;
        this.copier = copier;
    }
    
    public MessageData getMessage() {
        return root;
    }
    
    public void startMessage() {
        isMessage = true;
        if (root == null) {
            root = new MessageData();
            //root.setMime(true);
        }
    }
    
    public void endMessage() {
        isMessage = false;
    }

    public void startHeader() {
        int len = currentHeader.length();
        if (len > 0) {
            currentHeader.delete(0, len);
        }
    }

    public void field(String fieldData) {
        if (fieldData.trim().length() > 0) {
            currentHeader.append(TextUtil.trimRight(fieldData));
            currentHeader.append("\r\n");
        }
    }
    
    public void endHeader() {
        if (isMessage) {
            if (bodyStack.isEmpty()) {
                root.setHeader(currentHeader.toString());
            } else {
                bodyStack.peek().setHeader(currentHeader.toString());
            }
        } else {
            bodyStack.peek().setMimeheader(currentHeader.toString());
        }
    }
    
    public void startMultipart(BodyHeader header) {
        if (!bodyStack.isEmpty()) {
            MessageBody mb = bodyStack.peek();
            mb.setMimeType(getMimeType(header.getContentType()));
            mb.setBoundary(header.getContentType().getBoundary());
        } else {
            root.setBoundary(header.getContentType().getBoundary());
        }
    }

    public void startBodyPart() {
        isMessage = false;
        MessageBody mb = new MessageBody();
        mb.setBodyless(true);
        bodyStack.push(mb);
    }
    
    public void body(BodyHeader bd, InputStream is) throws IOException {
        Body body = mgr.createMailBody();
        mgr.read(body, is, copier);
        MessageBody mb;
        if (isMessage) {
            mb = new MessageBody();
            if (bodyStack.isEmpty()) {
                root.addMessageBody(mb);
            } else {
                bodyStack.peek().addMessageBody(mb);
            }
        } else {
            mb = bodyStack.peek();
        }
        mb.setBodyId(body.getStoreId().longValue());
        mb.setSize((int) body.getSize());
        mb.setBodyless(false);
        mb.setMimeType(getMimeType(bd.getContentType()));
    }
    
    private String getMimeType(ContentType ct) {
        return ct.getType() + "/" + ct.getSubType();
    }
    
    public void endBodyPart() {
        MessageBody body = bodyStack.pop();
        if (bodyStack.isEmpty()) {
            root.addMessageBody(body);
        } else {
            bodyStack.peek().addMessageBody(body);
        }
    }

    public void endMultipart() {
    }

    public void epilogue(InputStream is) throws IOException {
        String epilogue = IOUtil.toString(is, Mail.ENCODING);
        if (bodyStack.isEmpty()) {
            root.setEpilogue(epilogue);
        } else {
            bodyStack.peek().setEpilogue(epilogue);
        }
    }

    public void preamble(InputStream is) throws IOException {
        String preamble = IOUtil.toString(is, Mail.ENCODING);
        if (bodyStack.isEmpty()) {
            root.setMimePreamble(preamble);
        } else {
            bodyStack.peek().setPreamble(preamble);
        }
    }

    public void raw(InputStream is) throws IOException {
    }

    /**
     * Simple stack on top of an array list.
     *
     * @param <E>
     */
    private static class MyStack<E> extends ArrayList<E> {
        
        private static final long serialVersionUID = 1L;

        public E pop() {
            if (isEmpty()) {
                return null;
            }
            return remove(size() - 1);
        }
        
        public E peek() {
            if (isEmpty()) {
                return null;
            }
            return get(size() - 1);
        }
        
        public void push(E e) {
            add(e);
        }
        
        public boolean isEmpty() {
            return size() == 0;
        }
    }
}
