package org.sudo248.handshake.server;

import org.sudo248.handshake.Handshake;

/**
 * Interface for the server handshake
 */

public interface ServerHandshake extends Handshake {

    /**
     * Get the http status code
     *
     * @return the http status code
     */
    short getHttpStatus();

    /**
     * Get the http status message
     *
     * @return the http status message
     */
    String getHttpStatusMessage();
}
