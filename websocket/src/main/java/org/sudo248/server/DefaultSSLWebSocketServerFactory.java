package org.sudo248.server;

import org.sudo248.AbstractWebSocketListenerImpl;
import org.sudo248.WebSocketImpl;
import org.sudo248.drafts.Draft;
import org.sudo248.ssl.SSLChannelImpl;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import java.io.IOException;
import java.nio.channels.ByteChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Default SSL web socket server factory
 */
public class DefaultSSLWebSocketServerFactory implements WebSocketServerFactory{

    protected final SSLContext sslContext;

    protected final ExecutorService executor;


    public DefaultSSLWebSocketServerFactory(SSLContext sslContext) {
        this(sslContext, Executors.newSingleThreadScheduledExecutor());
    }

    public DefaultSSLWebSocketServerFactory(SSLContext sslContext, ExecutorService exec) {
        if (sslContext == null || exec == null) {
            throw new IllegalArgumentException();
        }
        this.sslContext = sslContext;
        this.executor = exec;
    }

    @Override
    public WebSocketImpl createWebSocket(AbstractWebSocketListenerImpl webSocketListener, Draft draft) {
        return new WebSocketImpl(webSocketListener, draft);
    }

    @Override
    public WebSocketImpl createWebSocket(AbstractWebSocketListenerImpl webSocketListener, List<Draft> drafts) {
        return new WebSocketImpl(webSocketListener, drafts);
    }

    @Override
    public ByteChannel wrapChannel(SocketChannel channel, SelectionKey key) throws IOException {
        SSLEngine engine = sslContext.createSSLEngine();
        List<String> ciphers = new ArrayList<>(Arrays.asList(engine.getEnabledCipherSuites()));
        ciphers.remove("TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256");
        engine.setEnabledCipherSuites(ciphers.toArray(new String[ciphers.size()]));
        engine.setUseClientMode(false);
        return new SSLChannelImpl(channel, engine, executor, key);
    }

    @Override
    public void close() {
        executor.shutdown();
    }
}
