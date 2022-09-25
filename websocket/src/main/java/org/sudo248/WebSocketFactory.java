package org.sudo248;

import org.sudo248.drafts.Draft;

import java.util.List;

/**
 * WebSocketFactory interface
 */
public interface WebSocketFactory {
    /**
     * Create a new Websocket with the provided listener, drafts and socket
     *
     * @param webSocketListener The Listener for the WebsocketImpl
     * @param draft The draft which should be used
     * @return A WebsocketImpl
     */
    WebSocket createWebSocket(AbstractWebSocketListenerImpl webSocketListener, Draft draft);

    /**
     * Create a new Websocket with the provided listener, drafts and socket
     *
     * @param webSocketListener      The Listener for the WebsocketImpl
     * @param drafts The drafts which should be used
     * @return A WebsocketImpl
     */
    WebSocket createWebSocket(AbstractWebSocketListenerImpl webSocketListener, List<Draft> drafts);
}
