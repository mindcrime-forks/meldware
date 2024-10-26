/**
 * 
 */
package org.buni.meldware.mail.protocol;

/**
 * @author Michael.Barker
 *
 */
public class ValidationException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * 
     */
    public ValidationException() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @param message
     * @param cause
     */
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param message
     */
    public ValidationException(String message) {
        super(message);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param cause
     */
    public ValidationException(Throwable cause) {
        super(cause);
        // TODO Auto-generated constructor stub
    }

    
}
