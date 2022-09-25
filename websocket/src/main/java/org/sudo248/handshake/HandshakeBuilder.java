package org.sudo248.handshake;

/**
 * The interface for building a handshake
 */

public interface HandshakeBuilder extends Handshake {

    /**
     * Setter for the content of the handshake
     *
     * @param content the content to set
     */
    void setContent(byte[] content);

    /**
     * Adding a specific field with a specific value
     *
     * @param name  the http field
     * @param value the value for this field
     */
    void put(String name, String value);
}
