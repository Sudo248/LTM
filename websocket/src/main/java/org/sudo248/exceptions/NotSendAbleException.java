package org.sudo248.exceptions;

/**
 * exception which indicates the frame payload is not sendable
 */
public class NotSendAbleException extends RuntimeException{

    private static final long serialVersionUID = -6468967874576651628L;

    /**
     * constructor for a NotSendableException
     *
     * @param message the detail message.
     */
    public NotSendAbleException(String message) {
        super(message);
    }

    /**
     * constructor for a NotSendableException
     *
     * @param cause the throwable causing this exception.
     */
    public NotSendAbleException(Throwable cause) {
        super(cause);
    }

    /**
     * constructor for a NotSendableException
     *
     * @param message the detail message.
     * @param cause the throwable causing this exception.
     */
    public NotSendAbleException(String message, Throwable cause) {
        super(message, cause);
    }
}
