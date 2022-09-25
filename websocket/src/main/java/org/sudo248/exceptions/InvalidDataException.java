package org.sudo248.exceptions;

/**
 * exception which indicates that a invalid data was received
 */
public class InvalidDataException extends Exception{

    private static final long serialVersionUID = 3731842424390998726L;

    /**
     * attribute which close code will be returned
     */
    private final int closeCode;

    /**
     * constructor for a InvalidDataException
     *
     * @param closeCode the close code which will be returned
     */
    public InvalidDataException(int closeCode) {
        this.closeCode = closeCode;
    }

    /**
     * constructor for a InvalidDataException.
     *
     * @param closeCode the close code which will be returned.
     * @param message   the detail message.
     */
    public InvalidDataException(int closeCode, String message) {
        super(message);
        this.closeCode = closeCode;
    }

    /**
     * constructor for a InvalidDataException.
     *
     * @param closeCode the close code which will be returned.
     * @param cause     the throwable causing this exception.
     */
    public InvalidDataException(int closeCode, Throwable cause) {
        super(cause);
        this.closeCode = closeCode;
    }

    /**
     * constructor for a InvalidDataException.
     *
     * @param closeCode the close code which will be returned.
     * @param message   the detail message.
     * @param cause     the throwable causing this exception.
     */
    public InvalidDataException( int closeCode, String message, Throwable cause) {
        super(message, cause);
        this.closeCode = closeCode;
    }

    /**
     * Getter close code
     *
     * @return the close code
     */
    public int getCloseCode() {
        return closeCode;
    }
}
