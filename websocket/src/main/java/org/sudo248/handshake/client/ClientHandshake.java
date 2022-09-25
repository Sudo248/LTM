package org.sudo248.handshake.client;

import org.sudo248.handshake.Handshake;

/**
 * The interface for a client handshake
 */

public interface ClientHandshake extends Handshake {

    /**
     * returns the HTTP Request-URI as defined by http://tools.ietf.org/html/rfc2616#section-5.1.2
     *
     * @return the HTTP Request-URI
     */
    String getResourceDescriptor();
}
