package org.sudo248.protocols;

/**
 * Interface which specifies all required methods for a Sec-WebSocket-Protocol
 */

public interface Protocol {
    /**
     * Check if the received Sec-WebSocket-Protocol header field contains a offer for the specific
     * protocol
     *
     * @param inputProtocolHeader the received Sec-WebSocket-Protocol header field offered by the
     *                            other endpoint
     * @return true, if the offer does fit to this specific protocol
     */
    boolean acceptProvidedProtocol(String inputProtocolHeader);

    /**
     * Return the specific Sec-WebSocket-protocol header offer for this protocol if the endpoint. If
     * the extension returns an empty string (""), the offer will not be included in the handshake.
     *
     * @return the specific Sec-WebSocket-Protocol header for this protocol
     */
    String getProvidedProtocol();

    /**
     * To prevent protocols to be used more than once the Websocket implementation should call this
     * method in order to create a new usable version of a given protocol instance.
     *
     * @return a copy of the protocol
     */
    Protocol copy();

    /**
     * Return a string which should contain the protocol name as well as additional information about
     * the current configurations for this protocol (DEBUG purposes)
     *
     * @return a string containing the protocol name as well as additional information
     */
    String toString();
}
