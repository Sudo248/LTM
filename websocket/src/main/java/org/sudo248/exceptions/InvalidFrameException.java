package org.sudo248.exceptions;

import org.sudo248.frames.CloseFrame;

/**
 * exception which indicates that a invalid frame was received (CloseFrame.PROTOCOL_ERROR)
 */
public class InvalidFrameException extends InvalidDataException{

    private static final long serialVersionUID = -9016496369828887591L;

    /**
     * constructor for a InvalidFrameException
     * <p>
     * calling InvalidDataException with close code PROTOCOL_ERROR
     */
    public InvalidFrameException(int closeCode) {
        super(closeCode);
    }

    /**
     * constructor for a InvalidFrameException
     * <p>
     * calling InvalidDataException with closecode PROTOCOL_ERROR
     *
     * @param message the detail message.
     */
    public InvalidFrameException(String message) {
        super(CloseFrame.PROTOCOL_ERROR, message);
    }

    /**
     * constructor for a InvalidFrameException
     * <p>
     * calling InvalidDataException with closecode PROTOCOL_ERROR
     *
     * @param cause the throwable causing this exception.
     */
    public InvalidFrameException(Throwable cause) {
        super(CloseFrame.PROTOCOL_ERROR, cause);
    }

    /**
     * constructor for a InvalidFrameException
     * <p>
     * calling InvalidDataException with closecode PROTOCOL_ERROR
     *
     * @param message the detail message.
     * @param cause the throwable causing this exception.
     */
    public InvalidFrameException(String message, Throwable cause) {
        super(CloseFrame.PROTOCOL_ERROR, message, cause);
    }
}
