package org.sudo248.client;

import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;

/**
 * Users may implement this interface to override the default DNS lookup offered by the OS.
 *
 */
public interface DnsResolver {

    /**
     * Resolves the IP address for the given URI.
     * <p>
     * This method should never return null. If it's not able to resolve the IP address then it should
     * throw an UnknownHostException
     *
     * @param uri The URI to be resolved
     * @return The resolved IP address
     * @throws UnknownHostException if no IP address for the <code>uri</code> could be found.
     */
    InetAddress resolve(URI uri) throws UnknownHostException;
}
