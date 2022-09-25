package org.sudo248.exceptions;

import org.sudo248.frames.CloseFrame;

/**
 * exception which indicates that the message limited was exceeded (CloseFrame.TOOBIG)
 */
public class LimitExceededException extends InvalidDataException{

    private static final long serialVersionUID = 6908339749836826785L;

    /**
     * A closer indication about the limit
     */
    private final int limit;

    /**
     * constructor for a LimitExceededException
     * <p>
     * calling LimitExceededException with close code TOO_BIG
     */
    public LimitExceededException() {
        this(Integer.MAX_VALUE);
    }

    /**
     * constructor for a LimitExceededException
     * <p>
     * calling InvalidDataException with close code TOO_BIG
     * @param limit the allowed size which was not enough
     */
    public LimitExceededException(int limit) {
        super(CloseFrame.TOO_BIG);
        this.limit = limit;
    }

    /**
     * constructor for a LimitExceededException
     * <p>
     * calling InvalidDataException with close code TOO_BIG
     * @param message the detail message.
     * @param limit the allowed size which was not enough
     */
    public LimitExceededException(String message, int limit) {
        super(CloseFrame.TOO_BIG, message);
        this.limit = limit;
    }

    /**
     * constructor for a LimitExceededException
     * <p>
     * calling InvalidDataException with closecode TOOBIG
     *
     * @param message the detail message.
     */
    public LimitExceededException(String message) {
        this(message, Integer.MAX_VALUE);
    }

    /**
     * Get the limit which was hit so this exception was caused
     *
     * @return the limit as int
     */
    public int getLimit() {
        return limit;
    }
}
