package org.sudo248;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sudo248.frames.CloseFrame;
import org.sudo248.utils.NamedThreadFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Base class for additional implementations for the server as well as the client
 */
public abstract class AbstractWebSocket extends AbstractWebSocketListenerImpl {

    /**
     * Logger instance
     */
    private final Logger log = LoggerFactory.getLogger(AbstractWebSocket.class);

    /**
     * Attribute which allows you to deactivate the Nagle's algorithm
     * <a href="https://en.wikipedia.org/wiki/Nagle%27s_algorithm">Nagle's algorithm</a>
     */
    private boolean isTcpNoDelay;

    /**
     * Attribute which allows you to enable/disable the SO_REUSEADDR socket option.
     *
     */
    private boolean isReuseAddress;

    /**
     * Attribute for a service that triggers lost connection checking
     */
    private ScheduledExecutorService connectionLostCheckerService;

    /**
     * Attribute for a task that checks for lost connections
     */
    private ScheduledFuture<?> connectionLostCheckerFuture;

    /**
     * Attribute for the lost connection check interval in nanoseconds
     * timeout = 60s
     */
    private long connectionLostTimeout = TimeUnit.SECONDS.toNanos(60);

    /**
     * Attribute to keep track if the WebSocket Server/Client is running/connected
     */
    private boolean isWebSocketRunning = false;

    /**
     * Attribute to sync on
     */
    private final Object lockConnectionLost = new Object();

    /**
     * Get the interval checking for lost connections Default is 60 seconds
     *
     * @return the interval in seconds
     */
    public int getConnectionLostTimeout() {
        synchronized (lockConnectionLost) {
            return (int) TimeUnit.NANOSECONDS.toSeconds(connectionLostTimeout);
        }
    }

    /**
     * Setter for the interval checking for lost connections A value lower or equal 0 results in the
     * check to be deactivated
     *
     * @param timeout the interval in seconds
     */
    public void setConnectionLostTimeout(int timeout) {
        synchronized (lockConnectionLost) {
            this.connectionLostTimeout = TimeUnit.SECONDS.toNanos(timeout);

            if (this.connectionLostTimeout <= 0) {
                log.trace("Connection lost timer stopped");
                cancelConnectionLostTimer();
                return;
            }

            if (isWebSocketRunning) {
                log.trace("Connection lost timer restarted");
                try {
                    ArrayList<WebSocket> connections = new ArrayList<>(getConnections());
                    WebSocketImpl webSocket;
                    for (WebSocket connection : connections) {
                        if (connection instanceof WebSocketImpl) {
                            webSocket = (WebSocketImpl) connection;
                            webSocket.updateLastPong();
                        }
                    }
                } catch (Exception e) {
                    log.error("Exception during connection lost restart", e);
                }
                restartConnectionLostTimer();
            }
        }
    }

    /**
     * This methods allows the reset of the connection lost timer in case of a changed parameter
     *
     * @since 1.3.4
     */
    private void restartConnectionLostTimer() {
        cancelConnectionLostTimer();
        connectionLostCheckerService = Executors
                .newSingleThreadScheduledExecutor(new NamedThreadFactory("connectionLostChecker"));
        Runnable connectionLostChecker = new Runnable() {

            /**
             * Keep the connections in a separate list to not cause deadlocks
             */
            private ArrayList<WebSocket> connections = new ArrayList<>();

            @Override
            public void run() {
                connections.clear();
                try {
                    connections.addAll(getConnections());
                    long minimumPongTime;
                    synchronized (lockConnectionLost) {
                        minimumPongTime = (long) (System.nanoTime() - (connectionLostTimeout * 1.5));
                    }
                    for (WebSocket conn : connections) {
                        executeConnectionLostDetection(conn, minimumPongTime);
                    }
                } catch (Exception e) {
                    //Ignore this exception
                }
                connections.clear();
            }
        };

        connectionLostCheckerFuture = connectionLostCheckerService
                .scheduleAtFixedRate(connectionLostChecker, connectionLostTimeout, connectionLostTimeout,
                        TimeUnit.NANOSECONDS);
    }

    /**
     * Send a ping to the endpoint or close the connection since the other endpoint did not respond
     * with a ping
     *
     * @param webSocket       the websocket instance
     * @param minimumPongTime the lowest/oldest allowable last pong time (in nanoTime) before we
     *                        consider the connection to be lost
     */
    private void executeConnectionLostDetection(WebSocket webSocket, long minimumPongTime) {
        if (!(webSocket instanceof WebSocketImpl)) {
            return;
        }
        WebSocketImpl webSocketImpl = (WebSocketImpl) webSocket;
        if (webSocketImpl.getLastPong() < minimumPongTime) {
            log.trace("Closing connection due to no pong received: {}", webSocketImpl);
            webSocketImpl.closeConnection(CloseFrame.ABNORMAL_CLOSE,
                    "The connection was closed because the other endpoint did not respond with a pong in time. For more information check: https://github.com/TooTallNate/Java-WebSocket/wiki/Lost-connection-detection");
        } else {
            if (webSocketImpl.isOpen()) {
                webSocketImpl.sendPing();
            } else {
                log.trace("Trying to ping a non open connection: {}", webSocketImpl);
            }
        }
    }

    /**
     * Stop the connection lost timer
     *
     */
    protected void stopConnectionLostTimer() {
        synchronized (lockConnectionLost) {
            if (connectionLostCheckerService != null || connectionLostCheckerFuture != null) {
                this.isWebSocketRunning = false;
                log.trace("Connection lost timer stopped");
                cancelConnectionLostTimer();
            }
        }
    }

    /**
     * Start the connection lost timer
     *
     */
    protected void startConnectionLostTimer() {
        synchronized (lockConnectionLost) {
            if (this.connectionLostTimeout <= 0) {
                log.trace("Connection lost timer deactivated");
                return;
            }
            log.trace("Connection lost timer started");
            this.isWebSocketRunning = true;
            restartConnectionLostTimer();
        }
    }

    /**
     * Getter to get all the currently available connections
     *
     * @return the currently available connections
     */
    protected abstract Collection<WebSocket> getConnections();

    /**
     * Cancel any running timer for the connection lost detection
     *
     */
    private void cancelConnectionLostTimer() {
        if (connectionLostCheckerService != null) {
            connectionLostCheckerService.shutdownNow();
            connectionLostCheckerService = null;
        }

        if (connectionLostCheckerFuture != null) {
            connectionLostCheckerFuture.cancel(false);
            connectionLostCheckerFuture = null;
        }
    }

    /**
     * Tests if TCP_NODELAY is enabled.
     *
     * @return a boolean indicating whether or not TCP_NODELAY is enabled for new connections.
     */
    public boolean isTcpNoDelay() {
        return isTcpNoDelay;
    }

    /**
     * Setter for tcpNoDelay
     * <p>
     * Enable/disable TCP_NODELAY (disable/enable Nagle's algorithm) for new connections
     *
     * @param isTcpNoDelay true to enable TCP_NODELAY, false to disable.
     */
    public void setIsTcpNoDelay(boolean isTcpNoDelay) {
        this.isTcpNoDelay = isTcpNoDelay;
    }

    /**
     * Tests Tests if SO_REUSEADDR is enabled.
     *
     * @return a boolean indicating whether or not SO_REUSEADDR is enabled.
     */
    public boolean isReuseAddress() {
        return isReuseAddress;
    }

    /**
     * Setter for soReuseAddr
     * <p>
     * Enable/disable SO_REUSEADDR for the socket
     *
     * @param isReuseAddress whether to enable or disable SO_REUSEADDR
     */
    public void setIsReuseAddress(boolean isReuseAddress) {
        this.isReuseAddress = isReuseAddress;
    }
}
