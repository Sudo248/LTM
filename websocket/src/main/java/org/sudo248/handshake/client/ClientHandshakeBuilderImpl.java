package org.sudo248.handshake.client;


import org.sudo248.handshake.HandshakeBuilderImpl;

/**
 * Implementation for a client handshake
 */

public class ClientHandshakeBuilderImpl  extends HandshakeBuilderImpl implements  ClientHandshakeBuilder{

    /**
     * Attribute for the resource descriptor
     */
    private String resourceDescriptor = "*";

    @Override
    public String getResourceDescriptor() {
        return resourceDescriptor;
    }

    @Override
    public void setResourceDescriptor(String resourceDescriptor) {
        if (resourceDescriptor == null) {
            throw new IllegalArgumentException("http resource descriptor must not be null");
        }
        this.resourceDescriptor = resourceDescriptor;
    }
}
