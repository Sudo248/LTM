package org.sudo248.handshake.client;

import org.sudo248.handshake.HandshakeBuilder;

/**
 * The interface for building a handshake for the client
 */

public interface ClientHandshakeBuilder extends HandshakeBuilder, ClientHandshake{

    /**
     * Set a specific resource descriptor
     *
     * @param resourceDescriptor the resource descriptior to set
     */
    void setResourceDescriptor(String resourceDescriptor);
}
