package org.sudo248.client;

import org.sudo248.AbstractWebSocket;
import org.sudo248.WebSocket;
import org.sudo248.WebSocketImpl;
import org.sudo248.common.Opcode;
import org.sudo248.common.ReadyState;
import org.sudo248.drafts.Draft;
import org.sudo248.drafts.Draft_6455;
import org.sudo248.exceptions.InvalidHandshakeException;
import org.sudo248.frames.CloseFrame;
import org.sudo248.frames.FrameData;
import org.sudo248.handshake.Handshake;
import org.sudo248.handshake.client.ClientHandshakeBuilderImpl;
import org.sudo248.handshake.server.ServerHandshake;
import org.sudo248.protocols.Protocol;

import javax.net.SocketFactory;
import javax.net.ssl.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.*;
import java.nio.ByteBuffer;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * A subclass must implement at least <var>onOpen</var>, <var>onClose</var>, and
 * <var>onMessage</var> to be useful. At runtime the user is expected to establish a connection via
 * {@link #connect()}, then receive events like {@link #onMessage(String)} via the overloaded
 * methods and to {@link #send(String)} data to the server.
 */
public abstract class WebSocketClient extends AbstractWebSocket implements Runnable, WebSocket {

    /**
     * The URI this channel is supposed to connect to.
     */
    protected URI uri;

    /**
     * The underlying engine
     */
    private WebSocketImpl engine;

    /**
     * The socket for this WebSocketClient
     */
    private Socket socket = null;

    /**
     * The SocketFactory for this WebSocketClient
     *
     */
    private SocketFactory socketFactory = null;

    /**
     * The used OutputStream
     */
    private OutputStream oStream;

    /**
     * The used proxy, if any
     */
    private Proxy proxy = Proxy.NO_PROXY;

    /**
     * The thread to write outgoing message
     */
    private Thread writeThread;

    /**
     * The thread to connect and read message
     */
    private Thread connectReadThread;

    /**
     * The draft to use
     */
    private final Draft draft;

    /**
     * The additional headers to use
     */
    private Map<String, String> headers;

    /**
     * The latch for connectBlocking()
     */
    private CountDownLatch connectLatch = new CountDownLatch(1);

    /**
     * The latch for closeBlocking()
     */
    private CountDownLatch closeLatch = new CountDownLatch(1);

    /**
     * The socket timeout value to be used in milliseconds.
     */
    private int connectTimeout = 0;

    /**
     * DNS resolver that translates a URI to an InetAddress
     *
     * @see InetAddress
     */
    private DnsResolver dnsResolver;

    /**
     * Constructs a WebSocketClient instance and sets it to the connect to the specified URI. The
     * channel does not attampt to connect automatically. The connection will be established once you
     * call <var>connect</var>.
     *
     * @param serverUri the server URI to connect to
     */
    public WebSocketClient(URI serverUri) {
        this(serverUri, new Draft_6455());
    }

    /**
     * Constructs a WebSocketClient instance and sets it to the connect to the specified URI. The
     * channel does not attampt to connect automatically. The connection will be established once you
     * call <var>connect</var>.
     *
     * @param serverUri     the server URI to connect to
     * @param protocolDraft The draft which should be used for this connection
     */
    public WebSocketClient(URI serverUri, Draft protocolDraft) {
        this(serverUri, protocolDraft, null, 0);
    }

    /**
     * Constructs a WebSocketClient instance and sets it to the connect to the specified URI. The
     * channel does not attampt to connect automatically. The connection will be established once you
     * call <var>connect</var>.
     *
     * @param serverUri   the server URI to connect to
     * @param httpHeaders Additional HTTP-Headers
     * @since 1.3.8
     */
    public WebSocketClient(URI serverUri, Map<String, String> httpHeaders) {
        this(serverUri, new Draft_6455(), httpHeaders);
    }

    /**
     * Constructs a WebSocketClient instance and sets it to the connect to the specified URI. The
     * channel does not attampt to connect automatically. The connection will be established once you
     * call <var>connect</var>.
     *
     * @param serverUri     the server URI to connect to
     * @param protocolDraft The draft which should be used for this connection
     * @param httpHeaders   Additional HTTP-Headers
     * @since 1.3.8
     */
    public WebSocketClient(URI serverUri, Draft protocolDraft, Map<String, String> httpHeaders) {
        this(serverUri, protocolDraft, httpHeaders, 0);
    }

    /**
     * Constructs a WebSocketClient instance and sets it to the connect to the specified URI. The
     * channel does not attampt to connect automatically. The connection will be established once you
     * call <var>connect</var>.
     *
     * @param serverUri      the server URI to connect to
     * @param protocolDraft  The draft which should be used for this connection
     * @param httpHeaders    Additional HTTP-Headers
     * @param connectTimeout The Timeout for the connection
     */
    public WebSocketClient(URI serverUri, Draft protocolDraft, Map<String, String> httpHeaders,
                           int connectTimeout) {
        if (serverUri == null) {
            throw new IllegalArgumentException();
        } else if (protocolDraft == null) {
            throw new IllegalArgumentException("null as draft is permitted for `WebSocketServer` only!");
        }
        this.uri = serverUri;
        this.draft = protocolDraft;
        this.dnsResolver = new DnsResolver() {
            @Override
            public InetAddress resolve(URI uri) throws UnknownHostException {
                return InetAddress.getByName(uri.getHost());
            }
        };
        if (httpHeaders != null) {
            headers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
            headers.putAll(httpHeaders);
        }
        this.connectTimeout = connectTimeout;
        setIsTcpNoDelay(false);
        setIsReuseAddress(false);
        this.engine = new WebSocketImpl(this, protocolDraft);
    }

    /**
     * Returns the URI that this WebSocketClient is connected to.
     *
     * @return the URI connected to
     */
    public URI getURI() {
        return uri;
    }

    /**
     * Returns the protocol version this channel uses.<br> For more infos see
     * https://github.com/TooTallNate/Java-WebSocket/wiki/Drafts
     *
     * @return The draft used for this client
     */
    public Draft getDraft() {
        return draft;
    }

    /**
     * Returns the socket to allow Hostname Verification
     *
     * @return the socket used for this connection
     */
    public Socket getSocket() {
        return socket;
    }

    /**
     * @param key   Name of the header to add.
     * @param value Value of the header to add.
     * @since 1.4.1 Adds an additional header to be sent in the handshake.<br> If the connection is
     * already made, adding headers has no effect, unless reconnect is called, which then a new
     * handshake is sent.<br> If a header with the same key already exists, it is overridden.
     */
    public void addHeader(String key, String value) {
        if (headers == null) {
            headers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        }
        headers.put(key, value);
    }

    /**
     * @param key Name of the header to remove.
     * @return the previous value associated with key, or null if there was no mapping for key.
     * @since 1.4.1 Removes a header from the handshake to be sent, if header key exists.<br>
     */
    public String removeHeader(String key) {
        if (headers == null) {
            return null;
        }
        return headers.remove(key);
    }

    /**
     * @since 1.4.1 Clears all previously put headers.
     */
    public void clearHeaders() {
        headers = null;
    }

    /**
     * Sets a custom DNS resolver.
     *
     * @param dnsResolver The DnsResolver to use.
     */
    public void setDnsResolver(DnsResolver dnsResolver) {
        this.dnsResolver = dnsResolver;
    }

    /**
     * Reinitiates the websocket connection. This method does not block.
     *
     */
    public void reconnect() {
        reset();
        connect();
    }

    /**
     * Same as <code>reconnect</code> but blocks until the websocket reconnected or failed to do
     * so.<br>
     *
     * @return Returns whether it succeeded or not.
     * @throws InterruptedException Thrown when the threads get interrupted
     * @since 1.3.8
     */
    public boolean reconnectBlocking() throws InterruptedException {
        reset();
        return connectBlocking();
    }

    /**
     * Reset everything relevant to allow a reconnect
     *
     * @since 1.3.8
     */
    private void reset() {
        Thread current = Thread.currentThread();
        if (current == writeThread || current == connectReadThread) {
            throw new IllegalStateException(
                    "You cannot initialize a reconnect out of the websocket thread. Use reconnect in another thread to ensure a successful cleanup.");
        }
        try {
            closeBlocking();
            if (writeThread != null) {
                this.writeThread.interrupt();
                this.writeThread = null;
            }
            if (connectReadThread != null) {
                this.connectReadThread.interrupt();
                this.connectReadThread = null;
            }
            this.draft.reset();
            if (this.socket != null) {
                this.socket.close();
                this.socket = null;
            }
        } catch (Exception e) {
            onError(e);
            engine.closeConnection(CloseFrame.ABNORMAL_CLOSE, e.getMessage());
            return;
        }
        connectLatch = new CountDownLatch(1);
        closeLatch = new CountDownLatch(1);
        this.engine = new WebSocketImpl(this, this.draft);
    }

    /**
     * Initiates the websocket connection. This method does not block.
     */
    public void connect() {
        if (connectReadThread != null) {
            throw new IllegalStateException("WebSocketClient objects are not reuseable");
        }
        connectReadThread = new Thread(this);
        connectReadThread.setName("WebSocketConnectReadThread-" + connectReadThread.getId());
        connectReadThread.start();
    }

    /**
     * Same as <code>connect</code> but blocks until the websocket connected or failed to do so.<br>
     *
     * @return Returns whether it succeeded or not.
     * @throws InterruptedException Thrown when the threads get interrupted
     */
    public boolean connectBlocking() throws InterruptedException {
        connect();
        connectLatch.await();
        return engine.isOpen();
    }

    /**
     * Same as <code>connect</code> but blocks with a timeout until the websocket connected or failed
     * to do so.<br>
     *
     * @param timeout  The connect timeout
     * @param timeUnit The timeout time unit
     * @return Returns whether it succeeded or not.
     * @throws InterruptedException Thrown when the threads get interrupted
     */
    public boolean connectBlocking(long timeout, TimeUnit timeUnit) throws InterruptedException {
        connect();
        return connectLatch.await(timeout, timeUnit) && engine.isOpen();
    }

    /**
     * Same as <code>close</code> but blocks until the websocket closed or failed to do so.<br>
     *
     * @throws InterruptedException Thrown when the threads get interrupted
     */
    public void closeBlocking() throws InterruptedException {
        close();
        closeLatch.await();
    }

    private void upgradeSocketToSSL()
            throws NoSuchAlgorithmException, KeyManagementException, IOException {
        SSLSocketFactory factory;
        // Prioritise the provided socketfactory
        // Helps when using web debuggers like Fiddler Classic
        if (socketFactory instanceof SSLSocketFactory) {
            factory = (SSLSocketFactory) socketFactory;
        } else {
            SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(null, null, null);
            factory = sslContext.getSocketFactory();
        }
        socket = factory.createSocket(socket, uri.getHost(), getPort(), true);
    }

    private boolean prepareSocket() throws IOException {
        boolean upgradeSocketToSSLSocket = false;
        // Prioritise a proxy over a socket factory and apply the socketfactory later
        if (proxy != Proxy.NO_PROXY) {
            socket = new Socket(proxy);
            upgradeSocketToSSLSocket = true;
        } else if (socketFactory != null) {
            socket = socketFactory.createSocket();
        } else if (socket == null) {
            socket = new Socket(proxy);
            upgradeSocketToSSLSocket = true;
        } else if (socket.isClosed()) {
            throw new IOException();
        }
        return upgradeSocketToSSLSocket;
    }

    /**
     * Apply specific SSLParameters If you override this method make sure to always call
     * super.onSetSSLParameters() to ensure the hostname validation is active
     *
     * @param sslParameters the SSLParameters which will be used for the SSLSocket
     */
    protected void onSetSSLParameters(SSLParameters sslParameters) {
        // If you run into problem on Android (NoSuchMethodException), check out the wiki https://github.com/TooTallNate/Java-WebSocket/wiki/No-such-method-error-setEndpointIdentificationAlgorithm
        // Perform hostname validation
        sslParameters.setEndpointIdentificationAlgorithm("HTTPS");
    }

    /**
     * Extract the specified port
     *
     * @return the specified port or the default port for the specific scheme
     */
    private int getPort() {
        int port = uri.getPort();
        String scheme = uri.getScheme();
        if ("wss".equals(scheme)) {
            return port == -1 ? WebSocketImpl.DEFAULT_WSS_PORT : port;
        } else if ("ws".equals(scheme)) {
            return port == -1 ? WebSocketImpl.DEFAULT_PORT : port;
        } else {
            throw new IllegalArgumentException("unknown scheme: " + scheme);
        }
    }

    /**
     * Create and send the handshake to the other endpoint
     *
     * @throws InvalidHandshakeException a invalid handshake was created
     */
    private void sendHandshake() throws InvalidHandshakeException {
        String path;
        String part1 = uri.getRawPath();
        String part2 = uri.getRawQuery();
        if (part1 == null || part1.length() == 0) {
            path = "/";
        } else {
            path = part1;
        }
        if (part2 != null) {
            path += '?' + part2;
        }
        int port = getPort();
        String host =
                uri.getHost() + ((port != WebSocketImpl.DEFAULT_PORT && port != WebSocketImpl.DEFAULT_WSS_PORT) ? ":" + port : "");

        ClientHandshakeBuilderImpl handshake = new ClientHandshakeBuilderImpl();
        handshake.setResourceDescriptor(path);
        handshake.put("Host", host);
        if (headers != null) {
            for (Map.Entry<String, String> kv : headers.entrySet()) {
                handshake.put(kv.getKey(), kv.getValue());
            }
        }
        engine.startHandshake(handshake);
    }

    /**
     * Getter for the engine
     *
     * @return the engine
     */
    public WebSocket getConnection() {
        return engine;
    }

    /**
     * Method to give some additional info for specific IOExceptions
     *
     * @param e the IOException causing a eot.
     */
    private void handleIOException(IOException e) {
        if (e instanceof SSLException) {
            onError(e);
        }
        engine.eot();
    }

    // ABSTRACT METHODS /////////////////////////////////////////////////////////

    /**
     * Called after an opening handshake has been performed and the given websocket is ready to be
     * written on.
     *
     * @param handshake The handshake of the websocket instance
     */
    public abstract void onOpen(ServerHandshake handshake);

    /**
     * Callback for string messages received from the remote host
     *
     * @param message The UTF-8 decoded message that was received.
     * @see #onMessage(ByteBuffer)
     **/
    public abstract void onMessage(String message);

    /**
     * Called after the websocket connection has been closed.
     *
     * @param code   The codes can be looked up here: {@link CloseFrame}
     * @param reason Additional information string
     * @param remote Returns whether or not the closing of the connection was initiated by the remote
     *               host.
     **/
    public abstract void onClose(int code, String reason, boolean remote);

    /**
     * Called when errors occurs. If an error causes the websocket connection to fail {@link
     * #onClose(int, String, boolean)} will be called additionally.<br> This method will be called
     * primarily because of IO or protocol errors.<br> If the given exception is an RuntimeException
     * that probably means that you encountered a bug.<br>
     *
     * @param ex The exception causing this error
     **/
    public abstract void onError(Exception ex);

    /**
     * Callback for binary messages received from the remote host
     *
     * @param bytes The binary message that was received.
     * @see #onMessage(String)
     **/
    public void onMessage(ByteBuffer bytes) {
        //To overwrite
    }

    ///////////////////////////////////////////

    /**
     * Method to set a proxy for this connection
     *
     * @param proxy the proxy to use for this websocket client
     */
    public void setProxy(Proxy proxy) {
        if (proxy == null) {
            throw new IllegalArgumentException();
        }
        this.proxy = proxy;
    }

    /**
     * Accepts a SocketFactory.<br> This method must be called before <code>connect</code>. The socket
     * will be bound to the uri specified in the constructor.
     *
     * @param socketFactory The socket factory which should be used for the connection.
     */
    public void setSocketFactory(SocketFactory socketFactory) {
        this.socketFactory = socketFactory;
    }


    /**
     * Initiates the websocket close handshake. This method does not block<br> In oder to make sure
     * the connection is closed use <code>closeBlocking</code>
     */
    @Override
    public void close() {
        if (writeThread != null) {
            engine.close(CloseFrame.NORMAL);
        }
    }

    /**
     * Sends <var>text</var> to the connected websocket server.
     *
     * @param text The string which will be transmitted.
     */
    @Override
    public void send(String text) {
        engine.send(text);
    }

    /**
     * Sends binary <var> data</var> to the connected webSocket server.
     *
     * @param data The byte-Array of data to send to the WebSocket server.
     */
    @Override
    public void send(byte[] data) {
        engine.send(data);
    }

    @Override
    public <T> T getAttachment() {
        return engine.getAttachment();
    }

    @Override
    public <T> void setAttachment(T attachment) {
        engine.setAttachment(attachment);
    }

    @Override
    protected Collection<WebSocket> getConnections() {
        return Collections.singletonList((WebSocket) engine);
    }

    @Override
    public void sendPing() {
        engine.sendPing();
    }

    /**
     * This represents the state of the connection.
     */
    public ReadyState getReadyState() {
        return engine.getReadyState();
    }

    /**
     * Calls subclass' implementation of <var>onMessage</var>.
     */
    @Override
    public final void onWebSocketMessage(WebSocket ws, String message) {
        onMessage(message);
    }

    @Override
    public final void onWebSocketMessage(WebSocket ws, ByteBuffer blob) {
        onMessage(blob);
    }

    /**
     * Calls subclass' implementation of <var>onOpen</var>.
     */
    @Override
    public final void onWebSocketOpen(WebSocket ws, Handshake handshake) {
        startConnectionLostTimer();
        onOpen((ServerHandshake) handshake);
        connectLatch.countDown();
    }

    /**
     * Calls subclass' implementation of <var>onClose</var>.
     */
    @Override
    public final void onWebSocketClose(WebSocket ws, int code, String reason, boolean remote) {
        stopConnectionLostTimer();
        if (writeThread != null) {
            writeThread.interrupt();
        }
        onClose(code, reason, remote);
        connectLatch.countDown();
        closeLatch.countDown();
    }

    /**
     * Calls subclass' implementation of <var>onIOError</var>.
     */
    @Override
    public final void onWebSocketError(WebSocket ws, Exception ex) {
        onError(ex);
    }

    @Override
    public final void onWriteDemand(WebSocket ws) {
        // nothing to do
    }

    @Override
    public void onWebSocketCloseInitiated(WebSocket ws, int code, String reason) {
        onCloseInitiated(code, reason);
    }

    @Override
    public void onWebSocketClosing(WebSocket ws, int code, String reason, boolean remote) {
        onClosing(code, reason, remote);
    }

    /**
     * Send when this peer sends a close handshake
     *
     * @param code   The codes can be looked up here: {@link CloseFrame}
     * @param reason Additional information string
     */
    public void onCloseInitiated(int code, String reason) {
        //To overwrite
    }

    /**
     * Called as soon as no further frames are accepted
     *
     * @param code   The codes can be looked up here: {@link CloseFrame}
     * @param reason Additional information string
     * @param remote Returns whether or not the closing of the connection was initiated by the remote
     *               host.
     */
    public void onClosing(int code, String reason, boolean remote) {
        //To overwrite
    }

    @Override
    public InetSocketAddress getLocalSocketAddress(WebSocket ws) {
        if (socket != null) {
            return (InetSocketAddress) socket.getLocalSocketAddress();
        }
        return null;
    }

    @Override
    public InetSocketAddress getRemoteSocketAddress(WebSocket ws) {
        if (socket != null) {
            return (InetSocketAddress) socket.getRemoteSocketAddress();
        }
        return null;
    }

    @Override
    public void sendFragmentedFrame(Opcode op, ByteBuffer buffer, boolean fin) {
        engine.sendFragmentedFrame(op, buffer, fin);
    }

    @Override
    public boolean isOpen() {
        return engine.isOpen();
    }

    @Override
    public boolean isFlushAndClose() {
        return engine.isFlushAndClose();
    }

    @Override
    public boolean isClosed() {
        return engine.isClosed();
    }

    @Override
    public boolean isClosing() {
        return engine.isClosing();
    }

    @Override
    public boolean hasBufferedData() {
        return engine.hasBufferedData();
    }

    @Override
    public void close(int code) {
        engine.close(code);
    }

    @Override
    public void close(int code, String message) {
        engine.close(code, message);
    }

    @Override
    public void closeConnection(int code, String message) {
        engine.closeConnection(code, message);
    }

    @Override
    public void send(ByteBuffer bytes) {
        engine.send(bytes);
    }

    @Override
    public void sendFrame(FrameData frameData) {
        engine.sendFrame(frameData);
    }

    @Override
    public void sendFrame(Collection<FrameData> frames) {
        engine.sendFrame(frames);
    }

    @Override
    public InetSocketAddress getLocalSocketAddress() {
        return engine.getLocalSocketAddress();
    }

    @Override
    public InetSocketAddress getRemoteSocketAddress() {
        return engine.getRemoteSocketAddress();
    }

    @Override
    public String getResourceDescriptor() {
        return uri.getPath();
    }

    @Override
    public boolean hasSSLSupport() {
        return socket instanceof SSLSocket;
    }

    @Override
    public SSLSession getSSLSession() {
        if (!hasSSLSupport()) {
            throw new IllegalArgumentException(
                    "This websocket uses ws instead of wss. No SSLSession available.");
        }
        return ((SSLSocket)socket).getSession();
    }

    @Override
    public Protocol getProtocol() {
        return engine.getProtocol();
    }

    @Override
    public void run() {
        InputStream iStream;
        try {
            boolean upgradeSocketToSSLSocket = prepareSocket();
            socket.setTcpNoDelay(isTcpNoDelay());
            socket.setReuseAddress(isReuseAddress());

            if (!socket.isConnected()) {
                InetSocketAddress addr = dnsResolver == null ? InetSocketAddress.createUnresolved(uri.getHost(), getPort()) : new InetSocketAddress(dnsResolver.resolve(uri), this.getPort());
                socket.connect(addr, connectTimeout);
            }

            // if the socket is set by others we don't apply any TLS wrapper
            if (upgradeSocketToSSLSocket && "wss".equals(uri.getScheme())) {
                upgradeSocketToSSL();
            }

            if (socket instanceof SSLSocket) {
                SSLSocket sslSocket = (SSLSocket) socket;
                SSLParameters sslParameters = sslSocket.getSSLParameters();
                onSetSSLParameters(sslParameters);
                sslSocket.setSSLParameters(sslParameters);
            }

            iStream = socket.getInputStream();
            oStream = socket.getOutputStream();

            sendHandshake();
        } catch (/*IOException | SecurityException | UnresolvedAddressException | InvalidHandshakeException | ClosedByInterruptException | SocketTimeoutException */Exception e) {
            onWebSocketError(engine, e);
            engine.closeConnection(CloseFrame.NEVER_CONNECTED, e.getMessage());
            return;
        } catch (InternalError e) {
            // https://bugs.openjdk.java.net/browse/JDK-8173620
            if (e.getCause() instanceof InvocationTargetException && e.getCause()
                    .getCause() instanceof IOException) {
                IOException cause = (IOException) e.getCause().getCause();
                onWebSocketError(engine, cause);
                engine.closeConnection(CloseFrame.NEVER_CONNECTED, cause.getMessage());
                return;
            }
            throw e;
        }

        writeThread = new Thread(new WebsocketWriteThread(this));
        writeThread.start();

        byte[] rawBuffer = new byte[WebSocketImpl.RCV_BUFFER];
        int readBytes;

        try {
            while (!isClosing() && !isClosed() && (readBytes = iStream.read(rawBuffer)) != -1) {
                engine.decode(ByteBuffer.wrap(rawBuffer, 0, readBytes));
            }
            engine.eot();
        } catch (IOException e) {
            handleIOException(e);
        } catch (RuntimeException e) {
            // this catch case covers internal errors only and indicates a bug in this websocket implementation
            onError(e);
            engine.closeConnection(CloseFrame.ABNORMAL_CLOSE, e.getMessage());
        }
        connectReadThread = null;
    }

    private class WebsocketWriteThread implements Runnable {

        private final WebSocketClient webSocketClient;

        WebsocketWriteThread(WebSocketClient webSocketClient) {
            this.webSocketClient = webSocketClient;
        }

        @Override
        public void run() {
            Thread.currentThread().setName("WebSocketWriteThread-" + Thread.currentThread().getId());
            try {
                runWriteData();
            } catch (IOException e) {
                handleIOException(e);
            } finally {
                closeSocket();
                writeThread = null;
            }
        }

        /**
         * Write the data into the outstream
         *
         * @throws IOException if write or flush did not work
         */
        private void runWriteData() throws IOException {
            try {
                while (!Thread.interrupted()) {
                    ByteBuffer buffer = engine.outQueue.take();
                    oStream.write(buffer.array(), 0, buffer.limit());
                    oStream.flush();
                }
            } catch (InterruptedException e) {
                for (ByteBuffer buffer : engine.outQueue) {
                    oStream.write(buffer.array(), 0, buffer.limit());
                    oStream.flush();
                }
                Thread.currentThread().interrupt();
            }
        }

        /**
         * Closing the socket
         */
        private void closeSocket() {
            try {
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException ex) {
                onWebSocketError(webSocketClient, ex);
            }
        }
    }
}
