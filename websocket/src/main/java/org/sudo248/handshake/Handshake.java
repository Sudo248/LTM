package org.sudo248.handshake;

import java.util.Iterator;

/**
 * The interface for a handshake
 */

public interface Handshake {

    /**
     * Iterator for the http fields
     *
     * @return the http fields
     */
    Iterator<String> iterateHttpFields();

    /**
     * Gets the value of the field
     *
     * @param name The name of the field
     * @return the value of the field or an empty String if not in the handshake
     */
    String getFieldValue(String name);

    /**
     * Checks if this handshake contains a specific field
     *
     * @param name The name of the field
     * @return true, if it contains the field
     */
    boolean hasFieldValue(String name);

    /**
     * Get the content of the handshake
     *
     * @return the content as byte-array
     */
    byte[] getContent();
}
