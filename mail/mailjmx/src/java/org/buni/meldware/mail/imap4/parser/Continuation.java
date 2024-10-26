package org.buni.meldware.mail.imap4.parser;

/**
 * Allows arbitrary exits from the parser.
 * 
 * @author Michael.Barker
 *
 */
public class Continuation extends RuntimeException {

    public enum Type { STRING, MESSAGE };
    
    private static final long serialVersionUID = 1L;
    private final int len;
    private final Type type;

    public Continuation(int len, Type type) {
        this.len = len;
        this.type = type;
    }
    
    public Continuation(int len) {
        this.len = len;
        this.type = Type.STRING;
    }
    
    public int getLength() {
        return len;
    }
    
    public Type getType() {
        return type;
    }

    /**
     * @see java.lang.Throwable#fillInStackTrace()
     */
    @Override
    public Throwable fillInStackTrace() {
        return null;
    }
    
    public String getMessage() {
        return "Continuation: " + len + " bytes";
    }
}
