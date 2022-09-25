package org.sudo248.ssl;

import javax.net.ssl.SSLEngine;

/**
 * Interface which specifies all required methods a SSLSocketChannel has to make public.
 *
 */
public interface SSLChannel {
    /**
     * Get the ssl engine used for the de- and encryption of the communication.
     *
     * @return the ssl engine of this channel
     */
    SSLEngine getSSLEngine();
}
