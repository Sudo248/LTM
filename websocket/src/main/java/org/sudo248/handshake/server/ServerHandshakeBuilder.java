package org.sudo248.handshake.server;

import org.sudo248.handshake.HandshakeBuilder;

/**
 * The interface for building a handshake for the server
 */

public interface ServerHandshakeBuilder extends HandshakeBuilder, ServerHandshake {

    /**
     * Setter for the http status code
     *
     * @param status the http status code
     */
    void setHttpStatus(short status);

    /**
     * Setter for the http status message
     *
     * @param message the http status message
     */
    void setHttpStatusMessage(String message);
}
