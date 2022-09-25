package org.sudo248.exceptions;

import org.sudo248.frames.CloseFrame;

/**
 * exception which indicates that a invalid handshake was received (CloseFrame.PROTOCOL_ERROR)
 */
public class InvalidHandshakeException extends InvalidDataException{

    private static final long serialVersionUID = -1426533877490484964L;

    /**
     * constructor for a InvalidHandshakeException
     * <p>
     * calling InvalidDataException with close code PROTOCOL_ERROR
     */
    public InvalidHandshakeException() {
        super(CloseFrame.PROTOCOL_ERROR);
    }

    /**
     * constructor for a InvalidHandshakeException
     * <p>
     * calling InvalidDataException with closecode PROTOCOL_ERROR
     *
     * @param message the detail message.
     * @param cause   the throwable causing this exception.
     */
    public InvalidHandshakeException(String message, Throwable cause) {
        super(CloseFrame.PROTOCOL_ERROR, message, cause);
    }

    /**
     * constructor for a InvalidHandshakeException
     * <p>
     * calling InvalidDataException with closecode PROTOCOL_ERROR
     *
     * @param message the detail message.
     */
    public InvalidHandshakeException(String message) {
        super(CloseFrame.PROTOCOL_ERROR, message);
    }

    /**
     * constructor for a InvalidHandshakeException
     * <p>
     * calling InvalidDataException with closecode PROTOCOL_ERROR
     *
     * @param cause the throwable causing this exception.
     */
    public InvalidHandshakeException(Throwable cause) {
        super(CloseFrame.PROTOCOL_ERROR, cause);
    }
}
