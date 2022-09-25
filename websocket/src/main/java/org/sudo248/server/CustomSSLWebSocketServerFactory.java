package org.sudo248.server;

import org.sudo248.ssl.SSLChannelImpl;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import java.io.IOException;
import java.nio.channels.ByteChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * WebSocketFactory that can be configured to only support specific protocols and cipher suites.
 */
public class CustomSSLWebSocketServerFactory extends DefaultSSLWebSocketServerFactory{

    /**
     * The enabled protocols saved as a String array
     */
    private final String[] enabledProtocols;

    /**
     * The enabled cipher suites saved as a String array
     */
    private final String[] enabledCiphersuites;

    /**
     * New CustomSSLWebSocketServerFactory configured to only support given protocols and given cipher
     * suites.
     *
     * @param sslContext          - can not be <code>null</code>
     * @param enabledProtocols    - only these protocols are enabled, when <code>null</code> default
     *                            settings will be used.
     * @param enabledCipherSuites - only these cipher suites are enabled, when <code>null</code>
     *                            default settings will be used.
     */
    public CustomSSLWebSocketServerFactory(SSLContext sslContext, String[] enabledProtocols,
                                           String[] enabledCipherSuites) {
        this(sslContext, Executors.newSingleThreadScheduledExecutor(), enabledProtocols,
                enabledCipherSuites);
    }

    /**
     * New CustomSSLWebSocketServerFactory configured to only support given protocols and given cipher
     * suites.
     *
     * @param sslContext          - can not be <code>null</code>
     * @param executorService     - can not be <code>null</code>
     * @param enabledProtocols    - only these protocols are enabled, when <code>null</code> default
     *                            settings will be used.
     * @param enabledCipherSuites - only these cipher suites are enabled, when <code>null</code>
     *                            default settings will be used.
     */
    public CustomSSLWebSocketServerFactory(SSLContext sslContext, ExecutorService executorService,
                                           String[] enabledProtocols, String[] enabledCipherSuites) {
        super(sslContext, executorService);
        this.enabledProtocols = enabledProtocols;
        this.enabledCiphersuites = enabledCipherSuites;
    }

    @Override
    public ByteChannel wrapChannel(SocketChannel channel, SelectionKey key) throws IOException {
        SSLEngine engine = sslContext.createSSLEngine();
        if (enabledProtocols != null) {
            engine.setEnabledProtocols(enabledProtocols);
        }
        if (enabledCiphersuites != null) {
            engine.setEnabledCipherSuites(enabledCiphersuites);
        }
        engine.setUseClientMode(false);
        return new SSLChannelImpl(channel, engine, executor, key);
    }
}
