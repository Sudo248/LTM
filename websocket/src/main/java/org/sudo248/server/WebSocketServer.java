package org.sudo248.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sudo248.*;
import org.sudo248.drafts.Draft;
import org.sudo248.frames.CloseFrame;
import org.sudo248.frames.Frame;
import org.sudo248.handshake.Handshake;
import org.sudo248.handshake.client.ClientHandshake;
import org.sudo248.exceptions.WebsocketNotConnectedException;
import org.sudo248.exceptions.WrappedIOException;
import org.sudo248.mqtt.MqttConnection;
import org.sudo248.mqtt.MqttListener;
import org.sudo248.mqtt.MqttManager;
import org.sudo248.mqtt.database.H2Builder;
import org.sudo248.mqtt.model.MqttMessage;
import org.sudo248.mqtt.model.Subscription;
import org.sudo248.mqtt.repository.SubscriptionRepository;
import org.sudo248.utils.SocketChannelIOUtils;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <tt>WebSocketServer</tt> is an abstract class that only takes care of the
 * HTTP handshake portion of WebSockets. It's up to a subclass to add functionality/purpose to the
 * server.
 */
public abstract class WebSocketServer extends AbstractWebSocket implements Runnable, MqttListener {

    /**
     * Number of current available process
     */
    private static final int AVAILABLE_PROCESSORS = Runtime.getRuntime().availableProcessors();

    private final String pathStore;

    /**
     * Logger instance
     *
     */
    private final Logger log = LoggerFactory.getLogger(WebSocketServer.class);

    /**
     * Holds the list of active WebSocket connections. "Active" means WebSocket handshake is complete
     * and socket can be written to, or read from.
     */
    private final Collection<WebSocket> connections;

    /**
     * The port number that this WebSocket server should listen on. Default is
     * WebSocketImpl.DEFAULT_PORT.
     */
    private final InetSocketAddress address;

    /**
     * The socket channel for this WebSocket server.
     */
    private ServerSocketChannel server;

    /**
     * Mqtt connection
     */
    private MqttConnection mqttConnection;

    /**
     * MqttManager
     */
    private MqttManager mqttManager;

    /**
     * H2Builder
     */
    private H2Builder h2Builder;

    /**
     * The 'Selector' used to get event keys from the underlying socket.
     */
    private Selector selector;

    /**
     * The Draft of the WebSocket protocol the Server is adhering to.
     */
    private final List<Draft> drafts;

    private Thread selectorThread;

    private final AtomicBoolean isClosed = new AtomicBoolean(false);

    protected List<WebSocketServerWorker> decoders;

    /**
     * Queue of WebSocket that request to server
     */
    private final Queue<WebSocketImpl> wsQueue;

    private final BlockingQueue<ByteBuffer> buffers;

    private int queueInvokes = 0;

    private final AtomicInteger queueSize = new AtomicInteger(0);

    private WebSocketServerFactory wsf = new DefaultWebSocketServerFactory();

    /**
     * Attribute which allows you to configure the socket "backlog" parameter which determines how
     * many client connections can be queued.
     */
    private int maxPendingConnections = -1;

    /**
     * Creates a WebSocketServer that will attempt to listen on port <var>WebSocketImpl.DEFAULT_PORT</var>.
     *
     * @see #WebSocketServer(String, InetSocketAddress, int, List, Collection) more details here
     */
    public WebSocketServer(String pathStore) {
        this(pathStore, new InetSocketAddress(WebSocketImpl.DEFAULT_WSS_PORT), AVAILABLE_PROCESSORS, null);
    }

    /**
     * Creates a WebSocketServer that will attempt to bind/listen on the given <var>address</var>.
     *
     * @param address The address to listen to
     * @see #WebSocketServer(String, InetSocketAddress, int, List, Collection) more details here
     */
    public WebSocketServer(String pathStore, InetSocketAddress address) {
        this(pathStore, address, AVAILABLE_PROCESSORS, null);
    }

    /**
     * Creates a WebSocketServer that will attempt to bind/listen on the given <var>address</var>.
     *
     * @param address The address to listen to
     * @see #WebSocketServer(String, InetSocketAddress, int, List, Collection) more details here
     */
    public WebSocketServer(InetSocketAddress address) {
        this("", address, AVAILABLE_PROCESSORS, null);
    }

    /**
     * @param address      The address (host:port) this server should listen on.
     * @param decoderCount The number of {@link WebSocketServerWorker}s that will be used to process the
     *                     incoming network data. By default this will be <code>Runtime.getRuntime().availableProcessors()</code>
     * @see #WebSocketServer(String, InetSocketAddress, int, List, Collection) more details here
     */
    public WebSocketServer(String pathStore, InetSocketAddress address, int decoderCount) {
        this(pathStore, address, decoderCount, null);
    }

    /**
     * @param address The address (host:port) this server should listen on.
     * @param drafts  The versions of the WebSocket protocol that this server instance should comply
     *                to. Clients that use an other protocol version will be rejected.
     * @see #WebSocketServer(String, InetSocketAddress, int, List, Collection) more details here
     */
    public WebSocketServer(String pathStore, InetSocketAddress address, List<Draft> drafts) {
        this(pathStore, address, AVAILABLE_PROCESSORS, drafts);
    }

    /**
     * @param address      The address (host:port) this server should listen on.
     * @param decoderCount The number of {@link WebSocketServerWorker}s that will be used to process the
     *                     incoming network data. By default this will be <code>Runtime.getRuntime().availableProcessors()</code>
     * @param drafts       The versions of the WebSocket protocol that this server instance should
     *                     comply to. Clients that use an other protocol version will be rejected.
     * @see #WebSocketServer(String, InetSocketAddress, int, List, Collection) more details here
     */
    public WebSocketServer(String pathStore, InetSocketAddress address, int decoderCount, List<Draft> drafts) {
        this(pathStore, address, decoderCount, drafts, new HashSet<>());
    }

    /**
     * Creates a WebSocketServer that will attempt to bind/listen on the given <var>address</var>, and
     * comply with <tt>Draft</tt> version <var>draft</var>.
     *
     * @param address              The address (host:port) this server should listen on.
     * @param decoderCount         The number of {@link WebSocketServerWorker}s that will be used to process
     *                             the incoming network data. By default this will be
     *                             <code>Runtime.getRuntime().availableProcessors()</code>
     * @param drafts               The versions of the WebSocket protocol that this server instance
     *                             should comply to. Clients that use an other protocol version will
     *                             be rejected.
     * @param connectionsContainer Allows to specify a collection that will be used to store the
     *                             websockets in. <br> If you plan to often iterate through the
     *                             currently connected websockets you may want to use a collection
     *                             that does not require synchronization like a {@link
     *                             CopyOnWriteArraySet}. In that case make sure that you overload
     *                             {@link #removeConnection(WebSocket)} and {@link
     *                             #addConnection(WebSocket)}.<br> By default a {@link HashSet} will
     *                             be used.
     * @see #removeConnection(WebSocket) for more control over syncronized operation
     * @see <a href="https://github.com/TooTallNate/Java-WebSocket/wiki/Drafts" > more about
     * drafts</a>
     */
    public WebSocketServer(String pathStore, InetSocketAddress address, int decoderCount, List<Draft> drafts,
                           Collection<WebSocket> connectionsContainer) {
        if (address == null || decoderCount < 1 || connectionsContainer == null) {
            throw new IllegalArgumentException(
                    "address and connections container must not be null and you need at least 1 decoder");
        }

        this.pathStore = pathStore;
        if (drafts == null) {
            this.drafts = Collections.emptyList();
        } else {
            this.drafts = drafts;
        }
        this.address = address;
        this.connections = connectionsContainer;
        setIsTcpNoDelay(false);
        setIsReuseAddress(false);
        wsQueue = new LinkedList<>();

        decoders = new ArrayList<>(decoderCount);
        buffers = new LinkedBlockingQueue<>();
        for (int i = 0; i < decoderCount; i++) {
            WebSocketServerWorker ex = new WebSocketServerWorker();
            decoders.add(ex);
        }
    }

    /**
     * Starts the server selectorThread that binds to the currently set port number and listeners for
     * WebSocket connection requests. Creates a fixed thread pool with the size {@link
     * WebSocketServer#AVAILABLE_PROCESSORS}<br> May only be called once.
     * <p>
     * Alternatively you can call {@link WebSocketServer#run()} directly.
     *
     * @throws IllegalStateException Starting an instance again
     */
    public void start() {
        if (selectorThread != null) {
            throw new IllegalStateException(getClass().getName() + " can only be started once.");
        }
        new Thread(this).start();
    }

    public void stop(int timeout) throws InterruptedException {
        stop(timeout, "");
    }

    /**
     * Closes all connected clients sockets, then closes the underlying ServerSocketChannel,
     * effectively killing the server socket selectorThread, freeing the port the server was bound to
     * and stops all internal workerthreads.
     * <p>
     * If this method is called before the server is started it will never start.
     *
     * @param timeout Specifies how many milliseconds the overall close handshaking may take
     *                altogether before the connections are closed without proper close
     *                handshaking.
     * @param closeMessage Specifies message for remote client<br>
     * @throws InterruptedException Interrupt
     */
    public void stop(int timeout, String closeMessage) throws InterruptedException {
        if (!isClosed.compareAndSet(false,
                true)) { // this also makes sure that no further connections will be added to this.connections
            return;
        }

        List<WebSocket> socketsToClose;

        // copy the connections in a list (prevent callback deadlocks)
        synchronized (connections) {
            socketsToClose = new ArrayList<>(connections);
        }

        for (WebSocket ws : socketsToClose) {
            ws.close(CloseFrame.GOING_AWAY, closeMessage);
        }

        wsf.close();

        synchronized (this) {
            if (selectorThread != null && selector != null) {
                selector.wakeup();
                selectorThread.join(timeout);
            }
        }
    }

    public void stop() throws InterruptedException {
        stop(0);
    }

    public InetSocketAddress getAddress() {
        return this.address;
    }

    /**
     * Gets the port number that this server listens on.
     *
     * @return The port number.
     */
    public int getPort() {
        int port = getAddress().getPort();
        if (port == 0 && server != null) {
            port = server.socket().getLocalPort();
        }
        return port;
    }

    /**
     * Get the list of active drafts
     *
     * @return the available drafts for this server
     */
    public List<Draft> getDraft() {
        return Collections.unmodifiableList(drafts);
    }

    /**
     * Set the requested maximum number of pending connections on the socket. The exact semantics are
     * implementation specific. The value provided should be greater than 0. If it is less than or
     * equal to 0, then an implementation specific default will be used. This option will be passed as
     * "backlog" parameter to {@link ServerSocket#bind(SocketAddress, int)}
     *
     * @since 1.5.0
     * @param numberOfConnections the new number of allowed pending connections
     */
    public void setMaxPendingConnections(int numberOfConnections) {
        maxPendingConnections = numberOfConnections;
    }

    /**
     * Returns the currently configured maximum number of pending connections.
     *
     * @see #setMaxPendingConnections(int)
     * @since 1.5.0
     * @return the maximum number of pending connections
     */
    public int getMaxPendingConnections() {
        return maxPendingConnections;
    }

    /**
     * Do an additional read
     *
     * @throws InterruptedException thrown by taking a buffer
     * @throws IOException          if an error happened during read
     */
    private void doAdditionalRead() throws InterruptedException, IOException {
        WebSocketImpl ws;
        while (!wsQueue.isEmpty()) {
            ws = wsQueue.poll();
            WrappedByteChannel c = ((WrappedByteChannel) ws.getChannel());
            ByteBuffer buf = takeBuffer();
            try {
                if (SocketChannelIOUtils.readMore(buf, ws, c)) {
                    wsQueue.add(ws);
                }
                if (buf.hasRemaining()) {
                    ws.inQueue.put(buf);
                    queue(ws);
                } else {
                    pushBuffer(buf);
                }
            } catch (IOException e) {
                pushBuffer(buf);
                throw e;
            }
        }
    }

    /**
     * Execute a accept operation
     *
     * @param key the selectionkey to read off
     * @param i   the iterator for the selection keys
     * @throws InterruptedException thrown by taking a buffer
     * @throws IOException          if an error happened during accept
     */
    private void doAccept(SelectionKey key, Iterator<SelectionKey> i)
            throws IOException, InterruptedException {
        if (!onConnect(key)) {
            key.cancel();
            return;
        }

        SocketChannel channel = server.accept();
        if (channel == null) {
            return;
        }
        channel.configureBlocking(false);
        Socket socket = channel.socket();
        socket.setTcpNoDelay(isTcpNoDelay());
        socket.setKeepAlive(true);
        WebSocketImpl ws = wsf.createWebSocket(this, drafts);
        ws.setSelectionKey(channel.register(selector, SelectionKey.OP_READ, ws));
        try {
            ws.setChannel(wsf.wrapChannel(channel, ws.getSelectionKey()));
            i.remove();
            allocateBuffers(ws);
        } catch (IOException ex) {
            if (ws.getSelectionKey() != null) {
                ws.getSelectionKey().cancel();
            }
            handleIOException(ws.getSelectionKey(), null, ex);
        }
    }

    /**
     * Execute a read operation
     *
     * @param key the selectionkey to read off
     * @param i   the iterator for the selection keys
     * @return true, if the read was successful, or false if there was an error
     * @throws InterruptedException thrown by taking a buffer
     * @throws IOException          if an error happened during read
     */
    private boolean doRead(SelectionKey key, Iterator<SelectionKey> i)
            throws InterruptedException, WrappedIOException {
        WebSocketImpl ws = (WebSocketImpl) key.attachment();
        ByteBuffer buf = takeBuffer();
        if (ws.getChannel() == null) {
            key.cancel();
            handleIOException(key, ws, new IOException());
            return false;
        }
        try {
            if (SocketChannelIOUtils.read(buf, ws, ws.getChannel())) {
                if (buf.hasRemaining()) {
                    ws.inQueue.put(buf);
                    queue(ws);
                    i.remove();
                    if (ws.getChannel() instanceof WrappedByteChannel && ((WrappedByteChannel) ws
                            .getChannel()).isNeedRead()) {
                        wsQueue.add(ws);
                    }
                } else {
                    pushBuffer(buf);
                }
            } else {
                pushBuffer(buf);
            }
        } catch (IOException e) {
            pushBuffer(buf);
            throw new WrappedIOException(ws, e);
        }
        return true;
    }

    /**
     * Execute a write operation
     *
     * @param key the selectionkey to write on
     * @throws IOException if an error happened during batch
     */
    private void doWrite(SelectionKey key) throws WrappedIOException {
        WebSocketImpl ws = (WebSocketImpl) key.attachment();
        try {
            if (SocketChannelIOUtils.batch(ws, ws.getChannel()) && key.isValid()) {
                key.interestOps(SelectionKey.OP_READ);
            }
        } catch (IOException e) {
            throw new WrappedIOException(ws, e);
        }
    }

    /**
     * Setup the selector thread as well as basic server settings
     *
     * @return true, if everything was successful, false if some error happened
     */
    private boolean doSetupSelectorAndServerThread() {
        selectorThread.setName("WebSocketSelector-" + selectorThread.getId());
        try {
            server = ServerSocketChannel.open();
            server.configureBlocking(false);
            ServerSocket socket = server.socket();
            socket.setReceiveBufferSize(WebSocketImpl.RCV_BUFFER);
            socket.setReuseAddress(isReuseAddress());
            socket.bind(address, getMaxPendingConnections());
            selector = Selector.open();
            server.register(selector, server.validOps());
            startConnectionLostTimer();
            for (WebSocketServerWorker ex : decoders) {
                ex.start();
            }
            onStart();
            log.info("WebSocket running on: ws://" + address.getHostString() + ":" + address.getPort());
        } catch (IOException ex) {
            handleInterrupt(null, ex);
            return false;
        }
        return true;
    }

    private boolean doSetupMqttConnection() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        h2Builder = new H2Builder(pathStore, scheduler).initStore();
        SubscriptionRepository subscriptionRepository = h2Builder.subscriptionRepository();
        mqttManager = new MqttManager(subscriptionRepository);
        mqttManager.getSubscriptionFromDb();
        mqttConnection = new MqttConnection(mqttManager.getPublishers(), this, mqttManager.getSubscriberTopic());
        return true;
    }

    /**
     * The websocket server can only be started once
     *
     * @return true, if the server can be started, false if already a thread is running
     */
    private boolean doEnsureSingleThread() {
        synchronized (this) {
            if (selectorThread != null) {
                throw new IllegalStateException(getClass().getName() + " can only be started once.");
            }
            selectorThread = Thread.currentThread();
            if (isClosed.get()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Clean up everything after a shutdown
     */
    private void doServerShutdown() {
        stopConnectionLostTimer();
        if (decoders != null) {
            for (WebSocketServerWorker w : decoders) {
                w.interrupt();
            }
        }
        if (selector != null) {
            try {
                selector.close();
            } catch (IOException e) {
                log.error("IOException during selector.close", e);
                onError(null, e);
            }
        }
        if (server != null) {
            try {
                server.close();
            } catch (IOException e) {
                log.error("IOException during server.close", e);
                onError(null, e);
            }
        }
        h2Builder.closeStore();
    }

    protected void allocateBuffers(WebSocket ws) throws InterruptedException {
        if (queueSize.get() >= 2 * decoders.size() + 1) {
            return;
        }
        queueSize.incrementAndGet();
        buffers.put(createBuffer());
    }

    protected void releaseBuffers(WebSocket c) throws InterruptedException {
        // queuesize.decrementAndGet();
        // takeBuffer();
    }

    public ByteBuffer createBuffer() {
        return ByteBuffer.allocate(WebSocketImpl.RCV_BUFFER);
    }

    protected void queue(WebSocketImpl ws) throws InterruptedException {
        if (ws.getWorkerThread() == null) {
            ws.setWorkerThread(decoders.get(queueInvokes % decoders.size()));
            queueInvokes++;
        }
        ws.getWorkerThread().put(ws);
    }

    private ByteBuffer takeBuffer() throws InterruptedException {
        return buffers.take();
    }

    private void pushBuffer(ByteBuffer buf) throws InterruptedException {
        if (buffers.size() > queueSize.intValue()) {
            return;
        }
        buffers.put(buf);
    }

    private void handleIOException(SelectionKey key, WebSocket ws, IOException ex) {
        if (key != null) {
            key.cancel();
        }
        if (ws != null) {
            ws.closeConnection(CloseFrame.ABNORMAL_CLOSE, ex.getMessage());
        } else if (key != null) {
            SelectableChannel channel = key.channel();
            if (channel != null && channel
                    .isOpen()) { // this could be the case if the IOException ex is a SSLException
                try {
                    channel.close();
                } catch (IOException e) {
                    // there is nothing that must be done here
                }
                log.trace("Connection closed because of exception", ex);
            }
        }
    }

    private void handleInterrupt(WebSocket ws, Exception e) {
        log.error("Shutdown due to fatal error", e);
        onError(ws, e);

        String causeMessage = e.getCause() != null ? " caused by " + e.getCause().getClass().getName() : "";
        String errorMessage = "Got error on server side: " + e.getClass().getName() + causeMessage;
        try {
            stop(0, errorMessage);
        } catch (InterruptedException e1) {
            Thread.currentThread().interrupt();
            log.error("Interrupt during stop", e);
            onError(null, e1);
        }

        //Shutting down WebSocketWorkers, see #222
        if (decoders != null) {
            for (WebSocketServerWorker w : decoders) {
                w.interrupt();
            }
        }
        if (selectorThread != null) {
            selectorThread.interrupt();
        }
    }

    /**
     * This method performs remove operations on the connection and therefore also gives control over
     * whether the operation shall be synchronized
     * <p>
     * {@link #WebSocketServer(String,InetSocketAddress, int, List, Collection)} allows to specify a
     * collection which will be used to store current connections in.<br> Depending on the type on the
     * connection, modifications of that collection may have to be synchronized.
     *
     * @param ws The Websocket connection which should be removed
     * @return Removing connection successful
     */
    protected boolean removeConnection(WebSocket ws) {
        boolean removed = false;
        synchronized (connections) {
            if (this.connections.contains(ws)) {
                removed = this.connections.remove(ws);
            } else {
                //Don't throw an assert error if the ws is not in the list. e.g. when the other endpoint did not send any handshake. see #512
                log.trace(
                        "Removing connection which is not in the connections collection! Possible no handshake received! {}",
                        ws);
            }
        }
        if (isClosed.get() && connections.isEmpty()) {
            selectorThread.interrupt();
        }
        return removed;
    }

    /**
     * @param ws the Websocket connection which should be added
     * @return Adding connection successful
     * @see #removeConnection(WebSocket)
     */
    protected boolean addConnection(WebSocket ws) {
        if (!isClosed.get()) {
            synchronized (connections) {
                return this.connections.add(ws);
            }
        } else {
            // This case will happen when a new connection gets ready while the server is already stopping.
            ws.close(CloseFrame.GOING_AWAY);
            return true;// for consistency sake we will make sure that both onOpen will be called
        }
    }

    public void onCloseInitiated(WebSocket ws, int code, String reason) {
    }

    public void onClosing(WebSocket ws, int code, String reason, boolean remote) {

    }

    public final void setWebSocketFactory(WebSocketServerFactory wsf) {
        if (this.wsf != null) {
            this.wsf.close();
        }
        this.wsf = wsf;
    }

    public final WebSocketFactory getWebSocketFactory() {
        return wsf;
    }

    /**
     * Returns whether a new connection shall be accepted or not.<br> Therefore method is well suited
     * to implement some kind of connection limitation.<br>
     *
     * @param key the SelectionKey for the new connection
     * @return Can this new connection be accepted
     * @see #onOpen(WebSocket, ClientHandshake)
     * @see #onWebSocketHandshakeReceivedAsServer(WebSocket, Draft, ClientHandshake)
     **/
    protected boolean onConnect(SelectionKey key) {
        return true;
    }

    /**
     * Getter to return the socket used by this specific connection
     *
     * @param ws The specific connection
     * @return The socket used by this connection
     */
    private Socket getSocket(WebSocket ws) {
        WebSocketImpl impl = (WebSocketImpl) ws;
        return ((SocketChannel) impl.getSelectionKey().channel()).socket();
    }

    /**
     * precess when mess is MqttMessage
     * @param message
     * @param ws
     */

    private void onMqttMessage(MqttMessage message, WebSocket ws) {
        mqttConnection.handleMessage(message, ws);
    }

    /**
     * Cal
     * led after an opening handshake has been performed and the given websocket is ready to be
     * written on.
     *
     * @param ws      The <tt>WebSocket</tt> instance this event is occurring on.
     * @param handshake The handshake of the websocket instance
     */
    public abstract void onOpen(WebSocket ws, ClientHandshake handshake);

    /**
     * Called after the websocket connection has been closed.
     *
     * @param ws   The <tt>WebSocket</tt> instance this event is occurring on.
     * @param code   The codes can be looked up here: {@link CloseFrame}
     * @param reason Additional information string
     * @param remote Returns whether or not the closing of the connection was initiated by the remote
     *               host.
     **/
    public abstract void onClose(WebSocket ws, int code, String reason, boolean remote);

    /**
     * Callback for string messages received from the remote host
     *
     * @param ws    The <tt>WebSocket</tt> instance this event is occurring on.
     * @param message The UTF-8 decoded message that was received.
     * @see #onMessage(WebSocket, ByteBuffer)
     **/
    public abstract void onMessage(WebSocket ws, String message);

    /**
     * Callback for object received from the remote host
     *
     * @param ws    The <tt>WebSocket</tt> instance this event is occurring on.
     * @param object The Object decoded message that was received.
     * @see #onMessage(WebSocket, Object)
     **/
    public abstract void onMessage(WebSocket ws, Object object);

    /**
     * Called when errors occurs. If an error causes the websocket connection to fail {@link
     * #onClose(WebSocket, int, String, boolean)} will be called additionally.<br> This method will be
     * called primarily because of IO or protocol errors.<br> If the given exception is an
     * RuntimeException that probably means that you encountered a bug.<br>
     *
     * @param ws Can be null if there error does not belong to one specific websocket. For example
     *             if the servers port could not be bound.
     * @param ex   The exception causing this error
     **/
    public abstract void onError(WebSocket ws, Exception ex);

    /**
     * Called when the server started up successfully.
     * <p>
     * If any error occurred, onMqttError is called instead.
     */
    public abstract void onStart();

    /**
     * Callback for binary messages received from the remote host
     *
     * @param ws    The <tt>WebSocket</tt> instance this event is occurring on.
     * @param blob The binary message that was received.
     * @see #onMessage(WebSocket, ByteBuffer)
     **/
    public void onMessage(WebSocket ws, ByteBuffer blob) {
    }

    /**
     * Send a text to all connected endpoints
     *
     * @param text the text to send to the endpoints
     */
    public void broadcast(String text) {
        broadcast(text, connections);
    }

    /**
     * Send a byte array to all connected endpoints
     *
     * @param data the data to send to the endpoints
     */
    public void broadcast(byte[] data) {
        broadcast(data, connections);
    }

    /**
     * Send a ByteBuffer to all connected endpoints
     *
     * @param data the data to send to the endpoints
     */
    public void broadcast(ByteBuffer data) {
        broadcast(data, connections);
    }

    /**
     * Send a byte array to a specific collection of websocket connections
     *
     * @param data    the data to send to the endpoints
     * @param clients a collection of endpoints to whom the text has to be send
     */
    public void broadcast(byte[] data, Collection<WebSocket> clients) {
        if (data == null || clients == null) {
            throw new IllegalArgumentException();
        }
        broadcast(ByteBuffer.wrap(data), clients);
    }

    /**
     * Send a ByteBuffer to a specific collection of websocket connections
     *
     * @param data    the data to send to the endpoints
     * @param clients a collection of endpoints to whom the text has to be send
     */
    public void broadcast(ByteBuffer data, Collection<WebSocket> clients) {
        if (data == null || clients == null) {
            throw new IllegalArgumentException();
        }
        doBroadcast(data, clients);
    }

    /**
     * Send a text to a specific collection of websocket connections
     *
     * @param text    the text to send to the endpoints
     * @param clients a collection of endpoints to whom the text has to be send
     */
    public void broadcast(String text, Collection<WebSocket> clients) {
        if (text == null || clients == null) {
            throw new IllegalArgumentException();
        }
        doBroadcast(text, clients);
    }

    /**
     * Private method to cache all the frames to improve memory footprint and conversion time
     *
     * @param data    the data to broadcast
     * @param clients the clients to send the message to
     */
    private void doBroadcast(Object data, Collection<WebSocket> clients) {
        String strData = null;
        if (data instanceof String) {
            strData = (String) data;
        }
        ByteBuffer byteData = null;
        if (data instanceof ByteBuffer) {
            byteData = (ByteBuffer) data;
        }
        if (strData == null && byteData == null) {
            return;
        }
        Map<Draft, List<Frame>> draftFrames = new HashMap<>();
        List<WebSocket> clientCopy;
        synchronized (clients) {
            clientCopy = new ArrayList<>(clients);
        }
        for (WebSocket client : clientCopy) {
            if (client != null) {
                Draft draft = client.getDraft();
                fillFrames(draft, draftFrames, strData, byteData);
                try {
                    client.sendFrame(draftFrames.get(draft));
                } catch (WebsocketNotConnectedException e) {
                    //Ignore this exception in this case
                }
            }
        }
    }

    /**
     * Fills the draftFrames with new data for the broadcast
     *
     * @param draft       The draft to use
     * @param draftFrames The list of frames per draft to fill
     * @param strData       the string data, can be null
     * @param byteData       the byte buffer data, can be null
     */
    private void fillFrames(Draft draft, Map<Draft, List<Frame>> draftFrames, String strData,
                            ByteBuffer byteData) {
        if (!draftFrames.containsKey(draft)) {
            List<Frame> frames = null;
            if (strData != null) {
                frames = draft.createFrames(strData, false);
            }
            if (byteData != null) {
                frames = draft.createFrames(byteData, false);
            }
            if (frames != null) {
                draftFrames.put(draft, frames);
            }
        }
    }

    @Override
    public final void onWebSocketMessage(WebSocket ws, String message) {
        onMessage(ws, message);
    }

    @Override
    public final void onWebSocketMessage(WebSocket ws, ByteBuffer blob) {
        onMessage(ws, blob);
    }

    @Override
    public void onWebSocketMessage(WebSocket ws, Object object) {
        if (object instanceof MqttMessage) {
            onMqttMessage((MqttMessage) object, ws);
        } else {
            onMessage(ws, object);
        }
    }

    @Override
    public final void onWebSocketOpen(WebSocket ws, Handshake handshake) {
        if (addConnection(ws)) {
            onOpen(ws, (ClientHandshake) handshake);
        }
    }

    @Override
    public final void onWebSocketClose(WebSocket ws, int code, String reason, boolean remote) {
        selector.wakeup();
        try {
            if (removeConnection(ws)) {
                onClose(ws, code, reason, remote);
            }
        } finally {
            try {
                releaseBuffers(ws);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @Override
    public final void onWebSocketError(WebSocket ws, Exception ex) {
        onError(ws, ex);
    }

    @Override
    public final void onWriteDemand(WebSocket ws) {
        WebSocketImpl wsImpl = (WebSocketImpl) ws;
        try {
            wsImpl.getSelectionKey().interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
        } catch (CancelledKeyException e) {
            // the thread which cancels key is responsible for possible cleanup
            wsImpl.outQueue.clear();
        }
        selector.wakeup();
    }

    @Override
    public void onWebSocketCloseInitiated(WebSocket ws, int code, String reason) {
        onCloseInitiated(ws, code, reason);
    }

    @Override
    public void onWebSocketClosing(WebSocket ws, int code, String reason, boolean remote) {
        onClosing(ws, code, reason, remote);

    }

    @Override
    public InetSocketAddress getLocalSocketAddress(WebSocket ws) {
        return (InetSocketAddress) getSocket(ws).getLocalSocketAddress();
    }

    @Override
    public InetSocketAddress getRemoteSocketAddress(WebSocket ws) {
        return (InetSocketAddress) getSocket(ws).getRemoteSocketAddress();
    }

    @Override
    public void run() {
        if (!doEnsureSingleThread()) {
            return;
        }
        if (!doSetupSelectorAndServerThread()) {
            return;
        }
        if (!doSetupMqttConnection()) {
            return;
        }
        try {
            int shutdownCount = 5;
            int selectTimeout = 0;
            while (!selectorThread.isInterrupted() && shutdownCount != 0) {
                SelectionKey key = null;
                try {
                    if (isClosed.get()) {
                        selectTimeout = 5;
                    }
                    int keyCount = selector.select(selectTimeout);
                    if (keyCount == 0 && isClosed.get()) {
                        shutdownCount--;
                    }
                    Set<SelectionKey> keys = selector.selectedKeys();
                    Iterator<SelectionKey> i = keys.iterator();

                    while (i.hasNext()) {
                        key = i.next();

                        if (!key.isValid()) {
                            continue;
                        }

                        if (key.isAcceptable()) {
                            doAccept(key, i);
                            continue;
                        }

                        if (key.isReadable() && !doRead(key, i)) {
                            continue;
                        }

                        if (key.isWritable()) {
                            doWrite(key);
                        }
                    }
                    doAdditionalRead();
                } catch (CancelledKeyException e) {
                    // an other thread may cancel the key
                } catch (ClosedByInterruptException e) {
                    return; // do the same stuff as when InterruptedException is thrown
                } catch (WrappedIOException ex) {
                    handleIOException(key, ex.getWebSocket(), ex.getIOException());
                } catch (IOException ex) {
                    handleIOException(key, null, ex);
                } catch (InterruptedException e) {
                    // FIXME controlled shutdown (e.g. take care of buffermanagement)
                    Thread.currentThread().interrupt();
                }
            }
        } catch (RuntimeException e) {
            // should hopefully never occur
            handleInterrupt(null, e);
        } finally {
            doServerShutdown();
        }
    }

    /**
     * Returns  all currently connected clients. This collection does not allow any modification e.g.
     * removing a client.
     *
     * @return A unmodifiable collection of all currently connected clients
     * @since 1.3.8
     */
    @Override
    public Collection<WebSocket> getConnections() {
        synchronized (connections) {
            return Collections.unmodifiableCollection(new ArrayList<>(connections));
        }
    }

    @Override
    public void onMqttConnect(MqttMessage message) {

    }

    @Override
    public void onMqttSubscribe(MqttMessage message) {
        log.info("onMqttSubscribe: message" + message);
        Subscription subscription = new Subscription(
                message.getClientId(),
                message.getTopic()
        );
        mqttManager.addSubscription(subscription);
        publish(message);
    }

    @Override
    public void onMqttUnSubscribe(MqttMessage message) {
        Subscription subscription = new Subscription(
                message.getClientId(),
                message.getTopic()
        );
        mqttManager.removeSubscription(subscription);
        publish(message);
    }

    @Override
    public void onMqttDisconnect(MqttMessage message) {

    }

    @Override
    public void onMqttError(MqttMessage message, String reason) {

    }

    public void publish(MqttMessage message) {
        mqttConnection.publish(message.getTopic(), message);
    }

    /**
     * This class is used to process incoming data
     */
    public class WebSocketServerWorker extends Thread {

        private final BlockingQueue<WebSocketImpl> wsBLockingQueue;

        public WebSocketServerWorker() {
            wsBLockingQueue = new LinkedBlockingQueue<>();
            setName("WebSocketWorker-" + getId());
            setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
                @Override
                public void uncaughtException(Thread t, Throwable e) {
                    log.error("Uncaught exception in thread {}: {}", t.getName(), e);
                }
            });
        }

        public void put(WebSocketImpl ws) throws InterruptedException {
            wsBLockingQueue.put(ws);
        }

        @Override
        public void run() {
            WebSocketImpl ws = null;
            try {
                while (true) {
                    ByteBuffer buf;
                    ws = wsBLockingQueue.take();
                    buf = ws.inQueue.poll();
                    assert (buf != null);
                    doDecode(ws, buf);
                    ws = null;
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (VirtualMachineError | ThreadDeath | LinkageError e) {
                log.error("Got fatal error in worker thread {}", getName());
                Exception exception = new Exception(e);
                handleInterrupt(ws, exception);
            } catch (Throwable e) {
                log.error("Uncaught exception in thread {}: {}", getName(), e);
                if (ws != null) {
                    Exception exception = new Exception(e);
                    onWebSocketError(ws, exception);
                    ws.close();
                }
            }
        }

        /**
         * call ws.decode on the byteBuffer
         *
         * @param ws  the Websocket
         * @param buf the buffer to decode to
         * @throws InterruptedException thrown by pushBuffer
         */
        private void doDecode(WebSocketImpl ws, ByteBuffer buf) throws InterruptedException {
            try {
                ws.decode(buf);
            } catch (Exception e) {
                log.error("Error while reading from remote connection", e);
            } finally {
                pushBuffer(buf);
            }
        }
    }
}
