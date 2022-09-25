package org.sudo248;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sudo248.common.*;
import org.sudo248.drafts.Draft;
import org.sudo248.drafts.Draft_6455;
import org.sudo248.frames.CloseFrame;
import org.sudo248.frames.FrameData;
import org.sudo248.ssl.SSLChannel;
import org.sudo248.utils.CharsetFunctions;
import org.sudo248.exceptions.*;
import org.sudo248.frames.PingFrame;
import org.sudo248.handshake.Handshake;
import org.sudo248.handshake.client.ClientHandshake;
import org.sudo248.handshake.client.ClientHandshakeBuilder;
import org.sudo248.handshake.server.ServerHandshake;
import org.sudo248.handshake.server.ServerHandshakeBuilder;
import org.sudo248.protocols.Protocol;
import org.sudo248.server.WebSocketServer.WebSocketServerWorker;

import javax.net.ssl.SSLSession;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.channels.SelectionKey;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Represents one end (client or server) of a single WebSocketImpl connection. Takes care of the
 * "handshake" phase, then allows for easy sending of text frames, and receiving frames through an
 * event-based model.
 */

public class WebSocketImpl implements WebSocket {

    /**
     * The default port of WebSockets, as defined in the spec. If the nullary constructor is used,
     * DEFAULT_PORT will be the port the WebSocketServer is binded to. Note that ports under 1024
     * usually require root permissions.
     */
    public static final int DEFAULT_PORT = 80;

    /**
     * The default wss port of WebSockets, as defined in the spec. If the nullary constructor is used,
     * DEFAULT_WSS_PORT will be the port the WebSocketServer is binded to. Note that ports under 1024
     * usually require root permissions.
     */
    public static final int DEFAULT_WSS_PORT = 433;

    /**
     * Initial buffer size
     */
    public static final int RCV_BUFFER = 16384;

    /**
     * Logger instance
     */
    private final Logger log = LoggerFactory.getLogger(WebSocketImpl.class);

    /**
     * Queue of buffers that need to be sent to the client.
     */
    public final BlockingQueue<ByteBuffer> outQueue;

    /**
     * Queue of buffers that need to be processed (client sent to server)
     */
    public final BlockingQueue<ByteBuffer> inQueue;

    /**
     * The listener to notify of WebSocket events.
     */
    private final WebSocketListener webSocketListener;

    /**
     * selection key
     */
    private SelectionKey key;

    /**
     * the possibly wrapped channel object whose selection is controlled by {@link #key}
     */
    private ByteChannel channel;

    /**
     * Helper variable meant to store the thread which ( exclusively ) triggers this objects decode
     * method.
     **/
    private WebSocketServerWorker serverWorkerThread;

    /**
     * When true no further frames may be submitted to be sent
     */
    private boolean isFlushAndCloseState = false;

    /**
     * The current state of the connection
     */
    private volatile ReadyState readyState = ReadyState.NOT_YET_CONNECTED;

    /**
     * A list of drafts available for this websocket
     */
    private List<Draft> knownDrafts;

    /**
     * The draft which is used by this websocket
     */
    private Draft draft = null;

    /**
     * The role which this websocket takes in the connection
     */
    private Role role;

    /**
     * the bytes of an incomplete received handshake
     */
    private ByteBuffer tmpHandshakeBytes = ByteBuffer.allocate(0);

    /**
     * stores the handshake sent by this websocket ( Role.CLIENT only )
     */
    private ClientHandshake handshakeRequest = null;

    private String closeMessage = null;

    private Integer closeCode = null;

    private Boolean closedRemotely = null;

    private String resourceDescriptor = null;

    /**
     * Attribute, when the last pong was received
     */
    private long lastPong = System.nanoTime();

    /**
     * Attribut to synchronize the write
     */
    private final Object lockWriteObject = new Object();

    /**
     * Attribute to store connection attachment
     */
    private Object attachment;

    /**
     * Creates a websocket with server role
     *
     * @param listener The listener for this instance
     * @param drafts   The drafts which should be used
     */
    public WebSocketImpl(WebSocketListener listener, List<Draft> drafts) {
        this(listener,(Draft) null);
        this.role = Role.SERVER;
        // draft.copyInstance will be called when the draft is first needed
        if (drafts == null || drafts.isEmpty()) {
            knownDrafts = new ArrayList<>();
            knownDrafts.add(new Draft_6455());
        } else {
            knownDrafts = drafts;
        }
    }

    /**
     * creates a websocket with client role
     *
     * @param listener The listener for this instance
     * @param draft    The draft which should be used
     */
    public WebSocketImpl(WebSocketListener listener, Draft draft) {
        // socket can be null because we want do be able to create the object without already having a bound channel
        if (listener == null || (draft == null && role == Role.SERVER)) {
            throw new IllegalArgumentException("parameters must not be null");
        }
        this.outQueue = new LinkedBlockingQueue<>();
        this.inQueue = new LinkedBlockingQueue<>();
        this.webSocketListener = listener;
        this.role = Role.CLIENT;
        if (draft != null) {
            this.draft = draft.copy();
        }
    }

    /**
     * Method to decode the provided ByteBuffer
     *
     * @param socketBuffer the ByteBuffer to decode
     */
    public void decode(ByteBuffer socketBuffer) {
        assert (socketBuffer.hasRemaining());
        log.trace("process({}): ({})", socketBuffer.remaining(),
                (socketBuffer.remaining() > 1000 ? "too big to display"
                        : new String(socketBuffer.array(), socketBuffer.position(), socketBuffer.remaining())));

        if (readyState != ReadyState.NOT_YET_CONNECTED) {
            if (readyState == ReadyState.OPEN) {
                decodeFrames(socketBuffer);
            }
        } else {
            if (decodeHandshake(socketBuffer) && (!isClosing() && !isClosed())) {
                assert (tmpHandshakeBytes.hasRemaining() != socketBuffer.hasRemaining() || !socketBuffer
                        .hasRemaining()); // the buffers will never have remaining bytes at the same time
                if (socketBuffer.hasRemaining()) {
                    decodeFrames(socketBuffer);
                } else if (tmpHandshakeBytes.hasRemaining()) {
                    decodeFrames(tmpHandshakeBytes);
                }
            }
        }
    }

    /**
     * Returns whether the handshake phase has is completed. In case of a broken handshake this will
     * be never the case.
     **/
    private boolean decodeHandshake(ByteBuffer socketBufferNew) {
        ByteBuffer socketBuffer;
        if (tmpHandshakeBytes.capacity() == 0) {
            socketBuffer = socketBufferNew;
        } else {
            if (tmpHandshakeBytes.remaining() < socketBufferNew.remaining()) {
                ByteBuffer buf = ByteBuffer
                        .allocate(tmpHandshakeBytes.capacity() + socketBufferNew.remaining());
                tmpHandshakeBytes.flip();
                buf.put(tmpHandshakeBytes);
                tmpHandshakeBytes = buf;
            }

            tmpHandshakeBytes.put(socketBufferNew);
            tmpHandshakeBytes.flip();
            socketBuffer = tmpHandshakeBytes;
        }
        socketBuffer.mark();
        try {
            HandshakeState handshakeState;
            try {
                if (role == Role.SERVER) {
                    if (draft == null) {
                        for (Draft d : knownDrafts) {
                            d = d.copy();
                            try {
                                d.setParseMode(role);
                                socketBuffer.reset();
                                Handshake tmpHandshake = d.translateHandshake(socketBuffer);
                                if (!(tmpHandshake instanceof ClientHandshake)) {
                                    log.trace("Closing due to wrong handshake");
                                    closeConnectionDueToWrongHandshake(
                                            new InvalidDataException(CloseFrame.PROTOCOL_ERROR, "wrong http function"));
                                    return false;
                                }
                                ClientHandshake handshake = (ClientHandshake) tmpHandshake;
                                handshakeState = d.acceptHandshakeAsServer(handshake);
                                if (handshakeState == HandshakeState.MATCHED) {
                                    resourceDescriptor = handshake.getResourceDescriptor();
                                    ServerHandshakeBuilder response;
                                    try {
                                        response = webSocketListener.onWebSocketHandshakeReceivedAsServer(this, d, handshake);
                                    } catch (InvalidDataException e) {
                                        log.trace("Closing due to wrong handshake. Possible handshake rejection", e);
                                        closeConnectionDueToWrongHandshake(e);
                                        return false;
                                    } catch (RuntimeException e) {
                                        log.error("Closing due to internal server error", e);
                                        webSocketListener.onWebSocketError(this, e);
                                        closeConnectionDueToInternalServerError(e);
                                        return false;
                                    }
                                    write(d.createHandshake(
                                            d.postProcessHandshakeResponseAsServer(handshake, response)));
                                    draft = d;
                                    open(handshake);
                                    return true;
                                }
                            } catch (InvalidHandshakeException e) {
                                // go on with an other draft
                            }
                        }
                        if (draft == null) {
                            log.trace("Closing due to protocol error: no draft matches");
                            closeConnectionDueToWrongHandshake(
                                    new InvalidDataException(CloseFrame.PROTOCOL_ERROR, "no draft matches"));
                        }
                    } else {
                        // special case for multiple step handshakes
                        Handshake tmpHandshake = draft.translateHandshake(socketBuffer);
                        if (!(tmpHandshake instanceof ClientHandshake)) {
                            log.trace("Closing due to protocol error: wrong http function");
                            flushAndClose(CloseFrame.PROTOCOL_ERROR, "wrong http function", false);
                            return false;
                        }
                        ClientHandshake handshake = (ClientHandshake) tmpHandshake;
                        handshakeState = draft.acceptHandshakeAsServer(handshake);

                        if (handshakeState == HandshakeState.MATCHED) {
                            open(handshake);
                            return true;
                        } else {
                            log.trace("Closing due to protocol error: the handshake did finally not match");
                            close(CloseFrame.PROTOCOL_ERROR, "the handshake did finally not match");
                        }
                    }
                    return false;
                } else if (role == Role.CLIENT) {
                    draft.setParseMode(role);
                    Handshake tmpHandshake = draft.translateHandshake(socketBuffer);
                    if (!(tmpHandshake instanceof ServerHandshake)) {
                        log.trace("Closing due to protocol error: wrong http function");
                        flushAndClose(CloseFrame.PROTOCOL_ERROR, "wrong http function", false);
                        return false;
                    }
                    ServerHandshake handshake = (ServerHandshake) tmpHandshake;
                    handshakeState = draft.acceptHandshakeAsClient(handshakeRequest, handshake);
                    if (handshakeState == HandshakeState.MATCHED) {
                        try {
                            webSocketListener.onWebSocketHandshakeReceivedAsClient(this, handshakeRequest, handshake);
                        } catch (InvalidDataException e) {
                            log.trace("Closing due to invalid data exception. Possible handshake rejection", e);
                            flushAndClose(e.getCloseCode(), e.getMessage(), false);
                            return false;
                        } catch (RuntimeException e) {
                            log.error("Closing since client was never connected", e);
                            webSocketListener.onWebSocketError(this, e);
                            flushAndClose(CloseFrame.NEVER_CONNECTED, e.getMessage(), false);
                            return false;
                        }
                        open(handshake);
                        return true;
                    } else {
                        log.trace("Closing due to protocol error: draft {} refuses handshake", draft);
                        close(CloseFrame.PROTOCOL_ERROR, "draft " + draft + " refuses handshake");
                    }
                }
            } catch (InvalidHandshakeException e) {
                log.trace("Closing due to invalid handshake", e);
                close(e);
            }
        } catch (IncompleteHandshakeException e) {
            if (tmpHandshakeBytes.capacity() == 0) {
                socketBuffer.reset();
                int newSize = e.getPreferredSize();
                if (newSize == 0) {
                    newSize = socketBuffer.capacity() + 16;
                } else {
                    assert (e.getPreferredSize() >= socketBuffer.remaining());
                }
                tmpHandshakeBytes = ByteBuffer.allocate(newSize);

                tmpHandshakeBytes.put(socketBufferNew);
            } else {
                tmpHandshakeBytes.position(tmpHandshakeBytes.limit());
                tmpHandshakeBytes.limit(tmpHandshakeBytes.capacity());
            }
        }
        return false;
    }

    /**
     * decode frames
     * @param socketBuffer
     */
    private void decodeFrames(ByteBuffer socketBuffer) {
        List<FrameData> frames;
        try {
            frames = draft.translateFrame(socketBuffer);
            for (FrameData f : frames) {
                log.trace("matched frame: {}", f);
                draft.processFrame(this, f);
            }
        } catch (LimitExceededException e) {
            if (e.getLimit() == Integer.MAX_VALUE) {
                log.error("Closing due to invalid size of frame", e);
                webSocketListener.onWebSocketError(this, e);
            }
            close(e);
        } catch (InvalidDataException e) {
            log.error("Closing due to invalid data in frame", e);
            webSocketListener.onWebSocketError(this, e);
            close(e);
        } catch (VirtualMachineError | ThreadDeath | LinkageError e) {
            log.error("Got fatal error during frame processing");
            throw e;
        } catch (Error e) {
            log.error("Closing web socket due to an error during frame processing");
            Exception exception = new Exception(e);
            webSocketListener.onWebSocketError(this, exception);
            String errorMessage = "Got error " + e.getClass().getName();
            close(CloseFrame.UNEXPECTED_CONDITION, errorMessage);
        }
    }

    public synchronized void flushAndClose(int code, String message, boolean remote) {
        if (isFlushAndCloseState) {
            return;
        }
        closeCode = code;
        closeMessage = message;
        closedRemotely = remote;

        isFlushAndCloseState = true;

        webSocketListener.onWriteDemand(
                this); // ensures that all outgoing frames are flushed before closing the connection
        try {
            webSocketListener.onWebSocketClosing(this, code, message, remote);
        } catch (RuntimeException e) {
            log.error("Exception in onWebsocketClosing", e);
            webSocketListener.onWebSocketError(this, e);
        }
        if (draft != null) {
            draft.reset();
        }
        handshakeRequest = null;
    }

    public void eot() {
        if (readyState == ReadyState.NOT_YET_CONNECTED) {
            closeConnection(CloseFrame.NEVER_CONNECTED, true);
        } else if (isFlushAndClose()) {
            closeConnection(closeCode, closeMessage, closedRemotely);
        } else if (draft.getCloseHandshakeType() == CloseHandshakeType.NONE) {
            closeConnection(CloseFrame.NORMAL, true);
        } else if (draft.getCloseHandshakeType() == CloseHandshakeType.ONEWAY) {
            if (role == Role.SERVER) {
                closeConnection(CloseFrame.ABNORMAL_CLOSE, true);
            } else {
                closeConnection(CloseFrame.NORMAL, true);
            }
        } else {
            closeConnection(CloseFrame.ABNORMAL_CLOSE, true);
        }
    }

    public void close(InvalidDataException e) {
        close(e.getCloseCode(), e.getMessage(), false);
    }

    public synchronized void close(int code, String message, boolean remote) {
        if (readyState != ReadyState.CLOSING && readyState != ReadyState.CLOSED) {
            if (readyState == ReadyState.OPEN) {
                if (code == CloseFrame.ABNORMAL_CLOSE) {
                    assert (!remote);
                    readyState = ReadyState.CLOSING;
                    flushAndClose(code, message, false);
                    return;
                }
                if (draft.getCloseHandshakeType() != CloseHandshakeType.NONE) {
                    try {
                        if (!remote) {
                            try {
                                webSocketListener.onWebSocketCloseInitiated(this, code, message);
                            } catch (RuntimeException e) {
                                webSocketListener.onWebSocketError(this, e);
                            }
                        }
                        if (isOpen()) {
                            CloseFrame closeFrame = new CloseFrame();
                            closeFrame.setReason(message);
                            closeFrame.setCode(code);
                            closeFrame.isValid();
                            sendFrame(closeFrame);
                        }
                    } catch (InvalidDataException e) {
                        log.error("generated frame is invalid", e);
                        webSocketListener.onWebSocketError(this, e);
                        flushAndClose(CloseFrame.ABNORMAL_CLOSE, "generated frame is invalid", false);
                    }
                }
                flushAndClose(code, message, remote);
            } else if (code == CloseFrame.FLASH_POLICY) {
                assert (remote);
                flushAndClose(CloseFrame.FLASH_POLICY, message, true);
            } else if (code == CloseFrame.PROTOCOL_ERROR) { // this endpoint found a PROTOCOL_ERROR
                flushAndClose(code, message, remote);
            } else {
                flushAndClose(CloseFrame.NEVER_CONNECTED, message, false);
            }
            readyState = ReadyState.CLOSING;
            tmpHandshakeBytes = null;
            return;
        }
    }

    /**
     * This will close the connection immediately without a proper close handshake. The code and the
     * message therefore won't be transferred over the wire also they will be forwarded to
     * onClose/onWebsocketClose.
     *
     * @param code    the closing code
     * @param message the closing message
     * @param remote  Indicates who "generated" <code>code</code>.<br>
     *                <code>true</code> means that this endpoint received the <code>code</code> from
     *                the other endpoint.<br> false means this endpoint decided to send the given
     *                code,<br>
     *                <code>remote</code> may also be true if this endpoint started the closing
     *                handshake since the other endpoint may not simply echo the <code>code</code> but
     *                close the connection the same time this endpoint does do but with an other
     *                <code>code</code>. <br>
     **/
    public synchronized void closeConnection(int code, String message, boolean remote) {
        if (readyState == ReadyState.CLOSED) {
            return;
        }
        //Methods like eot() call this method without calling onClose(). Due to that reason we have to adjust the ReadyState manually
        if (readyState == ReadyState.OPEN) {
            if (code == CloseFrame.ABNORMAL_CLOSE) {
                readyState = ReadyState.CLOSING;
            }
        }
        if (key != null) {
            // key.attach( null ); //see issue #114
            key.cancel();
        }
        if (channel != null) {
            try {
                channel.close();
            } catch (IOException e) {
                if (e.getMessage() != null && e.getMessage().equals("Broken pipe")) {
                    log.trace("Caught IOException: Broken pipe during closeConnection()", e);
                } else {
                    log.error("Exception during channel.close()", e);
                    webSocketListener.onWebSocketError(this, e);
                }
            }
        }
        try {
            this.webSocketListener.onWebSocketClose(this, code, message, remote);
        } catch (RuntimeException e) {

            webSocketListener.onWebSocketError(this, e);
        }
        if (draft != null) {
            draft.reset();
        }
        handshakeRequest = null;
        readyState = ReadyState.CLOSED;
    }

    protected void closeConnection(int code, boolean remote) {
        closeConnection(code, "", remote);
    }

    public void closeConnection() {
        if (closedRemotely == null) {
            throw new IllegalStateException("this method must be used in conjunction with flushAndClose");
        }
        closeConnection(closeCode, closeMessage, closedRemotely);
    }

    /**
     * Close the connection if the received handshake was not correct
     *
     * @param exception the InvalidDataException causing this problem
     */
    private void closeConnectionDueToWrongHandshake(InvalidDataException exception) {
        write(generateHttpResponseDueToError(404));
        flushAndClose(exception.getCloseCode(), exception.getMessage(), false);
    }

    /**
     * Close the connection if there was a server error by a RuntimeException
     *
     * @param exception the RuntimeException causing this problem
     */
    private void closeConnectionDueToInternalServerError(RuntimeException exception) {
        write(generateHttpResponseDueToError(500));
        flushAndClose(CloseFrame.NEVER_CONNECTED, exception.getMessage(), false);
    }

    /**
     * Write a list of bytebuffer (frames in binary form) into the outgoing queue
     *
     * @param bufs the list of bytebuffer
     */
    private void write(List<ByteBuffer> bufs) {
        synchronized (lockWriteObject) {
            for (ByteBuffer b : bufs) {
                write(b);
            }
        }
    }

    private void write(ByteBuffer buf) {
        log.trace("write({}): {}", buf.remaining(),
                buf.remaining() > 1000 ? "too big to display" : new String(buf.array()));

        outQueue.add(buf);
        webSocketListener.onWriteDemand(this);
    }

    /**
     * Generate a simple response for the corresponding endpoint to indicate some error
     *
     * @param errorCode the http error code
     * @return the complete response as ByteBuffer
     */
    private ByteBuffer generateHttpResponseDueToError(int errorCode) {
        String errorCodeDescription;
        switch (errorCode) {
            case 404:
                errorCodeDescription = "404 WebSocket Upgrade Failure";
                break;
            case 500:
            default:
                errorCodeDescription = "500 Internal Server Error";
        }
        return ByteBuffer.wrap(CharsetFunctions.asciiBytes("HTTP/1.1 " + errorCodeDescription
                + "\r\nContent-Type: text/html\r\nServer: TooTallNate Module-WebSocket\r\nContent-Length: "
                + (48 + errorCodeDescription.length()) + "\r\n\r\n<html><head></head><body><h1>"
                + errorCodeDescription + "</h1></body></html>"));
    }

    private void open(Handshake handshake) {
        log.trace("open using draft: {}", draft);
        readyState = ReadyState.OPEN;
        updateLastPong();
        try {
            webSocketListener.onWebSocketOpen(this, handshake);
        } catch (RuntimeException e) {
            webSocketListener.onWebSocketError(this, e);
        }
    }

    private void send(Collection<FrameData> frames) {
        if (!isOpen()) {
            throw new WebsocketNotConnectedException();
        }
        if (frames == null) {
            throw new IllegalArgumentException();
        }
        ArrayList<ByteBuffer> outgoingFrames = new ArrayList<>();
        for (FrameData frameData : frames) {
            log.trace("send frame: {}", frameData);
            outgoingFrames.add(draft.createBinaryFrame(frameData));
        }
        write(outgoingFrames);
    }

    public void startHandshake(ClientHandshakeBuilder clientHandshake)
            throws InvalidHandshakeException {
        // Store the Handshake Request we are about to send
        this.handshakeRequest = draft.postProcessHandshakeRequestAsClient(clientHandshake);

        resourceDescriptor = clientHandshake.getResourceDescriptor();
        assert (resourceDescriptor != null);

        // Notify Listener
        try {
            webSocketListener.onWebSocketHandshakeSentAsClient(this, this.handshakeRequest);
        } catch (InvalidDataException e) {
            // Stop if the client code throws an exception
            throw new InvalidHandshakeException("Handshake data rejected by client.");
        } catch (RuntimeException e) {
            log.error("Exception in startHandshake", e);
            webSocketListener.onWebSocketError(this, e);
            throw new InvalidHandshakeException("rejected because of " + e);
        }

        // Send
        write(draft.createHandshake(this.handshakeRequest));
    }

    long getLastPong() {
        return lastPong;
    }

    /**
     * Update the timestamp when the last pong was received
     */
    public void updateLastPong() {
        this.lastPong = System.nanoTime();
    }

    public WebSocketListener getWebSocketListener() {
        return webSocketListener;
    }

    /**
     * @param key the selection key of this implementation
     */
    public void setSelectionKey(SelectionKey key) {
        this.key = key;
    }

    /**
     * @return the selection key of this implementation
     */
    public SelectionKey getSelectionKey() {
        return key;
    }

    public ByteChannel getChannel() {
        return channel;
    }

    public void setChannel(ByteChannel channel) {
        this.channel = channel;
    }

    public WebSocketServerWorker getWorkerThread() {
        return serverWorkerThread;
    }

    public void setWorkerThread(WebSocketServerWorker workerThread) {
        this.serverWorkerThread = workerThread;
    }

    @Override
    public void close(int code, String message) {
        close(code, message, false);
    }

    @Override
    public void close(int code) {
        close(code, "", false);
    }

    @Override
    public void close() {
        close(CloseFrame.NORMAL);
    }

    @Override
    public void closeConnection(int code, String message) {
        closeConnection(code, message, false);
    }

    /**
     * Send Text data to the other end.
     *
     * @throws WebsocketNotConnectedException websocket is not yet connected
     */
    @Override
    public void send(String text) {
        if (text == null) {
            throw new IllegalArgumentException("Cannot send 'null' data to a WebSocketImpl.");
        }
        send(draft.createFrames(text, role == Role.CLIENT));
    }

    /**
     * Send Binary data (plain bytes) to the other end.
     *
     * @throws IllegalArgumentException       the data is null
     * @throws WebsocketNotConnectedException websocket is not yet connected
     */
    @Override
    public void send(ByteBuffer bytes) {
        if (bytes == null) {
            throw new IllegalArgumentException("Cannot send 'null' data to a WebSocketImpl.");
        }
        send(draft.createFrames(bytes, role == Role.CLIENT));
    }

    @Override
    public void send(byte[] bytes) {
        send(ByteBuffer.wrap(bytes));
    }

    @Override
    public void sendFrame(FrameData frameData) {
        send(Collections.singletonList(frameData));
    }

    @Override
    public void sendFrame(Collection<FrameData> frames) {
        send(frames);
    }

    @Override
    public void sendPing() {
        // Gets a PingAbstractFrame from WebSocketListener(wsl) and sends it.
        PingFrame pingFrame = webSocketListener.onPreparePing(this);
        if (pingFrame == null) {
            throw new NullPointerException(
                    "onPreparePing(WebSocket) returned null. PingAbstractFrame to sent can't be null.");
        }
        sendFrame(pingFrame);
    }

    @Override
    public void sendFragmentedFrame(Opcode op, ByteBuffer buffer, boolean fin) {
        send(draft.continuousFrame(op, buffer, fin));
    }

    @Override
    public boolean hasBufferedData() {
        return !this.outQueue.isEmpty();
    }

    @Override
    public InetSocketAddress getRemoteSocketAddress() {
        return webSocketListener.getRemoteSocketAddress(this);
    }

    @Override
    public InetSocketAddress getLocalSocketAddress() {
        return webSocketListener.getLocalSocketAddress(this);
    }

    @Override
    public boolean isOpen() {
        return readyState == ReadyState.OPEN;
    }

    @Override
    public boolean isClosing() {
        return readyState == ReadyState.CLOSING;
    }

    @Override
    public boolean isFlushAndClose() {
        return isFlushAndCloseState;
    }

    @Override
    public boolean isClosed() {
        return readyState == ReadyState.CLOSED;
    }

    @Override
    public Draft getDraft() {
        return draft;
    }

    @Override
    public ReadyState getReadyState() {
        return readyState;
    }

    @Override
    public String getResourceDescriptor() {
        return resourceDescriptor;
    }

    @Override
    public <T> void setAttachment(T attachment) {
        this.attachment = attachment;
    }

    @Override
    public <T> T getAttachment() {
        return (T) attachment;
    }

    @Override
    public boolean hasSSLSupport() {
        return channel instanceof SSLChannel;
    }

    @Override
    public SSLSession getSSLSession() throws IllegalArgumentException {
        if (!hasSSLSupport()) {
            throw new IllegalArgumentException(
                    "This websocket uses ws instead of wss. No SSLSession available.");
        }
        return ((SSLChannel) channel).getSSLEngine().getSession();
    }

    @Override
    public Protocol getProtocol() {
        if (draft == null) {
            return null;
        }
        if (!(draft instanceof Draft_6455)) {
            throw new IllegalArgumentException("This draft does not support Sec-WebSocket-Protocol");
        }
        return ((Draft_6455) draft).getProtocol();
    }
}
