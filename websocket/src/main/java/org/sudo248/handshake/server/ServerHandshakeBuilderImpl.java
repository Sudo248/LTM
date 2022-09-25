package org.sudo248.handshake.server;

import org.sudo248.handshake.HandshakeBuilderImpl;

/**
 * Implementation for a server handshake
 */

public class ServerHandshakeBuilderImpl extends HandshakeBuilderImpl implements ServerHandshakeBuilder {

    /**
     * Attribute for the http status
     */
    private short httpStatus;

    /**
     * Attribute for the http status message
     */
    private String httpStatusMessage;

    @Override
    public short getHttpStatus() {
        return this.httpStatus;
    }

    @Override
    public String getHttpStatusMessage() {
        return this.httpStatusMessage;
    }

    @Override
    public void setHttpStatus(short status) {
        this.httpStatus = status;
    }

    @Override
    public void setHttpStatusMessage(String message) {
        this.httpStatusMessage = message;
    }
}
