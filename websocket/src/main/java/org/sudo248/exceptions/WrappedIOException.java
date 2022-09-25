package org.sudo248.exceptions;

import org.sudo248.WebSocket;

import java.io.IOException;

/**
 * Exception to wrap an IOException and include information about the websocket which had the
 * exception
 *
 */
public class WrappedIOException extends Exception{

    /**
     * The websocket where the IOException happened
     */
    private final transient WebSocket ws;

    /**
     * The IOException
     */
    private final IOException ioException;

    /**
     * Wrapp an IOException and include the websocket
     *
     * @param ws  the websocket where the IOException happened
     * @param ioException the IOException
     */
    public WrappedIOException(WebSocket ws, IOException ioException) {
        this.ws = ws;
        this.ioException = ioException;
    }
    /**
     * The websocket where the IOException happened
     *
     * @return the websocket for the wrapped IOException
     */
    public WebSocket getWebSocket() {
        return ws;
    }

    /**
     * The wrapped IOException
     *
     * @return IOException which is wrapped
     */
    public IOException getIOException() {
        return ioException;
    }
}
